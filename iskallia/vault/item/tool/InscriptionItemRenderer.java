package iskallia.vault.item.tool;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.item.InscriptionItem;
import iskallia.vault.item.data.InscriptionData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class InscriptionItemRenderer extends SpecialItemRenderer {
   public static final InscriptionItemRenderer INSTANCE = new InscriptionItemRenderer();

   public void renderByItem(
      @NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack matrices, @NotNull MultiBufferSource buffer, int light, int overlay
   ) {
      int model = InscriptionData.from(stack).getModel();
      ModelResourceLocation core = new ModelResourceLocation("the_vault:inscription/core#inventory");
      ModelResourceLocation shape = new ModelResourceLocation("the_vault:inscription/%d#inventory".formatted(model));
      this.renderModel(core, InscriptionItem.getColor(stack), stack, transformType, matrices, buffer, light, overlay, null);
      this.renderModel(shape, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
   }
}
