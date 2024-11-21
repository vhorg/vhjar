package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.block.ICollectionTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.util.InventoryUtil;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class CollectionTask extends ProgressConfiguredTask<Integer, CollectionTask.Config> {
   public CollectionTask() {
      super(new CollectionTask.Config(), TaskCounter.Adapter.INT);
   }

   public CollectionTask(CollectionTask.Config config, TaskCounter<Integer, ?> counter) {
      super(config, counter, TaskCounter.Adapter.INT);
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.BLOCK_USE.register(this, event -> {
         if (this.parent == null || this.parent.hasActiveChildren()) {
            if (!event.getWorld().isClientSide()) {
               if (context.getSource() instanceof EntityTaskSource entitySource) {
                  if (entitySource.matches(event.getPlayer())) {
                     if (event.getWorld().getBlockEntity(event.getPos()) instanceof ICollectionTileEntity collectionTile && collectionTile.isForTask(this.id)) {
                        if (this.collectItems(context, event)) {
                           event.setResult(InteractionResult.SUCCESS);
                        }
                     }
                  }
               }
            }
         }
      });
      super.onAttach(context);
   }

   private boolean collectItems(TaskContext context, BlockUseEvent.Data event) {
      boolean collected = false;

      for (InventoryUtil.ItemAccess itemAccess : InventoryUtil.findAllItems(event.getPlayer())) {
         if (this.getConfig().item != null) {
            ItemStack stack = itemAccess.getStack();
            if (this.getConfig().item.test(stack)) {
               int remaining = this.counter.getProgress().getTarget().intValue() - this.counter.getProgress().getCurrent().intValue();
               if (remaining <= 0) {
                  break;
               }

               int shrinkBy = Math.min(remaining, stack.getCount());
               this.counter.onAdd(shrinkBy, context);
               if (shrinkBy == stack.getCount()) {
                  itemAccess.setStack(ItemStack.EMPTY);
               } else {
                  stack.shrink(shrinkBy);
                  itemAccess.setStack(stack);
               }

               collected = true;
            }
         }

         if (this.counter.isCompleted()) {
            break;
         }
      }

      return collected;
   }

   public static class Config extends ConfiguredTask.Config {
      public ItemPredicate item;

      public Config() {
      }

      public Config(ItemPredicate item) {
         this.item = item;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.ITEM_PREDICATE.writeBits(this.item, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.item = Adapters.ITEM_PREDICATE.readBits(buffer).orElse(null);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.ITEM_PREDICATE.writeNbt(this.item).ifPresent(value -> nbt.put("item", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.item = Adapters.ITEM_PREDICATE.readNbt(nbt.get("item")).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.ITEM_PREDICATE.writeJson(this.item).ifPresent(value -> json.add("item", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.item = Adapters.ITEM_PREDICATE.readJson(json.get("item")).orElse(null);
      }
   }
}
