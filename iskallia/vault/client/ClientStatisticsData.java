package iskallia.vault.client;

import iskallia.vault.util.calc.PlayerStatisticsCollector;
import iskallia.vault.world.data.PlayerFavourData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class ClientStatisticsData {
   private static final List<PlayerStatisticsCollector.AttributeSnapshot> attributeValues = new ArrayList<>();
   private static final Map<PlayerFavourData.VaultGodType, Integer> favourStats = new HashMap<>();
   private static CompoundNBT serializedVaultStats = new CompoundNBT();

   public static void receiveUpdate(CompoundNBT statisticsData) {
      attributeValues.clear();
      favourStats.clear();
      ListNBT attributes = statisticsData.func_150295_c("attributes", 10);

      for (int i = 0; i < attributes.size(); i++) {
         attributeValues.add(PlayerStatisticsCollector.AttributeSnapshot.deserialize(attributes.func_150305_b(i)));
      }

      CompoundNBT favourData = statisticsData.func_74775_l("favourStats");

      for (PlayerFavourData.VaultGodType type : PlayerFavourData.VaultGodType.values()) {
         favourStats.put(type, favourData.func_74762_e(type.name()));
      }

      serializedVaultStats = statisticsData.func_74775_l("vaultStats");
   }

   public static List<PlayerStatisticsCollector.AttributeSnapshot> getPlayerAttributeSnapshots() {
      return Collections.unmodifiableList(attributeValues);
   }

   public static int getFavour(PlayerFavourData.VaultGodType type) {
      return favourStats.getOrDefault(type, 0);
   }

   public static CompoundNBT getSerializedVaultStats() {
      return serializedVaultStats;
   }
}
