package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ReforgeAllModification extends GearModification {
   public ReforgeAllModification() {
      super(VaultMod.id("reforge_all"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.WILD_FOCUS);
   }

   @Override
   public boolean doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      VaultGearModifierHelper.reForgeAllModifiers(stack, rand);
      return true;
   }
}
