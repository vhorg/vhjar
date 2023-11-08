package iskallia.vault.entity.boss;

import iskallia.vault.core.util.WeightedList;
import net.minecraft.nbt.CompoundTag;

public class BerserkStageAttributes extends MeleeStageAttributes {
   private final double maxSpeedMultiplier;
   private final double maxBaseDamageMultiplier;
   private final int bloodOrbSpawnCooldown;
   private final int maxBloodOrbs;

   public BerserkStageAttributes(
      int health,
      int baseAttackDamage,
      double baseSpeed,
      double maxSpeedMultiplier,
      double maxBaseDamageMultiplier,
      WeightedList<MeleeAttacks.AttackData> meleeAttacks,
      WeightedList<MeleeAttacks.AttackData> rageAttacks,
      int bloodOrbSpawnCooldown,
      int maxBloodOrbs
   ) {
      super(health, baseAttackDamage, baseSpeed, meleeAttacks, rageAttacks);
      this.maxSpeedMultiplier = maxSpeedMultiplier;
      this.maxBaseDamageMultiplier = maxBaseDamageMultiplier;
      this.bloodOrbSpawnCooldown = bloodOrbSpawnCooldown;
      this.maxBloodOrbs = maxBloodOrbs;
   }

   public double getMaxSpeedMultiplier() {
      return this.maxSpeedMultiplier;
   }

   public double getMaxBaseDamageMultiplier() {
      return this.maxBaseDamageMultiplier;
   }

   public int getBloodOrbSpawnCooldown() {
      return this.bloodOrbSpawnCooldown;
   }

   public int getMaxBloodOrbs() {
      return this.maxBloodOrbs;
   }

   public static BerserkStageAttributes from(CompoundTag tag) {
      return new BerserkStageAttributes(
         tag.getInt("Health"),
         tag.getInt("AttackDamage"),
         tag.getDouble("Speed"),
         tag.getDouble("MaxSpeedMultiplier"),
         tag.getDouble("MaxBaseDamageMultiplier"),
         deserializeAttacks(tag.getList("MeleeAttacks", 10)),
         deserializeAttacks(tag.getList("RageAttacks", 10)),
         tag.getInt("BloodOrbSpawnCooldown"),
         tag.getInt("MaxBloodOrbs")
      );
   }

   @Override
   public CompoundTag serialize() {
      CompoundTag tag = super.serialize();
      tag.putDouble("MaxSpeedMultiplier", this.maxSpeedMultiplier);
      tag.putDouble("MaxBaseDamageMultiplier", this.maxBaseDamageMultiplier);
      tag.putInt("BloodOrbSpawnCooldown", this.bloodOrbSpawnCooldown);
      tag.putInt("MaxBloodOrbs", this.maxBloodOrbs);
      return tag;
   }
}
