package iskallia.vault.block.item;

import iskallia.vault.init.ModItems;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.TraderCore;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;

public class GlobalTraderBlockItem extends BlockItem {
   public GlobalTraderBlockItem(Block block) {
      super(block, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(64));
   }

   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      CompoundNBT nbt = stack.func_77978_p();
      if (nbt != null) {
         CompoundNBT blockEntityTag = nbt.func_74775_l("BlockEntityTag");

         for (INBT tag : blockEntityTag.func_150295_c("coresList", 10)) {
            try {
               TraderCore core = NBTSerializer.deserialize(TraderCore.class, (CompoundNBT)tag);
               StringTextComponent text = new StringTextComponent(" Vendor: " + core.getName());
               text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-26266)));
               tooltip.add(text);
               return;
            } catch (Exception var12) {
               var12.printStackTrace();
            }
         }
      }

      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }
}
