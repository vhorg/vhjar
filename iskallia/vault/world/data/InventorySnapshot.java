package iskallia.vault.world.data;

import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.integration.IntegrationSB;
import iskallia.vault.nbt.VListNBT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.ModList;

public class InventorySnapshot implements INBTSerializable<CompoundTag> {
   private final VListNBT<Integer, IntTag> invIds = new VListNBT<>(IntTag::valueOf, IntTag::getAsInt);
   private final VListNBT<ItemStack, CompoundTag> items = new VListNBT<>(IForgeItemStack::serializeNBT, ItemStack::of);
   private final Map<String, CompoundTag> customInventoryData = new HashMap<>();
   private boolean removeSnapshotItems = false;
   private boolean replaceExisting = false;

   protected InventorySnapshot() {
   }

   public InventorySnapshot(boolean removeSnapshotItems, boolean replaceExisting) {
      this.removeSnapshotItems = removeSnapshotItems;
      this.replaceExisting = replaceExisting;
   }

   private void createSnapshot(Player player, BiPredicate<Player, ItemStack> stackFilter) {
      for (int slot = 0; slot < ((InventorySnapshotData.InventoryAccessor)player.getInventory()).getSize(); slot++) {
         ItemStack stack = player.getInventory().getItem(slot);
         if (stackFilter.test(player, stack)) {
            this.addItemStack(slot, stack);
            if (this.removeSnapshotItems) {
               player.getInventory().setItem(slot, ItemStack.EMPTY);
            }
         }
      }

      if (ModList.get().isLoaded("curios")) {
         CompoundTag curiosData = IntegrationCurios.getMappedSerializedCuriosItemStacks(player, stackFilter, this.removeSnapshotItems);
         this.customInventoryData.put("curios", curiosData);
      }
   }

   private void addItemStack(int slot, ItemStack stack) {
      this.invIds.add(slot);
      ItemStack stackCopy = stack.copy();
      if (ModList.get().isLoaded("sophisticatedbackpacksvh")) {
         IntegrationSB.addSnapshotDataIfBackpack(stackCopy);
      }

      this.items.add(stackCopy);
   }

   public boolean apply(Player player) {
      if (!player.isAlive()) {
         return false;
      } else {
         List<ItemStack> addLater = new ArrayList<>();

         for (int index = 0; index < this.items.size(); index++) {
            ItemStack toAdd = this.items.get(index).copy();
            if (ModList.get().isLoaded("sophisticatedbackpacksvh")) {
               IntegrationSB.restoreSnapshotIfBackpack(toAdd);
            }

            int slot = this.invIds.get(index);
            if (!this.replaceExisting && !player.getInventory().getItem(slot).isEmpty()) {
               addLater.add(toAdd);
            } else {
               player.getInventory().setItem(slot, toAdd);
            }
         }

         if (ModList.get().isLoaded("curios") && this.customInventoryData.containsKey("curios")) {
            CompoundTag curiosData = this.customInventoryData.get("curios");
            addLater.addAll(IntegrationCurios.applyMappedSerializedCuriosItemStacks(player, curiosData, this.replaceExisting));
         }

         for (ItemStack stack : addLater) {
            if (!player.addItem(stack)) {
               ItemEntity itementity = player.drop(stack, false);
               if (itementity != null) {
                  itementity.setNoPickUpDelay();
                  itementity.setOwner(player.getUUID());
               }
            }
         }

         return true;
      }
   }

   public List<ItemStack> getItems() {
      return this.items;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.put("InvIds", this.invIds.serializeNBT());
      nbt.put("Items", this.items.serializeNBT());
      CompoundTag customData = new CompoundTag();
      this.customInventoryData.forEach(customData::put);
      nbt.put("customData", customData);
      nbt.putBoolean("removeSnapshotItems", this.removeSnapshotItems);
      nbt.putBoolean("replaceExisting", this.replaceExisting);
      return (R)nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.invIds.deserializeNBT(nbt.getList("InvIds", 3));
      this.items.deserializeNBT(nbt.getList("Items", 10));
      this.customInventoryData.clear();
      if (nbt.contains("customData", 10)) {
         CompoundTag customData = nbt.getCompound("customData");

         for (String key : customData.getAllKeys()) {
            this.customInventoryData.put(key, customData.getCompound(key));
         }
      }

      this.removeSnapshotItems = nbt.getBoolean("removeSnapshotItems");
      this.replaceExisting = nbt.getBoolean("replaceExisting");
   }

   public static class Builder {
      private final Player player;
      private boolean removeSnapshotItems = false;
      private boolean replaceExisting = false;
      private BiPredicate<Player, ItemStack> stackFilter = (playerx, stack) -> true;

      public Builder(Player player) {
         this.player = player;
      }

      public InventorySnapshot.Builder removeSnapshotItems() {
         this.removeSnapshotItems = true;
         return this;
      }

      public InventorySnapshot.Builder replaceExisting() {
         this.replaceExisting = true;
         return this;
      }

      public InventorySnapshot.Builder setStackFilter(BiPredicate<Player, ItemStack> stackFilter) {
         this.stackFilter = stackFilter;
         return this;
      }

      public InventorySnapshot createSnapshot() {
         InventorySnapshot snapshot = new InventorySnapshot(this.removeSnapshotItems, this.replaceExisting);
         snapshot.createSnapshot(this.player, this.stackFilter);
         return snapshot;
      }
   }
}
