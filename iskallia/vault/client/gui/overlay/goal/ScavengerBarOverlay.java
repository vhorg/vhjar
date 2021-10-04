package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.vault.goal.VaultScavengerData;
import iskallia.vault.world.vault.logic.objective.ScavengerHuntObjective;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ScavengerBarOverlay extends BossBarOverlay {
   private final VaultScavengerData data;

   public ScavengerBarOverlay(VaultScavengerData data) {
      this.data = data;
   }

   @Override
   public boolean shouldDisplay() {
      List<ScavengerHuntObjective.ItemSubmission> items = this.data.getRequiredItemSubmissions();
      return !items.isEmpty();
   }

   @Override
   public int drawOverlay(MatrixStack renderStack, float pTicks) {
      List<ScavengerHuntObjective.ItemSubmission> items = this.data.getRequiredItemSubmissions();
      Minecraft mc = Minecraft.func_71410_x();
      int midX = mc.func_228018_at_().func_198107_o() / 2;
      int gapWidth = 7;
      int itemBoxWidth = 32;
      int totalWidth = items.size() * itemBoxWidth + (items.size() - 1) * gapWidth;
      int shiftX = -totalWidth / 2 + itemBoxWidth / 2;
      mc.func_110434_K().func_110577_a(PlayerContainer.field_226615_c_);
      renderStack.func_227860_a_();
      int yOffset = 0;
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(midX + shiftX, itemBoxWidth * 0.75F, 0.0);

      for (ScavengerHuntObjective.ItemSubmission itemRequirement : items) {
         int reqYOffset = renderItemRequirement(renderStack, itemRequirement, itemBoxWidth);
         if (reqYOffset > yOffset) {
            yOffset = reqYOffset;
         }

         renderStack.func_227861_a_(itemBoxWidth + gapWidth, 0.0, 0.0);
      }

      renderStack.func_227865_b_();
      return yOffset;
   }

   private static int renderItemRequirement(MatrixStack renderStack, ScavengerHuntObjective.ItemSubmission itemRequirement, int itemBoxWidth) {
      Minecraft mc = Minecraft.func_71410_x();
      ItemStack requiredStack = new ItemStack(itemRequirement.getRequiredItem());
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(0.0, -itemBoxWidth / 2.0F, 0.0);
      renderItemStack(renderStack, requiredStack);
      renderStack.func_227861_a_(0.0, 10.0, 0.0);
      String requiredText = itemRequirement.getCurrentAmount() + "/" + itemRequirement.getRequiredAmount();
      IFormattableTextComponent cmp = new StringTextComponent(requiredText).func_240699_a_(TextFormatting.GREEN);
      UIHelper.renderCenteredWrappedText(renderStack, cmp, 30, 0);
      renderStack.func_227861_a_(0.0, 10.0, 0.0);
      renderStack.func_227860_a_();
      renderStack.func_227862_a_(0.5F, 0.5F, 1.0F);
      ITextComponent name = requiredStack.func_200301_q();
      IFormattableTextComponent display = name.func_230532_e_().func_240699_a_(TextFormatting.WHITE);
      int lines = UIHelper.renderCenteredWrappedText(renderStack, display, 60, 0);
      renderStack.func_227865_b_();
      renderStack.func_227865_b_();
      return 25 + lines * 5;
   }

   private static void renderItemStack(MatrixStack renderStack, ItemStack item) {
      Minecraft mc = Minecraft.func_71410_x();
      ItemRenderer ir = mc.func_175599_af();
      FontRenderer fr = item.func_77973_b().getFontRenderer(item);
      if (fr == null) {
         fr = mc.field_71466_p;
      }

      renderStack.func_227861_a_(-8.0, -8.0, 0.0);
      RenderSystem.pushMatrix();
      RenderSystem.multMatrix(renderStack.func_227866_c_().func_227870_a_());
      ir.field_77023_b = 200.0F;
      ir.func_180450_b(item, 0, 0);
      ir.func_180453_a(fr, item, 0, 0, null);
      ir.field_77023_b = 0.0F;
      RenderSystem.popMatrix();
      renderStack.func_227861_a_(8.0, 8.0, 0.0);
   }
}
