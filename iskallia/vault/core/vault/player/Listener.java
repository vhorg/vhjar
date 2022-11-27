package iskallia.vault.core.vault.player;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataMap;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.data.sync.SyncMode;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.util.iterator.MappingIterator;
import iskallia.vault.core.vault.EntityState;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultMessage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public abstract class Listener extends DataObject<Listener> implements ISupplierKey<Listener> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<UUID> ID = FieldKey.of("id", UUID.class).with(Version.v1_0, Adapter.ofUUID(), DISK.all().or(CLIENT.all())).register(FIELDS);
   public static final FieldKey<EntityState> JOIN_STATE = FieldKey.of("join_state", EntityState.class)
      .with(Version.v1_0, Adapter.ofCompound(EntityState::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Objective.IdList> OBJECTIVES = FieldKey.of("objectives", Objective.IdList.class)
      .with(Version.v1_0, Adapter.ofCompound(Objective.IdList::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   public Listener() {
      this.set(OBJECTIVES, new Objective.IdList());
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public UUID getId() {
      return this.get(ID);
   }

   public void initServer(VirtualWorld world, Vault vault) {
   }

   public void tickServer(VirtualWorld world, Vault vault) {
      this.getPlayer()
         .ifPresent(
            player -> ModNetwork.CHANNEL
               .sendTo(new VaultMessage.Sync(player, vault, SyncMode.FULL), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
         );
   }

   public void releaseServer() {
      CommonEvents.release(this);
   }

   public void onJoin(VirtualWorld world, Vault vault) {
      vault.ifPresent(Vault.MODIFIERS, modifiers -> modifiers.onListenerAdd(world, vault, this));
   }

   public void onLeave(VirtualWorld world, Vault vault) {
      vault.ifPresent(Vault.MODIFIERS, modifiers -> modifiers.onListenerRemove(world, vault, this));
      this.getPlayer()
         .ifPresent(player -> ModNetwork.CHANNEL.sendTo(new VaultMessage.Unload(vault), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
   }

   public Optional<ServerPlayer> getPlayer() {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      return Optional.ofNullable(server.getPlayerList().getPlayer(this.get(ID)));
   }

   public boolean isOnline() {
      return this.getPlayer().isPresent();
   }

   public Iterator<Objective> getObjectives(Vault vault) {
      return new MappingIterator<>(
         this.get(OBJECTIVES).iterator(), index -> !vault.has(Vault.OBJECTIVES) ? null : vault.get(Vault.OBJECTIVES).get(Objectives.LIST).get(index)
      );
   }

   public void addObjective(Vault vault, Objective objective) {
      this.addObjective(vault, objective, this.get(OBJECTIVES).size());
   }

   public void addObjective(Vault vault, Objective objective, int priority) {
      this.get(OBJECTIVES).add(objective.get(Objective.ID), Integer.valueOf(priority));
   }

   public int getPriority(Objective objective) {
      return this.get(OBJECTIVES).indexOf(objective.get(Objective.ID));
   }

   public boolean isActive(Vault vault, Objective objective) {
      for (int index : this.get(OBJECTIVES)) {
         boolean active = vault.get(Vault.OBJECTIVES).get(index).map(other -> other.isActive(objective)).orElse(false);
         if (active) {
            return true;
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public void renderObjectives(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      for (int index : this.get(OBJECTIVES)) {
         boolean rendered = vault.get(Vault.OBJECTIVES).get(index).map(other -> other.render(matrixStack, window, partialTicks, player)).orElse(false);
         if (rendered) {
            return;
         }
      }
   }

   public static class Map extends DataMap<Listener.Map, UUID, Listener> {
      public Map() {
         super(new HashMap<>(), Adapter.ofUUID(), Adapter.ofRegistryValue(() -> VaultRegistry.LISTENER, ISupplierKey::getKey, Supplier::get));
      }
   }
}
