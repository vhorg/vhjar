package iskallia.vault.dynamodel;

import iskallia.vault.dynamodel.baked.JsonFileBakedModel;
import iskallia.vault.dynamodel.baked.PlainBakedModel;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DynamicBakedOverride implements BakedModel {
   protected BakedModel original;
   protected BakedModel override;

   public DynamicBakedOverride(BakedModel original, BakedModel override) {
      this.original = original;
      this.override = override;
   }

   @Nonnull
   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random random) {
      return this.override.getQuads(state, side, random);
   }

   public boolean useAmbientOcclusion() {
      return this.override instanceof JsonFileBakedModel ? this.override.useAmbientOcclusion() : this.original.useAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.override instanceof JsonFileBakedModel ? this.override.isGui3d() : this.original.isGui3d();
   }

   public boolean usesBlockLight() {
      return this.original.usesBlockLight();
   }

   public boolean isCustomRenderer() {
      return this.override.isCustomRenderer();
   }

   @Nonnull
   public TextureAtlasSprite getParticleIcon() {
      return this.override.getParticleIcon();
   }

   @Nonnull
   public ItemTransforms getTransforms() {
      return this.override instanceof PlainBakedModel ? this.original.getTransforms() : this.override.getTransforms();
   }

   @Nonnull
   public ItemOverrides getOverrides() {
      return this.override.getOverrides();
   }
}
