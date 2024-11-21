package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.InfusedCatalystItem;
import iskallia.vault.util.LootInitialization;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CatalystForgeRecipe extends VaultForgeRecipe {
   public CatalystForgeRecipe(ResourceLocation id, ItemStack output) {
      super(ForgeRecipeType.CATALYST, id, output);
   }

   public CatalystForgeRecipe(ResourceLocation id, ItemStack output, List<ItemStack> inputs) {
      super(ForgeRecipeType.CATALYST, id, output, inputs);
   }

   @Override
   public ItemStack createOutput(List<OverSizedItemStack> consumed, ServerPlayer crafter, int vaultLevel) {
      ItemStack stack = super.createOutput(consumed, crafter, vaultLevel);
      if (stack.is(ModItems.VAULT_CATALYST_INFUSED)) {
         stack.removeTagKey("display");
         stack.removeTagKey("size");
         stack.removeTagKey("modifiers");
         stack = LootInitialization.initializeVaultLoot(stack, vaultLevel);
      }

      return stack;
   }

   @Override
   public void addCraftingDisplayTooltip(ItemStack result, List<Component> out) {
      List<ResourceLocation> modifiers = InfusedCatalystItem.getModifiers(result);
      Tag size = result.getTag() == null ? null : result.getOrCreateTag().get("size");
      if (size instanceof NumericTag numeric) {
         out.add(new TextComponent("Size: ").append(new TextComponent(String.valueOf(numeric.getAsInt())).withStyle(Style.EMPTY.withColor(14540253))));
      } else if (size instanceof CollectionTag<?> collection && collection.size() == 2) {
         Tag e1 = (Tag)collection.get(0);
         Tag e2 = (Tag)collection.get(1);
         if (e1 instanceof NumericTag n1 && e2 instanceof NumericTag n2) {
            out.add(
               new TextComponent("Size: ")
                  .append(new TextComponent(String.valueOf(n1.getAsInt())).withStyle(Style.EMPTY.withColor(14540253)))
                  .append(new TextComponent(" - "))
                  .append(new TextComponent(String.valueOf(n2.getAsInt())).withStyle(Style.EMPTY.withColor(14540253)))
            );
         }
      } else {
         out.add(new TextComponent("Size: ???").withStyle(ChatFormatting.GRAY));
      }

      for (ResourceLocation modifier : modifiers) {
         VaultModifierRegistry.getOpt(modifier).ifPresent(vaultModifier -> {
            out.add(new TextComponent(" • " + vaultModifier.getDisplayName()).withStyle(Style.EMPTY.withColor(vaultModifier.getDisplayTextColor())));
            if (Screen.hasShiftDown()) {
               out.add(new TextComponent("    " + vaultModifier.getDisplayDescription()).withStyle(ChatFormatting.DARK_GRAY));
            }
         });
      }

      out.add(TextComponent.EMPTY);
   }
}
