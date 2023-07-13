package iskallia.vault.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ScreenParticle {
   public static final int[] PARTICLE_COLORS = new int[]{-8185907, -9037875, -9758771, -10545203, -11397171};
   protected Random random = new Random();
   protected float angleMin;
   protected float angleMax;
   protected float speedMin;
   protected float speedMax;
   protected int delayMin;
   protected int delayMax;
   protected int lifetimeMin;
   protected int lifetimeMax;
   protected int sizeMin;
   protected int sizeMax;
   protected int quantityMin;
   protected int quantityMax;
   protected Vec3 spawnerPos = new Vec3(0.0, 0.0, 0.0);
   protected float spawnerHeight;
   protected float spawnerWidth;
   protected List<ScreenParticle.Particle> particles = new LinkedList<>();

   public ScreenParticle angleRange(float min, float max) {
      this.angleMin = (float)Math.toRadians(min);
      this.angleMax = (float)Math.toRadians(max);
      return this;
   }

   public ScreenParticle speedRange(float min, float max) {
      this.speedMin = min;
      this.speedMax = max;
      return this;
   }

   public ScreenParticle delayRange(int min, int max) {
      this.delayMin = min;
      this.delayMax = max;
      return this;
   }

   public ScreenParticle lifespanRange(int min, int max) {
      this.lifetimeMin = min;
      this.lifetimeMax = max;
      return this;
   }

   public ScreenParticle sizeRange(int min, int max) {
      this.sizeMin = min;
      this.sizeMax = max;
      return this;
   }

   public ScreenParticle quantityRange(int min, int max) {
      this.quantityMin = min;
      this.quantityMax = max;
      return this;
   }

   public ScreenParticle spawnedPosition(int x, int y) {
      this.spawnerPos = new Vec3(x, y, 0.0);
      return this;
   }

   public ScreenParticle spawnedWidthHeight(int w, int h) {
      this.spawnerWidth = w;
      this.spawnerHeight = h;
      return this;
   }

   private int randi(int min, int max) {
      return min == max ? min : this.random.nextInt(max - min) + min;
   }

   private float randf(float min, float max) {
      return min == max ? min : this.random.nextFloat() * (max - min) + min;
   }

   public void tick() {
      for (ScreenParticle.Particle particle : this.particles) {
         if (particle.hasDelay()) {
            particle.tickDelay--;
         } else {
            particle.posO = particle.pos;
            particle.roll = particle.roll + particle.rollSpeed * particle.rollDir;
            particle.pos = particle.pos.add(particle.velocity);
            particle.velocity = particle.velocity.scale(0.97);
            particle.velocity = particle.velocity.add(particle.velocityAdded);
            particle.ticksLived++;
         }
      }

      this.particles.removeIf(ScreenParticle.Particle::shouldDespawn);
   }

   public void render(PoseStack matrixStack, float partialTick) {
      for (ScreenParticle.Particle particle : this.particles) {
         double x0 = particle.pos.x() - particle.size / 2.0;
         double y0 = particle.pos.y() - particle.size / 2.0;
         double x0Old = particle.posO.x() - particle.size / 2.0;
         double y0Old = particle.posO.y() - particle.size / 2.0;
         matrixStack.pushPose();
         matrixStack.translate(Mth.lerp(partialTick, x0, x0Old), Mth.lerp(partialTick, y0, y0Old), 0.0);
         matrixStack.translate(particle.size / 2.0F, particle.size / 2.0F, 0.0);
         matrixStack.mulPose(Vector3f.ZP.rotationDegrees(particle.roll));
         matrixStack.translate(-particle.size / 2.0F, -particle.size / 2.0F, 0.0);
         matrixStack.scale(
            1.0F - (float)Math.pow((float)particle.ticksLived / particle.ticksLifespan, 5.0),
            1.0F - (float)Math.pow((float)particle.ticksLived / particle.ticksLifespan, 5.0),
            1.0F
         );
         GuiComponent.fill(matrixStack, 0, 0, particle.size, particle.size, particle.color);
         matrixStack.popPose();
      }

      RenderSystem.enableBlend();
   }

   public void pop() {
      this.pop(1.0F, 1.0F);
   }

   public void pop(float velocityMul, float quantityMul) {
      int quantity = (int)(this.randi(this.quantityMin, this.quantityMax) * quantityMul);

      for (int i = 0; i < quantity; i++) {
         ScreenParticle.Particle particle = new ScreenParticle.Particle();
         particle.rollSpeed = particle.rollSpeed + Mth.randomBetween(this.random, 0.0F, 20.0F);
         particle.roll = particle.rollSpeed * this.random.nextInt(20);
         particle.velocity = new Vec3(1.0, 0.0, 0.0)
            .zRot(-this.randf(this.angleMin, this.angleMax))
            .scale(this.randf(this.speedMin, this.speedMax))
            .scale(velocityMul);
         particle.velocityAdded = new Vec3(0.0, Mth.randomBetween(this.random, -0.02F, 0.02F), 0.0);
         particle.size = this.randi(this.sizeMin, this.sizeMax);
         particle.pos = new Vec3(
            this.spawnerPos.x + Mth.randomBetween(this.random, 0.0F, this.spawnerWidth),
            this.spawnerPos.y + Mth.randomBetween(this.random, 0.0F, this.spawnerHeight),
            0.0
         );
         particle.posO = particle.pos;
         particle.rollDir = this.random.nextBoolean() ? -1 : 1;
         particle.color = PARTICLE_COLORS[this.random.nextInt(PARTICLE_COLORS.length)];
         particle.tickDelay = this.randi(this.delayMin, this.delayMax);
         particle.ticksLifespan = this.randi(this.lifetimeMin, this.lifetimeMax);
         this.particles.add(particle);
      }
   }

   protected static class Particle {
      public Vec3 pos;
      public Vec3 posO;
      public Vec3 velocity;
      public Vec3 velocityAdded;
      protected float roll;
      protected float rollSpeed = 0.0F;
      protected int rollDir;
      public int size;
      public int color;
      public int ticksLived;
      public int ticksLifespan;
      public int tickDelay;

      public boolean hasDelay() {
         return this.tickDelay > 0;
      }

      public boolean shouldDespawn() {
         return this.ticksLived >= this.ticksLifespan;
      }
   }
}
