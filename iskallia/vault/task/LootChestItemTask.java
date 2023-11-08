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
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class LootChestItemTask extends ProgressConfiguredTask<Integer, LootChestItemTask.Config> {
   public LootChestItemTask() {
      super(new LootChestItemTask.Config(), 0, Adapters.INT_SEGMENTED_7, Integer::compare);
   }

   public LootChestItemTask(LootChestItemTask.Config config) {
      super(config, 0, Adapters.INT_SEGMENTED_7, Integer::compare);
   }

   @Override
   public void onPopulate(TaskSource source) {
      this.targetCount = this.getConfig().count.get(source.getRandom());
   }

   @Override
   public void onAttach(TaskSource source) {
      CommonEvents.CHEST_LOOT_GENERATION.register(this, data -> {
         if (!data.getPlayer().getLevel().isClientSide()) {
            if (source instanceof EntityTaskSource entitySource) {
               if (entitySource.matches(data.getPlayer())) {
                  PartialTile tile = PartialTile.of(PartialBlockState.of(data.getState()), PartialCompoundNbt.of(data.getTileEntity()));
                  if (this.getConfig().chestFilter.test(tile)) {
                     for (ItemStack stack : data.getLoot()) {
                        if (this.getConfig().itemFilter.test(stack)) {
                           this.currentCount = this.currentCount + stack.getCount();
                        }
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
      CommonEvents.CHEST_LOOT_GENERATION.release(this);
      super.onDetach();
   }

   public static class Config extends ConfiguredTask.Config {
      public TilePredicate chestFilter;
      public ItemPredicate itemFilter;
      public IntRoll count;

      public Config() {
      }

      public Config(TilePredicate chestFilter, ItemPredicate itemFilter, IntRoll count) {
         this.chestFilter = chestFilter;
         this.itemFilter = itemFilter;
         this.count = count;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.TILE_PREDICATE.writeBits(this.chestFilter, buffer);
         Adapters.ITEM_PREDICATE.writeBits(this.itemFilter, buffer);
         Adapters.INT_ROLL.writeBits(this.count, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.chestFilter = Adapters.TILE_PREDICATE.readBits(buffer).orElseThrow();
         this.itemFilter = Adapters.ITEM_PREDICATE.readBits(buffer).orElseThrow();
         this.count = Adapters.INT_ROLL.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.TILE_PREDICATE.writeNbt(this.chestFilter).ifPresent(value -> nbt.put("chestFilter", value));
            Adapters.ITEM_PREDICATE.writeNbt(this.itemFilter).ifPresent(value -> nbt.put("itemFilter", value));
            Adapters.INT_ROLL.writeNbt(this.count).ifPresent(value -> nbt.put("count", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.chestFilter = Adapters.TILE_PREDICATE.readNbt(nbt.get("chestFilter")).orElse(TilePredicate.FALSE);
         this.itemFilter = Adapters.ITEM_PREDICATE.readNbt(nbt.get("itemFilter")).orElse(ItemPredicate.FALSE);
         this.count = Adapters.INT_ROLL.readNbt(nbt.get("count")).orElse(IntRoll.ofConstant(0));
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.TILE_PREDICATE.writeJson(this.chestFilter).ifPresent(value -> json.add("chestFilter", value));
            Adapters.ITEM_PREDICATE.writeJson(this.itemFilter).ifPresent(value -> json.add("itemFilter", value));
            Adapters.INT_ROLL.writeJson(this.count).ifPresent(value -> json.add("count", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.chestFilter = Adapters.TILE_PREDICATE.readJson(json.get("chestFilter")).orElse(TilePredicate.FALSE);
         this.itemFilter = Adapters.ITEM_PREDICATE.readJson(json.get("itemFilter")).orElse(ItemPredicate.FALSE);
         this.count = Adapters.INT_ROLL.readJson(json.get("count")).orElse(IntRoll.ofConstant(0));
      }
   }
}
