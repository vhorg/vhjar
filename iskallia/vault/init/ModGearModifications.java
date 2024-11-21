package iskallia.vault.init;

import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.operation.AddModifierModification;
import iskallia.vault.gear.modification.operation.CorruptGearModification;
import iskallia.vault.gear.modification.operation.ImproveGearRarityModification;
import iskallia.vault.gear.modification.operation.ImproveRandomModifierModification;
import iskallia.vault.gear.modification.operation.LockModifierModification;
import iskallia.vault.gear.modification.operation.ReforgeAddTaggedModification;
import iskallia.vault.gear.modification.operation.ReforgeAffixGroupModification;
import iskallia.vault.gear.modification.operation.ReforgeAllModification;
import iskallia.vault.gear.modification.operation.ReforgeImplicitModification;
import iskallia.vault.gear.modification.operation.ReforgeRandomTierModification;
import iskallia.vault.gear.modification.operation.ReforgeResilientBaseAttributes;
import iskallia.vault.gear.modification.operation.RemoveModifierModification;
import iskallia.vault.gear.modification.operation.ResetPotentialModification;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModGearModifications {
   public static final ReforgeAllModification REFORGE_ALL_MODIFIERS = new ReforgeAllModification();
   public static final AddModifierModification ADD_MODIFIER = new AddModifierModification();
   public static final RemoveModifierModification REMOVE_MODIFIER = new RemoveModifierModification();
   public static final ResetPotentialModification RESET_POTENTIAL = new ResetPotentialModification();
   public static final ReforgeResilientBaseAttributes REFORGE_BASE_ATTRIBUTES = new ReforgeResilientBaseAttributes();
   public static final ReforgeImplicitModification REFORGE_ALL_IMPLICITS = new ReforgeImplicitModification();
   public static final ReforgeAddTaggedModification REFORGE_ALL_ADD_TAG = new ReforgeAddTaggedModification();
   public static final ReforgeRandomTierModification REFORGE_RANDOM_TIER = new ReforgeRandomTierModification();
   public static final ReforgeAffixGroupModification REFORGE_PREFIXES = new ReforgeAffixGroupModification(VaultGearModifier.AffixType.PREFIX);
   public static final ReforgeAffixGroupModification REFORGE_SUFFIXES = new ReforgeAffixGroupModification(VaultGearModifier.AffixType.SUFFIX);
   public static final ImproveRandomModifierModification IMPROVE_MODIFIER = new ImproveRandomModifierModification();
   public static final LockModifierModification LOCK_MODIFIER = new LockModifierModification();
   public static final ImproveGearRarityModification IMPROVE_RARITY = new ImproveGearRarityModification();
   public static final CorruptGearModification CORRUPT_GEAR = new CorruptGearModification();

   public static void init(Register<GearModification> event) {
      IForgeRegistry<GearModification> registry = event.getRegistry();
      registry.register(REFORGE_ALL_MODIFIERS);
      registry.register(ADD_MODIFIER);
      registry.register(REMOVE_MODIFIER);
      registry.register(RESET_POTENTIAL);
      registry.register(REFORGE_BASE_ATTRIBUTES);
      registry.register(REFORGE_ALL_IMPLICITS);
      registry.register(REFORGE_ALL_ADD_TAG);
      registry.register(REFORGE_RANDOM_TIER);
      registry.register(REFORGE_PREFIXES);
      registry.register(REFORGE_SUFFIXES);
      registry.register(IMPROVE_MODIFIER);
      registry.register(LOCK_MODIFIER);
      registry.register(IMPROVE_RARITY);
      registry.register(CORRUPT_GEAR);
   }
}
