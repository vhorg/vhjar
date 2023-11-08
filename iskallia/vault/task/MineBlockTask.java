package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.EventPriority;

public class MineBlockTask extends ProgressConfiguredTask<Integer, MineBlockTask.Config> {
   public MineBlockTask() {
      super(new MineBlockTask.Config(), 0, Adapters.INT_SEGMENTED_7, Integer::compare);
   }

   public MineBlockTask(MineBlockTask.Config config) {
      super(config, 0, Adapters.INT_SEGMENTED_7, Integer::compare);
   }

   @Override
   public void onPopulate(TaskSource source) {
      this.targetCount = this.getConfig().count.get(source.getRandom());
   }

   @Override
   public void onAttach(TaskSource source) {
      CommonEvents.PLAYER_MINE
         .register(
            this,
            EventPriority.LOW,
            data -> {
               if (!data.getPlayer().getLevel().isClientSide()) {
                  if (source instanceof EntityTaskSource entitySource) {
                     if (entitySource.matches(data.getPlayer())) {
                        PartialTile tile = PartialTile.of(
                           PartialBlockState.of(data.getState()), PartialCompoundNbt.of(data.getWorld().getBlockEntity(data.getPos()))
                        );
                        if (this.getConfig().filter.test(tile)) {
                           Integer var5 = this.currentCount;
                           this.currentCount = this.currentCount + 1;
                        }
                     }
                  }
               }
            }
         );
      super.onAttach(source);
   }

   @Override
   public void onDetach() {
      CommonEvents.PLAYER_MINE.release(this);
      super.onDetach();
   }

   public static class Config extends ConfiguredTask.Config {
      public TilePredicate filter;
      public IntRoll count;

      public Config() {
      }

      public Config(TilePredicate filter, IntRoll count) {
         this.filter = filter;
         this.count = count;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.TILE_PREDICATE.writeBits(this.filter, buffer);
         Adapters.INT_ROLL.writeBits(this.count, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.filter = Adapters.TILE_PREDICATE.readBits(buffer).orElseThrow();
         this.count = Adapters.INT_ROLL.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.TILE_PREDICATE.writeNbt(this.filter).ifPresent(value -> nbt.put("filter", value));
            Adapters.INT_ROLL.writeNbt(this.count).ifPresent(value -> nbt.put("count", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.filter = Adapters.TILE_PREDICATE.readNbt(nbt.get("filter")).orElse(TilePredicate.FALSE);
         this.count = Adapters.INT_ROLL.readNbt(nbt.get("count")).orElse(IntRoll.ofConstant(0));
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.TILE_PREDICATE.writeJson(this.filter).ifPresent(value -> json.add("filter", value));
            Adapters.INT_ROLL.writeJson(this.count).ifPresent(value -> json.add("count", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.filter = Adapters.TILE_PREDICATE.readJson(json.get("filter")).orElse(TilePredicate.FALSE);
         this.count = Adapters.INT_ROLL.readJson(json.get("count")).orElse(IntRoll.ofConstant(0));
      }
   }
}
