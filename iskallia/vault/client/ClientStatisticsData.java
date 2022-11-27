package iskallia.vault.client;

import iskallia.vault.world.data.PlayerFavourData;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;

public class ClientStatisticsData {
   private static final Map<PlayerFavourData.VaultGodType, Integer> favourStats = new HashMap<>();

   public static void receiveUpdate(CompoundTag statisticsData) {
      favourStats.clear();
      CompoundTag favourData = statisticsData.getCompound("favourStats");

      for (PlayerFavourData.VaultGodType type : PlayerFavourData.VaultGodType.values()) {
         favourStats.put(type, favourData.getInt(type.name()));
      }
   }

   public static int getFavour(PlayerFavourData.VaultGodType type) {
      return favourStats.getOrDefault(type, 0);
   }
}
