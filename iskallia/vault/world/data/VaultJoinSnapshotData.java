package iskallia.vault.world.data;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModGameRules;
import java.io.File;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.server.ServerLifecycleHooks;

public class VaultJoinSnapshotData extends TimestampedInventorySnapshotData {
   protected static final String DATA_NAME = "the_vault_VaultJoinSnapshots";

   public void save(File file) {
      long timeMs = System.currentTimeMillis();
      super.save(file);
      if (System.currentTimeMillis() - timeMs > 50L) {
         MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
         if (srv != null && srv.getGameRules().getBoolean(ModGameRules.PRINT_SAVE_DATA_TIMING)) {
            VaultMod.LOGGER.info("VaultJoinSnapshots saving took %s ms".formatted(System.currentTimeMillis() - timeMs));
         }
      }
   }

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
