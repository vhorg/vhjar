package iskallia.vault.mixin;

import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.init.ModDynamicModels;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ModelBakery.class})
public class MixinModelBakery {
   @Final
   @Shadow
   protected ResourceManager resourceManager;

   @Inject(
      method = {"loadBlockModel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void loadBlockModel(ResourceLocation resourceLocation, CallbackInfoReturnable<BlockModel> cir) {
      if (resourceLocation.getPath().startsWith("item/")) {
         ResourceLocation itemModelLocation = DynamicModel.removePrefixFromId("item/", resourceLocation);
         ModDynamicModels.REGISTRIES.getModelByResourceLocation(itemModelLocation).ifPresent(dynamicModel -> {
            BlockModel itemModel = this.loadItemModel(resourceLocation, (DynamicModel<?>)dynamicModel);
            itemModel.name = resourceLocation.toString();
            cir.setReturnValue(itemModel);
         });
      }
   }

   private BlockModel loadItemModel(ResourceLocation resourceLocation, DynamicModel<?> dynamicModel) {
      ResourceManager resourceManager = this.resourceManager;
      if (ModDynamicModels.jsonModelExists(resourceManager, resourceLocation)) {
         try {
            ResourceLocation modelPath = new ResourceLocation(resourceLocation.getNamespace(), "models/" + resourceLocation.getPath() + ".json");
            Resource resource = resourceManager.getResource(modelPath);
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return BlockModel.fromStream(reader);
         } catch (IOException var7) {
            throw new RuntimeException(var7);
         }
      } else {
         Map<String, ResourceLocation> textures = dynamicModel.resolveTextures(resourceManager, resourceLocation);
         return dynamicModel.generateItemModel(textures);
      }
   }
}
