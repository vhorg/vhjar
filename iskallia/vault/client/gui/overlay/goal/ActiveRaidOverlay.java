package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.vault.goal.ActiveRaidGoalData;
import iskallia.vault.client.vault.goal.VaultGoalData;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ActiveRaidOverlay extends BossBarOverlay {
   public static final ResourceLocation VAULT_HUD_RESOURCE = Vault.id("textures/gui/vault-hud.png");
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
   public int drawOverlay(MatrixStack renderStack, float pTicks) {
      int offsetY = 5;
      offsetY = this.drawWaveDisplay(renderStack, pTicks, offsetY);
      offsetY = this.drawMobBar(renderStack, pTicks, offsetY);
      return this.drawModifierDisplay(renderStack, pTicks, offsetY);
   }

   private int drawWaveDisplay(MatrixStack renderStack, float pTicks, int offsetY) {
      if (this.data.getTotalWaves() <= 0) {
         return offsetY;
      } else {
         String waveDisplay = String.format("%s / %s", this.data.getWave() + 1, this.data.getTotalWaves());
         String fullDisplay = waveDisplay;
         if (this.data.getTickWaveDelay() > 0) {
            fullDisplay = waveDisplay + " - " + UIHelper.formatTimeString(this.data.getTickWaveDelay());
         }

         Impl buffer = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
         Minecraft mc = Minecraft.func_71410_x();
         FontRenderer fr = mc.field_71466_p;
         int width = fr.func_78256_a(waveDisplay);
         float midX = mc.func_228018_at_().func_198107_o() / 2.0F;
         renderStack.func_227860_a_();
         renderStack.func_227861_a_(midX - width / 2.0F, offsetY, 0.0);
         renderStack.func_227862_a_(1.25F, 1.25F, 1.0F);
         FontHelper.drawStringWithBorder(renderStack, fullDisplay, 0.0F, 0.0F, 16777215, 0);
         buffer.func_228461_a_();
         renderStack.func_227865_b_();
         return offsetY + 13;
      }
   }

   private int drawMobBar(MatrixStack renderStack, float pTicks, int offsetY) {
      if (this.data.getTotalWaves() <= 0) {
         return offsetY;
      } else {
         Minecraft mc = Minecraft.func_71410_x();
         mc.func_110434_K().func_110577_a(VAULT_HUD_RESOURCE);
         float killedPerc = (float)this.data.getAliveMobs() / this.data.getTotalMobs();
         float midX = mc.func_228018_at_().func_198107_o() / 2.0F;
         int width = 182;
         int mobWidth = (int)(width * killedPerc);
         int totalWidth = width - mobWidth;
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         ScreenDrawHelper.drawQuad(
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
         RenderSystem.disableBlend();
         mc.func_110434_K().func_110577_a(PlayerContainer.field_226615_c_);
         return offsetY + 8;
      }
   }

   private int drawModifierDisplay(MatrixStack renderStack, float pTicks, int offsetY) {
      Impl buffer = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
      Minecraft mc = Minecraft.func_71410_x();
      FontRenderer fr = mc.field_71466_p;
      int guiScale = mc.field_71474_y.field_74335_Z;
      boolean drawAdditionalInfo = false;
      List<ITextComponent> positives = this.data.getPositives();
      List<ITextComponent> negatives = this.data.getNegatives();
      if (!mc.field_71474_y.field_74321_H.func_151470_d()) {
         drawAdditionalInfo = positives.size() > 2 || negatives.size() > 2;
         positives = positives.subList(0, Math.min(positives.size(), 2));
         negatives = negatives.subList(0, Math.min(negatives.size(), 2));
      }

      float midX = mc.func_228018_at_().func_198107_o() / 2.0F;
      float scale = guiScale < 4 && guiScale != 0 ? 1.0F : 0.7F;
      float height = 10.0F * scale;
      float maxHeight = Math.max(positives.size(), negatives.size()) * height;
      if (this.data.getRaidsCompleted() > 0) {
         renderStack.func_227860_a_();
         renderStack.func_227861_a_(midX, offsetY, 0.0);
         renderStack.func_227862_a_(scale, scale, 1.0F);
         String raid = this.data.getRaidsCompleted() > 1 ? " Raids" : " Raid";
         ITextComponent info = new StringTextComponent(this.data.getRaidsCompleted() + raid + " Completed").func_240699_a_(TextFormatting.GOLD);
         int width = fr.func_238414_a_(info);
         fr.func_243247_a(
            info, -width / 2, 0.0F, -1, false, renderStack.func_227866_c_().func_227870_a_(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords()
         );
         renderStack.func_227865_b_();
         offsetY = (int)(offsetY + height + 1.0F);
      }

      renderStack.func_227860_a_();
      renderStack.func_227861_a_(midX - 5.0F, offsetY, 0.0);
      renderStack.func_227862_a_(scale, scale, 1.0F);

      for (ITextComponent positive : positives) {
         int width = fr.func_238414_a_(positive);
         fr.func_243247_a(
            positive, -width, 0.0F, -1, false, renderStack.func_227866_c_().func_227870_a_(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords()
         );
         renderStack.func_227861_a_(0.0, 10.0, 0.0);
      }

      renderStack.func_227865_b_();
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(midX + 5.0F, offsetY, 0.0);
      renderStack.func_227862_a_(scale, scale, 1.0F);

      for (ITextComponent negative : negatives) {
         fr.func_243247_a(
            negative, 0.0F, 0.0F, -1, false, renderStack.func_227866_c_().func_227870_a_(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords()
         );
         renderStack.func_227861_a_(0.0, 10.0, 0.0);
      }

      renderStack.func_227865_b_();
      if (drawAdditionalInfo) {
         renderStack.func_227860_a_();
         renderStack.func_227861_a_(midX, offsetY + maxHeight, 0.0);
         renderStack.func_227862_a_(scale, scale, 1.0F);
         KeyBinding listSetting = mc.field_71474_y.field_74321_H;
         ITextComponent info = new StringTextComponent("Hold ").func_240699_a_(TextFormatting.DARK_GRAY).func_230529_a_(listSetting.func_238171_j_());
         int width = fr.func_238414_a_(info);
         fr.func_243247_a(
            info, -width / 2, 0.0F, -1, false, renderStack.func_227866_c_().func_227870_a_(), buffer, true, 0, LightmapHelper.getPackedFullbrightCoords()
         );
         renderStack.func_227865_b_();
         maxHeight += height;
      }

      buffer.func_228461_a_();
      return MathHelper.func_76123_f(offsetY + maxHeight);
   }
}
