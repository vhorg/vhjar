package iskallia.vault.entity.boss.trait;

import com.google.common.collect.ImmutableMap.Builder;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.attack.PersistentMeleeAttackGoal;
import iskallia.vault.entity.boss.goal.BloodOrbGoal;
import iskallia.vault.entity.boss.goal.CobwebRangedAttackGoal;
import iskallia.vault.entity.boss.goal.EvokerFangsGoal;
import iskallia.vault.entity.boss.goal.FireballRangedAttackGoal;
import iskallia.vault.entity.boss.goal.GolemHandRangedAttackGoal;
import iskallia.vault.entity.boss.goal.HealGoal;
import iskallia.vault.entity.boss.goal.PlaceBlockAroundGoal;
import iskallia.vault.entity.boss.goal.PotionAuraGoal;
import iskallia.vault.entity.boss.goal.ShulkerAttackGoal;
import iskallia.vault.entity.boss.goal.SnowballRangedAttackGoal;
import iskallia.vault.entity.boss.goal.SummonAtTargetGoal;
import iskallia.vault.entity.boss.goal.SummonGoal;
import iskallia.vault.entity.boss.goal.ThrowPotionGoal;
import iskallia.vault.entity.entity.VaultSpiderEntity;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;

public class VaultBossTraitRegistry {
   public static final String LEAP_AT_TARGET_TYPE = "leap_at_target";
   public static final String SPIDER_ATTACK_TYPE = "spider_attack";
   private static final Map<String, VaultBossTraitRegistry.ITraitFactory> TRAIT_FACTORIES = new Builder()
      .put("melee_attack", PersistentMeleeAttackGoal::new)
      .put("fireball_ranged_attack", FireballRangedAttackGoal::new)
      .put("snowball_ranged_attack", SnowballRangedAttackGoal::new)
      .put("golem_hand_ranged_attack", GolemHandRangedAttackGoal::new)
      .put("cobweb_ranged_attack", CobwebRangedAttackGoal::new)
      .put("summon", SummonGoal::new)
      .put("summon_at_target", SummonAtTargetGoal::new)
      .put("shulker_bullet", ShulkerAttackGoal::new)
      .put("heal", HealGoal::new)
      .put("blood_orb", BloodOrbGoal::new)
      .put("evoker_fangs", EvokerFangsGoal::new)
      .put("attribute_modifier", (VaultBossTraitRegistry.ITraitFactory)boss -> new AttributeModifierTrait())
      .put("life_leech_on_hit", (VaultBossTraitRegistry.ITraitFactory)boss -> new LifeLeechOnHitEffect())
      .put("apply_potion_on_hit", (VaultBossTraitRegistry.ITraitFactory)boss -> new ApplyPotionOnHitEffect())
      .put("throw_potion", ThrowPotionGoal::new)
      .put(PotionAuraGoal.TYPE, (VaultBossTraitRegistry.ITraitFactory)boss -> {
         boss.noCulling = true;
         return new PotionAuraGoal(boss);
      })
      .put(
         "leap_at_target",
         (VaultBossTraitRegistry.ITraitFactory)boss -> new SimpleTrait("leap_at_target", b -> b.addTraitGoal(new LeapAtTargetGoal(boss, 0.4F)))
      )
      .put(
         "spider_attack",
         (VaultBossTraitRegistry.ITraitFactory)boss -> new SimpleTrait("spider_attack", b -> b.addTraitGoal(new VaultSpiderEntity.SpiderAttackGoal(boss)))
      )
      .put("place_block_around", PlaceBlockAroundGoal::new)
      .build();

   public static Optional<ITrait> createTrait(String type, VaultBossEntity boss, CompoundTag attributesNbt) {
      if (!TRAIT_FACTORIES.containsKey(type)) {
         return Optional.empty();
      } else {
         ITrait trait = TRAIT_FACTORIES.get(type).create(boss);
         trait.deserializeNBT(attributesNbt, boss);
         return Optional.of(trait);
      }
   }

   public interface ITraitFactory {
      ITrait create(VaultBossEntity var1);
   }
}
