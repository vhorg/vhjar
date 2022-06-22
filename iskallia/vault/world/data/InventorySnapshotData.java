package iskallia.vault.world.data;

import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.nbt.VMapNBT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.ModList;

public abstract class InventorySnapshotData extends WorldSavedData {
   public VMapNBT<UUID, InventorySnapshotData.InventorySnapshot> snapshotData = VMapNBT.ofUUID(() -> new InventorySnapshotData.InventorySnapshot());

   protected InventorySnapshotData(String name) {
      super(name);
   }

   protected abstract boolean shouldSnapshotItem(PlayerEntity var1, ItemStack var2);

   protected InventorySnapshotData.Builder makeSnapshotBuilder(PlayerEntity player) {
      return new InventorySnapshotData.Builder(player).setStackFilter(this::shouldSnapshotItem).removeSnapshotItems();
   }

   public boolean hasSnapshot(PlayerEntity player) {
      return this.hasSnapshot(player.func_110124_au());
   }

   public boolean hasSnapshot(UUID playerUUID) {
      return this.snapshotData.containsKey(playerUUID);
   }

   public void createSnapshot(PlayerEntity player) {
      if (this.snapshotData.containsKey(player.func_110124_au())) {
         this.restoreSnapshot(player);
      }

      this.snapshotData.put(player.func_110124_au(), this.makeSnapshotBuilder(player).createSnapshot());
      this.func_76185_a();
   }

   public boolean removeSnapshot(PlayerEntity player) {
      return this.removeSnapshot(player.func_110124_au());
   }

   public boolean removeSnapshot(UUID playerUUID) {
      if (this.snapshotData.remove(playerUUID) != null) {
         this.func_76185_a();
         return true;
      } else {
         return false;
      }
   }

   public boolean restoreSnapshot(PlayerEntity player) {
      InventorySnapshotData.InventorySnapshot snapshot = this.snapshotData.remove(player.func_110124_au());
      if (snapshot != null) {
         this.func_76185_a();
         return snapshot.apply(player);
      } else {
         return false;
      }
   }

   public void func_76184_a(CompoundNBT nbt) {
      this.snapshotData.deserializeNBT(nbt.func_150295_c("Players", 10));
   }

   public CompoundNBT func_189551_b(CompoundNBT compound) {
      compound.func_218657_a("Players", this.snapshotData.serializeNBT());
      return compound;
   }

   public static class Builder {
      private final PlayerEntity player;
      private boolean removeSnapshotItems = false;
      private boolean replaceExisting = false;
      private BiPredicate<PlayerEntity, ItemStack> stackFilter = (playerx, stack) -> true;

      public Builder(PlayerEntity player) {
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

      public InventorySnapshotData.Builder setStackFilter(BiPredicate<PlayerEntity, ItemStack> stackFilter) {
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

   public static class InventorySnapshot implements INBTSerializable<CompoundNBT> {
      private final VListNBT<Integer, IntNBT> invIds = new VListNBT<>(IntNBT::func_229692_a_, IntNBT::func_150287_d);
      private final VListNBT<ItemStack, CompoundNBT> items = new VListNBT<>(IForgeItemStack::serializeNBT, ItemStack::func_199557_a);
      private final Map<String, CompoundNBT> customInventoryData = new HashMap<>();
      private boolean removeSnapshotItems = false;
      private boolean replaceExisting = false;

      private InventorySnapshot() {
      }

      private InventorySnapshot(boolean removeSnapshotItems, boolean replaceExisting) {
         this.removeSnapshotItems = removeSnapshotItems;
         this.replaceExisting = replaceExisting;
      }

      private void createSnapshot(PlayerEntity player, BiPredicate<PlayerEntity, ItemStack> stackFilter) {
         for (int slot = 0; slot < ((InventorySnapshotData.InventoryAccessor)player.field_71071_by).getSize(); slot++) {
            ItemStack stack = player.field_71071_by.func_70301_a(slot);
            if (stackFilter.test(player, stack)) {
               this.addItemStack(slot, stack);
               if (this.removeSnapshotItems) {
                  player.field_71071_by.func_70299_a(slot, ItemStack.field_190927_a);
               }
            }
         }

         if (ModList.get().isLoaded("curios")) {
            CompoundNBT curiosData = IntegrationCurios.getMappedSerializedCuriosItemStacks(player, stackFilter, this.removeSnapshotItems);
            this.customInventoryData.put("curios", curiosData);
         }
      }

      private void addItemStack(int slot, ItemStack stack) {
         this.invIds.add(slot);
         this.items.add(stack.func_77946_l());
      }

      public boolean apply(PlayerEntity player) {
         if (!player.func_70089_S()) {
            return false;
         } else {
            List<ItemStack> addLater = new ArrayList<>();

            for (int index = 0; index < this.items.size(); index++) {
               ItemStack toAdd = this.items.get(index).func_77946_l();
               int slot = this.invIds.get(index);
               if (!this.replaceExisting && !player.field_71071_by.func_70301_a(slot).func_190926_b()) {
                  addLater.add(toAdd);
               } else {
                  player.field_71071_by.func_70299_a(slot, toAdd);
               }
            }

            if (ModList.get().isLoaded("curios") && this.customInventoryData.containsKey("curios")) {
               CompoundNBT curiosData = this.customInventoryData.get("curios");
               addLater.addAll(IntegrationCurios.applyMappedSerializedCuriosItemStacks(player, curiosData, this.replaceExisting));
            }

            for (ItemStack stack : addLater) {
               if (!player.func_191521_c(stack)) {
                  ItemEntity itementity = player.func_71019_a(stack, false);
                  if (itementity != null) {
                     itementity.func_174868_q();
                     itementity.func_200217_b(player.func_110124_au());
                  }
               }
            }

            return true;
         }
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_218657_a("InvIds", this.invIds.serializeNBT());
         nbt.func_218657_a("Items", this.items.serializeNBT());
         CompoundNBT customData = new CompoundNBT();
         this.customInventoryData.forEach(customData::func_218657_a);
         nbt.func_218657_a("customData", customData);
         nbt.func_74757_a("removeSnapshotItems", this.removeSnapshotItems);
         nbt.func_74757_a("replaceExisting", this.replaceExisting);
         return (R)nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.invIds.deserializeNBT(nbt.func_150295_c("InvIds", 3));
         this.items.deserializeNBT(nbt.func_150295_c("Items", 10));
         this.customInventoryData.clear();
         if (nbt.func_150297_b("customData", 10)) {
            CompoundNBT customData = nbt.func_74775_l("customData");

            for (String key : customData.func_150296_c()) {
               this.customInventoryData.put(key, customData.func_74775_l(key));
            }
         }

         this.removeSnapshotItems = nbt.func_74767_n("removeSnapshotItems");
         this.replaceExisting = nbt.func_74767_n("replaceExisting");
      }
   }
}
