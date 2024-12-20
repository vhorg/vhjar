package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.LootInitialization;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class InscriptionForgeRecipe extends VaultForgeRecipe {
   public InscriptionForgeRecipe(ResourceLocation id, ItemStack output) {
      super(ForgeRecipeType.INSCRIPTION, id, output);
   }

   public InscriptionForgeRecipe(ResourceLocation id, ItemStack output, List<ItemStack> inputs) {
      super(ForgeRecipeType.INSCRIPTION, id, output, inputs);
   }

   @Override
   public ItemStack createOutput(List<OverSizedItemStack> consumed, ServerPlayer crafter, int vaultLevel) {
      ItemStack stack = super.createOutput(consumed, crafter, vaultLevel);
      if (stack.is(ModItems.INSCRIPTION)) {
         stack.removeTagKey("display");
         stack = LootInitialization.initializeVaultLoot(stack, vaultLevel);
      }

      return stack;
   }
}
