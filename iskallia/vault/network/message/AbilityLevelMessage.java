package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import iskallia.vault.skill.ability.group.AbilityGroup;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbilityLevelMessage {
   private final String abilityName;
   private final boolean isUpgrade;

   public AbilityLevelMessage(String abilityName, boolean isUpgrade) {
      this.abilityName = abilityName;
      this.isUpgrade = isUpgrade;
   }

   public static void encode(AbilityLevelMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.abilityName);
      buffer.writeBoolean(message.isUpgrade);
   }

   public static AbilityLevelMessage decode(FriendlyByteBuf buffer) {
      return new AbilityLevelMessage(buffer.readUtf(), buffer.readBoolean());
   }

   public static void handle(AbilityLevelMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            if (message.isUpgrade) {
               upgradeAbility(message, sender);
            } else {
               downgradeAbility(message, sender);
            }
         }
      });
      context.setPacketHandled(true);
   }

   private static void upgradeAbility(AbilityLevelMessage message, ServerPlayer player) {
      ServerLevel level = player.getLevel();
      AbilityGroup<?, ?> abilityGroup = ModConfigs.ABILITIES.getAbilityGroupByName(message.abilityName);
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get(level);
      AbilityTree abilityTree = abilitiesData.getAbilities(player);
      AbilityNode<?, ?> abilityNode = abilityTree.getNodeByName(message.abilityName);
      PlayerVaultStats stats = statsData.getVaultStats(player);
      if (stats.getVaultLevel() >= abilityNode.getAbilityConfig().getLevelRequirement()) {
         if (abilityNode.getLevel() < abilityGroup.getMaxLevel()) {
            int requiredSkillPts = abilityGroup.levelUpCost(abilityNode.getSpecialization(), abilityNode.getLevel() + 1);
            if (stats.getUnspentSkillPoints() >= requiredSkillPts) {
               abilitiesData.upgradeAbility(player, abilityNode);
               statsData.spendSkillPoints(player, requiredSkillPts);
            }
         }
      }
   }

   private static void downgradeAbility(AbilityLevelMessage message, ServerPlayer player) {
      ServerLevel level = player.getLevel();
      AbilityGroup<?, ?> abilityGroup = ModConfigs.ABILITIES.getAbilityGroupByName(message.abilityName);
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get(level);
      AbilityTree abilityTree = abilitiesData.getAbilities(player);
      AbilityNode<?, ?> abilityNode = abilityTree.getNodeByName(message.abilityName);
      AbstractAbility<?> ability = abilityNode.getAbility();
      PlayerVaultStats stats = statsData.getVaultStats(player);
      if (ability != null && abilityNode.getAbilityConfig() != null && abilityNode.isLearned()) {
         boolean isSpecialization = !ability.getAbilityGroupName().equals(message.abilityName);
         if (!isSpecialization) {
            int requiredRegretPoints = abilityNode.getAbilityConfig().getRegretCost();
            if (abilityNode.getLevel() == 1) {
               for (AbilityGroup<?, ?> dependent : ModConfigs.SKILL_GATES.getGates().getAbilitiesDependingOn(abilityGroup.getParentName())) {
                  if (abilityTree.getNodeOf(dependent).isLearned()) {
                     return;
                  }
               }
            }

            if (stats.getUnspentRegretPoints() >= requiredRegretPoints) {
               abilitiesData.downgradeAbility(player, abilityNode);
               statsData.spendSkillPoints(player, -abilityNode.getAbilityConfig().getLearningCost());
               statsData.spendRegretPoints(player, requiredRegretPoints);
            }
         }
      }
   }
}
