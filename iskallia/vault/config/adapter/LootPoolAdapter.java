package iskallia.vault.config.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.util.WeightedTree;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootRoll;
import iskallia.vault.core.world.loot.entry.ItemLootEntry;
import iskallia.vault.core.world.loot.entry.LootEntry;
import iskallia.vault.core.world.loot.entry.ReferenceLootEntry;
import java.lang.reflect.Type;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class LootPoolAdapter extends WeightedTreeAdapter<LootEntry> {
   public static final LootPoolAdapter INSTANCE = new LootPoolAdapter();

   @Override
   public WeightedTree<LootEntry> create() {
      return new LootPool();
   }

   public String getName(LootEntry value) {
      if (value instanceof ItemLootEntry) {
         return "item";
      } else {
         return value instanceof ReferenceLootEntry ? "reference" : null;
      }
   }

   @Override
   public <V extends JsonSerializer<LootEntry> & JsonDeserializer<LootEntry>> V getAdapter(String name) {
      return (V)(switch (name) {
         case "item" -> (JsonSerializer)LootPoolAdapter.ItemEntry.INSTANCE;
         case "reference" -> (JsonSerializer)LootPoolAdapter.ReferenceEntry.INSTANCE;
         default -> null;
      });
   }

   public static class ItemEntry implements JsonSerializer<ItemLootEntry>, JsonDeserializer<ItemLootEntry> {
      public static final LootPoolAdapter.ItemEntry INSTANCE = new LootPoolAdapter.ItemEntry();

      public ItemLootEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         Item item = Items.AIR;
         CompoundTag nbt = null;
         LootRoll count = LootRoll.ofConstant(1);
         if (object.has("id")) {
            item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(object.get("id").getAsString()));
         }

         if (object.has("nbt")) {
            try {
               nbt = TagParser.parseTag(object.get("nbt").getAsString());
            } catch (CommandSyntaxException var9) {
               var9.printStackTrace();
            }
         }

         if (object.has("count")) {
            count = LootRollAdapter.INSTANCE.deserialize(object.get("count"), typeOfT, context);
         }

         return new ItemLootEntry(item, nbt, count);
      }

      public JsonElement serialize(ItemLootEntry value, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject object = new JsonObject();
         object.addProperty("id", value.getItem().getRegistryName().toString());
         if (value.getNbt() != null) {
            object.addProperty("nbt", value.getNbt().toString());
         }

         object.add("count", LootRollAdapter.INSTANCE.serialize(value.getCount(), typeOfSrc, context));
         return object;
      }
   }

   public static class ReferenceEntry implements JsonSerializer<ReferenceLootEntry>, JsonDeserializer<ReferenceLootEntry> {
      public static final LootPoolAdapter.ReferenceEntry INSTANCE = new LootPoolAdapter.ReferenceEntry();

      public ReferenceLootEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         return new ReferenceLootEntry(new ResourceLocation(json.getAsString()));
      }

      public JsonElement serialize(ReferenceLootEntry value, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(value.getReferenceId().toString());
      }
   }
}
