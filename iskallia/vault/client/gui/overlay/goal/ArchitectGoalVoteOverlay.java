package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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
      if (event.getType() == ElementType.HOTBAR && type == VaultOverlayMessage.OverlayType.VAULT) {
         Minecraft mc = Minecraft.func_71410_x();
         VaultGoalData data = VaultGoalData.CURRENT_DATA;
         if (data instanceof ArchitectGoalData) {
            ArchitectGoalData displayData = (ArchitectGoalData)data;
            MatrixStack renderStack = event.getMatrixStack();
            Impl buffer = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
            FontRenderer fr = mc.field_71466_p;
            int bottom = mc.func_228018_at_().func_198087_p();
            float part = displayData.getCompletedPercent();
            ITextComponent txt = new StringTextComponent("Build the vault!").func_240699_a_(TextFormatting.AQUA);
            fr.func_238416_a_(
               txt.func_241878_f(),
               8.0F,
               bottom - 60,
               -1,
               true,
               renderStack.func_227866_c_().func_227870_a_(),
               buffer,
               false,
               0,
               LightmapHelper.getPackedFullbrightCoords()
            );
            int lockTime = displayData.getTotalTicksUntilNextVote();
            String duration = UIHelper.formatTimeString(lockTime);
            txt = new StringTextComponent("Vote Lock Time");
            fr.func_238416_a_(
               txt.func_241878_f(),
               8.0F,
               bottom - 42,
               -1,
               true,
               renderStack.func_227866_c_().func_227870_a_(),
               buffer,
               false,
               0,
               LightmapHelper.getPackedFullbrightCoords()
            );
            txt = new StringTextComponent(duration).func_240699_a_(lockTime > 0 ? TextFormatting.RED : TextFormatting.GREEN);
            fr.func_238416_a_(
               txt.func_241878_f(),
               28.0F,
               bottom - 32,
               -1,
               true,
               renderStack.func_227866_c_().func_227870_a_(),
               buffer,
               false,
               0,
               LightmapHelper.getPackedFullbrightCoords()
            );
            buffer.func_228461_a_();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            mc.func_110434_K().func_110577_a(ARCHITECT_HUD);
            ScreenDrawHelper.drawQuad(buf -> {
               ScreenDrawHelper.rect(buf, renderStack).at(15.0F, bottom - 51).dim(54.0F, 7.0F).texVanilla(0.0F, 105.0F, 54.0F, 7.0F).draw();
               ScreenDrawHelper.rect(buf, renderStack).at(16.0F, bottom - 50).dim(52.0F * part, 5.0F).texVanilla(0.0F, 113.0F, 52.0F * part, 5.0F).draw();
            });
         }

         mc.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
      }
   }

   @Override
   public boolean shouldDisplay() {
      return this.data.getTicksUntilNextVote() > 0 || this.data.getActiveSession() != null && !this.data.getActiveSession().getDirections().isEmpty();
   }

   @Override
   public int drawOverlay(MatrixStack renderStack, float pTicks) {
      VotingSession activeSession = this.data.getActiveSession();
      if (!this.shouldDisplay()) {
         return 0;
      } else {
         Minecraft mc = Minecraft.func_71410_x();
         int offsetY = 5;
         if (this.data.getTicksUntilNextVote() > 0) {
            offsetY = this.drawVotingTimer(this.data.getTicksUntilNextVote(), renderStack, offsetY);
         }

         if (activeSession != null && !activeSession.getDirections().isEmpty()) {
            offsetY = this.drawVotingSession(activeSession, renderStack, offsetY);
         }

         mc.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
         return offsetY;
      }
   }

   private int drawVotingTimer(int ticksUntilNextVote, MatrixStack renderStack, int offsetY) {
      Impl buffer = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
      Minecraft mc = Minecraft.func_71410_x();
      FontRenderer fr = mc.field_71466_p;
      int midX = mc.func_228018_at_().func_198107_o() / 2;
      float scale = 1.25F;
      String tplText = "Voting locked: ";
      String text = tplText + UIHelper.formatTimeString(ticksUntilNextVote);
      float shift = fr.func_78256_a(tplText + "00:00") * 1.25F;
      ITextComponent textCmp = new StringTextComponent(text).func_240699_a_(TextFormatting.RED);
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(midX - shift / 2.0F, offsetY, 0.0);
      renderStack.func_227862_a_(scale, scale, 1.0F);
      fr.func_243247_a(
         textCmp, 0.0F, 0.0F, -1, false, renderStack.func_227866_c_().func_227870_a_(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords()
      );
      buffer.func_228461_a_();
      renderStack.func_227865_b_();
      return offsetY + 13;
   }

   private int drawVotingSession(VotingSession activeSession, MatrixStack renderStack, int offsetY) {
      Impl buffer = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
      Minecraft mc = Minecraft.func_71410_x();
      FontRenderer fr = mc.field_71466_p;
      int midX = mc.func_228018_at_().func_198107_o() / 2;
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

      float shiftTitleX = fr.func_78256_a("Vote! 00:00") * 1.25F;
      String timeRemainingStr = UIHelper.formatTimeString(activeSession.getRemainingVoteTicks());
      ITextComponent title = new StringTextComponent("Vote! ").func_240702_b_(timeRemainingStr).func_240699_a_(TextFormatting.AQUA);
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(midX - shiftTitleX / 2.0F, offsetY, 0.0);
      renderStack.func_227862_a_(1.25F, 1.25F, 1.0F);
      fr.func_243247_a(title, 0.0F, 0.0F, -1, false, renderStack.func_227866_c_().func_227870_a_(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords());
      buffer.func_228461_a_();
      renderStack.func_227865_b_();
      offsetY = (int)(offsetY + 12.5F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      mc.func_110434_K().func_110577_a(ARCHITECT_HUD);
      this.drawBarContent(renderStack, offsetX + 1, offsetY + 1, barWidth, barParts);
      this.drawBarFrame(renderStack, offsetX, offsetY);
      offsetY += 12;
      return offsetY + this.drawVoteChoices(renderStack, offsetX, offsetY, totalWidth, activeSession.getDirections());
   }

   private int drawVoteChoices(MatrixStack renderStack, int offsetX, int offsetY, int totalWidth, List<DirectionChoice> choices) {
      Impl buffer = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
      Minecraft mc = Minecraft.func_71410_x();
      FontRenderer fr = mc.field_71466_p;
      int maxHeight = 0;

      for (int i = 0; i < choices.size(); i++) {
         DirectionChoice choice = choices.get(i);
         float offsetPart = (i + 0.5F) / choices.size();
         float barMid = offsetX + offsetPart * totalWidth;
         int yShift = 0;
         IReorderingProcessor bidiDir = choice.getDirectionDisplay("/").func_241878_f();
         int dirLength = fr.func_243245_a(bidiDir);
         fr.func_238416_a_(
            bidiDir,
            barMid - dirLength / 2.0F,
            yShift + offsetY,
            -1,
            false,
            renderStack.func_227866_c_().func_227870_a_(),
            buffer,
            true,
            0,
            LightmapHelper.getPackedFullbrightCoords()
         );
         yShift += 9;
         float scaledShift = 0.0F;
         float modifierScale = 0.75F;
         renderStack.func_227860_a_();
         renderStack.func_227861_a_(barMid, offsetY + yShift, 0.0);
         renderStack.func_227862_a_(modifierScale, modifierScale, 1.0F);

         for (VoteModifier modifier : choice.getModifiers()) {
            int changeSeconds = modifier.getVoteLockDurationChangeSeconds();
            if (changeSeconds != 0) {
               TextFormatting color = changeSeconds > 0 ? TextFormatting.RED : TextFormatting.GREEN;
               String changeDesc = changeSeconds > 0 ? "+" + changeSeconds : String.valueOf(changeSeconds);
               ITextComponent line = new StringTextComponent(changeDesc + "s Vote Lock").func_240699_a_(color);
               int voteLockLength = fr.func_238414_a_(line);
               fr.func_238416_a_(
                  line.func_241878_f(),
                  -voteLockLength / 2.0F,
                  0.0F,
                  -1,
                  false,
                  renderStack.func_227866_c_().func_227870_a_(),
                  buffer,
                  true,
                  0,
                  LightmapHelper.getPackedFullbrightCoords()
               );
               renderStack.func_227861_a_(0.0, 9.0, 0.0);
               scaledShift += 9.0F;
            }

            IReorderingProcessor bidiDesc = modifier.getDescription().func_241878_f();
            int descLength = fr.func_243245_a(bidiDesc);
            fr.func_238416_a_(
               bidiDesc,
               -descLength / 2.0F,
               0.0F,
               -1,
               false,
               renderStack.func_227866_c_().func_227870_a_(),
               buffer,
               true,
               0,
               LightmapHelper.getPackedFullbrightCoords()
            );
            renderStack.func_227861_a_(0.0, 9.0, 0.0);
            scaledShift += 9.0F;
         }

         renderStack.func_227865_b_();
         yShift += MathHelper.func_76123_f(scaledShift * modifierScale);
         if (yShift > maxHeight) {
            maxHeight = yShift;
         }
      }

      buffer.func_228461_a_();
      return maxHeight;
   }

   private void drawBarContent(MatrixStack renderStack, int offsetX, int offsetY, int barWidth, Map<DirectionChoice, Float> barParts) {
      ScreenDrawHelper.drawQuad(buf -> {
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

   private void drawBarFrame(MatrixStack renderStack, int offsetX, int offsetY) {
      renderStack.func_227860_a_();
      ScreenDrawHelper.drawQuad(buf -> {
         ScreenDrawHelper.rect(buf, renderStack).at(offsetX, offsetY).dim(4.0F, 10.0F).texVanilla(0.0F, 11.0F, 4.0F, 10.0F).draw();
         int barOffsetX = offsetX + 4;

         for (int i = 0; i < 22; i++) {
            ScreenDrawHelper.rect(buf, renderStack).at(barOffsetX, offsetY).dim(8.0F, 10.0F).texVanilla(0.0F, 0.0F, 8.0F, 10.0F).draw();
            barOffsetX += 8;
         }

         ScreenDrawHelper.rect(buf, renderStack).at(barOffsetX, offsetY).dim(4.0F, 10.0F).texVanilla(97.0F, 11.0F, 4.0F, 10.0F).draw();
      });
      renderStack.func_227865_b_();
   }
}
