package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.HealConfig;
import iskallia.vault.skill.ability.config.sub.HealEffectConfig;
import iskallia.vault.skill.ability.config.sub.HealGroupConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.ForgeRegistries;

public class HealAbilityGroup extends AbilityGroup<HealConfig, AbstractAbility<HealConfig>> {
   @Expose
   private final List<HealEffectConfig> effectLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<HealGroupConfig> groupLevelConfiguration = new ArrayList<>();

   protected HealAbilityGroup() {
      super("Heal");
   }

   protected HealConfig getSubConfig(String specialization, int level) {
      return (HealConfig)(switch (specialization) {
         case "Heal_Effect" -> (HealEffectConfig)this.effectLevelConfiguration.get(level);
         case "Heal_Group" -> (HealGroupConfig)this.groupLevelConfiguration.get(level);
         default -> null;
      });
   }

   @Override
   public String getSpecializationName(String specialization) {
      return switch (specialization) {
         case "Heal_Effect" -> "Cleanse";
         case "Heal_Group" -> "Aid";
         default -> "Heal";
      };
   }

   public static HealAbilityGroup defaultConfig() {
      HealAbilityGroup group = new HealAbilityGroup();
      int defaultLevelCount = 10;
      List<MobEffect> mobEffectList = ForgeRegistries.MOB_EFFECTS
         .getValues()
         .stream()
         .filter(effect -> effect.getCategory() == MobEffectCategory.HARMFUL)
         .collect(Collectors.toList());

      for (int i = 0; i < 10; i++) {
         group.addLevel(new HealConfig(1, 1, 10, 1, 10.0F, 5.0F));
         group.effectLevelConfiguration.add(new HealEffectConfig(1, 1, 10, 1, 10.0F, mobEffectList));
         group.groupLevelConfiguration.add(new HealGroupConfig(1, 1, 10, 1, 10.0F, 5.0F, 5.0F));
      }

      return group;
   }

   @Override
   public boolean isConfigurationValid() {
      return this.areListsEqualSize(this.levelConfiguration, new List[]{this.effectLevelConfiguration, this.groupLevelConfiguration});
   }
}
