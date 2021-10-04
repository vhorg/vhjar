package iskallia.vault.world.vault.player;

import iskallia.vault.Vault;
import iskallia.vault.world.raid.RaidProperties;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.behaviour.VaultBehaviour;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;

public class VaultSpectator extends VaultPlayer {
   public static final ResourceLocation ID = Vault.id("spectator");
   private VaultRunner delegate = new VaultRunner(null);
   public GameType oldGameType = GameType.NOT_SET;
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
      this.delegate.exited = true;
   }

   @Override
   public void tick(VaultRaid vault, ServerWorld world) {
      if (!this.hasExited()) {
         if (!this.isInitialized()) {
            this.runIfPresent(world.func_73046_m(), playerEntity -> {
               this.oldGameType = playerEntity.field_71134_c.func_73081_b();
               playerEntity.func_71033_a(GameType.SPECTATOR);
               this.setInitialized();
            });
         }

         super.tick(vault, world);
      }
   }

   @Override
   public void tickTimer(VaultRaid vault, ServerWorld world, VaultTimer timer) {
   }

   @Override
   public void tickObjectiveUpdates(VaultRaid vault, ServerWorld world) {
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      nbt.func_74768_a("OldGameType", this.oldGameType.ordinal());
      nbt.func_74757_a("Initialized", this.initialized);
      nbt.func_218657_a("Delegate", this.delegate.serializeNBT());
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      this.oldGameType = GameType.values()[nbt.func_74762_e("OldGameType")];
      this.initialized = nbt.func_74767_n("Initialized");
      this.delegate.deserializeNBT(nbt.func_74775_l("Delegate"));
      super.deserializeNBT(nbt);
   }
}
