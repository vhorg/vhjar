package iskallia.vault.skill.ability.component;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.skill.ability.group.AbilityGroup;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public final class AbilityDescriptionFactory {
   public static MutableComponent create(AbilityGroup<?, ?> group, @Nullable String specialization, int abilityLevel, int vaultLevel) {
      String skillName = specialization == null ? group.getParentName() : specialization;
      MutableComponent component = ModConfigs.ABILITIES_DESCRIPTIONS.getDescriptionFor(skillName);
      if (abilityLevel > 0) {
         AbstractAbilityConfig config = group.getAbilityConfig(specialization, abilityLevel - 1);
         List<String> keys = ModConfigs.ABILITIES_DESCRIPTIONS.getCurrent(skillName);
         appendLabels(component, keys, "\n\nCurrent", new AbilityLabelContext<>(config, vaultLevel));
      }

      if (abilityLevel < group.getMaxLevel()) {
         AbstractAbilityConfig config = group.getAbilityConfig(specialization, abilityLevel);
         List<String> keys = ModConfigs.ABILITIES_DESCRIPTIONS.getNext(skillName);
         appendLabels(component, keys, "\n\nNext", new AbilityLabelContext<>(config, vaultLevel));
      }

      return component;
   }

   private static void appendLabels(MutableComponent component, List<String> keys, String header, AbilityLabelContext<?> context) {
      if (!keys.isEmpty()) {
         component.append(new TextComponent(header).withStyle(Style.EMPTY.withBold(true)));

         for (String key : keys) {
            component.append(AbilityLabelFactory.create(key, context));
         }
      }
   }

   private AbilityDescriptionFactory() {
   }
}
