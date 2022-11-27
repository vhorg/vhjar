package iskallia.vault.world.vault.logic.behaviour;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.condition.IVaultCondition;
import iskallia.vault.world.vault.logic.condition.VaultCondition;
import iskallia.vault.world.vault.logic.task.IVaultTask;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultBehaviour implements IVaultCondition, IVaultTask, INBTSerializable<CompoundTag> {
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
   public boolean test(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      return this.condition.test(vault, player, world);
   }

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      this.task.execute(vault, player, world);
   }

   public void tick(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      if (this.test(vault, player, world)) {
         this.execute(vault, player, world);
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.put("Condition", this.condition.serializeNBT());
      nbt.put("Task", this.task.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.condition = VaultCondition.fromNBT(nbt.getCompound("Condition"));
      this.task = VaultTask.fromNBT(nbt.getCompound("Task"));
   }

   public static VaultBehaviour fromNBT(CompoundTag nbt) {
      VaultBehaviour behaviour = new VaultBehaviour();
      behaviour.deserializeNBT(nbt);
      return behaviour;
   }
}
