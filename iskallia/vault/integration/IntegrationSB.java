package iskallia.vault.integration;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;

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
}
