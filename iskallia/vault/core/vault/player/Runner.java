package iskallia.vault.core.vault.player;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.NaturalSpawner;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.influence.Influences;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.vault.time.modifier.ClockModifier;
import iskallia.vault.core.vault.time.modifier.FruitExtension;
import iskallia.vault.core.vault.time.modifier.VoidFluidExtension;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.VaultExperienceTrinket;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.item.VaultDollItem;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.VaultJoinSnapshotData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class Runner extends Listener {
   public static final SupplierKey<Listener> KEY = SupplierKey.of("runner", Listener.class).with(Version.v1_0, Runner::new);
   public static final FieldRegistry FIELDS = Listener.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<NaturalSpawner> SPAWNER = FieldKey.of("spawner", NaturalSpawner.class)
      .with(Version.v1_0, CompoundAdapter.of(NaturalSpawner::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Influences> INFLUENCES = FieldKey.of("influences", Influences.class)
      .with(Version.v1_5, CompoundAdapter.of(Influences::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   public Runner() {
      this.set(SPAWNER, new NaturalSpawner());
      this.set(INFLUENCES, new Influences());
   }

   @Override
   public SupplierKey<Listener> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.PLAYER_INTERACT.register(this, event -> {
         if (event.getPlayer().level == world) {
            if (event.isCancelable()) {
               if (event.getPlayer().getUUID().equals(this.get(ID))) {
                  if (!event.getPlayer().isCreative()) {
                     if (ModConfigs.VAULT_GENERAL.isBlacklisted(event.getItemStack())) {
                        event.setCanceled(true);
                     }
                  }
               }
            }
         }
      });
      CommonEvents.PLAYER_INTERACT.register(this, event -> {
         if (event.getPlayer().level == world) {
            if (event.isCancelable()) {
               if (event.getPlayer().getUUID().equals(this.get(ID))) {
                  if (!event.getPlayer().isCreative()) {
                     if (ModConfigs.VAULT_GENERAL.isBlacklisted(event.getWorld().getBlockState(event.getPos()))) {
                        event.setCanceled(true);
                     }
                  }
               }
            }
         }
      });
      CommonEvents.ENTITY_PLACE.register(this, event -> {
         if (event.getEntity() instanceof ServerPlayer) {
            if (event.getEntity().level == world) {
               if (event.isCancelable()) {
                  if (event.getEntity().getUUID().equals(this.get(ID))) {
                     if (!(event.getEntity() instanceof Player player && player.isCreative())) {
                        if (ModConfigs.VAULT_GENERAL.isBlacklisted(event.getWorld().getBlockState(event.getPos()))) {
                           event.setCanceled(true);
                        }
                     }
                  }
               }
            }
         }
      });
      CommonEvents.EFFECT_ADD
         .register(
            this,
            event -> {
               if (event.getEntity() instanceof ServerPlayer player) {
                  if (player.level == world) {
                     if (player.getUUID().equals(this.get(ID))) {
                        if (event.getPotionEffect().getEffect() == ModEffects.TIMER_ACCELERATION) {
                           for (ClockModifier modifier : vault.get(Vault.CLOCK).get(TickClock.MODIFIERS)) {
                              if (!modifier.has(ClockModifier.CONSUMED)
                                 && modifier instanceof VoidFluidExtension voidFluidExtension
                                 && voidFluidExtension.get(VoidFluidExtension.PLAYER).equals(this.get(ID))) {
                                 return;
                              }
                           }

                           vault.get(Vault.CLOCK).addModifier(new VoidFluidExtension(player));
                        }
                     }
                  }
               }
            }
         );
      CommonEvents.FRUIT_EATEN.register(this, data -> {
         if (data.getPlayer() instanceof ServerPlayer player) {
            if (player.level == world) {
               if (player.getUUID().equals(this.get(ID))) {
                  vault.get(Vault.CLOCK).addModifier(new FruitExtension(player, data.getTime()));
               }
            }
         }
      });
      CommonEvents.LOOT_GENERATION.pre().register(this, data -> {
         if (data.getGenerator() instanceof LootTableGenerator generator) {
            if (!(generator.source instanceof ServerPlayer player)) {
               return;
            }

            if (player.level != world) {
               return;
            }

            if (!player.getUUID().equals(this.get(ID))) {
               return;
            }

            int playerLevel = PlayerVaultStatsData.get(world).getVaultStats(this.getId()).getVaultLevel();
            int diff = playerLevel - vault.get(Vault.LEVEL).get() - 6;
            if (!player.getLevel().getGameRules().getBoolean(ModGameRules.BOOST_PENALTY)) {
               diff = 0;
            }

            if (diff <= 0) {
               return;
            }

            generator.itemQuantity -= diff * 0.05F;
            generator.itemQuantity = Math.max(generator.itemQuantity, -0.8F);
         }
      }, -100);
      CommonEvents.SOUL_SHARD_CHANCE.register(this, data -> {
         if (data.getKiller().level == world) {
            if (data.getKiller().getUUID().equals(this.get(ID))) {
               int playerLevel = PlayerVaultStatsData.get(world).getVaultStats(this.getId()).getVaultLevel();
               int diff = playerLevel - vault.get(Vault.LEVEL).get() - 6;
               if (!data.getKiller().getLevel().getGameRules().getBoolean(ModGameRules.BOOST_PENALTY)) {
                  diff = 0;
               }

               if (diff > 0) {
                  data.setChance(data.getChance() - diff * 0.05F);
               }
            }
         }
      }, -100);
      CommonEvents.PLAYER_STAT.of(PlayerStat.DURABILITY_WEAR_REDUCTION).register(this, data -> {
         if (data.getEntity() instanceof Player player) {
            if (player.level == world) {
               if (player.getUUID().equals(this.get(ID))) {
                  int playerLevel = PlayerVaultStatsData.get(world).getVaultStats(this.getId()).getVaultLevel();
                  int diff = playerLevel - vault.get(Vault.LEVEL).get() - 6;
                  if (!player.getLevel().getGameRules().getBoolean(ModGameRules.BOOST_PENALTY)) {
                     diff = 0;
                  }

                  if (diff > 0) {
                     float reduction = Math.min(0.25F + diff * 0.05F, 1.0F);
                     data.setValue(data.getValue() + reduction);
                  }
               }
            }
         }
      });
      CommonEvents.ENTITY_DEATH
         .register(
            this,
            event -> {
               if (event.getEntity().level == world) {
                  if (event.getEntity().getUUID().equals(this.getId())) {
                     if (event.getEntity().getTags().contains("soul_shards")) {
                        this.getPlayer()
                           .ifPresent(player -> BottleItem.getActive(vault, player).ifPresent(stack -> BottleItem.onMobKill(stack, player, event.getEntity())));
                     }
                  }
               }
            }
         );
      CommonEvents.CHEST_LOOT_GENERATION
         .post()
         .register(
            this,
            event -> {
               if (event.getTileEntity().getLevel() == world) {
                  if (event.getPlayer().getUUID().equals(this.getId())) {
                     this.getPlayer()
                        .ifPresent(player -> BottleItem.getActive(vault, player).ifPresent(stack -> BottleItem.onChestOpen(stack, player, event.getState())));
                  }
               }
            }
         );
      this.ifPresent(INFLUENCES, influences -> influences.initServer(world, vault, this));
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      super.tickServer(world, vault);
      this.ifPresent(SPAWNER, spawner -> {
         if (!vault.has(Vault.CLOCK) || vault.get(Vault.CLOCK).get(TickClock.LOGICAL_TIME) >= 300) {
            int vaultLevel = vault.get(Vault.LEVEL).get();
            NaturalSpawner.Config config = ModConfigs.VAULT_MOBS.getForLevel(vaultLevel).SPAWNER;
            spawner.setConfig(config).tickServer(world, vault, this);
         }
      });
      this.ifPresent(INFLUENCES, influences -> influences.tickServer(world, vault, this));
      this.getPlayer().flatMap(player -> BottleItem.getActive(vault, player)).ifPresent(BottleItem::onTimeTick);
   }

   @Override
   public void releaseServer() {
      super.releaseServer();
      this.ifPresent(INFLUENCES, Influences::releaseServer);
   }

   @Override
   public void onJoin(VirtualWorld world, Vault vault) {
      super.onJoin(world, vault);
      this.getPlayer().ifPresent(player -> {
         VaultJoinSnapshotData.get(player.getLevel()).createSnapshot(player);
         VaultDollItem.markDollOnVaultJoin(world, player, vault.get(Vault.ID));

         for (TrinketHelper.TrinketStack<VaultExperienceTrinket> trinketStack : TrinketHelper.getTrinkets(player, VaultExperienceTrinket.class)) {
            if (trinketStack.isUsable(player)) {
               vault.getOptional(Vault.STATS).map(s -> s.get(player.getUUID())).ifPresent(stats -> {
                  float multiplier = 1.0F + trinketStack.trinket().getConfig().getExperienceIncrease();
                  stats.modify(StatCollector.OBJECTIVE_EXP_MULTIPLIER, m -> m * multiplier);
                  stats.modify(StatCollector.BONUS_EXP_MULTIPLIER, m -> m * multiplier);
               });
            }
         }

         PlayerVaultStats playerStats = PlayerVaultStatsData.get(world).getVaultStats(player);
         int vaultLevel = vault.get(Vault.LEVEL).get();
         int playerLevel = playerStats.getVaultLevel();
         int diff = !player.getLevel().getGameRules().getBoolean(ModGameRules.BOOST_PENALTY) ? 0 : playerLevel - vaultLevel - 3;
         if (diff > 0) {
            vault.getOptional(Vault.STATS).map(s -> s.get(player.getUUID())).ifPresent(stats -> {
               stats.modify(StatCollector.OBJECTIVE_EXP_MULTIPLIER, m -> Math.max(0.0F, m - 0.1F * diff));
               stats.modify(StatCollector.BONUS_EXP_MULTIPLIER, m -> Math.max(0.0F, m - 0.1F * diff));
            });
         }

         BottleItem.setActive(vault, player);
      });
   }

   @Override
   public void onLeave(VirtualWorld world, Vault vault) {
      super.onLeave(world, vault);
      this.ifPresent(INFLUENCES, influences -> influences.onLeave(world, vault, this));
      this.getPlayer().ifPresent(InventoryUtil::makeItemsRotten);
   }
}
