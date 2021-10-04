package iskallia.vault.container.inventory;

import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.container.slot.player.ArmorEditSlot;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModContainers;
import iskallia.vault.world.data.EternalsData;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CryochamberContainer extends Container {
   private final BlockPos tilePos;

   public CryochamberContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory) {
      super(ModContainers.CRYOCHAMBER_CONTAINER, windowId);
      this.tilePos = pos;
      CryoChamberTileEntity cryoChamber = this.getCryoChamber(world);
      IInventory equipmentInventory;
      if (world instanceof ServerWorld && cryoChamber != null) {
         equipmentInventory = EternalsData.get((ServerWorld)world).getEternalEquipmentInventory(cryoChamber.getEternalId(), cryoChamber::sendUpdates);
         if (equipmentInventory == null) {
            return;
         }
      } else {
         equipmentInventory = new Inventory(5);
      }

      this.initSlots(equipmentInventory, playerInventory);
   }

   private void initSlots(IInventory equipmentInventory, PlayerInventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.func_75146_a(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 129 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.func_75146_a(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 187));
      }

      this.func_75146_a(new ArmorEditSlot(equipmentInventory, EquipmentSlotType.MAINHAND, 0, 151, 26));
      int offsetY = 98;
      int index = 1;

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         if (slot.func_188453_a() != Group.HAND) {
            this.func_75146_a(new ArmorEditSlot(equipmentInventory, slot, index, 151, offsetY));
            offsetY -= 18;
            index++;
         }
      }
   }

   public boolean func_75145_c(PlayerEntity player) {
      return this.getCryoChamber(player.func_130014_f_()) == null ? false : player.func_195048_a(Vector3d.func_237489_a_(this.tilePos)) <= 64.0;
   }

   @Nullable
   public CryoChamberTileEntity getCryoChamber(World world) {
      BlockState state = world.func_180495_p(this.tilePos);
      return !state.func_203425_a(ModBlocks.CRYO_CHAMBER) ? null : CryoChamberBlock.getCryoChamberTileEntity(world, this.tilePos, state);
   }

   public ItemStack func_82846_b(PlayerEntity playerIn, int index) {
      ItemStack itemstack = ItemStack.field_190927_a;
      Slot slot = (Slot)this.field_75151_b.get(index);
      if (slot != null && slot.func_75216_d()) {
         ItemStack slotStack = slot.func_75211_c();
         itemstack = slotStack.func_77946_l();
         if (index >= 0 && index < 36 && this.func_75135_a(slotStack, 36, 41, false)) {
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
