package iskallia.vault.config.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootPoolKey;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.ThemeKey;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.world.generator.theme.Theme;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.template.EmptyTemplate;
import iskallia.vault.core.world.template.StructureTemplate;
import iskallia.vault.core.world.template.Template;
import iskallia.vault.core.world.template.data.TemplatePool;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public class VersionedKeyAdapter implements JsonSerializer<VersionedKey<?, ?>>, JsonDeserializer<VersionedKey<?, ?>> {
   public static final VersionedKeyAdapter INSTANCE = new VersionedKeyAdapter();

   public JsonElement serialize(VersionedKey<?, ?> src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonObject();
   }

   public VersionedKey<?, ?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject keyObject = json.getAsJsonObject();
      if (typeOfT == LootPoolKey.class) {
         LootPoolKey key = LootPoolKey.create(new ResourceLocation(keyObject.get("id").getAsString()), keyObject.get("name").getAsString());
         this.deserializeBody(keyObject, e -> LootPool.fromPath(e.getAsString()), key::with);
         return key;
      } else if (typeOfT == LootTableKey.class) {
         LootTableKey key = LootTableKey.create(new ResourceLocation(keyObject.get("id").getAsString()), keyObject.get("name").getAsString());
         this.deserializeBody(keyObject, e -> LootTable.fromPath(e.getAsString()), key::with);
         return key;
      } else if (typeOfT == PaletteKey.class) {
         PaletteKey key = PaletteKey.create(new ResourceLocation(keyObject.get("id").getAsString()), keyObject.get("name").getAsString());
         this.deserializeBody(keyObject, e -> Palette.fromPath(e.getAsString()), key::with);
         return key;
      } else if (typeOfT == TemplatePoolKey.class) {
         TemplatePoolKey key = TemplatePoolKey.create(new ResourceLocation(keyObject.get("id").getAsString()), keyObject.get("name").getAsString());
         this.deserializeBody(keyObject, e -> TemplatePool.fromPath(e.getAsString()), key::with);
         return key;
      } else if (typeOfT == TemplateKey.class) {
         TemplateKey key = TemplateKey.create(new ResourceLocation(keyObject.get("id").getAsString()), keyObject.get("name").getAsString());
         this.deserializeBody(keyObject, e -> {
            JsonObject value = e.getAsJsonObject();
            String type = value.get("type").getAsString();

            Template template = (Template)(switch (type) {
               case "structure" -> StructureTemplate.fromPath(value.get("path").getAsString());
               case "empty" -> EmptyTemplate.INSTANCE;
               default -> null;
            });
            if (template != null && value.has("tags")) {
               for (JsonElement tag : value.get("tags").getAsJsonArray()) {
                  template.addTag(new ResourceLocation(tag.getAsString()));
               }
            }

            return template;
         }, key::with);
         return key;
      } else if (typeOfT == ThemeKey.class) {
         ThemeKey key = ThemeKey.create(
            new ResourceLocation(keyObject.get("id").getAsString()),
            keyObject.get("name").getAsString(),
            !keyObject.has("color") ? 16777215 : keyObject.get("color").getAsInt()
         );
         this.deserializeBody(keyObject, e -> Theme.fromPath(e.getAsString()), key::with);
         return key;
      } else {
         return null;
      }
   }

   public <T> void deserializeBody(JsonObject object, Function<JsonElement, T> mapper, BiConsumer<Version, T> consumer) {
      for (String property : object.keySet()) {
         Version version = Version.fromName(property);
         if (version != null) {
            consumer.accept(version, mapper.apply(object.get(property)));
         }
      }
   }
}
