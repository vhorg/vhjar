package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.NovaConfig;
import iskallia.vault.skill.ability.config.sub.NovaDotConfig;
import iskallia.vault.skill.ability.config.sub.NovaSpeedConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import java.util.ArrayList;
import java.util.List;

public class NovaAbilityGroup extends AbilityGroup<NovaConfig, AbstractAbility<NovaConfig>> {
   @Expose
   private final List<NovaSpeedConfig> speedLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<NovaDotConfig> damageLevelConfiguration = new ArrayList<>();

   protected NovaAbilityGroup() {
      super("Nova");
   }

   protected NovaConfig getSubConfig(String specialization, int level) {
      return (NovaConfig)(switch (specialization) {
         case "Nova_Speed" -> (NovaSpeedConfig)this.speedLevelConfiguration.get(level);
         case "Nova_Dot" -> (NovaDotConfig)this.damageLevelConfiguration.get(level);
         default -> null;
      });
   }

   @Override
   public String getSpecializationName(String specialization) {
      return switch (specialization) {
         case "Nova_Speed" -> "Frost";
         case "Nova_Dot" -> "Poison";
         default -> "Nova";
      };
   }

   public static NovaAbilityGroup defaultConfig() {
      NovaAbilityGroup group = new NovaAbilityGroup();

      for (int i = 0; i < 10; i++) {
         group.addLevel(new NovaConfig(1, 1, 10, 1, 10.0F, 10.0F, 0.5F, 0.0F));
         group.speedLevelConfiguration.add(new NovaSpeedConfig(1, 1, 10, 1, 10.0F, 10.0F, 400, 20));
         group.damageLevelConfiguration.add(new NovaDotConfig(1, 1, 10, 1, 10.0F, 10.0F, 1.0F, 5));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(this.levelConfiguration, new List[]{this.speedLevelConfiguration, this.damageLevelConfiguration});
   }
}
