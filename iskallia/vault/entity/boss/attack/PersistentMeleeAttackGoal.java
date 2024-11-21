package iskallia.vault.entity.boss.attack;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.trait.ITrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class PersistentMeleeAttackGoal extends BossMeleeAttackGoal implements ITrait {
   public static final String TYPE = "melee_attack";
   private final WeightedList<VaultBossBaseEntity.AttackData> meleeAttacks = new WeightedList<>();

   public PersistentMeleeAttackGoal(VaultBossBaseEntity boss) {
      super(boss);
      this.addPunchDefault();
   }

   private void addPunchDefault() {
      this.meleeAttacks.add(new VaultBossBaseEntity.AttackData("punch", 1.0), 1);
   }

   public PersistentMeleeAttackGoal setAttributes(WeightedList<VaultBossBaseEntity.AttackData> attacks) {
      this.meleeAttacks.clear();
      attacks.forEach(this.meleeAttacks::add);
      return this;
   }

   @Override
   protected WeightedList<VaultBossBaseEntity.AttackData> getMeleeAttacks() {
      return this.meleeAttacks;
   }

   @Override
   public String getType() {
      return "melee_attack";
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addTraitGoal(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof PersistentMeleeAttackGoal other) {
         other.meleeAttacks.forEach(this.meleeAttacks::add);
      }
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      if (!this.meleeAttacks.isEmpty()) {
         ListTag attacks = new ListTag();
         this.meleeAttacks.forEach((attack, weight) -> {
            CompoundTag attackNbt = new CompoundTag();
            attack.serializeTo(attackNbt);
            attackNbt.putDouble("Weight", weight);
            attacks.add(attackNbt);
         });
         nbt.put("Attacks", attacks);
      }

      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      this.meleeAttacks.clear();
      if (nbt.contains("Attacks")) {
         ListTag attacks = nbt.getList("Attacks", 10);
         attacks.forEach(tag -> {
            CompoundTag attackNbt = (CompoundTag)tag;
            VaultBossBaseEntity.AttackData attack = VaultBossBaseEntity.AttackData.from(attackNbt);
            if (boss.getMeleeAttackFactories().containsKey(attack.name())) {
               double weight = attackNbt.getDouble("Weight");
               this.meleeAttacks.add(attack, weight);
            }
         });
      } else {
         this.addPunchDefault();
      }
   }
}
