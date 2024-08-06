package iskallia.vault.item;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.config.card.BoosterPackConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.tool.SpecialItemRenderer;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BoosterPackItemRenderer extends SpecialItemRenderer {
   public static final BoosterPackItemRenderer INSTANCE = new BoosterPackItemRenderer();

   public void renderByItem(
      @NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack matrices, @NotNull MultiBufferSource buffer, int light, int overlay
   ) {
      String id = BoosterPackItem.getId(stack);
      String model = ModConfigs.BOOSTER_PACK
         .getModel(id)
         .map(config -> BoosterPackItem.getOutcomes(stack) == null ? config.getUnopened() : config.getOpened())
         .orElse(null);
      if (model == null) {
         List<String> models = ModConfigs.BOOSTER_PACK.getModels().stream().map(BoosterPackConfig.BoosterPackModel::getUnopened).toList();
         model = models.get((int)(ClientScheduler.INSTANCE.getTickCount() >> 4) % models.size());
      }

      ModelResourceLocation base = new ModelResourceLocation(model);
      this.renderModel(base, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
   }
}
