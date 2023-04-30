package iskallia.vault.client.particles;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LuckyHitDrainParticle extends TextureSheetParticle {
   protected float rotationDir;
   protected float fallingSpeed;
   protected double xdStart;
   protected double ydStart;
   protected double zdStart;
   protected double ydExtra;
   private LivingEntity livingEntity = null;

   public LuckyHitDrainParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.xdStart = motionX;
      this.ydStart = motionY;
      this.zdStart = motionZ;
      this.ydExtra = new Random().nextFloat() * (motionY / 10.0);
      this.rotationDir = new Random().nextFloat() - 0.5F;
      this.fallingSpeed = new Random().nextFloat();
      this.lifetime = 50 + (int)(new Random().nextFloat() * 50.0F);
      this.quadSize = 0.25F * (this.random.nextFloat() * 0.25F + 0.1F);
   }

   public void setLivingEntity(LivingEntity livingEntity) {
      this.livingEntity = livingEntity;
   }

   public void move(double pX, double pY, double pZ) {
      this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
      this.setLocationFromBoundingbox();
   }

   public int getLightColor(float pPartialTick) {
      return 15728880;
   }

   public void tick() {
      this.xd = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime) * this.xdStart;
      this.yd = Math.min(1.0F, (float)this.age / this.lifetime) * this.ydStart + this.ydExtra * ((float)this.age / this.lifetime);
      this.zd = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime) * this.zdStart;
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.livingEntity != null) {
         Vec3 vec = new Vec3(
            this.livingEntity.position().x - this.x,
            this.livingEntity.position().y + this.livingEntity.getBbHeight() / 2.0F - this.y,
            this.livingEntity.position().z - this.z
         );
         double len = vec.length();
         if (len < 0.5) {
            this.remove();
         }

         vec = vec.normalize().scale((float)this.age / this.lifetime);
         this.move(vec.x, vec.y, vec.z);
         this.alpha = Mth.clamp((float)len / 8.0F, 0.0F, 1.0F);
      }

      this.yd = this.yd - 0.04 * this.gravity;
      this.move(this.xd, this.yd, this.zd);
      if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
         this.xd *= 1.1;
         this.zd *= 1.1;
      }

      this.xd = this.xd * this.friction;
      this.yd = this.yd * this.friction;
      this.zd = this.zd * this.friction;
      if (this.onGround) {
         this.xd *= 0.7F;
         this.zd *= 0.7F;
      }

      if (this.age++ >= this.lifetime) {
         this.remove();
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet sprite) {
         this.spriteSet = sprite;
      }

      @Nullable
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Random rand = new Random();
         float colorOffset = rand.nextFloat() * 0.6F;
         LuckyHitDrainParticle luckyHitDrainParticle = new LuckyHitDrainParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         luckyHitDrainParticle.pickSprite(this.spriteSet);
         luckyHitDrainParticle.setColor(0.8F - colorOffset, 0.8F - colorOffset, 0.8F - colorOffset);
         luckyHitDrainParticle.setSprite(this.spriteSet.get(rand));
         return luckyHitDrainParticle;
      }
   }
}
