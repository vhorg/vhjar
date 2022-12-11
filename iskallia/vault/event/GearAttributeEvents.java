package iskallia.vault.event;

import iskallia.vault.entity.entity.EffectCloudEntity;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.calc.BlockChanceHelper;
import iskallia.vault.util.calc.ChainHelper;
import iskallia.vault.util.calc.FatalStrikeHelper;
import iskallia.vault.util.calc.GrantedEffectHelper;
import iskallia.vault.util.calc.ThornsHelper;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
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
            if (!(attacker instanceof Player player && PlayerActiveFlags.isSet(player, PlayerActiveFlags.Flag.ATTACK_AOE))) {
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
                     nearby.forEach(mob -> mob.hurt(event.getSource(), event.getAmount() * 0.6F));
                     if (attacker instanceof Player playerx) {
                        PlayerActiveFlags.set(playerx, PlayerActiveFlags.Flag.ATTACK_AOE, 2);
                     }
                  });
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void triggerChainAttack(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (!attacker.getLevel().isClientSide() && AttributeSnapshotHelper.canHaveSnapshot(attacker)) {
            if (!(attacker instanceof Player player && PlayerActiveFlags.isSet(player, PlayerActiveFlags.Flag.CHAINING_AOE))) {
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
                           mob.hurt(event.getSource(), event.getAmount() * multiplier);
                           multiplier *= 0.5F;
                        }
                     }

                     if (attacker instanceof Player playerx) {
                        PlayerActiveFlags.set(playerx, PlayerActiveFlags.Flag.CHAINING_AOE, 2);
                     }
                  });
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void triggerStunAttack(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (!attacker.getLevel().isClientSide() && AttributeSnapshotHelper.canHaveSnapshot(attacker)) {
            if (!(attacker instanceof Player player && PlayerActiveFlags.isSet(player, PlayerActiveFlags.Flag.ATTACK_AOE))) {
               AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
               float stunChance = snapshot.getAttributeValue(ModGearAttributes.ON_HIT_STUN, VaultGearAttributeTypeMerger.floatSum());
               if (!(stunChance <= 0.0F) && !(rand.nextFloat() >= stunChance)) {
                  LivingEntity attacked = event.getEntityLiving();
                  attacked.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 9));
                  attacked.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30, 9));
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void blockAttack(LivingAttackEvent event) {
      LivingEntity attacked = event.getEntityLiving();
      if (!attacked.getLevel().isClientSide() && !event.getSource().isBypassInvul()) {
         ItemStack mainHandStack = attacked.getItemInHand(InteractionHand.MAIN_HAND);
         ItemStack offHandStack = attacked.getItemInHand(InteractionHand.OFF_HAND);
         ItemStack shieldStack = mainHandStack.getItem() instanceof ShieldItem
            ? mainHandStack
            : (offHandStack.getItem() instanceof ShieldItem ? offHandStack : null);
         if (shieldStack != null) {
            if (AttributeSnapshotHelper.canHaveSnapshot(attacked)) {
               float blockChance = BlockChanceHelper.getBlockChance(attacked);
               if (!(rand.nextFloat() >= blockChance)) {
                  event.setCanceled(true);
                  if (shieldStack.getItem() instanceof VaultGearItem) {
                     VaultGearData gearData = VaultGearData.read(shieldStack);
                     Boolean isBellModel = gearData.getFirstValue(ModGearAttributes.GEAR_MODEL)
                        .map(modelId -> modelId.equals(ModDynamicModels.Shields.BELL.getId()))
                        .orElse(false);
                     if (isBellModel) {
                        attacked.getLevel().playSound(null, attacked.getOnPos(), SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);
                     }
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
            boolean doEffectClouds = !ActiveFlags.IS_AOE_ATTACKING.isSet()
               && !ActiveFlags.IS_DOT_ATTACKING.isSet()
               && !ActiveFlags.IS_REFLECT_ATTACKING.isSet();
            if (doEffectClouds) {
               LivingEntity attacked = event.getEntityLiving();
               AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
               snapshot.getAttributeValue(ModGearAttributes.EFFECT_CLOUD, VaultGearAttributeTypeMerger.asList()).forEach(cloud -> {
                  if (!(rand.nextFloat() >= cloud.getTriggerChance())) {
                     EffectCloudEntity cloudEntity = new EffectCloudEntity(attacker.getLevel(), attacked.getX(), attacked.getY(), attacked.getZ());
                     cloud.apply(cloudEntity);
                     cloudEntity.setOwner(attacker);
                     attacker.getLevel().addFreshEntity(cloudEntity);
                  }
               });
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
                     if (!(rand.nextFloat() >= cloud.getTriggerChance())) {
                        EffectCloudEntity cloudEntity = new EffectCloudEntity(attacked.getLevel(), attacked.getX(), attacked.getY(), attacked.getZ());
                        cloud.apply(cloudEntity);
                        cloudEntity.setOwner(attacked);
                        attacked.getLevel().addFreshEntity(cloudEntity);
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
         if (rand.nextFloat() < criticalChance) {
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
            if (!(rand.nextFloat() >= fatalStrikeChance)) {
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
               if (!(rand.nextFloat() >= thornsChance)) {
                  float thornsMultiplier = ThornsHelper.getThornsDamage(attacked);
                  if (!(thornsMultiplier <= 0.0F)) {
                     float dmg = (float)attacked.getAttributeValue(Attributes.ATTACK_DAMAGE);
                     DamageSource src = ThornsReflectDamageSource.of(attacked);
                     attacker.hurt(src, dmg * thornsMultiplier);
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
