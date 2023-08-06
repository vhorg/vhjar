package iskallia.vault.item;

import iskallia.vault.client.render.VelvetBedISTER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

public class VelvetBedItem extends BedItem {
   private final List<Component> tooltip = new ArrayList<>();

   public VelvetBedItem(ResourceLocation id, Block block, Properties properties) {
      super(block, properties);
      this.setRegistryName(id);
   }

   public VelvetBedItem withTooltip(Component tooltip) {
      this.tooltip.add(tooltip);
      return this;
   }

   public VelvetBedItem withTooltip(Component... tooltip) {
      this.tooltip.addAll(Arrays.asList(tooltip));
      return this;
   }

   public VelvetBedItem withTooltip(List<Component> tooltip) {
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

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(VelvetBedISTER.INSTANCE);
   }
}
