package iskallia.vault.util;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class NetcodeUtils {
   public static void runIfPresent(@Nullable MinecraftServer server, @Nonnull UUID uuid, Consumer<ServerPlayer> action) {
      runIfPresent(server, uuid, sPlayer -> {
         action.accept(sPlayer);
         return null;
      });
   }

   public static <T> Optional<T> runIfPresent(@Nullable MinecraftServer server, @Nonnull UUID uuid, Function<ServerPlayer, T> action) {
      if (server == null) {
         return Optional.empty();
      } else {
         ServerPlayer player = server.getPlayerList().getPlayer(uuid);
         return player == null ? Optional.empty() : Optional.ofNullable(action.apply(player));
      }
   }

   public static void writeString(ByteBuf buf, String str) {
      byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
      buf.writeInt(bytes.length);
      buf.writeBytes(bytes);
   }

   public static String readString(ByteBuf buf) {
      byte[] bytes = new byte[buf.readInt()];
      buf.readBytes(bytes);
      return new String(bytes, StandardCharsets.UTF_8);
   }

   public static void writeIdentifier(ByteBuf buf, ResourceLocation identifier) {
      writeString(buf, identifier.toString());
   }

   public static ResourceLocation readIdentifier(ByteBuf buf) {
      return new ResourceLocation(readString(buf));
   }

   public static <T> void writeCollection(ByteBuf buf, Collection<T> collection, BiConsumer<T, ByteBuf> elementWriter) {
      buf.writeInt(collection.size());

      for (T element : collection) {
         elementWriter.accept(element, buf);
      }
   }

   public static <T, C extends Collection<T>> C readCollection(ByteBuf buf, IntFunction<C> supplier, Function<ByteBuf, T> elementReader) {
      int size = buf.readInt();
      C collection = (C)supplier.apply(size);

      for (int i = 0; i < size; i++) {
         collection.add(elementReader.apply(buf));
      }

      return collection;
   }
}
