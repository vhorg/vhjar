package iskallia.vault.gear.attribute.ability.special.base;

import iskallia.vault.gear.attribute.ability.special.DashVelocityModification;
import iskallia.vault.gear.attribute.ability.special.EternalsSpeedModification;
import iskallia.vault.gear.attribute.ability.special.ExecuteHealthModification;
import iskallia.vault.gear.attribute.ability.special.FarmerAdditionalRangeModification;
import iskallia.vault.gear.attribute.ability.special.GhostWalkDurationModification;
import iskallia.vault.gear.attribute.ability.special.HealAdditionalHealthModification;
import iskallia.vault.gear.attribute.ability.special.HunterRangeModification;
import iskallia.vault.gear.attribute.ability.special.ManaShieldAbsorptionModification;
import iskallia.vault.gear.attribute.ability.special.MegaJumpVelocityModification;
import iskallia.vault.gear.attribute.ability.special.NovaRadiusModification;
import iskallia.vault.gear.attribute.ability.special.RampageDamageModification;
import iskallia.vault.gear.attribute.ability.special.TankImmunityModification;
import iskallia.vault.gear.attribute.ability.special.TauntRadiusModification;
import iskallia.vault.gear.attribute.ability.special.VeinMinerAdditionalBlocksModification;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class SpecialAbilityModificationRegistry {
   private static final Map<ResourceLocation, SpecialAbilityModification<?>> specialModifications = new HashMap<>();

   public static <T extends SpecialAbilityModification<?>> T getAbilityModification(ResourceLocation key) {
      T modification = (T)specialModifications.get(key);
      if (modification == null) {
         throw new IllegalArgumentException("Unknown special modification: " + key);
      } else {
         return modification;
      }
   }

   public static void init() {
      register(new DashVelocityModification());
      register(new ExecuteHealthModification());
      register(new FarmerAdditionalRangeModification());
      register(new GhostWalkDurationModification());
      register(new HealAdditionalHealthModification());
      register(new HunterRangeModification());
      register(new ManaShieldAbsorptionModification());
      register(new MegaJumpVelocityModification());
      register(new NovaRadiusModification());
      register(new RampageDamageModification());
      register(new TankImmunityModification());
      register(new TauntRadiusModification());
      register(new VeinMinerAdditionalBlocksModification());
      register(new EternalsSpeedModification());
   }

   private static void register(SpecialAbilityModification<?> modification) {
      specialModifications.put(modification.getKey(), modification);
   }
}
