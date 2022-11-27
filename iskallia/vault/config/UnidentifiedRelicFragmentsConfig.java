package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.data.WeightedList;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class UnidentifiedRelicFragmentsConfig extends Config {
   @Expose
   private int extraTickPerRelicDiscovered;
   @Expose
   private WeightedList<ResourceLocation> fragments;

   @Override
   public String getName() {
      return "unidentified_relic_fragments";
   }

   public ItemStack getRandomFragment(Random random) {
      ResourceLocation fragmentId = this.fragments.getRandom(random);
      if (fragmentId == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemStack = new ItemStack(ModItems.RELIC_FRAGMENT);
         DynamicModelItem.setGenericModelId(itemStack, fragmentId);
         return itemStack;
      }
   }

   public int getExtraTickPerRelicDiscovered() {
      return this.extraTickPerRelicDiscovered;
   }

   public WeightedList<ResourceLocation> getFragments() {
      return this.fragments;
   }

   @Override
   protected void reset() {
      this.extraTickPerRelicDiscovered = 1200;
      this.fragments = new WeightedList<>();
      ModDynamicModels.Relics.FRAGMENT_REGISTRY.forEach((fragmentId, fragmentModel) -> this.fragments.add(fragmentId, 1));
   }
}
