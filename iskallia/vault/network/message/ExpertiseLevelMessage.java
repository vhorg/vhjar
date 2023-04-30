package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ExpertiseLevelMessage {
   private final String expertiseName;
   private final boolean isUpgrade;

   public ExpertiseLevelMessage(String expertiseName, boolean isUpgrade) {
      this.expertiseName = expertiseName;
      this.isUpgrade = isUpgrade;
   }

   public static void encode(ExpertiseLevelMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.expertiseName);
      buffer.writeBoolean(message.isUpgrade);
   }

   public static ExpertiseLevelMessage decode(FriendlyByteBuf buffer) {
      return new ExpertiseLevelMessage(buffer.readUtf(), buffer.readBoolean());
   }

   public static void handle(ExpertiseLevelMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            if (message.isUpgrade) {
               upgradeExpertise(message, sender);
            }
         }
      });
      context.setPacketHandled(true);
   }

   private static void upgradeExpertise(ExpertiseLevelMessage message, ServerPlayer player) {
      ServerLevel level = player.getLevel();
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      PlayerExpertisesData expertisesData = PlayerExpertisesData.get(level);
      ExpertiseTree expertiseTree = expertisesData.getExpertises(player);
      if (!ModConfigs.SKILL_GATES.getGates().isLocked(message.expertiseName, expertiseTree)) {
         expertiseTree.getForId(message.expertiseName).ifPresent(skill -> {
            SkillContext context = SkillContext.ofExpertise(player);
            if (skill instanceof LearnableSkill learnable && learnable.canLearn(context)) {
               learnable.learn(context);
               PlayerVaultStats stats = statsData.getVaultStats(player);
               int learnPoints = stats.getUnspentExpertisePoints() - context.getLearnPoints();
               stats.spendExpertisePoints(player.getServer(), learnPoints);
               expertiseTree.sync(context);
            }
         });
      }
   }
}
