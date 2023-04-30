package iskallia.vault.network.message;

import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
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
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get(level);
      AbilityTree abilityTree = abilitiesData.getAbilities(player);
      abilityTree.getForId(message.abilityName).ifPresent(skill -> {
         SkillContext context = SkillContext.of(player);
         if (skill instanceof LearnableSkill learnable && learnable.canLearn(context)) {
            learnable.learn(context);
            PlayerVaultStats stats = statsData.getVaultStats(player);
            stats.setSkillPoints(context.getLearnPoints());
            stats.setRegretPoints(context.getRegretPoints());
            abilityTree.sync(context);
         }
      });
   }

   private static void downgradeAbility(AbilityLevelMessage message, ServerPlayer player) {
      ServerLevel level = player.getLevel();
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get(level);
      AbilityTree abilityTree = abilitiesData.getAbilities(player);
      abilityTree.getForId(message.abilityName).ifPresent(skill -> {
         SkillContext context = SkillContext.of(player);
         if (skill instanceof LearnableSkill learnable && learnable.canRegret(context)) {
            learnable.regret(context);
            PlayerVaultStats stats = statsData.getVaultStats(player);
            stats.setSkillPoints(context.getLearnPoints());
            stats.setRegretPoints(context.getRegretPoints());
            abilityTree.sync(context);
         }
      });
   }
}
