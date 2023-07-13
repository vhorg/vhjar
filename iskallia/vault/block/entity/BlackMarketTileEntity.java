package iskallia.vault.block.entity;

import iskallia.vault.block.BlackMarketBlock;
import iskallia.vault.client.ClientShardTradeData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlackMarketTileEntity extends BlockEntity {
   public BlackMarketTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.BLACK_MARKET_TILE_ENTITY, pos, state);
   }

   public static Vec3 getPointOnRectangleEdge(Vec3 center, double width, double height) {
      double halfWidth = width / 2.0;
      double halfHeight = height / 2.0;
      double angle = (Math.PI * 2) * Math.random();
      double x = center.x + halfWidth * Math.cos(angle);
      double y = center.y;
      double z = center.z + halfHeight * Math.sin(angle);
      return new Vec3(x, y, z);
   }

   public static float[] interpolateColor(float r1, float g1, float b1, float r2, float g2, float b2, float t) {
      return new float[]{interpolateComponent(r1, r2, t), interpolateComponent(g1, g2, t), interpolateComponent(b1, b2, t)};
   }

   public static float interpolateComponent(float c1, float c2, float t) {
      return c1 + (c2 - c1) * t;
   }

   public static void tick(Level level, BlockPos pos, BlockState state, BlackMarketTileEntity tile) {
      if (level.isClientSide && ClientShardTradeData.getAvailableTrades().containsKey(1)) {
         Random random = new Random();
         Direction dir = (Direction)state.getValue(BlackMarketBlock.FACING);
         int rot = 0;
         if (dir == Direction.WEST) {
            rot = 90;
         }

         if (dir == Direction.SOUTH) {
            rot = 180;
         }

         if (dir == Direction.EAST) {
            rot = 270;
         }

         Vec3 offset = new Vec3(0.0, 0.0, 0.12F).yRot((float)Math.toRadians(rot));
         Vec3 offset2 = getPointOnRectangleEdge(offset, 0.35F, 0.35F);
         ParticleEngine pm = Minecraft.getInstance().particleEngine;
         Particle particle = pm.createParticle(
            (ParticleOptions)ModParticles.ENDER_ANCHOR.get(),
            pos.getX() + 0.5F + offset.x + offset2.x,
            pos.getY() + 0.65F + offset.y + offset2.y,
            pos.getZ() + 0.5F + offset.z + offset2.z,
            0.0,
            Mth.randomBetween(random, 0.001F, 0.02F),
            0.0
         );
         if (particle != null) {
            float[] colors = interpolateColor(0.16796875F, 0.04296875F, 0.359375F, 0.51171875F, 0.08984375F, 0.80078125F, random.nextFloat());
            particle.setColor(colors[0], colors[1], colors[2]);
            particle.scale(0.35F);
         }
      }
   }
}
