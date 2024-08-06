package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPersistentGoal extends INBTSerializable<CompoundTag> {
   String getType();

   public interface IGoalFactory<T extends Goal & IPersistentGoal> {
      T create(VaultBossBaseEntity var1);
   }
}
