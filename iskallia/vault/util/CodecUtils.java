package iskallia.vault.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import iskallia.vault.VaultMod;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class CodecUtils {
   public static <T> Optional<T> readJson(Codec<T> codec, JsonElement jsonElement) {
      return codec.parse(JsonOps.INSTANCE, jsonElement).resultOrPartial(VaultMod.LOGGER::error);
   }

   public static <T> void writeJson(Codec<T> codec, T value, Consumer<JsonElement> successConsumer) {
      codec.encodeStart(JsonOps.INSTANCE, value).resultOrPartial(VaultMod.LOGGER::error).ifPresent(successConsumer);
   }

   public static <T> JsonElement writeJson(Codec<T> codec, T value) {
      return (JsonElement)codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(false, VaultMod.LOGGER::error);
   }

   public static <T> T readNBT(Codec<T> codec, CompoundTag tag, String targetKey, T defaultValue) {
      return readNBT(codec, tag.get(targetKey)).orElse(defaultValue);
   }

   public static <T> T readNBT(Codec<T> codec, Tag nbt, T defaultValue) {
      return readNBT(codec, nbt).orElse(defaultValue);
   }

   public static <T> Optional<T> readNBT(Codec<T> codec, Tag nbt) {
      return codec.parse(NbtOps.INSTANCE, nbt).resultOrPartial(VaultMod.LOGGER::error);
   }

   public static <T> void writeNBT(Codec<T> codec, T value, CompoundTag targetTag, String targetKey) {
      writeNBT(codec, value, nbt -> targetTag.put(targetKey, nbt));
   }

   public static <T> void writeNBT(Codec<T> codec, T value, Consumer<Tag> successConsumer) {
      codec.encodeStart(NbtOps.INSTANCE, value).resultOrPartial(VaultMod.LOGGER::error).ifPresent(successConsumer);
   }

   public static <T> Tag writeNBT(Codec<T> codec, T value) {
      return (Tag)codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow(false, VaultMod.LOGGER::error);
   }
}
