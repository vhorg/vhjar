package iskallia.vault.container.inventory;

import iskallia.vault.block.entity.CatalystDecryptionTableTileEntity;
import iskallia.vault.container.slot.FilteredSlot;
import iskallia.vault.init.ModContainers;
import iskallia.vault.item.VaultCatalystItem;
import iskallia.vault.item.VaultInhibitorItem;
import iskallia.vault.item.crystal.VaultCrystalItem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CatalystDecryptionContainer extends Container {
   private final BlockPos tilePos;
   private final List<Slot> catalystSlots = new ArrayList<>();

   public CatalystDecryptionContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory) {
      super(ModContainers.CATALYST_DECRYPTION_CONTAINER, windowId);
      this.tilePos = pos;
      TileEntity te = world.func_175625_s(pos);
      if (te instanceof CatalystDecryptionTableTileEntity) {
         te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(tableInventory -> this.initSlots(tableInventory, playerInventory));
      }
   }

   private void initSlots(IItemHandler tableInventory, PlayerInventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.func_75146_a(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 152 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.func_75146_a(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 210));
      }

      Predicate<ItemStack> catalystFilter = stack -> stack.func_77973_b() instanceof VaultCatalystItem || stack.func_77973_b() instanceof VaultInhibitorItem;
      Predicate<ItemStack> crystalFilter = stack -> stack.func_77973_b() instanceof VaultCrystalItem;

      for (int slotY = 0; slotY < 5; slotY++) {
         this.addCatalystSlot(new FilteredSlot(tableInventory, slotY * 2, 56, 15 + slotY * 26, catalystFilter));
         this.addCatalystSlot(new FilteredSlot(tableInventory, slotY * 2 + 1, 104, 15 + slotY * 26, catalystFilter));
      }

      this.func_75146_a(new FilteredSlot(tableInventory, 10, 80, 67, crystalFilter));
   }

   private void addCatalystSlot(Slot slot) {
      this.catalystSlots.add(slot);
      this.func_75146_a(slot);
   }

   public List<Slot> getCatalystSlots() {
      return this.catalystSlots;
   }

   public boolean func_75145_c(PlayerEntity player) {
      World world = player.func_130014_f_();
      return !(world.func_175625_s(this.tilePos) instanceof CatalystDecryptionTableTileEntity)
         ? false
         : player.func_70092_e(this.tilePos.func_177958_n() + 0.5, this.tilePos.func_177956_o() + 0.5, this.tilePos.func_177952_p() + 0.5) <= 64.0;
   }

   public ItemStack func_82846_b(PlayerEntity playerIn, int index) {
      ItemStack itemstack = ItemStack.field_190927_a;
      Slot slot = (Slot)this.field_75151_b.get(index);
      if (slot != null && slot.func_75216_d()) {
         ItemStack slotStack = slot.func_75211_c();
         itemstack = slotStack.func_77946_l();
         if (index >= 0 && index < 36 && this.func_75135_a(slotStack, 36, 47, false)) {
            return itemstack;
         }

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

         if (slotStack.func_190916_E() == itemstack.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         slot.func_190901_a(playerIn, slotStack);
      }

      return itemstack;
   }
}
