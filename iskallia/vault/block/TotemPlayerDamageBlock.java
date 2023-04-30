package iskallia.vault.block;

import iskallia.vault.block.entity.TotemPlayerDamageTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class TotemPlayerDamageBlock extends TotemBlock {
   public static final EnumProperty<TotemPlayerDamageBlock.Type> TYPE = EnumProperty.create("type", TotemPlayerDamageBlock.Type.class);

   public TotemPlayerDamageBlock() {
      this.registerDefaultState(
         (BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false)).setValue(TYPE, TotemPlayerDamageBlock.Type.BASE)
      );
   }

   @ParametersAreNonnullByDefault
   @Nullable
   public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return ModBlocks.TOTEM_PLAYER_DAMAGE_TILE_ENTITY.create(blockPos, blockState);
   }

   @ParametersAreNonnullByDefault
   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
      return level.isClientSide
         ? null
         : BlockHelper.getTicker(blockEntityType, ModBlocks.TOTEM_PLAYER_DAMAGE_TILE_ENTITY, TotemPlayerDamageTileEntity::serverTick);
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{TYPE});
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockState = super.getStateForPlacement(context);
      return blockState == null ? null : (BlockState)blockState.setValue(TYPE, TotemPlayerDamageBlock.Type.BASE);
   }

   public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
      return state.getValue(TYPE) == TotemPlayerDamageBlock.Type.GLOW ? 15 : 0;
   }

   public static enum Type implements StringRepresentable {
      BASE("base"),
      GLOW("glow");

      private final String name;

      private Type(String name) {
         this.name = name;
      }

      @Nonnull
      public String getSerializedName() {
         return this.name;
      }
   }
}
