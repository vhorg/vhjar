package iskallia.vault.util;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class NetcodeUtils {
   public static boolean runIfPresent(MinecraftServer server, UUID uuid, Consumer<ServerPlayerEntity> action) {
      if (server == null) {
         return false;
      } else {
         ServerPlayerEntity player = server.func_184103_al().func_177451_a(uuid);
         if (player == null) {
            return false;
         } else {
            action.accept(player);
            return true;
         }
      }
   }
}
