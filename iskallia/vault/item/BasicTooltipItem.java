package iskallia.vault.item;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BasicTooltipItem extends BasicItem {
   private final List<ITextComponent> components;

   public BasicTooltipItem(ResourceLocation id, Properties properties, ITextComponent... components) {
      this(id, properties, Arrays.asList(components));
   }

   public BasicTooltipItem(ResourceLocation id, Properties properties, List<ITextComponent> components) {
      super(id, properties);
      this.components = components;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      tooltip.add(StringTextComponent.field_240750_d_);
      tooltip.addAll(this.components);
   }
}
