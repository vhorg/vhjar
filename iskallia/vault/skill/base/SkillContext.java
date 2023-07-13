package iskallia.vault.skill.base;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.source.SkillSource;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkillContext {
   private int level;
   private int learnPoints;
   private int regretPoints;
   private SkillSource source;

   public SkillContext(int level, int learnPoints, int regretPoints, SkillSource source) {
      this.level = level;
      this.learnPoints = learnPoints;
      this.regretPoints = regretPoints;
      this.source = source;
   }

   public static SkillContext empty() {
      return new SkillContext(0, 0, 0, SkillSource.empty());
   }

   public static SkillContext of(ServerPlayer player) {
      PlayerVaultStats stats = PlayerVaultStatsData.get((ServerLevel)player.level).getVaultStats(player);
      return new SkillContext(stats.getVaultLevel(), stats.getUnspentSkillPoints(), stats.getUnspentRegretPoints(), SkillSource.of(player));
   }

   public static SkillContext of(ServerPlayer player, SkillSource source) {
      PlayerVaultStats stats = PlayerVaultStatsData.get((ServerLevel)player.level).getVaultStats(player);
      return new SkillContext(stats.getVaultLevel(), stats.getUnspentSkillPoints(), stats.getUnspentRegretPoints(), source);
   }

   public static SkillContext ofExpertise(ServerPlayer player) {
      PlayerVaultStats stats = PlayerVaultStatsData.get((ServerLevel)player.level).getVaultStats(player);
      return new SkillContext(stats.getVaultLevel(), stats.getUnspentExpertisePoints(), 0, SkillSource.of(player));
   }

   @OnlyIn(Dist.CLIENT)
   public static SkillContext ofClient() {
      return new SkillContext(
         VaultBarOverlay.vaultLevel, VaultBarOverlay.unspentSkillPoints, VaultBarOverlay.unspentRegretPoints, SkillSource.of(Minecraft.getInstance().player)
      );
   }

   @OnlyIn(Dist.CLIENT)
   public static SkillContext ofExpertiseClient() {
      return new SkillContext(VaultBarOverlay.vaultLevel, VaultBarOverlay.unspentExpertisePoints, 0, SkillSource.of(Minecraft.getInstance().player));
   }

   public SkillContext copy() {
      return new SkillContext(this.level, this.learnPoints, this.regretPoints, this.source.copy());
   }

   public int getLevel() {
      return this.level;
   }

   public int getLearnPoints() {
      return this.learnPoints;
   }

   public int getRegretPoints() {
      return this.regretPoints;
   }

   public SkillSource getSource() {
      return this.source;
   }

   public void setLearnPoints(int learnPoints) {
      this.learnPoints = learnPoints;
   }

   public void setRegretPoints(int regretPoints) {
      this.regretPoints = regretPoints;
   }
}
