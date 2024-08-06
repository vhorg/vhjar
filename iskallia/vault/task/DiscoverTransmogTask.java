package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.world.data.DiscoveredModelsData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class DiscoverTransmogTask extends ConsumableTask<DiscoverTransmogTask.Config> {
   public DiscoverTransmogTask() {
      super(new DiscoverTransmogTask.Config());
   }

   public DiscoverTransmogTask(DiscoverTransmogTask.Config config) {
      super(config);
   }

   @Override
   protected void onConsume(TaskContext context) {
      if (context.getSource() instanceof EntityTaskSource entityTaskSource) {
         DiscoveredModelsData data = DiscoveredModelsData.get(context.getServer());

         for (Player player : entityTaskSource.getEntities(Player.class)) {
            for (ResourceLocation transmog : this.getConfig().transmogs) {
               ModDynamicModels.REGISTRIES
                  .getModelAndAssociatedItem(transmog)
                  .ifPresent(pair -> data.discoverModelAndBroadcast((Item)pair.getSecond(), ((DynamicModel)pair.getFirst()).getId(), player));
            }
         }
      }
   }

   public static class Config extends ConfiguredTask.Config {
      public List<ResourceLocation> transmogs = new ArrayList<>();
      private static final ArrayAdapter<ResourceLocation> ADAPTER = Adapters.ofArray(ResourceLocation[]::new, Adapters.IDENTIFIER);

      public Config() {
      }

      public Config(List<ResourceLocation> transmogs) {
         this.transmogs = transmogs;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         ADAPTER.writeBits(this.transmogs.toArray(ResourceLocation[]::new), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.transmogs = Arrays.stream(ADAPTER.readBits(buffer).orElseThrow()).toList();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            ADAPTER.writeNbt(this.transmogs.toArray(ResourceLocation[]::new)).ifPresent(value -> nbt.put("transmogs", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.transmogs = Arrays.stream(ADAPTER.readNbt(nbt.get("transmogs")).orElse(new ResourceLocation[0])).toList();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            ADAPTER.writeJson(this.transmogs.toArray(ResourceLocation[]::new)).ifPresent(value -> json.add("transmogs", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.transmogs = Arrays.stream(ADAPTER.readJson(json.get("transmogs")).orElse(new ResourceLocation[0])).toList();
      }
   }
}
