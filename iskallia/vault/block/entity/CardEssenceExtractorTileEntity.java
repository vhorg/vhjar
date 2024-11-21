package iskallia.vault.block.entity;

import iskallia.vault.config.CardEssenceExtractorConfig;
import iskallia.vault.core.card.Card;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.CardDeckItem;
import iskallia.vault.item.CardItem;
import iskallia.vault.util.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardEssenceExtractorTileEntity extends BlockEntity {
   private int essence = 0;
   private final SimpleContainer inventory = new SimpleContainer(3) {
      public void setChanged() {
         super.setChanged();
         CardEssenceExtractorTileEntity.this.sendUpdates();
      }
   };
   private int maxExtractWorkTick = -1;
   private int extractWorkTick = -1;

   public CardEssenceExtractorTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.CARD_ESSENCE_EXTRACTOR_TILE_ENTITY, pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, CardEssenceExtractorTileEntity tile) {
      if (!level.isClientSide()) {
         if (tile.extractWorkTick >= 0) {
            if (tile.getEssenceInputStack().isEmpty()) {
               tile.resetExtractTick();
               tile.sendUpdates();
            } else {
               tile.extractWorkTick--;
               if (tile.extractWorkTick <= 0) {
                  tile.resetExtractTick();
                  tile.extractCardEssence();
               }

               tile.sendUpdates();
            }
         }
      }
   }

   public void resetExtractTick() {
      this.extractWorkTick = -1;
      this.maxExtractWorkTick = -1;
   }

   private void extractCardEssence() {
      ItemStack essenceInputStack = this.getEssenceInputStack();
      if (!essenceInputStack.isEmpty()) {
         if (essenceInputStack.is(ModItems.CARD)) {
            Card card = CardItem.getCard(essenceInputStack);
            int tier = card.getTier();
            ModConfigs.CARD_ESSENCE_EXTRACTOR.getConfig(tier).ifPresent(cfg -> this.essence = this.essence + cfg.getEssencePerCard().getRandom());
         }

         if (essenceInputStack.is(ModItems.CARD_DECK)) {
            String id = CardDeckItem.getId(essenceInputStack);
            ModConfigs.CARD_DECK.getEssence(id).ifPresent(essence -> this.essence = this.essence + essence);
         }

         essenceInputStack.shrink(1);
         this.setEssenceInputStack(essenceInputStack);
         this.level
            .playSound(
               null,
               this.worldPosition.getX(),
               this.worldPosition.getY(),
               this.worldPosition.getZ(),
               SoundEvents.BREWING_STAND_BREW,
               SoundSource.PLAYERS,
               0.8F,
               this.level.random.nextFloat() * 0.3F + 1.7F
            );
      }
   }

   public void startExtract() {
      ItemStack essenceInputStack = this.getEssenceInputStack();
      if (!essenceInputStack.isEmpty()) {
         if (essenceInputStack.is(ModItems.CARD) || essenceInputStack.is(ModItems.CARD_DECK)) {
            if (this.extractWorkTick < 0) {
               int extractTime = -1;
               if (essenceInputStack.is(ModItems.CARD)) {
                  Card card = CardItem.getCard(essenceInputStack);
                  int tier = card.getTier();
                  extractTime = ModConfigs.CARD_ESSENCE_EXTRACTOR.getConfig(tier).map(CardEssenceExtractorConfig.TierConfig::getExtractTickTime).orElse(-1);
               } else if (essenceInputStack.is(ModItems.CARD_DECK)) {
                  extractTime = ModConfigs.CARD_ESSENCE_EXTRACTOR.getConfig(1).map(CardEssenceExtractorConfig.TierConfig::getExtractTickTime).orElse(-1);
               }

               if (extractTime > 0) {
                  this.extractWorkTick = extractTime;
                  this.maxExtractWorkTick = extractTime;
                  this.sendUpdates();
               }
            }
         }
      }
   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   public float getExtractProgress() {
      return this.extractWorkTick < 0 ? 0.0F : Mth.clamp(1.0F - (float)this.extractWorkTick / this.maxExtractWorkTick, 0.0F, 1.0F);
   }

   public int getEssence() {
      return this.essence;
   }

   public void setEssence(int essence) {
      this.essence = essence;
      this.sendUpdates();
   }

   public ItemStack getEssenceInputStack() {
      return this.getInventory().getItem(0);
   }

   public void setEssenceInputStack(ItemStack essenceInputStack) {
      this.getInventory().setItem(0, essenceInputStack.copy());
   }

   public ItemStack getCardUpgradeStack() {
      return this.getInventory().getItem(1);
   }

   public void setCardUpgradeStack(ItemStack cardUpgradeStack) {
      this.getInventory().setItem(1, cardUpgradeStack.copy());
   }

   public ItemStack getCardUpgradeOutputStack() {
      return this.getInventory().getItem(2);
   }

   public void setCardUpgradeOutputStack(ItemStack cardOutputStack) {
      this.getInventory().setItem(2, cardOutputStack.copy());
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      NBTHelper.deserializeSimpleContainer(this.inventory, tag.getList("inventory", 10));
      this.essence = tag.getInt("essence");
      this.extractWorkTick = tag.getInt("extractWorkTick");
      this.maxExtractWorkTick = tag.getInt("maxExtractWorkTick");
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.put("inventory", NBTHelper.serializeSimpleContainer(this.inventory));
      tag.putInt("essence", this.essence);
      tag.putInt("extractWorkTick", this.extractWorkTick);
      tag.putInt("maxExtractWorkTick", this.maxExtractWorkTick);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
      return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
         ? LazyOptional.of(() -> new RangedWrapper(new InvWrapper(this.inventory), 0, 1)).cast()
         : super.getCapability(cap, side);
   }
}
