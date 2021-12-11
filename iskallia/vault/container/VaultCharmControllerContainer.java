package iskallia.vault.container;

import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.VaultCharmData;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultCharmControllerContainer extends Container {
   public IInventory visibleItems;
   private final int inventorySize;
   private final List<ResourceLocation> whitelist;
   private final int invStartIndex;
   private final int invEndIndex;
   private int currentStart = 0;
   private int currentEnd = 53;
   private float scrollDelta = 0.0F;

   public VaultCharmControllerContainer(int windowId, PlayerInventory playerInventory, CompoundNBT data) {
      super(ModContainers.VAULT_CHARM_CONTROLLER_CONTAINER, windowId);
      VaultCharmData.VaultCharmInventory vaultCharmInventory = VaultCharmData.VaultCharmInventory.fromNbt(data);
      this.inventorySize = vaultCharmInventory.getSize();
      this.whitelist = vaultCharmInventory.getWhitelist();
      this.initVisibleItems();
      this.initPlayerInventorySlots(playerInventory);
      this.initCharmControllerSlots();
      this.invStartIndex = 36;
      this.invEndIndex = 36 + Math.min(54, this.inventorySize);
   }

   private void initVisibleItems() {
      this.visibleItems = new Inventory(this.inventorySize);
      int index = 0;

      for (ResourceLocation id : this.whitelist) {
         this.visibleItems.func_70299_a(index, new ItemStack((IItemProvider)ForgeRegistries.ITEMS.getValue(id)));
         index++;
      }
   }

   private void initPlayerInventorySlots(PlayerInventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.func_75146_a(new Slot(playerInventory, column + row * 9 + 9, 9 + column * 18, 140 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.func_75146_a(new Slot(playerInventory, hotbarSlot, 9 + hotbarSlot * 18, 198));
      }
   }

   private void initCharmControllerSlots() {
      int rows = Math.min(6, this.inventorySize / 9);

      for (int row = 0; row < rows; row++) {
         for (int column = 0; column < 9; column++) {
            this.func_75146_a(new VaultCharmControllerContainer.VaultCharmControllerSlot(this.visibleItems, column + row * 9, 9 + column * 18, 18 + row * 18));
         }
      }
   }

   public boolean canScroll() {
      return this.inventorySize > 54;
   }

   public void scrollTo(float scroll) {
      if (!(scroll >= 1.0F) || !(this.scrollDelta >= 1.0F)) {
         this.shiftInventoryIndexes(this.scrollDelta - scroll < 0.0F);
         this.updateVisibleItems();
         this.scrollDelta = scroll;
      }
   }

   private void shiftInventoryIndexes(boolean ascending) {
      if (ascending) {
         this.currentStart = Math.min(this.inventorySize - 54, this.currentStart + 9);
         this.currentEnd = Math.min(this.currentStart + 54, this.inventorySize);
      } else {
         this.currentStart = Math.max(0, this.currentStart - 9);
         this.currentEnd = Math.max(54, this.currentEnd - 9);
      }
   }

   private void updateVisibleItems() {
      this.visibleItems.func_174888_l();

      for (int i = 0; i < 54; i++) {
         int whitelistIndex = this.currentStart + i;
         if (whitelistIndex >= this.whitelist.size()) {
            this.visibleItems.func_70299_a(i, ItemStack.field_190927_a);
            this.field_75153_a.add(i, ItemStack.field_190927_a);
            break;
         }

         ResourceLocation id = this.whitelist.get(whitelistIndex);
         ItemStack stack = new ItemStack((IItemProvider)ForgeRegistries.ITEMS.getValue(id));
         this.visibleItems.func_70299_a(i, stack);
         this.field_75153_a.add(i, stack);
      }
   }

   public boolean func_75145_c(PlayerEntity playerIn) {
      return true;
   }

   public ItemStack func_82846_b(PlayerEntity playerIn, int index) {
      ItemStack stack = ItemStack.field_190927_a;
      Slot slot = this.func_75139_a(index);
      if (!slot.func_75216_d()) {
         return stack;
      } else {
         ItemStack slotStack = slot.func_75211_c();
         stack = slotStack.func_77946_l();
         if (slot instanceof VaultCharmControllerContainer.VaultCharmControllerSlot) {
            this.whitelist.remove(slot.func_75211_c().func_77973_b().getRegistryName());
            slot.func_75215_d(ItemStack.field_190927_a);
            this.updateVisibleItems();
            return ItemStack.field_190927_a;
         } else if (this.whitelist.size() < this.inventorySize && !this.whitelist.contains(stack.func_77973_b().getRegistryName())) {
            this.whitelist.add(stack.func_77973_b().getRegistryName());
            this.updateVisibleItems();
            return ItemStack.field_190927_a;
         } else {
            if (index >= 0 && index < 27) {
               if (!this.func_75135_a(slotStack, 27, 36, false)) {
                  return ItemStack.field_190927_a;
               }
            } else if (index >= 27 && index < 36) {
               if (!this.func_75135_a(slotStack, 0, 27, false)) {
                  return ItemStack.field_190927_a;
               }
            } else if (!this.func_75135_a(slotStack, 0, 36, false)) {
               return ItemStack.field_190927_a;
            }

            if (slotStack.func_190916_E() == 0) {
               slot.func_75215_d(ItemStack.field_190927_a);
            } else {
               slot.func_75218_e();
            }

            if (slotStack.func_190916_E() == stack.func_190916_E()) {
               return ItemStack.field_190927_a;
            } else {
               slot.func_190901_a(playerIn, slotStack);
               this.updateVisibleItems();
               return stack;
            }
         }
      }
   }

   public ItemStack func_184996_a(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
      Slot slot = slotId >= 0 ? this.func_75139_a(slotId) : null;
      if (slot instanceof VaultCharmControllerContainer.VaultCharmControllerSlot) {
         if (slot.func_75216_d()) {
            this.whitelist.remove(slot.func_75211_c().func_77973_b().getRegistryName());
            slot.func_75215_d(ItemStack.field_190927_a);
            this.updateVisibleItems();
            return ItemStack.field_190927_a;
         }

         if (!player.field_71071_by.func_70445_o().func_190926_b()) {
            ItemStack stack = player.field_71071_by.func_70445_o().func_77946_l();
            if (!this.whitelist.contains(stack.func_77973_b().getRegistryName())) {
               this.whitelist.add(stack.func_77973_b().getRegistryName());
               this.updateVisibleItems();
               return ItemStack.field_190927_a;
            }
         }
      }

      return super.func_184996_a(slotId, dragType, clickTypeIn, player);
   }

   public boolean func_94530_a(ItemStack stack, Slot slot) {
      return slot.field_75222_d >= this.invStartIndex ? false : super.func_94530_a(stack, slot);
   }

   public void func_75134_a(PlayerEntity player) {
      if (player instanceof ServerPlayerEntity) {
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)player;
         VaultCharmData.get(sPlayer.func_71121_q()).updateWhitelist(sPlayer, this.whitelist);
      }

      super.func_75134_a(player);
   }

   public int getInventorySize() {
      return this.inventorySize;
   }

   public class VaultCharmControllerSlot extends Slot {
      public VaultCharmControllerSlot(IInventory inventory, int index, int xPosition, int yPosition) {
         super(inventory, index, xPosition, yPosition);
      }

      public boolean func_75214_a(@Nonnull ItemStack stack) {
         if (this.func_75216_d()) {
            return false;
         } else if (stack.func_77973_b() == ModItems.VAULT_CHARM) {
            return false;
         } else {
            ResourceLocation id = stack.func_77973_b().getRegistryName();
            return !VaultCharmControllerContainer.this.whitelist.contains(id);
         }
      }

      public int func_75219_a() {
         return 1;
      }
   }
}
