package iskallia.vault.entity.boss.trait;

import iskallia.vault.entity.boss.VaultBossEntity;
import net.minecraft.nbt.CompoundTag;

public interface ITrait {
   CompoundTag serializeNBT();

   void deserializeNBT(CompoundTag var1);

   String getType();

   void apply(VaultBossEntity var1);

   void addStack(ITrait var1);
}
