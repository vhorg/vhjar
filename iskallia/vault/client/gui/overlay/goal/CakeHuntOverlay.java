package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.vault.goal.CakeHuntData;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.network.message.VaultOverlayMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.util.ResourceLocation;
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
public class CakeHuntOverlay {
   private static final ResourceLocation ARCHITECT_HUD = Vault.id("textures/gui/architect_event_bar.png");

   @SubscribeEvent
   public static void onArchitectBuild(Post event) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (event.getType() == ElementType.HOTBAR && type == VaultOverlayMessage.OverlayType.VAULT) {
         Minecraft mc = Minecraft.func_71410_x();
         VaultGoalData data = VaultGoalData.CURRENT_DATA;
         if (data instanceof CakeHuntData) {
            CakeHuntData displayData = (CakeHuntData)data;
            MatrixStack renderStack = event.getMatrixStack();
            Impl buffer = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
            FontRenderer fr = mc.field_71466_p;
            int bottom = mc.func_228018_at_().func_198087_p();
            float part = displayData.getCompletePercent();
            ITextComponent txt = new StringTextComponent("Find the cakes!").func_240699_a_(TextFormatting.AQUA).func_240699_a_(TextFormatting.BOLD);
            fr.func_238416_a_(
               txt.func_241878_f(),
               8.0F,
               bottom - 54,
               -1,
               true,
               renderStack.func_227866_c_().func_227870_a_(),
               buffer,
               false,
               0,
               LightmapHelper.getPackedFullbrightCoords()
            );
            txt = new StringTextComponent(displayData.getFoundCakes() + " / " + displayData.getTotalCakes())
               .func_240699_a_(TextFormatting.AQUA)
               .func_240699_a_(TextFormatting.BOLD);
            fr.func_238416_a_(
               txt.func_241878_f(),
               12.0F,
               bottom - 44,
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
               ScreenDrawHelper.rect(buf, renderStack).at(15.0F, bottom - 31).dim(54.0F, 7.0F).texVanilla(0.0F, 105.0F, 54.0F, 7.0F).draw();
               ScreenDrawHelper.rect(buf, renderStack).at(16.0F, bottom - 30).dim(52.0F * part, 5.0F).texVanilla(0.0F, 113.0F, 52.0F * part, 5.0F).draw();
            });
         }

         mc.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
      }
   }
}
