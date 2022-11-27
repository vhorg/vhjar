package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.MegaJumpConfig;
import iskallia.vault.skill.ability.config.sub.MegaJumpBreakDownConfig;
import iskallia.vault.skill.ability.config.sub.MegaJumpBreakUpConfig;
import iskallia.vault.skill.ability.effect.MegaJumpAbility;
import java.util.ArrayList;
import java.util.List;

public class MegaJumpAbilityGroup extends AbilityGroup<MegaJumpConfig, MegaJumpAbility<MegaJumpConfig>> {
   @Expose
   private final List<MegaJumpBreakUpConfig> breakUpLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<MegaJumpBreakDownConfig> breakDownLevelConfiguration = new ArrayList<>();

   private MegaJumpAbilityGroup() {
      super("Mega Jump");
   }

   protected MegaJumpConfig getSubConfig(String specialization, int level) {
      return (MegaJumpConfig)(switch (specialization) {
         case "Mega Jump_Break_Up" -> (MegaJumpBreakUpConfig)this.breakUpLevelConfiguration.get(level);
         case "Mega Jump_Break_Down" -> (MegaJumpBreakDownConfig)this.breakDownLevelConfiguration.get(level);
         default -> null;
      });
   }

   @Override
   public String getSpecializationName(String specialization) {
      return switch (specialization) {
         case "Mega Jump_Break_Up" -> "Drill";
         case "Mega Jump_Break_Down" -> "Dig";
         default -> "Mega Jump";
      };
   }

   public static MegaJumpAbilityGroup defaultConfig() {
      MegaJumpAbilityGroup group = new MegaJumpAbilityGroup();

      for (int i = 0; i < 3; i++) {
         group.addLevel(new MegaJumpConfig(1, 1, 10, 1, 10.0F, 10 + i));
         group.breakUpLevelConfiguration.add(new MegaJumpBreakUpConfig(1, 1, 10, 1, 10.0F, 10 + i));
         group.breakDownLevelConfiguration.add(new MegaJumpBreakDownConfig(1, 1, 10, 1, 10.0F, 10 + i));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(this.levelConfiguration, new List[]{this.breakUpLevelConfiguration, this.breakDownLevelConfiguration});
   }
}
