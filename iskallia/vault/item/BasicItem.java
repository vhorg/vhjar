package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class BasicItem extends Item {
   private final List<Component> tooltip = new ArrayList<>();

   public BasicItem(ResourceLocation id) {
      this(id, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   }

   public BasicItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public BasicItem withTooltip(Component tooltip) {
      this.tooltip.add(tooltip);
      return this;
   }

   public BasicItem withTooltip(Component... tooltip) {
      this.tooltip.addAll(Arrays.asList(tooltip));
      return this;
   }

   public BasicItem withTooltip(List<Component> tooltip) {
      this.tooltip.addAll(tooltip);
      return this;
   }

   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (!this.tooltip.isEmpty()) {
         tooltip.add(TextComponent.EMPTY);
         tooltip.addAll(this.tooltip);
      }
   }
}
