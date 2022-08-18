package iskallia.vault.world.vault.player;

import iskallia.vault.Vault;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.PlayerVaultStatsData;
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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

public abstract class VaultPlayer implements INBTSerializable<CompoundNBT> {
   public static final Map<ResourceLocation, Supplier<VaultPlayer>> REGISTRY = new HashMap<>();
   private ResourceLocation id;
   protected UUID playerId;
   protected boolean exited;
   protected VaultTimer timer = this.createTimer();
   protected VListNBT<TimeExtension, CompoundNBT> addedExtensions = VListNBT.of(TimeExtension::fromNBT);
   protected VListNBT<TimeExtension, CompoundNBT> appliedExtensions = VListNBT.of(TimeExtension::fromNBT);
   protected VaultModifiers modifiers = new VaultModifiers();
   protected RaidProperties properties = new RaidProperties();
   protected VListNBT<VaultBehaviour, CompoundNBT> behaviours = VListNBT.of(VaultBehaviour::fromNBT);
   protected VListNBT<VaultObjective, CompoundNBT> objectives = VListNBT.of(VaultObjective::fromNBT);

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

   public void tick(VaultRaid vault, ServerWorld world) {
      if (!this.hasExited()) {
         this.getModifiers().tick(vault, world, PlayerFilter.of(this));
         MinecraftServer srv = world.func_73046_m();
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

   public abstract void tickTimer(VaultRaid var1, ServerWorld var2, VaultTimer var3);

   public abstract void tickObjectiveUpdates(VaultRaid var1, ServerWorld var2);

   public Optional<ServerPlayerEntity> getServerPlayer(MinecraftServer srv) {
      return Optional.ofNullable(srv.func_184103_al().func_177451_a(this.getPlayerId()));
   }

   public boolean isOnline(MinecraftServer srv) {
      return this.getServerPlayer(srv).isPresent();
   }

   public void runIfPresent(MinecraftServer server, Consumer<ServerPlayerEntity> action) {
      this.getServerPlayer(server).ifPresent(action::accept);
   }

   public <T> T mapIfPresent(MinecraftServer server, Function<ServerPlayerEntity, T> action, T _default) {
      return this.getServerPlayer(server).map(action).orElse(_default);
   }

   public void sendIfPresent(MinecraftServer server, Object message) {
      this.runIfPresent(server, playerEntity -> ModNetwork.CHANNEL.sendTo(message, playerEntity.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT));
   }

   public void grantVaultExp(MinecraftServer server, float multiplier) {
      PlayerVaultStatsData data = PlayerVaultStatsData.get(server);
      PlayerVaultStats stats = data.getVaultStats(this.getPlayerId());
      float expGrantedPercent = MathHelper.func_76131_a((float)this.timer.getRunTime() / this.timer.getStartTime(), 0.0F, 1.0F);
      expGrantedPercent *= multiplier;
      expGrantedPercent *= this.mapIfPresent(
            server, player -> MathHelper.func_76125_a(player.func_71121_q().func_82736_K().func_223592_c(ModGameRules.EXP_MULTIPLIER), 0, 25), 1
         )
         .intValue();
      int vaultLevel = stats.getVaultLevel();
      expGrantedPercent *= MathHelper.func_76131_a(1.0F - vaultLevel / 200.0F, 0.0F, 1.0F);
      float remainingPercent = 1.0F - (float)stats.getExp() / stats.getTnl();
      if (expGrantedPercent > remainingPercent) {
         expGrantedPercent -= remainingPercent;
         int remaining = stats.getTnl() - stats.getExp();
         stats.addVaultExp(server, remaining);
      }

      int expGranted = MathHelper.func_76141_d(stats.getTnl() * expGrantedPercent);
      stats.addVaultExp(server, expGranted);
      data.func_76185_a();
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Id", this.getId().toString());
      nbt.func_74778_a("PlayerId", this.getPlayerId().toString());
      nbt.func_74757_a("Exited", this.hasExited());
      nbt.func_218657_a("Timer", this.timer.serializeNBT());
      nbt.func_218657_a("AddedExtensions", this.addedExtensions.serializeNBT());
      nbt.func_218657_a("AppliedExtensions", this.appliedExtensions.serializeNBT());
      nbt.func_218657_a("Modifiers", this.modifiers.serializeNBT());
      nbt.func_218657_a("Properties", this.properties.serializeNBT());
      nbt.func_218657_a("Behaviours", this.behaviours.serializeNBT());
      nbt.func_218657_a("Objectives", this.objectives.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.id = new ResourceLocation(nbt.func_74779_i("Id"));
      this.playerId = UUID.fromString(nbt.func_74779_i("PlayerId"));
      this.exited = nbt.func_74767_n("Exited");
      this.timer = this.createTimer();
      this.timer.deserializeNBT(nbt.func_74775_l("Timer"));
      this.addedExtensions.deserializeNBT(nbt.func_150295_c("AddedExtensions", 10));
      this.appliedExtensions.deserializeNBT(nbt.func_150295_c("AppliedExtensions", 10));
      this.modifiers.deserializeNBT(nbt.func_74775_l("Modifiers"));
      this.properties.deserializeNBT(nbt.func_74775_l("Properties"));
      this.behaviours.deserializeNBT(nbt.func_150295_c("Behaviours", 10));
      this.objectives.deserializeNBT(nbt.func_150295_c("Objectives", 10));
   }

   public static VaultPlayer fromNBT(CompoundNBT nbt) {
      ResourceLocation id = new ResourceLocation(nbt.func_74779_i("Id"));
      VaultPlayer player = REGISTRY.getOrDefault(id, () -> null).get();
      if (player == null) {
         Vault.LOGGER.error("Player <" + id + "> is not defined.");
         return null;
      } else {
         try {
            player.deserializeNBT(nbt);
            return player;
         } catch (Exception var4) {
            Vault.LOGGER.error("Player <" + id + "> with uuid <" + nbt.func_74779_i("PlayerId") + "> could not be deserialized.");
            throw var4;
         }
      }
   }
}
