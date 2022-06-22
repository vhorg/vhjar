package iskallia.vault.world.data;

import iskallia.vault.nbt.VMapNBT;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class FinalVaultData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_FinalVault";
   private VMapNBT<UUID, Integer> timesCompleted = VMapNBT.ofUUIDToInt();

   public FinalVaultData() {
      super("the_vault_FinalVault");
   }

   public int getTimesCompleted(UUID player) {
      return this.timesCompleted.getOrDefault(player, Integer.valueOf(0));
   }

   public void onCompleted(UUID player) {
      this.timesCompleted.put(player, this.timesCompleted.getOrDefault(player, Integer.valueOf(0)) + 1);
      this.func_76186_a(true);
   }

   public void func_76184_a(CompoundNBT nbt) {
      this.timesCompleted.deserializeNBT(nbt.func_150295_c("timesCompleted", 10));
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      nbt.func_218657_a("timesCompleted", this.timesCompleted.serializeNBT());
      return nbt;
   }

   public static FinalVaultData get(ServerWorld world) {
      return (FinalVaultData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(FinalVaultData::new, "the_vault_FinalVault");
   }
}
