package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.network.message.VaultOverlayMessage;
import iskallia.vault.util.ListHelper;
import iskallia.vault.world.vault.modifier.TexturedVaultModifier;
import iskallia.vault.world.vault.modifier.VaultModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class VaultRaidOverlay {
   public static final ResourceLocation RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");
   public static final int PANIC_TICKS_THRESHOLD = 600;

   @SubscribeEvent
   public static void onRender(Post event) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (event.getType() == ElementType.POTION_ICONS && type != VaultOverlayMessage.OverlayType.NONE) {
         int remainingTicks = ClientVaultRaidData.getRemainingTicks();
         boolean canGetRecordTime = ClientVaultRaidData.canGetRecordTime();
         Minecraft minecraft = Minecraft.func_71410_x();
         MatrixStack matrixStack = event.getMatrixStack();
         int bottom = minecraft.func_228018_at_().func_198087_p();
         int barWidth = 62;
         int hourglassWidth = 12;
         int hourglassHeight = 16;
         int color = -1;
         if (remainingTicks < 600) {
            if (remainingTicks % 10 < 5) {
               color = -65536;
            }
         } else if (canGetRecordTime) {
            color = -17664;
         }

         String timer = UIHelper.formatTimeString(remainingTicks);
         if (ClientVaultRaidData.showTimer()) {
            FontHelper.drawStringWithBorder(matrixStack, timer, (float)(barWidth + 18), (float)(bottom - 12), color, -16777216);
         }

         minecraft.func_110434_K().func_110577_a(RESOURCE);
         RenderSystem.enableBlend();
         RenderSystem.disableDepthTest();
         matrixStack.func_227860_a_();
         if (ClientVaultRaidData.showTimer()) {
            matrixStack.func_227861_a_(barWidth + 30, bottom - 25, 0.0);
            if (remainingTicks < 600) {
               matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(remainingTicks * 10.0F % 360.0F));
            } else {
               matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(remainingTicks % 360));
            }

            matrixStack.func_227861_a_(-hourglassWidth / 2.0F, -hourglassHeight / 2.0F, 0.0);
            ScreenDrawHelper.drawQuad(
               buf -> ScreenDrawHelper.rect(buf, matrixStack)
                  .dim(hourglassWidth, hourglassHeight)
                  .texVanilla(1.0F, 36.0F, hourglassWidth, hourglassHeight)
                  .draw()
            );
         }

         matrixStack.func_227865_b_();
         if (type == VaultOverlayMessage.OverlayType.VAULT) {
            renderVaultModifiers(event);
         }

         minecraft.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
      }
   }

   public static void renderVaultModifiers(Post event) {
      Minecraft minecraft = Minecraft.func_71410_x();
      MatrixStack matrixStack = event.getMatrixStack();
      VaultModifiers modifiers = ClientVaultRaidData.getModifiers();
      int right = minecraft.func_228018_at_().func_198107_o();
      int bottom = minecraft.func_228018_at_().func_198087_p();
      int rightMargin = 28;
      int modifierSize = 24;
      int modifierGap = 2;
      ListHelper.traverseOccurrences(modifiers, (index, modifier, occurrence) -> {
         if (modifier instanceof TexturedVaultModifier) {
            minecraft.func_110434_K().func_110577_a(((TexturedVaultModifier)modifier).getIcon());
            int x = index % 4;
            int y = index / 4;
            int offsetX = modifierSize * x + modifierGap * Math.max(x - 1, 0);
            int offsetY = modifierSize * y + modifierGap * Math.max(y - 1, 0);
            int posX = right - (rightMargin + modifierSize) - offsetX;
            int posY = bottom - modifierSize - 2 - offsetY;
            AbstractGui.func_238463_a_(matrixStack, posX, posY, 0.0F, 0.0F, modifierSize, modifierSize, modifierSize, modifierSize);
            if (occurrence > 1L) {
               String text = String.valueOf(occurrence);
               int textWidth = minecraft.field_71466_p.func_78256_a(text);
               minecraft.field_71466_p.func_238405_a_(matrixStack, text, posX + (modifierSize - textWidth), posY + (modifierSize - 10), -1);
            }
         }
      });
   }
}
