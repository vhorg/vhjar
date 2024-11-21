package iskallia.vault.core.card;

import iskallia.vault.core.data.compound.ItemStackList;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.item.CardDeckItem;
import iskallia.vault.task.ResettingTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.world.data.ServerVaults;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

@EventBusSubscriber
public class ActiveCardTaskHelper {
   private static final ActiveCardTaskHelper INSTANCE = new ActiveCardTaskHelper();
   private final Map<UUID, ActiveCardTaskHelper.TaskEntry> entries = new HashMap<>();

   private ActiveCardTaskHelper() {
   }

   public static ActiveCardTaskHelper getInstance() {
      return INSTANCE;
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      getInstance().entries.forEach((id, entry) -> entry.task.onDetach());
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
         updateTasksFromCards(server);
         getInstance()
            .entries
            .values()
            .forEach(
               entry -> {
                  if (entry.task().isCompleted()) {
                     UUID playerId = entry.cardTask().playerId();
                     ServerPlayer player = server.getPlayerList().getPlayer(playerId);
                     if (player != null) {
                        ServerVaults.get(player.getLevel())
                           .ifPresent(
                              vault -> {
                                 if (vault.get(Vault.LISTENERS).get(playerId) instanceof Runner runner) {
                                    runner.setIfAbsent(Runner.ADDITIONAL_CRATE_ITEMS, ItemStackList::create);
                                    runner.get(Runner.ADDITIONAL_CRATE_ITEMS)
                                       .addAll(entry.cardTask().modifier().generateLoot(entry.cardTask().cardTier(), JavaRandom.ofNanoTime()));
                                 }
                              }
                           );
                     }

                     if (entry.task() instanceof ResettingTask resetTask) {
                        resetTask.onReset(entry.context());
                     }
                  }
               }
            );
      }
   }

   private static void updateTasksFromCards(MinecraftServer server) {
      Map<UUID, ActiveCardTaskHelper.CardTask> modifierTasks = new HashMap<>();

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         ItemStack deckStack = player.getCapability(CuriosCapability.INVENTORY).map(inventory -> {
            ICurioStacksHandler slot = (ICurioStacksHandler)inventory.getCurios().get("deck");
            return slot == null ? ItemStack.EMPTY : slot.getStacks().getStackInSlot(0);
         }).orElse(ItemStack.EMPTY);
         if (!deckStack.isEmpty()) {
            CardDeckItem.getCardDeck(deckStack)
               .ifPresent(
                  deck -> deck.getCards()
                     .values()
                     .forEach(
                        card -> card.getEntries()
                           .stream()
                           .filter(entryx -> entryx.getModifier() instanceof TaskLootCardModifier)
                           .map(entryx -> (TaskLootCardModifier)entryx.getModifier())
                           .forEach(
                              modifier -> modifierTasks.put(modifier.getUuid(), new ActiveCardTaskHelper.CardTask(modifier, card.getTier(), player.getUUID()))
                           )
                     )
               );
         }
      }

      Iterator<Entry<UUID, ActiveCardTaskHelper.TaskEntry>> existingTaskIterator = getInstance().entries.entrySet().iterator();

      while (existingTaskIterator.hasNext()) {
         Entry<UUID, ActiveCardTaskHelper.TaskEntry> entry = existingTaskIterator.next();
         UUID taskModifierId = entry.getKey();
         ActiveCardTaskHelper.CardTask task = modifierTasks.remove(taskModifierId);
         if (task == null) {
            entry.getValue().task.onDetach();
            existingTaskIterator.remove();
         } else {
            entry.getValue().context.setSource(EntityTaskSource.ofUuids(JavaRandom.ofNanoTime(), task.playerId()));
         }
      }

      modifierTasks.forEach(
         (taskModifierIdx, cardTask) -> {
            ActiveCardTaskHelper.TaskEntry newEntry = new ActiveCardTaskHelper.TaskEntry(
               cardTask.modifier().getTask(), TaskContext.of(EntityTaskSource.ofUuids(JavaRandom.ofNanoTime(), cardTask.playerId()), server), cardTask
            );
            getInstance().entries.put(taskModifierIdx, newEntry);
            newEntry.task.onAttach(newEntry.context);
         }
      );
   }

   private record CardTask(TaskLootCardModifier modifier, int cardTier, UUID playerId) {
   }

   private record TaskEntry(Task task, TaskContext context, ActiveCardTaskHelper.CardTask cardTask) {
   }
}
