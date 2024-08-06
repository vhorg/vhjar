package iskallia.vault.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;

public abstract class StatTask<T> extends ProgressConfiguredTask<Integer, StatTask.Config<T>> {
   public StatTask(StatTask.Config<T> config, ISimpleAdapter<T, ? super Tag, ? super JsonElement> adapter) {
      super(config, TaskCounter.Adapter.INT);
      config.adapter = adapter;
   }

   public StatTask(StatTask.Config<T> config, ISimpleAdapter<T, ? super Tag, ? super JsonElement> adapter, TaskCounter<Integer, ?> counter) {
      super(config, counter, TaskCounter.Adapter.INT);
      config.adapter = adapter;
   }

   protected abstract Stat<T> resolve(ServerStatsCounter var1);

   @Override
   public void onTick(TaskContext context) {
      super.onTick(context);
      if (context.getSource() instanceof EntityTaskSource entitySource) {
         int count = 0;

         for (ServerPlayer player : entitySource.getEntities((Class<T>)ServerPlayer.class)) {
            Stat<?> stat = this.resolve(player.getStats());
            count += player.getStats().getValue(stat);
         }

         this.counter.onSet(count, context);
      }
   }

   public static class Config<T> extends ConfiguredTask.Config {
      public ResourceLocation statType;
      public T value;
      public ISimpleAdapter<T, ? super Tag, ? super JsonElement> adapter;

      public Config() {
      }

      public Config(ResourceLocation statType, T value) {
         this.statType = statType;
         this.value = value;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.IDENTIFIER.writeBits(this.statType, buffer);
         this.adapter.writeBits(this.value, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.statType = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
         this.value = this.adapter.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.IDENTIFIER.writeNbt(this.statType).ifPresent(value -> nbt.put("statType", value));
            this.adapter.writeNbt(this.value).ifPresent(value -> nbt.put("value", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.statType = Adapters.IDENTIFIER.readNbt(nbt.get("statType")).orElseThrow();
         this.value = this.adapter.readNbt(nbt.get("value")).orElseThrow();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.IDENTIFIER.writeJson(this.statType).ifPresent(value -> json.add("statType", value));
            this.adapter.writeJson(this.value).ifPresent(value -> json.add("value", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.statType = Adapters.IDENTIFIER.readJson(json.get("statType")).orElseThrow();
         this.value = this.adapter.readJson(json.get("value")).orElseThrow();
      }
   }
}
