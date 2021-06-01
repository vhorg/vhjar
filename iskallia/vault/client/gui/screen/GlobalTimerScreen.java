package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import java.time.Instant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.StringTextComponent;

public class GlobalTimerScreen extends Screen {
   public static final ResourceLocation UI_RESOURCE = new ResourceLocation("the_vault", "textures/gui/global_timer.png");
   protected long endUnix;

   public GlobalTimerScreen(long endUnix) {
      super(new StringTextComponent("Global Timer"));
      this.endUnix = endUnix;
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_238651_a_(matrixStack, 0);
      Minecraft minecraft = Minecraft.func_71410_x();
      minecraft.func_110434_K().func_110577_a(UI_RESOURCE);
      long now = Instant.now().getEpochSecond();
      long secondsLeft = this.endUnix - now;
      float midX = minecraft.func_228018_at_().func_198107_o() / 2.0F;
      float midY = minecraft.func_228018_at_().func_198087_p() / 2.0F;
      int containerWidth = 140;
      int containerHeight = 70;
      UIHelper.renderContainerBorder(
         this, matrixStack, (int)(midX - containerWidth / 2), (int)(midY - containerHeight / 2), containerWidth, containerHeight, 1, 1, 5, 5, 5, 5, -3750202
      );
      String formattedTime = formatTimeLeft(secondsLeft);
      int formattedTimeLength = minecraft.field_71466_p.func_78256_a(formattedTime);
      String formattedSeconds = formatSecondsLeft(secondsLeft);
      int formattedSecondsLength = minecraft.field_71466_p.func_78256_a(formattedSeconds);
      String label = "Time left until the end...";
      int labelWidth = minecraft.field_71466_p.func_78256_a(label);
      minecraft.field_71466_p.func_238421_b_(matrixStack, label, midX - labelWidth / 2.0F, midY - 20.0F, -12632257);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.0, 5.0, 0.0);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(midX - formattedSecondsLength / 2.0F, midY, 0.0);
      matrixStack.func_227862_a_(2.0F, 2.0F, 2.0F);
      FontHelper.drawStringWithBorder(matrixStack, formattedTime, -formattedTimeLength / 2.0F, -4.0F, -1, -12046047);
      matrixStack.func_227865_b_();
      FontHelper.drawStringWithBorder(matrixStack, formattedSeconds, 5.0F + midX + formattedTimeLength / 2.0F + 12.0F, midY, -1, -12046047);
      matrixStack.func_227865_b_();
      minecraft.func_110434_K().func_110577_a(UI_RESOURCE);
      int hourglassWidth = 12;
      int hourglassHeight = 16;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(midX - containerWidth / 2.0F, midY, 0.0);
      matrixStack.func_227862_a_(2.0F, 2.0F, 2.0F);
      matrixStack.func_227861_a_(-18.0, 0.0, 0.0);
      matrixStack.func_227863_a_(new Quaternion(0.0F, 0.0F, (float)(System.currentTimeMillis() / 10L % 360L), true));
      this.func_238474_b_(matrixStack, (int)(-hourglassWidth / 2.0F), (int)(-hourglassHeight / 2.0F), 1, 15, hourglassWidth, hourglassHeight);
      matrixStack.func_227865_b_();
   }

   public static String formatTimeLeft(long secondsLeft) {
      long minutesLeft = secondsLeft / 60L;
      long hoursLeft = secondsLeft / 3600L;
      long daysLeft = secondsLeft / 86400L;
      return String.format("%02d:%02d:%02d", daysLeft, hoursLeft % 24L, minutesLeft % 60L);
   }

   public static String formatSecondsLeft(long secondsLeft) {
      return String.format("%02d", secondsLeft % 60L);
   }
}
