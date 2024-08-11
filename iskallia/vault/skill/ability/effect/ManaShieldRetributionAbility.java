package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.event.ActiveFlagsCheck;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.util.damage.AttackScaleHelper;
import iskallia.vault.util.damage.CritHelper;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ManaShieldRetributionAbility extends ManaShieldAbility {
   private float damageRadius;
   private float percentageDamageDealt;
   private static final Map<UUID, ManaShieldRetributionAbility.FloatContainer> DAMAGE_POOL_MAP = new HashMap<>();
   private static final Predicate<Entity> ENTITY_SELECTION_FILTER = entity -> !(entity instanceof Player)
      && entity instanceof LivingEntity livingEntity
      && livingEntity.isAlive();

   public ManaShieldRetributionAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      float percentageDamageAbsorbed,
      float manaPerDamageScalar,
      float damageRadius,
      float percentageDamageDealt
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond, percentageDamageAbsorbed, manaPerDamageScalar);
      this.damageRadius = damageRadius;
      this.percentageDamageDealt = percentageDamageDealt;
   }

   public ManaShieldRetributionAbility() {
   }

   public float getUnmodifiedDamageRadius() {
      return this.damageRadius;
   }

   public float getRadius(Entity attacker) {
      float realRadius = this.getUnmodifiedDamageRadius();
      if (attacker instanceof LivingEntity livingEntity) {
         realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, realRadius);
      }

      return realRadius;
   }

   public float getPercentageDamageDealt() {
      return this.percentageDamageDealt;
   }

   @Override
   protected ToggleAbilityEffect getEffect() {
      return ModEffects.MANA_SHIELD_RETRIBUTION;
   }

   @Override
   protected void onDamageAbsorbed(ServerPlayer player, float amount) {
      DAMAGE_POOL_MAP.computeIfAbsent(player.getUUID(), uuid -> new ManaShieldRetributionAbility.FloatContainer()).value += amount;
   }

   @Override
   protected void onEffectRemoved(ServerPlayer player) {
      resetDamageAbsorbed(player);
   }

   private static float getDamageAbsorbed(ServerPlayer player) {
      return DAMAGE_POOL_MAP.getOrDefault(player.getUUID(), ManaShieldRetributionAbility.FloatContainer.DEFAULT).value;
   }

   private static void resetDamageAbsorbed(ServerPlayer player) {
      DAMAGE_POOL_MAP.remove(player.getUUID());
   }

   @SubscribeEvent
   public static void on(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer
         && serverPlayer.hasEffect(ModEffects.MANA_SHIELD_RETRIBUTION)
         && !(event.getSource() instanceof ThornsReflectDamageSource)) {
         float damageAbsorbed = getDamageAbsorbed(serverPlayer);
         if (!Mth.equal(damageAbsorbed, 0.0F)) {
            if (!ActiveFlagsCheck.isAnyFlagActiveLuckyHit()) {
               if (!(event.getSource() instanceof ThornsReflectDamageSource)) {
                  if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
                     if (!CritHelper.getCrit(attacker)) {
                        if (!(AttackScaleHelper.getLastAttackScale(attacker) < 1.0F)) {
                           AbilityTree abilities = PlayerAbilitiesData.get(serverPlayer.getLevel()).getAbilities(serverPlayer);
                           AttributeInstance attributeInstance = serverPlayer.getAttribute(Attributes.ATTACK_DAMAGE);
                           if (attributeInstance == null) {
                              resetDamageAbsorbed(serverPlayer);
                           } else {
                              DamageSource srcPlayerAttack = DamageSource.playerAttack(serverPlayer);

                              for (ManaShieldRetributionAbility ability : abilities.getAll(ManaShieldRetributionAbility.class, Skill::isUnlocked)) {
                                 Vec3 origin = event.getEntity().position();
                                 float damageRadius = ability.getRadius(serverPlayer);
                                 AABB bounds = AABBHelper.create(origin, damageRadius);
                                 List<LivingEntity> result = new ArrayList<>();
                                 EntityHelper.getEntitiesInRange(serverPlayer.level, bounds, origin, damageRadius, ENTITY_SELECTION_FILTER, result);
                                 result.removeIf(entity -> entity == serverPlayer);
                                 result.removeIf(entity -> entity.isInvulnerableTo(srcPlayerAttack));
                                 ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
                                    event.setCanceled(true);
                                    float damage = (float)(attributeInstance.getValue() + damageAbsorbed * ability.getPercentageDamageDealt());

                                    for (LivingEntity entity : result) {
                                       entity.hurt(srcPlayerAttack, damage);
                                    }
                                 });
                                 serverPlayer.getLevel()
                                    .sendParticles(
                                       new SphericalParticleOptions(
                                          (ParticleType<SphericalParticleOptions>)ModParticles.MANA_SHIELD_RETRIBUTION_EFFECT_RANGE.get(),
                                          damageRadius,
                                          new Vector3f(0.0F, 1.0F, 1.0F)
                                       ),
                                       origin.x,
                                       origin.y,
                                       origin.z,
                                       200,
                                       0.0,
                                       0.0,
                                       0.0,
                                       0.0
                                    );
                              }

                              resetDamageAbsorbed(serverPlayer);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageRadius), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentageDamageDealt), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.damageRadius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.percentageDamageDealt = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageRadius)).ifPresent(tag -> nbt.put("damageRadius", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentageDamageDealt)).ifPresent(tag -> nbt.put("percentageDamageDealt", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.damageRadius = Adapters.FLOAT.readNbt(nbt.get("damageRadius")).orElse(0.0F);
      this.percentageDamageDealt = Adapters.FLOAT.readNbt(nbt.get("percentageDamageDealt")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageRadius)).ifPresent(element -> json.add("damageRadius", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentageDamageDealt)).ifPresent(element -> json.add("percentageDamageDealt", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.damageRadius = Adapters.FLOAT.readJson(json.get("damageRadius")).orElse(0.0F);
      this.percentageDamageDealt = Adapters.FLOAT.readJson(json.get("percentageDamageDealt")).orElse(0.0F);
   }

   private static class FloatContainer {
      private static final ManaShieldRetributionAbility.FloatContainer DEFAULT = new ManaShieldRetributionAbility.FloatContainer();
      private float value;
   }
}
