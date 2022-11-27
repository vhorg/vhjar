package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.DashConfig;
import iskallia.vault.skill.ability.config.sub.DashDamageConfig;
import iskallia.vault.skill.ability.effect.DashAbility;
import java.util.ArrayList;
import java.util.List;

public class DashAbilityGroup extends AbilityGroup<DashConfig, DashAbility<DashConfig>> {
   @Expose
   private final List<DashDamageConfig> damageLevelConfiguration = new ArrayList<>();

   private DashAbilityGroup() {
      super("Dash");
   }

   protected DashConfig getSubConfig(String specialization, int level) {
      byte var4 = -1;
      switch (specialization.hashCode()) {
         case 97445052:
            if (specialization.equals("Dash_Damage")) {
               var4 = 0;
            }
         default:
            return switch (var4) {
               case 0 -> (DashDamageConfig)this.damageLevelConfiguration.get(level);
               default -> null;
            };
      }
   }

   @Override
   public String getSpecializationName(String specialization) {
      byte var3 = -1;
      switch (specialization.hashCode()) {
         case 97445052:
            if (specialization.equals("Dash_Damage")) {
               var3 = 0;
            }
         default:
            return switch (var3) {
               case 0 -> "Bullet";
               default -> "Dash";
            };
      }
   }

   public static DashAbilityGroup defaultConfig() {
      DashAbilityGroup group = new DashAbilityGroup();

      for (int i = 0; i < 10; i++) {
         group.addLevel(new DashConfig(1, 1, 10, 1, 10.0F, 10));
         group.damageLevelConfiguration.add(new DashDamageConfig(1, 1, 10, 1, 10.0F, 10, 0.5F));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(this.levelConfiguration, new List[]{this.damageLevelConfiguration});
   }
}
