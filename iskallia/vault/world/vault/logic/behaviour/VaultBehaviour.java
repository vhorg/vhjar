package iskallia.vault.world.vault.logic.behaviour;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.condition.IVaultCondition;
import iskallia.vault.world.vault.logic.condition.VaultCondition;
import iskallia.vault.world.vault.logic.task.IVaultTask;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultBehaviour implements IVaultCondition, IVaultTask, INBTSerializable<CompoundNBT> {
   private VaultCondition condition;
   private VaultTask task;

   protected VaultBehaviour() {
   }

   public VaultBehaviour(VaultCondition condition, VaultTask task) {
      this.condition = condition;
      this.task = task;
   }

   public VaultCondition getCondition() {
      return this.condition;
   }

   public VaultTask getTask() {
      return this.task;
   }

   @Override
   public boolean test(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      return this.condition.test(vault, player, world);
   }

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      this.task.execute(vault, player, world);
   }

   public void tick(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      if (this.test(vault, player, world)) {
         this.execute(vault, player, world);
      }
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_218657_a("Condition", this.condition.serializeNBT());
      nbt.func_218657_a("Task", this.task.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.condition = VaultCondition.fromNBT(nbt.func_74775_l("Condition"));
      this.task = VaultTask.fromNBT(nbt.func_74775_l("Task"));
   }

   public static VaultBehaviour fromNBT(CompoundNBT nbt) {
      VaultBehaviour behaviour = new VaultBehaviour();
      behaviour.deserializeNBT(nbt);
      return behaviour;
   }
}
