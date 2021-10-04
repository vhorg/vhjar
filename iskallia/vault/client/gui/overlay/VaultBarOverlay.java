package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.AnimationTwoPhased;
import iskallia.vault.client.gui.helper.FontHelper;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class VaultBarOverlay {
   public static final ResourceLocation VAULT_HUD_SPRITE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");
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
         Minecraft minecraft = Minecraft.func_71410_x();
         ClientPlayerEntity player = minecraft.field_71439_g;
         if (player != null) {
            MatrixStack matrixStack = event.getMatrixStack();
            int midX = minecraft.func_228018_at_().func_198107_o() / 2;
            int bottom = minecraft.func_228018_at_().func_198087_p();
            int right = minecraft.func_228018_at_().func_198107_o();
            String text = String.valueOf(vaultLevel);
            int textX = midX + 50 - minecraft.field_71466_p.func_78256_a(text) / 2;
            int textY = bottom - 54;
            int barWidth = 85;
            float expPercentage = (float)vaultExp / tnl;
            int potionOffsetY = potionOffsetY(player);
            int gap = 5;
            matrixStack.func_227860_a_();
            if (potionOffsetY > 0) {
               matrixStack.func_227861_a_(0.0, potionOffsetY, 0.0);
            }

            if (unspentSkillPoints > 0) {
               minecraft.func_110434_K().func_110577_a(VAULT_HUD_SPRITE);
               String unspentText = unspentSkillPoints == 1 ? " unspent skill point" : " unspent skill points";
               String unspentPointsText = unspentSkillPoints + "";
               int unspentPointsWidth = minecraft.field_71466_p.func_78256_a(unspentPointsText);
               int unspentWidth = minecraft.field_71466_p.func_78256_a(unspentText);
               minecraft.field_71466_p.func_238405_a_(matrixStack, unspentSkillPoints + "", right - unspentWidth - unspentPointsWidth - gap, 18.0F, -10240);
               minecraft.field_71466_p.func_238405_a_(matrixStack, unspentText, right - unspentWidth - gap, 18.0F, -1);
               minecraft.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
            }

            if (unspentKnowledgePoints > 0) {
               minecraft.func_110434_K().func_110577_a(VAULT_HUD_SPRITE);
               String unspentText = unspentKnowledgePoints == 1 ? " unspent knowledge point" : " unspent knowledge points";
               String unspentPointsText = unspentKnowledgePoints + "";
               int unspentPointsWidth = minecraft.field_71466_p.func_78256_a(unspentPointsText);
               int unspentWidth = minecraft.field_71466_p.func_78256_a(unspentText);
               matrixStack.func_227860_a_();
               if (unspentSkillPoints > 0) {
                  matrixStack.func_227861_a_(0.0, 12.0, 0.0);
               }

               minecraft.field_71466_p
                  .func_238405_a_(matrixStack, unspentKnowledgePoints + "", right - unspentWidth - unspentPointsWidth - gap, 18.0F, -12527695);
               minecraft.field_71466_p.func_238405_a_(matrixStack, unspentText, right - unspentWidth - gap, 18.0F, -1);
               matrixStack.func_227865_b_();
               minecraft.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
            }

            matrixStack.func_227865_b_();
            expGainedAnimation.tick((int)(now - previousTick));
            previousTick = now;
            if (minecraft.field_71442_b != null && minecraft.field_71442_b.func_78763_f()) {
               minecraft.func_213239_aq().func_76320_a("vaultBar");
               minecraft.func_110434_K().func_110577_a(VAULT_HUD_SPRITE);
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

               FontHelper.drawStringWithBorder(matrixStack, text, (float)textX, (float)textY, -6601, 3945472);
               minecraft.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
               minecraft.func_213239_aq().func_76319_b();
            }
         }
      }
   }

   private static int potionOffsetY(ClientPlayerEntity player) {
      List<EffectInstance> effectInstances = player.func_70651_bq().stream().filter(EffectInstance::func_205348_f).collect(Collectors.toList());
      if (effectInstances.size() == 0) {
         return 0;
      } else {
         for (EffectInstance effectInstance : effectInstances) {
            if (effectInstance.func_188419_a().func_220303_e() == EffectType.HARMFUL) {
               return 36;
            }
         }

         return 18;
      }
   }
}
