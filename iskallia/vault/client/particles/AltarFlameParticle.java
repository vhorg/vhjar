package iskallia.vault.client.particles;

import net.minecraft.client.particle.DeceleratingParticle;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AltarFlameParticle extends DeceleratingParticle {
   public AltarFlameParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z, motionX, motionY, motionZ);
   }

   public IParticleRenderType func_217558_b() {
      return IParticleRenderType.field_217602_b;
   }

   public void func_187110_a(double x, double y, double z) {
      this.func_187108_a(this.func_187116_l().func_72317_d(x, y, z));
      this.func_187118_j();
   }

   public float func_217561_b(float scaleFactor) {
      float f = (this.field_70546_d + scaleFactor) / this.field_70547_e;
      return this.field_70544_f * (1.0F - f * f * 0.5F);
   }

   public int func_189214_a(float partialTick) {
      float f = (this.field_70546_d + partialTick) / this.field_70547_e;
      f = MathHelper.func_76131_a(f, 0.0F, 1.0F);
      int i = super.func_189214_a(partialTick);
      int j = i & 0xFF;
      int k = i >> 16 & 0xFF;
      j += (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle makeParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         AltarFlameParticle particle = new AltarFlameParticle(world, x, y, z, xSpeed, ySpeed, zSpeed);
         particle.func_217568_a(this.spriteSet);
         return particle;
      }
   }
}
