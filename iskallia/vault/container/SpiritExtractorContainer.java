package iskallia.vault.container;

import com.mojang.authlib.GameProfile;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSlotIcons;
import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.network.message.ClientboundRefreshSpiritExtractorMessage;
import iskallia.vault.network.message.SpiritExtractorMessage;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.world.data.PlayerSpiritRecoveryData;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkDirection;

public class SpiritExtractorContainer extends OverSizedSlotContainer {
   private final BlockPos pos;
   private final SpiritExtractorTileEntity tileEntity;
   private boolean recycleUnlocked = false;
   private int spiritRecoveryCount = -1;
   private float multiplier = -1.0F;
   private float heroDiscount = 0.0F;
   private SpiritExtractorTileEntity.RecoveryCost currentPlayersRecoveryCost = new SpiritExtractorTileEntity.RecoveryCost();

   public SpiritExtractorContainer(int id, Inventory playerInventory, BlockPos pos) {
      super(ModContainers.SPIRIT_EXTRACTOR_CONTAINER, id, playerInventory.player);
      this.pos = pos;
      if (this.player.level.getBlockEntity(pos) instanceof SpiritExtractorTileEntity spiritExtractorTile) {
         this.tileEntity = spiritExtractorTile;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   private void calculateCostForCurrentPlayer() {
      List<ItemStack> items = NonNullList.create();

      for (int slot = 0; slot < this.player.getInventory().getContainerSize(); slot++) {
         ItemStack stackInSlot = this.player.getInventory().getItem(slot);
         if (shouldAddItem(stackInSlot)) {
            items.add(stackInSlot.copy());
         }
      }

      if (ModList.get().isLoaded("curios")) {
         IntegrationCurios.getCuriosItemStacks(this.player).forEach((slotType, stacks) -> stacks.forEach(stack -> {
            if (shouldAddItem(stack)) {
               items.add(stack.copy());
            }
         }));
      }

      this.currentPlayersRecoveryCost.calculate(this.getMultiplier(), this.getPlayerLevel(), items, this.heroDiscount, this.getRescuedBonus());
   }

   private static boolean shouldAddItem(ItemStack stack) {
      return !stack.isEmpty()
         && (
            !AttributeGearData.hasData(stack)
               || !AttributeGearData.<AttributeGearData>read(stack).get(ModGearAttributes.SOULBOUND, VaultGearAttributeTypeMerger.anyTrue())
         );
   }

   private void initSlots(Inventory playerInventory) {
      int playerInventoryTopY = 102;

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, playerInventoryTopY + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, playerInventoryTopY + 54 + 4));
      }

