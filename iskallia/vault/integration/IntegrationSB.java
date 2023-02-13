package iskallia.vault.integration;

import iskallia.vault.util.InventoryUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;

public class IntegrationSB {
   private static final String VH_SNAPSHOT_TAG = "VHSnapshot";

   private IntegrationSB() {
   }

   public static void addSnapshotDataIfBackpack(ItemStack stack) {
      if (stack.getItem() instanceof BackpackItem) {
         stack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance())
            .ifPresent(
               backpackWrapper -> backpackWrapper.getContentsUuid()
                  .ifPresent(backpackUuid -> stack.getTag().put("VHSnapshot", BackpackStorage.get().getOrCreateBackpackContents(backpackUuid).copy()))
            );
      }
   }

   public static void restoreSnapshotIfBackpack(ItemStack stack) {
      stack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).ifPresent(backpackWrapper -> {
         backpackWrapper.getContentsUuid().ifPresent(backpackUuid -> {
            if (stack.getTag() != null && stack.getTag().contains("VHSnapshot")) {
               CompoundTag tag = stack.getTag().getCompound("VHSnapshot");
               BackpackStorage storage = BackpackStorage.get();
               if (tag.isEmpty()) {
                  storage.removeBackpackContents(backpackUuid);
               } else {
                  storage.setBackpackContents(backpackUuid, tag);
               }

               stack.getTag().remove("VHSnapshot");
            }
         });
         backpackWrapper.onContentsNbtUpdated();
      });
   }

   public static List<InventoryUtil.ItemAccess> getBackpackItemAccess(InventoryUtil.ItemAccess backpackAccess) {
      return backpackAccess.getStack().getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).map(wrapper -> {
         List<InventoryUtil.ItemAccess> accesses = new ArrayList<>();
         InventoryHandler invHandler = wrapper.getInventoryHandler();

         for (int slot = 0; slot < invHandler.getSlots(); slot++) {
            ItemStack slotStack = invHandler.getStackInSlot(slot);
            if (!slotStack.isEmpty()) {
               int finalSlot = slot;
               accesses.add(backpackAccess.chain(slotStack, (backpackCt, newStack) -> setBackpackItem(backpackCt, newStack, finalSlot)));
            }
         }

         return accesses;
      }).orElse(Collections.emptyList());
   }

   public static boolean setBackpackItem(ItemStack container, ItemStack toInsert, int slotId) {
      return container.getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).map(wrapper -> {
         InventoryHandler invHandler = wrapper.getInventoryHandler();
         if (slotId >= invHandler.getSlots()) {
            return false;
         } else {
            invHandler.setStackInSlot(slotId, toInsert);
            return true;
         }
      }).orElse(false);
   }
}
