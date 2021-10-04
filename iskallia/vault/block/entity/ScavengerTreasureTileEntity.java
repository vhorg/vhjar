package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.MiscUtils;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScavengerTreasureTileEntity extends TileEntity implements ITickableTileEntity {
   private static final Random rand = new Random();

   protected ScavengerTreasureTileEntity(TileEntityType<?> tileEntityTypeIn) {
      super(tileEntityTypeIn);
   }

   public ScavengerTreasureTileEntity() {
      super(ModBlocks.SCAVENGER_TREASURE_TILE_ENTITY);
   }

   public void func_73660_a() {
      if (this.field_145850_b.func_201670_d()) {
         this.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      if (rand.nextInt(4) == 0) {
         ParticleManager mgr = Minecraft.func_71410_x().field_71452_i;
         BlockPos pos = this.func_174877_v();
         Vector3d rPos = new Vector3d(
            pos.func_177958_n() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 1.5,
            pos.func_177956_o() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 1.5,
            pos.func_177952_p() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 1.5
         );
         SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.func_199280_a(
            ParticleTypes.field_197629_v, rPos.field_72450_a, rPos.field_72448_b, rPos.field_72449_c, 0.0, 0.0, 0.0
         );
         if (p != null) {
            p.field_187149_H = 0.0F;
            p.func_187146_c(MiscUtils.blendColors(-3241472, -3229440, rand.nextFloat()));
         }
      }
   }
}
