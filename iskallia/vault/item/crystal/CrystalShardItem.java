package iskallia.vault.item.crystal;

import iskallia.vault.item.BasicItem;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrystalShardItem extends BasicItem {
   private final List<Component> lines;

   public CrystalShardItem(ResourceLocation id, CreativeModeTab group, MutableComponent... lines) {
      super(id, new Properties().tab(group).stacksTo(8));
      this.lines = Arrays.stream(lines).map(txt -> txt.withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC)).collect(Collectors.toList());
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
   }
}
