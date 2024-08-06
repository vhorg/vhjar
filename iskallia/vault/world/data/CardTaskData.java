package iskallia.vault.world.data;

import iskallia.vault.core.card.CardEntry;
import iskallia.vault.core.card.TaskLootCardModifier;
import iskallia.vault.core.data.compound.ItemStackList;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.item.CardDeckItem;
import iskallia.vault.task.ResettingTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

@EventBusSubscriber
public class CardTaskData extends SavedData {
   protected static final String DATA_NAME = "the_vault_CardTasks";
   private final Map<UUID, CardTaskData.Entry> entries = new HashMap<>();

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      get().entries.forEach((id, entry) -> entry.task.onDetach());
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
         Set<UUID> newModifiers = new HashSet<>();
         Map<UUID, TaskLootCardModifier> taskModifiers = new HashMap<>();
         Map<UUID, ServerPlayer> taskPlayers = new HashMap<>();
         Map<UUID, Integer> taskTiers = new HashMap<>();

         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ItemStack deckStack = player.getCapability(CuriosCapability.INVENTORY).map(inventory -> {
               ICurioStacksHandler slot = (ICurioStacksHandler)inventory.getCurios().get("deck");
               return slot == null ? ItemStack.EMPTY : slot.getStacks().getStackInSlot(0);
            }).orElse(ItemStack.EMPTY);
            if (!deckStack.isEmpty()) {
               CardDeckItem.getCardDeck(deckStack).ifPresent(deck -> deck.getCards().forEach((pos, card) -> {
                  for (CardEntry entryx : card.getEntries()) {
                     if (entryx.getModifier() instanceof TaskLootCardModifier modifier) {
                        newModifiers.add(modifier.getUuid());
                        taskModifiers.put(modifier.getUuid(), modifier);
                        taskPlayers.put(modifier.getUuid(), player);
                        taskTiers.put(modifier.getUuid(), card.getTier());
                     }
                  }
               }));
            }
         }

         Iterator<Map.Entry<UUID, CardTaskData.Entry>> it = get().entries.entrySet().iterator();

         while (it.hasNext()) {
            Map.Entry<UUID, CardTaskData.Entry> entry = it.next();
            UUID uuid = entry.getKey();
            if (!newModifiers.contains(uuid)) {
               entry.getValue().task.onDetach();
               it.remove();
            } else {
               entry.getValue().context.setSource(EntityTaskSource.ofEntities(JavaRandom.ofNanoTime(), (Entity)taskPlayers.get(uuid)));
               newModifiers.remove(uuid);
            }
         }

         newModifiers.forEach(
            uuid -> {
               CardTaskData.Entry newEntry = new CardTaskData.Entry(
                  taskModifiers.get(uuid).getTask(),
                  TaskContext.of(EntityTaskSource.ofEntities(JavaRandom.ofNanoTime(), (Entity)taskPlayers.get(uuid)), server)
               );
               get().entries.put(uuid, newEntry);
               newEntry.task.onAttach(newEntry.context);
            }
         );
         get().entries.forEach((uuid, entryx) -> {
            if (entryx.task.isCompleted()) {
               ServerVaults.get(taskPlayers.get(uuid).level).ifPresent(vault -> {
                  if (vault.get(Vault.LISTENERS).get(taskPlayers.get(uuid).getUUID()) instanceof Runner runner) {
                     runner.setIfAbsent(Runner.ADDITIONAL_CRATE_ITEMS, ItemStackList::create);
                     runner.get(Runner.ADDITIONAL_CRATE_ITEMS).addAll(taskModifiers.get(uuid).generateLoot(taskTiers.get(uuid), JavaRandom.ofNanoTime()));
                  }
               });
               if (entryx.task instanceof ResettingTask task) {
                  task.onReset(entryx.context);
               }
            }
         });

         for (ServerPlayer playerx : server.getPlayerList().getPlayers()) {
            ItemStack deckStack = playerx.getCapability(CuriosCapability.INVENTORY).map(inventory -> {
               ICurioStacksHandler slot = (ICurioStacksHandler)inventory.getCurios().get("deck");
               return slot == null ? ItemStack.EMPTY : slot.getStacks().getStackInSlot(0);
            }).orElse(ItemStack.EMPTY);
            if (!deckStack.isEmpty()) {
               CardDeckItem.getCardDeck(deckStack)
                  .ifPresent(
                     deck -> {
                        deck.getCards().forEach((pos, card) -> {
                           for (CardEntry entryx : card.getEntries()) {
                              get().entries.forEach((uuid, e) -> {
                                 if (entryx.getModifier() instanceof TaskLootCardModifier modifier && uuid.equals(modifier.getUuid())) {
                                    modifier.task = e.task;
                                 }
                              });
                           }
                        });
                        CardDeckItem.setCardDeck(deckStack, deck);
                        player.getCapability(CuriosCapability.INVENTORY)
                           .ifPresent(inventory -> ((ICurioStacksHandler)inventory.getCurios().get("deck")).getStacks().setStackInSlot(0, deckStack));
                     }
                  );
            }
         }
      }
   }

   @Nonnull
   public CompoundTag save(CompoundTag nbt) {
      return nbt;
   }

   private static CardTaskData load(CompoundTag nbt) {
      return new CardTaskData();
   }

   public static CardTaskData get() {
      return (CardTaskData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(CardTaskData::load, CardTaskData::new, "the_vault_CardTasks");
   }

   public static class Entry {
      private Task task;
      private TaskContext context;

      public Entry() {
      }

      public Entry(Task task, TaskContext context) {
         this.task = task;
         this.context = context;
      }

      public Task getTask() {
         return this.task;
      }

      public TaskContext getContext() {
         return this.context;
      }
   }
}
