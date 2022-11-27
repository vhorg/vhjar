package iskallia.vault.core.world.processor.tile;

import com.mojang.brigadier.StringReader;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.PartialState;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.data.TileParser;
import iskallia.vault.core.world.processor.ProcessorContext;
import iskallia.vault.init.ModBlocks;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WeightedTileProcessor extends TargetTileProcessor<WeightedTileProcessor> {
   protected final WeightedList<PartialTile> output = new WeightedList<>();

   public WeightedList<PartialTile> getOutput() {
      return this.output;
   }

   public WeightedTileProcessor into(String target) {
      return this.into(target, 1);
   }

   public WeightedTileProcessor into(Block block) {
      return this.into(PartialTile.of(block));
   }

   public WeightedTileProcessor into(BlockState state) {
      return this.into(PartialTile.of(state));
   }

   public WeightedTileProcessor into(PartialState state) {
      return this.into(PartialTile.of(state));
   }

   public WeightedTileProcessor into(Block block, CompoundTag nbt) {
      return this.into(PartialTile.of(block, nbt));
   }

   public WeightedTileProcessor into(BlockState state, CompoundTag nbt) {
      return this.into(PartialTile.of(state, nbt));
   }

   public WeightedTileProcessor into(PartialState state, CompoundTag nbt) {
      return this.into(PartialTile.of(state, nbt));
   }

   public WeightedTileProcessor into(PartialTile tile) {
      return this.into(tile, 1);
   }

   public WeightedTileProcessor into(String target, int weight) {
      PartialTile tile = new TileParser(new StringReader(target), ModBlocks.ERROR_BLOCK, false).toTile();
      return this.into(tile, weight);
   }

   public WeightedTileProcessor into(Block block, int weight) {
      return this.into(PartialTile.of(block), weight);
   }

   public WeightedTileProcessor into(BlockState state, int weight) {
      return this.into(PartialTile.of(state), weight);
   }

   public WeightedTileProcessor into(PartialState state, int weight) {
      return this.into(PartialTile.of(state), weight);
   }

   public WeightedTileProcessor into(Block block, CompoundTag nbt, int weight) {
      return this.into(PartialTile.of(block, nbt), weight);
   }

   public WeightedTileProcessor into(BlockState state, CompoundTag nbt, int weight) {
      return this.into(PartialTile.of(state, nbt), weight);
   }

   public WeightedTileProcessor into(PartialState state, CompoundTag nbt, int weight) {
      return this.into(PartialTile.of(state, nbt), weight);
   }

   public WeightedTileProcessor into(PartialTile tile, int weight) {
      this.output.put(tile, weight);
      return this;
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      if (this.predicate.test(tile)) {
         Optional<PartialTile> output = this.output.getRandom(context.random);
         output.ifPresent(partialTile -> partialTile.copyInto(tile));
      }

      return tile;
   }
}
