package iskallia.vault.block.entity.challenge.raid.action;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class PoolChallengeAction extends ChallengeAction<PoolChallengeAction.Config> {
   protected PoolChallengeAction() {
      super(new PoolChallengeAction.Config());
   }

   protected PoolChallengeAction(PoolChallengeAction.Config config) {
      super(config);
   }

   @Override
   public boolean onMerge(ChallengeAction<?> action) {
      return false;
   }

   @Override
   public Component getText() {
      return new TextComponent("POOL");
   }

   @Override
   public Stream<ChallengeAction<?>> flatten(RandomSource random) {
      return this.getConfig().pool.getRandom(random).map(action -> {
         action = action.copy();
         action.onPopulate(random);
         return action.flatten(random);
      }).orElseGet(Stream::empty);
   }

   public static class Config extends ChallengeAction.Config {
      public static final ArrayAdapter<ChallengeAction<?>> CHILDREN = Adapters.ofArray(ChallengeAction[]::new, Adapters.RAID_ACTION);
      private final WeightedList<ChallengeAction<?>> pool = new WeightedList<>();

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.pool.size()), buffer);
         this.pool.forEach((action, weight) -> {
            Adapters.RAID_ACTION.writeBits((ChallengeAction<?>)action, buffer);
            Adapters.DOUBLE.writeBits(weight, buffer);
         });
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
         this.pool.clear();

         for (int i = 0; i < size; i++) {
            this.pool.add(Adapters.RAID_ACTION.readBits(buffer).orElseThrow(), Adapters.DOUBLE.readBits(buffer).orElseThrow());
         }
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            ListTag list = new ListTag();
            this.pool.forEach((action, weight) -> Adapters.RAID_ACTION.writeNbt((ChallengeAction<?>)action).map(tag -> {
               if (tag instanceof CompoundTag compound) {
                  Adapters.DOUBLE.writeNbt(weight).ifPresent(t -> compound.put("weight", t));
                  return compound;
               } else {
                  CompoundTag entry = new CompoundTag();
                  entry.put("value", tag);
                  Adapters.DOUBLE.writeNbt(weight).ifPresent(t -> entry.put("weight", t));
                  return entry;
               }
            }).ifPresent(list::add));
            nbt.put("pool", list);
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.pool.clear();
         if (nbt.get("pool") instanceof ListTag list) {
            for (int i = 0; i < list.size(); i++) {
               CompoundTag entry = list.getCompound(i);
               if (!entry.contains("type") && entry.contains("value") && entry.contains("weight")) {
                  this.pool.put(Adapters.RAID_ACTION.readNbt(entry.get("value")).orElseThrow(), Adapters.DOUBLE.readNbt(entry.get("weight")).orElseThrow());
               } else {
                  this.pool.put(Adapters.RAID_ACTION.readNbt(entry).orElseThrow(), Adapters.DOUBLE.readNbt(entry.get("weight")).orElseThrow());
               }
            }
         } else {
            for (String s : nbt.getAllKeys()) {
               if (!s.equals("type") && !s.equals("weight")) {
                  Adapters.DOUBLE
                     .readNbt(nbt.get(s))
                     .ifPresent(weight -> this.pool.put(new ReferenceChallengeAction(new ReferenceChallengeAction.Config(s)), weight));
               }
            }
         }
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return Optional.of(new JsonObject()).map(json -> {
            JsonArray list = new JsonArray();
            this.pool.forEach((action, weight) -> Adapters.RAID_ACTION.writeJson((ChallengeAction<?>)action).map(tag -> {
               if (tag instanceof JsonObject object) {
                  Adapters.DOUBLE.writeJson(weight).ifPresent(t -> object.add("weight", t));
                  return object;
               } else {
                  JsonObject entry = new JsonObject();
                  entry.add("value", tag);
                  Adapters.DOUBLE.writeJson(weight).ifPresent(t -> entry.add("weight", t));
                  return entry;
               }
            }).ifPresent(list::add));
            json.add("pool", list);
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.pool.clear();
         if (json.get("pool") instanceof JsonArray list) {
            for (int i = 0; i < list.size(); i++) {
               JsonObject entry = list.get(i).getAsJsonObject();
               if (!entry.has("type") && entry.has("value") && entry.has("weight")) {
                  this.pool.put(Adapters.RAID_ACTION.readJson(entry.get("value")).orElseThrow(), Adapters.DOUBLE.readJson(entry.get("weight")).orElseThrow());
               } else {
                  this.pool.put(Adapters.RAID_ACTION.readJson(entry).orElseThrow(), Adapters.DOUBLE.readJson(entry.get("weight")).orElseThrow());
               }
            }
         } else {
            for (String s : json.keySet()) {
               if (!s.equals("type") && !s.equals("weight")) {
                  Adapters.DOUBLE
                     .readJson(json.get(s))
                     .ifPresent(roll -> this.pool.put(new ReferenceChallengeAction(new ReferenceChallengeAction.Config(s)), roll));
               }
            }
         }
      }
   }
}
