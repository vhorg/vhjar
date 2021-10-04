package iskallia.vault.block.item;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.RelicSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RelicStatueBlockItem extends BlockItem {
   public RelicStatueBlockItem() {
      super(ModBlocks.RELIC_STATUE, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      CompoundNBT nbt = stack.func_77978_p();
      if (nbt != null) {
         CompoundNBT blockEntityTag = nbt.func_74775_l("BlockEntityTag");
         String relicSet = blockEntityTag.func_74779_i("RelicSet");
         RelicSet set = RelicSet.REGISTRY.get(new ResourceLocation(relicSet));
         if (set != null) {
            StringTextComponent titleText = new StringTextComponent(" Relic Set: " + set.getName());
            titleText.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-26266)));
            tooltip.add(titleText);
         }
      }

      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   public static ItemStack withRelicSet(RelicSet relicSet) {
      ItemStack itemStack = new ItemStack(ModBlocks.RELIC_STATUE);
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("RelicSet", relicSet.getId().toString());
      CompoundNBT stackNBT = new CompoundNBT();
      stackNBT.func_218657_a("BlockEntityTag", nbt);
      itemStack.func_77982_d(stackNBT);
      return itemStack;
   }
}
