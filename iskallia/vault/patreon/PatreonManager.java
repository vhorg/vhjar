package iskallia.vault.patreon;

import com.google.gson.Gson;
import iskallia.vault.VaultMod;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.io.IOUtils;

public class PatreonManager {
   private static final PatreonManager INSTANCE = new PatreonManager();
   private static final String DEMO_URL_BASE = "https://demo-api.vaulthunters.gg/";
   private static final String PROD_URL_BASE = "https://api.vaulthunters.gg/";
   private static final Gson GSON = new Gson();
   private final Map<UUID, List<PatreonTier>> loadedTierCache = new HashMap<>();

   private PatreonManager() {
   }

   public static PatreonManager getInstance() {
      return INSTANCE;
   }

   public void clearCache() {
      this.loadedTierCache.clear();
   }

   public void clearCache(UUID playerId) {
      this.loadedTierCache.remove(playerId);
   }

   public PatreonPlayerData getPlayerData(UUID playerId) {
      return new PatreonPlayerData(this.getPatreonTiers(playerId));
   }

   public synchronized List<PatreonTier> getPatreonTiers(UUID playerId) {
      List<PatreonTier> tiers = this.loadedTierCache.get(playerId);
      if (tiers != null) {
         return tiers;
      } else {
         this.loadedTierCache.put(playerId, new ArrayList<>());
         this.requestPatreonTiers(playerId, loadedTiers -> this.loadedTierCache.put(playerId, loadedTiers));
         return new ArrayList<>();
      }
   }

   private void requestPatreonTiers(UUID playerId, Consumer<List<PatreonTier>> onLoad) {
      Thread tr = new Thread(() -> {
         try {
            String urlPart = FMLEnvironment.production ? "https://api.vaulthunters.gg/" : "https://demo-api.vaulthunters.gg/";
            URL url = new URL(urlPart + "users/reward?uuid=" + playerId.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();
            if (connection.getResponseCode() != 200) {
               VaultMod.LOGGER.error("Fetching Patreon tiers failed! Response code: " + connection.getResponseCode());
               onLoad.accept(new ArrayList<>());
               return;
            }

            String response = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
            RewardResponse responseObj = (RewardResponse)GSON.fromJson(response, RewardResponse.class);
            onLoad.accept(responseObj.convertTiers());
         } catch (Exception var7) {
            VaultMod.LOGGER.error("Fetching Patreon tiers failed!", var7);
            onLoad.accept(new ArrayList<>());
         }
      }, "Patreon Request - " + playerId.toString());
      tr.setDaemon(true);
      tr.start();
   }
}
