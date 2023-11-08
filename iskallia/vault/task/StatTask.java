package iskallia.vault.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;

public abstract class StatTask<T> extends ProgressConfiguredTask<Integer, StatTask.Config<T>> {
   public StatTask(StatTask.Config<T> config) {
      super(config, 0, Adapters.INT_SEGMENTED_7, Integer::compare);
   }

   protected abstract Stat<T> resolve(ServerStatsCounter var1);

   @Override
   public void onPopulate(TaskSource source) {
      this.targetCount = this.getConfig().count.get(source.getRandom());
   }

   @Override
   public void onTick(TaskSource source) {
      super.onTick(source);
      if (source instanceof EntityTaskSource entitySource) {
         int count = 0;

         for (ServerPlayer player : entitySource.getEntities((Class<T>)ServerPlayer.class)) {
            Stat<?> stat = this.resolve(player.getStats());
            count += player.getStats().getValue(stat);
         }

         this.currentCount = count;
      }
   }

   public static class Config<T> extends ConfiguredTask.Config {
      public ResourceLocation statType;
      public T value;
      public IntRoll count;
      public ISimpleAdapter<T, ? super Tag, ? super JsonElement> adapter;

      public Config() {
      }

      public Config(ResourceLocation statType, T value, IntRoll count) {
         this.statType = statType;
         this.value = value;
         this.count = count;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.IDENTIFIER.writeBits(this.statType, buffer);
         this.adapter.writeBits(this.value, buffer);
         Adapters.INT_ROLL.writeBits(this.count, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.statType = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
         this.value = this.adapter.readBits(buffer).orElseThrow();
         this.count = Adapters.INT_ROLL.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.IDENTIFIER.writeNbt(this.statType).ifPresent(value -> nbt.put("statType", value));
            this.adapter.writeNbt(this.value).ifPresent(value -> nbt.put("value", value));
            Adapters.INT_ROLL.writeNbt(this.count).ifPresent(value -> nbt.put("count", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.statType = Adapters.IDENTIFIER.readNbt(nbt.get("statType")).orElseThrow();
         this.value = this.adapter.readNbt(nbt.get("value")).orElseThrow();
         this.count = Adapters.INT_ROLL.readNbt(nbt.get("count")).orElse(IntRoll.ofConstant(0));
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.IDENTIFIER.writeJson(this.statType).ifPresent(value -> json.add("statType", value));
            this.adapter.writeJson(this.value).ifPresent(value -> json.add("value", value));
            Adapters.INT_ROLL.writeJson(this.count).ifPresent(value -> json.add("count", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.statType = Adapters.IDENTIFIER.readJson(json.get("statType")).orElseThrow();
         this.value = this.adapter.readJson(json.get("value")).orElseThrow();
         this.count = Adapters.INT_ROLL.readJson(json.get("count")).orElse(IntRoll.ofConstant(0));
      }
   }
}
