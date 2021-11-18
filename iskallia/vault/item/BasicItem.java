package iskallia.vault.item;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class BasicItem extends Item {
   ITextComponent[] tooltip;

   public BasicItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public BasicItem withTooltip(ITextComponent... tooltip) {
      this.tooltip = tooltip;
      return this;
   }

   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      super.func_77624_a(stack, worldIn, tooltip, flagIn);
      if (this.tooltip != null) {
         tooltip.addAll(Arrays.asList(this.tooltip));
      }
   }
}
