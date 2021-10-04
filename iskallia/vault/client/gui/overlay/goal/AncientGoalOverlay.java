package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.vault.goal.AncientGoalData;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.network.message.VaultOverlayMessage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.FORGE
)
public class AncientGoalOverlay {
   public static final ResourceLocation VAULT_HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");

   @SubscribeEvent
   public static void onObeliskRender(Post event) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (event.getType() == ElementType.HOTBAR && type == VaultOverlayMessage.OverlayType.VAULT) {
         VaultGoalData data = VaultGoalData.CURRENT_DATA;
         if (data != null) {
            if (data instanceof AncientGoalData) {
               MatrixStack renderStack = event.getMatrixStack();
               AncientGoalData displayData = (AncientGoalData)data;
               renderAncientsMessage(renderStack, displayData);
               renderAncientIndicator(renderStack, displayData);
            }

            Minecraft.func_71410_x().func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
         }
      }
   }

   private static void renderAncientsMessage(MatrixStack matrixStack, AncientGoalData data) {
      Minecraft mc = Minecraft.func_71410_x();
      FontRenderer fr = mc.field_71466_p;
      Impl buffer = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
      int bottom = mc.func_228018_at_().func_198087_p();
      int offsetY = 54;
      List<IReorderingProcessor> msg = new ArrayList<>();
      if (data.getTotalAncients() <= 0) {
         msg.add(new StringTextComponent("Hunt and escape").func_240699_a_(TextFormatting.DARK_AQUA).func_240699_a_(TextFormatting.BOLD).func_241878_f());
         msg.add(new StringTextComponent("the Vault!").func_240699_a_(TextFormatting.DARK_AQUA).func_240699_a_(TextFormatting.BOLD).func_241878_f());
         offsetY = 24;
      } else {
         String eternalPart = data.getTotalAncients() > 1 ? "eternals" : "eternal";
         msg.add(
            new StringTextComponent("Find your " + eternalPart).func_240699_a_(TextFormatting.DARK_AQUA).func_240699_a_(TextFormatting.BOLD).func_241878_f()
         );
         msg.add(new StringTextComponent("and escape the Vault!").func_240699_a_(TextFormatting.DARK_AQUA).func_240699_a_(TextFormatting.BOLD).func_241878_f());
      }

      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(12.0, bottom - offsetY - msg.size() * 10, 0.0);

      for (int i = 0; i < msg.size(); i++) {
         IReorderingProcessor txt = msg.get(i);
         fr.func_238416_a_(
            txt, 0.0F, i * 10, -1, true, matrixStack.func_227866_c_().func_227870_a_(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
         );
      }

      buffer.func_228461_a_();
      matrixStack.func_227865_b_();
   }

   private static void renderAncientIndicator(MatrixStack matrixStack, AncientGoalData data) {
      int totalAncients = data.getTotalAncients();
      int foundAncients = data.getFoundAncients();
      if (totalAncients > 0) {
         Minecraft mc = Minecraft.func_71410_x();
         int untouchedObelisks = totalAncients - foundAncients;
         int bottom = mc.func_228018_at_().func_198087_p();
         float scale = 1.0F;
         int gap = 2;
         int margin = 2;
         mc.func_110434_K().func_110577_a(VAULT_HUD_RESOURCE);
         int iconWidth = 15;
         int iconHeight = 27;
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(12.0, bottom - 24, 0.0);
         matrixStack.func_227861_a_(0.0, -margin, 0.0);
         matrixStack.func_227861_a_(0.0, -scale * iconHeight, 0.0);
         matrixStack.func_227862_a_(scale, scale, scale);

         for (int i = 0; i < foundAncients; i++) {
            int u = 81;
            int v = 109;
            AbstractGui.func_238463_a_(matrixStack, 0, 0, u, v, iconWidth, iconHeight, 256, 256);
            matrixStack.func_227861_a_(scale * gap + iconWidth, 0.0, 0.0);
         }

         for (int i = 0; i < untouchedObelisks; i++) {
            int u = 64;
            int v = 109;
            AbstractGui.func_238463_a_(matrixStack, 0, 0, u, v, iconWidth, iconHeight, 256, 256);
            matrixStack.func_227861_a_(scale * gap + iconWidth, 0.0, 0.0);
         }

         matrixStack.func_227865_b_();
      }
   }
}
