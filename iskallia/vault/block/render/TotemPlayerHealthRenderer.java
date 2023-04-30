package iskallia.vault.block.render;

import iskallia.vault.block.TotemPlayerHealthBlock;
import iskallia.vault.block.entity.TotemPlayerHealthTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;

public class TotemPlayerHealthRenderer extends TotemGlowRenderer<TotemPlayerHealthTileEntity> {
   public TotemPlayerHealthRenderer(Context context) {
      super(context);
   }

   @Nonnull
   @Override
   protected BlockState getGlowBlockState() {
      return (BlockState)ModBlocks.TOTEM_PLAYER_HEALTH.defaultBlockState().setValue(TotemPlayerHealthBlock.TYPE, TotemPlayerHealthBlock.Type.GLOW);
   }
}
