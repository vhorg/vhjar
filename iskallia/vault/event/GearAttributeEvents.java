package iskallia.vault.event;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityChainAttackedEvent;
import iskallia.vault.core.event.common.EntityDamageBlockEvent;
import iskallia.vault.core.event.common.EntityStunnedEvent;
import iskallia.vault.entity.entity.EffectCloudEntity;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.Entropy;
import iskallia.vault.util.calc.BlockChanceHelper;
import iskallia.vault.util.calc.ChainHelper;
import iskallia.vault.util.calc.FatalStrikeHelper;
import iskallia.vault.util.calc.GrantedEffectHelper;
import iskallia.vault.util.calc.ThornsHelper;
import iskallia.vault.util.damage.AttackScaleHelper;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class GearAttributeEvents {
   private static final Random rand = new Random();

   @SubscribeEvent
   public static void fireImmunePreventFireDamage(LivingAttackEvent event) {
      if (event.getSource().isFire()) {
         withSnapshot(event, false, (entity, snapshot) -> {
            if (snapshot.getAttributeValue(ModGearAttributes.IS_FIRE_IMMUNE, VaultGearAttributeTypeMerger.anyTrue())) {
               event.setCanceled(true);
            }
         });
      }
   }

   @SubscribeEvent
   public static void triggerAoEAttack(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (!attacker.getLevel().isClientSide() && AttributeSnapshotHelper.canHaveSnapshot(attacker)) {
            if (attacker instanceof Player player) {
               if (PlayerActiveFlags.isSet(player, PlayerActiveFlags.Flag.ATTACK_AOE)) {
                  return;
               }

               if (AttackScaleHelper.getLastAttackScale(player) < 1.0F) {
                  return;
               }
            }

            LivingEntity attacked = event.getEntityLiving();
            Level world = attacker.getLevel();
            AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
            int aoeSize = snapshot.getAttributeValue(ModGearAttributes.ON_HIT_AOE, VaultGearAttributeTypeMerger.intSum());
            if (aoeSize > 0) {
               ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
                  List<Mob> nearby = EntityHelper.getNearby(world, attacked.blockPosition(), aoeSize, Mob.class);
                  nearby.remove(attacked);
                  nearby.remove(attacker);
                  nearby.removeIf(mob -> (attacker instanceof EternalEntity || attacker instanceof Player) && mob instanceof EternalEntity);
                  nearby.forEach(mob -> {
                     Vec3 movement = mob.getDeltaMovement();
                     mob.hurt(event.getSource(), event.getAmount() * 0.6F);
                     mob.setDeltaMovement(movement);
                  });
                  if (attacker instanceof Player player) {
                     PlayerActiveFlags.set(player, PlayerActiveFlags.Flag.ATTACK_AOE, 2);
                  }
               });
            }
         }
      }
   }

   @SubscribeEvent
   public static void triggerChainAttack(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (!attacker.getLevel().isClientSide() && AttributeSnapshotHelper.canHaveSnapshot(attacker)) {
            if (attacker instanceof Player player) {
               if (PlayerActiveFlags.isSet(player, PlayerActiveFlags.Flag.CHAINING_AOE)) {
                  return;
               }

               if (AttackScaleHelper.getLastAttackScale(player) < 1.0F) {
                  return;
               }
            }

            LivingEntity attacked = event.getEntityLiving();
            Level world = attacker.getLevel();
            int chainCount = ChainHelper.getChainCount(attacker);
            if (chainCount > 0) {
               ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
                  List<Mob> nearby = EntityHelper.getNearby(world, attacked.blockPosition(), 5.0F, Mob.class);
                  nearby.remove(attacked);
                  nearby.remove(attacker);
                  nearby.removeIf(mobx -> (attacker instanceof EternalEntity || attacker instanceof Player) && mobx instanceof EternalEntity);
                  if (!nearby.isEmpty()) {
                     nearby.sort(Comparator.comparing(e -> e.distanceTo(attacked)));
                     nearby = nearby.subList(0, Math.min(chainCount, nearby.size()));
                     float multiplier = 0.5F;

                     for (Mob mob : nearby) {
                        Vec3 movement = mob.getDeltaMovement();
                        mob.hurt(event.getSource(), event.getAmount() * multiplier);
                        mob.setDeltaMovement(movement);
                        multiplier *= 0.5F;
                     }

                     CommonEvents.ENTITY_CHAIN_ATTACKED.invoke(new EntityChainAttackedEvent.Data(attacker, nearby));
                  }

                  if (attacker instanceof Player player) {
                     PlayerActiveFlags.set(player, PlayerActiveFlags.Flag.CHAINING_AOE, 2);
                  }
               });
            }
         }
      }
   }

   @SubscribeEvent
   public static void triggerKnockbackAttack(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (!attacker.getLevel().isClientSide() && AttributeSnapshotHelper.canHaveSnapshot(attacker)) {
            if (attacker instanceof Player player) {
               if (PlayerActiveFlags.isSet(player, PlayerActiveFlags.Flag.ATTACK_AOE)) {
                  return;
               }

               if (AttackScaleHelper.getLastAttackScale(player) < 1.0F) {
                  return;
               }
            }

            AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
            float knockbackChance = snapshot.getAttributeValue(ModGearAttributes.SHOCKING_HIT_CHANCE, VaultGearAttributeTypeMerger.floatSum());
            if (Entropy.canExecute(attacker, Entropy.Stat.KNOCKBACK_ATTACK_CHANCE, knockbackChance)) {
               Level world = attacker.getLevel();
               LivingEntity attacked = event.getEntityLiving();
               ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
                  List<Mob> nearby = EntityHelper.getNearby(world, attacked.blockPosition(), 5.0F, Mob.class);
                  nearby.remove(attacker);
                  nearby.removeIf(mob -> (attacker instanceof EternalEntity || attacker instanceof Player) && mob instanceof EternalEntity);
                  nearby.forEach(mob -> EntityHelper.knockback(mob, attacker));
                  if (attacker instanceof Player player) {
                     PlayerActiveFlags.set(player, PlayerActiveFlags.Flag.ATTACK_AOE, 2);
                  }
               });
            }
         }
      }
   }

   @SubscribeEvent
   public static void triggerStunAttack(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (!attacker.getLevel().isClientSide() && AttributeSnapshotHelper.canHaveSnapshot(attacker)) {
            if (attacker instanceof Player player) {
               if (PlayerActiveFlags.isSet(player, PlayerActiveFlags.Flag.ATTACK_AOE)) {
                  return;
               }

               if (AttackScaleHelper.getLastAttackScale(player) < 1.0F) {
                  return;
               }
            }

            AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
            float stunChance = snapshot.getAttributeValue(ModGearAttributes.ON_HIT_STUN, VaultGearAttributeTypeMerger.floatSum());
            if (Entropy.canExecute(attacker, Entropy.Stat.STUN_ATTACK_CHANCE, stunChance)) {
               LivingEntity attacked = event.getEntityLiving();
               attacked.addEffect(new MobEffectInstance(ModEffects.NO_AI, 30, 1));
               CommonEvents.ENTITY_STUNNED.invoke(new EntityStunnedEvent.Data(attacker, attacked));
            }
         }
      }
   }

   @SubscribeEvent
   public static void blockAttack(LivingAttackEvent event) {
      LivingEntity attacked = event.getEntityLiving();
      DamageSource damageSource = event.getSource();
      if (!attacked.getLevel().isClientSide() && !damageSource.isBypassInvul()) {
         ItemStack mainHandStack = attacked.getItemInHand(InteractionHand.MAIN_HAND);
         ItemStack offHandStack = attacked.getItemInHand(InteractionHand.OFF_HAND);
         ItemStack shieldStack = mainHandStack.getItem() instanceof ShieldItem
            ? mainHandStack
            : (offHandStack.getItem() instanceof ShieldItem ? offHandStack : null);
         if (shieldStack != null) {
            if (AttributeSnapshotHelper.canHaveSnapshot(attacked)) {
               float blockChance = BlockChanceHelper.getBlockChance(attacked);
               if (!Entropy.canExecute(attacked, Entropy.Stat.BLOCK, blockChance)) {
                  CommonEvents.ENTITY_DAMAGE_BLOCK.invoke(new EntityDamageBlockEvent.Data(true, damageSource, attacked));
               } else {
                  event.setCanceled(true);
                  CommonEvents.ENTITY_DAMAGE_BLOCK.invoke(new EntityDamageBlockEvent.Data(false, damageSource, attacked));
                  if (shieldStack.getItem() instanceof VaultGearItem) {
                     VaultGearData gearData = VaultGearData.read(shieldStack);
                     gearData.getFirstValue(ModGearAttributes.GEAR_MODEL)
                        .flatMap(ModDynamicModels.Shields.REGISTRY::get)
                        .ifPresent(shieldModel -> shieldModel.onBlocked(attacked, damageSource));
                  }

                  attacked.getLevel().broadcastEntityEvent(attacked, (byte)29);
                  if (attacked instanceof Player player) {
                     BlockChanceHelper.setPlayerBlocking(player);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void increaseDamageDealt(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (!attacker.getLevel().isClientSide() && AttributeSnapshotHelper.canHaveSnapshot(attacker)) {
            LivingEntity attacked = event.getEntityLiving();
            MobType type = attacked.getMobType();
            AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
            float increasedDamage = 0.0F;
            increasedDamage += snapshot.getAttributeValue(ModGearAttributes.DAMAGE_INCREASE, VaultGearAttributeTypeMerger.floatSum());
            if (type == MobType.UNDEAD) {
               increasedDamage += snapshot.getAttributeValue(ModGearAttributes.DAMAGE_UNDEAD, VaultGearAttributeTypeMerger.floatSum());
            }

            if (type == MobType.ARTHROPOD) {
               increasedDamage += snapshot.getAttributeValue(ModGearAttributes.DAMAGE_SPIDERS, VaultGearAttributeTypeMerger.floatSum());
            }

            if (type == MobType.ILLAGER) {
               increasedDamage += snapshot.getAttributeValue(ModGearAttributes.DAMAGE_ILLAGERS, VaultGearAttributeTypeMerger.floatSum());
            }

            event.setAmount(event.getAmount() * (1.0F + increasedDamage));
         }
      }
   }

   @SubscribeEvent
   public static void triggerEffectCloudsActive(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (!attacker.getLevel().isClientSide() && AttributeSnapshotHelper.canHaveSnapshot(attacker)) {
            if (!(attacker instanceof Player player && AttackScaleHelper.getLastAttackScale(player) < 1.0F)) {
               boolean doEffectClouds = !ActiveFlags.IS_AOE_ATTACKING.isSet()
                  && !ActiveFlags.IS_DOT_ATTACKING.isSet()
                  && !ActiveFlags.IS_REFLECT_ATTACKING.isSet();
               if (doEffectClouds) {
                  LivingEntity attacked = event.getEntityLiving();
                  AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
                  snapshot.getAttributeValue(ModGearAttributes.EFFECT_CLOUD, VaultGearAttributeTypeMerger.asList()).forEach(cloud -> {
                     MobEffect effect = cloud.getPrimaryEffect();
                     if (effect != null) {
                        if (Entropy.canExecute(attacker, Entropy.Stat.effectCloud(effect), cloud.getTriggerChance())) {
                           EffectCloudEntity cloudEntity = new EffectCloudEntity(attacker.getLevel(), attacked.getX(), attacked.getY(), attacked.getZ());
                           cloud.apply(cloudEntity);
                           cloudEntity.setOwner(attacker);
                           attacker.getLevel().addFreshEntity(cloudEntity);
                        }
                     }
                  });
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void triggerEffectCloudsPassive(LivingHurtEvent event) {
      boolean doEffectClouds = !ActiveFlags.IS_AOE_ATTACKING.isSet() && !ActiveFlags.IS_DOT_ATTACKING.isSet() && !ActiveFlags.IS_REFLECT_ATTACKING.isSet();
      if (doEffectClouds) {
         if (event.getSource().getEntity() != null) {
            withSnapshot(
               event,
               true,
               (attacked, snapshot) -> snapshot.getAttributeValue(ModGearAttributes.EFFECT_CLOUD_WHEN_HIT, VaultGearAttributeTypeMerger.asList())
                  .forEach(cloud -> {
                     MobEffect effect = cloud.getPrimaryEffect();
                     if (effect != null) {
                        if (Entropy.canExecute(attacked, Entropy.Stat.effectCloudWhenHit(effect), cloud.getTriggerChance())) {
                           EffectCloudEntity cloudEntity = new EffectCloudEntity(attacked.getLevel(), attacked.getX(), attacked.getY(), attacked.getZ());
                           cloud.apply(cloudEntity);
                           cloudEntity.setOwner(attacked);
                           attacked.getLevel().addFreshEntity(cloudEntity);
                        }
                     }
                  })
            );
         }
      }
   }

   @SubscribeEvent
   public static void removeImmuneEffects(LivingUpdateEvent event) {
      withSnapshot(event, true, (entity, snapshot) -> GrantedEffectHelper.getImmunities(entity).forEach(entity::removeEffect));
   }

   @SubscribeEvent
   public static void avoidPotionEffect(PotionApplicableEvent event) {
      withSnapshot(event, true, (entity, snapshot) -> {
         if (GrantedEffectHelper.canAvoidEffect(event.getPotionEffect().getEffect(), entity, rand)) {
            event.setResult(Result.DENY);
         }
      });
   }

   @SubscribeEvent
   public static void forceVanillaCrit(CriticalHitEvent event) {
      withSnapshot(event, true, (entity, snapshot) -> {
         float criticalChance = snapshot.getAttributeValue(ModGearAttributes.VANILLA_CRITICAL_HIT_CHANCE, VaultGearAttributeTypeMerger.floatSum());
         if (Entropy.canExecute(entity, Entropy.Stat.VANILLA_CRITICAL, criticalChance)) {
            if (event.getDamageModifier() < 1.5F) {
               event.setDamageModifier(1.5F);
            }

            event.setResult(Result.ALLOW);
         }
      });
   }

   @SubscribeEvent
   public static void doFatalStrikeAttack(LivingHurtEvent event) {
      Entity source = event.getSource().getEntity();
      if (source instanceof LivingEntity attacker) {
         if (!source.getLevel().isClientSide()) {
            float fatalStrikeChance = FatalStrikeHelper.getFatalStrikeChance(attacker);
            if (Entropy.canExecute(attacker, Entropy.Stat.FATAL_STRIKE, fatalStrikeChance)) {
               float fatalPercentDamage = FatalStrikeHelper.getFatalStrikeDamage(attacker);
               float damage = event.getAmount() * (1.0F + fatalPercentDamage);
               event.setAmount(damage);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void thornsReflectDamage(LivingAttackEvent event) {
      if (!(event.getSource() instanceof ThornsReflectDamageSource)) {
         if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            withSnapshot(event, true, (attacked, snapshot) -> {
               float thornsChance = ThornsHelper.getThornsChance(attacked);
               if (Entropy.canExecute(attacked, Entropy.Stat.THORNS, thornsChance)) {
                  float thornsMultiplier = ThornsHelper.getThornsDamage(attacked);
                  if (!(thornsMultiplier <= 0.0F)) {
                     float dmg = (float)attacked.getAttributeValue(Attributes.ATTACK_DAMAGE);
                     DamageSource src = ThornsReflectDamageSource.of(attacked);
                     float reflectedDamage = dmg * thornsMultiplier;
                     attacker.hurt(src, reflectedDamage);
                  }
               }
            });
         }
      }
   }

   private static void withSnapshot(LivingEvent event, boolean serverOnly, BiConsumer<LivingEntity, AttributeSnapshot> fn) {
      withSnapshot(event.getEntityLiving(), serverOnly, fn);
   }

   private static void withSnapshot(LivingEntity entity, boolean serverOnly, BiConsumer<LivingEntity, AttributeSnapshot> fn) {
      if (AttributeSnapshotHelper.canHaveSnapshot(entity)) {
         if (!serverOnly || !entity.getCommandSenderWorld().isClientSide()) {
            fn.accept(entity, AttributeSnapshotHelper.getInstance().getSnapshot(entity));
         }
      }
   }
}
