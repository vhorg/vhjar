package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.stat.StatsCollector;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.BottleItem;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.world.data.PhoenixModifierSnapshotData;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerInventoryRestoreModifier extends VaultModifier<PlayerInventoryRestoreModifier.Properties> {
   private static final String RESTORE_FLAG = "the_vault_restore_inventory";

   public PlayerInventoryRestoreModifier(ResourceLocation id, PlayerInventoryRestoreModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      Player player = event.player;
      if (player.isAlive() && player.level instanceof ServerLevel) {
         if (player.getTags().contains("the_vault_restore_inventory")) {
            ServerLevel world = (ServerLevel)event.player.level;
            PhoenixModifierSnapshotData data = PhoenixModifierSnapshotData.get(world);
            if (data.hasSnapshot(player)) {
               data.restoreSnapshot(player);
            }

            player.removeTag("the_vault_restore_inventory");
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onDeath(LivingDeathEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayer sPlayer) {
         ServerVaults.get(sPlayer.getLevel())
            .ifPresent(
               vault -> {
                  InventoryUtil.makeScavItemsRotten(sPlayer);
                  if (vault.has(Vault.MODIFIERS)) {
                     boolean hasRestoreModifier = false;
                     PlayerInventoryRestoreModifier instantRestoreModifier = null;

                     for (VaultModifier<?> modifier : vault.get(Vault.MODIFIERS).getModifiers()) {
                        if (modifier instanceof PlayerInventoryRestoreModifier restoreModifier) {
                           hasRestoreModifier = true;
                           if (restoreModifier.properties().isInstantRevival()) {
                              instantRestoreModifier = restoreModifier;
                              break;
                           }
                        }
                     }

                     if (instantRestoreModifier != null && !event.getSource().isBypassInvul()) {
                        ModifierContext actualContext = vault.get(Vault.MODIFIERS).getContext(instantRestoreModifier);
                        if (actualContext != null) {
                           sPlayer.setHealth(sPlayer.getMaxHealth());
                           sPlayer.removeAllEffects();
                           sPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 1));
                           sPlayer.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
                           sPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 1));
                           sPlayer.getLevel().playSound(null, sPlayer.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                           event.setCanceled(true);
                           actualContext.setExpired();
                           return;
                        }
                     }

                     if (hasRestoreModifier) {
                        if (vault.has(Vault.STATS)) {
                           StatsCollector statsCollector = vault.get(Vault.STATS);
                           StatCollector playerStatCollector = statsCollector.get(sPlayer.getUUID());
                           if (playerStatCollector != null) {
                              int experience = ModConfigs.VAULT_STATS.getStatsExperience(playerStatCollector);
                              int durabilityExperience = experience - ModConfigs.VAULT_STATS.getFreeExperienceNotDealtAsDurabilityDamage();
                              int durabilityDamage = (int)(durabilityExperience * ModConfigs.VAULT_STATS.getPercentOfExperienceDealtAsDurabilityDamage());
                              if (durabilityDamage > 0) {
                                 InventoryUtil.findAllItems(sPlayer)
                                    .forEach(
                                       itemAccess -> {
                                          ItemStack foundStack = itemAccess.getStack();
                                          if (foundStack.getItem() instanceof VaultGearItem
                                             && foundStack.isDamageableItem()
                                             && !(foundStack.getItem() instanceof BottleItem)
                                             && !VaultGearData.read(foundStack).get(ModGearAttributes.SOULBOUND, VaultGearAttributeTypeMerger.anyTrue())) {
                                             foundStack.hurtAndBreak(durabilityDamage, sPlayer, pl -> pl.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                                             itemAccess.setStack(foundStack);
                                          }
                                       }
                                    );
                              }
                           }
                        }
                     }
                  }
               }
            );
      }
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.ENTITY_DEATH.register(context.getUUID(), event -> {
         if (!event.isCanceled()) {
            if (event.getEntityLiving() instanceof ServerPlayer player) {
               if (player.getLevel().equals(world)) {
                  if (!this.properties().isInstantRevival()) {
                     PhoenixModifierSnapshotData snapshotData = PhoenixModifierSnapshotData.get(player.getLevel());
                     if (snapshotData.hasSnapshot(player)) {
                        snapshotData.removeSnapshot(player);
                     }

                     snapshotData.createSnapshot(player);
                     player.getTags().add("the_vault_restore_inventory");
                  }
               }
            }
         }
      });
   }

   @Override
   public void onListenerAdd(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
      vault.getOptional(Vault.STATS).map(stats -> stats.get(listener)).ifPresent(stats -> {
         stats.modify(StatCollector.OBJECTIVE_EXP_MULTIPLIER, m -> m * this.properties().experienceMultiplierOnSuccess());
         stats.modify(StatCollector.BONUS_EXP_MULTIPLIER, m -> m * this.properties().experienceMultiplierOnSuccess());
      });
   }

   @Override
   public void onListenerRemove(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
      vault.ifPresent(Vault.STATS, stats -> {
         StatCollector statCollector = stats.get(listener);
         if (statCollector != null && statCollector.getCompletion() == Completion.FAILED) {
            statCollector.modify(StatCollector.OBJECTIVE_EXP_MULTIPLIER, m -> m * this.properties.experienceMultiplierOnDeath());
            statCollector.modify(StatCollector.BONUS_EXP_MULTIPLIER, m -> m * this.properties.experienceMultiplierOnDeath());
         }
      });
   }

   public static class Properties {
      @Expose
      private final boolean preventsArtifact;
      @Expose
      private final float experienceMultiplierOnDeath;
      @Expose
      private final float experienceMultiplierOnSuccess;
      @Expose
      private final boolean isInstantRevival;

      public Properties(boolean preventsArtifact, float experienceMultiplierOnDeath, float experienceMultiplierOnSuccess, boolean isInstantRevival) {
         this.preventsArtifact = preventsArtifact;
         this.experienceMultiplierOnDeath = experienceMultiplierOnDeath;
         this.experienceMultiplierOnSuccess = experienceMultiplierOnSuccess;
         this.isInstantRevival = isInstantRevival;
      }

      public boolean preventsArtifact() {
         return this.preventsArtifact;
      }

      public float experienceMultiplierOnDeath() {
         return this.experienceMultiplierOnDeath;
      }

      public float experienceMultiplierOnSuccess() {
         return this.experienceMultiplierOnSuccess;
      }

      public boolean isInstantRevival() {
         return this.isInstantRevival;
      }
   }
}
