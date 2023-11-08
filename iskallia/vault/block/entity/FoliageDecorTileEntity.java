package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.tags.ModBlockTags;
import iskallia.vault.util.nbt.NBTHelper;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class FoliageDecorTileEntity extends BlockEntity {
   private final FoliageDecorTileEntity.FoliageInventory inventory = new FoliageDecorTileEntity.FoliageInventory();

   public FoliageDecorTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.FOLIAGE_DECOR_TILE_ENTITY, pos, state);
   }

   public FoliageDecorTileEntity.FoliageInventory getInventory() {
      return this.inventory;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      NBTHelper.deserializeSimpleContainer(this.inventory, tag.getList("inventory", 10));
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.put("inventory", NBTHelper.serializeSimpleContainer(this.inventory));
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public boolean interact(@NotNull BlockState state, @NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
      ItemStack itemInHand = player.getItemInHand(hand).copy();
      ItemStack itemInFoliage = this.inventory.getItem(0).copy();
      if (!player.isCrouching() && itemInHand.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof ShulkerBoxBlock)) {
         BlockState foliageState = blockItem.getBlock().defaultBlockState();
         if (foliageState.is(ModBlockTags.FOLIAGE) || foliageState.is(BlockTags.SAPLINGS) || foliageState.is(BlockTags.CROPS)) {
            if (level instanceof ServerLevel serverWorld) {
               ItemStack stack = itemInHand.copy();
               stack.setCount(1);
               this.inventory.setItem(0, stack);
               player.getItemInHand(hand).shrink(1);
               level.playSound(
                  null,
                  this.getBlockPos(),
                  blockItem.getBlock().getSoundType(blockItem.getBlock().defaultBlockState()).getPlaceSound(),
                  SoundSource.BLOCKS,
                  0.75F,
                  1.5F
               );
               this.setChanged();
               serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
            }

            return true;
         }
      }

      if (player.isCrouching() && itemInHand.isEmpty() && !itemInFoliage.isEmpty()) {
         if (level instanceof ServerLevel serverWorld) {
            player.setItemInHand(hand, itemInFoliage);
            this.inventory.getItem(0).shrink(1);
            level.playSound(null, this.getBlockPos(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.setChanged();
            serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
         }

         return true;
      } else {
         return false;
      }
   }

   public class FoliageInventory extends SimpleContainer {
      public FoliageInventory() {
         super(1);
      }

      public boolean canPlaceItem(int slot, ItemStack stack) {
         return true;
      }

      public void setChanged() {
         super.setChanged();
         FoliageDecorTileEntity.this.setChanged();
      }
   }
}
