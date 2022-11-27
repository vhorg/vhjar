package iskallia.vault.client;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.network.message.AbilityActivityMessage;
import iskallia.vault.network.message.AbilityFocusMessage;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.group.AbilityGroup;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientAbilityData {
   private static final Map<String, ClientAbilityData.CooldownData> cooldowns = new HashMap<>();
   private static final Object2BooleanMap<String> active = new Object2BooleanOpenHashMap();
   private static List<AbilityNode<?, ?>> learnedAbilities = new ArrayList<>();
   private static AbilityGroup<?, ?> selectedAbility;

   public static AbilityGroup<?, ?> getSelectedAbility() {
      return selectedAbility;
   }

   public static boolean isActive(String abilityGroup) {
      return active.getBoolean(abilityGroup);
   }

   @Nonnull
   public static List<AbilityNode<?, ?>> getLearnedAbilityNodes() {
      return Collections.unmodifiableList(learnedAbilities);
   }

   public static int getIndexOf(AbilityNode<?, ?> node) {
      return getLearnedAbilityNodes().indexOf(node);
   }

   public static int getIndexOf(AbilityGroup<?, ?> group) {
      List<AbilityNode<?, ?>> nodes = getLearnedAbilityNodes();

      for (int i = 0; i < nodes.size(); i++) {
         AbilityNode<?, ?> node = nodes.get(i);
         if (node.getGroup().equals(group)) {
            return i;
         }
      }

      return -1;
   }

   public static int getCooldown(AbilityGroup<?, ?> abilityGroup) {
      return getCooldown(abilityGroup.getParentName());
   }

   public static int getCooldown(String abilityGroupName) {
      return !cooldowns.containsKey(abilityGroupName) ? 0 : cooldowns.get(abilityGroupName).getCooldownTicks();
   }

   public static int getMaxCooldown(AbilityGroup<?, ?> abilityGroup) {
      return getMaxCooldown(abilityGroup.getParentName());
   }

   public static int getMaxCooldown(String abilityGroupName) {
      return !cooldowns.containsKey(abilityGroupName) ? 0 : cooldowns.get(abilityGroupName).getMaxCooldownTicks();
   }

   @Nullable
   public static AbilityNode<?, ?> getLearnedAbilityNode(AbilityGroup<?, ?> ability) {
      return getLearnedAbilityNode(ability.getParentName());
   }

   @Nullable
   public static AbilityNode<?, ?> getLearnedAbilityNode(String abilityName) {
      for (AbilityNode<?, ?> node : learnedAbilities) {
         if (node.getGroup().getParentName().equals(abilityName)) {
            return node;
         }
      }

      return null;
   }

   public static void updateAbilities(AbilityKnownOnesMessage pkt) {
      learnedAbilities = pkt.getLearnedAbilities();
   }

   public static void updateActivity(AbilityActivityMessage pkt) {
      cooldowns.put(pkt.getAbility(), new ClientAbilityData.CooldownData(pkt.getCooldownTicks(), pkt.getMaxCooldownTicks()));
      if (pkt.getActiveFlag() != AbilityTree.ActivityFlag.NO_OP) {
         active.put(pkt.getAbility(), pkt.getActiveFlag() == AbilityTree.ActivityFlag.ACTIVATE_ABILITY);
      }
   }

   public static void updateSelectedAbility(AbilityFocusMessage pkt) {
      selectedAbility = ModConfigs.ABILITIES.getAbilityGroupByName(pkt.getSelectedAbility());
   }

   static {
      active.defaultReturnValue(false);
   }

   public static class CooldownData {
      private final int cooldownTicks;
      private final int maxCooldownTicks;

      public CooldownData(int cooldownTicks, int maxCooldownTicks) {
         this.cooldownTicks = cooldownTicks;
         this.maxCooldownTicks = maxCooldownTicks;
      }

      public int getCooldownTicks() {
         return this.cooldownTicks;
      }

      public int getMaxCooldownTicks() {
         return this.maxCooldownTicks;
      }
   }
}
