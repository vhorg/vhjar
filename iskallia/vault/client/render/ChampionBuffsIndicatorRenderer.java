package iskallia.vault.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.entity.champion.ChampionLogic;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModRenderTypes;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ChampionBuffsIndicatorRenderer {
   public static final Set<MobEffect> BUFFS = Set.of(MobEffects.DAMAGE_RESISTANCE, MobEffects.MOVEMENT_SPEED, MobEffects.DAMAGE_BOOST);

   public static void render(Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, Quaternion cameraOrientation) {
      if (entity instanceof LivingEntity livingEntity
         && !(livingEntity instanceof Player)
         && !(livingEntity instanceof EternalEntity)
         && !ClientVaults.getActive().isEmpty()
         && !ChampionLogic.isChampion(livingEntity)) {
         int effects = 0;
         int i = 0;

         for (MobEffect buff : BUFFS) {
            MobEffectInstance effectInstance = livingEntity.getEffect(buff);
            if (effectInstance != null) {
               effects++;
            }
         }

         for (MobEffect buffx : BUFFS) {
            MobEffectInstance effectInstance = livingEntity.getEffect(buffx);
            if (effectInstance != null) {
               poseStack.pushPose();
               poseStack.translate(0.0, entity.getBbHeight() + 0.5, 0.0);
               poseStack.mulPose(cameraOrientation);
               poseStack.scale(-0.025F, -0.025F, 0.025F);
               VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.CHAMPION_BUFF_FG_INDICATOR);
               Matrix4f matrix = poseStack.last().pose();
               int size = 10;
               RenderSystem.enableDepthTest();
               effectInstance.getEffect();
               TextureAtlasSprite icon = Minecraft.getInstance().getMobEffectTextures().get(effectInstance.getEffect());
               RenderSystem.setShaderTexture(0, icon.atlas().location());
               GuiComponent.blit(poseStack, 1 - effects * 10 + i * 20, -size + 1, 0, icon.getWidth(), icon.getHeight(), icon);
               float x1 = -(effects * 10) + i * 20;
               float x2 = x1 + size * 2;
               float y1 = -size;
               float y2 = y1 + size * 2;
               buffer.vertex(matrix, x1, y1, -0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).uv2(15728880).endVertex();
               buffer.vertex(matrix, x1, y2, -0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).uv2(15728880).endVertex();
               buffer.vertex(matrix, x2, y2, -0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F).uv2(15728880).endVertex();
               buffer.vertex(matrix, x2, y1, -0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F).uv2(15728880).endVertex();
               VertexConsumer buffer_bg = bufferSource.getBuffer(ModRenderTypes.CHAMPION_BUFF_BG_INDICATOR);
               buffer_bg.vertex(matrix, x1, y1, 0.25F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).uv2(15728880).endVertex();
               buffer_bg.vertex(matrix, x1, y2, 0.25F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).uv2(15728880).endVertex();
               buffer_bg.vertex(matrix, x2, y2, 0.25F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F).uv2(15728880).endVertex();
               buffer_bg.vertex(matrix, x2, y1, 0.25F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F).uv2(15728880).endVertex();
               poseStack.popPose();
               i++;
            }
         }
      }
   }
}
