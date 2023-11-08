package iskallia.vault.entity.boss;

import iskallia.vault.core.util.WeightedList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class MeleeStageAttributes {
   private final int health;
   private final int baseAttackDamage;
   private final double baseSpeed;
   private final WeightedList<MeleeAttacks.AttackData> meleeAttacks;
   private final WeightedList<MeleeAttacks.AttackData> rageAttacks;

   public MeleeStageAttributes(
      int health, int baseAttackDamage, double baseSpeed, WeightedList<MeleeAttacks.AttackData> meleeAttacks, WeightedList<MeleeAttacks.AttackData> rageAttacks
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

   public WeightedList<MeleeAttacks.AttackData> getMeleeAttacks() {
      return this.meleeAttacks;
   }

   public WeightedList<MeleeAttacks.AttackData> getRageAttacks() {
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

   protected static WeightedList<MeleeAttacks.AttackData> deserializeAttacks(ListTag tag) {
      WeightedList<MeleeAttacks.AttackData> weightedList = new WeightedList<>();

      for (Tag element : tag) {
         CompoundTag compoundTag = (CompoundTag)element;
         weightedList.add(MeleeAttacks.AttackData.from(compoundTag), compoundTag.getDouble("Weight"));
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

   private ListTag serializeAttacks(WeightedList<MeleeAttacks.AttackData> attacks) {
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
