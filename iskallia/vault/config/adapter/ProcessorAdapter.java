package iskallia.vault.config.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.processor.Processor;
import iskallia.vault.core.world.processor.entity.EntityProcessor;
import iskallia.vault.core.world.processor.tile.BernoulliWeightedTileProcessor;
import iskallia.vault.core.world.processor.tile.FilterTileProcessor;
import iskallia.vault.core.world.processor.tile.LeveledTileProcessor;
import iskallia.vault.core.world.processor.tile.ReferenceTileProcessor;
import iskallia.vault.core.world.processor.tile.SpawnerElementTileProcessor;
import iskallia.vault.core.world.processor.tile.SpawnerTileProcessor;
import iskallia.vault.core.world.processor.tile.TemplateStackSpawnerProcessor;
import iskallia.vault.core.world.processor.tile.TemplateStackTileProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.processor.tile.VaultLootTileProcessor;
import iskallia.vault.core.world.processor.tile.WeightedTileProcessor;
import java.lang.reflect.Type;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ProcessorAdapter implements JsonSerializer<Processor<?>>, JsonDeserializer<Processor<?>> {
   public static final ProcessorAdapter INSTANCE = new ProcessorAdapter();

   public Processor<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      String type = object.get("type").getAsString();
      if (typeOfT == TileProcessor.class) {
         switch (type) {
            case "reference":
               if (object.has("id")) {
                  return new ReferenceTileProcessor(new WeightedList<ResourceLocation>().add(new ResourceLocation(object.get("id").getAsString()), 1.0));
               } else if (object.has("pool")) {
                  JsonObject pool = object.getAsJsonObject("pool");
                  ReferenceTileProcessor processorxx = new ReferenceTileProcessor(new WeightedList<>());

                  for (String s : pool.keySet()) {
                     processorxx.getPool().add(new ResourceLocation(s), pool.get(s).getAsDouble());
                  }

                  return processorxx;
               }
            default:
               return TileProcessor.ofIdentity();
            case "weighted_target":
               WeightedTileProcessor processorx = new WeightedTileProcessor();
               JsonElement thing = object.get("target");
               if (thing instanceof JsonPrimitive || thing instanceof JsonArray) {
                  processorx.target(Adapters.TILE_PREDICATE.readJson(thing).orElse(TilePredicate.FALSE));
               } else if (thing instanceof JsonObject object1) {
                  TilePredicate block = Adapters.TILE_PREDICATE
                     .readJson(object1.get("block"))
                     .orElseGet(() -> TilePredicate.of("", true).orElse(TilePredicate.FALSE));
                  CompoundTag nbt;
                  if (object1.has("nbt")) {
                     nbt = Adapters.COMPOUND_NBT.readJson(object1.get("nbt")).orElse(null);
                  } else {
                     nbt = Adapters.COMPOUND_NBT.readJson(object1).orElse(new CompoundTag());
                     nbt.remove("block");
                     nbt.remove("weight");
                     if (nbt.isEmpty()) {
                        nbt = null;
                     }
                  }

                  if (block instanceof PartialTile tile) {
                     tile.setEntity(PartialCompoundNbt.of(nbt));
                  }

                  processorx.target(block);
               }

               JsonElement output = object.get("output");
               if (output instanceof JsonObject object1) {
                  object1.keySet().forEach(key -> processor.into(key, object1.get(key).getAsInt()));
               } else if (output instanceof JsonArray array) {
                  array.forEach(elementx -> {
                     JsonObject object1 = elementx.getAsJsonObject();
                     String blockx = !object1.has("block") ? "" : object1.get("block").getAsString();
                     CompoundTag nbtx;
                     if (object1.has("nbt")) {
                        nbtx = Adapters.COMPOUND_NBT.readJson(object1.get("nbt")).orElse(null);
                     } else {
                        nbtx = Adapters.COMPOUND_NBT.readJson(object1).orElse(new CompoundTag());
                        nbtx.remove("block");
                        nbtx.remove("weight");
                        if (nbtx.isEmpty()) {
                           nbtx = null;
                        }
                     }

                     PartialTile tile = PartialTile.parse(blockx, true).orElse(PartialTile.ERROR);
                     tile.setEntity(PartialCompoundNbt.of(nbtx));
                     processor.into(tile, object1.get("weight").getAsInt());
                  });
               }

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
               LeveledTileProcessor processorx = new LeveledTileProcessor();

               for (JsonElement entry : object.get("levels").getAsJsonArray()) {
                  processorx.levels.put(entry.getAsJsonObject().get("level").getAsInt(), (TileProcessor)this.deserialize(entry, TileProcessor.class, context));
               }

               return processorx;
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
            case "template_stack_tile":
               TemplateStackTileProcessor processor = new TemplateStackTileProcessor();
               processor.target(object.get("target").getAsString());
               JsonElement stack = object.get("stack");
               if (stack.isJsonArray()) {
                  stack.getAsJsonArray().forEach(ex -> processor.stack(ex.getAsString()));
               } else {
                  processor.stack(stack.getAsString());
               }

               return processor;
            case "template_stack_spawner":
               TemplateStackSpawnerProcessor processor = new TemplateStackSpawnerProcessor();
               processor.target(object.get("target").getAsString());
               JsonElement stack = object.get("stack");
               if (stack.isJsonArray()) {
                  stack.getAsJsonArray().forEach(ex -> processor.stack(ex.getAsString()));
               } else {
                  processor.stack(stack.getAsString());
               }

               return processor;
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
            case "filter":
               JsonElement target = object.get("target");
               return new FilterTileProcessor(target.getAsString());
         }
      } else {
         if (typeOfT == EntityProcessor.class) {
         }

         return null;
      }
   }

   public JsonElement serialize(Processor<?> src, Type typeOfSrc, JsonSerializationContext context) {
      return null;
   }
}
