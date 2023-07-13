package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.entity.EntityScaler;
import iskallia.vault.entity.Targeting;
import iskallia.vault.entity.entity.EffectCloudEntity;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.entity.MonsterEyeEntity;
import iskallia.vault.gear.attribute.custom.EffectCloudAttribute;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.calc.SoulChanceHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.Event.Result;

public class ClassicMobLogic extends MobLogic {
   public static final String SOUL_SHARDS = "soul_shards";
   public static final SupplierKey<MobLogic> KEY = SupplierKey.of("classic", MobLogic.class).with(Version.v1_0, ClassicMobLogic::new);
   public static final FieldRegistry FIELDS = MobLogic.FIELDS.merge(new FieldRegistry());

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public SupplierKey<MobLogic> getKey() {
      return KEY;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_CHECK_SPAWN.register(this, event -> {
         if (event.getEntity().level == world && !event.isSpawner()) {
            event.setResult(Result.DENY);
         }
      });
      CommonEvents.ENTITY_TICK
         .register(
            this,
            event -> {
               if (event.getEntity().level == world) {
                  EntityScaler.scale(vault, event.getEntityLiving());
                  if (event.getEntityLiving() instanceof Mob mob) {
                     mob.setCanPickUpLoot(false);
                     if (!mob.hasEffect(ModEffects.TAUNT_CHARM) && mob.getRandom().nextInt(5) == 0) {
                        Player nearestPlayer = world.getNearestPlayer(
                           event.getEntity().getX(),
                           event.getEntity().getY(),
                           event.getEntity().getZ(),
                           48.0,
                           target -> Targeting.getTargetingResult(mob, target) != Targeting.TargetingResult.IGNORE
                        );
                        mob.setTarget(nearestPlayer);
                        mob.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, nearestPlayer);
                        mob.getBrain().setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, world.getGameTime());
                        mob.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, nearestPlayer);
                        mob.getBrain().setMemory(MemoryModuleType.UNIVERSAL_ANGER, true);
                        mob.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, new NearestVisibleLivingEntities(mob, new ArrayList()) {
                           public boolean contains(LivingEntity p_186108_) {
                              return true;
                           }

                           public boolean contains(Predicate<LivingEntity> p_186131_) {
                              return true;
                           }
                        });
                     }
                  }
               }
            }
         );
      CommonEvents.ENTITY_READ.register(this, data -> {
         if (data.getEntity().level == world) {
            if (data.getNbt() != null && data.getNbt().contains("Spawner", 10)) {
               CompoundTag manager = data.getNbt().getCompound("Spawner").getCompound("Manager");
               int counter = manager.getInt("WaveCounter");
               int usesLeft = manager.contains("UsesLeft") ? manager.getInt("UsesLeft") : -1;
               if (counter <= 0 || usesLeft >= 0) {
                  data.getEntity().getTags().add("soul_shards");
               }
            }
         }
      });
      CommonEvents.ENTITY_TICK.register(this, event -> {
         if (event.getEntity().level == world) {
            if (event.getEntity().getTags().contains("soul_shards")) {
               Entity entity = event.getEntity();
               if (entity.getLevel().getRandom().nextInt(4) <= 0) {
                  Vec3 at = MiscUtils.getRandomOffset(entity.getBoundingBox().inflate(0.2F), entity.getLevel().getRandom());
                  world.sendParticles((SimpleParticleType)ModParticles.PURPLE_FLAME.get(), at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
               }
            }
         }
      });
      CommonEvents.ENTITY_DROPS.register(this, EventPriority.HIGHEST, event -> {
         if (event.getEntity().level == world) {
            if (!(event.getEntity() instanceof Player)) {
               event.getDrops().clear();
               Entity killed = event.getEntity();
               Entity killer = event.getSource().getEntity();
               if (killed.getTags().contains("soul_shards")) {
                  if (killer instanceof EternalEntity eternal) {
                     killer = (Entity)eternal.getOwner().right().orElse(null);
                  }

                  if (killer instanceof ServerPlayer player) {
                     float chanceMultiplier = SoulChanceHelper.getSoulChance(player);
                     chanceMultiplier = CommonEvents.SOUL_SHARD_CHANCE.invoke(player, chanceMultiplier).getChance();
                     int shardCount = ModConfigs.SOUL_SHARD.getRandomShards(killed, 1.0F + chanceMultiplier);
                     if (shardCount > 0) {
                        ItemStack shards = new ItemStack(ModItems.SOUL_SHARD, shardCount);
                        ItemEntity item = new ItemEntity(world, killed.getX(), killed.getY(), killed.getZ(), shards);
                        item.setDefaultPickUpDelay();
                        event.getDrops().add(item);
                     }
                  }
               }
            }
         }
      });
      CommonEvents.ENTITY_DEATH.register(this, event -> {
         if (event.getEntity().level == world) {
            if (event.getEntityLiving() instanceof Slime slime) {
               if (!(slime instanceof MonsterEyeEntity)) {
                  int level = vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
                  ModConfigs.VAULT_ENTITIES.getSlimeEffectConfig(level).map(EffectCloudAttribute.EffectCloud::fromConfig).ifPresent(config -> {
                     if (!(world.getRandom().nextFloat() >= config.getTriggerChance())) {
                        EffectCloudEntity cloud = new EffectCloudEntity(slime.getLevel(), slime.getX(), slime.getY(), slime.getZ());
                        config.apply(cloud);
                        slime.getLevel().addFreshEntity(cloud);
                     }
                  });
               }
            }
         }
      });
      CommonEvents.ENTITY_JOIN.register(this, event -> {
         if (event.getWorld() == world) {
            if (!event.loadedFromDisk() && event.getEntity() instanceof ThrownPotion potion) {
               Entity thrower = potion.getOwner();
               if (thrower instanceof Witch) {
                  int level = vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
                  List<MobEffectInstance> configuredEffects = ModConfigs.VAULT_ENTITIES.getWitchAdditionalThrownEffects(level);
                  ItemStack thrown = potion.getItem();
                  List<MobEffectInstance> effects = new ArrayList<>(configuredEffects);
                  PotionUtils.setPotion(thrown, Potions.WATER);
                  PotionUtils.setCustomEffects(thrown, effects);
                  potion.setItem(thrown);
               }
            }
         }
      });
      CommonEvents.EFFECT_CHECK.register(this, event -> {
         if (event.getEntity().level == world) {
            if (event.getPotionEffect().getEffect() == MobEffects.POISON) {
               if (event.getEntityLiving().getMobType() == MobType.UNDEAD || event.getEntityLiving() instanceof Spider) {
                  event.setResult(Result.ALLOW);
               }
            }
         }
      });
      CommonEvents.ZOMBIE_REINFORCEMENT.register(this, event -> {
         if (event.getEntity().level == world) {
            event.setResult(Result.DENY);
         }
      });
   }
}
