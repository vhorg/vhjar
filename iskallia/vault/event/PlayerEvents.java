package iskallia.vault.event;

import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.entity.FighterEntity;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.DamageImmunityTrinket;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.AnimalJarItem;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.mana.Mana;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.network.message.InvalidConfigsMessage;
import iskallia.vault.util.AdvancementHelper;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.VaultCharmData;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkDirection;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerEvents {
   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      Entity target = event.getTarget();
      if (!target.level.isClientSide) {
         ServerPlayer player = (ServerPlayer)event.getPlayer();
         if (target instanceof FighterEntity) {
            ModNetwork.CHANNEL
               .sendTo(new FighterSizeMessage(target, ((FighterEntity)target).sizeMultiplier), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         }

         if (target instanceof EternalEntity) {
            ModNetwork.CHANNEL
               .sendTo(new FighterSizeMessage(target, ((EternalEntity)target).sizeMultiplier), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         }
      }
   }

   @SubscribeEvent
   public static void onAttack(AttackEntityEvent event) {
      Player attacker = event.getPlayer();
      if (!attacker.level.isClientSide()) {
         int playerLevel = PlayerVaultStatsData.get((ServerLevel)attacker.level).getVaultStats(attacker).getVaultLevel();
         ItemStack stack = attacker.getMainHandItem();
         if (stack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(stack);
            if (playerLevel < data.getItemLevel()) {
               event.setCanceled(true);
               return;
            }
         }

         if (event.getTarget() instanceof LivingEntity target) {
            EntityHelper.getNearby(attacker.level, attacker.blockPosition(), 9.0F, EternalEntity.class).forEach(eternal -> eternal.setTarget(target));
         }
      }
   }

   @SubscribeEvent
   public static void customTrinketCurioEquip(CurioEquipEvent event) {
      ItemStack stack = event.getStack();
      if (stack.getItem() instanceof TrinketItem trinketItem) {
         if (trinketItem.canEquip(event.getSlotContext(), stack)) {
            event.setResult(Result.ALLOW);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick2(PlayerTickEvent event) {
      Player player = event.player;
      if (player.isOnFire()) {
         if (player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            player.clearFire();
         }

         if (TrinketHelper.getTrinkets(player, DamageImmunityTrinket.class)
            .stream()
            .filter(trinket -> trinket.isUsable(player))
            .map(TrinketHelper.TrinketStack::trinket)
            .anyMatch(DamageImmunityTrinket::isFireDamage)) {
            player.clearFire();
         }
      }

      if (player.getHealth() > player.getMaxHealth()) {
         player.setHealth(player.getMaxHealth());
         if (player instanceof ServerPlayer sPlayer) {
            sPlayer.lastSentHealth = player.getHealth();
         }
      }
   }

   @SubscribeEvent
   public static void itemInteractionForEntity(EntityInteract event) {
      if (!event.getWorld().isClientSide() && event.getTarget() instanceof Animal && !(event.getTarget() instanceof Player)) {
         Player player = event.getPlayer();
         if (player.isCrouching() && player.getItemInHand(event.getHand()).getItem() == ModItems.ANIMAL_JAR && event.getTarget() instanceof Animal animal) {
            if (!AnimalJarItem.canAddEntity(event.getItemStack(), animal)) {
               player.displayClientMessage(new TextComponent("Cannot add to jar."), true);
               return;
            }

            if (animal.isBaby()) {
               player.displayClientMessage(new TextComponent("This animal is too small to fit in this jar.."), true);
               return;
            }

            if (event.getTarget() instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() != null) {
               player.displayClientMessage(new TextComponent("Cannot jar up tamed animals"), true);
               return;
            }

            if (event.getTarget() instanceof Horse horse && horse.isTamed()) {
               player.displayClientMessage(new TextComponent("Cannot jar up tamed animals"), true);
               return;
            }

            if (player.getItemInHand(event.getHand()).getCount() > 1) {
               ItemStack jar = event.getItemStack().copy();
               event.getItemStack().shrink(1);
               jar.setCount(1);
               ItemStack output = AnimalJarItem.AddEntity(jar, animal);
               player.getInventory().add(output);
            } else {
               AnimalJarItem.AddEntity(event.getItemStack(), animal);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void fillLootOnBreak(BreakEvent event) {
      if (!event.getWorld().isClientSide() && event.getWorld() instanceof ServerLevel) {
         BlockEntity tile = event.getWorld().getBlockEntity(event.getPos());
         if (tile instanceof RandomizableContainerBlockEntity) {
            if (tile instanceof VaultChestTileEntity) {
               ((VaultChestTileEntity)tile).generateChestLoot(event.getPlayer(), true);
            } else {
               ((RandomizableContainerBlockEntity)tile).unpackLootTable(event.getPlayer());
            }
         }

         if (tile instanceof VaultChestTileEntity) {
            Random rand = event.getWorld().getRandom();
            VaultRarity rarity = ((VaultChestTileEntity)tile).getRarity();
            if (rarity == VaultRarity.EPIC) {
               event.getWorld().playSound(null, event.getPos(), ModSounds.VAULT_CHEST_EPIC_OPEN, SoundSource.BLOCKS, 0.5F, rand.nextFloat() * 0.1F + 0.9F);
            } else if (rarity == VaultRarity.OMEGA) {
               event.getWorld().playSound(null, event.getPos(), ModSounds.VAULT_CHEST_OMEGA_OPEN, SoundSource.BLOCKS, 0.5F, rand.nextFloat() * 0.1F + 0.9F);
            } else if (rarity == VaultRarity.RARE) {
               event.getWorld().playSound(null, event.getPos(), ModSounds.VAULT_CHEST_RARE_OPEN, SoundSource.BLOCKS, 0.5F, rand.nextFloat() * 0.1F + 0.9F);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void hideIdolRegistryName(ItemTooltipEvent event) {
      if (Minecraft.getInstance().player == null || !Minecraft.getInstance().player.isCreative()) {
         for (int i = 0; i < event.getToolTip().size(); i++) {
            Component txt = (Component)event.getToolTip().get(i);
            if (txt.getString().contains("the_vault:idol")) {
               event.getToolTip().set(i, new TextComponent("the_vault:idol").setStyle(txt.getStyle()));
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerEnterVault(PlayerChangedDimensionEvent event) {
      if (event.getPlayer() instanceof ServerPlayer serverPlayer && ServerVaults.get(serverPlayer.level).isPresent()) {
         AdvancementHelper.grantCriterion(serverPlayer, VaultMod.id("main/root"), "entered_vault");
         AdvancementHelper.grantCriterion(serverPlayer, VaultMod.id("armors/root"), "entered_vault");
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onVaultCharmUse(EntityItemPickupEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         ItemEntity itemEntity = event.getItem();
         ItemStack stack = itemEntity.getItem();
         if (!stack.isEmpty()) {
            ServerLevel world = player.getLevel();
            if (!ServerVaults.get(world).isEmpty()) {
               if (hasVaultCharm(player.getInventory())) {
                  List<ResourceLocation> whitelist = VaultCharmData.get(world).getWhitelistedItems(player);
                  if (whitelist.contains(stack.getItem().getRegistryName())) {
                     event.setCanceled(true);
                     itemEntity.remove(RemovalReason.DISCARDED);
                     world.playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.ITEM_PICKUP,
                        SoundSource.PLAYERS,
                        0.2F,
                        (world.random.nextFloat() - world.random.nextFloat()) * 1.4F + 2.0F
                     );
                  }
               }
            }
         }
      }
   }

   private static boolean hasVaultCharm(Inventory inventory) {
      for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
         ItemStack stack = inventory.getItem(slot);
         if (!stack.isEmpty() && stack.getItem() == ModItems.VAULT_CHARM) {
            return true;
         }
      }

      return false;
   }

   @SubscribeEvent
   public static void onManaRegen(PlayerTickEvent event) {
      if (event.side == LogicalSide.SERVER && event.phase == Phase.START) {
         event.player.getAttribute(ModAttributes.MANA_MAX).setBaseValue(ModConfigs.MANA.getManaMax());
         event.player.getAttribute(ModAttributes.MANA_REGEN).setBaseValue(ModConfigs.MANA.getManaRegenPerSecond());
         Mana.increase(event.player, Mana.getRegenPerSecond(event.player) * 0.05F);
      }
   }

   @SubscribeEvent
   public static void onInvalidConfigs(PlayerLoggedInEvent event) {
      if (!ModConfigs.INVALID_CONFIGS.isEmpty()) {
         if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            ModNetwork.CHANNEL
               .sendTo(new InvalidConfigsMessage(ModConfigs.INVALID_CONFIGS), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onPlayerDrops(LivingDropsEvent event) {
      if (event.getEntity() instanceof Player player && ServerVaults.get(player.level).isPresent()) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void on(LivingDeathEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         PlayerAbilitiesData.deactivateAllAbilities(player);
      }
   }

   @SubscribeEvent
   public static void on(Clone event) {
      if (event.getPlayer() instanceof ServerPlayer player && !event.isWasDeath()) {
         Mana.set(player, Mana.get(event.getOriginal()));
      }
   }
}
