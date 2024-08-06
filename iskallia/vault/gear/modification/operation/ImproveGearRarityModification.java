package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ImproveGearRarityModification extends GearModification {
   public ImproveGearRarityModification() {
      super(VaultMod.id("improve_rarity"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.PYRETIC_FOCUS);
   }

   @Override
   public GearModification.Result doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      return VaultGearModifierHelper.improveGearRarity(stack, rand);
   }
}
