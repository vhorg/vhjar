package iskallia.vault.block.entity;

import iskallia.vault.core.vault.pylon.PylonBuff;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PylonTileEntity extends BlockEntity {
   protected boolean consumed = false;
   public PylonBuff.Config<?> config;

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
      if (level.isClientSide()) {
         tile.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      if (this.getLevel() != null && !this.consumed) {
         BlockPos pos = this.getBlockPos();
         if (!this.isConsumed()) {
            if (this.getLevel().getGameTime() % 2L >= 1L) {
               ParticleEngine mgr = Minecraft.getInstance().particleEngine;
               int color = this.config == null ? -1 : this.config.getColor();
               int r = color >>> 16 & 0xFF;
               int g = color >>> 8 & 0xFF;
               int b = color & 0xFF;
               float ringSize = 3.0F;

               for (int i = 0; i < ringSize; i++) {
                  float angle = (i + 1) * (float) Math.PI / ringSize + (float)this.getLevel().getGameTime() / 7.0F;
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
