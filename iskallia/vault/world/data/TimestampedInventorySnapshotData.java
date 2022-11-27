package iskallia.vault.world.data;

import iskallia.vault.nbt.VMapNBT;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class TimestampedInventorySnapshotData extends SavedData {
   public VMapNBT<UUID, TimestampedInventorySnapshotData.TimestampedInventorySnapshot> snapshotData = VMapNBT.ofUUID(
      TimestampedInventorySnapshotData.TimestampedInventorySnapshot::new
   );

   protected InventorySnapshotData.Builder makeSnapshotBuilder(Player player) {
      return new InventorySnapshotData.Builder(player).setStackFilter((p, stack) -> true);
   }

   public void createSnapshot(Player player) {
      TimestampedInventorySnapshotData.TimestampedInventorySnapshot timestampedSnapshots = this.snapshotData
         .computeIfAbsent(player.getUUID(), u -> new TimestampedInventorySnapshotData.TimestampedInventorySnapshot());
      timestampedSnapshots.addSnapshot((int)(Util.getEpochMillis() / 1000L), this.makeSnapshotBuilder(player).createSnapshot());
      this.setDirty();
   }

   public void removeLastSnapshot(ServerPlayer player) {
      this.snapshotData.computeIfPresent(player.getUUID(), (playerUuid, snapshots) -> {
         if (!snapshots.timestampedSnapshots.isEmpty()) {
            ArrayList<Integer> timestamps = new ArrayList<>(snapshots.getTimestamps());
            timestamps.sort(Comparator.reverseOrder());
            snapshots.timestampedSnapshots.remove(timestamps.get(0));
            this.setDirty();
         }

         return (TimestampedInventorySnapshotData.TimestampedInventorySnapshot)snapshots;
      });
   }

   public boolean removeSnapshots(Player player) {
      return this.removeSnapshots(player.getUUID());
   }

   public boolean removeSnapshots(UUID playerUUID) {
      if (this.snapshotData.remove(playerUUID) != null) {
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public Set<Integer> getSnapshotTimestamps(UUID playerUuid) {
      return !this.snapshotData.containsKey(playerUuid) ? Collections.emptySet() : this.snapshotData.get(playerUuid).getTimestamps();
   }

   public boolean restoreSnapshot(Player applyToPlayer, UUID playerUuid, int timestamp) {
      if (this.snapshotData.containsKey(playerUuid)) {
         TimestampedInventorySnapshotData.TimestampedInventorySnapshot timestampedSnapshots = this.snapshotData.get(playerUuid);
         return timestampedSnapshots.getSnapshot(timestamp).map(snapshot -> snapshot.apply(applyToPlayer)).orElse(false);
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

   public static class TimestampedInventorySnapshot implements INBTSerializable<CompoundTag> {
      private final VMapNBT<Integer, InventorySnapshotData.InventorySnapshot> timestampedSnapshots = VMapNBT.ofInt(InventorySnapshotData.InventorySnapshot::new);

      TimestampedInventorySnapshot() {
      }

      public void addSnapshot(int timestamp, InventorySnapshotData.InventorySnapshot snapshot) {
         this.timestampedSnapshots.put(timestamp, snapshot);
      }

      public CompoundTag serializeNBT() {
         CompoundTag tag = new CompoundTag();
         tag.put("timestampedSnapshots", this.timestampedSnapshots.serializeNBT());
         return tag;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.timestampedSnapshots.deserializeNBT(nbt.getList("timestampedSnapshots", 10));
      }

      public Optional<InventorySnapshotData.InventorySnapshot> getSnapshot(int timestamp) {
         return Optional.ofNullable(this.timestampedSnapshots.get(timestamp));
      }

      public Set<Integer> getTimestamps() {
         return this.timestampedSnapshots.keySet();
      }
   }
}