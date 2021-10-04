package iskallia.vault.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class IntegrationCurios {
   public static Collection<CompoundNBT> getSerializedCuriosItemStacks(PlayerEntity player) {
      return player.getCapability(CuriosCapability.INVENTORY).map(inv -> {
         List<CompoundNBT> stacks = new ArrayList<>();

         for (ICurioStacksHandler handle : inv.getCurios().values()) {
            IDynamicStackHandler stackHandler = handle.getStacks();

            for (int index = 0; index < stackHandler.getSlots(); index++) {
               ItemStack stack = stackHandler.getStackInSlot(index);
               if (!stack.func_190926_b()) {
                  stacks.add(stack.serializeNBT());
               }
            }
         }

         return stacks;
      }).orElse(Collections.emptyList());
   }

   public static CompoundNBT getMappedSerializedCuriosItemStacks(
      PlayerEntity player, BiPredicate<PlayerEntity, ItemStack> stackFilter, boolean removeSnapshotItems
   ) {
      return player.getCapability(CuriosCapability.INVENTORY).map(inv -> {
         CompoundNBT tag = new CompoundNBT();
         inv.getCurios().forEach((key, handle) -> {
            CompoundNBT keyMap = new CompoundNBT();
            IDynamicStackHandler stackHandler = handle.getStacks();

            for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
               ItemStack stack = stackHandler.getStackInSlot(slot);
               if (stackFilter.test(player, stack) && !stack.func_190926_b()) {
                  keyMap.func_218657_a(String.valueOf(slot), stack.serializeNBT());
                  if (removeSnapshotItems) {
                     stackHandler.setStackInSlot(slot, ItemStack.field_190927_a);
                  }
               }
            }

            tag.func_218657_a(key, keyMap);
         });
         return tag;
      }).orElse(new CompoundNBT());
   }

   public static List<ItemStack> applyMappedSerializedCuriosItemStacks(PlayerEntity player, CompoundNBT tag, boolean replaceExisting) {
      return player.getCapability(CuriosCapability.INVENTORY).map(inv -> {
         List<ItemStack> filledItems = new ArrayList<>();

         for (String handlerKey : tag.func_150296_c()) {
            inv.getStacksHandler(handlerKey).ifPresent(handle -> {
               IDynamicStackHandler stackHandler = handle.getStacks();
               CompoundNBT handlerKeyMap = tag.func_74775_l(handlerKey);

               for (String strSlot : handlerKeyMap.func_150296_c()) {
                  int slot;
                  try {
                     slot = Integer.parseInt(strSlot);
                  } catch (NumberFormatException var11) {
                     continue;
                  }

                  if (slot >= 0 && slot < stackHandler.getSlots()) {
                     ItemStack stack = ItemStack.func_199557_a(handlerKeyMap.func_74775_l(strSlot));
                     if (!replaceExisting && !stackHandler.getStackInSlot(slot).func_190926_b()) {
                        filledItems.add(stack);
                     } else {
                        stackHandler.setStackInSlot(slot, stack);
                     }
                  }
               }
            });
         }

         return filledItems;
      }).orElse(Collections.emptyList());
   }
}
