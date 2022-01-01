package iskallia.vault.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.Utils;

@Mixin({DankItem.class})
public class MixinDankItem {
   @Inject(
      method = {"onItemRightClick"},
      cancellable = true,
      at = {@At(
         value = "INVOKE",
         shift = Shift.BEFORE,
         target = "net/minecraft/item/ItemStack.copy()Lnet/minecraft/item/ItemStack;"
      )}
   )
   public void onItemRightClick(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> result) {
      ItemStack bag = player.func_184586_b(hand);
      if (Utils.isConstruction(bag)) {
         ItemStack selectedItem = Utils.getItemStackInSelectedSlot(bag);
         String registryName = selectedItem.func_77973_b().getRegistryName().toString();
         if (registryName.equalsIgnoreCase("quark:pickarang") || registryName.equalsIgnoreCase("quark:flamerang")) {
            result.setReturnValue(new ActionResult(ActionResultType.FAIL, player.func_184586_b(hand)));
            result.cancel();
         }
      }
   }

   @Inject(
      method = {"onItemUse"},
      cancellable = true,
      at = {@At(
         value = "INVOKE",
         target = "Ltfar/dankstorage/utils/Utils;getHandler(Lnet/minecraft/item/ItemStack;)Ltfar/dankstorage/inventory/PortableDankHandler;"
      )}
   )
   public void onItemUse(ItemUseContext ctx, CallbackInfoReturnable<ActionResultType> cir) {
      ItemStack bag = ctx.func_195996_i();
      if (Utils.isConstruction(bag)) {
         ItemStack selectedItem = Utils.getItemStackInSelectedSlot(bag);
         String registryName = selectedItem.func_77973_b().getRegistryName().toString();
         if (registryName.equalsIgnoreCase("quark:pickarang") || registryName.equalsIgnoreCase("quark:flamerang")) {
            cir.setReturnValue(ActionResultType.FAIL);
            cir.cancel();
         }
      }
   }
}
