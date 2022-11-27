package iskallia.vault.core.vault.stat;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.VaultSnapshots;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2FloatMap.Entry;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class StatTotals implements INBTSerializable<CompoundTag> {
   private int bailed;
   private int failed;
   private int completed;
   private int experience;
   private final Object2FloatMap<ResourceLocation> damageReceived = new Object2FloatOpenHashMap();
   private final Object2FloatMap<ResourceLocation> damageDealt = new Object2FloatOpenHashMap();
   private final Object2IntMap<ResourceLocation> entitiesKilled = new Object2IntOpenHashMap();
   private int treasureRoomsOpened;
   private final Object2IntMap<ResourceLocation> minedBlocks = new Object2IntOpenHashMap();
   private final Object2IntMap<VaultChestType> trappedChests = new Object2IntOpenHashMap();
   private final Object2IntMap<StatTotals.ChestKey> lootedChests = new Object2IntOpenHashMap();
   private int crystalsCrafted;
   private static final String KEY_KEYS = "keys";
   private static final String KEY_VALUES = "values";
   private static final String KEY_BAILED = "survived";
   private static final String KEY_FAILED = "failed";
   private static final String KEY_COMPLETED = "completed";
   private static final String KEY_EXPERIENCE = "experience";
   private static final String KEY_DAMAGE_RECEIVED = "damageReceived";
   private static final String KEY_DAMAGE_DEALT = "damageDealt";
   private static final String KEY_ENTITIES_KILLED = "entitiesKilled";
   private static final String KEY_TREASURE_ROOMS_OPENED = "treasureRoomsOpened";
   private static final String KEY_MINED_BLOCKS = "minedBlocks";
   private static final String KEY_TRAPPED_CHESTS = "trappedChests";
   private static final String KEY_LOOTED_CHESTS = "lootedChests";
   private static final String KEY_CRYSTALS_CRAFTED = "crystalsCrafted";

   public static StatTotals of(UUID playerUuid) {
      List<Vault> vaults = VaultSnapshots.getAll().stream().map(VaultSnapshot::getEnd).filter(Objects::nonNull).toList();
      StatTotals statTotals = new StatTotals();

      for (Vault vault : vaults) {
         StatsCollector statsCollector = vault.get(Vault.STATS);
         if (statsCollector != null) {
            StatCollector collector = statsCollector.get(playerUuid);
            if (collector != null) {
               switch (collector.getCompletion()) {
                  case BAILED:
                     statTotals.bailed++;
                     break;
                  case FAILED:
                     statTotals.failed++;
                     break;
                  case COMPLETED:
                     statTotals.completed++;
               }

               statTotals.experience = statTotals.experience + collector.getExperience(vault);
               ObjectIterator chestStats = collector.getDamageReceived().object2FloatEntrySet().iterator();

               while (chestStats.hasNext()) {
                  Entry<ResourceLocation> entry = (Entry<ResourceLocation>)chestStats.next();
                  statTotals.damageReceived
                     .computeFloat((ResourceLocation)entry.getKey(), (resourceLocation, value) -> (value == null ? 0.0F : value) + entry.getFloatValue());
               }

               chestStats = collector.getDamageDealt().object2FloatEntrySet().iterator();

               while (chestStats.hasNext()) {
                  Entry<ResourceLocation> entry = (Entry<ResourceLocation>)chestStats.next();
                  statTotals.damageDealt
                     .computeFloat((ResourceLocation)entry.getKey(), (resourceLocation, value) -> (value == null ? 0.0F : value) + entry.getFloatValue());
               }

               chestStats = collector.getEntitiesKilled().object2IntEntrySet().iterator();

               while (chestStats.hasNext()) {
                  it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<ResourceLocation> entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<ResourceLocation>)chestStats.next();
                  statTotals.entitiesKilled
                     .computeInt((ResourceLocation)entry.getKey(), (resourceLocation, value) -> (value == null ? 0 : value) + entry.getIntValue());
               }

               statTotals.treasureRoomsOpened = statTotals.treasureRoomsOpened + collector.getTreasureRoomsOpened();
               chestStats = collector.getMinedBlocks().object2IntEntrySet().iterator();

               while (chestStats.hasNext()) {
                  it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<ResourceLocation> entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<ResourceLocation>)chestStats.next();
                  statTotals.minedBlocks
                     .computeInt((ResourceLocation)entry.getKey(), (resourceLocation, value) -> (value == null ? 0 : value) + entry.getIntValue());
               }

               for (ChestStat stat : collector.get(StatCollector.CHESTS)) {
                  if (stat.has(ChestStat.TRAPPED)) {
                     statTotals.trappedChests.computeInt(stat.get(ChestStat.TYPE), (resourceLocation, value) -> (value == null ? 0 : value) + 1);
                  } else {
                     statTotals.lootedChests
                        .computeInt(
                           new StatTotals.ChestKey(stat.get(ChestStat.TYPE), stat.get(ChestStat.RARITY)),
                           (resourceLocation, value) -> (value == null ? 0 : value) + 1
                        );
                  }
               }
            }
         }
      }

      PlayerStatsData.Stats playerStatsData = PlayerStatsData.get().get(playerUuid);
      statTotals.crystalsCrafted = playerStatsData.getCrystals().size();
      return statTotals;
   }

   public int getBailed() {
      return this.bailed;
   }

   public int getFailed() {
      return this.failed;
   }

   public int getCompleted() {
      return this.completed;
   }

   public int getExperience() {
      return this.experience;
   }

   public Map<ResourceLocation, Float> getDamageReceived() {
      return Collections.unmodifiableMap(this.damageReceived);
   }

   public Map<ResourceLocation, Float> getDamageDealt() {
      return Collections.unmodifiableMap(this.damageDealt);
   }

   public Map<ResourceLocation, Integer> getEntitiesKilled() {
      return Collections.unmodifiableMap(this.entitiesKilled);
   }

   public int getTreasureRoomsOpened() {
      return this.treasureRoomsOpened;
   }

   public Map<ResourceLocation, Integer> getMinedBlocks() {
      return Collections.unmodifiableMap(this.minedBlocks);
   }

   public Map<VaultChestType, Integer> getTrappedChests() {
      return Collections.unmodifiableMap(this.trappedChests);
   }

   public Map<StatTotals.ChestKey, Integer> getLootedChests() {
      return Collections.unmodifiableMap(this.lootedChests);
   }

   public int getCrystalsCrafted() {
      return this.crystalsCrafted;
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("survived", this.bailed);
      tag.putInt("failed", this.failed);
      tag.putInt("completed", this.completed);
      tag.putInt("experience", this.experience);
      tag.put("damageReceived", this.serializeResourceLocationMap(this.damageReceived));
      tag.put("damageDealt", this.serializeResourceLocationMap(this.damageDealt));
      tag.put("entitiesKilled", this.serializeResourceLocationMap(this.entitiesKilled));
      tag.putInt("treasureRoomsOpened", this.treasureRoomsOpened);
      tag.put("minedBlocks", this.serializeResourceLocationMap(this.minedBlocks));
      tag.put("trappedChests", this.serializeEnumMap(this.trappedChests));
      tag.put("lootedChests", this.serializeChestKeyMap(this.lootedChests));
      tag.putInt("crystalsCrafted", this.crystalsCrafted);
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.bailed = tag.getInt("survived");
      this.failed = tag.getInt("failed");
      this.completed = tag.getInt("completed");
      this.experience = tag.getInt("experience");
      this.deserializeResourceLocationMap(tag.getCompound("damageReceived"), this.damageReceived);
      this.deserializeResourceLocationMap(tag.getCompound("damageDealt"), this.damageDealt);
      this.deserializeResourceLocationMap(tag.getCompound("entitiesKilled"), this.entitiesKilled);
      this.treasureRoomsOpened = tag.getInt("treasureRoomsOpened");
      this.deserializeResourceLocationMap(tag.getCompound("minedBlocks"), this.minedBlocks);
      this.deserializeEnumMap(tag.getCompound("trappedChests"), ordinal -> VaultChestType.values()[ordinal], this.trappedChests);
      this.deserializeChestKeyMap(tag.getCompound("lootedChests"), this.lootedChests);
      this.crystalsCrafted = tag.getInt("crystalsCrafted");
   }

   private <T extends Enum<T>> CompoundTag serializeEnumMap(Object2IntMap<T> map) {
      return this.serializeMap((keys, values) -> {
         ObjectIterator var3 = map.object2IntEntrySet().iterator();

         while (var3.hasNext()) {
            it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<T> entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<T>)var3.next();
            keys.add(IntTag.valueOf(((Enum)entry.getKey()).ordinal()));
            values.add(IntTag.valueOf(entry.getIntValue()));
         }
      });
   }

   private CompoundTag serializeChestKeyMap(Object2IntMap<StatTotals.ChestKey> map) {
      return this.serializeMap(
         (keys, values) -> {
            ObjectIterator var3 = map.object2IntEntrySet().iterator();

            while (var3.hasNext()) {
               it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<StatTotals.ChestKey> entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<StatTotals.ChestKey>)var3.next();
               keys.add(((StatTotals.ChestKey)entry.getKey()).serializeNBT());
               values.add(IntTag.valueOf(entry.getIntValue()));
            }
         }
      );
   }

   private CompoundTag serializeResourceLocationMap(Object2FloatMap<ResourceLocation> map) {
      return this.serializeMap((keys, values) -> {
         ObjectIterator var3 = map.object2FloatEntrySet().iterator();

         while (var3.hasNext()) {
            Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var3.next();
            keys.add(StringTag.valueOf(((ResourceLocation)entry.getKey()).toString()));
            values.add(FloatTag.valueOf(entry.getFloatValue()));
         }
      });
   }

   private CompoundTag serializeResourceLocationMap(Object2IntMap<ResourceLocation> map) {
      return this.serializeMap(
         (keys, values) -> {
            ObjectIterator var3 = map.object2IntEntrySet().iterator();

            while (var3.hasNext()) {
               it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<ResourceLocation> entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<ResourceLocation>)var3.next();
               keys.add(StringTag.valueOf(((ResourceLocation)entry.getKey()).toString()));
               values.add(IntTag.valueOf(entry.getIntValue()));
            }
         }
      );
   }

   private CompoundTag serializeMap(BiConsumer<ListTag, ListTag> consumer) {
      ListTag keyList = new ListTag();
      ListTag valueList = new ListTag();
      consumer.accept(keyList, valueList);
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.put("keys", keyList);
      compoundTag.put("values", valueList);
      return compoundTag;
   }

   private <T extends Enum<T>> void deserializeEnumMap(CompoundTag tag, Function<Integer, T> keyFunction, Object2IntMap<T> map) {
      this.deserializeMap(tag, (byte)3, (byte)3, (keys, values) -> {
         for (int i = 0; i < keys.size(); i++) {
            map.put(keyFunction.apply(keys.getInt(i)), values.getInt(i));
         }
      });
   }

   private void deserializeChestKeyMap(CompoundTag tag, Object2IntMap<StatTotals.ChestKey> map) {
      this.deserializeMap(tag, (byte)2, (byte)3, (keys, values) -> {
         for (int i = 0; i < keys.size(); i++) {
            map.put(StatTotals.ChestKey.decode(keys.getShort(i)), values.getInt(i));
         }
      });
   }

   private void deserializeResourceLocationMap(CompoundTag tag, Object2FloatMap<ResourceLocation> map) {
      this.deserializeMap(tag, (byte)8, (byte)5, (keys, values) -> {
         for (int i = 0; i < keys.size(); i++) {
            map.put(ResourceLocation.tryParse(keys.getString(i)), values.getFloat(i));
         }
      });
   }

   private void deserializeResourceLocationMap(CompoundTag tag, Object2IntMap<ResourceLocation> map) {
      this.deserializeMap(tag, (byte)8, (byte)3, (keys, values) -> {
         for (int i = 0; i < keys.size(); i++) {
            map.put(ResourceLocation.tryParse(keys.getString(i)), values.getInt(i));
         }
      });
   }

   private void deserializeMap(CompoundTag tag, byte keyType, byte valueType, BiConsumer<ListTag, ListTag> consumer) {
      ListTag keyList = tag.getList("keys", keyType);
      ListTag valueList = tag.getList("values", valueType);
      if (keyList.size() != valueList.size()) {
         throw new IllegalStateException("Key / value size mismatch");
      } else {
         for (int i = 0; i < keyList.size(); i++) {
            consumer.accept(keyList, valueList);
         }
      }
   }

   public record ChestKey(VaultChestType type, VaultRarity rarity) {
      public ShortTag serializeNBT() {
         return ShortTag.valueOf((short)(this.type().ordinal() << 8 | this.rarity().ordinal() & 0xFF));
      }

      public static StatTotals.ChestKey decode(short encoded) {
         return new StatTotals.ChestKey(VaultChestType.values()[encoded >> 8], VaultRarity.values()[encoded & 255]);
      }
   }
}
