package iskallia.vault.config.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.VaultMod;
import java.lang.reflect.Type;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

public class CompoundTagAdapter implements JsonSerializer<CompoundTag>, JsonDeserializer<CompoundTag> {
   public static final CompoundTagAdapter INSTANCE = new CompoundTagAdapter();

   private CompoundTagAdapter() {
   }

   public CompoundTag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      try {
         return TagParser.parseTag(json.getAsString());
      } catch (CommandSyntaxException var5) {
         VaultMod.LOGGER.error("Error parsing compound tag: ", var5);
         return null;
      }
   }

   public JsonElement serialize(CompoundTag src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toString());
   }
}
