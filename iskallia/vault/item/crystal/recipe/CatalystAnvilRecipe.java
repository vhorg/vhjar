package iskallia.vault.item.crystal.recipe;

import iskallia.vault.VaultMod;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.InfusedCatalystItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.properties.CapacityCrystalProperties;
import iskallia.vault.item.crystal.properties.InstabilityCrystalProperties;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.InfuserExpertise;
import iskallia.vault.world.data.PlayerExpertisesData;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AnvilBlock;

public class CatalystAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      if (context.getBlockState().map(state -> state.getBlock() instanceof AnvilBlock).orElse(false)) {
         return false;
      } else {
         ItemStack primary = context.getInput()[0];
         ItemStack secondary = context.getInput()[1];
         if (primary.getItem() == ModItems.VAULT_CRYSTAL && secondary.getItem() == ModItems.VAULT_CATALYST_INFUSED) {
            ItemStack output = primary.copy();
            CrystalData crystal = CrystalData.read(output);
            Integer size = InfusedCatalystItem.getSize(secondary).orElse(null);
            if (size == null) {
               return false;
            } else {
               if (crystal.getProperties() instanceof CapacityCrystalProperties properties) {
                  Integer capacity = properties.getCapacity().orElse(null);
                  Integer level = properties.getLevel().orElse(null);
                  if (capacity == null || level == null) {
                     return false;
                  }

                  if (capacity < size) {
                     ModConfigs.VAULT_MODIFIER_POOLS
                        .getRandom(VaultMod.id("catalyst_curse"), level, JavaRandom.ofNanoTime())
                        .forEach(modifierx -> crystal.getModifiers().add(VaultModifierStack.of(modifierx)));
                     crystal.write(output);
                  }

                  properties.setSize(properties.getSize() + size);
               }

               List<VaultModifierStack> modifiers = InfusedCatalystItem.getModifiers(secondary)
                  .stream()
                  .map(VaultModifierRegistry::getOpt)
                  .flatMap(Optional::stream)
                  .map(VaultModifierStack::of)
                  .toList();
               if (!crystal.getModifiers().addByCrafting(crystal, modifiers, false)) {
                  return false;
               } else {
                  Random random = new Random();
                  if (shouldRemoveRandomModifier(context.getPlayer().orElse(null), modifiers, random)) {
                     Iterator<VaultModifierStack> iterator = crystal.getModifiers().getList().iterator();

                     while (iterator.hasNext()) {
                        VaultModifierStack modifier = iterator.next();
                        if (modifier.getModifier().getId().equals(VaultCrystalItem.NEGATIVE_MODIFIER_POOL_NAME)) {
                           modifier.shrink(1);
                           if (modifier.isEmpty()) {
                              iterator.remove();
                           }
                        }
                     }
                  }

                  if (crystal.getProperties() instanceof InstabilityCrystalProperties properties
                     && random.nextFloat() < properties.getInstability()
                     && random.nextDouble() > getInstabilityAvoidanceChance(context.getPlayer().orElse(null))) {
                     if (random.nextFloat() < ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.exhaustProbability) {
                        VaultCrystalItem.scheduleTask(VaultCrystalItem.ExhaustTask.INSTANCE, output);
                     } else {
                        VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddModifiersTask(VaultMod.id("catalyst_curse"), 1), output);
                     }
                  }

                  crystal.write(output);
                  context.setOutput(output);
                  context.onTake(context.getTake().append(() -> {
                     context.getInput()[0].shrink(1);
                     context.getInput()[1].shrink(1);
                  }));
                  return true;
               }
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
   }

   private static boolean shouldRemoveRandomModifier(Player player, List<VaultModifierStack> modifierStackList, Random random) {
      double negativeModifierRemovalChance = getNegativeModifierRemovalChance(player);
      return random.nextDouble() < negativeModifierRemovalChance
         && modifierStackList.stream().anyMatch(m -> m.getModifier().getId().equals(VaultCrystalItem.NEGATIVE_MODIFIER_POOL_NAME));
   }

   private static double getInstabilityAvoidanceChance(Player player) {
      return 0.0;
   }

   private static double getNegativeModifierRemovalChance(Player player) {
      double instabilityAvoidanceChance = 0.0;
      if (player instanceof ServerPlayer serverPlayer) {
         instabilityAvoidanceChance = PlayerExpertisesData.get(serverPlayer.getLevel())
            .getExpertises(serverPlayer)
            .getAll(InfuserExpertise.class, Skill::isUnlocked)
            .stream()
            .mapToDouble(InfuserExpertise::getNegativeModifierRemovalChance)
            .sum();
      }

      return instabilityAvoidanceChance;
   }
}
