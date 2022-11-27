package iskallia.vault.client.gui.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.phys.Vec3;

public class ConfettiParticles {
   public static final int[] PARTICLE_COLORS = new int[]{-4317479, -3202482, -14760415, -12173357, -8633010, -1927921, -1297373, -929780};
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
   protected Vec3 spawnerPos;
   protected List<ConfettiParticles.ConfettiParticle> particles = new LinkedList<>();

   public ConfettiParticles() {
      this.spawnerPos = new Vec3(0.0, 0.0, 0.0);
   }

   public ConfettiParticles angleRange(float min, float max) {
      this.angleMin = (float)Math.toRadians(min);
      this.angleMax = (float)Math.toRadians(max);
      return this;
   }

   public ConfettiParticles speedRange(float min, float max) {
      this.speedMin = min;
      this.speedMax = max;
      return this;
   }

   public ConfettiParticles delayRange(int min, int max) {
      this.delayMin = min;
      this.delayMax = max;
      return this;
   }

   public ConfettiParticles lifespanRange(int min, int max) {
      this.lifetimeMin = min;
      this.lifetimeMax = max;
      return this;
   }

   public ConfettiParticles sizeRange(int min, int max) {
      this.sizeMin = min;
      this.sizeMax = max;
      return this;
   }

   public ConfettiParticles quantityRange(int min, int max) {
      this.quantityMin = min;
      this.quantityMax = max;
      return this;
   }

   public ConfettiParticles spawnedPosition(int x, int y) {
      this.spawnerPos = new Vec3(x, y, 0.0);
      return this;
   }

   private int randi(int min, int max) {
      return min == max ? min : this.random.nextInt(max - min) + min;
   }

   private float randf(float min, float max) {
      return min == max ? min : this.random.nextFloat() * (max - min) + min;
   }

   public void tick() {
      for (ConfettiParticles.ConfettiParticle particle : this.particles) {
         if (particle.hasDelay()) {
            particle.tickDelay--;
         } else {
            particle.pos = particle.pos.add(particle.velocity);
            particle.velocity = particle.velocity.scale(0.97);
            particle.velocity = particle.velocity.add(0.0, 0.1, 0.0);
            particle.ticksLived++;
         }
      }

      this.particles.removeIf(ConfettiParticles.ConfettiParticle::shouldDespawn);
   }

   public void render(PoseStack matrixStack) {
      for (ConfettiParticles.ConfettiParticle particle : this.particles) {
         double x0 = particle.pos.x() - particle.size / 2.0;
         double y0 = particle.pos.y() - particle.size / 2.0;
         double x1 = x0 + particle.size;
         double y1 = y0 + particle.size;
         GuiComponent.fill(matrixStack, (int)x0, (int)y0, (int)x1, (int)y1, particle.color);
      }

      RenderSystem.enableBlend();
   }

   public void pop() {
      int quantity = this.randi(this.quantityMin, this.quantityMax);

      for (int i = 0; i < quantity; i++) {
         ConfettiParticles.ConfettiParticle particle = new ConfettiParticles.ConfettiParticle();
         particle.pos = new Vec3(this.spawnerPos.x, this.spawnerPos.y, 0.0);
         particle.velocity = new Vec3(1.0, 0.0, 0.0).zRot(-this.randf(this.angleMin, this.angleMax)).scale(this.randf(this.speedMin, this.speedMax));
         particle.size = this.randi(this.sizeMin, this.sizeMax);
         particle.color = PARTICLE_COLORS[this.random.nextInt(PARTICLE_COLORS.length)];
         particle.tickDelay = this.randi(this.delayMin, this.delayMax);
         particle.ticksLifespan = this.randi(this.lifetimeMin, this.lifetimeMax);
         this.particles.add(particle);
      }
   }

   protected static class ConfettiParticle {
      public Vec3 pos;
      public Vec3 velocity;
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
