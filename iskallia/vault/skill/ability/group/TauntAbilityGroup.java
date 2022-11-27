package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.TauntConfig;
import iskallia.vault.skill.ability.config.sub.TauntRepelConfig;
import iskallia.vault.skill.ability.effect.TauntAbility;
import java.util.ArrayList;
import java.util.List;

public class TauntAbilityGroup extends AbilityGroup<TauntConfig, TauntAbility> {
   @Expose
   private final List<TauntRepelConfig> repelLevelConfiguration = new ArrayList<>();

   protected TauntAbilityGroup() {
      super("Taunt");
   }

   protected TauntConfig getSubConfig(String specialization, int level) {
      byte var4 = -1;
      switch (specialization.hashCode()) {
         case 1879631283:
            if (specialization.equals("Taunt_Repel")) {
               var4 = 0;
            }
         default:
            return switch (var4) {
               case 0 -> (TauntRepelConfig)this.repelLevelConfiguration.get(level);
               default -> null;
            };
      }
   }

   @Override
   public String getSpecializationName(String specialization) {
      byte var3 = -1;
      switch (specialization.hashCode()) {
         case 1879631283:
            if (specialization.equals("Taunt_Repel")) {
               var3 = 0;
            }
         default:
            return switch (var3) {
               case 0 -> "Fear";
               default -> "Taunt";
            };
      }
   }

   public static TauntAbilityGroup defaultConfig() {
      TauntAbilityGroup group = new TauntAbilityGroup();

      for (int i = 0; i < 5; i++) {
         group.addLevel(new TauntConfig(1, 1, 10, 1, 1.0F, 10.0F, 400));
         group.repelLevelConfiguration.add(new TauntRepelConfig(1, 1, 10, 1, 1.0F, 10.0F, 400, 10.0F));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(this.levelConfiguration, new List[]{this.repelLevelConfiguration});
   }
}
