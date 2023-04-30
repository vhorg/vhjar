package iskallia.vault.quest.type;

import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.QuestStatesData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class CraftingQuest extends Quest {
   public static final String CRAFTING = "crafting";

   public CraftingQuest(
      String id,
      String name,
      Quest.DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("crafting", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @SubscribeEvent
   public void onCrafted(ItemCraftedEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         if (QuestStatesData.get().getState(player).getInProgress().contains(this.id)) {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(event.getCrafting().getItem());
            if (itemId != null && itemId.equals(this.targetId)) {
               this.progress(player, event.getCrafting().getCount());
            }
         }
      }
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Craft the target item.");
   }
}
