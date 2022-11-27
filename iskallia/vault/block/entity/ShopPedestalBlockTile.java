package iskallia.vault.block.entity;

import iskallia.vault.config.ShopPedestalConfig;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLootItem;
import iskallia.vault.world.data.ServerVaults;
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
   private ItemStack currency = ItemStack.EMPTY;

   public ShopPedestalBlockTile(BlockPos pos, BlockState state) {
      super(ModBlocks.SHOP_PEDESTAL_TILE_ENTITY, pos, state);
   }

   public AABB getRenderBoundingBox() {
      return new AABB(this.worldPosition, this.worldPosition.offset(1, 2, 1));
   }

   public static void tick(Level world, BlockPos pos, BlockState state, ShopPedestalBlockTile tile) {
      if (world instanceof ServerLevel serverLevel) {
         if (!tile.initialized) {
            tile.initialized = true;
            int level = ServerVaults.getVaultLevelOrZero(serverLevel);
            ShopPedestalConfig.ShopOffer shopOffer = ModConfigs.SHOP_PEDESTAL.getForLevel(level, serverLevel.getRandom());
            if (shopOffer != null && !shopOffer.isEmpty()) {
               ItemStack offerStack = shopOffer.offer().copy();
               if (offerStack.getItem() instanceof VaultLootItem lootItem) {
                  Vault vault = ServerVaults.get(serverLevel).orElse(null);
                  if (vault != null) {
                     lootItem.initializeLoot(vault, offerStack);
                  }
               }

               offerStack = DataTransferItem.doConvertStack(offerStack);
               tile.offer = offerStack.copy();
               tile.currency = shopOffer.currency().copy();
            }

            tile.setChanged();
            world.sendBlockUpdated(pos, state, state, 3);
         }
      }
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.initialized = tag.getBoolean("initialized");
      this.offer = ItemStack.of(tag.getCompound("offerStack"));
      this.currency = ItemStack.of(tag.getCompound("currencyStack"));
   }

   public void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.putBoolean("initialized", this.initialized);
      tag.put("offerStack", this.offer.serializeNBT());
      tag.put("currencyStack", this.currency.serializeNBT());
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public ItemStack getOfferStack() {
      return this.offer.copy();
   }

   public ItemStack getCurrencyStack() {
      return this.currency.copy();
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
      this.currency = ItemStack.EMPTY;
      this.setChanged();
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
   }

   public void setOffer(ItemStack offer, ItemStack currency) {
      this.initialized = true;
      this.offer = offer.copy();
      this.currency = currency.copy();
      this.setChanged();
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
   }
}
