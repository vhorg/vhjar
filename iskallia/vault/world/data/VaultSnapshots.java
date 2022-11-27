package iskallia.vault.world.data;

import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.nbt.VListNBT;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;

public class VaultSnapshots extends SavedData {
   protected static final String DATA_NAME = "the_vault_VaultSnapshots";
   private final VListNBT<VaultSnapshot, LongArrayTag> snapshots = new VListNBT<>(new ArrayList<>(), snapshot -> {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      snapshot.write(buffer);
      return new LongArrayTag(buffer.toLongArray());
   }, nbt -> new VaultSnapshot(ArrayBitBuffer.backing(nbt.getAsLongArray(), 0)));

   public static VaultSnapshot get(UUID vaultId) {
      for (VaultSnapshot snapshot : get(ServerLifecycleHooks.getCurrentServer()).snapshots) {
         if (snapshot.getStart().get(Vault.ID).equals(vaultId)) {
            return snapshot;
         }
      }

      return null;
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

   public static VaultSnapshots get(MinecraftServer server) {
      return (VaultSnapshots)server.overworld().getDataStorage().computeIfAbsent(VaultSnapshots::create, VaultSnapshots::new, "the_vault_VaultSnapshots");
   }

   private static VaultSnapshots create(CompoundTag tag) {
      VaultSnapshots data = new VaultSnapshots();
      data.load(tag);
      return data;
   }
}
