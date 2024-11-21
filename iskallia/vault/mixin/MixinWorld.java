package iskallia.vault.mixin;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.world.storage.IZonedWorld;
import iskallia.vault.core.world.storage.WorldZones;
import iskallia.vault.world.data.WorldZonesData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Level.class})
public class MixinWorld implements IZonedWorld {
   @Unique
   private boolean bypassed;

   @Override
   public boolean isBypassed() {
      return this.bypassed;
   }

   @Override
   public void setBypassed(boolean bypassed) {
      this.bypassed = bypassed;
   }

   @Override
   public WorldZones getZones() {
      if (this.isBypassed()) {
         return new WorldZones();
      } else {
         Level world = (Level)this;
         return WorldZonesData.get(world).getOrCreate(world.dimension());
      }
   }

   @Inject(
      method = {"setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z"},
      at = {@At("HEAD")}
   )
   public void setBlockHead(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
      CommonEvents.BLOCK_SET.invoke((LevelWriter)this, pos, state, flags, recursionLeft, BlockSetEvent.Type.HEAD);
   }

   @Inject(
      method = {"setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z"},
      at = {@At("RETURN")}
   )
   public void setBlockReturn(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
      CommonEvents.BLOCK_SET.invoke((LevelWriter)this, pos, state, flags, recursionLeft, BlockSetEvent.Type.RETURN);
   }
}
