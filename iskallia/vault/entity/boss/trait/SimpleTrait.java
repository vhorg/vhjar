package iskallia.vault.entity.boss.trait;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;

public class SimpleTrait implements ITrait {
   private final String type;
   private final Consumer<VaultBossEntity> apply;

   public SimpleTrait(String type, Consumer<VaultBossEntity> apply) {
      this.type = type;
      this.apply = apply;
   }

   @Override
   public CompoundTag serializeNBT() {
      return new CompoundTag();
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
   }

   @Override
   public String getType() {
      return this.type;
   }

   @Override
   public void apply(VaultBossEntity boss) {
      this.apply.accept(boss);
   }

   @Override
   public void addStack(ITrait trait) {
   }
}
