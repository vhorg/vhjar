package iskallia.vault.dynamodel;

import iskallia.vault.init.ModDynamicModels;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.SimpleModelState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DynamicModel<M extends DynamicModel<M>> {
   protected final ResourceLocation id;
   protected final String displayName;
   protected DynamicModelProperties modelProperties = new DynamicModelProperties();

   public DynamicModel(ResourceLocation id, String displayName) {
      this.id = id;
      this.displayName = displayName;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public DynamicModelProperties getModelProperties() {
      return this.modelProperties;
   }

   public M properties(DynamicModelProperties modelProperties) {
      this.modelProperties = modelProperties;
      return (M)this;
   }

   @OnlyIn(Dist.CLIENT)
   public Set<ModelResourceLocation> getAssociatedModelLocations() {
      return Collections.singleton(new ModelResourceLocation(this.getId(), "inventory"));
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation resolveBakedIcon(@NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
      return this.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public Map<String, ResourceLocation> resolveTextures(ResourceManager resourceManager, ResourceLocation resourceLocation) {
      HashMap<String, ResourceLocation> textures = new HashMap<>();
      textures.put("layer0", ModDynamicModels.textureExists(resourceManager, resourceLocation) ? resourceLocation : ModDynamicModels.EMPTY_TEXTURE);
      if (ModDynamicModels.textureExists(resourceManager, appendToId(resourceLocation, "_overlay"))) {
         textures.put("layer1", appendToId(resourceLocation, "_overlay"));
      }

      for (int i = 0; i < 10; i++) {
         if (ModDynamicModels.textureExists(resourceManager, appendToId(resourceLocation, "_layer" + i))) {
            textures.put("layer" + i, appendToId(resourceLocation, "_layer" + i));
         }
      }

      return textures;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockModel generateItemModel(Map<String, ResourceLocation> textures) {
      String jsonPattern = "{  \"parent\": \"item/generated\",  \"textures\": {{textures}},  \"display\": {    \"thirdperson_lefthand\": {      \"rotation\": [0.0, 0.0, 0.0],      \"translation\": [0.0, 0.1875, 0.0625],      \"scale\": [0.55, 0.55, 0.55]    },    \"thirdperson_righthand\": {      \"rotation\": [0.0, 0.0, 0.0],      \"translation\": [0.0, 0.1875, 0.0625],      \"scale\": [0.55, 0.55, 0.55]    },    \"firstperson_lefthand\": {      \"rotation\": [0.0, -90.0, 25.0],      \"translation\": [0.070625, 0.2, 0.070625],      \"scale\": [0.68, 0.68, 0.68]    },    \"firstperson_righthand\": {      \"rotation\": [0.0, -90.0, 25.0],      \"translation\": [0.070625, 0.2, 0.070625],      \"scale\": [0.68, 0.68, 0.68]    },    \"head\": {      \"rotation\": [0, 180, 0],      \"translation\": [0.0, 0.8125, 0.4375],      \"scale\": [1, 1, 1]    },    \"gui\": {      \"rotation\": [0, 0, 0],      \"translation\": [0, 0, 0],      \"scale\": [1, 1, 1]    },    \"ground\": {      \"rotation\": [0, 0, 0],      \"translation\": [0.0, 0.125, 0.0],      \"scale\": [0.5, 0.5, 0.5]    },    \"fixed\": {      \"rotation\": [0.0, 180.0, 0.0],      \"translation\": [0.0, 0.0, 0.0],      \"scale\": [0.5, 0.5, 0.5]    }  }}";
      return this.createUnbakedModel(jsonPattern, textures);
   }

   @OnlyIn(Dist.CLIENT)
   protected BlockModel createUnbakedModel(String jsonPattern, Map<String, ResourceLocation> textures) {
      String texturesJson = textures.entrySet()
         .stream()
         .map(entry -> "\"" + entry.getKey() + "\": \"" + entry.getValue() + "\"")
         .collect(Collectors.joining(", ", "{", "}"));
      String modelJson = jsonPattern.replace("{{textures}}", texturesJson);
      return BlockModel.fromString(modelJson);
   }

   @OnlyIn(Dist.CLIENT)
   public BakedModel bakeModel(ModelResourceLocation modelLocation, ForgeModelBakery modelLoader, BlockModel unbakedModel) {
      BlockModel itemModel = new ItemModelGenerator().generateBlockModel(ForgeModelBakery.defaultTextureGetter(), unbakedModel);
      return itemModel.bake(modelLoader, itemModel, ForgeModelBakery.defaultTextureGetter(), SimpleModelState.IDENTITY, modelLocation, false);
   }

   public static ResourceLocation appendToId(ResourceLocation id, String append) {
      return new ResourceLocation(id.getNamespace(), id.getPath() + append);
   }

   public static ResourceLocation prependToId(String prepend, ResourceLocation id) {
      return new ResourceLocation(id.getNamespace(), prepend + id.getPath());
   }

   public static ResourceLocation removePrefixFromId(String prefix, ResourceLocation id) {
      return id.getPath().startsWith(prefix) ? new ResourceLocation(id.getNamespace(), id.getPath().replaceFirst(prefix, "")) : id;
   }
}
