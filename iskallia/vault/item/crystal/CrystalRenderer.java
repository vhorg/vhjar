package iskallia.vault.item.crystal;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.item.tool.SpecialItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrystalRenderer extends SpecialItemRenderer {
   public static final CrystalRenderer INSTANCE = new CrystalRenderer();

   public void renderByItem(
      @NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack matrices, @NotNull MultiBufferSource buffer, int light, int overlay
   ) {
      CrystalData data = CrystalData.read(stack);
      data.getModel().renderItem(this, data, stack, transformType, matrices, buffer, light, overlay);
   }
}
