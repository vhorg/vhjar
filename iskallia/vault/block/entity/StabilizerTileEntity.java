package iskallia.vault.block.entity;

import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StabilizerTileEntity extends BlockEntity {
   private static final Random rand = new Random();
   private static final AABB RENDER_BOX = new AABB(-1.0, -1.0, -1.0, 1.0, 2.0, 1.0);
   private boolean active = false;
   private int timeout = 20;
   private final List<Object> particleReferences = new ArrayList<>();

   public StabilizerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.STABILIZER_TILE_ENTITY, pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, StabilizerTileEntity tile) {
      if (!level.isClientSide()) {
         BlockState up = level.getBlockState(pos.above());
         if (!(up.getBlock() instanceof StabilizerBlock)) {
            level.setBlockAndUpdate(pos.above(), (BlockState)ModBlocks.STABILIZER.defaultBlockState().setValue(StabilizerBlock.HALF, DoubleBlockHalf.UPPER));
         }

         if (tile.active && tile.timeout > 0) {
            tile.timeout--;
            if (tile.timeout <= 0) {
               tile.active = false;
               tile.markForUpdate();
            }
         }
      } else {
         tile.setupParticle();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void setupParticle() {
      if (this.particleReferences.size() < 3) {
         int toAdd = 3 - this.particleReferences.size();

         for (int i = 0; i < toAdd; i++) {
            ParticleEngine mgr = Minecraft.getInstance().particleEngine;
            Particle p = mgr.createParticle(
               (ParticleOptions)ModParticles.STABILIZER_CUBE.get(),
               this.worldPosition.getX() + 0.5,
               this.worldPosition.getY() + 0.5,
               this.worldPosition.getZ() + 0.5,
               0.0,
               0.0,
               0.0
            );
            if (p != null) {
               this.particleReferences.add(p);
            }
         }
      }

      this.particleReferences.removeIf(ref -> !((Particle)ref).isAlive());
      if (this.isActive()) {
         Vec3 particlePos = new Vec3(
            this.worldPosition.getX() + rand.nextFloat(), this.worldPosition.getY() + rand.nextFloat() * 2.0F, this.worldPosition.getZ() + rand.nextFloat()
         );
         ParticleEngine mgr = Minecraft.getInstance().particleEngine;
         SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.createParticle(
            ParticleTypes.FIREWORK, particlePos.x, particlePos.y, particlePos.z, 0.0, 0.0, 0.0
         );
         if (p != null) {
            p.setColor(301982);
         }
      }
   }

   public void setActive() {
      this.active = true;
      this.timeout = 20;
      this.markForUpdate();
   }

   public boolean isActive() {
      return this.active;
   }

   private void markForUpdate() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.active = tag.getBoolean("active");
   }

   public void saveAdditional(CompoundTag tag) {
      tag.putBoolean("active", this.active);
   }

   public CompoundTag getUpdateTag() {
      CompoundTag nbt = super.getUpdateTag();
      this.saveAdditional(nbt);
      return nbt;
   }

   public void handleUpdateTag(CompoundTag nbt) {
      this.load(nbt);
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
      CompoundTag nbt = pkt.getTag();
      this.handleUpdateTag(nbt);
   }

   public AABB getRenderBoundingBox() {
      return RENDER_BOX.move(this.getBlockPos());
   }
}
