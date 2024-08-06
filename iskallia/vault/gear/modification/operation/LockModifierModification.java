package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LockModifierModification extends GearModification {
   public LockModifierModification() {
      super(VaultMod.id("lock_modifier"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.CRYONIC_FOCUS);
   }

   @Override
   public GearModification.Result doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      VaultGearRarity rarity = GearDataCache.of(stack).getRarity();
      if (rarity == null || rarity.ordinal() < VaultGearRarity.RARE.ordinal()) {
         return GearModification.Result.makeActionError("min_modifiers");
      } else {
         return VaultGearModifierHelper.hasAnyOpenAffix(stack)
            ? GearModification.Result.makeActionError("min_modifiers")
            : VaultGearModifierHelper.lockRandomAffix(stack, rand);
      }
   }
}
