package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.client.vault.goal.VaultObeliskData;
import iskallia.vault.network.message.VaultOverlayMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ObeliskGoalOverlay {
   public static final ResourceLocation VAULT_HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");

   @SubscribeEvent
   public static void onObeliskRender(Post event) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (event.getType() == ElementType.HOTBAR && type == VaultOverlayMessage.OverlayType.VAULT) {
         VaultGoalData data = VaultGoalData.CURRENT_DATA;
         if (data != null) {
            if (data instanceof VaultObeliskData) {
               MatrixStack renderStack = event.getMatrixStack();
               VaultObeliskData displayData = (VaultObeliskData)data;
               renderObeliskMessage(renderStack, displayData);
               renderObeliskIndicator(renderStack, displayData);
            }

            Minecraft.func_71410_x().func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
         }
      }
   }

   private static void renderObeliskMessage(MatrixStack matrixStack, VaultObeliskData data) {
      Minecraft mc = Minecraft.func_71410_x();
      FontRenderer fr = mc.field_71466_p;
      Impl buffer = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
      int bottom = mc.func_228018_at_().func_198087_p();
      IReorderingProcessor bidiText = data.getMessage().func_241878_f();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(15.0, bottom - 34, 0.0);
      fr.func_238416_a_(
         bidiText, 0.0F, 0.0F, -1, true, matrixStack.func_227866_c_().func_227870_a_(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
      );
      buffer.func_228461_a_();
      matrixStack.func_227865_b_();
   }

   private static void renderObeliskIndicator(MatrixStack matrixStack, VaultObeliskData data) {
      int maxObelisks = data.getMaxObelisks();
      int touchedObelisks = data.getCurrentObelisks();
      if (maxObelisks > 0) {
         Minecraft mc = Minecraft.func_71410_x();
         int untouchedObelisks = maxObelisks - touchedObelisks;
         int bottom = mc.func_228018_at_().func_198087_p();
         float scale = 0.6F;
         int gap = 2;
         int margin = 2;
         mc.func_110434_K().func_110577_a(VAULT_HUD_RESOURCE);
         int iconWidth = 12;
         int iconHeight = 22;
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(15.0, bottom - 34, 0.0);
         matrixStack.func_227861_a_(0.0, -margin, 0.0);
         matrixStack.func_227861_a_(0.0, -scale * iconHeight, 0.0);
         matrixStack.func_227862_a_(scale, scale, scale);

         for (int i = 0; i < touchedObelisks; i++) {
            int u = 77;
            int v = 84;
            AbstractGui.func_238463_a_(matrixStack, 0, 0, u, v, iconWidth, iconHeight, 256, 256);
            matrixStack.func_227861_a_(scale * gap + iconWidth, 0.0, 0.0);
         }

         for (int i = 0; i < untouchedObelisks; i++) {
            int u = 64;
            int v = 84;
            AbstractGui.func_238463_a_(matrixStack, 0, 0, u, v, iconWidth, iconHeight, 256, 256);
            matrixStack.func_227861_a_(scale * gap + iconWidth, 0.0, 0.0);
         }

         matrixStack.func_227865_b_();
      }
   }
}
