package iskallia.vault.world.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class VaultJoinSnapshotData extends TimestampedInventorySnapshotData {
   protected static final String DATA_NAME = "the_vault_VaultJoinSnapshots";

   private static VaultJoinSnapshotData create(CompoundTag tag) {
      VaultJoinSnapshotData data = new VaultJoinSnapshotData();
      data.load(tag);
      return data;
   }

   public static VaultJoinSnapshotData get(ServerLevel serverLevel) {
      return (VaultJoinSnapshotData)serverLevel.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(VaultJoinSnapshotData::create, VaultJoinSnapshotData::new, "the_vault_VaultJoinSnapshots");
   }
}
