package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class LootChestItemTask extends ProgressConfiguredTask<Integer, LootChestItemTask.Config> {
   public LootChestItemTask() {
      super(new LootChestItemTask.Config(), TaskCounter.Adapter.INT);
   }

   public LootChestItemTask(LootChestItemTask.Config config, TaskCounter<Integer, ?> counter) {
      super(config, counter, TaskCounter.Adapter.INT);
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.CHEST_LOOT_GENERATION.register(this, data -> {
         if (this.parent == null || this.parent.isCompleted()) {
            if (!data.getPlayer().getLevel().isClientSide()) {
               if (context.getSource() instanceof EntityTaskSource entitySource) {
                  if (entitySource.matches(data.getPlayer())) {
                     PartialTile tile = PartialTile.of(PartialBlockState.of(data.getState()), PartialCompoundNbt.of(data.getTileEntity()));
                     if (this.getConfig().chestFilter.test(tile)) {
                        for (ItemStack stack : data.getLoot()) {
                           if (this.getConfig().itemFilter.test(stack)) {
                              this.counter.onAdd(stack.getCount(), context);
                           }
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
      public TilePredicate chestFilter;
      public ItemPredicate itemFilter;

      public Config() {
      }

      public Config(TilePredicate chestFilter, ItemPredicate itemFilter) {
         this.chestFilter = chestFilter;
         this.itemFilter = itemFilter;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.TILE_PREDICATE.writeBits(this.chestFilter, buffer);
         Adapters.ITEM_PREDICATE.writeBits(this.itemFilter, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.chestFilter = Adapters.TILE_PREDICATE.readBits(buffer).orElseThrow();
         this.itemFilter = Adapters.ITEM_PREDICATE.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.TILE_PREDICATE.writeNbt(this.chestFilter).ifPresent(value -> nbt.put("chestFilter", value));
            Adapters.ITEM_PREDICATE.writeNbt(this.itemFilter).ifPresent(value -> nbt.put("itemFilter", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.chestFilter = Adapters.TILE_PREDICATE.readNbt(nbt.get("chestFilter")).orElse(TilePredicate.FALSE);
         this.itemFilter = Adapters.ITEM_PREDICATE.readNbt(nbt.get("itemFilter")).orElse(ItemPredicate.FALSE);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.TILE_PREDICATE.writeJson(this.chestFilter).ifPresent(value -> json.add("chestFilter", value));
            Adapters.ITEM_PREDICATE.writeJson(this.itemFilter).ifPresent(value -> json.add("itemFilter", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.chestFilter = Adapters.TILE_PREDICATE.readJson(json.get("chestFilter")).orElse(TilePredicate.FALSE);
         this.itemFilter = Adapters.ITEM_PREDICATE.readJson(json.get("itemFilter")).orElse(ItemPredicate.FALSE);
      }
   }
}
