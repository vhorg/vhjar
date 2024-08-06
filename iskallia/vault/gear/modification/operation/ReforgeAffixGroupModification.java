package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import java.util.Locale;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ReforgeAffixGroupModification extends GearModification {
   private final VaultGearModifier.AffixType affixType;

   public ReforgeAffixGroupModification(VaultGearModifier.AffixType affixType) {
      super(VaultMod.id("reforge_affix_" + affixType.name().toLowerCase(Locale.ROOT)));
      this.affixType = affixType;
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(this.affixType == VaultGearModifier.AffixType.PREFIX ? ModItems.WAXING_FOCUS : ModItems.WANING_FOCUS);
   }

   @Override
   public GearModification.Result doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      GearModification.Result result = VaultGearModifierHelper.removeAllModifiersOfType(stack, this.affixType);
      return !result.success() ? result : VaultGearModifierHelper.generateModifiersOfAffix(stack, this.affixType, rand);
   }
}
