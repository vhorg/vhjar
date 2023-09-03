package iskallia.vault.mixin;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LiquidBlock.class})
public class MixinLiquidBlock {
   @Shadow
   @Final
   public static IntegerProperty LEVEL;

   @Inject(
      method = {"getShape"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> ci) {
      if (context instanceof EntityCollisionContext entityContext) {
         Entity entity = ((AccessorEntityCollisionContext)entityContext).getEntity();
         if (this.hasHydroVoid(entity)) {
            ci.setReturnValue(Shapes.block());
         }
      }
   }

   private boolean hasHydroVoid(Entity entity) {
      if (entity instanceof LivingEntity living) {
         ItemStack stack = living.getMainHandItem();
         if (stack.getItem() != ModItems.TOOL) {
            return false;
         } else {
            VaultGearData data = VaultGearData.read(stack);
            return data.get(ModGearAttributes.HYDROVOID, VaultGearAttributeTypeMerger.anyTrue());
         }
      } else {
         return false;
      }
   }
}
