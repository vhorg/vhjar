package iskallia.vault.block.entity.challenge.raid.action;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import iskallia.vault.block.entity.challenge.ChallengeManager;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public abstract class ChallengeAction<C extends ChallengeAction.Config> implements ISerializable<CompoundTag, JsonObject> {
   private final C config;
   private boolean populated;

   protected ChallengeAction(C config) {
      this.config = config;
   }

   public C getConfig() {
      return this.config;
   }

   public boolean isPopulated() {
      return this.populated;
   }

   public void setPopulated(boolean populated) {
      this.populated = populated;
   }

   public boolean onPopulate(RandomSource random) {
      boolean populated = this.isPopulated();
      this.setPopulated(true);
      return populated;
   }

   public abstract boolean onMerge(ChallengeAction<?> var1);

   public abstract Component getText();

   public void onSummonMob(Entity entity) {
   }

   public void onAddPlayer(Player player) {
   }

   public void onRemovePlayer(Player player) {
   }

   public void onActivate(ServerLevel world, ChallengeManager manager, RandomSource random) {
   }

   public Stream<ChallengeAction<?>> flatten(RandomSource random) {
      return Stream.of(this);
   }

   public <T extends ChallengeAction<C>> T copy() {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      Adapters.RAID_ACTION.writeBits(this, buffer);
      buffer.setPosition(0);
      return (T)Adapters.RAID_ACTION.readBits(buffer).orElseThrow();
   }

   public static List<ChallengeAction<?>> merge(List<ChallengeAction<?>> unmergedList) {
      return merge(new ArrayList<>(), unmergedList);
   }

   public static List<ChallengeAction<?>> merge(List<ChallengeAction<?>> mergedList, List<ChallengeAction<?>> unmergedList) {
      for (ChallengeAction<?> second : unmergedList) {
         boolean merged = false;

         for (ChallengeAction<?> first : mergedList) {
            if (first.onMerge(second)) {
               merged = true;
               break;
            }
         }

         if (!merged) {
            mergedList.add(second);
         }
      }

      return mergedList;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      this.config.writeBits(buffer);
      Adapters.BOOLEAN.writeBits(this.populated, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.config.readBits(buffer);
      this.populated = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         if (!this.populated) {
            CompoundTag other = this.config.writeNbt().orElseThrow();
            other.getAllKeys().forEach(key -> nbt.put(key, Objects.requireNonNull(other.get(key))));
         } else {
            nbt.put("config", (Tag)this.config.writeNbt().orElseThrow());
         }

         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      if (!nbt.contains("config")) {
         this.config.readNbt(nbt);
         this.populated = false;
      } else {
         this.config.readNbt(nbt.getCompound("config"));
         this.populated = true;
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.of(new JsonObject()).map(json -> {
         if (!this.populated) {
            this.config.writeJson().orElseThrow().entrySet().forEach(entry -> json.add((String)entry.getKey(), (JsonElement)entry.getValue()));
         } else {
            json.add("config", (JsonElement)this.config.writeJson().orElseThrow());
         }

         return (JsonObject)json;
      });
   }

   public void readJson(JsonObject json) {
      if (!json.has("config")) {
         this.config.readJson(json);
         this.populated = false;
      } else {
         this.config.readJson(json.getAsJsonObject("config"));
         this.populated = true;
      }
   }

   public static class Adapter extends TypeSupplierAdapter<ChallengeAction<?>> {
      public Adapter() {
         super("type", true);
         this.register("group", GroupChallengeAction.class, GroupChallengeAction::new);
         this.register("pool", PoolChallengeAction.class, PoolChallengeAction::new);
         this.register("reference", ReferenceChallengeAction.class, ReferenceChallengeAction::new);
         this.register("forfeit", ForfeitChallengeAction.class, ForfeitChallengeAction::new);
         this.register("mob_vanilla_attribute", MobVanillaAttributeChallengeAction.class, MobVanillaAttributeChallengeAction::new);
         this.register("player_vanilla_attribute", PlayerVanillaAttributeChallengeAction.class, PlayerVanillaAttributeChallengeAction::new);
         this.register("mob_spawn", AddMobsChallengeAction.class, AddMobsChallengeAction::new);
         this.register("floating_item_reward", FloatingItemRewardChallengeAction.class, FloatingItemRewardChallengeAction::new);
         this.register("tile_reward", TileRewardChallengeAction.class, TileRewardChallengeAction::new);
      }

      protected ChallengeAction<?> readSuppliedNbt(Tag nbt) {
         if (nbt instanceof StringTag tag) {
            return new ReferenceChallengeAction(new ReferenceChallengeAction.Config(tag.getAsString()));
         } else if (nbt instanceof ListTag tag) {
            if (!tag.isEmpty() && tag.get(0) instanceof CompoundTag entry && entry.contains("weight")) {
               CompoundTag action = new CompoundTag();
               action.putString("type", "pool");
               action.put("pool", tag);
               PoolChallengeAction value = new PoolChallengeAction();
               value.readNbt(action);
               return value;
            } else {
               CompoundTag action = new CompoundTag();
               action.putString("type", "group");
               action.put("children", tag);
               GroupChallengeAction value = new GroupChallengeAction();
               value.readNbt(action);
               return value;
            }
         } else {
            return (ChallengeAction<?>)super.readSuppliedNbt(nbt);
         }
      }

      protected ChallengeAction<?> readSuppliedJson(JsonElement json) {
         if (json instanceof JsonPrimitive primitive && primitive.isString()) {
            return new ReferenceChallengeAction(new ReferenceChallengeAction.Config(primitive.getAsString()));
         } else if (json instanceof JsonArray tag) {
            if (!tag.isEmpty() && tag.get(0) instanceof JsonObject entry && entry.has("weight")) {
               JsonObject action = new JsonObject();
               action.addProperty("type", "pool");
               action.add("pool", tag);
               PoolChallengeAction value = new PoolChallengeAction();
               value.readJson(action);
               return value;
            } else {
               JsonObject action = new JsonObject();
               action.addProperty("type", "group");
               action.add("children", tag);
               GroupChallengeAction value = new GroupChallengeAction();
               value.readJson(action);
               return value;
            }
         } else {
            return (ChallengeAction<?>)super.readSuppliedJson(json);
         }
      }
   }

   public static class Config implements ISerializable<CompoundTag, JsonObject> {
      protected int textColor;

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.INT.writeBits(Integer.valueOf(this.textColor), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         this.textColor = Adapters.INT.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Adapters.INT.writeNbt(Integer.valueOf(this.textColor)).ifPresent(tag -> nbt.put("textColor", tag));
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         this.textColor = Adapters.INT.readNbt(nbt.get("textColor")).orElse(16777215);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return Optional.of(new JsonObject()).map(json -> {
            Adapters.INT.writeJson(Integer.valueOf(this.textColor)).ifPresent(tag -> json.add("textColor", tag));
            return (JsonObject)json;
         });
      }

      public void readJson(JsonObject json) {
         this.textColor = Adapters.INT.readJson(json.get("textColor")).orElse(16777215);
      }
   }
}
