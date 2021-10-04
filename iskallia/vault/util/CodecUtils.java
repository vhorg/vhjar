package iskallia.vault.util;

import com.mojang.serialization.Codec;
import iskallia.vault.Vault;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;

public class CodecUtils {
   public static <T> T readNBT(Codec<T> codec, CompoundNBT tag, String targetKey, T defaultValue) {
      return readNBT(codec, tag.func_74781_a(targetKey)).orElse(defaultValue);
   }

   public static <T> T readNBT(Codec<T> codec, INBT nbt, T defaultValue) {
      return readNBT(codec, nbt).orElse(defaultValue);
   }

   public static <T> Optional<T> readNBT(Codec<T> codec, INBT nbt) {
      return codec.parse(NBTDynamicOps.field_210820_a, nbt).resultOrPartial(Vault.LOGGER::error);
   }

   public static <T> void writeNBT(Codec<T> codec, T value, CompoundNBT targetTag, String targetKey) {
      writeNBT(codec, value, nbt -> targetTag.func_218657_a(targetKey, nbt));
   }

   public static <T> void writeNBT(Codec<T> codec, T value, Consumer<INBT> successConsumer) {
      codec.encodeStart(NBTDynamicOps.field_210820_a, value).resultOrPartial(Vault.LOGGER::error).ifPresent(successConsumer);
   }

   public static <T> INBT writeNBT(Codec<T> codec, T value) {
      return (INBT)codec.encodeStart(NBTDynamicOps.field_210820_a, value).getOrThrow(false, Vault.LOGGER::error);
   }
}
