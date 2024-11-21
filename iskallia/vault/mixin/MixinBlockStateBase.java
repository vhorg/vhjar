package iskallia.vault.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.entity.VaultChestTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockStateBase.class})
public abstract class MixinBlockStateBase {
   @Shadow
   @Mutable
   @Final
   private float destroySpeed;

   @Shadow
   public abstract Block getBlock();

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   public void init(Block block, ImmutableMap<Property<?>, Comparable<?>> map, MapCodec<BlockState> codec, CallbackInfo ci) {
      if (block instanceof LiquidBlock) {
         this.destroySpeed = 2.2F;
      }
   }

   @Inject(
      method = {"getDestroySpeed"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void changeChestBreakSpeed(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
      if (this.getBlock() instanceof VaultChestBlock) {
         if (level.getBlockEntity(pos) instanceof VaultChestTileEntity chest) {
            if (!chest.isVaultChest()) {
               cir.setReturnValue(0.3F);
            }
         }
      }
   }
}
