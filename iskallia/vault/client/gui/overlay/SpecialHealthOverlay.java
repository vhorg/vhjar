package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEffects;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;

@OnlyIn(Dist.CLIENT)
public class SpecialHealthOverlay {
   public static final ResourceLocation VAULT_HUD_SPRITE = new ResourceLocation("the_vault", "textures/gui/vault_hud.png");
   private static final List<SpecialHealthOverlay.HeartRenderInfo> HEART_RENDER_INFOS = List.of(
      new SpecialHealthOverlay.HeartRenderInfo(126, 2, -1, -1, player -> {
         AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
         if (healthAttribute == null) {
            return 0;
         } else {
            AttributeModifier corruptionAttribute = healthAttribute.getModifier(ModEffects.CORRUPTION_HEALTH_MODIFIER_ID);
            return corruptionAttribute != null ? (int)Math.abs(corruptionAttribute.getAmount()) : 0;
         }
      }), new SpecialHealthOverlay.HeartRenderInfo(135, 2, -1, -1, player -> {
         AttributeInstance manaShieldAttribute = player.getAttribute(ModAttributes.MANA_SHIELD);
         return manaShieldAttribute == null ? 0 : (int)manaShieldAttribute.getValue();
      })
   );

   public static int getSpecialHealthPoints(Player player) {
      return HEART_RENDER_INFOS.stream().mapToInt(heartRenderInfo -> heartRenderInfo.getHealthPoints().applyAsInt(player)).sum();
   }

   public static void renderSpecialHearts(
      PoseStack poseStack,
      Player player,
      int left,
      int top,
      int rowHeight,
      int regen,
      float healthMax,
      int health,
      int healthLast,
      int absorb,
      boolean highlight
   ) {
      if (player != null) {
         int maxHearts = Mth.ceil(healthMax / 2.0);
         int absorbtionHearts = Mth.ceil(absorb / 2.0);
         int currentIndex = maxHearts + absorbtionHearts;
         int currentRow = currentIndex / 10;
         int vanillaHealthRows = currentRow;

         for (SpecialHealthOverlay.HeartRenderInfo heartRenderInfo : HEART_RENDER_INFOS) {
            int indexInRow = currentIndex % 10;
            int x = left + indexInRow * 8;
            int y = top - currentRow * rowHeight;
            int healthPoints = heartRenderInfo.getHealthPoints().applyAsInt(player);
            if (healthPoints > 0) {
               for (int j = 0; j < healthPoints / 2; j++) {
                  renderHeart(poseStack, x, y, heartRenderInfo);
                  currentIndex++;
                  currentRow = currentIndex / 10;
                  indexInRow = currentIndex % 10;
                  x = left + indexInRow * 8;
                  y = top - currentRow * rowHeight;
               }

               if (healthPoints % 2 == 1) {
                  renderHeartLeftHalf(poseStack, x, y, heartRenderInfo);
                  currentIndex++;
               }
            }
         }

         ((ForgeIngameGui)Minecraft.getInstance().gui).left_height += (currentRow - vanillaHealthRows) * rowHeight;
      }
   }

   private static void renderHeart(PoseStack poseStack, int x, int y, SpecialHealthOverlay.HeartRenderInfo heartRenderInfo) {
      setupShaderAndTexture();
      if (heartRenderInfo.containerU() > -1 && heartRenderInfo.containerV() > -1) {
         GuiComponent.blit(poseStack, x, y, heartRenderInfo.containerU(), heartRenderInfo.containerV(), 9, 9, 256, 256);
      }

      if (heartRenderInfo.heartU() > -1 && heartRenderInfo.heartV() > -1) {
         GuiComponent.blit(poseStack, x, y, heartRenderInfo.heartU(), heartRenderInfo.heartV(), 9, 9, 256, 256);
      }
   }

   private static void renderHeartLeftHalf(PoseStack poseStack, int x, int y, SpecialHealthOverlay.HeartRenderInfo heartRenderInfo) {
      setupShaderAndTexture();
      if (heartRenderInfo.containerU() > -1 && heartRenderInfo.containerV() > -1) {
         GuiComponent.blit(poseStack, x, y, heartRenderInfo.containerU(), heartRenderInfo.containerV(), 5, 9, 256, 256);
      }

      if (heartRenderInfo.heartU() > -1 && heartRenderInfo.heartV() > -1) {
         GuiComponent.blit(poseStack, x, y, heartRenderInfo.heartU(), heartRenderInfo.heartV(), 5, 9, 256, 256);
      }
   }

   private static void setupShaderAndTexture() {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, VAULT_HUD_SPRITE);
   }

   private record HeartRenderInfo(int containerU, int containerV, int heartU, int heartV, ToIntFunction<Player> getHealthPoints) {
   }
}
