package iskallia.vault.gear.crafting;

import iskallia.vault.config.gear.VaultGearCraftingConfig;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.config.gear.VaultGearTypeConfig;
import iskallia.vault.gear.VaultGearLegendaryHelper;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.ArtisanExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerProficiencyData;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

public class VaultGearCraftingHelper {
   private static final Random rand = new Random();

   public static void reducePotential(ItemStack stack, Player player, GearModification action) {
      if (!stack.isEmpty() && stack.getItem() instanceof VaultGearItem) {
         float chance = 0.0F;
         ExpertiseTree expertises = PlayerExpertisesData.get((ServerLevel)player.level).getExpertises(player);

         for (ArtisanExpertise expertise : expertises.getAll(ArtisanExpertise.class, Skill::isUnlocked)) {
            chance += expertise.getChanceToNotConsumePotential();
         }

         if (!(rand.nextFloat() < chance)) {
            VaultGearData data = VaultGearData.read(stack);
            int potentialReduction = ModConfigs.VAULT_GEAR_MODIFICATION_CONFIG.getPotentialUsed(action);
            int potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL).orElse(0);
            if (potential > 0) {
               data.createOrReplaceAttributeValue(ModGearAttributes.CRAFTING_POTENTIAL, Integer.valueOf(Math.max(potential - potentialReduction, 0)));
            } else {
               data.createOrReplaceAttributeValue(ModGearAttributes.CRAFTING_POTENTIAL, Integer.valueOf(potential - potentialReduction));
            }

            data.write(stack);
         }
      }
   }

   @Nonnull
   public static <T extends IForgeItem & VaultGearItem> ItemStack doCraftGear(T item, ServerPlayer crafter, int level, boolean simulate) {
      ItemStack stack = new ItemStack(item.getItem());
      VaultGearData data = VaultGearData.read(stack);
      data.setItemLevel(level);
      data.createOrReplaceAttributeValue(ModGearAttributes.CRAFTED_BY, crafter.getName().getContents());
      data.createOrReplaceAttributeValue(ModGearAttributes.GEAR_ROLL_TYPE, getCraftedRollType(stack, crafter, level).getName());
      data.write(stack);
      item.instantIdentify(simulate ? null : crafter, stack);
      int proficiency = PlayerProficiencyData.get(crafter.getLevel()).getAbsoluteProficiency(crafter);
      float proficiencyDegree = VaultGearCraftingConfig.calculateRelativeProficiency(proficiency, level);
      ModConfigs.VAULT_GEAR_CRAFTING_CONFIG.getProficiencyStep(proficiencyDegree).ifPresent(step -> {
         VaultGearData gearData = VaultGearData.read(stack);
         int potential = gearData.get(ModGearAttributes.MAX_CRAFTING_POTENTIAL, VaultGearAttributeTypeMerger.intSum());
         potential = Mth.ceil(potential * step.getCraftingPotentialMultiplier());
         gearData.createOrReplaceAttributeValue(ModGearAttributes.MAX_CRAFTING_POTENTIAL, Integer.valueOf(potential));
         gearData.createOrReplaceAttributeValue(ModGearAttributes.CRAFTING_POTENTIAL, Integer.valueOf(potential));
         gearData.setRepairSlots(Math.min(gearData.getRepairSlots(), step.getMaximumRepairSlots()));
         gearData.write(stack);
         step.getGearRollOutcomeModifiers().forEach(outcome -> outcome.apply(stack).ifPresent(generatedMod -> {
            VaultGearModifier<?> modifier = generatedMod.getModifier();
            gearData.getModifiers(modifier.getModifierIdentifier()).findFirst().ifPresent(existing -> {
               if (gearData.removeModifier((VaultGearModifier<?>)existing)) {
                  generatedMod.applyModifier(gearData);
               }
            });
            gearData.write(stack);
         }));
         if (step.getSoulboundModifierId() != null) {
            ResourceLocation soulboundModifierId = step.getSoulboundModifierId();
            VaultGearTierConfig.getConfig(stack).ifPresent(cfg -> {
               VaultGearTierConfig.ModifierTierGroup group = cfg.getTierGroup(soulboundModifierId);
               if (group != null) {
                  gearData.getModifiers(soulboundModifierId).toList().forEach(gearData::removeModifier);
                  if (rand.nextFloat() <= step.getSoulboundChance()) {
                     VaultGearModifier<?> generatedSoulbound = cfg.generateModifier(soulboundModifierId, level, rand);
                     VaultGearTierConfig.ModifierAffixTagGroup targetGroup = group.getTargetAffixTagGroup();
                     if (targetGroup != null) {
                        targetGroup.addModifier(gearData, generatedSoulbound);
                     }
                  }

                  gearData.write(stack);
               }
            });
         }

         if (rand.nextFloat() <= step.getGreaterModifierChance()) {
            VaultGearLegendaryHelper.generateImprovedModifier(stack, 1, rand, List.of(VaultGearModifier.AffixCategory.GREATER));
         }
      });
      return stack;
   }

   @Nonnull
   public static VaultGearTypeConfig.RollType getCraftedRollType(ItemStack stack, ServerPlayer player, int level) {
      VaultGearCraftingConfig cfg = ModConfigs.VAULT_GEAR_CRAFTING_CONFIG;
      if (!stack.isEmpty() && stack.getItem() instanceof VaultGearItem) {
         PlayerProficiencyData proficiencyData = PlayerProficiencyData.get(player.getLevel());
         int absProficiency = proficiencyData.getAbsoluteProficiency(player);
         float relProficiency = VaultGearCraftingConfig.calculateRelativeProficiency(absProficiency, level);
         return cfg.getProficiencyStep(relProficiency)
            .map(VaultGearCraftingConfig.ProficiencyStep::getPool)
            .flatMap(ModConfigs.VAULT_GEAR_TYPE_CONFIG::getRollPool)
            .orElse(cfg.getDefaultCraftedPool());
      } else {
         return cfg.getDefaultCraftedPool();
      }
   }

   public static GearModification.Result generateCraftingPotential(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         VaultGearRarity rarity = data.getRarity();
         String rollType = data.get(ModGearAttributes.GEAR_ROLL_TYPE, VaultGearAttributeTypeMerger.firstNonNull());
         int potential = ModConfigs.VAULT_GEAR_COMMON.getNewCraftingPotential(rarity, rollType);
         data.createOrReplaceAttributeValue(ModGearAttributes.MAX_CRAFTING_POTENTIAL, Integer.valueOf(potential));
         data.write(stack);
         GearModification.Result resetResult = refreshCraftingPotential(stack);
         return !resetResult.success() ? resetResult : GearModification.Result.makeSuccess();
      }
   }

   public static GearModification.Result refreshCraftingPotential(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         if (!data.hasAttribute(ModGearAttributes.MAX_CRAFTING_POTENTIAL)) {
            GearModification.Result genResult = generateCraftingPotential(stack);
            if (!genResult.success()) {
               return genResult;
            }
         }

         int potential = data.get(ModGearAttributes.MAX_CRAFTING_POTENTIAL, VaultGearAttributeTypeMerger.intSum());
         data.createOrReplaceAttributeValue(ModGearAttributes.CRAFTING_POTENTIAL, Integer.valueOf(potential));
         data.write(stack);
         return GearModification.Result.makeSuccess();
      }
   }
}
