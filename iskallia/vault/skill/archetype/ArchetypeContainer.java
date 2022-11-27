package iskallia.vault.skill.archetype;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundArchetypeMessage;
import iskallia.vault.util.NetcodeUtils;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;

public class ArchetypeContainer implements INBTSerializable<CompoundTag> {
   private final UUID playerUuid;
   private AbstractArchetype<?> currentArchetype;
   private static final String TAG_CURRENT_ARCHETYPE = "currentArchetype";

   public ArchetypeContainer(UUID playerUuid) {
      this.playerUuid = playerUuid;
      this.currentArchetype = ArchetypeRegistry.getDefaultArchetype();
   }

   public AbstractArchetype<?> getCurrentArchetype() {
      return this.currentArchetype;
   }

   public <T extends AbstractArchetype<?>> void ifCurrentArchetype(Class<T> type, Consumer<T> action) {
      if (this.isCurrentArchetype(type)) {
         action.accept((T)this.currentArchetype);
      }
   }

   public boolean isCurrentArchetype(Class<? extends AbstractArchetype<?>> type) {
      return type.isInstance(this.currentArchetype);
   }

   public void setCurrentArchetype(@Nullable MinecraftServer server, ResourceLocation id) {
      try {
         NetcodeUtils.runIfPresent(server, this.playerUuid, player -> this.currentArchetype.onRemoved(server, player));
      } catch (Exception var5) {
         VaultMod.LOGGER.error("Error executing onRemoved for %s".formatted(id), var5);
      }

      this.currentArchetype = ArchetypeRegistry.getArchetype(id);

      try {
         NetcodeUtils.runIfPresent(server, this.playerUuid, player -> {
            this.currentArchetype.onAdded(server, player);
            this.syncToClient(server);
         });
      } catch (Exception var4) {
         VaultMod.LOGGER.error("Error executing onAdded for %s".formatted(id), var4);
      }
   }

   public void tick(MinecraftServer server, ServerPlayer serverPlayer) {
      this.currentArchetype.onTick(server, serverPlayer);
   }

   public void syncToClient(MinecraftServer server) {
      NetcodeUtils.runIfPresent(
         server,
         this.playerUuid,
         player -> ModNetwork.CHANNEL
            .sendTo(new ClientboundArchetypeMessage(this.currentArchetype.getRegistryName()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
      );
   }

   public CompoundTag serializeNBT() {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.putString("currentArchetype", this.currentArchetype.getRegistryName().toString());
      return compoundTag;
   }

   public void deserializeNBT(CompoundTag compoundTag) {
      String resourceLocationString = compoundTag.getString("currentArchetype");
      ResourceLocation resourceLocation = new ResourceLocation(resourceLocationString);
      this.currentArchetype = ArchetypeRegistry.getArchetype(resourceLocation);
   }
}
