package iskallia.vault.quest.type;

import iskallia.vault.quest.base.Quest;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.world.data.QuestStatesData;
import java.util.List;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class CollectionQuest extends Quest {
   public static final String COLLECTION = "collection";

   public CollectionQuest(
      String id,
      String name,
      Quest.DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("collection", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @SubscribeEvent
   public void onTick(PlayerTickEvent event) {
      if (event.player instanceof ServerPlayer player) {
         if (event.phase == Phase.START) {
            if (player.tickCount % 20 == 0) {
               this.queryCollection(player);
            }
         }
      }
   }

   public void queryCollection(ServerPlayer player) {
      if (QuestStatesData.get().getState(player).getInProgress().contains(this.id)) {
         Item targetItem = (Item)ForgeRegistries.ITEMS.getValue(this.targetId);
         if (targetItem != null) {
            List<ItemStack> items = InventoryUtil.findAllItems(player)
               .stream()
               .map(InventoryUtil.ItemAccess::getStack)
               .filter(stackx -> stackx.getItem() == targetItem)
               .toList();
            int heldAmount = 0;

            for (ItemStack stack : items) {
               heldAmount += stack.getCount();
            }

            if (heldAmount >= this.targetProgress) {
               this.progress(player, heldAmount);
            }
         }
      }
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Collect the item(s). When clicking 'Complete', the items must be in your inventory and are not consumed.");
   }
}
