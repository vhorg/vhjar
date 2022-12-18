package iskallia.vault.core.vault.player;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.NaturalSpawner;
import iskallia.vault.core.vault.Vault;
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
import iskallia.vault.item.VaultDollItem;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.VaultJoinSnapshotData;
import net.minecraft.server.level.ServerPlayer;

public class Runner extends Listener {
   public static final SupplierKey<Listener> KEY = SupplierKey.of("runner", Listener.class).with(Version.v1_0, Runner::new);
   public static final FieldRegistry FIELDS = Listener.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<NaturalSpawner> SPAWNER = FieldKey.of("spawner", NaturalSpawner.class)
      .with(Version.v1_0, Adapter.ofCompound(NaturalSpawner::new), DISK.all())
      .register(FIELDS);

   public Runner() {
      this.set(SPAWNER, new NaturalSpawner());
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
                  if (ModConfigs.VAULT_GENERAL.isBlacklisted(event.getItemStack())) {
                     event.setCanceled(true);
                  }
               }
            }
         }
      });
      CommonEvents.PLAYER_INTERACT.register(this, event -> {
         if (event.getPlayer().level == world) {
            if (event.isCancelable()) {
               if (event.getPlayer().getUUID().equals(this.get(ID))) {
                  if (ModConfigs.VAULT_GENERAL.isBlacklisted(event.getWorld().getBlockState(event.getPos()))) {
                     event.setCanceled(true);
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
                     if (ModConfigs.VAULT_GENERAL.isBlacklisted(event.getWorld().getBlockState(event.getPos()))) {
                        event.setCanceled(true);
                     }
                  }
               }
            }
         }
      });
      CommonEvents.EFFECT_ADDED
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
   }

   @Override
   public void onJoin(VirtualWorld world, Vault vault) {
      super.onJoin(world, vault);
      this.getPlayer()
         .ifPresent(
            player -> {
               VaultJoinSnapshotData.get(player.getLevel()).createSnapshot(player);
               VaultDollItem.markDollOnVaultJoin(world, player, vault.get(Vault.ID));

               for (TrinketHelper.TrinketStack<VaultExperienceTrinket> trinketStack : TrinketHelper.getTrinkets(player, VaultExperienceTrinket.class)) {
                  vault.getOptional(Vault.STATS)
                     .map(s -> s.get(player.getUUID()))
                     .ifPresent(
                        stats -> stats.modify(StatCollector.EXP_MULTIPLIER, m -> m * (1.0F + trinketStack.trinket().getConfig().getExperienceIncrease()))
                     );
               }

               PlayerVaultStats playerStats = PlayerVaultStatsData.get(world).getVaultStats(player);
               int vaultLevel = vault.get(Vault.LEVEL).get();
               int playerLevel = playerStats.getVaultLevel();
               int diff = playerLevel - vaultLevel - 3;
               if (diff > 0) {
                  vault.getOptional(Vault.STATS)
                     .map(s -> s.get(player.getUUID()))
                     .ifPresent(stats -> stats.modify(StatCollector.EXP_MULTIPLIER, m -> Math.max(0.0F, m - 0.1F * diff)));
               }
            }
         );
   }
}
