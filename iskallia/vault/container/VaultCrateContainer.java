package iskallia.vault.container;

import iskallia.vault.init.ModContainers;
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
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class VaultCrateContainer extends Container {
   public IItemHandler crateInventory;
   private PlayerEntity playerEntity;
   private IItemHandler playerInventory;
   private TileEntity tileEntity;

   public VaultCrateContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
      super(ModContainers.VAULT_CRATE_CONTAINER, windowId);
      this.tileEntity = world.func_175625_s(pos);
      this.playerEntity = player;
      this.playerInventory = new InvWrapper(playerInventory);
      if (this.tileEntity != null) {
         this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            this.crateInventory = h;
            int i = 36;

            for (int j = 0; j < 6; j++) {
               for (int k = 0; k < 9; k++) {
                  this.func_75146_a(new SlotItemHandler(h, k + j * 9, 8 + k * 18, 18 + j * 18));
               }
            }

            for (int l = 0; l < 3; l++) {
               for (int j1 = 0; j1 < 9; j1++) {
                  this.func_75146_a(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
               }
            }

            for (int i1 = 0; i1 < 9; i1++) {
               this.func_75146_a(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
            }
         });
      }
   }

   public ItemStack func_82846_b(PlayerEntity playerIn, int index) {
      ItemStack stack = ItemStack.field_190927_a;
      Slot slot = (Slot)this.field_75151_b.get(index);
      if (slot != null && slot.func_75216_d()) {
         ItemStack stackInSlot = slot.func_75211_c();
         stack = stackInSlot.func_77946_l();
         if (index < this.crateInventory.getSlots()) {
            if (!this.func_75135_a(stackInSlot, this.crateInventory.getSlots(), this.field_75151_b.size(), true)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.func_75135_a(stackInSlot, 0, this.crateInventory.getSlots(), false)) {
            return ItemStack.field_190927_a;
         }

         if (stackInSlot.func_190926_b()) {
            slot.func_75215_d(ItemStack.field_190927_a);
         } else {
            slot.func_75218_e();
         }
      }

      return stack;
   }

   public boolean func_75145_c(PlayerEntity player) {
      return true;
   }

   private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
      for (int j = 0; j < verAmount; j++) {
         index = this.addSlotRange(handler, index, x, y, horAmount, dx);
         y += dy;
      }

      return index;
   }

   private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
      for (int i = 0; i < amount; i++) {
         this.func_75146_a(new SlotItemHandler(handler, index, x, y));
         x += dx;
         index++;
      }

      return index;
   }
}
