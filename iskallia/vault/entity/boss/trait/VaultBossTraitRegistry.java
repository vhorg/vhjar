package iskallia.vault.entity.boss.trait;

import com.google.common.collect.ImmutableMap.Builder;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.attack.PersistentMeleeAttackGoal;
import iskallia.vault.entity.boss.goal.BloodOrbGoal;
import iskallia.vault.entity.boss.goal.EvokerFangsGoal;
import iskallia.vault.entity.boss.goal.FireballRangedAttackGoal;
import iskallia.vault.entity.boss.goal.HealGoal;
import iskallia.vault.entity.boss.goal.PotionAuraGoal;
import iskallia.vault.entity.boss.goal.ShulkerAttackGoal;
import iskallia.vault.entity.boss.goal.SummoningGoal;
import iskallia.vault.entity.boss.goal.ThrowPotionGoal;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class VaultBossTraitRegistry {
   private static final Map<String, VaultBossTraitRegistry.ITraitFactory> TRAIT_FACTORIES = new Builder()
      .put("melee_attack", PersistentMeleeAttackGoal::new)
      .put("fireball_ranged_attack", FireballRangedAttackGoal::new)
      .put("summoning", SummoningGoal::new)
      .put("shulker_bullet", ShulkerAttackGoal::new)
      .put("heal", HealGoal::new)
      .put("blood_orb", BloodOrbGoal::new)
      .put("evoker_fangs", EvokerFangsGoal::new)
      .put("attribute_modifier", (VaultBossTraitRegistry.ITraitFactory)boss -> new AttributeModifierTrait())
      .put("life_leech_on_hit", (VaultBossTraitRegistry.ITraitFactory)boss -> new LifeLeechOnHitEffect())
      .put("apply_potion_on_hit", (VaultBossTraitRegistry.ITraitFactory)boss -> new ApplyPotionOnHitEffect())
      .put("throw_potion", ThrowPotionGoal::new)
      .put(PotionAuraGoal.TYPE, PotionAuraGoal::new)
      .build();

   public static Optional<ITrait> createTrait(String type, VaultBossEntity vaultBossEntity, CompoundTag attributesNbt) {
      if (!TRAIT_FACTORIES.containsKey(type)) {
         return Optional.empty();
      } else {
         ITrait trait = TRAIT_FACTORIES.get(type).create(vaultBossEntity);
         trait.deserializeNBT(attributesNbt);
         return Optional.of(trait);
      }
   }

   public interface ITraitFactory {
      ITrait create(VaultBossEntity var1);
   }
}
