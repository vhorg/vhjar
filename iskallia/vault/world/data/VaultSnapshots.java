package iskallia.vault.world.data;

import iskallia.vault.VaultMod;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.nbt.VListNBT;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;

public class VaultSnapshots extends SavedData {
   protected static final String DATA_NAME = "the_vault_VaultSnapshots";
   private final VListNBT<VaultSnapshot, LongArrayTag> snapshots = new VListNBT<>(
      new ArrayList<>(), snapshot -> new LongArrayTag(snapshot.getCache()), nbt -> new VaultSnapshot(ArrayBitBuffer.backing(nbt.getAsLongArray(), 0))
   );

   public static VaultSnapshot get(UUID vaultId) {
      for (VaultSnapshot snapshot : get(ServerLifecycleHooks.getCurrentServer()).snapshots) {
         if (snapshot.getStart().get(Vault.ID).equals(vaultId)) {
            return snapshot;
         }
      }

      return null;
   }

   public static List<VaultSnapshot> getPrev50(UUID owner, List<VaultSnapshot> snapshots) {
      List<VaultSnapshot> copy = new ArrayList<>(snapshots);
      List<VaultSnapshot> snapshotToReturn = new ArrayList<>();

      for (VaultSnapshot snapshot : copy) {
         if (snapshot.getEnd().get(Vault.STATS).getMap().containsKey(owner)) {
            snapshotToReturn.add(snapshot);
            if (snapshotToReturn.size() >= 50) {
               break;
            }
         }
      }

      return snapshotToReturn;
   }

   public static List<VaultSnapshot> getAll() {
      return get(ServerLifecycleHooks.getCurrentServer()).snapshots;
   }

   public static void onVaultStarted(Vault vault) {
      get(ServerLifecycleHooks.getCurrentServer()).snapshots.add(new VaultSnapshot(vault.get(Vault.VERSION)).setStart(vault));
      get(ServerLifecycleHooks.getCurrentServer()).setDirty();
   }

   public static void onVaultEnded(Vault vault) {
      List<VaultSnapshot> snapshots = get(ServerLifecycleHooks.getCurrentServer()).snapshots;

      for (int i = snapshots.size() - 1; i >= 0; i--) {
         VaultSnapshot snapshot = snapshots.get(i);
         if (snapshot.matches(vault)) {
            snapshot.setEnd(vault);
            break;
         }
      }

      get(ServerLifecycleHooks.getCurrentServer()).setDirty();
   }

   public CompoundTag save(CompoundTag nbt) {
      nbt.put("snapshots", this.snapshots.serializeNBT());
      return nbt;
   }

   public void load(CompoundTag nbt) {
      this.snapshots.deserializeNBT(nbt.getList("snapshots", 12));
   }

   public void save(File file) {
      if (this.isDirty()) {
         long start = System.nanoTime();

         try (
            FileOutputStream fos = new FileOutputStream(file);
            FileChannel channel = fos.getChannel();
         ) {
            VaultSnapshots.ArrayOutputStream array = new VaultSnapshots.ArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(array);
            CompoundTag nbt = new CompoundTag();
            nbt.put("data", this.save(new CompoundTag()));
            nbt.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
            NbtIo.write(nbt, dos);
            ByteBuffer buffer = ByteBuffer.wrap(array.getBuf(), 0, array.getCount());
            channel.write(buffer);
         } catch (IOException var14) {
            var14.printStackTrace();
         }

         MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
         if (srv != null && srv.getGameRules().getBoolean(ModGameRules.PRINT_SAVE_DATA_TIMING)) {
            long time = System.nanoTime() - start;
            VaultMod.LOGGER.info("VaultSnapshots saving took %f ms".formatted(time / 1000000.0));
         }

         this.setDirty(false);
      }
   }

   public static VaultSnapshots get(MinecraftServer server) {
      return (VaultSnapshots)server.overworld().getDataStorage().computeIfAbsent(VaultSnapshots::create, VaultSnapshots::new, "the_vault_VaultSnapshots");
   }

   private static VaultSnapshots create(CompoundTag tag) {
      VaultSnapshots data = new VaultSnapshots();
      data.load(tag);
      return data;
   }

   public static class ArrayOutputStream extends ByteArrayOutputStream {
      public byte[] getBuf() {
         return this.buf;
      }

      public int getCount() {
         return this.count;
      }
   }
}
