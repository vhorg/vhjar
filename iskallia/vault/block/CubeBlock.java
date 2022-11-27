package iskallia.vault.block;

import iskallia.vault.block.base.LootableBlock;
import iskallia.vault.block.entity.CubeTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CubeBlock extends LootableBlock {
   public static final EnumProperty<CubeBlock.CubeColor> COLOR = EnumProperty.create("color", CubeBlock.CubeColor.class);

   public CubeBlock() {
      super(Properties.copy(Blocks.BLACK_CONCRETE));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(COLOR, CubeBlock.CubeColor.BLUE));
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new CubeTileEntity(pos, state);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{COLOR});
   }

   @Nullable
   public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
      CubeBlock.CubeColor color = CubeBlock.CubeColor.values()[context.getLevel().random.nextInt(CubeBlock.CubeColor.values().length)];
      return (BlockState)this.defaultBlockState().setValue(COLOR, color);
   }

   static enum CubeColor implements StringRepresentable {
      BLUE("blue"),
      GREEN("green"),
      RED("red"),
      YELLOW("yellow");

      private final String name;

      private CubeColor(String name) {
         this.name = name;
      }

      @NotNull
      public String getSerializedName() {
         return this.name;
      }
   }
}
