package iskallia.vault.dynamodel.registry;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.dynamodel.DynamicModel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class DynamicModelRegistries {
   private final Map<Item, DynamicModelRegistry<?>> ITEM_TO_REGISTRY = new HashMap<>();
   private final Map<DynamicModelRegistry<?>, Item> REGISTRY_TO_ITEM = new HashMap<>();

   public DynamicModelRegistries associate(Item item, DynamicModelRegistry<?> registry) {
      this.ITEM_TO_REGISTRY.put(item, registry);
      this.REGISTRY_TO_ITEM.put(registry, item);
      return this;
   }

   public Set<DynamicModelRegistry<?>> getUniqueRegistries() {
      return Collections.unmodifiableSet(this.REGISTRY_TO_ITEM.keySet());
   }

   public Set<Item> getUniqueItems() {
      return Collections.unmodifiableSet(this.ITEM_TO_REGISTRY.keySet());
   }

   public Item getAssociatedItem(DynamicModelRegistry<?> registry) {
      return this.REGISTRY_TO_ITEM.get(registry);
   }

   public Optional<DynamicModelRegistry<?>> getAssociatedRegistry(Item item) {
      return Optional.ofNullable(this.ITEM_TO_REGISTRY.get(item));
   }

   public Optional<? extends DynamicModel<?>> getModel(Item item, ResourceLocation modelId) {
      return this.getAssociatedRegistry(item).flatMap(registry -> (Optional<? extends DynamicModel<?>>)registry.get(modelId));
   }

   @OnlyIn(Dist.CLIENT)
   public Optional<? extends DynamicModel<?>> getModelByResourceLocation(ResourceLocation resourceLocation) {
      for (DynamicModelRegistry<?> uniqueRegistry : this.getUniqueRegistries()) {
         Optional<? extends DynamicModel<?>> modelOptional = (Optional<? extends DynamicModel<?>>)uniqueRegistry.getByResourceLocation(resourceLocation);
         if (modelOptional.isPresent()) {
            return modelOptional;
         }
      }

      return Optional.empty();
   }

   public Optional<Pair<? extends DynamicModel<?>, Item>> getModelAndAssociatedItem(ResourceLocation modelId) {
      for (DynamicModelRegistry<?> uniqueRegistry : this.getUniqueRegistries()) {
         Optional<? extends DynamicModel<?>> modelOptional = (Optional<? extends DynamicModel<?>>)uniqueRegistry.get(modelId);
         if (modelOptional.isPresent()) {
            Item associatedItem = this.getAssociatedItem(uniqueRegistry);
            return Optional.of(Pair.of(modelOptional.get(), associatedItem));
         }
      }

      return Optional.empty();
   }
}
