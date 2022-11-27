package iskallia.vault.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class VaultCatalystItem extends Item {
   public VaultCatalystItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(64));
      this.setRegistryName(id);
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag isAdvanced) {
      tooltip.add(TextComponent.EMPTY);
      tooltip.add(new TranslatableComponent("tooltip.the_vault.vault_catalyst").withStyle(ChatFormatting.GRAY));
   }
}
