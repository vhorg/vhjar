package iskallia.vault.item;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class BasicMobEggItem extends ForgeSpawnEggItem {
   Component[] tooltip;

   public BasicMobEggItem(ResourceLocation id, Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
      super(type, backgroundColor, highlightColor, props);
      this.setRegistryName(id);
   }

   public BasicMobEggItem withTooltip(Component... tooltip) {
      this.tooltip = tooltip;
      return this;
   }

   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (this.tooltip != null) {
         tooltip.addAll(Arrays.asList(this.tooltip));
      }
   }
}
