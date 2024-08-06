package iskallia.vault.item;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.tool.SpecialItemRenderer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CardDeckItemRenderer extends SpecialItemRenderer {
   public static final CardDeckItemRenderer INSTANCE = new CardDeckItemRenderer();

   public void renderByItem(
      @NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack matrices, @NotNull MultiBufferSource buffer, int light, int overlay
   ) {
      String id = CardDeckItem.getId(stack);
      String model = ModConfigs.CARD_DECK.getModel(id).orElse(null);
      if (model == null) {
         List<String> models = new ArrayList<>(ModConfigs.CARD_DECK.getModels());
         model = models.get((int)(ClientScheduler.INSTANCE.getTickCount() >> 4) % models.size());
      }

      ModelResourceLocation base = new ModelResourceLocation(model);
      this.renderModel(base, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
   }
}
