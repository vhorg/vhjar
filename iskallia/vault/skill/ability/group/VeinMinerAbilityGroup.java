package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.VeinMinerConfig;
import iskallia.vault.skill.ability.config.sub.VeinMinerDurabilityConfig;
import iskallia.vault.skill.ability.config.sub.VeinMinerFortuneConfig;
import iskallia.vault.skill.ability.config.sub.VeinMinerVoidConfig;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import java.util.ArrayList;
import java.util.List;

public class VeinMinerAbilityGroup extends AbilityGroup<VeinMinerConfig, VeinMinerAbility<VeinMinerConfig>> {
   @Expose
   private final List<VeinMinerDurabilityConfig> durabilityLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<VeinMinerFortuneConfig> fortuneLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<VeinMinerVoidConfig> voidLevelConfiguration = new ArrayList<>();

   private VeinMinerAbilityGroup() {
      super("Vein Miner");
   }

   protected VeinMinerConfig getSubConfig(String specialization, int level) {
      return (VeinMinerConfig)(switch (specialization) {
         case "Vein Miner_Durability" -> (VeinMinerDurabilityConfig)this.durabilityLevelConfiguration.get(level);
         case "Vein Miner_Fortune" -> (VeinMinerFortuneConfig)this.fortuneLevelConfiguration.get(level);
         case "Vein Miner_Void" -> (VeinMinerVoidConfig)this.voidLevelConfiguration.get(level);
         default -> null;
      });
   }

   @Override
   public String getSpecializationName(String specialization) {
      return switch (specialization) {
         case "Vein Miner_Durability" -> "Finesse";
         case "Vein Miner_Fortune" -> "Fortune";
         case "Vein Miner_Void" -> "Void";
         default -> "Vein Miner";
      };
   }

   public static VeinMinerAbilityGroup defaultConfig() {
      VeinMinerAbilityGroup group = new VeinMinerAbilityGroup();

      for (int i = 0; i < 5; i++) {
         int blockLimit = (int)Math.pow(2.0, i + 2);
         group.addLevel(new VeinMinerConfig(1, 1, 10, 1, blockLimit));
         group.durabilityLevelConfiguration.add(new VeinMinerDurabilityConfig(1, 1, 10, 1, blockLimit, 1 + i));
         group.fortuneLevelConfiguration.add(new VeinMinerFortuneConfig(1, 1, 10, 1, blockLimit, 1 + i));
         group.voidLevelConfiguration.add(new VeinMinerVoidConfig(1, 1, 10, 1, blockLimit));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(
         this.levelConfiguration, new List[]{this.durabilityLevelConfiguration, this.fortuneLevelConfiguration, this.voidLevelConfiguration}
      );
   }
}
