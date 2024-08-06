package iskallia.vault.item;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.card.Card;
import iskallia.vault.core.card.CardEntry;
import iskallia.vault.item.tool.SpecialItemRenderer;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CardItemRenderer extends SpecialItemRenderer {
   public static final CardItemRenderer INSTANCE = new CardItemRenderer();

   public void renderByItem(
      @NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack matrices, @NotNull MultiBufferSource buffer, int light, int overlay
   ) {
      Card card = CardItem.getCard(stack);
      CardEntry.Color color = card.getFirstColor();
      Set<CardEntry.Color> colors = card.getColors();
      if (color != null) {
         if (card.hasGroup("Wild")) {
            StringBuilder prefix = new StringBuilder();

            for (CardEntry.Color value : CardEntry.Color.values()) {
               if (colors.contains(value)) {
                  prefix.append(value.name().toLowerCase()).append("_");
               }
            }

            ModelResourceLocation base = new ModelResourceLocation("the_vault:card/%swild#inventory".formatted(prefix));
            this.renderModel(base, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
         } else if (card.hasGroup("Arcane")) {
            ModelResourceLocation base = new ModelResourceLocation("the_vault:card/%s_arcane#inventory".formatted(color.name().toLowerCase()));
            this.renderModel(base, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
         } else if (card.hasGroup("Temporal")) {
            ModelResourceLocation base = new ModelResourceLocation(
               "the_vault:card/%s_temporal_%d#inventory".formatted(color.name().toLowerCase(), card.getTier())
            );
            this.renderModel(base, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
         } else if (card.hasGroup("Resource")) {
            ModelResourceLocation base = new ModelResourceLocation("the_vault:card/%s_resource#inventory".formatted(color.name().toLowerCase()));
            this.renderModel(base, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
         } else if (card.hasGroup("Evolution")) {
            ModelResourceLocation base = new ModelResourceLocation(
               "the_vault:card/%s_evolution_%d#inventory".formatted(color.name().toLowerCase(), card.getTier())
            );
            this.renderModel(base, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
         } else {
            ModelResourceLocation base = new ModelResourceLocation("the_vault:card/%s_%d#inventory".formatted(color.name().toLowerCase(), card.getTier()));
            this.renderModel(base, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
         }
      }

      matrices.pushPose();
      if (card.getFirstModel() != null) {
         ModelResourceLocation icon = new ModelResourceLocation(card.getFirstModel());
         this.renderModel(icon, 16777215, stack, transformType, matrices, buffer, light, overlay, null, () -> {
            matrices.scale(0.5F, 0.5F, 1.0F);
            translate(matrices, card);
         });
      }

      matrices.popPose();
   }

   public static void translate(PoseStack matrices, Card card) {
      if (card.hasGroup("Wild")) {
         matrices.translate(0.5, 0.53F, 0.02F);
      } else if (card.hasGroup("Arcane")) {
         matrices.translate(0.5, 0.53F, 0.02F);
      } else if (card.hasGroup("Temporal")) {
         matrices.translate(0.5, 0.53F, 0.02F);
      } else if (card.hasGroup("Resource")) {
         matrices.translate(0.5, 0.5, 0.02F);
      } else if (card.hasGroup("Evolution")) {
         matrices.translate(0.5, 0.75, 0.02F);
      } else {
         matrices.translate(0.5, 0.75, 0.02F);
      }
   }
}
