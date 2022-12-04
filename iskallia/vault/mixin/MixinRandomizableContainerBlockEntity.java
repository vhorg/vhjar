package iskallia.vault.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(
   value = {RandomizableContainerBlockEntity.class},
   priority = 1001
)
public abstract class MixinRandomizableContainerBlockEntity extends BaseContainerBlockEntity {
   protected MixinRandomizableContainerBlockEntity(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
      super(p_155076_, p_155077_, p_155078_);
   }

   @Overwrite
   public boolean stillValid(Player player) {
      if (this.level.getBlockEntity(this.worldPosition) != this) {
         return false;
      } else {
         double bonusReach = Math.max(player.getReachDistance() - ((Attribute)ForgeMod.REACH_DISTANCE.get()).getDefaultValue(), 0.0);
         double maxDistanceSquared = (8.0 + bonusReach) * (8.0 + bonusReach);
         return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= maxDistanceSquared;
      }
   }
}
