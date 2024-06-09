package iskallia.vault.item.tool;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CatalystItemRenderer extends SpecialItemRenderer {
   public static final CatalystItemRenderer INSTANCE = new CatalystItemRenderer();

   public void renderByItem(
      @NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack matrices, @NotNull MultiBufferSource buffer, int light, int overlay
   ) {
      int model = stack.getTag() == null ? 0 : stack.getTag().getInt("model");
      ModelResourceLocation shape = new ModelResourceLocation("the_vault:catalyst/%d#inventory".formatted(model));
      this.renderModel(shape, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
   }
}
