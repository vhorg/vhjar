package iskallia.vault.world.data;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ProficiencyMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkDirection;

public class PlayerProficiencyData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerProficiency";
   private final Map<UUID, Integer> playerProficiency = new HashMap<>();

   private PlayerProficiencyData() {
   }

   private PlayerProficiencyData(CompoundTag tag) {
      this.load(tag);
   }

   public Integer getAbsoluteProficiency(Player player) {
      return this.getAbsoluteProficiency(player.getUUID());
   }

   public Integer getAbsoluteProficiency(UUID playerId) {
      return this.playerProficiency.getOrDefault(playerId, 0);
   }

   public void setAbsoluteProficiency(UUID playerId, int value) {
      this.playerProficiency.put(playerId, value);
      this.setDirty();
   }

   public void addAbsoluteProficiency(UUID playerId, int value) {
      this.setAbsoluteProficiency(playerId, this.getAbsoluteProficiency(playerId) + value);
   }

   public void sendProficiencyInformation(ServerPlayer player) {
      UUID playerId = player.getUUID();
      int absProficiency = this.getAbsoluteProficiency(playerId);
      ModNetwork.CHANNEL.sendTo(new ProficiencyMessage(absProficiency), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   protected void load(CompoundTag tag) {
      this.playerProficiency.clear();

      for (String playerIdStr : tag.getAllKeys()) {
         UUID playerId = UUID.fromString(playerIdStr);
         int proficiency = tag.getInt(playerIdStr);
         this.playerProficiency.put(playerId, proficiency);
      }
   }

   public CompoundTag save(CompoundTag tag) {
      this.playerProficiency.forEach((playerId, proficiency) -> tag.putInt(playerId.toString(), proficiency));
      return tag;
   }

   public static PlayerProficiencyData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static PlayerProficiencyData get(MinecraftServer server) {
      return (PlayerProficiencyData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerProficiencyData::new, PlayerProficiencyData::new, "the_vault_PlayerProficiency");
   }
}
