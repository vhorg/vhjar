package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ReforgeImplicitModification extends GearModification {
   public ReforgeImplicitModification() {
      super(VaultMod.id("reforge_implicits"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.FUNDAMENTAL_FOCUS);
   }

   @Override
   public boolean doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      VaultGearModifierHelper.reForgeAllImplicits(stack, rand);
      return true;
   }
}
