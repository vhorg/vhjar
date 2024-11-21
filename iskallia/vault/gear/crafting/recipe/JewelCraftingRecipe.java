package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.VaultMod;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class JewelCraftingRecipe extends VaultForgeRecipe {
   private ResourceLocation jewelAttribute = VaultMod.id("empty");
   private int size = 0;

   public JewelCraftingRecipe(ResourceLocation id, ItemStack output) {
      super(ForgeRecipeType.JEWEL_CRAFTING, id, output);
   }

   public JewelCraftingRecipe(ResourceLocation id, ItemStack output, List<ItemStack> inputs, ResourceLocation jewelAttribute, int size) {
      super(ForgeRecipeType.JEWEL_CRAFTING, id, output, inputs);
      this.jewelAttribute = jewelAttribute;
      this.size = size;
   }

   @Override
   public void addCraftingDisplayTooltip(ItemStack result, List<Component> out) {
      VaultGearAttribute attribute = VaultGearAttributeRegistry.getAttribute(this.jewelAttribute);
      if (attribute != null) {
         String name = attribute.getReader().getModifierName();
         out.add(new TextComponent(name).withStyle(attribute.getReader().getColoredTextStyle()));
      }
   }

   @Override
   public ItemStack getDisplayOutput(int vaultLevel) {
      return this.createJewel(vaultLevel);
   }

   @Override
   public ItemStack createOutput(List<OverSizedItemStack> consumed, ServerPlayer crafter, int vaultLevel) {
      return this.createJewel(vaultLevel);
   }

   private ItemStack createJewel(int vaultLevel) {
      ItemStack stack = this.getRawOutput();
      if (stack.is(ModItems.JEWEL)) {
         createAttributeJewel(stack, vaultLevel, this.jewelAttribute, this.size);
      }

      return stack;
   }

   public static void createAttributeJewel(ItemStack stack, int vaultLevel, ResourceLocation attributeKey, int size) {
      VaultGearAttribute attribute = VaultGearAttributeRegistry.getAttribute(attributeKey);
      if (attribute != null) {
         VaultGearData data = VaultGearData.read(stack);
         data.setState(VaultGearState.IDENTIFIED);
         data.setRarity(VaultGearRarity.COMMON);
         data.createOrReplaceAttributeValue(ModGearAttributes.SUFFIXES, Integer.valueOf(1));
         data.addModifier(VaultGearModifier.AffixType.IMPLICIT, new VaultGearModifier<>(ModGearAttributes.JEWEL_SIZE, size));
         data.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(attribute, true));
         data.setItemLevel(vaultLevel);
         data.write(stack);
      }
   }

   @Override
   protected void writeAdditional(FriendlyByteBuf buf) {
      super.writeAdditional(buf);
      buf.writeResourceLocation(this.jewelAttribute);
      buf.writeInt(this.size);
   }

   @Override
   protected void readAdditional(FriendlyByteBuf buf) {
      super.readAdditional(buf);
      this.jewelAttribute = buf.readResourceLocation();
      this.size = buf.readInt();
   }
}
