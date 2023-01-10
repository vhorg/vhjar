package iskallia.vault.client;

import iskallia.vault.core.vault.influence.VaultGod;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class ClientStatisticsData {
   private static final Map<VaultGod, Integer> REPUTATION = new EnumMap<>(VaultGod.class);
   private static VaultGod FAVOUR = null;

   public static void receiveUpdate(CompoundTag data) {
      REPUTATION.clear();
      CompoundTag favourData = data.getCompound("reputation");

      for (VaultGod type : VaultGod.values()) {
         REPUTATION.put(type, favourData.getInt(type.getName()));
      }

      FAVOUR = data.contains("favour", 8) ? VaultGod.fromName(data.getString("favour")) : null;
   }

   public static int getReputation(VaultGod type) {
      return REPUTATION.getOrDefault(type, 0);
   }

   public static Optional<VaultGod> getFavour() {
      return Optional.ofNullable(FAVOUR);
   }
}
