package iskallia.vault.quest.type;

import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.DescriptionData;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LevelUpQuest extends Quest {
   public static final String LEVEL_UP = "level_up";

   public LevelUpQuest(
      String id,
      String name,
      DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("level_up", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @SubscribeEvent
   public void checkPastLevel(PlayerTickEvent event) {
      if (event.player instanceof ServerPlayer player) {
         if (player.tickCount % 100 == 0) {
            int currentLevel = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player).getVaultLevel();

            int targetLevel;
            try {
               targetLevel = Integer.parseInt(this.targetId.getPath());
            } catch (NumberFormatException var6) {
               VaultMod.LOGGER.error("Attempted to parse a vault level via ID in Level Up quest and the value was not a number. FIX IT (eg. \"the_vault:5\")");
               return;
            }

            if (currentLevel >= targetLevel) {
               this.progress(player, 1.0F);
            }
         }
      }
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Gain Vault Levels");
   }
}
