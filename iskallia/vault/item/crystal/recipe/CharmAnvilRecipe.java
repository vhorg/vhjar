package iskallia.vault.item.crystal.recipe;

import iskallia.vault.VaultMod;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.properties.CapacityCrystalProperties;
import iskallia.vault.item.gear.CharmItem;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;

public class CharmAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() == ModItems.VAULT_CRYSTAL && secondary.getItem() instanceof CharmItem) {
         ItemStack output = primary.copy();
         CrystalData crystal = CrystalData.read(output);
         float value = CharmItem.getValue(secondary);
         float use = CharmItem.getUsePercentage(secondary);
         VaultGod god = CharmItem.getCharm(secondary).map(effect -> effect.getCharmConfig().getGod()).orElse(null);
         Integer level = crystal.getProperties().getLevel().orElse(null);
         if (!crystal.getProperties().isUnmodifiable() && level != null && !(value <= 0.0F) && god != null) {
            int size = CharmItem.getCrystalIngredientSize();
            if (crystal.getProperties() instanceof CapacityCrystalProperties properties) {
               properties.getCapacity()
                  .ifPresent(
                     capacity -> {
                        if (capacity < size) {
                           crystal.getProperties()
                              .getLevel()
                              .ifPresent(
                                 crystaLevel -> {
                                    ModConfigs.VAULT_MODIFIER_POOLS
                                       .getRandom(VaultMod.id("catalyst_curse"), crystaLevel, JavaRandom.ofNanoTime())
                                       .forEach(modifierx -> crystal.getModifiers().add(VaultModifierStack.of(modifierx)));
                                    crystal.write(output);
                                 }
                              );
                           properties.setSize(properties.getSize() + capacity);
                        } else {
                           properties.setSize(properties.getSize() + size);
                        }
                     }
                  );
            }

            int stacks = Math.round(value * use * 100.0F);

            for (int i = 0; i < stacks; i++) {
               for (VaultModifier<?> modifier : ModConfigs.VAULT_MODIFIER_POOLS
                  .getRandom(VaultMod.id(god.getSerializedName() + "_charm_stack"), level, JavaRandom.ofNanoTime())) {
                  crystal.getModifiers().add(VaultModifierStack.of(modifier));
               }
            }

            crystal.getModifiers().setRandomModifiers(false);
            crystal.write(output);
            context.setOutput(output);
            context.onTake(context.getTake().append(() -> {
               context.getInput()[0].shrink(1);
               context.getInput()[1].shrink(1);
            }));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
   }
}
