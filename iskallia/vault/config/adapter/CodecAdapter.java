package iskallia.vault.config.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import iskallia.vault.VaultMod;
import java.lang.reflect.Type;

public record CodecAdapter<T>(Codec<T> codec) implements JsonSerializer<T>, JsonDeserializer<T> {
   public static <T> CodecAdapter<T> of(Codec<T> codec) {
      return new CodecAdapter<>(codec);
   }

   public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
      return (JsonElement)this.codec.encodeStart(JsonOps.INSTANCE, src).resultOrPartial(VaultMod.LOGGER::error).orElseThrow();
   }

   public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return (T)this.codec.parse(JsonOps.INSTANCE, json).resultOrPartial(VaultMod.LOGGER::error).orElseThrow();
   }
}
