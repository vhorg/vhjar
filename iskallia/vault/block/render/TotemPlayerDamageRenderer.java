package iskallia.vault.block.render;

import iskallia.vault.block.TotemPlayerDamageBlock;
import iskallia.vault.block.entity.TotemPlayerDamageTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;

public class TotemPlayerDamageRenderer extends TotemGlowRenderer<TotemPlayerDamageTileEntity> {
   public TotemPlayerDamageRenderer(Context context) {
      super(context);
   }

   @Nonnull
   @Override
   protected BlockState getGlowBlockState() {
      return (BlockState)ModBlocks.TOTEM_PLAYER_DAMAGE.defaultBlockState().setValue(TotemPlayerDamageBlock.TYPE, TotemPlayerDamageBlock.Type.GLOW);
   }
}
