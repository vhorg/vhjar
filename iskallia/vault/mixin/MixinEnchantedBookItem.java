package iskallia.vault.mixin;

import iskallia.vault.util.OverlevelEnchantHelper;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EnchantedBookItem.class})
public class MixinEnchantedBookItem {
   @Inject(
      method = {"addInformation"},
      at = {@At("TAIL")}
   )
   public void appendOverlevelBookExplanation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag, CallbackInfo ci) {
      if (stack.func_77973_b() == Items.field_151134_bR && OverlevelEnchantHelper.getOverlevels(stack) != -1) {
         tooltip.add(new StringTextComponent(""));
         tooltip.add(
            new StringTextComponent("Upgrades an equipment's EXISTING")
               .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240745_a_("#FFFFFF")).func_240722_b_(true))
         );
         tooltip.add(
            new StringTextComponent("enchantment level when used on Anvil.")
               .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240745_a_("#FFFFFF")).func_240722_b_(true))
         );
      }
   }
}
