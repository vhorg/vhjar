package iskallia.vault.block.entity.challenge.raid.action;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.roll.IntRoll;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class GroupChallengeAction extends ChallengeAction<GroupChallengeAction.Config> {
   private final List<Integer> rolls = new ArrayList<>();

   protected GroupChallengeAction() {
      super(new GroupChallengeAction.Config());
   }

   protected GroupChallengeAction(GroupChallengeAction.Config config) {
      super(config);
   }

   @Override
   public boolean onMerge(ChallengeAction<?> action) {
      return false;
   }

   @Override
   public boolean onPopulate(RandomSource random) {
      if (!super.onPopulate(random)) {
         this.rolls.clear();
         this.getConfig().children.forEach((action, roll) -> this.rolls.add(roll.get(random)));
         return false;
      } else {
         return true;
      }
   }

   @Override
   public Component getText() {
      return new TextComponent("GROUP");
   }

   @Override
   public Stream<ChallengeAction<?>> flatten(RandomSource random) {
      List<ChallengeAction<?>> actions = new ArrayList<>();
      int index = 0;

      for (ChallengeAction<?> action : this.getConfig().children.keySet()) {
         for (int i = 0; i < this.rolls.get(index); i++) {
            ChallengeAction<?> copy = action.copy();
            copy.onPopulate(random);
            actions.add(copy);
         }

         index++;
      }

      return actions.stream().flatMap(actionx -> actionx.flatten(random));
   }

   public static class Config extends ChallengeAction.Config {
      private final Map<ChallengeAction<?>, IntRoll> children = new LinkedHashMap<>();

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.children.size()), buffer);
         this.children.forEach((action, roll) -> {
            Adapters.RAID_ACTION.writeBits((ChallengeAction<?>)action, buffer);
            Adapters.INT_ROLL.writeBits(roll, buffer);
         });
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

         for (int i = 0; i < size; i++) {
            this.children.put(Adapters.RAID_ACTION.readBits(buffer).orElseThrow(), Adapters.INT_ROLL.readBits(buffer).orElseThrow());
         }
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            ListTag children = new ListTag();
            this.children.forEach((action, roll) -> Adapters.RAID_ACTION.writeNbt((ChallengeAction<?>)action).ifPresent(tag1 -> {
               if (tag1 instanceof CompoundTag entry) {
                  Adapters.INT_ROLL.writeNbt(roll).ifPresent(tag2 -> entry.put("roll", tag2));
                  children.add(entry);
               } else {
                  CompoundTag entry = new CompoundTag();
                  entry.put("value", tag1);
                  Adapters.INT_ROLL.writeNbt(roll).ifPresent(tag2 -> entry.put("roll", tag2));
                  children.add(entry);
               }
            }));
            nbt.put("children", children);
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.children.clear();
         if (nbt.get("children") instanceof ListTag children) {
            for (int i = 0; i < children.size(); i++) {
               CompoundTag entry = children.getCompound(i);
               if (entry.contains("value") && !entry.contains("type")) {
                  this.children.put(Adapters.RAID_ACTION.readNbt(entry.get("value")).orElseThrow(), Adapters.INT_ROLL.readNbt(entry.get("roll")).orElseThrow());
               } else {
                  this.children.put(Adapters.RAID_ACTION.readNbt(entry).orElseThrow(), Adapters.INT_ROLL.readNbt(entry.get("roll")).orElseThrow());
               }
            }
         } else {
            for (String s : nbt.getAllKeys()) {
               if (!s.equals("type") && !s.equals("weight")) {
                  Adapters.INT_ROLL
                     .readNbt(nbt.get(s))
                     .ifPresent(roll -> this.children.put(new ReferenceChallengeAction(new ReferenceChallengeAction.Config(s)), roll));
               }
            }
         }
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            JsonArray children = new JsonArray();
            this.children.forEach((action, roll) -> Adapters.RAID_ACTION.writeJson((ChallengeAction<?>)action).ifPresent(tag1 -> {
               if (tag1 instanceof JsonObject entry) {
                  Adapters.INT_ROLL.writeJson(roll).ifPresent(tag2 -> entry.add("roll", tag2));
                  children.add(entry);
               } else {
                  JsonObject entry = new JsonObject();
                  entry.add("value", tag1);
                  Adapters.INT_ROLL.writeJson(roll).ifPresent(tag2 -> entry.add("roll", tag2));
                  children.add(entry);
               }
            }));
            json.add("children", children);
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.children.clear();
         if (json.get("children") instanceof JsonArray children) {
            for (int i = 0; i < children.size(); i++) {
               JsonObject entry = children.get(i).getAsJsonObject();
               if (entry.has("value") && !entry.has("type")) {
                  this.children
                     .put(Adapters.RAID_ACTION.readJson(entry.get("value")).orElseThrow(), Adapters.INT_ROLL.readJson(entry.get("roll")).orElseThrow());
               } else {
                  this.children.put(Adapters.RAID_ACTION.readJson(entry).orElseThrow(), Adapters.INT_ROLL.readJson(entry.get("roll")).orElseThrow());
               }
            }
         } else {
            for (String s : json.keySet()) {
               if (!s.equals("type") && !s.equals("weight")) {
                  Adapters.INT_ROLL
                     .readJson(json.get(s))
                     .ifPresent(roll -> this.children.put(new ReferenceChallengeAction(new ReferenceChallengeAction.Config(s)), roll));
               }
            }
         }
      }
   }
}
