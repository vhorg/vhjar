package iskallia.vault.event;

import iskallia.vault.block.TreasureDoorBlock;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.gear.FocusItem;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.item.gear.VaultShieldItem;
import iskallia.vault.item.gear.WandItem;
import iskallia.vault.network.message.ClientboundPlayerLastDamageSourceMessage;
import iskallia.vault.network.message.MobCritParticleMessage;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import iskallia.vault.world.data.DiscoveredModelsData;
import iskallia.vault.world.data.ServerVaults;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Fox.Type;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class EntityEvents {
   @SubscribeEvent
   public static void onTradesLoad(VillagerTradesEvent event) {
      ObjectIterator var1 = event.getTrades().values().iterator();

      while (var1.hasNext()) {
         List<ItemListing> trades = (List<ItemListing>)var1.next();
         trades.removeIf(trade -> {
            try {
               MerchantOffer offer = trade.getOffer(null, new Random());
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
               if (!ServerVaults.get(world).isEmpty() || !(offHand.getItem() instanceof VaultGearItem)) {
                  if (offHand.getItem() instanceof IdolItem
                     || offHand.getItem() instanceof VaultShieldItem
                     || offHand.getItem() instanceof WandItem
                     || offHand.getItem() instanceof FocusItem) {
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
   public static void onDamageArmorHit(LivingDamageEvent event) {
      LivingEntity damaged = event.getEntityLiving();
      if (damaged instanceof Player player && !damaged.getCommandSenderWorld().isClientSide()) {
         Entity trueSrc = event.getSource().getEntity();
         if (trueSrc instanceof LivingEntity) {
            double chance = ((LivingEntity)trueSrc).getAttributeValue(ModAttributes.BREAK_ARMOR_CHANCE);

            while (chance > 0.0 && !(damaged.getLevel().getRandom().nextFloat() > chance)) {
               chance--;
               player.getInventory().hurtArmor(event.getSource(), 4.0F, Inventory.ALL_ARMOR_SLOTS);
            }
         }
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
                  if (!(attacked.getLevel().getRandom().nextFloat() >= chance)) {
                     float multiplier = (float)attacker.getAttributeValue(ModAttributes.CRIT_MULTIPLIER);
                     if (AttributeSnapshotHelper.canHaveSnapshot(attacked) && multiplier > 1.0F) {
                        AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacked);
                        float mitigation = snapshot.getAttributeValue(ModGearAttributes.CRITICAL_HIT_TAKEN_REDUCTION, VaultGearAttributeTypeMerger.floatSum());
                        float multiplierPart = multiplier - 1.0F;
                        multiplierPart = Math.max(multiplierPart - multiplierPart * mitigation, 0.0F);
                        multiplier = multiplierPart + 1.0F;
                     }

                     ModNetwork.CHANNEL
                        .send(
                           PacketDistributor.ALL.noArg(),
                           new MobCritParticleMessage(
                              new Vec3(event.getEntity().getX(), event.getEntity().getY() + event.getEntity().getBbHeight(), event.getEntity().getZ())
                           )
                        );
                     attacker.level
                        .playSound(
                           null,
                           attacker.getX(),
                           attacker.getY(),
                           attacker.getZ(),
                           ModSounds.MOB_CRIT,
                           attacker.getSoundSource(),
                           1.2F,
                           new Random().nextFloat() * 0.5F + 0.5F
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

   @SubscribeEvent
   public static void breedSnowFox(BabyEntitySpawnEvent event) {
      if (event.getParentA() instanceof Fox foxA && event.getParentB() instanceof Fox foxB && foxA.getFoxType() == Type.SNOW && foxB.getFoxType() == Type.SNOW) {
         ServerPlayer player = (ServerPlayer)event.getCausedByPlayer();
         DiscoveredModelsData discoveredModelsData = DiscoveredModelsData.get((ServerLevel)player.level);
         ResourceLocation modelId = ModDynamicModels.Armor.SILENTFOXXY.getId();
         if (!discoveredModelsData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
            MutableComponent info = new TextComponent("You have bred a snowfox!!!").withStyle(ChatFormatting.GOLD);
            player.sendMessage(info, Util.NIL_UUID);
            discoveredModelsData.discoverAllArmorPieceAndBroadcast(player, ModDynamicModels.Armor.SILENTFOXXY);
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
               Random rand = attacked.getLevel().getRandom();
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

   @SubscribeEvent
   public static void onEntityDestroy(LivingDestroyBlockEvent event) {
      if (event.getState().getBlock() instanceof TreasureDoorBlock) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onCheckSpawn(CheckSpawn event) {
      ResourceLocation id = event.getEntityLiving().getType().getRegistryName();
      if (id != null && id.getNamespace().equals("rottencreatures")) {
         event.setResult(Result.DENY);
      }
   }
}
