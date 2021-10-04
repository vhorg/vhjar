package iskallia.vault.block.entity;

import iskallia.vault.block.ObeliskBlock;
import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.init.ModBlocks;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ObeliskTileEntity extends TileEntity implements ITickableTileEntity {
   private static final Random rand = new Random();

   public ObeliskTileEntity() {
      super(ModBlocks.OBELISK_TILE_ENTITY);
   }

   public void func_73660_a() {
      if (!this.func_145831_w().func_201670_d()) {
         BlockState up = this.func_145831_w().func_180495_p(this.func_174877_v().func_177984_a());
         if (!(up.func_177230_c() instanceof ObeliskBlock)) {
            this.func_145831_w()
               .func_175656_a(
                  this.func_174877_v().func_177984_a(),
                  (BlockState)ModBlocks.OBELISK.func_176223_P().func_206870_a(StabilizerBlock.HALF, DoubleBlockHalf.UPPER)
               );
         }
      } else {
         this.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      BlockPos pos = this.func_174877_v();
      BlockState state = this.func_145831_w().func_180495_p(pos);
      if (this.func_145831_w().func_82737_E() % 5L == 0L) {
         ParticleManager mgr = Minecraft.func_71410_x().field_71452_i;
         if ((Integer)state.func_177229_b(ObeliskBlock.COMPLETION) > 0) {
            for (int count = 0; count < 3; count++) {
               double x = pos.func_177958_n() - 0.25 + rand.nextFloat() * 1.5;
               double y = pos.func_177956_o() + rand.nextFloat() * 3.0F;
               double z = pos.func_177952_p() - 0.25 + rand.nextFloat() * 1.5;
               Particle fwParticle = mgr.func_199280_a(ParticleTypes.field_197629_v, x, y, z, 0.0, 0.0, 0.0);
               fwParticle.func_70538_b(0.4F, 0.0F, 0.0F);
            }
         } else {
            for (int count = 0; count < 5; count++) {
               double x = pos.func_177958_n() + rand.nextFloat();
               double y = pos.func_177956_o() + rand.nextFloat() * 10.0F;
               double z = pos.func_177952_p() + rand.nextFloat();
               Particle fwParticle = mgr.func_199280_a(ParticleTypes.field_197629_v, x, y, z, 0.0, 0.0, 0.0);
               fwParticle.func_187114_a((int)(fwParticle.func_206254_h() * 1.5F));
            }
         }
      }
   }
}