      OverSizedTabSlot paymentSlot = new OverSizedTabSlot(this.tileEntity.getPaymentInventory(), 0, 131, playerInventoryTopY - 32) {
         public boolean mayPickup(Player pPlayer) {
            return !SpiritExtractorContainer.this.tileEntity.isSpewingItems();
         }

         @Override
         public boolean mayPlace(ItemStack stack) {
            return SpiritExtractorContainer.this.getTotalCost().getItem() == stack.getItem();
         }
      };
      paymentSlot.setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM);
      this.addSlot(paymentSlot);
   }

   public boolean stillValid(Player player) {
      return player.distanceToSqr(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   public List<ItemStack> getStoredItems() {
      return this.tileEntity.getItems();
   }

   public void startSpewingItems() {
      this.tileEntity.spewItems();
      if (this.player.level.isClientSide()) {
         ModNetwork.CHANNEL.sendToServer(new SpiritExtractorMessage(this.getExtractorPos(), SpiritExtractorMessage.Action.REVIVE));
      }

      this.player.closeContainer();
   }

   public void recycle() {
      if (this.player.level.isClientSide()) {
         ModNetwork.CHANNEL.sendToServer(new SpiritExtractorMessage(this.getExtractorPos(), SpiritExtractorMessage.Action.RECYCLE));
      }

      this.player.closeContainer();
   }

   public boolean coinsCoverTotalCost() {
      return this.tileEntity.coinsCoverTotalCost();
   }

   public BlockPos getExtractorPos() {
      return this.pos;
   }

   public ItemStack getTotalCost() {
      return this.tileEntity.getRecoveryCost().getTotalCost();
   }

   public void setSpiritRecoveryCountAndMultiplier(int spiritRecoveryCount, float multiplier, float heroDiscount) {
      this.spiritRecoveryCount = spiritRecoveryCount;
      this.multiplier = multiplier;
      this.heroDiscount = heroDiscount;
   }

   public int getSpiritRecoveryCount() {
      return this.spiritRecoveryCount;
   }

   public float getHeroDiscount() {
      return this.heroDiscount;
   }

   public void broadcastChanges() {
      if (this.player.level instanceof ServerLevel serverLevel && this.costDataChanged(serverLevel)) {
         this.updateClientCostData(serverLevel);
      }

      super.broadcastChanges();
   }

   public void broadcastFullState() {
      if (this.player.level instanceof ServerLevel serverLevel && this.costDataChanged(serverLevel)) {
         this.updateClientCostData(serverLevel);
      }

      super.broadcastFullState();
   }

   private boolean costDataChanged(ServerLevel serverLevel) {
      UUID playerId = this.tileEntity.getGameProfile().<UUID>map(GameProfile::getId).orElse(this.player.getUUID());
      PlayerSpiritRecoveryData data = PlayerSpiritRecoveryData.get(serverLevel);
      return data.getSpiritRecoveryCount(playerId) != this.spiritRecoveryCount || !Mth.equal(data.getSpiritRecoveryMultiplier(playerId), this.multiplier);
   }

   public SpiritExtractorTileEntity.RecoveryCost getRecoveryCost() {
      if (this.tileEntity.getGameProfile().isEmpty() && this.currentPlayersRecoveryCost.isEmpty()) {
         this.calculateCostForCurrentPlayer();
      }

      return this.tileEntity.getGameProfile().isPresent() ? this.tileEntity.getRecoveryCost() : this.currentPlayersRecoveryCost;
   }

   private void updateClientCostData(ServerLevel serverLevel) {
      this.tileEntity.recalculateCost();
      serverLevel.sendBlockUpdated(this.tileEntity.getBlockPos(), this.tileEntity.getBlockState(), this.tileEntity.getBlockState(), 3);
      UUID playerId = this.tileEntity.getGameProfile().<UUID>map(GameProfile::getId).orElse(this.player.getUUID());
      PlayerSpiritRecoveryData data = PlayerSpiritRecoveryData.get(serverLevel);
      this.spiritRecoveryCount = data.getSpiritRecoveryCount(playerId);
      this.multiplier = data.getSpiritRecoveryMultiplier(playerId);
      this.heroDiscount = data.getHeroDiscount(playerId);
      if (this.player instanceof ServerPlayer serverPlayer) {
         ModNetwork.CHANNEL
            .sendTo(
               new ClientboundRefreshSpiritExtractorMessage(this.spiritRecoveryCount, this.multiplier, this.heroDiscount),
               serverPlayer.connection.connection,
               NetworkDirection.PLAY_TO_CLIENT
            );
      }
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveOverSizedItemStackTo(slotStack, slot, 36, 37, false)) {
            return ItemStack.EMPTY;
         }

         if (index >= 0 && index < 27) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 36, false)) {
            return ItemStack.EMPTY;
         }

         if (slotStack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (slotStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, slotStack);
      }

      return itemstack;
   }

   public boolean isSpewingItems() {
      return this.tileEntity.isSpewingItems();
   }

   public boolean hasSpirit() {
      return this.tileEntity.getGameProfile().isPresent();
   }

   public void toggleRecycleLock() {
      this.recycleUnlocked = !this.recycleUnlocked;
   }

   public boolean isRecycleUnlocked() {
      return this.recycleUnlocked;
   }

   public int getPlayerLevel() {
      return this.hasSpirit() ? this.tileEntity.getPlayerLevel() : SidedHelper.getVaultLevel(this.player);
   }

   public float getMultiplier() {
      return this.multiplier;
   }

   public boolean isRecyclable() {
      return this.tileEntity.isRecyclable();
   }

   public float getRescuedBonus() {
      return this.tileEntity.getRescuedBonus();
   }
}
