package iskallia.vault.block;

import iskallia.vault.block.entity.FinalVaultFrameTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class FinalVaultFrameBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

   public FinalVaultFrameBlock() {
      super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0F, 3600000.0F).noOcclusion());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @SubscribeEvent
   public static void onBlockHit(LeftClickBlock event) {
      if (event.isCancelable()) {
         Player player = event.getPlayer();
         if (!player.isCreative()) {
            FinalVaultFrameTileEntity tileEntity = FinalVaultFrameTileEntity.get(player.level, event.getPos());
            if (tileEntity != null) {
               if (!tileEntity.getOwnerUUID().equals(player.getUUID())) {
                  event.setCanceled(true);
               }
            }
         }
      }
   }

   @Nonnull
   public PushReaction getPistonPushReaction(@Nonnull BlockState state) {
      return PushReaction.BLOCK;
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
      ItemStack itemStack = new ItemStack(this);
      FinalVaultFrameTileEntity tileEntity = FinalVaultFrameTileEntity.get(level, pos);
      CompoundTag entityNBT = new CompoundTag();
      if (tileEntity != null) {
         tileEntity.writeToEntityTag(entityNBT);
      }

      itemStack.getOrCreateTag().put("BlockEntityTag", entityNBT);
      return itemStack;
   }

   public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
      if (!world.isClientSide()) {
         CompoundTag tag = stack.getTagElement("BlockEntityTag");
         if (tag != null) {
            FinalVaultFrameTileEntity tileEntity = FinalVaultFrameTileEntity.get(world, pos);
            if (tileEntity != null) {
               tileEntity.loadFromNBT(tag);
               super.setPlacedBy(world, pos, state, placer, stack);
            }
         }
      }
   }

   public void playerWillDestroy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
      if (!world.isClientSide && !player.isCreative()) {
         FinalVaultFrameTileEntity tileEntity = FinalVaultFrameTileEntity.get(world, pos);
         if (tileEntity != null) {
            ItemStack itemStack = new ItemStack(this);
            CompoundTag entityNBT = new CompoundTag();
            tileEntity.writeToEntityTag(entityNBT);
            itemStack.getOrCreateTag().put("BlockEntityTag", entityNBT);
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
            itemEntity.setDefaultPickUpDelay();
            world.addFreshEntity(itemEntity);
         }
      }

      super.playerWillDestroy(world, pos, state, player);
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.FINAL_VAULT_FRAME_TILE_ENTITY.create(pos, state);
   }
}
