package iskallia.vault.client;

import iskallia.vault.client.gui.screen.summary.VaultHistoricDataScreen;
import iskallia.vault.network.message.HistoricFavoritesMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;

public class ClientHistoricFavoritesData {
   private static List<UUID> favorites;

   public static void receive(HistoricFavoritesMessage message) {
      favorites = message.getFavorites();
      if (Minecraft.getInstance().screen instanceof VaultHistoricDataScreen vaultHistoricDataScreen) {
         vaultHistoricDataScreen.getFavoritesContainer().removeElements();
         vaultHistoricDataScreen.getFavoritesContainer().reset(true);
      }
   }

   public static List<UUID> getFavorites() {
      return (List<UUID>)(favorites == null ? new ArrayList<>() : favorites);
   }
}
