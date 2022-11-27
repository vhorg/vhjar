package iskallia.vault.world.vault.player;

import iskallia.vault.VaultMod;
import iskallia.vault.world.raid.RaidProperties;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.behaviour.VaultBehaviour;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameType;

public class VaultSpectator extends VaultPlayer {
   public static final ResourceLocation ID = VaultMod.id("spectator");
   private VaultRunner delegate = new VaultRunner(null);
   public GameType oldGameType;
   private boolean initialized = false;

   public VaultSpectator() {
   }

   public VaultSpectator(VaultRunner delegate) {
      this(ID, delegate);
   }

   public VaultSpectator(ResourceLocation id, VaultRunner delegate) {
      super(id, delegate.getPlayerId());
      this.delegate = delegate;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   @Override
   public UUID getPlayerId() {
      return this.delegate.getPlayerId();
   }

   @Override
   public boolean hasExited() {
      return this.delegate.hasExited();
   }

   @Override
   public VaultTimer getTimer() {
      return this.delegate.getTimer();
   }

   @Override
   public RaidProperties getProperties() {
      return this.delegate.getProperties();
   }

   @Override
   public List<VaultBehaviour> getBehaviours() {
      return this.delegate.getBehaviours();
   }

   @Override
   public List<VaultObjective> getObjectives() {
      return this.delegate.getObjectives();
   }

   @Override
   public List<VaultObjective> getAllObjectives() {
      return this.delegate.getAllObjectives();
   }

   @Override
   public <T extends VaultObjective> Optional<T> getActiveObjective(Class<T> type) {
      return this.delegate.getActiveObjective(type);
   }

   public void setInitialized() {
      this.initialized = true;
   }

   @Override
   public void exit() {
      this.delegate.exit();
   }

   @Override
   public void tick(VaultRaid vault, ServerLevel world) {
      if (!this.hasExited()) {
         if (!this.isInitialized()) {
            this.runIfPresent(world.getServer(), playerEntity -> {
               this.oldGameType = playerEntity.gameMode.getGameModeForPlayer();
               playerEntity.setGameMode(GameType.SPECTATOR);
               this.setInitialized();
            });
         }

         super.tick(vault, world);
      }
   }

   @Override
   public void tickTimer(VaultRaid vault, ServerLevel world, VaultTimer timer) {
   }

   @Override
   public void tickObjectiveUpdates(VaultRaid vault, ServerLevel world) {
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putInt("OldGameType", this.oldGameType.ordinal());
      nbt.putBoolean("Initialized", this.initialized);
      nbt.put("Delegate", this.delegate.serializeNBT());
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      this.oldGameType = GameType.values()[nbt.getInt("OldGameType")];
      this.initialized = nbt.getBoolean("Initialized");
      this.delegate.deserializeNBT(nbt.getCompound("Delegate"));
      super.deserializeNBT(nbt);
   }
}
