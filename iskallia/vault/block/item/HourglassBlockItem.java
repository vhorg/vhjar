package iskallia.vault.block.item;

import iskallia.vault.init.ModItems;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HourglassBlockItem extends BlockItem {
   public HourglassBlockItem(Block blockIn) {
      super(blockIn, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      CompoundNBT tag = stack.func_196082_o().func_74775_l("BlockEntityTag");
      if (tag.func_74764_b("ownerPlayerName")) {
         tooltip.add(new StringTextComponent(tag.func_74779_i("ownerPlayerName")).func_240699_a_(TextFormatting.GOLD));
      }

      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   public static void addHourglassOwner(ItemStack stack, UUID playerUUID, String playerName) {
      if (stack.func_77973_b() instanceof HourglassBlockItem) {
         CompoundNBT tileTag = new CompoundNBT();
         tileTag.func_186854_a("ownerUUID", playerUUID);
         tileTag.func_74778_a("ownerPlayerName", playerName);
         stack.func_196082_o().func_218657_a("BlockEntityTag", tileTag);
      }
   }
}
