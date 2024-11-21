package iskallia.vault.quest.type;

import iskallia.vault.config.entry.DescriptionData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.world.data.QuestStatesData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class CollectionQuest extends Quest {
   public static final String COLLECTION = "collection";

   public CollectionQuest(
      String id,
      String name,
      DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("collection", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.player instanceof ServerPlayer sPlayer) {
         if (event.phase == Phase.START) {
            if (sPlayer.tickCount % 20 == 0) {
               checkCollections(sPlayer);
            }
         }
      }
   }

   private static void checkCollections(ServerPlayer sPlayer) {
      Set<String> inProgressQuests = QuestStatesData.get().getState(sPlayer).getInProgress();
      Map<Item, List<CollectionQuest>> itemQuestMap = new HashMap<>();
      ModConfigs.QUESTS
         .getQuests()
         .stream()
         .filter(quest -> inProgressQuests.contains(quest.getId()))
         .filter(quest -> quest instanceof CollectionQuest)
         .map(quest -> (CollectionQuest)quest)
         .forEach(quest -> {
            Item target = (Item)ForgeRegistries.ITEMS.getValue(quest.targetId);
            if (target != null) {
               itemQuestMap.computeIfAbsent(target, k -> new ArrayList<>()).add(quest);
            }
         });
      Map<CollectionQuest, Integer> countMap = new HashMap<>();
      InventoryUtil.findAllItems(sPlayer)
         .stream()
         .map(InventoryUtil.ItemAccess::getStack)
         .filter(stack -> itemQuestMap.containsKey(stack.getItem()))
         .forEach(stack -> {
            List<CollectionQuest> quests = itemQuestMap.get(stack.getItem());
            if (quests != null) {
               quests.forEach(quest -> {
                  int existing = countMap.getOrDefault(quest, 0);
                  countMap.put(quest, existing + stack.getCount());
               });
            }
         });
      countMap.forEach((quest, count) -> {
         if (count.intValue() >= quest.targetProgress) {
            quest.progress(sPlayer, count.intValue());
         }
      });
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Collect the item(s). When clicking 'Complete', the items must be in your inventory and are not consumed.");
   }
}
