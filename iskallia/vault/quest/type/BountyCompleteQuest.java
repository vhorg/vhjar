package iskallia.vault.quest.type;

import iskallia.vault.event.event.BountyCompleteEvent;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.QuestStatesData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BountyCompleteQuest extends Quest {
   public static final String BOUNTY_COMPLETE = "bounty_complete";

   public BountyCompleteQuest(
      String id,
      String name,
      Quest.DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("bounty_complete", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onBountyComplete(BountyCompleteEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         if (!QuestStatesData.get().getState(player).getInProgress().contains(this.id)) {
            return;
         }

         this.progress(player, 1.0F);
      }
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Complete a Bounty");
   }
}
