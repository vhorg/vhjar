package iskallia.vault.mixin;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {RedStoneWireBlock.class},
   priority = 2000
)
public abstract class MixinRedStoneWireBlock {
   @Shadow
   @Final
   public static IntegerProperty POWER;
   @Shadow
   @Final
   public static Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION;
   private final ThreadLocal<Boolean> shouldSignalSafe = ThreadLocal.withInitial(() -> true);

   @Shadow
   protected abstract BlockState getConnectionState(BlockGetter var1, BlockState var2, BlockPos var3);

   @Inject(
      method = {"calculateTargetStrength"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getBestNeighborSignal(Lnet/minecraft/core/BlockPos;)I",
         shift = Shift.BEFORE
      )}
   )
   private void calculateTargetStrengthBefore(Level state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
      this.shouldSignalSafe.set(false);
   }

   @Inject(
      method = {"calculateTargetStrength"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getBestNeighborSignal(Lnet/minecraft/core/BlockPos;)I",
         shift = Shift.AFTER
      )}
   )
   private void calculateTargetStrengthAfter(Level state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
      this.shouldSignalSafe.set(true);
   }

   @Overwrite
   public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
      return !this.shouldSignalSafe.get() ? 0 : pBlockState.getSignal(pBlockAccess, pPos, pSide);
   }

   @Overwrite
   public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
      if (this.shouldSignalSafe.get() && pSide != Direction.DOWN) {
         int i = (Integer)pBlockState.getValue(POWER);
         if (i == 0) {
            return 0;
         } else {
            return pSide != Direction.UP
                  && !((RedstoneSide)this.getConnectionState(pBlockAccess, pBlockState, pPos)
                        .getValue((Property)PROPERTY_BY_DIRECTION.get(pSide.getOpposite())))
                     .isConnected()
               ? 0
               : i;
         }
      } else {
         return 0;
      }
   }

   @Overwrite
   public boolean isSignalSource(BlockState pState) {
      return this.shouldSignalSafe.get();
   }
}
