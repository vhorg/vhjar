package iskallia.vault.block;

import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.init.ModBlocks;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VaultBarrelBlock extends VaultChestBlock {
   public static final DirectionProperty DIRECTION = DirectionProperty.create(
      "direction", new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN}
   );
   public static final ResourceLocation CONTENTS = new ResourceLocation("contents");

   public VaultBarrelBlock(VaultChestType type, boolean locked, boolean dynamicRenderer, Properties builder) {
      super(type, locked, dynamicRenderer, builder, () -> ModBlocks.VAULT_CHEST_TILE_ENTITY);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DIRECTION, Direction.UP))
                  .setValue(FACING, Direction.NORTH))
               .setValue(TYPE, ChestType.SINGLE))
            .setValue(WATERLOGGED, false)
      );
   }

   @Override
   protected void createBlockStateDefinition(@NotNull Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{DIRECTION});
   }

   @Override
   public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
      return true;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      return state != null ? (BlockState)state.setValue(DIRECTION, context.getNearestLookingDirection().getOpposite()) : null;
   }

   @NotNull
   public List<ItemStack> getDrops(@NotNull BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      BlockEntity blockEntity = (BlockEntity)builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (blockEntity instanceof VaultChestTileEntity barrel && !barrel.isVaultChest()) {
         builder = builder.withDynamicDrop(CONTENTS, (p_56218_, p_56219_) -> {
            for (int index = 0; index < barrel.getContainerSize(); index++) {
               p_56219_.accept(barrel.getItem(index));
            }
         });
      }

      return super.getDrops(state, builder);
   }

   public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         if (level.getBlockEntity(pos) instanceof VaultChestTileEntity entity && !entity.isVaultChest()) {
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
            if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
               level.removeBlockEntity(pos);
            }
         } else {
            super.onRemove(state, level, pos, newState, isMoving);
         }
      }
   }

   public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter blockGetter, @NotNull List<Component> toolTip, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, blockGetter, toolTip, flag);
      CompoundTag entityData = BlockItem.getBlockEntityData(stack);
      if (entityData != null && entityData.contains("Items", 9)) {
         NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
         ContainerHelper.loadAllItems(entityData, items);
         int unique = 0;
         int index = 0;

         for (ItemStack itemStack : items) {
            if (!itemStack.isEmpty()) {
               index++;
               if (unique <= 4) {
                  unique++;
                  MutableComponent component = itemStack.getHoverName().copy();
                  component.append(" x").append(String.valueOf(itemStack.getCount()));
                  toolTip.add(component);
               }
            }
         }

         if (index - unique > 0) {
            toolTip.add(new TranslatableComponent("container.the_vault.barrel.more", new Object[]{index - unique}).withStyle(ChatFormatting.ITALIC));
         }
      }
   }
}
