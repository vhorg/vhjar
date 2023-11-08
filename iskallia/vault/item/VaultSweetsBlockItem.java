package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class VaultSweetsBlockItem extends BlockItem {
   private final List<Component> tooltip = new ArrayList<>();

   public VaultSweetsBlockItem(Block block, FoodProperties foodProperties) {
      this(block, new Properties().tab(ModItems.VAULT_MOD_GROUP).food(foodProperties).stacksTo(64));
   }

   public VaultSweetsBlockItem(Block block, Properties properties) {
      super(block, properties);
   }

   public VaultSweetsBlockItem withTooltip(Component tooltip) {
      this.tooltip.add(tooltip);
      return this;
   }

   public VaultSweetsBlockItem withTooltip(Component... tooltip) {
      this.tooltip.addAll(Arrays.asList(tooltip));
      return this;
   }

   public VaultSweetsBlockItem withTooltip(List<Component> tooltip) {
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

   public InteractionResult place(BlockPlaceContext context) {
      return context.getPlayer() != null && !context.getPlayer().isCrouching() ? InteractionResult.FAIL : super.place(context);
   }
}
