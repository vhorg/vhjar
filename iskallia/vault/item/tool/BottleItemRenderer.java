package iskallia.vault.item.tool;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.item.BottleItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BottleItemRenderer extends SpecialItemRenderer {
   public static final BottleItemRenderer INSTANCE = new BottleItemRenderer();

   public void renderByItem(
      @NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack matrices, @NotNull MultiBufferSource buffer, int light, int overlay
   ) {
      BottleItem.Type type = BottleItem.getType(stack).orElse(null);
      if (type == null) {
         type = BottleItem.Type.values()[(int)(ClientScheduler.INSTANCE.getTickCount() / 16L) % BottleItem.Type.values().length];
      }

      ModelResourceLocation empty = new ModelResourceLocation("the_vault:bottle/%s/empty#inventory".formatted(type.getName()));
      ModelResourceLocation juice = new ModelResourceLocation("the_vault:bottle/%s/juice#inventory".formatted(type.getName()));
      if (BottleItem.getCharges(stack) > 0) {
         this.renderModel(juice, BottleItem.getColor(stack), stack, transformType, matrices, buffer, light, overlay, null);
      }

      this.renderModel(empty, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
   }
}
