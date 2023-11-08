package iskallia.vault.integration.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import iskallia.vault.client.particles.CharmParticle;
import iskallia.vault.client.particles.EnderAnchorParticle;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModParticles;
import iskallia.vault.item.gear.CharmItem;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CuriosCharmCompat implements ICurioRenderer {
   Map<UUID, CuriosCharmCompat.ParticleData> charmList = new HashMap<>();
   ParticleEngine pe = Minecraft.getInstance().particleEngine;
   Random random = new Random();
   Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
   private static final double RADIUS = 1.0;

   public static void register() {
      CuriosRendererRegistry.register(ModItems.SMALL_CHARM, CuriosCharmCompat::new);
      CuriosRendererRegistry.register(ModItems.LARGE_CHARM, CuriosCharmCompat::new);
      CuriosRendererRegistry.register(ModItems.GRAND_CHARM, CuriosCharmCompat::new);
      CuriosRendererRegistry.register(ModItems.MAJESTIC_CHARM, CuriosCharmCompat::new);
   }

   public <T extends LivingEntity, M extends EntityModel<T>> void render(
      ItemStack stack,
      SlotContext slotContext,
      PoseStack matrixStack,
      RenderLayerParent<T, M> renderLayerParent,
      MultiBufferSource renderTypeBuffer,
      int light,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      matrixStack.pushPose();
      Vec3 pos = slotContext.entity().position();
      LivingEntity entity = slotContext.entity();
      if (!this.charmList.containsKey(slotContext.entity().getUUID())) {
         Vec3 target = new Vec3(pos.x, pos.y, pos.z);
         Vec3 vec = new Vec3(-entity.getBbWidth() / 4.0F * 3.0F, 0.0, 0.0).yRot(-((float)Math.toRadians(entity.yBodyRot)) - (float)Math.toRadians(20.0));
         Vec3 particlePos = new Vec3(
            entity.position().x + vec.x, entity.position().y + entity.getBbHeight() + entity.getBbHeight() * 0.15, entity.position().z + vec.z
         );
         this.charmList.put(slotContext.entity().getUUID(), new CuriosCharmCompat.ParticleData(entity.tickCount, target, particlePos, entity));
      } else {
         double[] offset = generateRandomSmoothPath(slotContext.entity().tickCount + partialTicks, 25.0F);
         CuriosCharmCompat.ParticleData data = this.charmList.get(slotContext.entity().getUUID());
         if (data.lastTick != entity.tickCount) {
            Vec3 vec3 = new Vec3(-entity.getBbWidth() / 4.0F * 3.0F, 0.0, 0.0).yRot(-((float)Math.toRadians(entity.yBodyRot)) - (float)Math.toRadians(20.0));
            Vec3 target = new Vec3(pos.x, pos.y + entity.getBbHeight() + entity.getBbHeight() * 0.15, pos.z)
               .add(vec3)
               .add(offset[0] / 7.0, generateRandomSmoothPath((slotContext.entity().tickCount + partialTicks) / 2.0F, 30.0F)[0] / 10.0, offset[1] / 7.0);
            Vec3 vec2 = new Vec3(target.x - data.particlePos.x, target.y - data.particlePos.y, target.z - data.particlePos.z).scale(0.25);
            data.particlePos = new Vec3(data.particlePos.x + vec2.x, data.particlePos.y + vec2.y, data.particlePos.z + vec2.z);
            if (data.particle == null
               || !data.particle.isAlive()
               || data.particle.getCustomTexture() != null && !data.particle.getCustomTexture().equals(CharmItem.getParticleLoc(stack))) {
               Vec3 vec = new Vec3(-entity.getBbWidth() / 4.0F * 3.0F, 0.0, 0.0).yRot(-((float)Math.toRadians(entity.yBodyRot)) - (float)Math.toRadians(20.0));
               Vec3 particlePos = new Vec3(
                  entity.position().x + vec.x, entity.position().y + entity.getBbHeight() + entity.getBbHeight() * 0.15, entity.position().z + vec.z
               );
               data.particlePos = particlePos;
               Particle particle = this.pe
                  .createParticle((ParticleOptions)ModParticles.CHARM.get(), particlePos.x, particlePos.y, particlePos.z, 0.0, 0.0, 0.0);
               if (particle instanceof CharmParticle charmParticle) {
                  particle.setLifetime(500);
                  charmParticle.setLastAgeRendered(charmParticle.getAge());
                  charmParticle.setCustomTexture(CharmItem.getParticleLoc(stack));
                  data.particle = charmParticle;
               }
            } else {
               Vec3 particlePos = data.particlePos;
               data.particle.setPos(particlePos.x, particlePos.y, particlePos.z);
               data.particle.setLastAgeRendered(data.particle.getAge());
               Quaternion quaternion = this.camera.rotation();
               if (stack.getItem() == ModItems.SMALL_CHARM) {
                  Vec3 vector3f1 = this.camera.getPosition().subtract(particlePos).normalize().multiply(-0.2F, -0.2F, -0.2F);
                  if (this.pe
                     .createParticle(
                        (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
                        particlePos.x + vector3f1.x(),
                        particlePos.y + vector3f1.y(),
                        particlePos.z + vector3f1.z(),
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F
                     ) instanceof EnderAnchorParticle enderParticle) {
                     enderParticle.scale(0.75F);
                     int color = CharmItem.getCharm(stack).map(effect -> effect.getCharmConfig().getColor()).orElse(16777215);
                     float r = (color >> 16 & 0xFF) / 255.0F;
                     float g = (color >> 8 & 0xFF) / 255.0F;
                     float b = (color & 0xFF) / 255.0F;
                     enderParticle.setColor(r, g, b);
                     enderParticle.setLifetime(3);
                  }
               }

               if (stack.getItem() == ModItems.LARGE_CHARM) {
                  Vec3 vector3f1 = this.camera.getPosition().subtract(particlePos).normalize().multiply(-0.2F, -0.2F, -0.2F);
                  if (this.pe
                     .createParticle(
                        (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
                        particlePos.x + vector3f1.x(),
                        particlePos.y + vector3f1.y(),
                        particlePos.z + vector3f1.z(),
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F
                     ) instanceof EnderAnchorParticle enderParticle) {
                     enderParticle.scale(0.75F);
                     int color = CharmItem.getCharm(stack).map(effect -> effect.getCharmConfig().getColor()).orElse(16777215);
                     float r = (color >> 16 & 0xFF) / 255.0F;
                     float g = (color >> 8 & 0xFF) / 255.0F;
                     float b = (color & 0xFF) / 255.0F;
                     enderParticle.setColor(r, g, b);
                     enderParticle.setLifetime(4);
                  }

                  vector3f1 = this.camera.getPosition().subtract(particlePos).normalize().multiply(-0.2F, -0.2F, -0.2F);
                  if (this.pe
                     .createParticle(
                        (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
                        particlePos.x + vector3f1.x(),
                        particlePos.y + vector3f1.y(),
                        particlePos.z + vector3f1.z(),
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F
                     ) instanceof EnderAnchorParticle enderParticle) {
                     enderParticle.scale(0.75F);
                     int color = CharmItem.getCharm(stack).map(effect -> effect.getCharmConfig().getColor()).orElse(16777215);
                     float r = (color >> 16 & 0xFF) / 255.0F;
                     float g = (color >> 8 & 0xFF) / 255.0F;
                     float b = (color & 0xFF) / 255.0F;
                     enderParticle.setColor(r, g, b);
                     enderParticle.setLifetime(4);
                  }
               }

               if (stack.getItem() == ModItems.GRAND_CHARM) {
                  Vec3 vector3f1x = this.camera.getPosition().subtract(particlePos).normalize().multiply(-0.2F, -0.2F, -0.2F);
                  if (this.pe
                     .createParticle(
                        (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
                        particlePos.x + vector3f1x.x(),
                        particlePos.y + vector3f1x.y(),
                        particlePos.z + vector3f1x.z(),
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F
                     ) instanceof EnderAnchorParticle enderParticle) {
                     enderParticle.scale(0.75F);
                     int color = CharmItem.getCharm(stack).map(effect -> effect.getCharmConfig().getColor()).orElse(16777215);
                     float r = (color >> 16 & 0xFF) / 255.0F;
                     float g = (color >> 8 & 0xFF) / 255.0F;
                     float b = (color & 0xFF) / 255.0F;
                     enderParticle.setColor(r, g, b);
                     enderParticle.setLifetime(5);
                  }

                  if (this.pe
                     .createParticle(
                        (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
                        particlePos.x + vector3f1x.x(),
                        particlePos.y + vector3f1x.y(),
                        particlePos.z + vector3f1x.z(),
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F
                     ) instanceof EnderAnchorParticle enderParticle) {
                     enderParticle.scale(0.75F);
                     int color = CharmItem.getCharm(stack).map(effect -> effect.getCharmConfig().getColor()).orElse(16777215);
                     float r = (color >> 16 & 0xFF) / 255.0F;
                     float g = (color >> 8 & 0xFF) / 255.0F;
                     float b = (color & 0xFF) / 255.0F;
                     enderParticle.setColor(r, g, b);
                     enderParticle.setLifetime(5);
                  }

                  vector3f1x = this.camera.getPosition().subtract(particlePos).normalize().multiply(-0.2F, -0.2F, -0.2F);
                  if (this.pe
                     .createParticle(
                        (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
                        particlePos.x + vector3f1x.x(),
                        particlePos.y + vector3f1x.y(),
                        particlePos.z + vector3f1x.z(),
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F
                     ) instanceof EnderAnchorParticle enderParticle) {
                     enderParticle.scale(0.75F);
                     int color = CharmItem.getCharm(stack).map(effect -> effect.getCharmConfig().getColor()).orElse(16777215);
                     float r = (color >> 16 & 0xFF) / 255.0F;
                     float g = (color >> 8 & 0xFF) / 255.0F;
                     float b = (color & 0xFF) / 255.0F;
                     enderParticle.setColor(r, g, b);
                     enderParticle.setLifetime(5);
                  }
               }

               if (stack.getItem() == ModItems.MAJESTIC_CHARM) {
                  Vec3 vector3f1xx = this.camera.getPosition().subtract(particlePos).normalize().multiply(-0.2F, -0.2F, -0.2F);
                  if (this.pe
                     .createParticle(
                        (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
                        particlePos.x + vector3f1xx.x(),
                        particlePos.y + vector3f1xx.y(),
                        particlePos.z + vector3f1xx.z(),
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F
                     ) instanceof EnderAnchorParticle enderParticle) {
                     enderParticle.scale(0.75F);
                     int color = CharmItem.getCharm(stack).map(effect -> effect.getCharmConfig().getColor()).orElse(16777215);
                     float r = (color >> 16 & 0xFF) / 255.0F;
                     float g = (color >> 8 & 0xFF) / 255.0F;
                     float b = (color & 0xFF) / 255.0F;
                     enderParticle.setColor(r, g, b);
                     enderParticle.setLifetime(6);
                  }

                  if (this.pe
                     .createParticle(
                        (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
                        particlePos.x + vector3f1xx.x(),
                        particlePos.y + vector3f1xx.y(),
                        particlePos.z + vector3f1xx.z(),
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F
                     ) instanceof EnderAnchorParticle enderParticle) {
                     enderParticle.scale(0.75F);
                     int color = CharmItem.getCharm(stack).map(effect -> effect.getCharmConfig().getColor()).orElse(16777215);
                     float r = (color >> 16 & 0xFF) / 255.0F;
                     float g = (color >> 8 & 0xFF) / 255.0F;
                     float b = (color & 0xFF) / 255.0F;
                     enderParticle.setColor(r, g, b);
                     enderParticle.setLifetime(6);
                  }

                  vector3f1xx = this.camera.getPosition().subtract(particlePos).normalize().multiply(-0.2F, -0.2F, -0.2F);
                  if (this.pe
                     .createParticle(
                        (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
                        particlePos.x + vector3f1xx.x(),
                        particlePos.y + vector3f1xx.y(),
                        particlePos.z + vector3f1xx.z(),
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F,
                        -0.05F + this.random.nextFloat() * 0.1F
                     ) instanceof EnderAnchorParticle enderParticle) {
                     enderParticle.scale(0.75F);
                     int color = CharmItem.getCharm(stack).map(effect -> effect.getCharmConfig().getMajesticColor()).orElse(16777215);
                     float r = (color >> 16 & 0xFF) / 255.0F;
                     float g = (color >> 8 & 0xFF) / 255.0F;
                     float b = (color & 0xFF) / 255.0F;
                     enderParticle.setColor(r, g, b);
                     enderParticle.setLifetime(6);
                  }
               }
            }

            data.lastTick = entity.tickCount;
         }
      }

      matrixStack.popPose();
   }

   public static double[] generateRandomSmoothPath(double T, float speed) {
      double angle = (Math.PI * 2) * (T / speed);
      double x = 1.0 * Math.cos(angle);
      double y = 1.0 * Math.sin(angle);
      return new double[]{x, y};
   }

   public static class ParticleData {
      int lastTick;
      Vec3 targetPos;
      Vec3 particlePos;
      CharmParticle particle;
      LivingEntity owner;

      public ParticleData(int lastTick, Vec3 targetPos, Vec3 particlePos, LivingEntity owner) {
         this.lastTick = lastTick;
         this.targetPos = targetPos;
         this.particlePos = particlePos;
         this.owner = owner;
      }
   }
}
