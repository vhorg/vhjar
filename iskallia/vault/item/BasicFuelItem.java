package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BasicFuelItem extends BasicItem {
   private final List<Component> tooltip = new ArrayList<>();
   private final int fuelTime;

   public BasicFuelItem(ResourceLocation id, int fuelTime) {
      this(id, new Properties().tab(ModItems.VAULT_MOD_GROUP), fuelTime);
   }

   public BasicFuelItem(ResourceLocation id, Properties properties, int fuelTime) {
      super(id, properties);
      this.fuelTime = fuelTime;
   }

   public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
      return this.fuelTime;
   }

   public BasicFuelItem withTooltip(Component tooltip) {
      this.tooltip.add(tooltip);
      return this;
   }

   public BasicFuelItem withTooltip(Component... tooltip) {
      this.tooltip.addAll(Arrays.asList(tooltip));
      return this;
   }

   public BasicFuelItem withTooltip(List<Component> tooltip) {
      this.tooltip.addAll(tooltip);
      return this;
   }

   @Override
   public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (!this.tooltip.isEmpty()) {
         tooltip.add(TextComponent.EMPTY);
         tooltip.addAll(this.tooltip);
      }
   }
}
