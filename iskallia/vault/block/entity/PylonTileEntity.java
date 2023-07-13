package iskallia.vault.block.entity;

import iskallia.vault.core.vault.pylon.PylonBuff;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PylonTileEntity extends BlockEntity {
   protected boolean consumed = false;
   public PylonBuff.Config<?> config;
   public int tickCount = 0;

   public PylonTileEntity(BlockPos pos, BlockState blockState) {
      super(ModBlocks.PYLON_TILE_ENTITY, pos, blockState);
   }

   public boolean isConsumed() {
      return this.consumed;
   }

   public void setConsumed(boolean consumed) {
      this.consumed = consumed;
      this.sendUpdates();
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public static void tick(Level level, BlockPos pos, BlockState state, PylonTileEntity tile) {
      tile.tickCount++;
      if (level.isClientSide()) {
         tile.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      if (this.getLevel() != null && !this.consumed) {
         BlockPos pos = this.getBlockPos();
         Random random = new Random();
         if (!this.isConsumed()) {
            if (this.getLevel().getGameTime() % 2L >= 1L) {
               ParticleEngine mgr = Minecraft.getInstance().particleEngine;
               int color = this.config == null ? -1 : this.config.getColor();
               int uberColor = this.config == null ? -1 : this.config.getUberColor();
               int r = color >>> 16 & 0xFF;
               int g = color >>> 8 & 0xFF;
               int b = color & 0xFF;
               int r2 = uberColor >>> 16 & 0xFF;
               int g2 = uberColor >>> 8 & 0xFF;
               int b2 = uberColor & 0xFF;
               float ringSize = 3.0F;
               boolean isUber = this.config != null && this.config.getUber();
               if (!isUber) {
                  for (int i = 0; i < ringSize; i++) {
                     float angle = (i + 1) * (float) Math.PI / ringSize + this.tickCount / 7.0F;
                     float radius = 1.0F;
                     float x = (float)(pos.getX() + 0.5F + radius * Math.cos(angle));
                     float y = (float)(pos.getY() + 0.2F * (3 * i + 1) + Math.sin(angle) * 0.4F);
                     float z = (float)(pos.getZ() + 0.5F + radius * Math.sin(angle));
                     Particle fwParticle = mgr.createParticle(ParticleTypes.FIREWORK, x, y, z, 0.0, 0.0, 0.0);
                     if (fwParticle != null) {
                        fwParticle.setColor(r / 255.0F, g / 255.0F, b / 255.0F);
                        fwParticle.setParticleSpeed(0.0, 0.0, 0.0);
                        fwParticle.setLifetime(10);
                     }
                  }
               } else {
                  for (int ix = 0; ix < 2; ix++) {
                     float radius = random.nextFloat() * 0.75F + 0.35F;
                     float rotation = random.nextFloat() * 360.0F;
                     Vec3 offset = new Vec3(radius * Math.cos(rotation), 0.0, radius * Math.sin(rotation));
                     float f = -0.5F + random.nextFloat() + (float)offset.x();
                     float f1 = -0.5F + random.nextFloat() + (float)offset.y();
                     float f2 = -0.5F + random.nextFloat() + (float)offset.z();
                     Particle fwParticle = mgr.createParticle(
                        (ParticleOptions)ModParticles.UBER_PYLON.get(), pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, f, f1, f2
                     );
                     if (fwParticle != null) {
                        fwParticle.setColor(r / 255.0F, g / 255.0F, b / 255.0F);
                        if (random.nextInt(3) == 0) {
                           fwParticle.setColor(r2 / 255.0F, g2 / 255.0F, b2 / 255.0F);
                        }
                     }

                     float angle = (float) Math.PI * ix + this.tickCount / 7.0F;
                     radius = 2.0F;
                     float x = (float)(pos.getX() + 0.5F + radius * Math.cos(angle));
                     float y = pos.getY() + 0.5F;
                     float z = (float)(pos.getZ() + 0.5F + radius * Math.sin(angle));
                     fwParticle = mgr.createParticle(ParticleTypes.FIREWORK, x, y, z, 0.0, 0.0, 0.0);
                     if (fwParticle != null) {
                        fwParticle.setColor(r / 255.0F, g / 255.0F, b / 255.0F);
                        fwParticle.setParticleSpeed(0.0, 0.0, 0.0);
                        fwParticle.setLifetime(10);
                     }

                     fwParticle = mgr.createParticle(
                        (ParticleOptions)ModParticles.UBER_PYLON_FOUNTAIN.get(),
                        pos.getX() + 0.5F + Mth.randomBetween(random, -0.175F, 0.175F),
                        pos.getY() + 0.8F,
                        pos.getZ() + 0.5F + Mth.randomBetween(random, -0.175F, 0.175F),
                        40.0,
                        0.0625,
                        0.2625F
                     );
                     if (fwParticle != null) {
                        fwParticle.setColor(r / 255.0F, g / 255.0F, b / 255.0F);
                     }

                     fwParticle = mgr.createParticle(
                        (ParticleOptions)ModParticles.UBER_PYLON_FOUNTAIN.get(),
                        pos.getX() + 0.5F + Mth.randomBetween(random, -0.175F, 0.175F),
                        pos.getY() + 0.8F,
                        pos.getZ() + 0.5F + Mth.randomBetween(random, -0.175F, 0.175F),
                        40.0,
                        0.0625,
                        0.2625F
                     );
                     if (fwParticle != null) {
                        fwParticle.setColor(r / 255.0F, g / 255.0F, b / 255.0F);
                     }

                     fwParticle = mgr.createParticle(
                        (ParticleOptions)ModParticles.UBER_PYLON_FOUNTAIN.get(),
                        pos.getX() + 0.5F + Mth.randomBetween(random, -0.175F, 0.175F),
                        pos.getY() + 0.8F,
                        pos.getZ() + 0.5F + Mth.randomBetween(random, -0.175F, 0.175F),
                        40.0,
                        0.0625,
                        0.2625F
                     );
                     if (fwParticle != null) {
                        fwParticle.setColor(r2 / 255.0F, g2 / 255.0F, b2 / 255.0F);
                     }
                  }
               }
            }
         }
      }
   }

   protected void saveAdditional(@Nonnull CompoundTag nbt) {
      super.saveAdditional(nbt);
      nbt.putBoolean("Consumed", this.consumed);
      if (this.config != null) {
         nbt.put("Config", this.config.serializeNBT());
      }
   }

   public void load(@Nonnull CompoundTag nbt) {
      super.load(nbt);
      this.consumed = nbt.getBoolean("Consumed");
      if (nbt.contains("Config", 10)) {
         this.config = PylonBuff.Config.fromNBT(nbt.getCompound("Config"));
      }
   }

   @Nonnull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
