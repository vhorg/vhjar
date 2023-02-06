package iskallia.vault.dynamodel;

import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.MagnetItem;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicBakedModel implements BakedModel {
   private final BakedModel original;
   private final ItemOverrides overrides;

   public DynamicBakedModel(BakedModel original, ModelBakery loader) {
      this.original = original;
      BlockModel missing = (BlockModel)loader.getModel(ModelBakery.MISSING_MODEL_LOCATION);
      this.overrides = new ItemOverrides(loader, missing, id2 -> missing, Collections.emptyList()) {
         public BakedModel resolve(@NotNull BakedModel original, @NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            if (stack.getItem() instanceof DynamicModelItem dynamicModelItem) {
               if (stack.is(ModItems.MAGNET) && MagnetItem.hasLegacyData(stack)) {
                  MagnetItem.removeLegacyData(stack);
               }

               ResourceLocation modelId = dynamicModelItem.getDynamicModelId(stack).orElse(null);
               if (modelId == null) {
                  return original;
               } else {
                  DynamicModelRegistry<?> registry = ModDynamicModels.REGISTRIES.getAssociatedRegistry(stack.getItem()).orElse(null);
                  if (registry == null) {
                     return original;
                  } else {
                     DynamicModel<?> dynamicModel = (DynamicModel<?>)registry.get(modelId).orElse(null);
                     if (dynamicModel == null) {
                        return original;
                     } else {
                        ResourceLocation bakedId = dynamicModel.resolveBakedIcon(stack, world, entity, seed);
                        BakedModel bakedIcon = registry.getBakedIcon(bakedId);
                        return new DynamicBakedOverride(original, bakedIcon);
                     }
                  }
               }
            } else {
               return original;
            }
         }
      };
   }

   @Nonnull
   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random random) {
      return this.original.getQuads(state, side, random);
   }

   public boolean useAmbientOcclusion() {
      return this.original.useAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.original.isGui3d();
   }

   public boolean usesBlockLight() {
      return this.original.usesBlockLight();
   }

   public boolean isCustomRenderer() {
      return this.original.isCustomRenderer();
   }

   @NotNull
   public TextureAtlasSprite getParticleIcon() {
      return this.original.getParticleIcon();
   }

   @Nonnull
   public ItemTransforms getTransforms() {
      return this.original.getTransforms();
   }

   @Nonnull
   public ItemOverrides getOverrides() {
      return this.overrides;
   }
}
