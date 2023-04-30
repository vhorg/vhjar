package iskallia.vault.gear.crafting;

import iskallia.vault.config.gear.VaultGearCraftingConfig;
import iskallia.vault.config.gear.VaultGearTypeConfig;
import iskallia.vault.gear.VaultGearRarity;
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
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
               data.updateAttribute(ModGearAttributes.CRAFTING_POTENTIAL, Integer.valueOf(Math.max(potential - potentialReduction, 0)));
            } else {
               data.updateAttribute(ModGearAttributes.CRAFTING_POTENTIAL, Integer.valueOf(potential - potentialReduction));
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
      data.updateAttribute(ModGearAttributes.CRAFTED_BY, crafter.getName().getContents());
      data.updateAttribute(ModGearAttributes.GEAR_ROLL_TYPE, getCraftedRollType(stack, crafter).getName());
      data.write(stack);
      if (!simulate) {
         notifyGearCrafted(stack, crafter);
      }

      return stack;
   }

   @Nonnull
   public static VaultGearTypeConfig.RollType getCraftedRollType(ItemStack stack, ServerPlayer player) {
      VaultGearCraftingConfig cfg = ModConfigs.VAULT_GEAR_CRAFTING_CONFIG;
      if (!stack.isEmpty() && stack.getItem() instanceof VaultGearItem gearItem) {
         PlayerProficiencyData var6 = PlayerProficiencyData.get(player.getLevel());
         ProficiencyType type = gearItem.getCraftingProficiencyType(stack);
         return cfg.getRollPool(var6.getProficiency(player, type));
      } else {
         return cfg.getDefaultCraftedPool();
      }
   }

   public static void notifyGearCrafted(ItemStack stack, ServerPlayer player) {
      if (!stack.isEmpty() && stack.getItem() instanceof VaultGearItem gearItem) {
         VaultGearCraftingConfig var6 = ModConfigs.VAULT_GEAR_CRAFTING_CONFIG;
         PlayerProficiencyData proficiencyData = PlayerProficiencyData.get(player.getLevel());
         ProficiencyType type = gearItem.getCraftingProficiencyType(stack);
         proficiencyData.updateProficiency(player, type, var6.getRandomProficiencyGain());
      }
   }

   public static void reRollCraftingPotential(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearRarity rarity = data.getRarity();
      int potential = ModConfigs.VAULT_GEAR_CRAFTING_CONFIG.getNewCraftingPotential(rarity);
      potential = (int)(potential * (1.0F + ModConfigs.VAULT_GEAR_CRAFTING_CONFIG.getPotentialIncreasePerLevel() * data.getItemLevel()));
      data.updateAttribute(ModGearAttributes.CRAFTING_POTENTIAL, Integer.valueOf(potential));
      data.write(stack);
   }
}
