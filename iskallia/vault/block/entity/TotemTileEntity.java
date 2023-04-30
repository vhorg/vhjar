package iskallia.vault.block.entity;

import com.mojang.math.Vector3f;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class TotemTileEntity extends BlockEntity {
   protected static final int RANGE_PARTICLE_MINIMUM = 10;
   protected static final int RANGE_PARTICLE_MAXIMUM = 200;
   protected static final int UPDATE_INTERVAL_TICKS = 2;
   private int remainingDurationTicks;
   private float effectRadius;
   private int nextUpdateTick;
   private UUID playerUUID;
   private Vec3 effectOrigin = new Vec3(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5);
   private AABB effectBounds;
   protected static final String TAG_REMAINING_DURATION_TICKS = "remainingDurationTicks";
   protected static final String TAG_EFFECT_RADIUS = "effectRadius";
   protected static final String TAG_NEXT_UPDATE_TICK = "nextUpdateTick";
   private static final String TAG_PLAYER_UUID = "playerUUID";
   @OnlyIn(Dist.CLIENT)
   private TotemTileEntity.RenderContext renderContext;

   public TotemTileEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   protected void initialize(UUID playerUUID, int durationTicks, float effectRadius) {
      this.playerUUID = playerUUID;
      this.remainingDurationTicks = durationTicks;
      this.effectRadius = effectRadius;
      this.resetUpdateCounter();
      this.updateEffectBounds();
   }

   protected void updateEffectBounds() {
      this.effectBounds = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).move(this.getBlockPos()).inflate(this.getEffectRadius() + 2.0F);
   }

   protected void resetUpdateCounter() {
      this.nextUpdateTick = this.getUpdateIntervalTicks();
   }

   protected int getRemainingDurationTicks() {
      return this.remainingDurationTicks;
   }

   protected int decrementUpdateCounter(int ticks) {
      return this.nextUpdateTick -= ticks;
   }

   public float getEffectRadius() {
      return this.effectRadius;
   }

   protected int getUpdateIntervalTicks() {
      return 2;
   }

   public AABB getEffectBounds() {
      return this.effectBounds;
   }

   public Vec3 getEffectOrigin() {
      return this.effectOrigin;
   }

   public UUID getPlayerUUID() {
      return this.playerUUID;
   }

   protected boolean serverTick() {
      if (--this.remainingDurationTicks <= 0) {
         this.removeTotem();
         return false;
      } else {
         this.doParticles();
         this.doAmbientSound();
         return true;
      }
   }

   protected void removeTotem() {
      if (this.level != null) {
         BlockPos blockPos = this.getBlockPos();
         if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 25, 0.25, 0.25, 0.25, 0.0);
            serverLevel.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.5F, 2.0F);
         }

         this.level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
      }
   }

   protected void doParticles() {
      if (this.getRemainingDurationTicks() % 5 == 0 && this.level instanceof ServerLevel serverLevel) {
         BlockPos blockPos = this.getBlockPos();
         this.doFountainParticles(serverLevel, blockPos);
         this.doEffectRangeParticles(serverLevel, blockPos);
      }
   }

   protected abstract ParticleOptions getFountainParticleOptions();

   private void doFountainParticles(ServerLevel serverLevel, BlockPos blockPos) {
      serverLevel.sendParticles(
         this.getFountainParticleOptions(), blockPos.getX() + 0.5, blockPos.getY() + 0.7, blockPos.getZ() + 0.5, 5, 0.075, 0.0, 0.075, 0.0
      );
   }

   protected abstract ParticleOptions getEffectRangeParticleOptions(float var1);

   private void doEffectRangeParticles(ServerLevel serverLevel, BlockPos blockPos) {
      serverLevel.sendParticles(
         this.getEffectRangeParticleOptions(this.effectRadius),
         blockPos.getX() + 0.5,
         blockPos.getY() + 0.5,
         blockPos.getZ() + 0.5,
         this.getEffectRangeParticleCount(),
         0.0,
         0.0,
         0.0,
         0.0
      );
   }

   protected int getEffectRangeParticleCount() {
      return Mth.clamp((int)((Math.PI * 4) * this.effectRadius * this.effectRadius * 0.166666), 10, 200);
   }

   public abstract Vector3f getParticleEffectColor();

   protected void doAmbientSound() {
      if (this.level != null) {
         if (this.remainingDurationTicks >= 40 && this.level.getGameTime() % 40L == 0L) {
            BlockPos blockPos = this.getBlockPos();
            this.level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.5F, 2.0F);
         }
      }
   }

   public void load(@Nonnull CompoundTag tag) {
      super.load(tag);
      this.remainingDurationTicks = tag.getInt("remainingDurationTicks");
      this.effectRadius = tag.getFloat("effectRadius");
      this.nextUpdateTick = tag.getInt("nextUpdateTick");
      this.playerUUID = tag.getUUID("playerUUID");
      this.updateEffectBounds();
   }

   protected void saveAdditional(@Nonnull CompoundTag tag) {
      super.saveAdditional(tag);
      tag.putInt("remainingDurationTicks", this.remainingDurationTicks);
      tag.putFloat("effectRadius", this.effectRadius);
      tag.putInt("nextUpdateTick", this.nextUpdateTick);
      tag.putUUID("playerUUID", this.playerUUID);
   }

   @OnlyIn(Dist.CLIENT)
   public <R extends TotemTileEntity.RenderContext> R getRenderContext() {
      if (this.renderContext == null) {
         this.renderContext = this.createRenderContext();
      }

      return (R)this.renderContext;
   }

   @Nonnull
   @OnlyIn(Dist.CLIENT)
   protected TotemTileEntity.RenderContext createRenderContext() {
      return new TotemTileEntity.RenderContext();
   }

   @OnlyIn(Dist.CLIENT)
   public static class RenderContext {
      public float rotationAngleRadians;
      public int[] glyphIndices;
   }
}
