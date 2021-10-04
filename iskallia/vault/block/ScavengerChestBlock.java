package iskallia.vault.block;

import iskallia.vault.block.entity.ScavengerChestTileEntity;
import iskallia.vault.init.ModBlocks;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;

public class ScavengerChestBlock extends ChestBlock {
   protected ScavengerChestBlock(Properties builder, Supplier<TileEntityType<? extends ChestTileEntity>> tileEntityTypeIn) {
      super(builder, tileEntityTypeIn);
   }

   public ScavengerChestBlock(Properties builder) {
      this(builder, () -> ModBlocks.SCAVENGER_CHEST_TILE_ENTITY);
   }

   public TileEntity func_196283_a_(IBlockReader worldIn) {
      return new ScavengerChestTileEntity();
   }

   public BlockState func_196258_a(BlockItemUseContext context) {
      BlockState state = super.func_196258_a(context);
      return state == null ? null : (BlockState)state.func_206870_a(field_196314_b, ChestType.SINGLE);
   }
}
