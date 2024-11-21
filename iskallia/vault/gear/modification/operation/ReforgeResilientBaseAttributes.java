package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ReforgeResilientBaseAttributes extends GearModification {
   public ReforgeResilientBaseAttributes() {
      super(VaultMod.id("reforge_base_durability"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.RESILIENT_FOCUS);
   }

   @Override
   public GearModification.Result doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      return VaultGearModifierHelper.reForgeAllModifiersTagged(stack, rand, "resilientFocusTarget");
   }
}
