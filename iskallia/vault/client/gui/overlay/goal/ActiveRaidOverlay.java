package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.vault.goal.ActiveRaidGoalData;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.world.vault.logic.objective.VaultModifierVotingSession;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ActiveRaidOverlay extends BossBarOverlay {
   public static final ResourceLocation VAULT_HUD_RESOURCE = VaultMod.id("textures/gui/vault_hud.png");
   private final ActiveRaidGoalData data;

   public ActiveRaidOverlay(ActiveRaidGoalData data) {
      this.data = data;
   }

   @SubscribeEvent
   public static void onDrawPlayerlist(Pre event) {
      if (event.getType() == ElementType.PLAYER_LIST) {
         VaultGoalData data = VaultGoalData.CURRENT_DATA;
         if (data instanceof ActiveRaidGoalData) {
            event.setCanceled(true);
         }
      }
   }

   @Override
   public boolean shouldDisplay() {
      return true;
   }

   @Override
   public int drawOverlay(PoseStack renderStack, float pTicks) {
      int offsetY = 5;
      offsetY = this.drawWaveDisplay(renderStack, pTicks, offsetY);
      offsetY = this.drawMobBar(renderStack, pTicks, offsetY);
      offsetY = this.drawModifierDisplay(renderStack, pTicks, offsetY);
      return this.drawVotingDisplay(renderStack, pTicks, offsetY);
   }

   private int drawWaveDisplay(PoseStack renderStack, float pTicks, int offsetY) {
      if (this.data.getTotalWaves() <= 0) {
         return offsetY;
      } else {
         String waveDisplay = String.format("%s / %s", this.data.getWave() + 1, this.data.getTotalWaves());
         String fullDisplay = waveDisplay;
         if (this.data.getTickWaveDelay() > 0) {
            fullDisplay = waveDisplay + " - " + UIHelper.formatTimeString(this.data.getTickWaveDelay());
         }

         BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         Minecraft mc = Minecraft.getInstance();
         Font fr = mc.font;
         int width = fr.width(waveDisplay);
         float midX = mc.getWindow().getGuiScaledWidth() / 2.0F;
         renderStack.pushPose();
         renderStack.translate(midX - width / 2.0F, offsetY, 0.0);
         renderStack.scale(1.25F, 1.25F, 1.0F);
         FontHelper.drawStringWithBorder(renderStack, fullDisplay, 0.0F, 0.0F, 16777215, 0);
         buffer.endBatch();
         renderStack.popPose();
         return offsetY + 13;
      }
   }

   private int drawMobBar(PoseStack renderStack, float pTicks, int offsetY) {
      if (this.data.getTotalWaves() <= 0) {
         return offsetY;
      } else {
         Minecraft mc = Minecraft.getInstance();
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, VAULT_HUD_RESOURCE);
         float killedPerc = (float)this.data.getAliveMobs() / this.data.getTotalMobs();
         float midX = mc.getWindow().getGuiScaledWidth() / 2.0F;
         int width = 182;
         int mobWidth = (int)(width * killedPerc);
         int totalWidth = width - mobWidth;
         ScreenDrawHelper.drawTexturedQuads(
            buf -> {
               ScreenDrawHelper.rect(buf, renderStack).at(midX - width / 2.0F, offsetY).dim(mobWidth, 5.0F).texVanilla(0.0F, 168.0F, mobWidth, 5.0F).draw();
               ScreenDrawHelper.rect(buf, renderStack).at(midX - width / 2.0F, offsetY).dim(mobWidth, 5.0F).texVanilla(0.0F, 178.0F, mobWidth, 5.0F).draw();
               ScreenDrawHelper.rect(buf, renderStack)
                  .at(midX - width / 2.0F + mobWidth, offsetY)
                  .dim(totalWidth, 5.0F)
                  .texVanilla(mobWidth, 163.0F, totalWidth, 5.0F)
                  .draw();
               ScreenDrawHelper.rect(buf, renderStack)
                  .at(midX - width / 2.0F + mobWidth, offsetY)
                  .dim(totalWidth, 5.0F)
                  .texVanilla(mobWidth, 173.0F, totalWidth, 5.0F)
                  .draw();
            }
         );
         RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
         return offsetY + 8;
      }
   }

   private int drawModifierDisplay(PoseStack renderStack, float pTicks, int offsetY) {
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      Minecraft mc = Minecraft.getInstance();
      Font fr = mc.font;
      int guiScale = mc.options.guiScale;
      boolean drawAdditionalInfo = false;
      List<Component> positives = this.data.getPositives();
      List<Component> negatives = this.data.getNegatives();
      if (!mc.options.keyPlayerList.isDown()) {
         drawAdditionalInfo = positives.size() > 2 || negatives.size() > 2;
         positives = positives.subList(0, Math.min(positives.size(), 2));
         negatives = negatives.subList(0, Math.min(negatives.size(), 2));
      }

      float midX = mc.getWindow().getGuiScaledWidth() / 2.0F;
      float scale = guiScale < 4 && guiScale != 0 ? 1.0F : 0.7F;
      float height = 10.0F * scale;
      float maxHeight = Math.max(positives.size(), negatives.size()) * height;
      if (this.data.getRaidsCompleted() > 0) {
         renderStack.pushPose();
         renderStack.translate(midX, offsetY, 0.0);
         renderStack.scale(scale, scale, 1.0F);
         String raid = this.data.getRaidsCompleted() > 1 ? " Raids" : " Raid";
         String infoText = String.format("%d / %d %s Completed", this.data.getRaidsCompleted(), this.data.getTargetRaids(), raid);
         Component info = new TextComponent(infoText).withStyle(ChatFormatting.GOLD);
         int width = fr.width(info);
         fr.drawInBatch(info, -width / 2, 0.0F, -1, false, renderStack.last().pose(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords());
         renderStack.popPose();
         offsetY = (int)(offsetY + height + 1.0F);
      }

      renderStack.pushPose();
      renderStack.translate(midX - 5.0F, offsetY, 0.0);
      renderStack.scale(scale, scale, 1.0F);

      for (Component positive : positives) {
         int width = fr.width(positive);
         fr.drawInBatch(positive, -width, 0.0F, -1, false, renderStack.last().pose(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords());
         renderStack.translate(0.0, 10.0, 0.0);
      }

      renderStack.popPose();
      renderStack.pushPose();
      renderStack.translate(midX + 5.0F, offsetY, 0.0);
      renderStack.scale(scale, scale, 1.0F);

      for (Component negative : negatives) {
         fr.drawInBatch(negative, 0.0F, 0.0F, -1, false, renderStack.last().pose(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords());
         renderStack.translate(0.0, 10.0, 0.0);
      }

      renderStack.popPose();
      if (drawAdditionalInfo) {
         renderStack.pushPose();
         renderStack.translate(midX, offsetY + maxHeight, 0.0);
         renderStack.scale(scale, scale, 1.0F);
         KeyMapping listSetting = mc.options.keyPlayerList;
         Component info = new TextComponent("Hold ").withStyle(ChatFormatting.DARK_GRAY).append(listSetting.getTranslatedKeyMessage());
         int width = fr.width(info);
         fr.drawInBatch(info, -width / 2, 0.0F, -1, false, renderStack.last().pose(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords());
         renderStack.popPose();
         maxHeight += height;
      }

      buffer.endBatch();
      return Mth.ceil(offsetY + maxHeight);
   }

   private int drawVotingDisplay(PoseStack renderStack, float pTicks, int offsetY) {
      if (this.data.getVotingSession() == null) {
         return offsetY;
      } else {
         VaultModifierVotingSession session = this.data.getVotingSession();
         BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         Minecraft mc = Minecraft.getInstance();
         Font fr = mc.font;
         int guiScale = mc.options.guiScale;
         float midX = mc.getWindow().getGuiScaledWidth() / 2.0F;
         float scale = guiScale < 4 && guiScale != 0 ? 1.2F : 1.0F;
         float height = 10.0F * scale;
         offsetY += 2;
         String voteStr = "Vote! " + UIHelper.formatTimeString(session.getVotingDuration()) + " ";
         MutableComponent display = new TextComponent(voteStr);
         List<VaultModifier> modifiers = session.getModifiers();

         for (int i = 0; i < modifiers.size(); i++) {
            VaultModifier choice = modifiers.get(i);
            if (i > 0) {
               display = display.append(new TextComponent(" | ").withStyle(ChatFormatting.GRAY));
            }

            display.append(
               new TextComponent("!" + choice.getDisplayName().replaceAll("\\s", "").toLowerCase())
                  .setStyle(Style.EMPTY.withColor(choice.getDisplayTextColor()))
            );
         }

         float width = fr.width(display);
         renderStack.pushPose();
         renderStack.translate(midX, offsetY, 0.0);
         renderStack.scale(scale, scale, 1.0F);
         fr.drawInBatch(display, -width / 2.0F, 0.0F, -1, true, renderStack.last().pose(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords());
         renderStack.popPose();
         buffer.endBatch();
         return offsetY + Mth.ceil(height);
      }
   }
}
