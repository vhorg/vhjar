package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ImproveRandomModifierModification extends GearModification {
   public ImproveRandomModifierModification() {
      super(VaultMod.id("improve_tier"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.EMPOWERED_CHAOTIC_FOCUS);
   }

   @Override
   public GearModification.Result doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      return VaultGearModifierHelper.improveRandomModifier(stack, player.getLevel().getGameTime(), rand);
   }
}
