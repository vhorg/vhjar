package iskallia.vault.entity.entity.elite;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EliteHuskEntity extends Husk {
   public EliteHuskEntity(EntityType<? extends Husk> entityType, Level world) {
      super(entityType, world);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         BlockState blockState = new BlockState(Blocks.SAND, ImmutableMap.of(), MapCodec.unit(this.getBlockStateOn()));
         this.level
            .addParticle(
               new BlockParticleOption(ParticleTypes.FALLING_DUST, blockState), this.getRandomX(0.9), this.getY() + 1.9F, this.getRandomZ(0.9), 0.0, 0.01, 0.0
            );
      }
   }
}
