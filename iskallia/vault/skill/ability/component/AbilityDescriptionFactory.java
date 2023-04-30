package iskallia.vault.skill.ability.component;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.TieredSkill;
import java.util.List;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public final class AbilityDescriptionFactory {
   public static MutableComponent create(TieredSkill skill, int tier, int maxTier, int vaultLevel) {
      MutableComponent component = ModConfigs.ABILITIES_DESCRIPTIONS.getDescriptionFor(skill.getId());
      if (tier > 0) {
         List<String> keys = ModConfigs.ABILITIES_DESCRIPTIONS.getCurrent(skill.getId());
         appendLabels(component, keys, "\n\nCurrent", new AbilityLabelContext<>(skill.getChild(tier), vaultLevel));
      }

      if (tier < maxTier) {
         List<String> keys = ModConfigs.ABILITIES_DESCRIPTIONS.getNext(skill.getId());
         appendLabels(component, keys, "\n\nNext", new AbilityLabelContext<>(skill.getChild(tier + 1), vaultLevel));
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
