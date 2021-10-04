package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.config.DashConfig;
import iskallia.vault.skill.ability.config.sub.DashBuffConfig;
import iskallia.vault.skill.ability.config.sub.DashDamageConfig;
import iskallia.vault.skill.ability.config.sub.DashHealConfig;
import iskallia.vault.skill.ability.effect.DashAbility;
import java.util.ArrayList;
import java.util.List;

public class DashAbilityGroup extends AbilityGroup<DashConfig, DashAbility<DashConfig>> {
   @Expose
   private final List<DashBuffConfig> buffLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<DashDamageConfig> damageLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<DashHealConfig> healLevelConfiguration = new ArrayList<>();

   private DashAbilityGroup() {
      super("Dash");
   }

   protected DashConfig getSubConfig(String specialization, int level) {
      switch (specialization) {
         case "Dash_Buff":
            return this.buffLevelConfiguration.get(level);
         case "Dash_Damage":
            return this.damageLevelConfiguration.get(level);
         case "Dash_Heal":
            return this.healLevelConfiguration.get(level);
         default:
            return null;
      }
   }

   @Override
   public String getSpecializationName(String specialization) {
      switch (specialization) {
         case "Dash_Buff":
            return "Power-Up";
         case "Dash_Damage":
            return "Bullet";
         case "Dash_Heal":
            return "Recharge";
         default:
            return "Dash";
      }
   }

   public static DashAbilityGroup defaultConfig() {
      DashAbilityGroup group = new DashAbilityGroup();
      group.addLevel(new DashConfig(2, 1));
      group.addLevel(new DashConfig(1, 2));
      group.addLevel(new DashConfig(1, 3));
      group.addLevel(new DashConfig(1, 4));
      group.addLevel(new DashConfig(1, 5));
      group.addLevel(new DashConfig(1, 6));
      group.addLevel(new DashConfig(1, 7));
      group.addLevel(new DashConfig(1, 8));
      group.addLevel(new DashConfig(1, 9));
      group.addLevel(new DashConfig(1, 10));
      group.buffLevelConfiguration.add(new DashBuffConfig(2, 1, 0.1F, 140));
      group.buffLevelConfiguration.add(new DashBuffConfig(1, 2, 0.1F, 140));
      group.buffLevelConfiguration.add(new DashBuffConfig(1, 3, 0.15F, 140));
      group.buffLevelConfiguration.add(new DashBuffConfig(1, 4, 0.15F, 140));
      group.buffLevelConfiguration.add(new DashBuffConfig(1, 5, 0.15F, 140));
      group.buffLevelConfiguration.add(new DashBuffConfig(1, 6, 0.15F, 140));
      group.buffLevelConfiguration.add(new DashBuffConfig(1, 7, 0.2F, 140));
      group.buffLevelConfiguration.add(new DashBuffConfig(1, 8, 0.2F, 140));
      group.buffLevelConfiguration.add(new DashBuffConfig(1, 9, 0.2F, 140));
      group.buffLevelConfiguration.add(new DashBuffConfig(1, 10, 0.25F, 140));
      group.damageLevelConfiguration.add(new DashDamageConfig(2, 1, 0.5F, 4.0F));
      group.damageLevelConfiguration.add(new DashDamageConfig(1, 2, 0.5F, 4.0F));
      group.damageLevelConfiguration.add(new DashDamageConfig(1, 3, 0.6F, 5.0F));
      group.damageLevelConfiguration.add(new DashDamageConfig(1, 4, 0.75F, 5.0F));
      group.damageLevelConfiguration.add(new DashDamageConfig(1, 5, 1.0F, 6.0F));
      group.damageLevelConfiguration.add(new DashDamageConfig(1, 6, 1.25F, 6.0F));
      group.damageLevelConfiguration.add(new DashDamageConfig(1, 7, 1.5F, 7.0F));
      group.damageLevelConfiguration.add(new DashDamageConfig(1, 8, 1.75F, 7.0F));
      group.damageLevelConfiguration.add(new DashDamageConfig(1, 9, 2.0F, 7.0F));
      group.damageLevelConfiguration.add(new DashDamageConfig(1, 10, 2.5F, 8.0F));
      group.healLevelConfiguration.add(new DashHealConfig(2, 1, 1.0F));
      group.healLevelConfiguration.add(new DashHealConfig(1, 2, 1.0F));
      group.healLevelConfiguration.add(new DashHealConfig(1, 3, 2.0F));
      group.healLevelConfiguration.add(new DashHealConfig(1, 4, 2.0F));
      group.healLevelConfiguration.add(new DashHealConfig(1, 5, 2.0F));
      group.healLevelConfiguration.add(new DashHealConfig(1, 6, 4.0F));
      group.healLevelConfiguration.add(new DashHealConfig(1, 7, 4.0F));
      group.healLevelConfiguration.add(new DashHealConfig(1, 8, 4.0F));
      group.healLevelConfiguration.add(new DashHealConfig(1, 9, 6.0F));
      group.healLevelConfiguration.add(new DashHealConfig(1, 10, 8.0F));
      return group;
   }
}
