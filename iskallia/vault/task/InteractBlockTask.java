package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import iskallia.vault.task.util.IProgressTask;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class InteractBlockTask extends ProgressConfiguredTask<Integer, InteractBlockTask.Config> implements IProgressTask {
   public InteractBlockTask() {
      super(new InteractBlockTask.Config(), 0, Adapters.INT_SEGMENTED_7, Integer::compare);
   }

   public InteractBlockTask(InteractBlockTask.Config config) {
      super(config, 0, Adapters.INT_SEGMENTED_7, Integer::compare);
   }

   @Override
   public void onPopulate(TaskSource source) {
      this.targetCount = this.getConfig().count.get(source.getRandom());
   }

   @Override
   public void onAttach(TaskSource source) {
      CommonEvents.BLOCK_USE.register(this, event -> {
         if (!event.getWorld().isClientSide()) {
            if (source instanceof EntityTaskSource entitySource) {
               if (entitySource.matches(event.getPlayer())) {
                  PartialTile tile = PartialTile.at(event.getWorld(), event.getPos());
                  if (event.getPhase() != BlockUseEvent.Phase.HEAD || this.getConfig().pre.test(tile)) {
                     if (event.getPhase() != BlockUseEvent.Phase.RETURN || this.getConfig().post.test(tile)) {
                        Integer var5 = this.currentCount;
                        this.currentCount = this.currentCount + 1;
                     }
                  }
               }
            }
         }
      });
      super.onAttach(source);
   }

   @Override
   public void onDetach() {
      CommonEvents.BLOCK_USE.release(this);
      super.onDetach();
   }

   public static class Config extends ConfiguredTask.Config {
      public TilePredicate pre;
      public TilePredicate post;
      public IntRoll count;

      public Config() {
      }

      public Config(TilePredicate pre, TilePredicate post, IntRoll count) {
         this.pre = pre;
         this.post = post;
         this.count = count;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.TILE_PREDICATE.writeBits(this.pre, buffer);
         Adapters.TILE_PREDICATE.writeBits(this.post, buffer);
         Adapters.INT_ROLL.writeBits(this.count, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.pre = Adapters.TILE_PREDICATE.readBits(buffer).orElseThrow();
         this.post = Adapters.TILE_PREDICATE.readBits(buffer).orElseThrow();
         this.count = Adapters.INT_ROLL.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.TILE_PREDICATE.writeNbt(this.pre).ifPresent(value -> nbt.put("pre", value));
            Adapters.TILE_PREDICATE.writeNbt(this.post).ifPresent(value -> nbt.put("post", value));
            Adapters.INT_ROLL.writeNbt(this.count).ifPresent(value -> nbt.put("count", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.pre = Adapters.TILE_PREDICATE.readNbt(nbt.get("pre")).orElse(TilePredicate.TRUE);
         this.post = Adapters.TILE_PREDICATE.readNbt(nbt.get("post")).orElse(TilePredicate.TRUE);
         this.count = Adapters.INT_ROLL.readNbt(nbt.get("count")).orElse(IntRoll.ofConstant(0));
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.TILE_PREDICATE.writeJson(this.pre).ifPresent(value -> json.add("pre", value));
            Adapters.TILE_PREDICATE.writeJson(this.post).ifPresent(value -> json.add("post", value));
            Adapters.INT_ROLL.writeJson(this.count).ifPresent(value -> json.add("count", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.pre = Adapters.TILE_PREDICATE.readJson(json.get("pre")).orElse(TilePredicate.TRUE);
         this.post = Adapters.TILE_PREDICATE.readJson(json.get("post")).orElse(TilePredicate.TRUE);
         this.count = Adapters.INT_ROLL.readJson(json.get("count")).orElse(IntRoll.ofConstant(0));
      }
   }
}
