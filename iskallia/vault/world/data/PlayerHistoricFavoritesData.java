package iskallia.vault.world.data;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.HistoricFavoritesMessage;
import iskallia.vault.util.NetcodeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkDirection;

public class PlayerHistoricFavoritesData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerHistoricFavorites";
   private final Map<UUID, PlayerHistoricFavoritesData.HistoricFavorites> playerMap = new HashMap<>();
   private static final String TAG_PLAYER_LIST = "playerList";
   private static final String TAG_FAVORITES_LIST = "favoritesList";

   public static PlayerHistoricFavoritesData get(ServerLevel level) {
      return get(level.getServer());
   }

   public static PlayerHistoricFavoritesData get(MinecraftServer server) {
      return (PlayerHistoricFavoritesData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerHistoricFavoritesData::create, PlayerHistoricFavoritesData::new, "the_vault_PlayerHistoricFavorites");
   }

   private static PlayerHistoricFavoritesData create(CompoundTag compoundTag) {
      return new PlayerHistoricFavoritesData(compoundTag);
   }

   private PlayerHistoricFavoritesData() {
   }

   private PlayerHistoricFavoritesData(CompoundTag compoundTag) {
      this();
      this.load(compoundTag);
   }

   public Map<UUID, PlayerHistoricFavoritesData.HistoricFavorites> getPlayerMap() {
      return this.playerMap;
   }

   public PlayerHistoricFavoritesData.HistoricFavorites getHistoricFavorites(Player player) {
      return this.getHistoricFavorites(player.getUUID());
   }

   public PlayerHistoricFavoritesData.HistoricFavorites getHistoricFavorites(UUID playerUuid) {
      return this.playerMap.computeIfAbsent(playerUuid, x$0 -> new PlayerHistoricFavoritesData.HistoricFavorites(x$0));
   }

   private void load(CompoundTag compoundTag) {
      ListTag playerList = compoundTag.getList("playerList", 8);
      ListTag favoritesList = compoundTag.getList("favoritesList", 10);
      if (playerList.size() != favoritesList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getHistoricFavorites(playerUUID).deserializeNBT(favoritesList.getCompound(i));
         }
      }
   }

   @Nonnull
   public CompoundTag save(CompoundTag compoundTag) {
      ListTag playerList = new ListTag();
      ListTag favoritesList = new ListTag();
      this.playerMap.forEach((key, value) -> {
         playerList.add(StringTag.valueOf(key.toString()));
         favoritesList.add(value.serializeNBT());
      });
      compoundTag.put("playerList", playerList);
      compoundTag.put("favoritesList", favoritesList);
      return compoundTag;
   }

   public class HistoricFavorites {
      private UUID playerUuid;
      private List<UUID> favoritesUUID;

      public HistoricFavorites(UUID playerUuid) {
         this.playerUuid = playerUuid;
         this.favoritesUUID = new ArrayList<>();
      }

      public List<UUID> getFavorites() {
         return this.favoritesUUID;
      }

      private static PlayerHistoricFavoritesData create(CompoundTag tag) {
         PlayerHistoricFavoritesData data = new PlayerHistoricFavoritesData();
         data.load(tag);
         return data;
      }

      public void deserializeNBT(CompoundTag tag) {
         ListTag list = tag.getList("favorites", 10);

         for (int i = 0; i < list.size(); i++) {
            CompoundTag tradeTag = list.getCompound(i);
            this.favoritesUUID.add(tradeTag.getUUID("uuid"));
         }

         this.playerUuid = tag.getUUID("uuid");
      }

      public CompoundTag serializeNBT() {
         CompoundTag tag = new CompoundTag();
         ListTag list = new ListTag();
         this.favoritesUUID.forEach(uuid -> {
            CompoundTag tradeTag = new CompoundTag();
            tradeTag.putUUID("uuid", uuid);
            list.add(tradeTag);
         });
         tag.put("favorites", list);
         tag.putUUID("uuid", this.playerUuid);
         return tag;
      }

      public void syncToClient(MinecraftServer server) {
         NetcodeUtils.runIfPresent(
            server,
            this.playerUuid,
            player -> ModNetwork.CHANNEL
               .sendTo(new HistoricFavoritesMessage(this.favoritesUUID), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
         );
      }

      public static PlayerHistoricFavoritesData get(ServerLevel world) {
         return get(world.getServer());
      }

      public static PlayerHistoricFavoritesData get(MinecraftServer server) {
         return (PlayerHistoricFavoritesData)server.overworld()
            .getDataStorage()
            .computeIfAbsent(PlayerHistoricFavoritesData::create, PlayerHistoricFavoritesData::new, "the_vault_PlayerHistoricFavorites");
      }
   }
}
