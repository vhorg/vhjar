package iskallia.vault.entity.boss.stage;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class MeleeStageAttributes {
   private final int health;
   private final int baseAttackDamage;
   private final double baseSpeed;
   private final WeightedList<VaultBossBaseEntity.AttackData> meleeAttacks;
   private final WeightedList<VaultBossBaseEntity.AttackData> rageAttacks;

   public MeleeStageAttributes(
      int health,
      int baseAttackDamage,
      double baseSpeed,
      WeightedList<VaultBossBaseEntity.AttackData> meleeAttacks,
      WeightedList<VaultBossBaseEntity.AttackData> rageAttacks
   ) {
      this.health = health;
      this.baseAttackDamage = baseAttackDamage;
      this.baseSpeed = baseSpeed;
      this.meleeAttacks = meleeAttacks;
      this.rageAttacks = rageAttacks;
   }

   public int getHealth() {
      return this.health;
   }

   public double getBaseAttackDamage() {
      return this.baseAttackDamage;
   }

   public double getBaseSpeed() {
      return this.baseSpeed;
   }

   public WeightedList<VaultBossBaseEntity.AttackData> getMeleeAttacks() {
      return this.meleeAttacks;
   }

   public WeightedList<VaultBossBaseEntity.AttackData> getRageAttacks() {
      return this.rageAttacks;
   }

   public static MeleeStageAttributes from(CompoundTag tag) {
      return new MeleeStageAttributes(
         tag.getInt("Health"),
         tag.getInt("AttackDamage"),
         tag.getDouble("Speed"),
         deserializeAttacks(tag.getList("MeleeAttacks", 10)),
         deserializeAttacks(tag.getList("RageAttacks", 10))
      );
   }

   protected static WeightedList<VaultBossBaseEntity.AttackData> deserializeAttacks(ListTag tag) {
      WeightedList<VaultBossBaseEntity.AttackData> weightedList = new WeightedList<>();

      for (Tag element : tag) {
         CompoundTag compoundTag = (CompoundTag)element;
         weightedList.add(VaultBossBaseEntity.AttackData.from(compoundTag), compoundTag.getDouble("Weight"));
      }

      return weightedList;
   }

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("Health", this.health);
      tag.putInt("AttackDamage", this.baseAttackDamage);
      tag.putDouble("Speed", this.baseSpeed);
      tag.put("MeleeAttacks", this.serializeAttacks(this.meleeAttacks));
      tag.put("RageAttacks", this.serializeAttacks(this.rageAttacks));
      return tag;
   }

   private ListTag serializeAttacks(WeightedList<VaultBossBaseEntity.AttackData> attacks) {
      ListTag tag = new ListTag();
      attacks.forEach((attackData, weight) -> {
         CompoundTag compoundTag = new CompoundTag();
         attackData.serializeTo(compoundTag);
         compoundTag.putDouble("Weight", weight);
         tag.add(compoundTag);
      });
      return tag;
   }
}
