package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class RemoveModifierModification extends GearModification {
   public RemoveModifierModification() {
      super(VaultMod.id("remove_modifier"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.NULLIFYING_FOCUS);
   }

   @Override
   public boolean doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      return VaultGearModifierHelper.removeRandomModifier(stack, rand);
   }
}
