package iskallia.vault.item.crystal.model;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.tool.SpecialItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class CoreCrystalModel extends CrystalModel {
   @Override
   public void renderItem(
      SpecialItemRenderer renderer,
      CrystalData crystal,
      ItemStack stack,
      TransformType transformType,
      PoseStack matrices,
      MultiBufferSource buffer,
      int light,
      int overlay
   ) {
      float time = (float)ClientScheduler.INSTANCE.getTickCount();
      renderer.renderModel(this.getItemModel(), this.getItemColor(time), stack, transformType, matrices, buffer, light, overlay, null);
   }

   public abstract ModelResourceLocation getItemModel();

   public abstract int getItemColor(float var1);
}
