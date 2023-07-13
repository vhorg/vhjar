package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class SpawnerElementTileProcessor extends TargetTileProcessor<SpawnerElementTileProcessor> {
   protected EntityPredicate element;
   protected final WeightedList<PartialEntity> output = new WeightedList<>();

   public WeightedList<PartialEntity> getOutput() {
      return this.output;
   }

   public SpawnerElementTileProcessor setElement(EntityPredicate element) {
      this.element = element;
      return this;
   }

   public SpawnerElementTileProcessor into(PartialEntity output, int weight) {
      this.output.put(output, (Number)weight);
      return this;
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      tile.getEntity().asWhole().ifPresent(tag -> {
         if (tag.contains("Inventory", 10)) {
            tag = tag.getCompound("Inventory");
            if (tag.contains("Stacks", 9)) {
               ListTag stacks = tag.getList("Stacks", 10);

               for (int i = 0; i < stacks.size(); i++) {
                  CompoundTag stack = stacks.getCompound(i);
                  CompoundTag nbt = stack.getCompound("tag").getCompound("EntityTag");
                  PartialEntity entity = PartialEntity.of(null, null, PartialCompoundNbt.of(nbt));
                  if (this.element.test(entity)) {
                     this.output.getRandom(context.random).ifPresent(other -> {
                        PartialCompoundNbt result = PartialCompoundNbt.of(nbt);
                        other.getNbt().fillInto(result);
                        result.asWhole().ifPresent(compoundTag -> {
                           CompoundTag wrapped = new CompoundTag();
                           wrapped.put("EntityTag", compoundTag);
                           stack.put("tag", wrapped);
                        });
                     });
                  }
               }
            }
         }
      });
      return tile;
   }
}
