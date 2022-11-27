package iskallia.vault.block.entity;

import iskallia.vault.block.MonolithBlock;
import iskallia.vault.init.ModBlocks;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MonolithTileEntity extends BlockEntity {
   private static final Random rand = new Random();

   public MonolithTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.MONOLITH_TILE_ENTITY, pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, MonolithTileEntity tile) {
      if (!level.isClientSide()) {
         BlockState up = level.getBlockState(pos.above());
         if (!(up.getBlock() instanceof MonolithBlock)) {
            level.setBlockAndUpdate(pos.above(), (BlockState)ModBlocks.MONOLITH.defaultBlockState().setValue(MonolithBlock.HALF, DoubleBlockHalf.UPPER));
         }
      } else {
         tile.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      if (this.getLevel() != null) {
         BlockPos pos = this.getBlockPos();
         BlockState state = this.getBlockState();
         if (this.getLevel().getGameTime() % 5L == 0L) {
            ParticleEngine mgr = Minecraft.getInstance().particleEngine;
            if ((Boolean)state.getValue(MonolithBlock.FILLED)) {
               Random random = this.getLevel().getRandom();
               if (random.nextInt(5) == 0) {
                  Vec3 offset = new Vec3(
                     random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
                  );
                  this.getLevel()
                     .addParticle(
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        true,
                        pos.getX() + 0.5 + offset.x,
                        pos.getY() + random.nextDouble() * 0.15F + 0.55F,
                        pos.getZ() + 0.5 + offset.z,
                        offset.x / 120.0,
                        random.nextDouble() * -0.005 + 0.075,
                        offset.z / 120.0
                     );
               }

               if (random.nextInt(2) == 0) {
                  Vec3 offset = new Vec3(
                     random.nextDouble() / 9.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 9.0 * (random.nextBoolean() ? 1 : -1)
                  );
                  this.getLevel()
                     .addParticle(
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        true,
                        pos.getX() + 0.5 + offset.x,
                        pos.getY() + 1.55F + random.nextDouble() * 0.15F,
                        pos.getZ() + 0.5 + offset.z,
                        offset.x / 120.0,
                        random.nextDouble() * -0.005 + 0.075,
                        offset.z / 120.0
                     );
               }

               if (random.nextInt(3) == 0) {
                  Vec3 offset = new Vec3(
                     random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
                  );
                  this.getLevel()
                     .addParticle(
                        ParticleTypes.LAVA,
                        true,
                        pos.getX() + 0.5 + offset.x,
                        pos.getY() + random.nextDouble() * 0.15F + 0.55F,
                        pos.getZ() + 0.5 + offset.z,
                        offset.x / 12.0,
                        random.nextDouble() * 0.1 + 0.1,
                        offset.z / 12.0
                     );
               }
            } else {
               for (int count = 0; count < 1; count++) {
                  double x = pos.getX() + 0.5F + rand.nextFloat() * 0.4F - 0.2F;
                  double y = pos.getY() + rand.nextFloat() * 2.0F;
                  double z = pos.getZ() + 0.5F + rand.nextFloat() * 0.4F - 0.2F;
                  Particle fwParticle = mgr.createParticle(ParticleTypes.FIREWORK, x, y, z, 0.0, 0.0, 0.0);
                  if (fwParticle == null) {
                     return;
                  }

                  fwParticle.setLifetime((int)(fwParticle.getLifetime() * 1.5F));
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnIgniteParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < 50; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.LARGE_SMOKE,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.45F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1 + 0.1,
               offset.z / 2.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.CAMPFIRE_COSY_SMOKE,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.45F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1 + 0.1,
               offset.z / 2.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.LAVA,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.45F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1 + 0.1,
               offset.z / 2.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.LARGE_SMOKE,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.2 + 0.2,
               offset.z / 20.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.CAMPFIRE_COSY_SMOKE,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.2 + 0.1,
               offset.z / 20.0
            );
         }
      }
   }
}
