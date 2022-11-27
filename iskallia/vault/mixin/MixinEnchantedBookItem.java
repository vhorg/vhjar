package iskallia.vault.mixin;

import iskallia.vault.util.OverlevelEnchantHelper;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EnchantedBookItem.class})
public class MixinEnchantedBookItem {
   @Inject(
      method = {"appendHoverText"},
      at = {@At("TAIL")}
   )
   public void appendOverlevelBookExplanation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
      if (stack.getItem() == Items.ENCHANTED_BOOK && OverlevelEnchantHelper.getOverlevels(stack) != -1) {
         tooltip.add(new TextComponent(""));
         tooltip.add(new TextComponent("Upgrades an equipment's EXISTING").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF")).withItalic(true)));
         tooltip.add(
            new TextComponent("enchantment level when used on Anvil.").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF")).withItalic(true))
         );
      }
   }
}
