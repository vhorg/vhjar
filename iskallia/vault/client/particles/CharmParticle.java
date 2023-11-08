package iskallia.vault.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CharmParticle extends TextureSheetParticle {
   private boolean hasHitGround;
   private final SpriteSet sprites;
   private int lastAgeRendered;
   private String customTexture;
   private static final ResourceLocation TEXTURE = VaultMod.id("textures/particle/charm.png");
   private static final ParticleRenderType CHARM_PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
      public void begin(BufferBuilder buf, TextureManager mgr) {
         PARTICLE_SHEET_TRANSLUCENT.begin(buf, mgr);
      }

      public void end(Tesselator tes) {
         PARTICLE_SHEET_TRANSLUCENT.end(tes);
      }
   };

   CharmParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ);
      this.friction = 0.96F;
      this.xd = pXSpeed;
      this.yd = pYSpeed;
      this.zd = pZSpeed;
      this.rCol = Mth.nextFloat(this.random, 0.0F, 0.0F);
      this.gCol = Mth.nextFloat(this.random, 0.8235294F, 0.9764706F);
      this.bCol = Mth.nextFloat(this.random, 0.6176471F, 0.7745098F);
      this.quadSize = 0.2F;
      this.lifetime = (int)(5.0 + this.random.nextFloat() * 3.0);
      this.hasHitGround = false;
      this.hasPhysics = false;
      this.sprites = pSprites;
      this.lastAgeRendered = this.age;
      this.setSpriteFromAge(pSprites);
   }

   public int getSpriteAge(int pParticleAge, int pParticleMaxAge) {
      return pParticleAge * 5 / pParticleMaxAge;
   }

   public void setCustomTexture(String customTexture) {
      this.customTexture = customTexture;
   }

   public String getCustomTexture() {
      return this.customTexture;
   }

   public void setLastAgeRendered(int lastAgeRendered) {
      this.lastAgeRendered = lastAgeRendered;
   }

   public int getAge() {
      return this.age;
   }

   public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
      Vec3 vec3 = pRenderInfo.getPosition();
      float f = (float)(Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
      float f1 = (float)(Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
      float f2 = (float)(Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());
      Quaternion quaternion;
      if (this.roll == 0.0F) {
         quaternion = pRenderInfo.rotation();
      } else {
         quaternion = new Quaternion(pRenderInfo.rotation());
         float f3 = Mth.lerp(pPartialTicks, this.oRoll, this.roll);
         quaternion.mul(Vector3f.ZP.rotation(f3));
      }

      Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
      vector3f1.transform(quaternion);
      Vector3f[] avector3f = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float f4 = 0.2F;

      for (int i = 0; i < 4; i++) {
         Vector3f vector3f = avector3f[i];
         vector3f.transform(quaternion);
         vector3f.mul(f4);
         vector3f.add(f, f1, f2);
      }

      ResourceLocation loc = VaultMod.id(this.customTexture);
      TextureAtlasSprite sprite = Minecraft.getInstance().particleEngine.textureAtlas.getSprite(loc);
      float minU = sprite.getU(0.0);
      float maxU = sprite.getU(16.0);
      float minV = sprite.getV(0.0);
      float maxV = sprite.getV(16.0);
      this.rCol = 0.7578125F;
      this.bCol = 0.07421875F;
      this.gCol = 0.90234375F;
      int j = this.getLightColor(pPartialTicks);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      pBuffer.defaultColor((int)(this.rCol * 256.0F), (int)(this.bCol * 256.0F), (int)(this.gCol * 256.0F), (int)(this.alpha * 256.0F));
      pBuffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(maxU, maxV).uv2(j).endVertex();
      pBuffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(maxU, minV).uv2(j).endVertex();
      pBuffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(minU, minV).uv2(j).endVertex();
      pBuffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(minU, maxV).uv2(j).endVertex();
      pBuffer.unsetDefaultColor();
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lastAgeRendered + 1 < this.age) {
         this.remove();
      }

      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         if (this.onGround) {
            this.yd = 0.0;
            this.hasHitGround = true;
         }

         if (this.hasHitGround) {
            this.yd += 0.002;
         }

         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
         }

         this.xd = this.xd * this.friction;
         this.zd = this.zd * this.friction;
         if (this.hasHitGround) {
            this.yd = this.yd * this.friction;
         }
      }
   }

   public ParticleRenderType getRenderType() {
      return CHARM_PARTICLE_SHEET_TRANSLUCENT;
   }

   public float getQuadSize(float pScaleFactor) {
      return this.quadSize * Mth.clamp((this.age + pScaleFactor) / this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      public Particle createParticle(
         SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         return new CharmParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
      }
   }
}
