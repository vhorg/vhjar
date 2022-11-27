package iskallia.vault.world.data;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.NetcodeUtils;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.legacy.raid.ArenaRaid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;

public class StreamData extends SavedData {
   protected static final String DATA_NAME = "the_vault_StreamSubs";
   private Map<UUID, StreamData.Subscribers> subBufferMap = new HashMap<>();
   private Map<UUID, StreamData.Subscribers> subMap = new HashMap<>();
   private Map<UUID, StreamData.Donations> donoMap = new HashMap<>();

   public StreamData.Subscribers getSubscribers(UUID streamer) {
      return this.subMap.computeIfAbsent(streamer, uuid -> new StreamData.Subscribers());
   }

   public StreamData.Donations getDonations(UUID streamer) {
      return this.donoMap.computeIfAbsent(streamer, uuid -> new StreamData.Donations());
   }

   public StreamData reset(MinecraftServer server, UUID streamer) {
      this.subMap.put(streamer, new StreamData.Subscribers());
      this.subBufferMap.put(streamer, new StreamData.Subscribers());
      this.donoMap.put(streamer, new StreamData.Donations());
      this.setDirty();
      return this;
   }

   public StreamData resetDonos(MinecraftServer server, UUID streamer) {
      this.donoMap.put(streamer, new StreamData.Donations());
      this.setDirty();
      return this;
   }

   public StreamData onSub(MinecraftServer server, UUID streamer, String name, int months) {
      NetcodeUtils.runIfPresent(server, streamer, player -> {
         ArenaRaid activeRaid = ArenaRaidData.get(player.getLevel()).getActiveFor(player);
         if (activeRaid != null) {
            StreamData.Subscribers subscribers = this.subBufferMap.computeIfAbsent(streamer, uuid -> new StreamData.Subscribers());
            subscribers.onSub(name, months);
         } else {
            StreamData.Subscribers subscribers = this.subMap.computeIfAbsent(streamer, uuid -> new StreamData.Subscribers());
            subscribers.onSub(name, months);
            int maxSubs = ModConfigs.STREAMER_MULTIPLIERS.ofStreamer(player.getDisplayName().getString()).subsNeededForArena;
            int multiplier = ModConfigs.STREAMER_MULTIPLIERS.ofStreamer(player.getDisplayName().getString()).subsMultiplier;
            if (subscribers.count() >= maxSubs) {
               ArenaRaid raid = ArenaRaidData.get(player.getLevel()).startNew(player);

               for (int i = 0; i < maxSubs; i++) {
                  StreamData.Subscribers.Instance sub = subscribers.popOneSub();

                  for (int j = 0; j < multiplier; j++) {
                     raid.spawner.subscribers.add(sub);
                  }
               }
            }
         }

         this.setDirty();
      });
      return this;
   }

   public StreamData onDono(MinecraftServer server, UUID streamer, String donator, int amount) {
      this.getDonations(streamer).onDono(donator, amount);
      this.setDirty();
      return this;
   }

   public StreamData onArenaLeave(MinecraftServer server, UUID streamer) {
      NetcodeUtils.runIfPresent(server, streamer, player -> {
         StreamData.Subscribers bufferedSubs = this.subBufferMap.computeIfAbsent(streamer, uuid -> new StreamData.Subscribers());
         int maxSubs = ModConfigs.STREAMER_MULTIPLIERS.ofStreamer(player.getDisplayName().getString()).subsNeededForArena;
         int subsToMove = Math.min(bufferedSubs.count(), maxSubs);

         for (int i = 0; i < subsToMove; i++) {
            StreamData.Subscribers.Instance e = bufferedSubs.popOneSub();
            this.onSub(server, streamer, e.name, e.months);
         }

         this.setDirty();
      });
      return this;
   }

   private static StreamData create(CompoundTag tag) {
      StreamData data = new StreamData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      this.subMap = NBTHelper.readMap(nbt, "StreamSubs", ListTag.class, list -> {
         StreamData.Subscribers subs = new StreamData.Subscribers();
         subs.deserializeNBT(list);
         return subs;
      });
      this.subBufferMap = NBTHelper.readMap(nbt, "StreamSubsBuffer", ListTag.class, list -> {
         StreamData.Subscribers subs = new StreamData.Subscribers();
         subs.deserializeNBT(list);
         return subs;
      });
      this.donoMap = NBTHelper.readMap(nbt, "StreamDonos", CompoundTag.class, list -> {
         StreamData.Donations donos = new StreamData.Donations();
         donos.deserializeNBT(list);
         return donos;
      });
   }

