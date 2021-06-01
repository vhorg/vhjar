package iskallia.vault.item;

import iskallia.vault.util.RelicSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;

public class RelicPartItem extends Item {
   protected RelicSet relicSet;

   public RelicPartItem(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(64));
      this.setRegistryName(id);
   }

   public RelicSet getRelicSet() {
      return this.relicSet;
   }

   public void setRelicSet(RelicSet relicSet) {
      this.relicSet = relicSet;
   }

   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      StringTextComponent line = new StringTextComponent("Vault Relic - " + this.relicSet.getName());
      line.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-3755746)));
      tooltip.add(new StringTextComponent(""));
      tooltip.add(line);
      super.func_77624_a(stack, world, tooltip, flag);
   }
}
