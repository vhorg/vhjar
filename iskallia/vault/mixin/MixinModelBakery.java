package iskallia.vault.mixin;

import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.item.tool.IManualModelLoading;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ModelBakery.class})
public abstract class MixinModelBakery {
   @Final
   @Shadow
   protected ResourceManager resourceManager;

   @Shadow
   protected abstract void loadTopLevel(ModelResourceLocation var1);

   @Inject(
      method = {"loadBlockModel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void loadBlockModel(ResourceLocation id, CallbackInfoReturnable<BlockModel> cir) {
      if (id.getPath().startsWith("item/")) {
         ResourceLocation itemModelLocation = DynamicModel.removePrefixFromId("item/", id);
         ModDynamicModels.REGISTRIES.getModelByResourceLocation(itemModelLocation).ifPresent(dynamicModel -> {
            BlockModel itemModel = this.loadItemModel(id, (DynamicModel<?>)dynamicModel);
            itemModel.name = id.toString();
            cir.setReturnValue(itemModel);
         });
         if (id.getNamespace().equals("the_vault") && id.getPath().startsWith("item/tool/")) {
            StringReader reader = new StringReader("{\"parent\":\"item/handheld\",\"textures\":{\"layer0\":\"%s\"}}".formatted(id.toString()));

            try {
               BlockModel model = BlockModel.fromStream(reader);
               model.name = id.toString();
               cir.setReturnValue(model);
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         }
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

   @Redirect(
      method = {"processLoading"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"
      )
   )
   private void onItemLoading(ProfilerFiller profiler, String phase) {
      Registry.ITEM.forEach(item -> {
         if (item instanceof IManualModelLoading loader) {
            loader.loadModels(this::loadTopLevel);
         }
      });
      profiler.popPush(phase);
   }
}
