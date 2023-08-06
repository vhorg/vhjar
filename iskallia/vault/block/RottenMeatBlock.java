package iskallia.vault.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec3;

public class RottenMeatBlock extends Block {
   public RottenMeatBlock() {
      super(Properties.of(Material.CLAY, MaterialColor.GRASS).sound(SoundType.SLIME_BLOCK).noOcclusion());
   }

   public void fallOn(Level p_154567_, BlockState p_154568_, BlockPos p_154569_, Entity p_154570_, float p_154571_) {
      if (p_154570_.isSuppressingBounce()) {
         super.fallOn(p_154567_, p_154568_, p_154569_, p_154570_, p_154571_);
      } else {
         p_154570_.causeFallDamage(p_154571_, 0.0F, DamageSource.FALL);
      }
   }

   public void updateEntityAfterFallOn(BlockGetter pLevel, Entity pEntity) {
      if (pEntity.isSuppressingBounce()) {
         super.updateEntityAfterFallOn(pLevel, pEntity);
      } else {
         this.bounceUp(pEntity);
      }
   }

   private void bounceUp(Entity pEntity) {
      Vec3 vec3 = pEntity.getDeltaMovement();
      if (vec3.y < 0.0) {
         double d0 = pEntity instanceof LivingEntity ? 1.0 : 0.8;
         pEntity.setDeltaMovement(vec3.x, -vec3.y * 0.66F * d0, vec3.z);
      }
   }
}
