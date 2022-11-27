package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.vault.goal.ArchitectGoalData;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.network.message.VaultOverlayMessage;
import iskallia.vault.world.vault.logic.objective.architect.DirectionChoice;
import iskallia.vault.world.vault.logic.objective.architect.VotingSession;
import iskallia.vault.world.vault.logic.objective.architect.modifier.VoteModifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.FORGE
)
public class ArchitectGoalVoteOverlay extends BossBarOverlay {
   private static final ResourceLocation ARCHITECT_HUD = new ResourceLocation("the_vault", "textures/gui/architect_event_bar.png");
   private final ArchitectGoalData data;

   public ArchitectGoalVoteOverlay(ArchitectGoalData data) {
      this.data = data;
   }

   @SubscribeEvent
   public static void onArchitectBuild(Post event) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (event.getType() == ElementType.ALL && type == VaultOverlayMessage.OverlayType.VAULT) {
         Minecraft mc = Minecraft.getInstance();
         if (VaultGoalData.CURRENT_DATA instanceof ArchitectGoalData displayData) {
            PoseStack renderStack = event.getMatrixStack();
            BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            Font fr = mc.font;
            int bottom = mc.getWindow().getGuiScaledHeight();
            float part = displayData.getCompletedPercent();
            Component txt = new TextComponent("Build the vault!").withStyle(ChatFormatting.AQUA);
            fr.drawInBatch(
               txt.getVisualOrderText(), 8.0F, bottom - 60, -1, true, renderStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
            );
            int lockTime = displayData.getTotalTicksUntilNextVote();
            String duration = UIHelper.formatTimeString(lockTime);
            txt = new TextComponent("Vote Lock Time");
            fr.drawInBatch(
               txt.getVisualOrderText(), 8.0F, bottom - 42, -1, true, renderStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
            );
            txt = new TextComponent(duration).withStyle(lockTime > 0 ? ChatFormatting.RED : ChatFormatting.GREEN);
            fr.drawInBatch(
               txt.getVisualOrderText(), 28.0F, bottom - 32, -1, true, renderStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
            );
            buffer.endBatch();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, ARCHITECT_HUD);
            ScreenDrawHelper.drawTexturedQuads(buf -> {
               ScreenDrawHelper.rect(buf, renderStack).at(15.0F, bottom - 51).dim(54.0F, 7.0F).texVanilla(0.0F, 105.0F, 54.0F, 7.0F).draw();
               ScreenDrawHelper.rect(buf, renderStack).at(16.0F, bottom - 50).dim(52.0F * part, 5.0F).texVanilla(0.0F, 113.0F, 52.0F * part, 5.0F).draw();
            });
         }

         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
      }
   }

   @Override
   public boolean shouldDisplay() {
      return this.data.getTicksUntilNextVote() > 0 || this.data.getActiveSession() != null && !this.data.getActiveSession().getDirections().isEmpty();
   }

   @Override
   public int drawOverlay(PoseStack renderStack, float pTicks) {
      VotingSession activeSession = this.data.getActiveSession();
      if (!this.shouldDisplay()) {
         return 0;
      } else {
         Minecraft mc = Minecraft.getInstance();
         int offsetY = 5;
         if (this.data.getTicksUntilNextVote() > 0) {
            offsetY = this.drawVotingTimer(this.data.getTicksUntilNextVote(), renderStack, offsetY);
         }

         if (activeSession != null && !activeSession.getDirections().isEmpty()) {
            offsetY = this.drawVotingSession(activeSession, renderStack, offsetY);
         }

         RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
         return offsetY;
      }
   }

   private int drawVotingTimer(int ticksUntilNextVote, PoseStack renderStack, int offsetY) {
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      Minecraft mc = Minecraft.getInstance();
      Font fr = mc.font;
      int midX = mc.getWindow().getGuiScaledWidth() / 2;
      float scale = 1.25F;
      String tplText = "Voting locked: ";
      String text = tplText + UIHelper.formatTimeString(ticksUntilNextVote);
      float shift = fr.width(tplText + "00:00") * 1.25F;
      Component textCmp = new TextComponent(text).withStyle(ChatFormatting.RED);
      renderStack.pushPose();
      renderStack.translate(midX - shift / 2.0F, offsetY, 0.0);
      renderStack.scale(scale, scale, 1.0F);
      fr.drawInBatch(textCmp, 0.0F, 0.0F, -1, false, renderStack.last().pose(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords());
      buffer.endBatch();
      renderStack.popPose();
      return offsetY + 13;
   }

   private int drawVotingSession(VotingSession activeSession, PoseStack renderStack, int offsetY) {
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      Minecraft mc = Minecraft.getInstance();
      Font fr = mc.font;
      int midX = mc.getWindow().getGuiScaledWidth() / 2;
      int segmentWidth = 8;
      int barSegments = 22;
      int startEndWith = 4;
      int barWidth = segmentWidth * barSegments;
      int totalWidth = barWidth + startEndWith * 2;
      int offsetX = midX - totalWidth / 2;
      Map<DirectionChoice, Float> barParts = new LinkedHashMap<>();

      for (DirectionChoice choice : activeSession.getDirections()) {
         barParts.put(choice, activeSession.getChoicePercentage(choice));
      }

      float shiftTitleX = fr.width("Vote! 00:00") * 1.25F;
      String timeRemainingStr = UIHelper.formatTimeString(activeSession.getRemainingVoteTicks());
      Component title = new TextComponent("Vote! ").append(timeRemainingStr).withStyle(ChatFormatting.AQUA);
      renderStack.pushPose();
      renderStack.translate(midX - shiftTitleX / 2.0F, offsetY, 0.0);
      renderStack.scale(1.25F, 1.25F, 1.0F);
      fr.drawInBatch(title, 0.0F, 0.0F, -1, false, renderStack.last().pose(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords());
      buffer.endBatch();
      renderStack.popPose();
      offsetY = (int)(offsetY + 12.5F);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, ARCHITECT_HUD);
      this.drawBarContent(renderStack, offsetX + 1, offsetY + 1, barWidth, barParts);
      this.drawBarFrame(renderStack, offsetX, offsetY);
      offsetY += 12;
      return offsetY + this.drawVoteChoices(renderStack, offsetX, offsetY, totalWidth, activeSession.getDirections());
   }

   private int drawVoteChoices(PoseStack renderStack, int offsetX, int offsetY, int totalWidth, List<DirectionChoice> choices) {
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      Minecraft mc = Minecraft.getInstance();
      Font fr = mc.font;
      int maxHeight = 0;

      for (int i = 0; i < choices.size(); i++) {
         DirectionChoice choice = choices.get(i);
         float offsetPart = (i + 0.5F) / choices.size();
         float barMid = offsetX + offsetPart * totalWidth;
         int yShift = 0;
         FormattedCharSequence bidiDir = choice.getDirectionDisplay("!").getVisualOrderText();
         int dirLength = fr.width(bidiDir);
         fr.drawInBatch(
            bidiDir,
            barMid - dirLength / 2.0F,
            yShift + offsetY,
            -1,
            false,
            renderStack.last().pose(),
            buffer,
            true,
            0,
            LightmapHelper.getPackedFullbrightCoords()
         );
         yShift += 9;
         float scaledShift = 0.0F;
         float modifierScale = 0.75F;
         renderStack.pushPose();
         renderStack.translate(barMid, offsetY + yShift, 0.0);
         renderStack.scale(modifierScale, modifierScale, 1.0F);

         for (VoteModifier modifier : choice.getModifiers()) {
            int changeSeconds = modifier.getVoteLockDurationChangeSeconds();
            if (changeSeconds != 0) {
               ChatFormatting color = changeSeconds > 0 ? ChatFormatting.RED : ChatFormatting.GREEN;
               String changeDesc = changeSeconds > 0 ? "+" + changeSeconds : String.valueOf(changeSeconds);
               Component line = new TextComponent(changeDesc + "s Vote Lock").withStyle(color);
               int voteLockLength = fr.width(line);
               fr.drawInBatch(
                  line.getVisualOrderText(),
                  -voteLockLength / 2.0F,
                  0.0F,
                  -1,
                  false,
                  renderStack.last().pose(),
                  buffer,
                  true,
                  0,
                  LightmapHelper.getPackedFullbrightCoords()
               );
               renderStack.translate(0.0, 9.0, 0.0);
               scaledShift += 9.0F;
            }

            FormattedCharSequence bidiDesc = modifier.getDescription().getVisualOrderText();
            int descLength = fr.width(bidiDesc);
            fr.drawInBatch(
               bidiDesc, -descLength / 2.0F, 0.0F, -1, false, renderStack.last().pose(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords()
            );
            renderStack.translate(0.0, 9.0, 0.0);
            scaledShift += 9.0F;
         }

         renderStack.popPose();
         yShift += Mth.ceil(scaledShift * modifierScale);
         if (yShift > maxHeight) {
            maxHeight = yShift;
         }
      }

      buffer.endBatch();
      return maxHeight;
   }

   private void drawBarContent(PoseStack renderStack, int offsetX, int offsetY, int barWidth, Map<DirectionChoice, Float> barParts) {
      ScreenDrawHelper.drawTexturedQuads(buf -> {
         float drawX = offsetX;
         DirectionChoice lastChoice = null;
         boolean drawStart = true;

         for (DirectionChoice choice : barParts.keySet()) {
            float part = barParts.get(choice) * barWidth;
            int vOffset = DirectionChoice.getVOffset(choice.getDirection());
            lastChoice = choice;
            if (drawStart) {
               ScreenDrawHelper.rect(buf, renderStack).at(offsetX, offsetY).dim(3.0F, 8.0F).texVanilla(0.0F, vOffset, 3.0F, 8.0F).draw();
               drawX += 3.0F;
               drawStart = false;
            }

            while (part > 0.0F) {
               float length = Math.min(8.0F, part);
               part -= length;
               ScreenDrawHelper.rect(buf, renderStack).at(drawX, offsetY).dim(length, 8.0F).texVanilla(100.0F, vOffset, length, 8.0F).draw();
               drawX += length;
            }
         }

         int vOffset = DirectionChoice.getVOffset(lastChoice.getDirection());
         ScreenDrawHelper.rect(buf, renderStack).at(drawX, offsetY).dim(3.0F, 8.0F).texVanilla(96.0F, vOffset, 3.0F, 8.0F).draw();
      });
   }

   private void drawBarFrame(PoseStack renderStack, int offsetX, int offsetY) {
      renderStack.pushPose();
      ScreenDrawHelper.drawTexturedQuads(buf -> {
         ScreenDrawHelper.rect(buf, renderStack).at(offsetX, offsetY).dim(4.0F, 10.0F).texVanilla(0.0F, 11.0F, 4.0F, 10.0F).draw();
         int barOffsetX = offsetX + 4;

         for (int i = 0; i < 22; i++) {
            ScreenDrawHelper.rect(buf, renderStack).at(barOffsetX, offsetY).dim(8.0F, 10.0F).texVanilla(0.0F, 0.0F, 8.0F, 10.0F).draw();
            barOffsetX += 8;
         }

         ScreenDrawHelper.rect(buf, renderStack).at(barOffsetX, offsetY).dim(4.0F, 10.0F).texVanilla(97.0F, 11.0F, 4.0F, 10.0F).draw();
      });
      renderStack.popPose();
   }
}
