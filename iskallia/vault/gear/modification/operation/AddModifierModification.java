package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AddModifierModification extends GearModification {
   public AddModifierModification() {
      super(VaultMod.id("add_modifier"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.AMPLIFYING_FOCUS);
   }

   @Override
   public boolean doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      return VaultGearModifierHelper.addNewModifier(stack, player.getLevel().getGameTime(), rand);
   }
}
