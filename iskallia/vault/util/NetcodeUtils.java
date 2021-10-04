package iskallia.vault.util;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class NetcodeUtils {
   public static void runIfPresent(@Nullable MinecraftServer server, @Nonnull UUID uuid, Consumer<ServerPlayerEntity> action) {
      runIfPresent(server, uuid, sPlayer -> {
         action.accept(sPlayer);
         return null;
      });
   }

   public static <T> Optional<T> runIfPresent(@Nullable MinecraftServer server, @Nonnull UUID uuid, Function<ServerPlayerEntity, T> action) {
      if (server == null) {
         return Optional.empty();
      } else {
         ServerPlayerEntity player = server.func_184103_al().func_177451_a(uuid);
         return player == null ? Optional.empty() : Optional.ofNullable(action.apply(player));
      }
   }
}
