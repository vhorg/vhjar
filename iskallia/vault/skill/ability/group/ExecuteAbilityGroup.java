package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.config.ExecuteConfig;
import iskallia.vault.skill.ability.config.sub.ExecuteBuffConfig;
import iskallia.vault.skill.ability.config.sub.ExecuteDamageConfig;
import iskallia.vault.skill.ability.effect.ExecuteAbility;
import java.util.ArrayList;
import java.util.List;

public class ExecuteAbilityGroup extends AbilityGroup<ExecuteConfig, ExecuteAbility<ExecuteConfig>> {
   @Expose
   private final List<ExecuteBuffConfig> buffLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<ExecuteDamageConfig> damageLevelConfiguration = new ArrayList<>();

   private ExecuteAbilityGroup() {
      super("Execute");
   }

   protected ExecuteConfig getSubConfig(String specialization, int level) {
      switch (specialization) {
         case "Execute_Buff":
            return this.buffLevelConfiguration.get(level);
         case "Execute_Damage":
            return this.damageLevelConfiguration.get(level);
         default:
            return null;
      }
   }

   @Override
   public String getSpecializationName(String specialization) {
      switch (specialization) {
         case "Execute_Buff":
            return "Combo";
         case "Execute_Damage":
            return "Precision";
         default:
            return "Execute";
      }
   }

   public static ExecuteAbilityGroup defaultConfig() {
      ExecuteAbilityGroup group = new ExecuteAbilityGroup();
      group.addLevel(new ExecuteConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.05F, 100));
      group.addLevel(new ExecuteConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.1F, 100));
      group.addLevel(new ExecuteConfig(2, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.15F, 100));
      group.addLevel(new ExecuteConfig(2, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.2F, 100));
      group.addLevel(new ExecuteConfig(3, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.25F, 100));
      group.addLevel(new ExecuteConfig(3, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.3F, 100));
      group.addLevel(new ExecuteConfig(4, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.35F, 100));
      group.addLevel(new ExecuteConfig(4, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.4F, 100));
      group.addLevel(new ExecuteConfig(5, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.45F, 100));
      group.addLevel(new ExecuteConfig(5, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.5F, 100));
      group.buffLevelConfiguration.add(new ExecuteBuffConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.05F, 100, 0.05F));
      group.buffLevelConfiguration.add(new ExecuteBuffConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.1F, 100, 0.1F));
      group.buffLevelConfiguration.add(new ExecuteBuffConfig(2, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.15F, 100, 0.15F));
      group.buffLevelConfiguration.add(new ExecuteBuffConfig(2, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.2F, 100, 0.2F));
      group.buffLevelConfiguration.add(new ExecuteBuffConfig(3, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.25F, 100, 0.25F));
      group.buffLevelConfiguration.add(new ExecuteBuffConfig(3, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.3F, 100, 0.3F));
      group.buffLevelConfiguration.add(new ExecuteBuffConfig(4, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.35F, 100, 0.35F));
      group.buffLevelConfiguration.add(new ExecuteBuffConfig(4, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.4F, 100, 0.4F));
      group.buffLevelConfiguration.add(new ExecuteBuffConfig(5, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.45F, 100, 0.45F));
      group.buffLevelConfiguration.add(new ExecuteBuffConfig(5, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 0.5F, 100, 0.5F));
      group.damageLevelConfiguration.add(new ExecuteDamageConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 100, 0.1F));
      group.damageLevelConfiguration.add(new ExecuteDamageConfig(1, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 100, 0.2F));
      group.damageLevelConfiguration.add(new ExecuteDamageConfig(2, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 100, 0.3F));
      group.damageLevelConfiguration.add(new ExecuteDamageConfig(2, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 100, 0.4F));
      group.damageLevelConfiguration.add(new ExecuteDamageConfig(3, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 100, 0.5F));
      group.damageLevelConfiguration.add(new ExecuteDamageConfig(3, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 100, 0.6F));
      group.damageLevelConfiguration.add(new ExecuteDamageConfig(4, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 100, 0.7F));
      group.damageLevelConfiguration.add(new ExecuteDamageConfig(4, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 100, 0.8F));
      group.damageLevelConfiguration.add(new ExecuteDamageConfig(5, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 100, 0.9F));
      group.damageLevelConfiguration.add(new ExecuteDamageConfig(5, AbilityConfig.Behavior.RELEASE_TO_PERFORM, 100, 1.0F));
      return group;
   }
}
