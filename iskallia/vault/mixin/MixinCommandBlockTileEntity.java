package iskallia.vault.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({CommandBlockEntity.class})
public abstract class MixinCommandBlockTileEntity extends BlockEntity {
   public MixinCommandBlockTileEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
      super(pType, pWorldPosition, pBlockState);
   }
}
