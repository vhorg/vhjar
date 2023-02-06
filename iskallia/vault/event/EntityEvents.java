package iskallia.vault.event;

import iskallia.vault.VaultMod;
import iskallia.vault.block.TreasureDoorBlock;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.entity.entity.EffectCloudEntity;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.entity.FighterEntity;
import iskallia.vault.entity.entity.MonsterEyeEntity;
import iskallia.vault.entity.entity.TreasureGoblinEntity;
import iskallia.vault.gear.attribute.custom.EffectCloudAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.item.gear.VaultShieldItem;
import iskallia.vault.network.message.ClientboundPlayerLastDamageSourceMessage;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import iskallia.vault.world.data.ServerVaults;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkDirection;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class EntityEvents {
   private static final Random rand = new Random();

   @SubscribeEvent
   public static void onTradesLoad(VillagerTradesEvent event) {
      ObjectIterator var1 = event.getTrades().values().iterator();

      while (var1.hasNext()) {
         List<ItemListing> trades = (List<ItemListing>)var1.next();
         trades.removeIf(trade -> {
            try {
               MerchantOffer offer = trade.getOffer(null, rand);
               ItemStack output = offer.assemble();
               if (output.isEmpty()) {
                  return true;
               }

               Item outItem = output.getItem();
               if (outItem == Items.AIR) {
                  return true;
               }

               if (outItem instanceof ShieldItem) {
                  return true;
               }

               if (outItem instanceof TippedArrowItem) {
                  return true;
               }

               if (!outItem.getRegistryName().getNamespace().equals("minecraft")) {
                  return true;
               }
            } catch (Exception var4) {
            }

            return false;
         });
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onDamageTotem(LivingHurtEvent event) {
      Level world = event.getEntity().getCommandSenderWorld();
      if (!world.isClientSide() && world instanceof ServerLevel) {
         if (event.getEntityLiving() instanceof Player player) {
            if (!event.getSource().isBypassArmor()) {
               ItemStack offHand = event.getEntityLiving().getOffhandItem();
               if (ServerVaults.isVaultWorld(world) || !(offHand.getItem() instanceof VaultGearItem)) {
                  if (offHand.getItem() instanceof IdolItem || offHand.getItem() instanceof VaultShieldItem) {
                     int damage = (int)CommonEvents.PLAYER_STAT
                        .invoke(PlayerStat.DURABILITY_DAMAGE, player, Math.max(1.0F, event.getAmount() / 6.0F))
                        .getValue();
                     if (damage <= 1) {
                        damage = 1;
                     }

                     offHand.hurtAndBreak(damage, event.getEntityLiving(), entity -> entity.broadcastBreakEvent(EquipmentSlot.OFFHAND));
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntityTick2(LivingUpdateEvent event) {
      if (!event.getEntity().level.isClientSide && event.getEntity() instanceof FighterEntity && event.getEntity().level.dimension() == VaultMod.ARENA_KEY) {
         ((FighterEntity)event.getEntity()).setPersistenceRequired();
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onEntityDrops(LivingDropsEvent event) {
      Level world = event.getEntity().level;
      if (!world.isClientSide() && world instanceof ServerLevel sWorld) {
         if (ServerVaults.isVaultWorld(world)) {
            Entity entity = event.getEntity();
            if (!shouldDropDefaultInVault(entity)) {
               Vault vault = ServerVaults.get(sWorld).orElse(null);
               if (vault != null) {
                  DamageSource killingSrc = event.getSource();
                  event.getDrops().clear();
                  boolean addedDrops = false;
                  Entity killerEntity = killingSrc.getEntity();
                  if (killerEntity instanceof EternalEntity) {
                     killerEntity = (Entity)((EternalEntity)killerEntity).getOwner().right().orElse(null);
                  }

                  if (killerEntity instanceof ServerPlayer killer) {
                     addedDrops |= addShardDrops(world, entity, killer, event.getDrops());
                  }

                  if (!addedDrops) {
                  }
               }
            }
         }
      }
   }

   private static boolean shouldDropDefaultInVault(Entity entity) {
      return entity instanceof TreasureGoblinEntity || entity instanceof Player;
   }

   private static boolean addShardDrops(Level world, Entity killed, ServerPlayer killer, Collection<ItemEntity> drops) {
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(killer);
      float chanceMultiplier = snapshot.getAttributeValue(ModGearAttributes.SOUL_CHANCE, VaultGearAttributeTypeMerger.floatSum());
      chanceMultiplier = CommonEvents.SOUL_SHARD_CHANCE.invoke(killer, chanceMultiplier).getChance();
      int shardCount = ModConfigs.SOUL_SHARD.getRandomShards(killed.getType(), 1.0F + chanceMultiplier);
      if (shardCount <= 0) {
         return false;
      } else {
         ItemStack shards = new ItemStack(ModItems.SOUL_SHARD, shardCount);
         ItemEntity itemEntity = new ItemEntity(world, killed.getX(), killed.getY(), killed.getZ(), shards);
         itemEntity.setDefaultPickUpDelay();
         drops.add(itemEntity);
         return true;
      }
   }

   @SubscribeEvent
   public static void preventNormalMobSpawningInVaultAndArena(CheckSpawn event) {
      if (ServerVaults.isVaultWorld(event.getEntity().getCommandSenderWorld()) && !event.isSpawner()) {
         event.setResult(Result.DENY);
      } else if (event.getEntity().getCommandSenderWorld().dimension() == VaultMod.ARENA_KEY) {
         event.setResult(Result.DENY);
      }
   }

   @SubscribeEvent
   public static void onPlayerDeathInVaults(LivingDeathEvent event) {
      if (event.getEntityLiving() instanceof Slime slimeEntity && !slimeEntity.getLevel().isClientSide()) {
         if (!(slimeEntity instanceof MonsterEyeEntity)) {
            ServerVaults.get(slimeEntity.getLevel()).ifPresent(vault -> {
               int level = vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
               ModConfigs.VAULT_ENTITIES.getSlimeEffectConfig(level).map(EffectCloudAttribute.EffectCloud::fromConfig).ifPresent(cloud -> {
                  if (!(rand.nextFloat() >= cloud.getTriggerChance())) {
                     EffectCloudEntity cloudEntity = new EffectCloudEntity(slimeEntity.getLevel(), slimeEntity.getX(), slimeEntity.getY(), slimeEntity.getZ());
                     cloud.apply(cloudEntity);
                     slimeEntity.getLevel().addFreshEntity(cloudEntity);
                  }
               });
            });
         }
      }
   }

   @SubscribeEvent
   public static void onWitchThrowPotion(EntityJoinWorldEvent event) {
      if (event.getWorld() instanceof ServerLevel serverLevel) {
         if (!event.loadedFromDisk() && event.getEntity() instanceof ThrownPotion thrownPotion) {
            Entity thrower = thrownPotion.getOwner();
            if (thrower instanceof Witch) {
               ServerVaults.get(serverLevel).ifPresent(vault -> {
                  int level = vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
                  List<MobEffectInstance> configuredEffects = ModConfigs.VAULT_ENTITIES.getWitchAdditionalThrownEffects(level);
                  ItemStack thrown = thrownPotion.getItem();
                  List<MobEffectInstance> effects = new ArrayList<>(configuredEffects);
                  PotionUtils.setCustomEffects(thrown, effects);
                  thrownPotion.setItem(thrown);
               });
            }
         }
      }
   }

   @SubscribeEvent
   public static void onDamageArmorHit(LivingDamageEvent event) {
      LivingEntity damaged = event.getEntityLiving();
      if (damaged instanceof Player player && !damaged.getCommandSenderWorld().isClientSide()) {
         Entity trueSrc = event.getSource().getEntity();
         if (trueSrc instanceof LivingEntity) {
            double chance = ((LivingEntity)trueSrc).getAttributeValue(ModAttributes.BREAK_ARMOR_CHANCE);

            while (chance > 0.0 && !(rand.nextFloat() > chance)) {
               chance--;
               player.getInventory().hurtArmor(event.getSource(), 4.0F, Inventory.ALL_ARMOR_SLOTS);
            }
         }
      }
   }

   @SubscribeEvent
   public static void applyPoison(PotionApplicableEvent event) {
      if (event.getPotionEffect().getEffect() == MobEffects.POISON
         && ServerVaults.isInVault(event.getEntityLiving())
         && (event.getEntityLiving().getMobType() == MobType.UNDEAD || event.getEntityLiving() instanceof Spider)) {
         event.setResult(Result.ALLOW);
      }
   }

   @SubscribeEvent
   public static void entityDealCrit(LivingHurtEvent event) {
      LivingEntity attacked = event.getEntityLiving();
      if (!attacked.level.isClientSide) {
         if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            AttributeMap attributes = attacker.getAttributes();
            if (attributes.hasAttribute(ModAttributes.CRIT_CHANCE)) {
               if (attributes.hasAttribute(ModAttributes.CRIT_MULTIPLIER)) {
                  double chance = attacker.getAttributeValue(ModAttributes.CRIT_CHANCE);
                  if (!(rand.nextFloat() >= chance)) {
                     float multiplier = (float)attacker.getAttributeValue(ModAttributes.CRIT_MULTIPLIER);
                     if (AttributeSnapshotHelper.canHaveSnapshot(attacked) && multiplier > 1.0F) {
                        AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacked);
                        float mitigation = snapshot.getAttributeValue(ModGearAttributes.CRITICAL_HIT_TAKEN_REDUCTION, VaultGearAttributeTypeMerger.floatSum());
                        float multiplierPart = multiplier - 1.0F;
                        multiplierPart = Math.max(multiplierPart - multiplierPart * mitigation, 0.0F);
                        multiplier = multiplierPart + 1.0F;
                     }

                     attacker.level
                        .playSound(
                           null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, attacker.getSoundSource(), 1.0F, 1.0F
                        );
                     event.setAmount(event.getAmount() * multiplier);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHurtTp(LivingHurtEvent event) {
      if (!event.getEntityLiving().level.isClientSide) {
         boolean direct = event.getSource().getDirectEntity() == event.getSource().getEntity();
         if (direct && event.getEntityLiving().getAttributes().hasAttribute(ModAttributes.TP_CHANCE)) {
            double chance = event.getEntityLiving().getAttributeValue(ModAttributes.TP_CHANCE);
            if (event.getEntityLiving().getAttributes().hasAttribute(ModAttributes.TP_RANGE)) {
               double range = event.getEntityLiving().getAttributeValue(ModAttributes.TP_RANGE);
               if (event.getEntityLiving().level.random.nextDouble() < chance) {
                  for (int i = 0; i < 64; i++) {
                     if (teleportRandomly(event.getEntityLiving(), range)) {
                        event.getEntityLiving()
                           .level
                           .playSound(
                              null,
                              event.getEntityLiving().xo,
                              event.getEntityLiving().yo,
                              event.getEntityLiving().zo,
                              ModSounds.BOSS_TP_SFX,
                              event.getEntityLiving().getSoundSource(),
                              1.0F,
                              1.0F
                           );
                        event.setCanceled(true);
                        return;
                     }
                  }
               }
            }
         } else if (!direct && event.getEntityLiving().getAttributes().hasAttribute(ModAttributes.TP_INDIRECT_CHANCE)) {
            double chance = event.getEntityLiving().getAttributeValue(ModAttributes.TP_INDIRECT_CHANCE);
            if (event.getEntityLiving().getAttributes().hasAttribute(ModAttributes.TP_RANGE)) {
               double range = event.getEntityLiving().getAttributeValue(ModAttributes.TP_RANGE);
               if (event.getEntityLiving().level.random.nextDouble() < chance) {
                  for (int ix = 0; ix < 64; ix++) {
                     if (teleportRandomly(event.getEntityLiving(), range)) {
                        event.getEntityLiving()
                           .level
                           .playSound(
                              null,
                              event.getEntityLiving().xo,
                              event.getEntityLiving().yo,
                              event.getEntityLiving().zo,
                              ModSounds.BOSS_TP_SFX,
                              event.getEntityLiving().getSoundSource(),
                              1.0F,
                              1.0F
                           );
                        event.setCanceled(true);
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean teleportRandomly(LivingEntity entity, double range) {
      if (!entity.level.isClientSide() && entity.isAlive()) {
         double d0 = entity.getX() + (entity.level.random.nextDouble() - 0.5) * range * 2.0;
         double d1 = entity.getY() + (entity.level.random.nextInt((int)(range * 2.0)) - range);
         double d2 = entity.getZ() + (entity.level.random.nextDouble() - 0.5) * range * 2.0;
         return entity.randomTeleport(d0, d1, d2, true);
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void onEntityDestroy(LivingDestroyBlockEvent event) {
      if (event.getState().getBlock() instanceof TreasureDoorBlock) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onReinforcementsSpawn(SummonAidEvent event) {
      Entity entity = event.getEntity();
      if (ServerVaults.isInVault(entity)) {
         event.setResult(Result.DENY);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void thornsReflectDamage(LivingAttackEvent event) {
      if (!(event.getSource() instanceof ThornsReflectDamageSource)) {
         if (event.getEntityLiving() instanceof ServerPlayer player && (event.getSource() == DamageSource.MAGIC || event.getSource() == DamageSource.WITHER)) {
            ModNetwork.CHANNEL
               .sendTo(new ClientboundPlayerLastDamageSourceMessage(event.getSource()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         }

         if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            LivingEntity attacked = event.getEntityLiving();
            if (attacked.getAttribute(ModAttributes.THORNS_CHANCE) != null && attacked.getAttribute(ModAttributes.THORNS_DAMAGE) != null) {
               double thornsChance = attacked.getAttribute(ModAttributes.THORNS_CHANCE).getValue();
               if (!(rand.nextFloat() >= thornsChance)) {
                  double thornsMultiplier = attacked.getAttribute(ModAttributes.THORNS_DAMAGE).getValue();
                  if (!(thornsMultiplier <= 0.0)) {
                     float dmg = (float)attacked.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                     DamageSource src = ThornsReflectDamageSource.of(event.getEntityLiving());
                     attacker.hurt(src, (float)(dmg * thornsMultiplier));
                     event.getEntityLiving()
                        .level
                        .playSound(
                           null,
                           event.getEntityLiving().getOnPos(),
                           SoundEvents.THORNS_HIT,
                           SoundSource.BLOCKS,
                           1.0F,
                           (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F
                        );
                  }
               }
            }
         }
      }
   }
}
