package iskallia.vault.config.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.processor.Processor;
import iskallia.vault.core.world.processor.entity.EntityProcessor;
import iskallia.vault.core.world.processor.tile.BernoulliWeightedTileProcessor;
import iskallia.vault.core.world.processor.tile.LeveledTileProcessor;
import iskallia.vault.core.world.processor.tile.ReferenceTileProcessor;
import iskallia.vault.core.world.processor.tile.SpawnerElementTileProcessor;
import iskallia.vault.core.world.processor.tile.SpawnerTileProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.processor.tile.VaultLootTileProcessor;
import iskallia.vault.core.world.processor.tile.WeightedTileProcessor;
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
                  processor.target = TilePredicate.of(object.get("target").getAsString(), true).orElse(TilePredicate.FALSE);
               }

               processor.probability = object.get("probability").getAsDouble();
               JsonObject success = object.get("success").getAsJsonObject();
               JsonObject failure = object.get("failure").getAsJsonObject();
               if (success != null) {
                  success.keySet().forEach(key -> {
                     PartialTile tile = PartialTile.parse(key, true).orElse(PartialTile.ERROR);
                     processor.success.put(tile, (Number)success.get(key).getAsInt());
                  });
               }

               if (failure != null) {
                  failure.keySet().forEach(key -> {
                     PartialTile tile = PartialTile.parse(key, true).orElse(PartialTile.ERROR);
                     processor.failure.put(tile, (Number)failure.get(key).getAsInt());
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
            case "spawner":
               SpawnerTileProcessor processorx = new SpawnerTileProcessor();
               processorx.target(object.get("target").getAsString());
               JsonArray output = object.get("output").getAsJsonArray();
               if (output == null) {
                  return processorx;
               }

               for (JsonElement e : output) {
                  WeightedList<PartialEntity> result = new WeightedList<>();
                  JsonObject element = e.getAsJsonObject();
                  JsonObject elements = element.get("elements").getAsJsonObject();
                  int weight = element.get("weight").getAsInt();
                  elements.keySet()
                     .forEach(key -> PartialEntity.parse(key, true).ifPresent(entity -> result.put(entity, (Number)elements.get(key).getAsInt())));
                  processorx.into(result, weight);
               }

               return processorx;
            case "spawner_element":
               SpawnerElementTileProcessor processor = new SpawnerElementTileProcessor();
               processor.target(object.get("target").getAsString());
               EntityPredicate.of(object.get("element").getAsString(), true).ifPresent(processor::setElement);
               JsonObject output = object.get("output").getAsJsonObject();
               if (output == null) {
                  return processor;
               }

               output.keySet().forEach(key -> PartialEntity.parse(key, true).ifPresent(entity -> processor.into(entity, output.get(key).getAsInt())));
               return processor;
            default:
               return TileProcessor.ofIdentity();
         }
      }
   }

   public JsonElement serialize(Processor<?> src, Type typeOfSrc, JsonSerializationContext context) {
      return null;
   }
}
