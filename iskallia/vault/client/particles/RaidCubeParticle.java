package iskallia.vault.client.particles;

import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaidCubeParticle extends BaseFloatingCubeParticle {
   private final BlockPos originPos;

   private RaidCubeParticle(ClientLevel world, SpriteSet spriteSet, double x, double y, double z) {
      super(world, spriteSet, x, y, z);
      this.originPos = new BlockPos(x, y, z);
   }

   @Override
   protected boolean isValid() {
      return this.getBlockEntity() != null;
   }

   @Override
   protected boolean isActive() {
      ChallengeControllerBlockEntity entity = this.getBlockEntity();
      return entity != null
         && (entity.getState() == ChallengeControllerBlockEntity.State.ACTIVE || entity.getState() == ChallengeControllerBlockEntity.State.GENERATING);
   }

   @Nullable
   private ChallengeControllerBlockEntity getBlockEntity() {
      BlockEntity var2 = this.level.getBlockEntity(this.originPos);
      return var2 instanceof ChallengeControllerBlockEntity ? (ChallengeControllerBlockEntity)var2 : null;
   }

   @Override
   protected int getActiveColor() {
      ChallengeControllerBlockEntity entity = this.getBlockEntity();
      return entity != null ? entity.getRenderer().getCoreColor() : 16777215;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      @Nullable
      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new RaidCubeParticle(world, this.spriteSet, x, y, z);
      }
   }
}
