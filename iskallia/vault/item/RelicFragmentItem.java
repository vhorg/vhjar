package iskallia.vault.item;

import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.dynamodel.model.item.PlainItemModel;
import iskallia.vault.event.InputEvents;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModRelics;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RelicFragmentItem extends Item implements DynamicModelItem {
   public RelicFragmentItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(64));
      this.setRegistryName(id);
   }

   @Override
   public Optional<ResourceLocation> getDynamicModelId(ItemStack itemStack) {
      return DynamicModelItem.getGenericModelId(itemStack);
   }

   public void fillItemCategory(@Nonnull CreativeModeTab category, @Nonnull NonNullList<ItemStack> items) {
      if (this.allowdedIn(category)) {
         ModRelics.RECIPE_REGISTRY.values().forEach(relicRecipe -> relicRecipe.getFragments().forEach(fragmentId -> {
            ItemStack itemStack = new ItemStack(this);
            DynamicModelItem.setGenericModelId(itemStack, fragmentId);
            items.add(itemStack);
         }));
      }
   }

   @Nonnull
   public Component getName(@Nonnull ItemStack itemStack) {
      ResourceLocation modelId = DynamicModelItem.getGenericModelId(itemStack).orElse(null);
      if (modelId == null) {
         return super.getName(itemStack);
      } else {
         PlainItemModel itemModel = ModDynamicModels.Relics.FRAGMENT_REGISTRY.get(modelId).orElse(null);
         return (Component)(itemModel == null
            ? super.getName(itemStack)
            : new TextComponent(itemModel.getDisplayName() + " (Relic Fragment)").withStyle(Style.EMPTY.withColor(-2505149)));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
      DynamicModelItem.getGenericModelId(stack)
         .flatMap(ModDynamicModels.Relics.FRAGMENT_REGISTRY::get)
         .map(DynamicModel::getId)
         .flatMap(ModRelics::getRelicOfFragment)
         .map(ModRelics.RelicRecipe::getResultingRelic)
         .flatMap(ModDynamicModels.Relics.RELIC_REGISTRY::get)
         .ifPresent(
            relicModel -> {
               tooltip.add(
                  new TextComponent("Assembles: ")
                     .append(new TextComponent(relicModel.getDisplayName()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(-3755746))))
               );
               tooltip.add(new TextComponent(""));
               tooltip.add(new TextComponent("Craft a Relic Pedestal to assemble").withStyle(ChatFormatting.GRAY));
               tooltip.add(new TextComponent("this fragment into a Relic").withStyle(ChatFormatting.GRAY));
               if (flag.isAdvanced()) {
                  if (InputEvents.isShiftDown()) {
                     ResourceLocation fragmentId = DynamicModelItem.getGenericModelId(stack).orElse(ModRelics.EMPTY.getResultingRelic());
                     tooltip.add(new TextComponent(""));
                     tooltip.add(new TextComponent("Relic Fragment Id: "));
                     tooltip.add(new TextComponent(fragmentId.toString()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(-3755746))));
                  } else {
                     tooltip.add(new TextComponent("Press <SHIFT> for fragment id").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
                  }
               }
            }
         );
      super.appendHoverText(stack, world, tooltip, flag);
   }
}
