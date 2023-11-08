package iskallia.vault.block.entity;

import com.mojang.math.Vector3f;
import iskallia.vault.block.DivineAltarBlock;
import iskallia.vault.client.particles.AltarParticleOptions;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.network.message.DivineAltarConsumeMessage;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class DivineAltarTileEntity extends ScavengerAltarTileEntity {
   protected DivineAltarTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
      super(typeIn, pos, state);
      this.ticksToConsume = 40;
      this.consuming = false;
   }

   public DivineAltarTileEntity(BlockPos pos, BlockState state) {
      this(ModBlocks.DIVINE_ALTAR_TILE_ENTITY, pos, state);
   }

   public static void tickClient(Level world, BlockPos pos, BlockState state, DivineAltarTileEntity tile) {
      ScavengerAltarTileEntity.tickClient(world, pos, state, tile);
   }

   public static void tickServer(Level world, BlockPos pos, BlockState state, DivineAltarTileEntity tile) {
      ScavengerAltarTileEntity.tickServer(world, pos, state, tile);
   }

   @Override
   public void sendConsumeParticleMessage() {
      ModNetwork.CHANNEL
         .send(
            PacketDistributor.ALL.noArg(),
            new DivineAltarConsumeMessage(this.getBlockPos(), ((VaultGod)this.getBlockState().getValue(DivineAltarBlock.GOD)).getColor())
         );
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnConsumeParticles(BlockPos pos, int col) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         float r = (col >> 16 & 0xFF) / 255.0F;
         float g = (col >> 8 & 0xFF) / 255.0F;
         float b = (col & 0xFF) / 255.0F;

         for (int i = 0; i < 40; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               new AltarParticleOptions((ParticleType<AltarParticleOptions>)ModParticles.DIVINE_ALTAR_CONSUME.get(), new Vector3f(r, g, b)),
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.25,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1 + 0.1,
               offset.z / 2.0
            );
         }

         for (int i = 0; i < 30; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               new AltarParticleOptions((ParticleType<AltarParticleOptions>)ModParticles.DIVINE_ALTAR_CONSUME.get(), new Vector3f(r, g, b)),
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.2 + 0.2,
               offset.z / 20.0
            );
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   protected void playEffects(Level level) {
      if (level.isClientSide) {
         BlockPos pos = this.getBlockPos().above();
         Vec3 vec3 = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
         int particleSpeed = this.consuming && this.ticksToConsume > 32.0F ? 5 : 40;
         if (this.getBlockState().hasProperty(DivineAltarBlock.GOD)) {
            int col = ((VaultGod)this.getBlockState().getValue(DivineAltarBlock.GOD)).getColor();
            float r = (col >> 16 & 0xFF) / 255.0F;
            float g = (col >> 8 & 0xFF) / 255.0F;
            float b = (col & 0xFF) / 255.0F;

            for (BlockPos blockpos : list) {
               if (rand.nextInt(particleSpeed) == 0) {
                  float f = -0.5F + rand.nextFloat() + blockpos.above().getX();
                  float f1 = -2.0F + rand.nextFloat() + blockpos.above().getY() + 0.75F;
                  float f2 = -0.5F + rand.nextFloat() + blockpos.above().getZ();
                  level.addParticle(
                     new AltarParticleOptions((ParticleType<AltarParticleOptions>)ModParticles.DIVINE_ALTAR.get(), new Vector3f(r, g, b)),
                     vec3.x,
                     vec3.y + 0.25,
                     vec3.z,
                     f,
                     f1 + 0.5,
                     f2
                  );
               }

               if (rand.nextInt(particleSpeed) == 0) {
                  float f = -0.5F + rand.nextFloat() + blockpos.above().above().getX();
                  float f1 = -2.0F + rand.nextFloat() + blockpos.above().above().getY();
                  float f2 = -0.5F + rand.nextFloat() + blockpos.above().above().getZ();
                  level.addParticle(
                     new AltarParticleOptions((ParticleType<AltarParticleOptions>)ModParticles.DIVINE_ALTAR.get(), new Vector3f(r, g, b)),
                     vec3.x,
                     vec3.y + 0.25,
                     vec3.z,
                     f,
                     f1 + 0.5,
                     f2
                  );
               }

               if (rand.nextInt(particleSpeed) == 0) {
                  float f = -0.5F + rand.nextFloat() + blockpos.above().above().above().getX();
                  float f1 = -2.0F + rand.nextFloat() + blockpos.above().above().above().getY() - 0.75F;
                  float f2 = -0.5F + rand.nextFloat() + blockpos.above().above().above().getZ();
                  level.addParticle(
                     new AltarParticleOptions((ParticleType<AltarParticleOptions>)ModParticles.DIVINE_ALTAR.get(), new Vector3f(r, g, b)),
                     vec3.x,
                     vec3.y + 0.25,
                     vec3.z,
                     f,
                     f1 + 0.5,
                     f2
                  );
               }
            }
         }
      }
   }
}
