package iskallia.vault.util;

import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

public class McClientHelper {
   public static Optional<GameProfile> getOnlineProfile(UUID uuid) {
      if (uuid == null) {
         return Optional.empty();
      } else {
         ClientPacketListener connection = Minecraft.getInstance().getConnection();
         if (connection == null) {
            return Optional.empty();
         } else {
            Collection<PlayerInfo> playerInfoMap = connection.getOnlinePlayers();
            GameProfile gameProfile = playerInfoMap.stream()
               .<GameProfile>map(PlayerInfo::getProfile)
               .filter(profile -> profile.getId().equals(uuid))
               .findFirst()
               .orElse(null);
            return Optional.ofNullable(gameProfile);
         }
      }
   }
}
