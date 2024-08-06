package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.EventPriority;

public class CraftingTask extends ProgressConfiguredTask<Integer, CraftingTask.Config> {
   public CraftingTask() {
      super(new CraftingTask.Config(), TaskCounter.Adapter.INT);
   }

   public CraftingTask(CraftingTask.Config config, TaskCounter<Integer, ?> counter) {
      super(config, counter, TaskCounter.Adapter.INT);
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.PLAYER_CRAFT.register(this, EventPriority.NORMAL, event -> {
         if (this.parent == null || this.parent.isCompleted()) {
            if (context.getSource() instanceof EntityTaskSource entitySource) {
               if (entitySource.matches(event.getPlayer())) {
                  if (this.getConfig().filter.test(event.getCrafting())) {
                     this.counter.onAdd(1, context);
                  }
               }
            }
         }
      });
      super.onAttach(context);
   }

   public static class Config extends ConfiguredTask.Config {
      public ItemPredicate filter;

      public Config() {
      }

      public Config(ItemPredicate filter) {
         this.filter = filter;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.ITEM_PREDICATE.writeBits(this.filter, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.filter = Adapters.ITEM_PREDICATE.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.ITEM_PREDICATE.writeNbt(this.filter).ifPresent(value -> nbt.put("filter", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.filter = Adapters.ITEM_PREDICATE.readNbt(nbt.get("filter")).orElseThrow();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.ITEM_PREDICATE.writeJson(this.filter).ifPresent(value -> json.add("filter", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.filter = Adapters.ITEM_PREDICATE.readJson(json.get("filter")).orElseThrow();
      }
   }
}
