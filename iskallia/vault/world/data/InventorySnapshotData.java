package iskallia.vault.world.data;

import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.integration.IntegrationSB;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.nbt.VMapNBT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.ModList;

public abstract class InventorySnapshotData extends SavedData {
   public VMapNBT<UUID, InventorySnapshotData.InventorySnapshot> snapshotData = VMapNBT.ofUUID(InventorySnapshotData.InventorySnapshot::new);

   protected abstract boolean shouldSnapshotItem(Player var1, ItemStack var2);

   protected InventorySnapshotData.Builder makeSnapshotBuilder(Player player) {
      return new InventorySnapshotData.Builder(player).setStackFilter(this::shouldSnapshotItem).removeSnapshotItems();
   }

   public boolean hasSnapshot(Player player) {
      return this.hasSnapshot(player.getUUID());
   }

   public boolean hasSnapshot(UUID playerUUID) {
      return this.snapshotData.containsKey(playerUUID);
   }

   public void createSnapshot(Player player) {
      if (this.snapshotData.containsKey(player.getUUID())) {
         this.restoreSnapshot(player);
      }

      this.snapshotData.put(player.getUUID(), this.makeSnapshotBuilder(player).createSnapshot());
      this.setDirty();
   }

   public boolean removeSnapshot(Player player) {
      return this.removeSnapshot(player.getUUID());
   }

   public boolean removeSnapshot(UUID playerUUID) {
      if (this.snapshotData.remove(playerUUID) != null) {
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public boolean restoreSnapshot(Player player) {
      InventorySnapshotData.InventorySnapshot snapshot = this.snapshotData.remove(player.getUUID());
      if (snapshot != null) {
         this.setDirty();
         return snapshot.apply(player);
      } else {
         return false;
      }
   }

   public void load(CompoundTag nbt) {
      this.snapshotData.deserializeNBT(nbt.getList("Players", 10));
   }

   public CompoundTag save(CompoundTag compound) {
      compound.put("Players", this.snapshotData.serializeNBT());
      return compound;
   }

   public static class Builder {
      private final Player player;
      private boolean removeSnapshotItems = false;
      private boolean replaceExisting = false;
      private BiPredicate<Player, ItemStack> stackFilter = (playerx, stack) -> true;

      public Builder(Player player) {
         this.player = player;
      }

      public InventorySnapshotData.Builder removeSnapshotItems() {
         this.removeSnapshotItems = true;
         return this;
      }

      public InventorySnapshotData.Builder replaceExisting() {
         this.replaceExisting = true;
         return this;
      }

      public InventorySnapshotData.Builder setStackFilter(BiPredicate<Player, ItemStack> stackFilter) {
         this.stackFilter = stackFilter;
         return this;
      }

      public InventorySnapshotData.InventorySnapshot createSnapshot() {
         InventorySnapshotData.InventorySnapshot snapshot = new InventorySnapshotData.InventorySnapshot(this.removeSnapshotItems, this.replaceExisting);
         snapshot.createSnapshot(this.player, this.stackFilter);
         return snapshot;
      }
   }

   public interface InventoryAccessor {
      int getSize();
   }

   public static class InventorySnapshot implements INBTSerializable<CompoundTag> {
      private final VListNBT<Integer, IntTag> invIds = new VListNBT<>(IntTag::valueOf, IntTag::getAsInt);
      private final VListNBT<ItemStack, CompoundTag> items = new VListNBT<>(IForgeItemStack::serializeNBT, ItemStack::of);
      private final Map<String, CompoundTag> customInventoryData = new HashMap<>();
      private boolean removeSnapshotItems = false;
      private boolean replaceExisting = false;

      protected InventorySnapshot() {
      }

      protected InventorySnapshot(boolean removeSnapshotItems, boolean replaceExisting) {
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

      protected boolean apply(Player player) {
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
   }
}
