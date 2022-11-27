package iskallia.vault.world.vault.player;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.raid.RaidProperties;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.behaviour.VaultBehaviour;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.modifier.VaultModifiers;
import iskallia.vault.world.vault.time.VaultTimer;
import iskallia.vault.world.vault.time.extension.TimeExtension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;

public abstract class VaultPlayer implements INBTSerializable<CompoundTag> {
   public static final Map<ResourceLocation, Supplier<VaultPlayer>> REGISTRY = new HashMap<>();
   private ResourceLocation id;
   protected UUID playerId;
   protected boolean exited;
   protected VaultTimer timer = this.createTimer();
   protected VListNBT<TimeExtension, CompoundTag> addedExtensions = VListNBT.of(TimeExtension::fromNBT);
   protected VListNBT<TimeExtension, CompoundTag> appliedExtensions = VListNBT.of(TimeExtension::fromNBT);
   protected VaultModifiers modifiers = new VaultModifiers();
   protected RaidProperties properties = new RaidProperties();
   protected VListNBT<VaultBehaviour, CompoundTag> behaviours = VListNBT.of(VaultBehaviour::fromNBT);
   protected VListNBT<VaultObjective, CompoundTag> objectives = VListNBT.of(VaultObjective::fromNBT);

   public VaultPlayer() {
   }

   public VaultPlayer(ResourceLocation id, UUID playerId) {
      this.id = id;
      this.playerId = playerId;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public boolean hasExited() {
      return this.exited;
   }

   public VaultTimer getTimer() {
      return this.timer;
   }

   public VaultModifiers getModifiers() {
      return this.modifiers;
   }

   public RaidProperties getProperties() {
      return this.properties;
   }

   public List<VaultBehaviour> getBehaviours() {
      return this.behaviours;
   }

   public List<VaultObjective> getObjectives() {
      return this.objectives.stream().filter(objective -> !objective.isCompleted()).collect(Collectors.toList());
   }

   public List<VaultObjective> getAllObjectives() {
      return this.objectives;
   }

   public <T extends VaultObjective> Optional<T> getActiveObjective(Class<T> objectiveClass) {
      return this.getAllObjectives()
         .stream()
         .filter(objective -> !objective.isCompleted())
         .filter(objective -> objectiveClass.isAssignableFrom(objective.getClass()))
         .findFirst()
         .map(vaultObjective -> (T)vaultObjective);
   }

   public void exit() {
      this.exited = true;
   }

   public VaultTimer createTimer() {
      return new VaultTimer()
         .onExtensionAdded((timer, extension) -> this.addedExtensions.add(extension))
         .onExtensionApplied((timer, extension) -> this.appliedExtensions.add(extension));
   }

   public void tick(VaultRaid vault, ServerLevel world) {
      if (!this.hasExited()) {
         this.getModifiers().tick(vault, world, PlayerFilter.of(this));
         MinecraftServer srv = world.getServer();
         if (vault.getActiveObjectives().stream().noneMatch(objective -> objective.shouldPauseTimer(srv, vault))) {
            this.tickTimer(vault, world, this.getTimer());
         }

         this.tickObjectiveUpdates(vault, world);
         this.getBehaviours().forEach(completion -> {
            if (!this.hasExited()) {
               completion.tick(vault, this, world);
            }
         });
         if (!this.hasExited()) {
            this.getAllObjectives()
               .stream()
               .filter(objective -> objective.isCompleted() && objective.getCompletionTime() < 0)
               .peek(objective -> objective.setCompletionTime(this.getTimer().getRunTime()))
               .forEach(objective -> objective.complete(vault, this, world));
            this.getObjectives().forEach(objective -> objective.tick(vault, PlayerFilter.of(this), world));
         }
      }
   }

   public abstract void tickTimer(VaultRaid var1, ServerLevel var2, VaultTimer var3);

   public abstract void tickObjectiveUpdates(VaultRaid var1, ServerLevel var2);

   public Optional<ServerPlayer> getServerPlayer(MinecraftServer srv) {
      return Optional.ofNullable(srv.getPlayerList().getPlayer(this.getPlayerId()));
   }

   public boolean isOnline(MinecraftServer srv) {
      return this.getServerPlayer(srv).isPresent();
   }

   public void runIfPresent(MinecraftServer server, Consumer<ServerPlayer> action) {
      this.getServerPlayer(server).ifPresent(action::accept);
   }

   public void sendIfPresent(MinecraftServer server, Object message) {
      this.runIfPresent(server, playerEntity -> {
         if (playerEntity.level.dimension() != VaultMod.ARENA_KEY) {
            ModNetwork.CHANNEL.sendTo(message, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         }
      });
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Id", this.getId().toString());
      nbt.putString("PlayerId", this.getPlayerId().toString());
      nbt.putBoolean("Exited", this.hasExited());
      nbt.put("Timer", this.timer.serializeNBT());
      nbt.put("AddedExtensions", this.addedExtensions.serializeNBT());
      nbt.put("AppliedExtensions", this.appliedExtensions.serializeNBT());
      nbt.put("Modifiers", this.modifiers.serializeNBT());
      nbt.put("Properties", this.properties.serializeNBT());
      nbt.put("Behaviours", this.behaviours.serializeNBT());
      nbt.put("Objectives", this.objectives.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.id = new ResourceLocation(nbt.getString("Id"));
      this.playerId = UUID.fromString(nbt.getString("PlayerId"));
      this.exited = nbt.getBoolean("Exited");
      this.timer = this.createTimer();
      this.timer.deserializeNBT(nbt.getCompound("Timer"));
      this.addedExtensions.deserializeNBT(nbt.getList("AddedExtensions", 10));
      this.appliedExtensions.deserializeNBT(nbt.getList("AppliedExtensions", 10));
      this.modifiers.deserializeNBT(nbt.getCompound("Modifiers"));
      this.properties.deserializeNBT(nbt.getCompound("Properties"));
      this.behaviours.deserializeNBT(nbt.getList("Behaviours", 10));
      this.objectives.deserializeNBT(nbt.getList("Objectives", 10));
   }

   public static VaultPlayer fromNBT(CompoundTag nbt) {
      ResourceLocation id = new ResourceLocation(nbt.getString("Id"));
      VaultPlayer player = REGISTRY.getOrDefault(id, () -> null).get();
      if (player == null) {
         VaultMod.LOGGER.error("Player <" + id + "> is not defined.");
         return null;
      } else {
         try {
            player.deserializeNBT(nbt);
            return player;
         } catch (Exception var4) {
            VaultMod.LOGGER.error("Player <" + id + "> with uuid <" + nbt.getString("PlayerId") + "> could not be deserialized.");
            throw var4;
         }
      }
   }
}
