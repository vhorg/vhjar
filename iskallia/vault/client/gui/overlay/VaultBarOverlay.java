package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.AnimationTwoPhased;
import iskallia.vault.client.gui.helper.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class VaultBarOverlay {
   public static final ResourceLocation RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");
   public static int vaultLevel;
   public static int vaultExp;
   public static int tnl;
   public static int unspentSkillPoints;
   public static int unspentKnowledgePoints;
   public static AnimationTwoPhased expGainedAnimation = new AnimationTwoPhased(0.0F, 1.0F, 0.0F, 500);
   public static long previousTick = System.currentTimeMillis();

   @SubscribeEvent
   public static void onPostRender(Post event) {
      if (event.getType() == ElementType.HOTBAR) {
         long now = System.currentTimeMillis();
         MatrixStack matrixStack = event.getMatrixStack();
         Minecraft minecraft = Minecraft.func_71410_x();
         int midX = minecraft.func_228018_at_().func_198107_o() / 2;
         int bottom = minecraft.func_228018_at_().func_198087_p();
         int right = minecraft.func_228018_at_().func_198107_o();
         String text = String.valueOf(vaultLevel);
         int textX = midX + 50 - minecraft.field_71466_p.func_78256_a(text) / 2;
         int textY = bottom - 54;
         int barWidth = 85;
         float expPercentage = (float)vaultExp / tnl;
         if (unspentSkillPoints > 0) {
            ClientPlayerEntity player = minecraft.field_71439_g;
            boolean iconsShowing = player != null && player.func_70651_bq().stream().anyMatch(EffectInstance::func_205348_f);
            minecraft.func_110434_K().func_110577_a(RESOURCE);
            String unspentText = unspentSkillPoints == 1 ? " unspent skill point" : " unspent skill points";
            String unspentPointsText = unspentSkillPoints + "";
            int unspentPointsWidth = minecraft.field_71466_p.func_78256_a(unspentPointsText);
            int unspentWidth = minecraft.field_71466_p.func_78256_a(unspentText);
            int gap = 5;
            int yOffset = 18;
            minecraft.field_71466_p
               .func_238405_a_(
                  matrixStack, unspentSkillPoints + "", right - unspentWidth - unspentPointsWidth - gap, iconsShowing ? yOffset + 10 : yOffset, -10240
               );
            minecraft.field_71466_p.func_238405_a_(matrixStack, unspentText, right - unspentWidth - gap, iconsShowing ? yOffset + 10 : yOffset, -1);
         }

         if (unspentKnowledgePoints > 0) {
            ClientPlayerEntity player = minecraft.field_71439_g;
            boolean iconsShowing = player != null && player.func_70651_bq().stream().anyMatch(EffectInstance::func_205348_f);
            minecraft.func_110434_K().func_110577_a(RESOURCE);
            String unspentText = unspentKnowledgePoints == 1 ? " unspent knowledge point" : " unspent knowledge points";
            String unspentPointsText = unspentKnowledgePoints + "";
            int unspentPointsWidth = minecraft.field_71466_p.func_78256_a(unspentPointsText);
            int unspentWidth = minecraft.field_71466_p.func_78256_a(unspentText);
            int gap = 5;
            int yOffset = 18;
            matrixStack.func_227860_a_();
            if (unspentSkillPoints > 0) {
               matrixStack.func_227861_a_(0.0, 12.0, 0.0);
            }

            minecraft.field_71466_p
               .func_238405_a_(
                  matrixStack, unspentKnowledgePoints + "", right - unspentWidth - unspentPointsWidth - gap, iconsShowing ? yOffset + 10 : yOffset, -12527695
               );
            minecraft.field_71466_p.func_238405_a_(matrixStack, unspentText, right - unspentWidth - gap, iconsShowing ? yOffset + 10 : yOffset, -1);
            matrixStack.func_227865_b_();
         }

         expGainedAnimation.tick((int)(now - previousTick));
         previousTick = now;
         minecraft.func_213239_aq().func_76320_a("vaultBar");
         minecraft.func_110434_K().func_110577_a(RESOURCE);
         RenderSystem.enableBlend();
         minecraft.field_71456_v.func_238474_b_(matrixStack, midX + 9, bottom - 48, 1, 1, barWidth, 5);
         if (expGainedAnimation.getValue() != 0.0F) {
            GlStateManager.func_227702_d_(1.0F, 1.0F, 1.0F, expGainedAnimation.getValue());
            minecraft.field_71456_v.func_238474_b_(matrixStack, midX + 8, bottom - 49, 62, 41, 84, 7);
            GlStateManager.func_227702_d_(1.0F, 1.0F, 1.0F, 1.0F);
         }

         minecraft.field_71456_v.func_238474_b_(matrixStack, midX + 9, bottom - 48, 1, 7, (int)(barWidth * expPercentage), 5);
         if (expGainedAnimation.getValue() != 0.0F) {
            GlStateManager.func_227702_d_(1.0F, 1.0F, 1.0F, expGainedAnimation.getValue());
            minecraft.field_71456_v.func_238474_b_(matrixStack, midX + 8, bottom - 49, 62, 49, (int)(barWidth * expPercentage), 7);
            GlStateManager.func_227702_d_(1.0F, 1.0F, 1.0F, 1.0F);
         }

         FontHelper.drawStringWithBorder(matrixStack, text, textX, textY, -6601, 3945472);
         minecraft.func_213239_aq().func_76319_b();
      }
   }
}
