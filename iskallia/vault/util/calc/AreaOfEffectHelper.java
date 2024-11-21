package iskallia.vault.util.calc;

import iskallia.vault.gear.attribute.ability.AbilityAreaOfEffectFlatAttribute;
import iskallia.vault.gear.attribute.ability.AbilityAreaOfEffectPercentAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;

public class AreaOfEffectHelper {
   public static float adjustAreaOfEffectKey(LivingEntity entity, @Nullable String abilityKey, float range) {
      Skill ability = ModConfigs.ABILITIES.getAbilityById(abilityKey).orElse(null);
      return adjustAreaOfEffect(entity, ability, range);
   }

   public static float adjustAreaOfEffect(LivingEntity entity, @Nullable Skill ability, float range) {
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      if (ability != null) {
         for (AbilityAreaOfEffectFlatAttribute attribute : snapshot.getAttributeValue(
            ModGearAttributes.ABILITY_AREA_OF_EFFECT_FLAT, VaultGearAttributeTypeMerger.asList()
         )) {
            range = ability.up((skill, rangeIt) -> attribute.adjustAreaOfEffect(skill.getId(), rangeIt), range);
         }
      }

      float multiplier = 1.0F;
      multiplier += snapshot.getAttributeValue(ModGearAttributes.AREA_OF_EFFECT, VaultGearAttributeTypeMerger.floatSum());
      float adjustedRange = range * Math.max(0.0F, multiplier);
      if (ability != null) {
         for (AbilityAreaOfEffectPercentAttribute attribute : snapshot.getAttributeValue(
            ModGearAttributes.ABILITY_AREA_OF_EFFECT_PERCENT, VaultGearAttributeTypeMerger.asList()
         )) {
            adjustedRange = ability.up((skill, rangeIt) -> attribute.adjustAreaOfEffect(skill.getId(), rangeIt), adjustedRange);
         }
      }

      return Math.max(adjustedRange, 0.0F);
   }

   public static int adjustAreaOfEffectRound(LivingEntity entity, @Nullable String abilityKey, int range) {
      return Math.round(adjustAreaOfEffectKey(entity, abilityKey, range));
   }

   public static int adjustAreaOfEffectRound(LivingEntity entity, @Nullable Skill ability, int range) {
      return Math.round(adjustAreaOfEffect(entity, ability, range));
   }
}
