package iskallia.vault.core.vault.objective.bingo;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class BingoCard extends DataObject<BingoCard> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<TaskSource> ENTITY_TASK_SOURCE = FieldKey.of("entity_task_source", TaskSource.class)
      .with(Version.v1_0, Adapters.TASK_SOURCE, DISK.all())
      .register(FIELDS);
   private static final IBitAdapter<BingoCard.BingoItems, SyncContext> ITEMS_ADAPTER = new IBitAdapter<BingoCard.BingoItems, SyncContext>() {
      public void writeBits(@Nullable BingoCard.BingoItems value, BitBuffer buffer, SyncContext context) {
         if (value == null) {
            buffer.writeBoolean(false);
         } else {
            buffer.writeBoolean(true);
            value.writeBits(buffer, context);
         }
      }

      public Optional<BingoCard.BingoItems> readBits(BitBuffer buffer, SyncContext context) {
         return !buffer.readBoolean() ? Optional.empty() : Optional.of(BingoCard.BingoItems.readBits(buffer, context));
      }
   };
   public static final FieldKey<BingoCard.BingoItems> ITEMS = FieldKey.of("items", BingoCard.BingoItems.class)
      .with(Version.v1_0, ITEMS_ADAPTER, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> COMPLETED_BINGOS = FieldKey.of("completed_bingos", Integer.class)
      .with(Version.v1_0, Adapters.INT, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<ResourceLocation> MODIFIER_POOL = FieldKey.of("modifier_pool", ResourceLocation.class)
      .with(Version.v1_0, Adapters.IDENTIFIER, DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public BingoCard() {
   }

   public BingoCard(int level, EntityTaskSource entityTaskSource) {
      this.set(ENTITY_TASK_SOURCE, entityTaskSource);
      this.set(COMPLETED_BINGOS, Integer.valueOf(0));
      this.initFromConfig(level, entityTaskSource);
   }

   private void initFromConfig(int level, EntityTaskSource entityTaskSource) {
      BingoCard.BingoItems items = new BingoCard.BingoItems();
      this.set(ITEMS, items);
   }

   public void onTick(VirtualWorld world, Vault vault) {
      TaskSource taskSource = this.get(ENTITY_TASK_SOURCE);
      BingoCard.BingoItems bingoItems = this.get(ITEMS);
      int completedBingosBeforeCheck = this.getCompletedBingos();
      if (bingoItems.onTick(world, taskSource) && this.checkCompletion()) {
         if (this.isFullyCompleted()) {
         }

         int newlyCompletedBingos = this.getCompletedBingos() - completedBingosBeforeCheck;
         this.addModifiersToVault(vault, newlyCompletedBingos);
         this.playBingoCompletionNotification(world, taskSource, bingoItems.lastCompletedByPlayerName);
      }
   }

   private void addModifiersToVault(Vault vault, int newlyCompletedBingos) {
      RandomSource random = JavaRandom.ofNanoTime();

      for (int i = 0; i < newlyCompletedBingos; i++) {
         this.addModifier(vault, random);
      }
   }

   public void addModifier(Vault vault, RandomSource random) {
      List<VaultModifier<?>> modifiers = new ArrayList<>(
         ModConfigs.VAULT_MODIFIER_POOLS.getRandom(this.get(MODIFIER_POOL), vault.get(Vault.LEVEL).get(), random)
      );
      Object2IntMap<VaultModifier<?>> groups = new Object2IntOpenHashMap();
      modifiers.forEach(modifier -> groups.put(modifier, groups.getOrDefault(modifier, 0) + 1));
      ObjectIterator<Entry<VaultModifier<?>>> it = groups.object2IntEntrySet().iterator();
      TextComponent modifierNames = new TextComponent("");

      while (it.hasNext()) {
         Entry<VaultModifier<?>> entry = (Entry<VaultModifier<?>>)it.next();
         modifierNames.append(((VaultModifier)entry.getKey()).getChatDisplayNameComponent(entry.getIntValue()));
         if (it.hasNext()) {
            modifierNames.append(new TextComponent(", "));
         }
      }

      TextComponent text = new TextComponent("");
      if (!modifiers.isEmpty()) {
         String lastCompletedByPlayerName = this.get(ITEMS).lastCompletedByPlayerName;
         text.append(lastCompletedByPlayerName != null ? lastCompletedByPlayerName : "")
            .append(new TextComponent(" added ").withStyle(ChatFormatting.GRAY))
            .append(modifierNames)
            .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
      }

      groups.forEach((modifier, count) -> vault.get(Vault.MODIFIERS).addModifier(modifier, count, true, random));

      for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
         listener.getPlayer().ifPresent(other -> other.displayClientMessage(text, false));
      }
   }

   private void playBingoCompletionNotification(VirtualWorld world, TaskSource taskSource, String lastCompletedByPlayerName) {
      if (taskSource instanceof EntityTaskSource entityTaskSource) {
         entityTaskSource.getEntities(Player.class)
            .forEach(
               player -> {
                  world.playSound(null, player, SoundEvents.NOTE_BLOCK_CHIME, SoundSource.MASTER, 1.0F, 0.75F + world.getRandom().nextFloat() * 0.25F);
                  player.sendMessage(
                     new TextComponent(lastCompletedByPlayerName).append(new TextComponent(" completed a Bingo!").withStyle(ChatFormatting.GRAY)),
                     Util.NIL_UUID
                  );
               }
            );
      }
   }

   public void addPlayer(UUID playerId) {
      if (this.get(ENTITY_TASK_SOURCE) instanceof EntityTaskSource entityTaskSource) {
         entityTaskSource.add(playerId);
         this.get(ITEMS).adjustToOneMorePlayer(entityTaskSource.getEntities(Player.class).size(), entityTaskSource);
      }
   }

   public void onAttach() {
      this.get(ITEMS).onAttach(this.get(ENTITY_TASK_SOURCE));
   }

   public void onDetach() {
      this.get(ITEMS).onDetach();
   }

   public boolean isFullyCompleted() {
      return this.get(COMPLETED_BINGOS) >= 12;
   }

   public int getCompletedBingos() {
      return this.get(COMPLETED_BINGOS);
   }

   public boolean checkCompletion() {
      if (this.isFullyCompleted()) {
         return false;
      } else {
         long completed = 0L;
         TaskSource taskSource = this.get(ENTITY_TASK_SOURCE);
         completed = this.get(ITEMS).rowValues().stream().filter(row -> checkAndMarkItemsForBingoCompletion(row.values(), taskSource)).count();
         completed += this.get(ITEMS).columnValues().stream().filter(col -> checkAndMarkItemsForBingoCompletion(col.values(), taskSource)).count();
         if (checkAndMarkItemsForBingoCompletion(this.getDiagonalItems(true), taskSource)) {
            completed++;
         }

         if (checkAndMarkItemsForBingoCompletion(this.getDiagonalItems(false), taskSource)) {
            completed++;
         }

         if (completed > this.get(COMPLETED_BINGOS).intValue()) {
            this.set(COMPLETED_BINGOS, Integer.valueOf((int)completed));
            return true;
         } else {
            return false;
         }
      }
   }

   private List<BingoItem> getDiagonalItems(boolean first) {
      List<BingoItem> items = new ArrayList<>();

      for (int i = 0; i < 5; i++) {
         items.add(this.get(ITEMS).getItem(i, first ? i : 4 - i));
      }

      return items;
   }

   private static boolean checkAndMarkItemsForBingoCompletion(Collection<BingoItem> items, TaskSource taskSource) {
      boolean allComplete = false;
      if (allComplete) {
         items.forEach(BingoItem::markPartOfCompletedBingo);
         return true;
      } else {
         return false;
      }
   }

   public static class BingoItems {
      private final Table<Integer, Integer, BingoItem> items = HashBasedTable.create();
      @Nullable
      private String lastCompletedByPlayerName = null;

      public boolean onTick(VirtualWorld world, TaskSource taskSource) {
         AtomicBoolean taskCompleted = new AtomicBoolean(false);
         this.items.values().forEach(item -> {});
         return taskCompleted.get();
      }

      public static BingoCard.BingoItems readBits(BitBuffer buffer, SyncContext context) {
         BingoCard.BingoItems ret = new BingoCard.BingoItems();
         int size = buffer.readInt();

         for (int i = 0; i < size; i++) {
            int row = buffer.readInt();
            int column = buffer.readInt();
            Adapters.BINGO_ITEM.readBits(buffer, context).ifPresent(bingoItem -> ret.items.put(row, column, bingoItem));
         }

         return ret;
      }

      public void writeBits(BitBuffer buffer, SyncContext context) {
         buffer.writeInt(this.items.size());
         this.items.cellSet().forEach(cell -> {
            buffer.writeInt((Integer)cell.getRowKey());
            buffer.writeInt((Integer)cell.getColumnKey());
            Adapters.BINGO_ITEM.writeBits((BingoItem)cell.getValue(), buffer, context);
         });
      }

      public void adjustToOneMorePlayer(int newNumberOfPlayers, EntityTaskSource taskSource) {
         this.items.values().forEach(item -> item.adjustToOneMorePlayer(newNumberOfPlayers, taskSource));
      }

      public void onAttach(TaskSource taskSource) {
      }

      public void onDetach() {
         this.items.values().forEach(BingoItem::onDetach);
      }

      public Collection<Map<Integer, BingoItem>> rowValues() {
         return this.rowMap().values();
      }

      public Map<Integer, Map<Integer, BingoItem>> rowMap() {
         return this.items.rowMap();
      }

      public Collection<Map<Integer, BingoItem>> columnValues() {
         return this.items.columnMap().values();
      }

      public Map<Integer, Map<Integer, BingoItem>> columnMap() {
         return this.items.columnMap();
      }

      public BingoItem getItem(int row, int column) {
         return (BingoItem)this.items.get(row, column);
      }
   }
}
