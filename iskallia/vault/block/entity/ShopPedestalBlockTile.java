package iskallia.vault.block.entity;

import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class ShopPedestalBlockTile extends BlockEntity {
   private boolean initialized = false;
   private ItemStack offer = ItemStack.EMPTY;
   private OverSizedItemStack currency = OverSizedItemStack.EMPTY;

   public ShopPedestalBlockTile(BlockPos pos, BlockState state) {
      super(ModBlocks.SHOP_PEDESTAL_TILE_ENTITY, pos, state);
   }

   public AABB getRenderBoundingBox() {
      return new AABB(this.worldPosition, this.worldPosition.offset(1, 2, 1));
   }

   public static void tick(Level world, BlockPos pos, BlockState state, ShopPedestalBlockTile tile) {
      if (world instanceof ServerLevel serverLevel) {
         if (!tile.initialized) {
            CommonEvents.SHOP_PEDESTAL_LOOT_GENERATION.invoke(serverLevel, state, pos, tile, JavaRandom.ofNanoTime());
         }
      }
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.initialized = tag.getBoolean("initialized");
      this.offer = ItemStack.of(tag.getCompound("offerStack"));
      this.currency = OverSizedItemStack.deserialize(tag.getCompound("currencyStack"));
   }

   public void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.putBoolean("initialized", this.initialized);
      tag.put("offerStack", this.offer.serializeNBT());
      tag.put("currencyStack", this.currency.serialize());
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(boolean initialized) {
      this.initialized = initialized;
   }

   public ItemStack getOfferStack() {
      return this.offer.copy();
   }

   public ItemStack getCurrencyStack() {
      return this.currency.overSizedStack().copy();
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void clear() {
      this.initialized = false;
      this.offer = ItemStack.EMPTY;
      this.currency = OverSizedItemStack.EMPTY;
      this.setChanged();
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
   }

   public void setOffer(ItemStack offer, OverSizedItemStack currency) {
      this.initialized = true;
      this.offer = offer;
      this.currency = currency;
      this.setChanged();
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
   }
}
