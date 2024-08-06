package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ResetPotentialModification extends GearModification {
   public ResetPotentialModification() {
      super(VaultMod.id("reset_potential"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.OPPORTUNISTIC_FOCUS);
   }

   @Override
   public GearModification.Result doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      return VaultGearCraftingHelper.reRollCraftingPotential(stack);
   }
}
