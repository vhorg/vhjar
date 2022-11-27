package iskallia.vault.config.adapter;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.Codec;
import iskallia.vault.util.CodecUtils;
import java.lang.reflect.Type;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public class RegistryCodecAdapter<T extends IForgeRegistryEntry<T>> implements JsonSerializer<T>, JsonDeserializer<T> {
   protected final ResourceLocation resourceLocation;
   protected final Codec<T> codec;

   public static void registerVanillaRegistryCodecs(GsonBuilder gsonBuilder) {
      Registry.REGISTRY.keySet().forEach(resourceLocation -> {
         ForgeRegistry<?> registry = RegistryManager.ACTIVE.getRegistry(resourceLocation);
         if (registry != null) {
            Class<?> registrySuperType = registry.getRegistrySuperType();
            gsonBuilder.registerTypeAdapter(registrySuperType, of((IForgeRegistry<T>)registry));
         }
      });
   }

   public static <T extends IForgeRegistryEntry<T>> RegistryCodecAdapter<T> of(IForgeRegistry<T> registry) {
      return new RegistryCodecAdapter<>(registry.getRegistryName(), registry.getCodec());
   }

   protected RegistryCodecAdapter(ResourceLocation resourceLocation, Codec<T> codec) {
      this.resourceLocation = resourceLocation;
      this.codec = codec;
   }

   public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
      return CodecUtils.writeJson(this.codec, src);
   }

   public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return CodecUtils.<T>readJson(this.codec, json)
         .orElseThrow(() -> new JsonParseException("Invalid registry key '%s' for registry '%s'".formatted(json.getAsString(), this.resourceLocation)));
   }
}
