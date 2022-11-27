package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.helper.MobHeadTextures;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.vault.goal.VaultScavengerData;
import iskallia.vault.config.LegacyScavengerHuntConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.logic.objective.LegacyScavengerHuntObjective;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.RenderProperties;

public class ScavengerBarOverlay extends BossBarOverlay {
   private final VaultScavengerData data;

   public ScavengerBarOverlay(VaultScavengerData data) {
      this.data = data;
   }

   @Override
   public boolean shouldDisplay() {
      List<LegacyScavengerHuntObjective.ItemSubmission> items = this.data.getRequiredItemSubmissions();
      return !items.isEmpty();
   }

   @Override
   public int drawOverlay(PoseStack renderStack, float pTicks) {
      List<LegacyScavengerHuntObjective.ItemSubmission> items = this.data.getRequiredItemSubmissions();
      Minecraft mc = Minecraft.getInstance();
      int midX = mc.getWindow().getGuiScaledWidth() / 2;
      int gapWidth = 7;
      int itemBoxWidth = 32;
      int totalWidth = items.size() * itemBoxWidth + (items.size() - 1) * gapWidth;
      int shiftX = -totalWidth / 2 + itemBoxWidth / 2;
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
      renderStack.pushPose();
      int yOffset = 0;
      renderStack.pushPose();
      renderStack.translate(midX + shiftX, itemBoxWidth * 0.75F, 0.0);

      for (LegacyScavengerHuntObjective.ItemSubmission itemRequirement : items) {
         int reqYOffset = renderItemRequirement(renderStack, itemRequirement, itemBoxWidth);
         if (reqYOffset > yOffset) {
            yOffset = reqYOffset;
         }

         renderStack.translate(itemBoxWidth + gapWidth, 0.0, 0.0);
      }

      renderStack.popPose();
      return yOffset;
   }

   private static int renderItemRequirement(PoseStack renderStack, LegacyScavengerHuntObjective.ItemSubmission itemRequirement, int itemBoxWidth) {
      Minecraft mc = Minecraft.getInstance();
      Font fr = mc.font;
      ItemStack requiredStack = new ItemStack(itemRequirement.getRequiredItem());
      LegacyScavengerHuntConfig.SourceType source = ModConfigs.LEGACY_SCAVENGER_HUNT.getRequirementSource(requiredStack);
      ResourceLocation iconPath = source == LegacyScavengerHuntConfig.SourceType.MOB
         ? MobHeadTextures.get(ModConfigs.LEGACY_SCAVENGER_HUNT.getRequirementMobType(requiredStack)).orElse(source.getIconPath())
         : source.getIconPath();
      renderStack.pushPose();
      renderStack.translate(0.0, -itemBoxWidth / 2.0F, 0.0);
      renderItemStack(renderStack, requiredStack);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, iconPath);
      renderStack.pushPose();
      renderStack.translate(-16.0, -2.4, 0.0);
      renderStack.scale(0.4F, 0.4F, 1.0F);
      ScreenDrawHelper.drawTexturedQuads(buf -> ScreenDrawHelper.rect(buf, renderStack).dim(16.0F, 16.0F).draw());
      renderStack.popPose();
      renderStack.translate(0.0, 10.0, 0.0);
      String requiredText = itemRequirement.getCurrentAmount() + "/" + itemRequirement.getRequiredAmount();
      MutableComponent cmp = new TextComponent(requiredText).withStyle(ChatFormatting.GREEN);
      UIHelper.renderCenteredWrappedText(renderStack, cmp, 30, 0);
      renderStack.translate(0.0, 10.0, 0.0);
      renderStack.pushPose();
      renderStack.scale(0.5F, 0.5F, 1.0F);
      Component name = requiredStack.getHoverName();
      MutableComponent display = name.copy().withStyle(source.getRequirementColor());
      int lines = UIHelper.renderCenteredWrappedText(renderStack, display, 60, 0);
      renderStack.popPose();
      renderStack.popPose();
      return 25 + lines * 5;
   }

   private static void renderItemStack(PoseStack renderStack, ItemStack item) {
      Minecraft mc = Minecraft.getInstance();
      ItemRenderer ir = mc.getItemRenderer();
      Font fr = RenderProperties.get(item).getFont(item);
      if (fr == null) {
         fr = mc.font;
      }

      renderStack.translate(-8.0, -8.0, 0.0);
      renderStack.pushPose();
      renderStack.mulPoseMatrix(renderStack.last().pose());
      ir.blitOffset = 200.0F;
      ir.renderAndDecorateItem(item, 0, 0);
      ir.renderGuiItemDecorations(fr, item, 0, 0, null);
      ir.blitOffset = 0.0F;
      renderStack.popPose();
      renderStack.translate(8.0, 8.0, 0.0);
   }
}