   public CompoundTag save(CompoundTag nbt) {
      NBTHelper.writeMap(nbt, "StreamSubsBuffer", this.subBufferMap, ListTag.class, StreamData.Subscribers::serializeNBT);
      NBTHelper.writeMap(nbt, "StreamSubs", this.subMap, ListTag.class, StreamData.Subscribers::serializeNBT);
      NBTHelper.writeMap(nbt, "StreamDonos", this.donoMap, CompoundTag.class, StreamData.Donations::serializeNBT);
      return nbt;
   }

   public static StreamData get(ServerLevel world) {
      return (StreamData)world.getServer().overworld().getDataStorage().computeIfAbsent(StreamData::create, StreamData::new, "the_vault_StreamSubs");
   }

   public static class Donations implements INBTSerializable<CompoundTag> {
      private final Map<String, Integer> donoMap = new HashMap<>();

      public StreamData.Donations onDono(String name, int amount) {
         this.donoMap.put(name, this.donoMap.getOrDefault(name, 0) + amount);
         return this;
      }

      public WeightedList<String> toWeightedList() {
         WeightedList<String> list = new WeightedList<>();

         for (Entry<String, Integer> entry : this.donoMap.entrySet()) {
            list.add(entry.getKey(), entry.getValue());
         }

         return list;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         ListTag donators = new ListTag();
         ListTag amounts = new ListTag();
         this.donoMap.forEach((donator, amount) -> {
            donators.add(StringTag.valueOf(donator));
            amounts.add(IntTag.valueOf(amount));
         });
         nbt.put("Donators", donators);
         nbt.put("Amounts", amounts);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         ListTag donators = nbt.getList("Donators", 8);
         ListTag amounts = nbt.getList("Amounts", 3);
         if (donators.size() != amounts.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
         } else {
            for (int i = 0; i < donators.size(); i++) {
               this.donoMap.put(donators.getString(i), amounts.getInt(i));
            }
         }
      }
   }

   public static class Subscribers implements INBTSerializable<ListTag> {
      private final List<StreamData.Subscribers.Instance> subs = new ArrayList<>();

      public void onSub(String name, int months) {
         this.subs.add(new StreamData.Subscribers.Instance(name, months));
      }

      public void onSub(CompoundTag nbt) {
         StreamData.Subscribers.Instance sub = new StreamData.Subscribers.Instance();
         sub.deserializeNBT(nbt);
         this.subs.add(sub);
      }

      public int count() {
         return this.subs.size();
      }

      public StreamData.Subscribers.Instance popOneSub() {
         return this.subs.isEmpty() ? null : this.subs.remove(0);
      }

      public StreamData.Subscribers.Instance getRandom(Random random) {
         return this.subs.isEmpty() ? null : this.subs.get(random.nextInt(this.subs.size()));
      }

      public StreamData.Subscribers merge(StreamData.Subscribers other) {
         StreamData.Subscribers merged = new StreamData.Subscribers();
         merged.subs.addAll(this.subs);
         merged.subs.addAll(other.subs);
         return merged;
      }

      public ListTag serializeNBT() {
         return this.subs.stream().map(StreamData.Subscribers.Instance::serializeNBT).collect(Collectors.toCollection(ListTag::new));
      }

      public void deserializeNBT(ListTag nbt) {
         this.subs.clear();
         IntStream.range(0, nbt.size()).<CompoundTag>mapToObj(nbt::getCompound).forEach(this::onSub);
      }

      public static class Instance implements INBTSerializable<CompoundTag> {
         private String name = "";
         private int months = 0;

         public Instance() {
         }

         public Instance(String name, int months) {
            this.name = name;
            this.months = months;
         }

         public String getName() {
            return this.name;
         }

         public int getMonths() {
            return this.months;
         }

         public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("Name", this.name);
            nbt.putInt("Months", this.months);
            return nbt;
         }

         public void deserializeNBT(CompoundTag nbt) {
            this.name = nbt.getString("Name");
            this.months = nbt.getInt("Months");
         }
      }
   }
}
