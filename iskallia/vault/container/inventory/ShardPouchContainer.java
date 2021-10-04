package iskallia.vault.container.inventory;

import com.google.common.collect.Sets;
import iskallia.vault.container.slot.ConditionalReadSlot;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.ItemShardPouch;
import iskallia.vault.network.message.SyncOversizedStackMessage;
import iskallia.vault.util.ServerScheduler;
import java.util.Set;
import java.util.function.BiPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ShardPouchContainer extends Container {
   private final int pouchSlot;
   private final PlayerInventory inventory;
   private int dragMode = -1;
   private int dragEvent;
   private final Set<Slot> dragSlots = Sets.newHashSet();

   public ShardPouchContainer(int id, PlayerInventory inventory, int pouchSlot) {
      super(ModContainers.SHARD_POUCH_CONTAINER, id);
      this.inventory = inventory;
      this.pouchSlot = pouchSlot;
      if (this.hasPouch()) {
         inventory.field_70458_d.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(playerInvHandler -> {
            ItemStack pouch = this.inventory.func_70301_a(this.pouchSlot);
            pouch.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(pouchHandler -> this.initSlots(playerInvHandler, pouchHandler));
         });
      }
   }

   private void initSlots(IItemHandler playerInvHandler, final IItemHandler pouchHandler) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.func_75146_a(new ConditionalReadSlot(playerInvHandler, column + row * 9 + 9, 8 + column * 18, 55 + row * 18, this::canAccess));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.func_75146_a(new ConditionalReadSlot(playerInvHandler, hotbarSlot, 8 + hotbarSlot * 18, 113, this::canAccess));
      }

      this.func_75146_a(
         new ConditionalReadSlot(pouchHandler, 0, 80, 16, (slot, stack) -> this.canAccess(slot, stack) && stack.func_77973_b() == ModItems.SOUL_SHARD) {
            public int func_178170_b(@Nonnull ItemStack stack) {
               return pouchHandler.getSlotLimit(0);
            }

            public void func_75218_e() {
               ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(this.getSlotIndex(), this.func_75211_c());
            }
         }
      );
   }

   public boolean func_75145_c(PlayerEntity player) {
      return this.hasPouch();
   }

   public boolean canAccess(int slot, ItemStack slotStack) {
      return this.hasPouch() && !(slotStack.func_77973_b() instanceof ItemShardPouch);
   }

   public boolean hasPouch() {
      ItemStack pouchStack = this.inventory.func_70301_a(this.pouchSlot);
      return !pouchStack.func_190926_b() && pouchStack.func_77973_b() instanceof ItemShardPouch;
   }

   public ItemStack func_82846_b(PlayerEntity playerIn, int index) {
      ItemStack itemstack = ItemStack.field_190927_a;
      Slot slot = (Slot)this.field_75151_b.get(index);
      if (slot != null && slot.func_75216_d()) {
         ItemStack slotStack = slot.func_75211_c();
         itemstack = slotStack.func_77946_l();
         if (index >= 0 && index < 36 && this.func_75135_a(slotStack, 36, 37, false)) {
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

   @Nonnull
   public ItemStack func_184996_a(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
      ItemStack returnStack = ItemStack.field_190927_a;
      PlayerInventory PlayerInventory = player.field_71071_by;
      if (clickTypeIn == ClickType.QUICK_CRAFT) {
         int j1 = this.dragEvent;
         this.dragEvent = func_94532_c(dragType);
         if ((j1 != 1 || this.dragEvent != 2) && j1 != this.dragEvent) {
            this.func_94533_d();
         } else if (PlayerInventory.func_70445_o().func_190926_b()) {
            this.func_94533_d();
         } else if (this.dragEvent == 0) {
            this.dragMode = func_94529_b(dragType);
            if (func_180610_a(this.dragMode, player)) {
               this.dragEvent = 1;
               this.dragSlots.clear();
            } else {
               this.func_94533_d();
            }
         } else if (this.dragEvent == 1) {
            Slot slot = (Slot)this.field_75151_b.get(slotId);
            ItemStack mouseStack = PlayerInventory.func_70445_o();
            if (slot != null
               && canAddItemToSlot(slot, mouseStack, true)
               && slot.func_75214_a(mouseStack)
               && (this.dragMode == 2 || mouseStack.func_190916_E() > this.dragSlots.size())
               && this.func_94531_b(slot)) {
               this.dragSlots.add(slot);
            }
         } else if (this.dragEvent == 2) {
            if (!this.dragSlots.isEmpty()) {
               ItemStack mouseStackCopy = PlayerInventory.func_70445_o().func_77946_l();
               int k1 = PlayerInventory.func_70445_o().func_190916_E();

               for (Slot dragSlot : this.dragSlots) {
                  ItemStack mouseStack = PlayerInventory.func_70445_o();
                  if (dragSlot != null
                     && canAddItemToSlot(dragSlot, mouseStack, true)
                     && dragSlot.func_75214_a(mouseStack)
                     && (this.dragMode == 2 || mouseStack.func_190916_E() >= this.dragSlots.size())
                     && this.func_94531_b(dragSlot)) {
                     ItemStack itemstack14 = mouseStackCopy.func_77946_l();
                     int j3 = dragSlot.func_75216_d() ? dragSlot.func_75211_c().func_190916_E() : 0;
                     func_94525_a(this.dragSlots, this.dragMode, itemstack14, j3);
                     int k3 = dragSlot.func_178170_b(itemstack14);
                     if (itemstack14.func_190916_E() > k3) {
                        itemstack14.func_190920_e(k3);
                     }

                     k1 -= itemstack14.func_190916_E() - j3;
                     dragSlot.func_75215_d(itemstack14);
                  }
               }

               mouseStackCopy.func_190920_e(k1);
               PlayerInventory.func_70437_b(mouseStackCopy);
            }

            this.func_94533_d();
         } else {
            this.func_94533_d();
         }
      } else if (this.dragEvent != 0) {
         this.func_94533_d();
      } else if ((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1)) {
         if (slotId == -999) {
            if (!PlayerInventory.func_70445_o().func_190926_b()) {
               if (dragType == 0) {
                  player.func_71019_a(PlayerInventory.func_70445_o(), true);
                  PlayerInventory.func_70437_b(ItemStack.field_190927_a);
               }

               if (dragType == 1) {
                  player.func_71019_a(PlayerInventory.func_70445_o().func_77979_a(1), true);
               }
            }
         } else if (clickTypeIn == ClickType.QUICK_MOVE) {
            if (slotId < 0) {
               return ItemStack.field_190927_a;
            }

            Slot slot = (Slot)this.field_75151_b.get(slotId);
            if (slot == null || !slot.func_82869_a(player)) {
               return ItemStack.field_190927_a;
            }

            for (ItemStack itemstack7 = this.func_82846_b(player, slotId);
               !itemstack7.func_190926_b() && ItemStack.func_179545_c(slot.func_75211_c(), itemstack7);
               itemstack7 = this.func_82846_b(player, slotId)
            ) {
               returnStack = itemstack7.func_77946_l();
            }
         } else {
            if (slotId < 0) {
               return ItemStack.field_190927_a;
            }

            Slot slot = (Slot)this.field_75151_b.get(slotId);
            if (slot != null) {
               ItemStack slotStack = slot.func_75211_c();
               ItemStack mouseStack = PlayerInventory.func_70445_o();
               if (!slotStack.func_190926_b()) {
                  returnStack = slotStack.func_77946_l();
               }

               if (slotStack.func_190926_b()) {
                  if (!mouseStack.func_190926_b() && slot.func_75214_a(mouseStack)) {
                     int i3 = dragType == 0 ? mouseStack.func_190916_E() : 1;
                     if (i3 > slot.func_178170_b(mouseStack)) {
                        i3 = slot.func_178170_b(mouseStack);
                     }

                     slot.func_75215_d(mouseStack.func_77979_a(i3));
                  }
               } else if (slot.func_82869_a(player)) {
                  if (mouseStack.func_190926_b()) {
                     if (slotStack.func_190926_b()) {
                        slot.func_75215_d(ItemStack.field_190927_a);
                        PlayerInventory.func_70437_b(ItemStack.field_190927_a);
                     } else {
                        int toMove = dragType == 0 ? slotStack.func_190916_E() : (slotStack.func_190916_E() + 1) / 2;
                        PlayerInventory.func_70437_b(slot.func_75209_a(toMove));
                        if (slotStack.func_190926_b()) {
                           slot.func_75215_d(ItemStack.field_190927_a);
                        }

                        slot.func_190901_a(player, PlayerInventory.func_70445_o());
                     }
                  } else if (slot.func_75214_a(mouseStack)) {
                     if (slotStack.func_77973_b() == mouseStack.func_77973_b() && ItemStack.func_77970_a(slotStack, mouseStack)) {
                        int k2 = dragType == 0 ? mouseStack.func_190916_E() : 1;
                        if (k2 > slot.func_178170_b(mouseStack) - slotStack.func_190916_E()) {
                           k2 = slot.func_178170_b(mouseStack) - slotStack.func_190916_E();
                        }

                        mouseStack.func_190918_g(k2);
                        slotStack.func_190917_f(k2);
                     } else if (mouseStack.func_190916_E() <= slot.func_178170_b(mouseStack) && slotStack.func_190916_E() <= slotStack.func_77976_d()) {
                        slot.func_75215_d(mouseStack);
                        PlayerInventory.func_70437_b(slotStack);
                     }
                  } else if (slotStack.func_77973_b() == mouseStack.func_77973_b()
                     && mouseStack.func_77976_d() > 1
                     && ItemStack.func_77970_a(slotStack, mouseStack)
                     && !slotStack.func_190926_b()) {
                     int j2 = slotStack.func_190916_E();
                     if (j2 + mouseStack.func_190916_E() <= mouseStack.func_77976_d()) {
                        mouseStack.func_190917_f(j2);
                        slotStack = slot.func_75209_a(j2);
                        if (slotStack.func_190926_b()) {
                           slot.func_75215_d(ItemStack.field_190927_a);
                        }

                        slot.func_190901_a(player, PlayerInventory.func_70445_o());
                     }
                  }
               }

               slot.func_75218_e();
            }
         }
      } else if (clickTypeIn != ClickType.SWAP || dragType < 0 || dragType >= 9) {
         if (clickTypeIn == ClickType.CLONE && player.field_71075_bZ.field_75098_d && PlayerInventory.func_70445_o().func_190926_b() && slotId >= 0) {
            Slot slot3 = (Slot)this.field_75151_b.get(slotId);
            if (slot3 != null && slot3.func_75216_d()) {
               ItemStack itemstack5 = slot3.func_75211_c().func_77946_l();
               itemstack5.func_190920_e(itemstack5.func_77976_d());
               PlayerInventory.func_70437_b(itemstack5);
            }
         } else if (clickTypeIn == ClickType.THROW && PlayerInventory.func_70445_o().func_190926_b() && slotId >= 0) {
            Slot slot = (Slot)this.field_75151_b.get(slotId);
            if (slot != null && slot.func_75216_d() && slot.func_82869_a(player)) {
               ItemStack itemstack4 = slot.func_75209_a(dragType == 0 ? 1 : slot.func_75211_c().func_190916_E());
               slot.func_190901_a(player, itemstack4);
               player.func_71019_a(itemstack4, true);
            }
         } else if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0) {
            Slot slot = (Slot)this.field_75151_b.get(slotId);
            ItemStack mouseStackx = PlayerInventory.func_70445_o();
            if (!mouseStackx.func_190926_b() && (slot == null || !slot.func_75216_d() || !slot.func_82869_a(player))) {
               int i = dragType == 0 ? 0 : this.field_75151_b.size() - 1;
               int j = dragType == 0 ? 1 : -1;

               for (int k = 0; k < 2; k++) {
                  for (int l = i; l >= 0 && l < this.field_75151_b.size() && mouseStackx.func_190916_E() < mouseStackx.func_77976_d(); l += j) {
                     Slot slot1 = (Slot)this.field_75151_b.get(l);
                     if (slot1.func_75216_d()
                        && canAddItemToSlot(slot1, mouseStackx, true)
                        && slot1.func_82869_a(player)
                        && this.func_94530_a(mouseStackx, slot1)) {
                        ItemStack itemstack2 = slot1.func_75211_c();
                        if (k != 0 || itemstack2.func_190916_E() < slot1.func_178170_b(itemstack2)) {
                           int i1 = Math.min(mouseStackx.func_77976_d() - mouseStackx.func_190916_E(), itemstack2.func_190916_E());
                           ItemStack itemstack3 = slot1.func_75209_a(i1);
                           mouseStackx.func_190917_f(i1);
                           if (itemstack3.func_190926_b()) {
                              slot1.func_75215_d(ItemStack.field_190927_a);
                           }

                           slot1.func_190901_a(player, itemstack3);
                        }
                     }
                  }
               }
            }

            this.func_75142_b();
         }
      }

      if (returnStack.func_190916_E() > 64) {
         returnStack = returnStack.func_77946_l();
         returnStack.func_190920_e(64);
      }

      return returnStack;
   }

   protected void func_94533_d() {
      this.dragEvent = 0;
      this.dragSlots.clear();
   }

   public void func_75142_b() {
      for (int i = 0; i < this.field_75151_b.size(); i++) {
         ItemStack itemstack = ((Slot)this.field_75151_b.get(i)).func_75211_c();
         ItemStack itemstack1 = (ItemStack)this.field_75153_a.get(i);
         if (!ItemStack.func_77989_b(itemstack1, itemstack)) {
            boolean clientStackChanged = !itemstack1.equals(itemstack, true);
            ItemStack itemstack2 = itemstack.func_77946_l();
            this.field_75153_a.set(i, itemstack2);
            if (clientStackChanged) {
               for (IContainerListener icontainerlistener : this.field_75149_d) {
                  if (icontainerlistener instanceof ServerPlayerEntity && i == 36) {
                     ServerPlayerEntity playerEntity = (ServerPlayerEntity)icontainerlistener;
                     ModNetwork.CHANNEL
                        .sendTo(
                           new SyncOversizedStackMessage(this.field_75152_c, i, itemstack1),
                           playerEntity.field_71135_a.func_147298_b(),
                           NetworkDirection.PLAY_TO_CLIENT
                        );
                  } else {
                     icontainerlistener.func_71111_a(this, i, itemstack2);
                  }
               }
            }
         }
      }

      for (int j = 0; j < this.field_216964_d.size(); j++) {
         IntReferenceHolder intreferenceholder = (IntReferenceHolder)this.field_216964_d.get(j);
         if (intreferenceholder.func_221496_c()) {
            for (IContainerListener icontainerlistener1 : this.field_75149_d) {
               icontainerlistener1.func_71112_a(this, j, intreferenceholder.func_221495_b());
            }
         }
      }
   }

   public void func_75132_a(IContainerListener listener) {
      if (!this.field_75149_d.contains(listener)) {
         this.field_75149_d.add(listener);
         if (!(listener instanceof ServerPlayerEntity)) {
            listener.func_71110_a(this, this.func_75138_a());
         } else {
            ServerPlayerEntity player = (ServerPlayerEntity)listener;

            for (int i = 0; i < this.field_75151_b.size(); i++) {
               ItemStack stack = ((Slot)this.field_75151_b.get(i)).func_75211_c();
               int slotIndex = i;
               ServerScheduler.INSTANCE
                  .schedule(
                     0,
                     () -> ModNetwork.CHANNEL
                        .sendTo(
                           new SyncOversizedStackMessage(this.field_75152_c, slotIndex, stack),
                           player.field_71135_a.func_147298_b(),
                           NetworkDirection.PLAY_TO_CLIENT
                        )
                  );
            }

            player.field_71135_a.func_147359_a(new SSetSlotPacket(-1, -1, player.field_71071_by.func_70445_o()));
         }

         this.func_75142_b();
      }
   }

   protected boolean func_75135_a(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
      boolean flag = false;
      int i = startIndex;
      if (reverseDirection) {
         i = endIndex - 1;
      }

      for (; !stack.func_190926_b() && (reverseDirection ? i >= startIndex : i < endIndex); i += reverseDirection ? -1 : 1) {
         Slot slot = (Slot)this.field_75151_b.get(i);
         ItemStack slotStack = slot.func_75211_c();
         if (!slotStack.func_190926_b() && slotStack.func_77973_b() == stack.func_77973_b() && ItemStack.func_77970_a(stack, slotStack)) {
            int j = slotStack.func_190916_E() + stack.func_190916_E();
            int maxSize = slot.func_178170_b(slotStack);
            if (j <= maxSize) {
               stack.func_190920_e(0);
               slotStack.func_190920_e(j);
               slot.func_75218_e();
               flag = true;
            } else if (slotStack.func_190916_E() < maxSize) {
               stack.func_190918_g(maxSize - slotStack.func_190916_E());
               slotStack.func_190920_e(maxSize);
               slot.func_75218_e();
               flag = true;
            }
         }
      }

      if (!stack.func_190926_b()) {
         if (reverseDirection) {
            i = endIndex - 1;
         } else {
            i = startIndex;
         }

         while (reverseDirection ? i >= startIndex : i < endIndex) {
            Slot slot1 = (Slot)this.field_75151_b.get(i);
            ItemStack itemstack1 = slot1.func_75211_c();
            if (itemstack1.func_190926_b() && slot1.func_75214_a(stack)) {
               if (stack.func_190916_E() > slot1.func_178170_b(stack)) {
                  slot1.func_75215_d(stack.func_77979_a(slot1.func_178170_b(stack)));
               } else {
                  slot1.func_75215_d(stack.func_77979_a(stack.func_190916_E()));
               }

               slot1.func_75218_e();
               flag = true;
               break;
            }

            i += reverseDirection ? -1 : 1;
         }
      }

      return flag;
   }

   public static boolean canAddItemToSlot(@Nullable Slot slot, @Nonnull ItemStack stack, boolean stackSizeMatters) {
      boolean flag = slot == null || !slot.func_75216_d();
      if (slot != null) {
         ItemStack slotStack = slot.func_75211_c();
         if (!flag && stack.func_77969_a(slotStack) && ItemStack.func_77970_a(slotStack, stack)) {
            return slotStack.func_190916_E() + (stackSizeMatters ? 0 : stack.func_190916_E()) <= slot.func_178170_b(slotStack);
         }
      }

      return flag;
   }
}
