package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class InteractBlockTask extends ProgressConfiguredTask<Integer, InteractBlockTask.Config> {
   public InteractBlockTask() {
      super(new InteractBlockTask.Config(), TaskCounter.Adapter.INT);
   }

   public InteractBlockTask(InteractBlockTask.Config config, TaskCounter<Integer, ?> counter) {
      super(config, counter, TaskCounter.Adapter.INT);
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.BLOCK_USE_MERGED.register(this, event -> {
         if (this.parent == null || this.parent.isCompleted()) {
            if (!event.getWorld().isClientSide()) {
               if (context.getSource() instanceof EntityTaskSource entitySource) {
                  if (entitySource.matches(event.getPlayer())) {
                     if (this.getConfig().pre == null || this.getConfig().pre.test(event.getPre())) {
                        if (this.getConfig().post == null || this.getConfig().post.test(event.getPost())) {
                           this.counter.onAdd(1, context);
                        }
                     }
                  }
               }
            }
         }
      });
      super.onAttach(context);
   }

   public static class Config extends ConfiguredTask.Config {
      public TilePredicate pre;
      public TilePredicate post;

      public Config() {
      }

      public Config(TilePredicate pre, TilePredicate post) {
         this.pre = pre;
         this.post = post;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.TILE_PREDICATE.writeBits(this.pre, buffer);
         Adapters.TILE_PREDICATE.writeBits(this.post, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.pre = Adapters.TILE_PREDICATE.readBits(buffer).orElse(null);
         this.post = Adapters.TILE_PREDICATE.readBits(buffer).orElse(null);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.TILE_PREDICATE.writeNbt(this.pre).ifPresent(value -> nbt.put("pre", value));
            Adapters.TILE_PREDICATE.writeNbt(this.post).ifPresent(value -> nbt.put("post", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.pre = Adapters.TILE_PREDICATE.readNbt(nbt.get("pre")).orElse(null);
         this.post = Adapters.TILE_PREDICATE.readNbt(nbt.get("post")).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.TILE_PREDICATE.writeJson(this.pre).ifPresent(value -> json.add("pre", value));
            Adapters.TILE_PREDICATE.writeJson(this.post).ifPresent(value -> json.add("post", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.pre = Adapters.TILE_PREDICATE.readJson(json.get("pre")).orElse(null);
         this.post = Adapters.TILE_PREDICATE.readJson(json.get("post")).orElse(null);
      }
   }
}
