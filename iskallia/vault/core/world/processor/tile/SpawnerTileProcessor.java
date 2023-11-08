package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class SpawnerTileProcessor extends TargetTileProcessor<SpawnerTileProcessor> {
   protected final WeightedList<WeightedList<PartialEntity>> output = new WeightedList<>();

   public WeightedList<WeightedList<PartialEntity>> getOutput() {
      return this.output;
   }

   public SpawnerTileProcessor into(WeightedList<PartialEntity> output, int weight) {
      this.output.put(output, (Number)weight);
      return this;
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      if (this.predicate.test(tile)) {
         CompoundTag nbt = new CompoundTag();
         CompoundTag inventory = new CompoundTag();
         ListTag stacks = new ListTag();
         this.output.getRandom(context.getRandom(tile.getPos())).ifPresent(result -> {
            result.forEach((entity, weight) -> {
               CompoundTag entry = new CompoundTag();
               entry.putString("id", "ispawner:spawn_egg");
               entry.putInt("Count", weight.intValue());
               entity.getNbt().asWhole().ifPresent(tag -> {
                  CompoundTag wrapped = new CompoundTag();
                  wrapped.put("EntityTag", tag);
                  entry.put("tag", wrapped);
               });
               stacks.add(entry);
            });
            inventory.put("Stacks", stacks);
            nbt.put("Inventory", inventory);
            PartialCompoundNbt.of(nbt).fillInto(tile.getEntity());
         });
      }

      return tile;
   }
}
