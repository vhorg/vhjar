package iskallia.vault.dynamodel.model.item;

import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.baked.PlainBakedModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;

public class PlainItemModel extends DynamicModel<PlainItemModel> {
   public PlainItemModel(ResourceLocation id, String displayName) {
      super(id, displayName);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public BakedModel bakeModel(ModelResourceLocation modelLocation, ForgeModelBakery modelLoader, BlockModel unbakedModel) {
      return new PlainBakedModel(super.bakeModel(modelLocation, modelLoader, unbakedModel));
   }
}
