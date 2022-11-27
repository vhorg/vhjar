package iskallia.vault.block.entity;

import iskallia.vault.client.util.color.ColorUtil;
import iskallia.vault.init.ModBlocks;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScavengerTreasureTileEntity extends BlockEntity {
   private static final Random rand = new Random();

   protected ScavengerTreasureTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
      super(tileEntityTypeIn, pos, state);
   }

   public ScavengerTreasureTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.SCAVENGER_TREASURE_TILE_ENTITY, pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, ScavengerTreasureTileEntity tile) {
      if (level.isClientSide()) {
         tile.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      if (rand.nextInt(4) == 0) {
         ParticleEngine mgr = Minecraft.getInstance().particleEngine;
         BlockPos pos = this.getBlockPos();
         Vec3 rPos = new Vec3(
            pos.getX() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 1.5,
            pos.getY() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 1.5,
            pos.getZ() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 1.5
         );
         SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.createParticle(ParticleTypes.FIREWORK, rPos.x, rPos.y, rPos.z, 0.0, 0.0, 0.0);
         if (p != null) {
            p.setColor(ColorUtil.blendColors(-3241472, -3229440, rand.nextFloat()));
         }
      }
   }
}
