package iskallia.vault.config.entry.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.recipe.TrinketForgeRecipe;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
import iskallia.vault.init.ModItems;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ConfigTrinketRecipe extends ConfigForgeRecipe<TrinketForgeRecipe> {
   @Expose
   private final ResourceLocation trinket;

   public ConfigTrinketRecipe(TrinketEffect<?> trinket) {
      super(trinket.getRegistryName(), new ItemStack(ModItems.TRINKET));
      this.trinket = trinket.getRegistryName();
   }

   public TrinketForgeRecipe makeRecipe() {
      TrinketEffect<?> trinket = TrinketEffectRegistry.getEffect(this.trinket);
      if (trinket == null) {
         throw new IllegalArgumentException("Unknown trinket: " + this.trinket.toString());
      } else {
         ItemStack out = this.output.createItemStack();
         List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
         return new TrinketForgeRecipe(this.id, out, in, trinket);
      }
   }
}
