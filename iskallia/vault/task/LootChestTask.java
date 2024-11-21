package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class LootChestTask extends ProgressConfiguredTask<Integer, LootChestTask.Config> {
   public LootChestTask() {
      super(new LootChestTask.Config(), TaskCounter.Adapter.INT);
   }

   public LootChestTask(LootChestTask.Config config, TaskCounter<Integer, ?> counter) {
      super(config, counter, TaskCounter.Adapter.INT);
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.CHEST_LOOT_GENERATION.post().register(this, data -> {
         if (this.parent == null || this.parent.hasActiveChildren()) {
            if (!data.getPlayer().getLevel().isClientSide()) {
               if (context.getSource() instanceof EntityTaskSource entitySource) {
                  if (entitySource.matches(data.getPlayer())) {
                     PartialTile tile = PartialTile.of(PartialBlockState.of(data.getState()), PartialCompoundNbt.of(data.getTileEntity()));
                     if (this.getConfig().filter.test(tile)) {
                        this.counter.onAdd(1, context);
                     }
                  }
               }
            }
         }
      });
      super.onAttach(context);
   }

   public static class Config extends ConfiguredTask.Config {
      public TilePredicate filter;

      public Config() {
      }

      public Config(TilePredicate filter) {
         this.filter = filter;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.TILE_PREDICATE.writeBits(this.filter, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.filter = Adapters.TILE_PREDICATE.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.TILE_PREDICATE.writeNbt(this.filter).ifPresent(value -> nbt.put("filter", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.filter = Adapters.TILE_PREDICATE.readNbt(nbt.get("filter")).orElse(TilePredicate.FALSE);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.TILE_PREDICATE.writeJson(this.filter).ifPresent(value -> json.add("filter", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.filter = Adapters.TILE_PREDICATE.readJson(json.get("filter")).orElse(TilePredicate.FALSE);
      }
   }
}
