package iskallia.vault.item;

import iskallia.vault.block.RelicPedestalBlock;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.dynamodel.model.item.PlainItemModel;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModRelics;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RelicItem extends BlockItem implements DynamicModelItem {
   public RelicItem(CreativeModeTab group, ResourceLocation id) {
      super(ModBlocks.RELIC_PEDESTAL, new Properties().tab(group).stacksTo(1));
      this.setRegistryName(id);
   }

   @Override
   public Optional<ResourceLocation> getDynamicModelId(ItemStack itemStack) {
      return DynamicModelItem.getGenericModelId(itemStack);
   }

   @Nonnull
   public Block getBlock() {
      return ModBlocks.RELIC_PEDESTAL;
   }

   public void fillItemCategory(@Nonnull CreativeModeTab category, @Nonnull NonNullList<ItemStack> items) {
      if (this.allowdedIn(category)) {
         ModRelics.RECIPE_REGISTRY.values().forEach(relicRecipe -> {
            ResourceLocation relicId = relicRecipe.getResultingRelic();
            ItemStack itemStack = new ItemStack(this);
            DynamicModelItem.setGenericModelId(itemStack, relicId);
            items.add(itemStack);
         });
      }
   }

   @Nonnull
   public Component getName(@Nonnull ItemStack itemStack) {
      ResourceLocation modelId = DynamicModelItem.getGenericModelId(itemStack).orElse(null);
      if (modelId == null) {
         return super.getName(itemStack);
      } else {
         PlainItemModel itemModel = ModDynamicModels.Relics.RELIC_REGISTRY.get(modelId).orElse(null);
         return (Component)(itemModel == null
            ? super.getName(itemStack)
            : new TextComponent(itemModel.getDisplayName()).withStyle(Style.EMPTY.withColor(-2505149)));
      }
   }

   protected boolean placeBlock(@Nonnull BlockPlaceContext context, @Nonnull BlockState blockState) {
      ItemStack itemStack = context.getItemInHand();
      ModRelics.RelicRecipe relicRecipe = DynamicModelItem.getGenericModelId(itemStack).map(ModRelics.RECIPE_REGISTRY::get).orElse(ModRelics.EMPTY);
      return super.placeBlock(context, (BlockState)blockState.setValue(RelicPedestalBlock.RELIC, relicRecipe));
   }
}
