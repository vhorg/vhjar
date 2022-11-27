package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.TankConfig;
import iskallia.vault.skill.ability.config.sub.TankProjectileConfig;
import iskallia.vault.skill.ability.config.sub.TankReflectConfig;
import iskallia.vault.skill.ability.effect.TankAbility;
import java.util.ArrayList;
import java.util.List;

public class TankAbilityGroup extends AbilityGroup<TankConfig, TankAbility> {
   @Expose
   private final List<TankProjectileConfig> projectileLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<TankReflectConfig> reflectLevelConfiguration = new ArrayList<>();

   private TankAbilityGroup() {
      super("Tank");
   }

   protected TankConfig getSubConfig(String specialization, int level) {
      return (TankConfig)(switch (specialization) {
         case "Tank_Projectile" -> (TankProjectileConfig)this.projectileLevelConfiguration.get(level);
         case "Tank_Reflect" -> (TankReflectConfig)this.reflectLevelConfiguration.get(level);
         default -> null;
      });
   }

   @Override
   public String getSpecializationName(String specialization) {
      return switch (specialization) {
         case "Tank_Projectile" -> "Rock";
         case "Tank_Reflect" -> "Porcupine";
         default -> "Tank";
      };
   }

   public static TankAbilityGroup defaultConfig() {
      TankAbilityGroup group = new TankAbilityGroup();

      for (int i = 0; i < 5; i++) {
         group.addLevel(new TankConfig(1, 1, 10, 1, 1.0F, 100, 0.05F, 0.5F));
         group.projectileLevelConfiguration.add(new TankProjectileConfig(1, 1, 10, 1, 1.0F, 1.0F, 0.1F * i));
         group.reflectLevelConfiguration.add(new TankReflectConfig(1, 1, 10, 1, 1.0F, 0.1F + 0.1F * i, 0.1F + 0.1F * i));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(this.levelConfiguration, new List[]{this.projectileLevelConfiguration, this.reflectLevelConfiguration});
   }
}
