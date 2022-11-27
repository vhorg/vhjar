package iskallia.vault.dynamodel.registry;

import iskallia.vault.dynamodel.DynamicModel;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class DynamicModelRegistry<M extends DynamicModel<M>> {
   private final Map<ResourceLocation, M> REGISTRY = new HashMap<>();
   private final Map<ResourceLocation, BakedModel> BAKED_ICONS = new HashMap<>();
   private final Map<ResourceLocation, M> RESOURCES = new HashMap<>();

   public M register(M model) {
      ResourceLocation id = model.getId();
      if (this.REGISTRY.containsKey(id)) {
         throw new InternalError("Registry already contains an entry with given id -> " + id);
      } else {
         if (FMLEnvironment.dist == Dist.CLIENT) {
            this.registerResourceAssociations(model);
         }

         this.REGISTRY.put(id, model);
         return model;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void registerResourceAssociations(M model) {
      model.getAssociatedModelLocations().forEach(modelLocation -> {
         ResourceLocation resourceLocation = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath());
         this.RESOURCES.put(resourceLocation, model);
      });
   }

   @OnlyIn(Dist.CLIENT)
   public Optional<M> getByResourceLocation(ResourceLocation resourceLocation) {
      return Optional.ofNullable(this.RESOURCES.get(resourceLocation));
   }

   public boolean containsId(ResourceLocation id) {
      return this.REGISTRY.containsKey(id);
   }

   public Optional<M> get(ResourceLocation id) {
      return Optional.ofNullable(this.REGISTRY.get(id));
   }

   public Set<ResourceLocation> getIds() {
      return this.REGISTRY.keySet();
   }

   public void bakeIcon(ResourceLocation id, BakedModel bakedModel) {
      this.BAKED_ICONS.put(id, bakedModel);
   }

   public BakedModel getBakedIcon(ResourceLocation id) {
      return this.BAKED_ICONS.get(id);
   }

   public void forEach(BiConsumer<ResourceLocation, ? super M> action) {
      this.REGISTRY.forEach(action);
   }
}
