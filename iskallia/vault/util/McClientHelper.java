package iskallia.vault.util;

import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;

public class McClientHelper {
   public static Optional<GameProfile> getOnlineProfile(UUID uuid) {
      if (uuid == null) {
         return Optional.empty();
      } else {
         ClientPlayNetHandler connection = Minecraft.func_71410_x().func_147114_u();
         if (connection == null) {
            return Optional.empty();
         } else {
            Collection<NetworkPlayerInfo> playerInfoMap = connection.func_175106_d();
            GameProfile gameProfile = playerInfoMap.stream()
               .<GameProfile>map(NetworkPlayerInfo::func_178845_a)
               .filter(profile -> profile.getId().equals(uuid))
               .findFirst()
               .orElse(null);
            return Optional.ofNullable(gameProfile);
         }
      }
   }
}
