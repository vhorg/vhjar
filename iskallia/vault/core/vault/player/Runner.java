package iskallia.vault.core.vault.player;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.compound.ItemStackList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.NaturalSpawner;
import iskallia.vault.core.vault.ScheduledModifiers;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.influence.Influences;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.vault.time.modifier.ClockModifier;
import iskallia.vault.core.vault.time.modifier.FruitExtension;
import iskallia.vault.core.vault.time.modifier.VoidFluidExtension;
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
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.VaultJoinSnapshotData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
   public static final FieldKey<ScheduledModifiers> SCHEDULED_MODIFIERS = FieldKey.of("scheduled_modifiers", ScheduledModifiers.class)
      .with(Version.v1_28, Adapters.of(ScheduledModifiers::new, true), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<ItemStackList> ADDITIONAL_CRATE_ITEMS = FieldKey.of("additional_crate_items", ItemStackList.class)
      .with(Version.v1_29, CompoundAdapter.of(ItemStackList::create), DISK.all())
      .register(FIELDS);

   public Runner() {
      this.set(SPAWNER, new NaturalSpawner());
      this.set(INFLUENCES, new Influences());
      this.set(SCHEDULED_MODIFIERS, new ScheduledModifiers());
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
      CommonEvents.ENTITY_DEATH
         .register(
            this,
            event -> {
               if (event.getEntity().level == world) {
                  Entity source = event.getSource().getEntity();
                  if (source != null && source.getUUID().equals(this.getId())) {
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
      this.getPlayer().ifPresent(player -> this.ifPresent(SCHEDULED_MODIFIERS, scheduledModifiers -> scheduledModifiers.onTick(world, vault, player)));
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
                  float multiplierx = 1.0F + trinketStack.trinket().getConfig().getExperienceIncrease();
                  stats.modify(StatCollector.OBJECTIVE_EXP_MULTIPLIER, m -> m * multiplierx);
                  stats.modify(StatCollector.BONUS_EXP_MULTIPLIER, m -> m * multiplierx);
               });
            }
         }

         if (player.getLevel().getGameRules().getBoolean(ModGameRules.BOOST_PENALTY)) {
            PlayerVaultStats playerStats = PlayerVaultStatsData.get(world).getVaultStats(player);
            int vaultLevel = vault.get(Vault.LEVEL).get();
            int playerLevel = playerStats.getVaultLevel();
            int delta = playerLevel - vaultLevel - 1;
            if (delta > 0) {
               float multiplier = Math.max(0.0F, 1.0F - delta * 0.2F);
               vault.getOptional(Vault.STATS).map(s -> s.get(player.getUUID())).ifPresent(stats -> {
                  stats.modify(StatCollector.OBJECTIVE_EXP_MULTIPLIER, m -> m * multiplier);
                  stats.modify(StatCollector.BONUS_EXP_MULTIPLIER, m -> m * multiplier);
               });
            }
         }

         BottleItem.setActive(vault, player);
         this.ifPresent(SCHEDULED_MODIFIERS, scheduledModifiers -> scheduledModifiers.onJoin(vault, player));
      });
   }

   @Override
   public void onLeave(VirtualWorld world, Vault vault) {
      super.onLeave(world, vault);
      this.ifPresent(INFLUENCES, influences -> influences.onLeave(world, vault, this));
      this.getPlayer().ifPresent(InventoryUtil::makeItemsRotten);
   }
}
