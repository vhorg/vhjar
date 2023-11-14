package iskallia.vault.world.data;

import iskallia.vault.nbt.VMapNBT;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

public abstract class InventorySnapshotData extends SavedData {
   public VMapNBT<UUID, InventorySnapshot> snapshotData = VMapNBT.ofUUID(InventorySnapshot::new);

   protected abstract boolean shouldSnapshotItem(Player var1, ItemStack var2);

   protected InventorySnapshot.Builder makeSnapshotBuilder(Player player) {
      return new InventorySnapshot.Builder(player).setStackFilter(this::shouldSnapshotItem).removeSnapshotItems();
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
      InventorySnapshot snapshot = this.snapshotData.remove(player.getUUID());
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

   public interface InventoryAccessor {
      int getSize();
   }
}
