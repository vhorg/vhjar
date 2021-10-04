package iskallia.vault.skill.ability.group;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.config.CleanseConfig;
import iskallia.vault.skill.ability.config.sub.CleanseApplyConfig;
import iskallia.vault.skill.ability.config.sub.CleanseEffectConfig;
import iskallia.vault.skill.ability.config.sub.CleanseHealConfig;
import iskallia.vault.skill.ability.config.sub.CleanseImmuneConfig;
import iskallia.vault.skill.ability.effect.CleanseAbility;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;

public class CleanseAbilityGroup extends AbilityGroup<CleanseConfig, CleanseAbility<CleanseConfig>> {
   @Expose
   private final List<CleanseApplyConfig> applyLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<CleanseEffectConfig> effectLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<CleanseHealConfig> healLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<CleanseImmuneConfig> immuneLevelConfiguration = new ArrayList<>();

   private CleanseAbilityGroup() {
      super("Cleanse");
   }

   protected CleanseConfig getSubConfig(String specialization, int level) {
      switch (specialization) {
         case "Cleanse_Applynearby":
            return this.applyLevelConfiguration.get(level);
         case "Cleanse_Effect":
            return this.effectLevelConfiguration.get(level);
         case "Cleanse_Heal":
            return this.healLevelConfiguration.get(level);
         case "Cleanse_Immune":
            return this.immuneLevelConfiguration.get(level);
         default:
            return null;
      }
   }

   @Override
   public String getSpecializationName(String specialization) {
      switch (specialization) {
         case "Cleanse_Applynearby":
            return "Infect";
         case "Cleanse_Effect":
            return "Rejuvenate";
         case "Cleanse_Heal":
            return "Mend";
         case "Cleanse_Immune":
            return "Immune";
         default:
            return "Cleanse";
      }
   }

   public static CleanseAbilityGroup defaultConfig() {
      List<Effect> effects = Lists.newArrayList(
         new Effect[]{
            Effects.field_76422_e,
            Effects.field_76420_g,
            Effects.field_76428_l,
            Effects.field_76443_y,
            Effects.field_188425_z,
            Effects.field_76426_n,
            Effects.field_76439_r
         }
      );
      CleanseAbilityGroup group = new CleanseAbilityGroup();
      group.addLevel(new CleanseConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 600));
      group.addLevel(new CleanseConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 540));
      group.addLevel(new CleanseConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 500));
      group.addLevel(new CleanseConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 460));
      group.addLevel(new CleanseConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 400));
      group.addLevel(new CleanseConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 360));
      group.addLevel(new CleanseConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 320));
      group.addLevel(new CleanseConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 280));
      group.addLevel(new CleanseConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 240));
      group.addLevel(new CleanseConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 200));
      group.applyLevelConfiguration.add(new CleanseApplyConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 600, 3));
      group.applyLevelConfiguration.add(new CleanseApplyConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 540, 3));
      group.applyLevelConfiguration.add(new CleanseApplyConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 500, 4));
      group.applyLevelConfiguration.add(new CleanseApplyConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 460, 4));
      group.applyLevelConfiguration.add(new CleanseApplyConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 400, 4));
      group.applyLevelConfiguration.add(new CleanseApplyConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 360, 5));
      group.applyLevelConfiguration.add(new CleanseApplyConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 320, 5));
      group.applyLevelConfiguration.add(new CleanseApplyConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 280, 5));
      group.applyLevelConfiguration.add(new CleanseApplyConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 240, 5));
      group.applyLevelConfiguration.add(new CleanseApplyConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 200, 6));
      group.healLevelConfiguration.add(new CleanseHealConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 600, 0.5F));
      group.healLevelConfiguration.add(new CleanseHealConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 540, 0.5F));
      group.healLevelConfiguration.add(new CleanseHealConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 500, 0.5F));
      group.healLevelConfiguration.add(new CleanseHealConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 460, 1.0F));
      group.healLevelConfiguration.add(new CleanseHealConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 400, 1.0F));
      group.healLevelConfiguration.add(new CleanseHealConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 360, 1.0F));
      group.healLevelConfiguration.add(new CleanseHealConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 320, 1.0F));
      group.healLevelConfiguration.add(new CleanseHealConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 280, 1.5F));
      group.healLevelConfiguration.add(new CleanseHealConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 240, 1.5F));
      group.healLevelConfiguration.add(new CleanseHealConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 200, 2.0F));
      group.effectLevelConfiguration.add(CleanseEffectConfig.ofEffects(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 600, 0, effects));
      group.effectLevelConfiguration.add(CleanseEffectConfig.ofEffects(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 540, 0, effects));
      group.effectLevelConfiguration.add(CleanseEffectConfig.ofEffects(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 500, 0, effects));
      group.effectLevelConfiguration.add(CleanseEffectConfig.ofEffects(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 460, 1, effects));
      group.effectLevelConfiguration.add(CleanseEffectConfig.ofEffects(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 400, 1, effects));
      group.effectLevelConfiguration.add(CleanseEffectConfig.ofEffects(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 360, 1, effects));
      group.effectLevelConfiguration.add(CleanseEffectConfig.ofEffects(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 320, 2, effects));
      group.effectLevelConfiguration.add(CleanseEffectConfig.ofEffects(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 280, 2, effects));
      group.effectLevelConfiguration.add(CleanseEffectConfig.ofEffects(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 240, 2, effects));
      group.effectLevelConfiguration.add(CleanseEffectConfig.ofEffects(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 200, 2, effects));
      group.immuneLevelConfiguration.add(new CleanseImmuneConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 600, 600));
      group.immuneLevelConfiguration.add(new CleanseImmuneConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 540, 540));
      group.immuneLevelConfiguration.add(new CleanseImmuneConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 500, 500));
      group.immuneLevelConfiguration.add(new CleanseImmuneConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 460, 460));
      group.immuneLevelConfiguration.add(new CleanseImmuneConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 400, 400));
      group.immuneLevelConfiguration.add(new CleanseImmuneConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 360, 360));
      group.immuneLevelConfiguration.add(new CleanseImmuneConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 320, 320));
      group.immuneLevelConfiguration.add(new CleanseImmuneConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 280, 280));
      group.immuneLevelConfiguration.add(new CleanseImmuneConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 240, 240));
      group.immuneLevelConfiguration.add(new CleanseImmuneConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 200, 200));
      return group;
   }
}
