package iskallia.vault.client;

import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.network.message.AbilityActivityMessage;
import iskallia.vault.network.message.AbilityFocusMessage;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.AbilityTree;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientAbilityData {
   private static AbilityTree ABILITIES = new AbilityTree();

   public static AbilityTree getTree() {
      return ABILITIES;
   }

   public static boolean isSelectedAbility(SpecializedSkill ability) {
      SpecializedSkill selected = ABILITIES.getSelected();
      return selected != null && selected.getId().equals(ability.getId());
   }

   @Nonnull
   public static List<TieredSkill> getLearnedAbilities() {
      List<TieredSkill> abilities = new ArrayList<>();
      ABILITIES.iterate(TieredSkill.class, ability -> {
         if (ability.isUnlocked() && ((SpecializedSkill)ability.getParent()).getSpecialization() == ability) {
            abilities.add(ability);
         }
      });
      return abilities;
   }

   @Nullable
   public static SpecializedSkill getSelected() {
      return ABILITIES.getSelected();
   }

   @Nullable
   public static Ability getSelectedAbility() {
      Player player = Minecraft.getInstance().player;
      return player == null
         ? null
         : (Ability)Optional.ofNullable(getSelected())
            .map(SpecializedSkill::getSpecialization)
            .filter(skill -> skill instanceof TieredSkill)
            .map(skill -> ((TieredSkill)skill).getChild())
            .filter(skill -> skill instanceof Ability)
            .orElse(null);
   }

   public static Skill getParent(Ability ability) {
      List<Skill> candidates = new ArrayList<>();
      ABILITIES.iterate(SpecializedSkill.class, skill -> {
         if (ability.getId().equals(skill.getSpecialization().getId())) {
            candidates.add(skill);
         }
      });
      return candidates.isEmpty() ? null : candidates.get(0);
   }

   public static int getIndexOf(String ability) {
      List<TieredSkill> nodes = getLearnedAbilities();

      for (int i = 0; i < nodes.size(); i++) {
         TieredSkill node = nodes.get(i);
         if (node.getId().equals(ability)) {
            return i;
         }
      }

      return -1;
   }

   @Nullable
   public static TieredSkill getLearnedAbilityNode(String abilityName) {
      for (TieredSkill node : getLearnedAbilities()) {
         if (node.getId().equals(abilityName)) {
            return node;
         }
      }

      return null;
   }

   public static void updateAbilities(AbilityKnownOnesMessage pkt) {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      pkt.getTree().writeBits(buffer);
      buffer.setPosition(0);
      ABILITIES.readBits(buffer);
   }

   public static void updateSelectedAbility(AbilityFocusMessage pkt) {
   }

   public static void updateActivity(AbilityActivityMessage pkt) {
   }
}
