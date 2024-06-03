package iskallia.vault.item.crystal.recipe;

import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.item.tool.ToolMaterial;
import iskallia.vault.item.tool.ToolType;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;

public class JewelAnvilRecipe extends VanillaAnvilRecipe {
   public static List<VaultGearModifier<?>> PRESET_MODIFIERS = new ArrayList<>();

   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() == ModItems.TOOL && secondary.getItem() == ModItems.JEWEL) {
         ItemStack output = primary.copy();
         if (!ToolItem.applyJewel(output, secondary)) {
            return false;
         } else {
            context.setOutput(output);
            context.onTake(context.getTake().append(() -> {
               context.getInput()[0].shrink(1);
               context.getInput()[1].shrink(1);
            }));
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
      IVanillaRecipeFactory factory = registry.getVanillaRecipeFactory();

      for (VaultGearModifier<?> modifier : PRESET_MODIFIERS) {
         List<ItemStack> primary = new ArrayList<>();
         List<ItemStack> secondary = new ArrayList<>();
         List<ItemStack> output = new ArrayList<>();
         ItemStack jewel = JewelItem.create(data -> {
            data.setRarity(VaultGearRarity.UNIQUE);
            data.setState(VaultGearState.IDENTIFIED);
            data.addModifier(VaultGearModifier.AffixType.IMPLICIT, new VaultGearModifier<>(ModGearAttributes.JEWEL_SIZE, 10));
            data.addModifier(VaultGearModifier.AffixType.SUFFIX, modifier);
         });

         for (ToolType type : ToolType.values()) {
            for (ToolMaterial material : ToolMaterial.values()) {
               ItemStack tool = ToolItem.create(material, type);
               ItemStack result = tool.copy();
               if (ToolItem.applyJewel(result, jewel) && ToolType.of(tool) != ToolType.of(result)) {
                  primary.add(tool);
                  output.add(result);
               }
            }
         }

         secondary.add(jewel);
         if (!primary.isEmpty()) {
            registry.addRecipes(RecipeTypes.ANVIL, List.of(factory.createAnvilRecipe(primary, secondary, output)));
         }
      }
   }

   static {
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.PICKING, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.AXING, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.SHOVELLING, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.WOODEN_AFFINITY, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ORNATE_AFFINITY, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.GILDED_AFFINITY, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.LIVING_AFFINITY, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.COIN_AFFINITY, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.SMELTING, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.PULVERIZING, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.HYDROVOID, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.SOULBOUND, true));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.MINING_SPEED, 0.1F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.MINING_SPEED, 1.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.MINING_SPEED, 10.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.DURABILITY, 1));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.DURABILITY, 10));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.DURABILITY, 100));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.DURABILITY, 1000));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.DURABILITY, 10000));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.COPIOUSLY, 0.001F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.COPIOUSLY, 0.01F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.COPIOUSLY, 0.1F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.COPIOUSLY, 1.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.COPIOUSLY, 10.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.COPIOUSLY, 100.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_QUANTITY, 0.001F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_QUANTITY, 0.01F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_QUANTITY, 0.1F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_QUANTITY, 1.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_QUANTITY, 10.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_QUANTITY, 100.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_RARITY, 0.001F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_RARITY, 0.01F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_RARITY, 0.1F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_RARITY, 1.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_RARITY, 10.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.ITEM_RARITY, 100.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.TRAP_DISARMING, 0.001F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.TRAP_DISARMING, 0.01F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.TRAP_DISARMING, 0.1F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.TRAP_DISARMING, 1.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.TRAP_DISARMING, 10.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.TRAP_DISARMING, 100.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.IMMORTALITY, 0.001F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.IMMORTALITY, 0.01F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.IMMORTALITY, 0.1F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.IMMORTALITY, 1.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.IMMORTALITY, 10.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.IMMORTALITY, 100.0F));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.REACH, 0.01));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.REACH, 0.1));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.REACH, 1.0));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.REACH, 10.0));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.HAMMER_SIZE, 1));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.HAMMER_SIZE, 2));
      PRESET_MODIFIERS.add(new VaultGearModifier<>(ModGearAttributes.HAMMER_SIZE, 5));
   }
}
