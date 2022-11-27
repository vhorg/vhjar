package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.container.RelicPedestalContainer;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRelics;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class RelicPedestalBlock extends FacedBlock implements EntityBlock {
   public static final ModRelics.RelicProperty RELIC = ModRelics.RelicProperty.create("relic");
   public static final VoxelShape SHAPE = Shapes.or(
      Block.box(2.0, 15.0, 2.0, 14.0, 16.0, 14.0), new VoxelShape[]{Block.box(3.0, 1.0, 3.0, 13.0, 15.0, 13.0), Block.box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0)}
   );

   public RelicPedestalBlock() {
      super(Properties.of(Material.STONE, MaterialColor.STONE).strength(1.0F, 3600000.0F).noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.SOUTH)).setValue(RELIC, ModRelics.EMPTY)
      );
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return SHAPE;
   }

   @Nullable
   public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
      return ModBlocks.RELIC_STATUE_TILE_ENTITY.create(blockPos, blockState);
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{RELIC});
   }

   @Nonnull
   public InteractionResult use(
      @Nonnull BlockState blockState,
      @Nonnull Level world,
      @Nonnull final BlockPos blockPos,
      @Nonnull Player player,
      @Nonnull InteractionHand hand,
      @Nonnull BlockHitResult hitResult
   ) {
      if (world.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         if (blockState.getValue(RELIC) == ModRelics.EMPTY) {
            NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
               @Nonnull
               public Component getDisplayName() {
                  return new TextComponent("Relic Assembly");
               }

               @Nonnull
               public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory pInventory, @Nonnull Player playerx) {
                  return new RelicPedestalContainer(containerId, playerx, blockPos);
               }
            }, byteBuffer -> byteBuffer.writeBlockPos(blockPos));
         }

         return InteractionResult.SUCCESS;
      }
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
      return this.getOnBreakItemStack(level, pos);
   }

   public void playerWillDestroy(Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
      if (!world.isClientSide && !player.isCreative()) {
         ItemStack itemStack = this.getOnBreakItemStack(world, pos);
         ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
         itemEntity.setDefaultPickUpDelay();
         world.addFreshEntity(itemEntity);
      }

      super.playerWillDestroy(world, pos, state, player);
   }

   protected ItemStack getOnBreakItemStack(BlockGetter world, BlockPos blockPos) {
      BlockState blockState = world.getBlockState(blockPos);
      ModRelics.RelicRecipe recipe = (ModRelics.RelicRecipe)blockState.getValue(RELIC);
      ItemStack itemStack;
      if (recipe == ModRelics.EMPTY) {
         itemStack = new ItemStack(this);
      } else {
         itemStack = new ItemStack(ModItems.RELIC);
         DynamicModelItem.setGenericModelId(itemStack, recipe.getResultingRelic());
      }

      return itemStack;
   }
}
