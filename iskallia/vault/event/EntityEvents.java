package iskallia.vault.event;

import iskallia.vault.Vault;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.VaultDoorBlock;
import iskallia.vault.config.ScavengerHuntConfig;
import iskallia.vault.entity.AggressiveCowEntity;
import iskallia.vault.entity.EffectCloudEntity;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.entity.TreasureGoblinEntity;
import iskallia.vault.entity.VaultFighterEntity;
import iskallia.vault.entity.VaultGuardianEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.BasicScavengerItem;
import iskallia.vault.item.ItemShardPouch;
import iskallia.vault.item.ItemVaultRaffleSeal;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.VaultGearHelper;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.skill.talent.type.SoulShardTalent;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.influence.VaultAttributeInfluence;
import iskallia.vault.world.vault.logic.objective.ScavengerHuntObjective;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.modifier.CurseOnHitModifier;
import iskallia.vault.world.vault.modifier.DurabilityDamageModifier;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class EntityEvents {
   private static final Random rand = new Random();

   @SubscribeEvent
   public static void onTradesLoad(VillagerTradesEvent event) {
      ObjectIterator var1 = event.getTrades().values().iterator();

      while (var1.hasNext()) {
         List<ITrade> trades = (List<ITrade>)var1.next();
         trades.removeIf(trade -> {
            try {
               MerchantOffer offer = trade.func_221182_a(null, rand);
               ItemStack output = offer.func_222206_f();
               Item outItem = output.func_77973_b();
               if (outItem instanceof ShieldItem) {
                  return true;
               }

               if (outItem instanceof TippedArrowItem && PotionUtils.func_185191_c(output).equals(Potions.field_185220_C)) {
                  return true;
               }
            } catch (Exception var4) {
            }

            return false;
         });
      }
   }

   @SubscribeEvent
   public static void onBlockBreak(BreakEvent event) {
      PlayerEntity player = event.getPlayer();
      ModifiableAttributeInstance reachAttr = player.func_110148_a((Attribute)ForgeMod.REACH_DISTANCE.get());
      if (reachAttr != null) {
         BlockPos pos = event.getPos();
         BlockState state = player.func_130014_f_().func_180495_p(pos);
         if (state.func_177230_c() instanceof VaultChestBlock) {
            double reach = reachAttr.func_111126_e();
            if (player.func_70092_e(pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5) >= reach * reach) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEffectImmune(LivingUpdateEvent event) {
      if (!event.getEntity().func_130014_f_().func_201670_d()) {
         if (event.getEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)event.getEntity();
            EffectTalent.getImmunities(livingEntity).forEach(livingEntity::func_195063_d);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerFallDamage(LivingDamageEvent event) {
      if (!event.getEntity().field_70170_p.field_72995_K) {
         if (event.getEntity() instanceof PlayerEntity) {
            if (event.getSource() == DamageSource.field_76379_h) {
               ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
               float totalReduction = 0.0F;

               for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                  ItemStack stack = player.func_184582_a(slot);
                  totalReduction += ModAttributes.FEATHER_FEET.get(stack).map(attribute -> attribute.getValue(stack)).orElse(0.0F);
               }

               event.setAmount(event.getAmount() * (1.0F - Math.min(totalReduction, 1.0F)));
               if (event.getAmount() <= 1.0E-4) {
                  event.setCanceled(true);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerMobHit(LivingHurtEvent event) {
      World world = event.getEntity().func_130014_f_();
      if (!world.func_201670_d()) {
         if (event.getSource().func_76346_g() instanceof LivingEntity) {
            LivingEntity attacked = event.getEntityLiving();
            LivingEntity attacker = (LivingEntity)event.getSource().func_76346_g();
            boolean doEffectClouds = !ActiveFlags.IS_AOE_ATTACKING.isSet()
               && !ActiveFlags.IS_DOT_ATTACKING.isSet()
               && !ActiveFlags.IS_REFLECT_ATTACKING.isSet();
            if (doEffectClouds) {
               for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                  ItemStack stack = attacker.func_184582_a(slot);
                  if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
                     for (EffectCloudEntity.Config config : ModAttributes.EFFECT_CLOUD.getOrDefault(stack, new ArrayList<>()).getValue(stack)) {
                        if (!(world.field_73012_v.nextFloat() >= config.getChance())) {
                           EffectCloudEntity cloud = EffectCloudEntity.fromConfig(
                              attacked.field_70170_p, attacker, attacked.func_226277_ct_(), attacked.func_226278_cu_(), attacked.func_226281_cx_(), config
                           );
                           world.func_217376_c(cloud);
                        }
                     }
                  }
               }
            }

            float incDamage = VaultGearHelper.getAttributeValueOnGearSumFloat(attacker, ModAttributes.DAMAGE_INCREASE, ModAttributes.DAMAGE_INCREASE_2);
            CreatureAttribute creatureType = attacked.func_70668_bt();
            if (creatureType == CreatureAttribute.field_223223_b_) {
               incDamage += VaultGearHelper.getAttributeValueOnGearSumFloat(attacker, ModAttributes.DAMAGE_UNDEAD);
            } else if (creatureType == CreatureAttribute.field_223224_c_) {
               incDamage += VaultGearHelper.getAttributeValueOnGearSumFloat(attacker, ModAttributes.DAMAGE_SPIDERS);
            } else if (creatureType == CreatureAttribute.field_223225_d_) {
               incDamage += VaultGearHelper.getAttributeValueOnGearSumFloat(attacker, ModAttributes.DAMAGE_ILLAGERS);
            }

            event.setAmount(event.getAmount() * (1.0F + incDamage));
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onPlayerMobHitAfter(LivingHurtEvent event) {
      World world = event.getEntity().func_130014_f_();
      if (!world.func_201670_d()) {
         if (event.getSource().func_76346_g() instanceof LivingEntity) {
            LivingEntity attacked = event.getEntityLiving();
            LivingEntity attacker = (LivingEntity)event.getSource().func_76346_g();
            if (!ActiveFlags.IS_DOT_ATTACKING.isSet() && !ActiveFlags.IS_REFLECT_ATTACKING.isSet()) {
               boolean mayChainAttack = true;
               if (attacker instanceof PlayerEntity) {
                  mayChainAttack = !PlayerActiveFlags.isSet((PlayerEntity)attacker, PlayerActiveFlags.Flag.CHAINING_AOE);
               }

               if (mayChainAttack) {
                  int additionalChains = VaultGearHelper.getAttributeValueOnGearSumInt(attacker, ModAttributes.ON_HIT_CHAIN);
                  if (additionalChains > 0) {
                     ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
                        List<MobEntity> nearby = EntityHelper.getNearby(world, attacked.func_233580_cy_(), 5.0F, MobEntity.class);
                        nearby.remove(attacked);
                        nearby.remove(attacker);
                        nearby.removeIf(mob -> (attacker instanceof EternalEntity || attacker instanceof PlayerEntity) && mob instanceof EternalEntity);
                        if (!nearby.isEmpty()) {
                           nearby.sort(Comparator.comparing(e -> e.func_70068_e(attacked)));
                           nearby = nearby.subList(0, Math.min(additionalChains, nearby.size()));
                           float multiplier = 0.5F;

                           for (MobEntity me : nearby) {
                              me.func_70097_a(event.getSource(), event.getAmount() * multiplier);
                              multiplier *= 0.5F;
                           }
                        }
                     });
                     if (attacker instanceof PlayerEntity) {
                        PlayerActiveFlags.set((PlayerEntity)attacker, PlayerActiveFlags.Flag.CHAINING_AOE, 2);
                     }
                  }
               }

               boolean mayAoeAttack = true;
               if (attacker instanceof PlayerEntity) {
                  mayAoeAttack = !PlayerActiveFlags.isSet((PlayerEntity)attacker, PlayerActiveFlags.Flag.ATTACK_AOE);
               }

               if (mayAoeAttack) {
                  int blockAoE = VaultGearHelper.getAttributeValueOnGearSumInt(attacker, ModAttributes.ON_HIT_AOE);
                  if (blockAoE > 0) {
                     ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
                        List<MobEntity> nearby = EntityHelper.getNearby(world, attacked.func_233580_cy_(), blockAoE, MobEntity.class);
                        nearby.remove(attacked);
                        nearby.remove(attacker);
                        nearby.removeIf(mob -> (attacker instanceof EternalEntity || attacker instanceof PlayerEntity) && mob instanceof EternalEntity);
                        if (!nearby.isEmpty()) {
                           for (MobEntity me : nearby) {
                              me.func_70097_a(event.getSource(), event.getAmount() * 0.6F);
                           }
                        }
                     });
                  }

                  if (attacker instanceof PlayerEntity) {
                     PlayerActiveFlags.set((PlayerEntity)attacker, PlayerActiveFlags.Flag.ATTACK_AOE, 2);
                  }
               }

               float stunChance = VaultGearHelper.getAttributeValueOnGearSumFloat(attacker, ModAttributes.ON_HIT_STUN);
               if (rand.nextFloat() < stunChance) {
                  attacked.func_195064_c(new EffectInstance(Effects.field_76421_d, 40, 9));
                  attacked.func_195064_c(new EffectInstance(Effects.field_76419_f, 40, 9));
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onDamageTotem(LivingHurtEvent event) {
      World world = event.getEntity().func_130014_f_();
      if (!world.func_201670_d() && world instanceof ServerWorld) {
         if (event.getEntityLiving() instanceof PlayerEntity) {
            if (!event.getSource().func_76363_c()) {
               ServerWorld sWorld = (ServerWorld)world;
               ItemStack offHand = event.getEntityLiving().func_184592_cb();
               if (offHand.func_77973_b() instanceof IdolItem) {
                  PlayerEntity player = (PlayerEntity)event.getEntityLiving();
                  float damage = Math.max(1.0F, event.getAmount() / 5.0F);
                  VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, player.func_233580_cy_());
                  if (vault != null) {
                     for (VaultAttributeInfluence influence : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
                        if (influence.getType() == VaultAttributeInfluence.Type.DURABILITY_DAMAGE && !influence.isMultiplicative()) {
                           damage += influence.getValue();
                        }
                     }

                     for (DurabilityDamageModifier modifier : vault.getActiveModifiersFor(PlayerFilter.of(player), DurabilityDamageModifier.class)) {
                        damage *= modifier.getDurabilityDamageTakenMultiplier();
                     }

                     for (VaultAttributeInfluence influencex : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
                        if (influencex.getType() == VaultAttributeInfluence.Type.DURABILITY_DAMAGE && influencex.isMultiplicative()) {
                           damage += influencex.getValue();
                        }
                     }
                  }

                  offHand.func_222118_a((int)damage, event.getEntityLiving(), entity -> entity.func_213361_c(EquipmentSlotType.OFFHAND));
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onEntityDrops(LivingDropsEvent event) {
      World world = event.getEntity().field_70170_p;
      if (!world.func_201670_d() && world instanceof ServerWorld) {
         if (world.func_234923_W_() == Vault.VAULT_KEY) {
            Entity entity = event.getEntity();
            if (!shouldDropDefaultInVault(entity)) {
               BlockPos pos = entity.func_233580_cy_();
               ServerWorld sWorld = (ServerWorld)world;
               VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, pos);
               if (vault == null) {
                  event.setCanceled(true);
               } else {
                  DamageSource killingSrc = event.getSource();
                  if (!(entity instanceof VaultFighterEntity) && !(entity instanceof AggressiveCowEntity)) {
                     event.getDrops().clear();
                  }

                  if (vault.getActiveObjectives().stream().anyMatch(VaultObjective::preventsNormalMonsterDrops)) {
                     event.setCanceled(true);
                  } else {
                     boolean addedDrops = entity instanceof AggressiveCowEntity;
                     addedDrops |= addScavengerDrops(world, entity, vault, event.getDrops());
                     addedDrops |= addSubFighterDrops(world, entity, vault, event.getDrops());
                     Entity killerEntity = killingSrc.func_76346_g();
                     if (killerEntity instanceof EternalEntity) {
                        killerEntity = (Entity)((EternalEntity)killerEntity).getOwner().right().orElse(null);
                     }

                     if (killerEntity instanceof ServerPlayerEntity) {
                        ServerPlayerEntity killer = (ServerPlayerEntity)killerEntity;
                        if (MiscUtils.inventoryContains(killer.field_71071_by, stack -> stack.func_77973_b() instanceof ItemShardPouch)
                           && vault.getActiveObjectives().stream().noneMatch(objective -> objective.shouldPauseTimer(sWorld.func_73046_m(), vault))) {
                           addedDrops |= addShardDrops(world, entity, killer, event.getDrops());
                        }
                     }

                     if (!addedDrops) {
                        event.setCanceled(true);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean shouldDropDefaultInVault(Entity entity) {
      return entity instanceof VaultGuardianEntity || entity instanceof TreasureGoblinEntity;
   }

   private static boolean addScavengerDrops(World world, Entity killed, VaultRaid vault, Collection<ItemEntity> drops) {
      Optional<ScavengerHuntObjective> objectiveOpt = vault.getActiveObjective(ScavengerHuntObjective.class);
      if (!objectiveOpt.isPresent()) {
         return false;
      } else {
         ScavengerHuntObjective objective = objectiveOpt.get();
         List<ScavengerHuntConfig.ItemEntry> specialDrops = ModConfigs.SCAVENGER_HUNT
            .generateMobDropLoot(objective.getGenerationDropFilter(), killed.func_200600_R());
         return specialDrops.isEmpty() ? false : vault.getProperties().getBase(VaultRaid.IDENTIFIER).map(identifier -> {
            specialDrops.forEach(entry -> {
               ItemStack stack = entry.createItemStack();
               if (!stack.func_190926_b()) {
                  BasicScavengerItem.setVaultIdentifier(stack, identifier);
                  ItemEntity itemEntity = new ItemEntity(world, killed.func_226277_ct_(), killed.func_226278_cu_(), killed.func_226281_cx_(), stack);
                  itemEntity.func_174869_p();
                  drops.add(itemEntity);
               }
            });
            return true;
         }).orElse(false);
      }
   }

   private static boolean addSubFighterDrops(World world, Entity killed, VaultRaid vault, Collection<ItemEntity> drops) {
      if (!(killed instanceof VaultFighterEntity)) {
         return false;
      } else {
         int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
         float shardChance = ModConfigs.LOOT_TABLES.getForLevel(level).getSubFighterRaffleChance();
         if (rand.nextFloat() >= shardChance) {
            return false;
         } else {
            String name = killed.getPersistentData().func_74779_i("VaultPlayerName");
            if (name.isEmpty()) {
               return false;
            } else {
               ItemStack raffleSeal = new ItemStack(ModItems.CRYSTAL_SEAL_RAFFLE);
               ItemVaultRaffleSeal.setPlayerName(raffleSeal, name);
               ItemEntity itemEntity = new ItemEntity(world, killed.func_226277_ct_(), killed.func_226278_cu_(), killed.func_226281_cx_(), raffleSeal);
               itemEntity.func_174869_p();
               drops.add(itemEntity);
               return true;
            }
         }
      }
   }

   private static boolean addShardDrops(World world, Entity killed, ServerPlayerEntity killer, Collection<ItemEntity> drops) {
      int shardCount = ModConfigs.SOUL_SHARD.getRandomShards(killed.func_200600_R());
      if (shardCount <= 0) {
         return false;
      } else {
         float additionalSoulShardChance = 0.0F;

         for (TalentNode<SoulShardTalent> node : PlayerTalentsData.get(killer.func_71121_q()).getTalents(killer).getLearnedNodes(SoulShardTalent.class)) {
            additionalSoulShardChance += node.getTalent().getAdditionalSoulShardChance();
         }

         float shShardCount = shardCount * (1.0F + additionalSoulShardChance);
         shardCount = MathHelper.func_76141_d(shShardCount);
         float decimal = shShardCount - shardCount;
         if (rand.nextFloat() < decimal) {
            shardCount++;
         }

         ItemStack shards = new ItemStack(ModItems.SOUL_SHARD, shardCount);
         ItemEntity itemEntity = new ItemEntity(world, killed.func_226277_ct_(), killed.func_226278_cu_(), killed.func_226281_cx_(), shards);
         itemEntity.func_174869_p();
         drops.add(itemEntity);
         return true;
      }
   }

   @SubscribeEvent
   public static void onEntitySpawn(CheckSpawn event) {
      if (event.getEntity().func_130014_f_().func_234923_W_() == Vault.VAULT_KEY && !event.isSpawner()) {
         event.setResult(Result.DENY);
      }
   }

   @SubscribeEvent
   public static void onDamageArmorHit(LivingDamageEvent event) {
      LivingEntity damaged = event.getEntityLiving();
      if (damaged instanceof PlayerEntity && !damaged.func_130014_f_().func_201670_d()) {
         PlayerEntity player = (PlayerEntity)damaged;
         Entity trueSrc = event.getSource().func_76346_g();
         if (trueSrc instanceof LivingEntity) {
            double chance = ((LivingEntity)trueSrc).func_233637_b_(ModAttributes.BREAK_ARMOR_CHANCE);

            while (chance > 0.0 && !(rand.nextFloat() > chance)) {
               chance--;
               player.field_71071_by.func_234563_a_(event.getSource(), 4.0F);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onCurseOnHit(LivingDamageEvent event) {
      if (event.getSource().func_76346_g() instanceof LivingEntity) {
         LivingEntity damaged = event.getEntityLiving();
         if (damaged instanceof ServerPlayerEntity) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)damaged;
            ServerWorld sWorld = sPlayer.func_71121_q();
            VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, sPlayer.func_233580_cy_());
            if (vault != null) {
               vault.getActiveModifiersFor(PlayerFilter.any(), CurseOnHitModifier.class).forEach(modifier -> modifier.applyCurse(sPlayer));
            }
         }
      }
   }

   @SubscribeEvent
   public static void onVaultGuardianDamage(LivingDamageEvent event) {
      LivingEntity entityLiving = event.getEntityLiving();
      if (!entityLiving.field_70170_p.field_72995_K) {
         if (entityLiving instanceof VaultGuardianEntity) {
            Entity trueSource = event.getSource().func_76346_g();
            if (trueSource instanceof LivingEntity) {
               LivingEntity attacker = (LivingEntity)trueSource;
               attacker.func_70097_a(DamageSource.func_92087_a(entityLiving), event.getAmount() * 0.2F);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHurtCrit(LivingHurtEvent event) {
      if (event.getSource().func_76346_g() instanceof LivingEntity) {
         LivingEntity source = (LivingEntity)event.getSource().func_76346_g();
         if (!source.field_70170_p.field_72995_K) {
            if (source.func_233645_dx_().func_233790_b_(ModAttributes.CRIT_CHANCE)) {
               double chance = source.func_233637_b_(ModAttributes.CRIT_CHANCE);
               if (source.func_233645_dx_().func_233790_b_(ModAttributes.CRIT_MULTIPLIER)) {
                  double multiplier = source.func_233637_b_(ModAttributes.CRIT_MULTIPLIER);
                  if (source.field_70170_p.field_73012_v.nextDouble() < chance) {
                     source.field_70170_p
                        .func_184148_a(
                           null,
                           source.func_226277_ct_(),
                           source.func_226278_cu_(),
                           source.func_226281_cx_(),
                           SoundEvents.field_187718_dS,
                           source.func_184176_by(),
                           1.0F,
                           1.0F
                        );
                     event.setAmount((float)(event.getAmount() * multiplier));
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHurtTp(LivingHurtEvent event) {
      if (!event.getEntityLiving().field_70170_p.field_72995_K) {
         boolean direct = event.getSource().func_76364_f() == event.getSource().func_76346_g();
         if (direct && event.getEntityLiving().func_233645_dx_().func_233790_b_(ModAttributes.TP_CHANCE)) {
            double chance = event.getEntityLiving().func_233637_b_(ModAttributes.TP_CHANCE);
            if (event.getEntityLiving().func_233645_dx_().func_233790_b_(ModAttributes.TP_RANGE)) {
               double range = event.getEntityLiving().func_233637_b_(ModAttributes.TP_RANGE);
               if (event.getEntityLiving().field_70170_p.field_73012_v.nextDouble() < chance) {
                  for (int i = 0; i < 64; i++) {
                     if (teleportRandomly(event.getEntityLiving(), range)) {
                        event.getEntityLiving()
                           .field_70170_p
                           .func_184148_a(
                              null,
                              event.getEntityLiving().field_70169_q,
                              event.getEntityLiving().field_70167_r,
                              event.getEntityLiving().field_70166_s,
                              ModSounds.BOSS_TP_SFX,
                              event.getEntityLiving().func_184176_by(),
                              1.0F,
                              1.0F
                           );
                        event.setCanceled(true);
                        return;
                     }
                  }
               }
            }
         } else if (!direct && event.getEntityLiving().func_233645_dx_().func_233790_b_(ModAttributes.TP_INDIRECT_CHANCE)) {
            double chance = event.getEntityLiving().func_233637_b_(ModAttributes.TP_INDIRECT_CHANCE);
            if (event.getEntityLiving().func_233645_dx_().func_233790_b_(ModAttributes.TP_RANGE)) {
               double range = event.getEntityLiving().func_233637_b_(ModAttributes.TP_RANGE);
               if (event.getEntityLiving().field_70170_p.field_73012_v.nextDouble() < chance) {
                  for (int ix = 0; ix < 64; ix++) {
                     if (teleportRandomly(event.getEntityLiving(), range)) {
                        event.getEntityLiving()
                           .field_70170_p
                           .func_184148_a(
                              null,
                              event.getEntityLiving().field_70169_q,
                              event.getEntityLiving().field_70167_r,
                              event.getEntityLiving().field_70166_s,
                              ModSounds.BOSS_TP_SFX,
                              event.getEntityLiving().func_184176_by(),
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
      if (!entity.field_70170_p.func_201670_d() && entity.func_70089_S()) {
         double d0 = entity.func_226277_ct_() + (entity.field_70170_p.field_73012_v.nextDouble() - 0.5) * range * 2.0;
         double d1 = entity.func_226278_cu_() + (entity.field_70170_p.field_73012_v.nextInt((int)(range * 2.0)) - range);
         double d2 = entity.func_226281_cx_() + (entity.field_70170_p.field_73012_v.nextDouble() - 0.5) * range * 2.0;
         return entity.func_213373_a(d0, d1, d2, true);
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void onEntityDestroy(LivingDestroyBlockEvent event) {
      if (event.getState().func_177230_c() instanceof VaultDoorBlock) {
         event.setCanceled(true);
      }
   }
}
