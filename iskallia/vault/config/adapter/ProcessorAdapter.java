package iskallia.vault.config.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.data.TileParser;
import iskallia.vault.core.world.data.TilePredicate;
import iskallia.vault.core.world.processor.Processor;
import iskallia.vault.core.world.processor.entity.EntityProcessor;
import iskallia.vault.core.world.processor.tile.BernoulliWeightedTileProcessor;
import iskallia.vault.core.world.processor.tile.LeveledTileProcessor;
import iskallia.vault.core.world.processor.tile.ReferenceTileProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.processor.tile.VaultLootTileProcessor;
import iskallia.vault.core.world.processor.tile.WeightedTileProcessor;
import iskallia.vault.init.ModBlocks;
import java.lang.reflect.Type;
import net.minecraft.resources.ResourceLocation;

public class ProcessorAdapter implements JsonSerializer<Processor<?>>, JsonDeserializer<Processor<?>> {
   public static final ProcessorAdapter INSTANCE = new ProcessorAdapter();

   public Processor<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      String type = object.get("type").getAsString();
      if (typeOfT != TileProcessor.class) {
         if (typeOfT == EntityProcessor.class) {
         }

         return null;
      } else {
         switch (type) {
            case "reference":
               return new ReferenceTileProcessor(new ResourceLocation(object.get("id").getAsString()));
            case "weighted_target":
               WeightedTileProcessor processorx = new WeightedTileProcessor();
               processorx.target(object.get("target").getAsString());
               JsonObject output = object.get("output").getAsJsonObject();
               if (output == null) {
                  return processorx;
               }

               output.keySet().forEach(key -> processor.into(key, output.get(key).getAsInt()));
               return processorx;
            case "bernoulli_weighted_target":
               BernoulliWeightedTileProcessor processor = new BernoulliWeightedTileProcessor();
               if (object.has("target")) {
                  processor.target = TilePredicate.of(object.get("target").getAsString());
               }

               processor.probability = object.get("probability").getAsDouble();
               JsonObject success = object.get("success").getAsJsonObject();
               JsonObject failure = object.get("failure").getAsJsonObject();
               if (success != null) {
                  success.keySet().forEach(key -> {
                     PartialTile tile = new TileParser(new StringReader(key), ModBlocks.ERROR_BLOCK, false).toTile();
                     processor.success.put(tile, success.get(key).getAsInt());
                  });
               }

               if (failure != null) {
                  failure.keySet().forEach(key -> {
                     PartialTile tile = new TileParser(new StringReader(key), ModBlocks.ERROR_BLOCK, false).toTile();
                     processor.failure.put(tile, failure.get(key).getAsInt());
                  });
               }

               return processor;
            case "leveled":
               LeveledTileProcessor processor = new LeveledTileProcessor();

               for (JsonElement entry : object.get("levels").getAsJsonArray()) {
                  processor.levels.put(entry.getAsJsonObject().get("level").getAsInt(), (TileProcessor)this.deserialize(entry, TileProcessor.class, context));
               }

               return processor;
            case "placeholder":
               VaultLootTileProcessor processorx = new VaultLootTileProcessor();
               processorx.target = PlaceholderBlock.Type.fromString(object.get("target").getAsString());

               for (JsonElement entry : object.get("levels").getAsJsonArray()) {
                  entry.getAsJsonObject().addProperty("type", "bernoulli_weighted_target");
                  processorx.levels.put(entry.getAsJsonObject().get("level").getAsInt(), (TileProcessor)this.deserialize(entry, TileProcessor.class, context));
               }

               return processorx;
            default:
               return TileProcessor.ofIdentity();
         }
      }
   }

   public JsonElement serialize(Processor<?> src, Type typeOfSrc, JsonSerializationContext context) {
      return null;
   }
}
