package iskallia.vault.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VoidOrbItem extends BasicItem {
   public VoidOrbItem(ResourceLocation id, Properties properties) {
      super(id, properties);
   }

   public Rarity func_77613_e(ItemStack stack) {
      return Rarity.RARE;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      tooltip.add(StringTextComponent.field_240750_d_);
      tooltip.add(new StringTextComponent("Removes a modifier at random from").func_240699_a_(TextFormatting.GRAY));
      tooltip.add(new StringTextComponent("a vault gear item when combined").func_240699_a_(TextFormatting.GRAY));
      tooltip.add(new StringTextComponent("in an anvil.").func_240699_a_(TextFormatting.GRAY));
      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }
}
