package iskallia.vault.quest.type;

import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.QuestStatesData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class AnvilQuest extends Quest {
   public static final String ANVIL = "anvil";

   public AnvilQuest(
      String id,
      String name,
      Quest.DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("anvil", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onAnvilCombine(AnvilRepairEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         if (QuestStatesData.get().getState(player).getInProgress().contains(this.id)) {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(event.getIngredientInput().getItem());
            if (itemId != null && itemId.equals(this.targetId)) {
               this.progress(player, event.getItemResult().getCount());
            }
         }
      }
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Combine items in an anvil.");
   }
}
