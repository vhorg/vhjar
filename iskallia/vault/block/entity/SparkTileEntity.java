package iskallia.vault.block.entity;

import iskallia.vault.block.SparkBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SparkTileEntity extends BlockEntity {
   private int totalLifetime = 0;
   private int lifetime = 0;
   private int lifetimeOld = 0;
   private final int offsetTicks = new Random().nextInt(360);

   public SparkTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.SPARK_TILE_ENTITY, pWorldPosition, pBlockState);
   }

   public int getOffsetTicks() {
      return this.offsetTicks;
   }

   public void setLifetime(int lifetime) {
      this.totalLifetime = lifetime;
      this.lifetime = lifetime;
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public float getLifeTimePercentage() {
      return this.totalLifetime > 0 ? (float)this.lifetime / this.totalLifetime : 1.0F;
   }

   public float getLifeTimePercentageOld() {
      return this.totalLifetime > 0 ? (float)this.lifetimeOld / this.totalLifetime : 1.0F;
   }

   public boolean hasntExpiredYet() {
      return this.lifetime > 0;
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.totalLifetime = tag.getInt("TotalLifetime");
      this.lifetime = tag.getInt("Lifetime");
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.putInt("TotalLifetime", this.totalLifetime);
      tag.putInt("Lifetime", this.lifetime);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, SparkTileEntity tile) {
      tile.lifetimeOld = tile.lifetime;
      if (tile.lifetime > 0) {
         tile.lifetime--;
         if (level.isClientSide) {
            Random random = new Random();
            float rotation = random.nextFloat() * 360.0F;
            float radiusOffset = random.nextFloat() + 0.5F;
            Vec3 offset = new Vec3(radiusOffset / 15.0F * Math.cos(rotation), 0.0, radiusOffset / 15.0F * Math.sin(rotation));
            level.addParticle(
               (ParticleOptions)ModParticles.WENDARR_SPARK_EXPLODE.get(),
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + 0.5 + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 8.0,
               random.nextDouble() * 0.025 - 0.0125F,
               offset.z / 8.0
            );
            level.addParticle(
               new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState()),
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 8.0,
               random.nextDouble() * 0.025 - 0.0125F,
               offset.z / 8.0
            );
         }

         if (tile.lifetime == 0) {
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(SparkBlock.EXPENDED, true));
         }
      }
   }
}
