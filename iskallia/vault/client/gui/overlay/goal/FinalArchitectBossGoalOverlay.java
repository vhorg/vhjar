package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.vault.goal.FinalArchitectGoalData;
import iskallia.vault.world.vault.logic.objective.architect.DirectionChoice;
import iskallia.vault.world.vault.logic.objective.architect.VotingSession;
import iskallia.vault.world.vault.logic.objective.architect.modifier.VoteModifier;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FinalArchitectBossGoalOverlay extends BossBarOverlay {
   private final FinalArchitectGoalData data;

   public FinalArchitectBossGoalOverlay(FinalArchitectGoalData data) {
      this.data = data;
   }

   @Override
   public boolean shouldDisplay() {
      return true;
   }

   @Override
   public int drawOverlay(MatrixStack renderStack, float pTicks) {
      VotingSession activeSession = this.data.getActiveSession();
      Minecraft mc = Minecraft.func_71410_x();
      int offsetY = 5;
      if (activeSession != null && !activeSession.getDirections().isEmpty()) {
         offsetY = this.drawVotingSession(activeSession, renderStack, offsetY);
      }

      mc.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
      return offsetY;
   }

   private int drawVotingSession(VotingSession activeSession, MatrixStack renderStack, int offsetY) {
      Impl buffer = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
      Minecraft mc = Minecraft.func_71410_x();
      FontRenderer fr = mc.field_71466_p;
      int midX = mc.func_228018_at_().func_198107_o() / 2;
      int segmentWidth = 8;
      int barSegments = 22;
      int startEndWith = 12;
      int barWidth = segmentWidth * barSegments;
      int totalWidth = barWidth + startEndWith * 2;
      int offsetX = midX - totalWidth / 2;
      ITextComponent title = new StringTextComponent("Vote!").func_240699_a_(TextFormatting.BOLD);
      float shiftTitleX = fr.func_238414_a_(title) * 1.25F;
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(midX - shiftTitleX / 2.0F, offsetY, 0.0);
      renderStack.func_227862_a_(1.25F, 1.25F, 1.0F);
      fr.func_243247_a(title, 0.0F, 0.0F, -1, false, renderStack.func_227866_c_().func_227870_a_(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords());
      buffer.func_228461_a_();
      renderStack.func_227865_b_();
      offsetY = (int)(offsetY + 12.5F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
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
         IReorderingProcessor bidiDir = choice.getDirectionDisplay().func_241878_f();
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

         for (VoteModifier modifier : choice.getFinalArchitectModifiers()) {
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
}
