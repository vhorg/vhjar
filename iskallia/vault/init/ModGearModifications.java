package iskallia.vault.init;

import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.operation.AddModifierModification;
import iskallia.vault.gear.modification.operation.ReforgeAddTaggedModification;
import iskallia.vault.gear.modification.operation.ReforgeAffixGroupModification;
import iskallia.vault.gear.modification.operation.ReforgeAllModification;
import iskallia.vault.gear.modification.operation.ReforgeImplicitModification;
import iskallia.vault.gear.modification.operation.ReforgeRandomTierModification;
import iskallia.vault.gear.modification.operation.ReforgeRepairSlotsModification;
import iskallia.vault.gear.modification.operation.RemoveModifierModification;
import iskallia.vault.gear.modification.operation.ResetPotentialModification;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModGearModifications {
   public static final ReforgeAllModification REFORGE_ALL_MODIFIERS = new ReforgeAllModification();
   public static final AddModifierModification ADD_MODIFIER = new AddModifierModification();
   public static final RemoveModifierModification REMOVE_MODIFIER = new RemoveModifierModification();
   public static final ResetPotentialModification RESET_POTENTIAL = new ResetPotentialModification();
   public static final ReforgeRepairSlotsModification REFORGE_REPAIR_SLOTS = new ReforgeRepairSlotsModification();
   public static final ReforgeImplicitModification REFORGE_ALL_IMPLICITS = new ReforgeImplicitModification();
   public static final ReforgeAddTaggedModification REFORGE_ALL_ADD_TAG = new ReforgeAddTaggedModification();
   public static final ReforgeRandomTierModification REFORGE_RANDOM_TIER = new ReforgeRandomTierModification();
   public static final ReforgeAffixGroupModification REFORGE_PREFIXES = new ReforgeAffixGroupModification(VaultGearModifier.AffixType.PREFIX);
   public static final ReforgeAffixGroupModification REFORGE_SUFFIXES = new ReforgeAffixGroupModification(VaultGearModifier.AffixType.SUFFIX);

   public static void init(Register<GearModification> event) {
      IForgeRegistry<GearModification> registry = event.getRegistry();
      registry.register(REFORGE_ALL_MODIFIERS);
      registry.register(ADD_MODIFIER);
      registry.register(REMOVE_MODIFIER);
      registry.register(RESET_POTENTIAL);
      registry.register(REFORGE_REPAIR_SLOTS);
      registry.register(REFORGE_ALL_IMPLICITS);
      registry.register(REFORGE_ALL_ADD_TAG);
      registry.register(REFORGE_RANDOM_TIER);
      registry.register(REFORGE_PREFIXES);
      registry.register(REFORGE_SUFFIXES);
   }
}
