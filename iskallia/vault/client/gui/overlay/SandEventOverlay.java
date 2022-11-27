package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.client.ClientSandEventData;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SandEventOverlay {
   public static int overlayYOffset = 0;
   private static final ResourceLocation HUD_TEXTURE = VaultMod.id("textures/gui/vault_hud.png");

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onPreRender(Pre event) {
      if (event.getType() == ElementType.BOSSINFO) {
         if (shouldRender()) {
            ClientSandEventData sandEventData = ClientSandEventData.getInstance();
            Minecraft mc = Minecraft.getInstance();
            PoseStack renderStack = event.getMatrixStack();
            int midX = mc.getWindow().getGuiScaledWidth() / 2;
            int barWidth = 127;
            int barOffsetX = midX - barWidth / 2;
            int barOffsetY = 5;
            int barHeight = 9;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, HUD_TEXTURE);
            ScreenDrawHelper.drawTexturedQuads(
               buf -> {
                  ScreenDrawHelper.rect(buf, renderStack)
                     .at(barOffsetX, barOffsetY)
                     .dim(barWidth * sandEventData.getFilledPercentage(), barHeight)
                     .texVanilla(62.0F, 67.0F, 99.0F * sandEventData.getFilledPercentage(), 7.0F)
                     .draw();
                  ScreenDrawHelper.rect(buf, renderStack).at(barOffsetX, barOffsetY).dim(barWidth, barHeight).texVanilla(62.0F, 60.0F, 99.0F, 7.0F).draw();
               }
            );
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            renderStack.pushPose();
            renderStack.translate(0.0, 15.0, 0.0);
         }
      }
   }

   private static void drawContributors(PoseStack renderStack, int sandRight, int offsetY) {
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      ClientSandEventData sandEventData = ClientSandEventData.getInstance();
      Font fr = Minecraft.getInstance().font;
      float scale = 0.85F;
      renderStack.pushPose();
      renderStack.translate(sandRight + 6, offsetY, 500.0);
      renderStack.scale(scale, scale, 1.0F);

      for (ClientSandEventData.ContributorDisplay display : sandEventData.getContributors()) {
         FormattedCharSequence bidiContributor = Language.getInstance().getVisualOrder(display.getContributorDisplay());
         int alpha = (int)(255.0F * display.getRenderOpacity());
         int color = alpha << 24 | 16777215;
         fr.drawInBatch(bidiContributor, 0.0F, 0.0F, color, true, renderStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords());
         renderStack.translate(0.0, 10.0, 0.0);
      }

      buffer.endBatch();
      renderStack.popPose();
   }

   private static int drawSandDescriptions(PoseStack renderStack, int barOffsetLeft, int barOffsetRight, int barOffsetY) {
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      ClientSandEventData sandEventData = ClientSandEventData.getInstance();
      Font fr = Minecraft.getInstance().font;
      int current = sandEventData.getCollectedSand();
      int total = sandEventData.getTotalSand();
      float collectedPercent = (float)current / total;
      int collectedColor = ChatFormatting.GREEN.getColor();
      if (collectedPercent < 0.1F) {
         collectedColor = ChatFormatting.DARK_RED.getColor();
      } else if (collectedPercent < 0.25F) {
         collectedColor = ChatFormatting.RED.getColor();
      } else if (collectedPercent < 0.5F) {
         collectedColor = 16755200;
      }

      MutableComponent sandCollected = new TextComponent("")
         .append(new TextComponent(String.valueOf(current)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(collectedColor))))
         .append(" / ")
         .append(new TextComponent(String.valueOf(total)).withStyle(ChatFormatting.GOLD))
         .append(" Sand");
      FormattedCharSequence bidiSandCollected = Language.getInstance().getVisualOrder(sandCollected);
      int sandCollectedLength = fr.width(bidiSandCollected);
      fr.drawInBatch(
         bidiSandCollected, barOffsetRight + 5, barOffsetY, -1, true, renderStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
      );
      int percent = (int)(sandEventData.getFilledPercentage() * 100.0F);
      Component sandBar = new TextComponent(percent + "%");
      FormattedCharSequence bidiSandBar = Language.getInstance().getVisualOrder(sandBar);
      int sandPercentLength = fr.width(bidiSandBar);
      fr.drawInBatch(
         bidiSandBar,
         barOffsetLeft - sandPercentLength - 5,
         barOffsetY,
         -1,
         true,
         renderStack.last().pose(),
         buffer,
         false,
         0,
         LightmapHelper.getPackedFullbrightCoords()
      );
      buffer.endBatch();
      return barOffsetRight + 5 + sandCollectedLength;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onPostRender(Post event) {
      if (event.getType() == ElementType.BOSSINFO) {
         if (shouldRender()) {
            event.getMatrixStack().popPose();
         }
      }
   }

   @SubscribeEvent
   public static void onPostPotions(Post event) {
      if (event.getType() == ElementType.ALL) {
         if (shouldRender()) {
            Minecraft mc = Minecraft.getInstance();
            PoseStack renderStack = event.getMatrixStack();
            int midX = mc.getWindow().getGuiScaledWidth() / 2;
            int barWidth = 127;
            int barOffsetX = midX - barWidth / 2;
            int barOffsetY = 5 + overlayYOffset;
            int barHeight = 9;
            int sandRight = drawSandDescriptions(renderStack, barOffsetX, barOffsetX + barWidth, barOffsetY);
            drawContributors(renderStack, sandRight, barOffsetY);
         }
      }
   }

   private static boolean shouldRender() {
      ClientSandEventData sandEventData = ClientSandEventData.getInstance();
      return sandEventData.isValid() && Minecraft.getInstance().player != null;
   }
}
