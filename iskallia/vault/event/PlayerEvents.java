package iskallia.vault.event;

import iskallia.vault.Vault;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.gear.EtchingItem;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.skill.set.BloodSet;
import iskallia.vault.skill.set.DragonSet;
import iskallia.vault.skill.set.DreamSet;
import iskallia.vault.skill.set.DryadSet;
import iskallia.vault.skill.set.PlayerSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.util.AdvancementHelper;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.PlayerDamageHelper;
import iskallia.vault.util.SideOnlyFixer;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.VaultCharmData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerEvents {
   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      Entity target = event.getTarget();
      if (!target.field_70170_p.field_72995_K) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
         if (target instanceof FighterEntity) {
            ModNetwork.CHANNEL
               .sendTo(
                  new FighterSizeMessage(target, ((FighterEntity)target).sizeMultiplier), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT
               );
         }

         if (target instanceof EternalEntity) {
            ModNetwork.CHANNEL
               .sendTo(
                  new FighterSizeMessage(target, ((EternalEntity)target).sizeMultiplier), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT
               );
         }
      }
   }

   @SubscribeEvent
   public static void onAttack(AttackEntityEvent event) {
      PlayerEntity attacker = event.getPlayer();
      if (!attacker.field_70170_p.func_201670_d()) {
         int level = PlayerVaultStatsData.get((ServerWorld)attacker.field_70170_p).getVaultStats(attacker).getVaultLevel();
         ItemStack stack = attacker.func_184614_ca();
         if (ModAttributes.MIN_VAULT_LEVEL.exists(stack) && level < ModAttributes.MIN_VAULT_LEVEL.get(stack).get().getValue(stack)) {
            event.setCanceled(true);
         } else {
            if (event.getTarget() instanceof LivingEntity) {
               LivingEntity target = (LivingEntity)event.getTarget();
               EntityHelper.getNearby(attacker.field_70170_p, attacker.func_233580_cy_(), 9.0F, EternalEntity.class)
                  .forEach(eternal -> eternal.func_70624_b(target));
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerDamage(LivingHurtEvent event) {
      Entity target = event.getEntity();
      if (target instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)target;
         if (player.func_71121_q().func_234923_W_() == Vault.VAULT_KEY) {
            VaultRaid active = VaultRaidData.get(player.func_71121_q()).getActiveFor(player);
            if (active != null && active.isFinished()) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick2(PlayerTickEvent event) {
      if (event.player.func_70644_a(Effects.field_76426_n)) {
         event.player.func_70066_B();
      }

      if (!event.player.func_130014_f_().func_201670_d() && event.player instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.player;

         for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.func_188453_a().equals(Group.ARMOR)) {
               ItemStack stack = player.func_184582_a(slot);
               int level = PlayerVaultStatsData.get((ServerWorld)event.player.field_70170_p).getVaultStats(player).getVaultLevel();
               if (ModAttributes.MIN_VAULT_LEVEL.exists(stack) && level < ModAttributes.MIN_VAULT_LEVEL.get(stack).get().getValue(stack)) {
                  player.func_146097_a(stack.func_77946_l(), false, false);
                  stack.func_190920_e(0);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onApplyPlayerSets(PlayerTickEvent event) {
      if (!event.player.func_130014_f_().func_201670_d() && event.player instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.player;
         SetTree sets = PlayerSetsData.get(player.func_71121_q()).getSets(player);
         if (PlayerSet.isActive(VaultGear.Set.DRAGON, player) && !PlayerDamageHelper.getMultiplier(player, DragonSet.MULTIPLIER_ID).isPresent()) {
            float multiplier = 1.0F;

            for (SetNode<?> node : sets.getNodes()) {
               if (node.getSet() instanceof DragonSet) {
                  DragonSet set = (DragonSet)node.getSet();
                  multiplier += set.getDamageMultiplier();
               }
            }

            PlayerDamageHelper.applyMultiplier(
               DragonSet.MULTIPLIER_ID, (ServerPlayerEntity)event.player, multiplier, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY, false
            );
         } else if (!PlayerSet.isActive(VaultGear.Set.DRAGON, player)) {
            PlayerDamageHelper.removeMultiplier(player, DragonSet.MULTIPLIER_ID);
         }

         if (PlayerSet.isActive(VaultGear.Set.DREAM, player) && !PlayerDamageHelper.getMultiplier(player, DreamSet.MULTIPLIER_ID).isPresent()) {
            float multiplier = 1.0F;

            for (SetNode<?> nodex : sets.getNodes()) {
               if (nodex.getSet() instanceof DreamSet) {
                  DreamSet set = (DreamSet)nodex.getSet();
                  multiplier += set.getIncreasedDamage();
               }
            }

            PlayerDamageHelper.applyMultiplier(
               DreamSet.MULTIPLIER_ID, (ServerPlayerEntity)event.player, multiplier, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY, false
            );
         } else if (!PlayerSet.isActive(VaultGear.Set.DREAM, player)) {
            PlayerDamageHelper.removeMultiplier(player, DreamSet.MULTIPLIER_ID);
         }

         if (PlayerSet.isActive(VaultGear.Set.DRYAD, player)) {
            float health = 0.0F;

            for (SetNode<?> nodexx : sets.getNodes()) {
               if (nodexx.getSet() instanceof DryadSet) {
                  DryadSet set = (DryadSet)nodexx.getSet();
                  health += set.getExtraHealth();
               }
            }

            player.func_110148_a(Attributes.field_233818_a_)
               .func_233767_b_(new AttributeModifier(DryadSet.HEALTH_MODIFIER_ID, "Dryad Bonus Health", health, Operation.ADDITION));
         } else {
            player.func_110148_a(Attributes.field_233818_a_).func_188479_b(DryadSet.HEALTH_MODIFIER_ID);
         }

         if (PlayerSet.isActive(VaultGear.Set.BLOOD, player) && !PlayerDamageHelper.getMultiplier(player, BloodSet.MULTIPLIER_ID).isPresent()) {
            float multiplier = 0.0F;

            for (SetNode<?> nodexxx : sets.getNodes()) {
               if (nodexxx.getSet() instanceof BloodSet) {
                  BloodSet set = (BloodSet)nodexxx.getSet();
                  multiplier += set.getDamageMultiplier();
               }
            }

            PlayerDamageHelper.applyMultiplier(
               BloodSet.MULTIPLIER_ID, (ServerPlayerEntity)event.player, multiplier, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY
            );
         } else if (!PlayerSet.isActive(VaultGear.Set.BLOOD, player)) {
            PlayerDamageHelper.removeMultiplier(player, BloodSet.MULTIPLIER_ID);
         }
      }
   }

   @SubscribeEvent
   public static void onBlockBreak(BreakEvent event) {
      if (!event.getWorld().func_201670_d() && event.getWorld() instanceof ServerWorld) {
         TileEntity tile = event.getWorld().func_175625_s(event.getPos());
         if (tile instanceof LockableLootTileEntity) {
            if (tile instanceof VaultChestTileEntity) {
               ((VaultChestTileEntity)tile).generateChestLoot(event.getPlayer(), true);
            } else {
               ((LockableLootTileEntity)tile).func_184281_d(event.getPlayer());
            }
         }

         if (tile instanceof VaultChestTileEntity) {
            Random rand = event.getWorld().func_201674_k();
            VaultRarity rarity = ((VaultChestTileEntity)tile).getRarity();
            if (rarity == VaultRarity.EPIC) {
               event.getWorld()
                  .func_184133_a(null, event.getPos(), ModSounds.VAULT_CHEST_EPIC_OPEN, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.1F + 0.9F);
            } else if (rarity == VaultRarity.OMEGA) {
               event.getWorld()
                  .func_184133_a(null, event.getPos(), ModSounds.VAULT_CHEST_OMEGA_OPEN, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.1F + 0.9F);
            } else if (rarity == VaultRarity.RARE) {
               event.getWorld()
                  .func_184133_a(null, event.getPos(), ModSounds.VAULT_CHEST_RARE_OPEN, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.1F + 0.9F);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onCraftVaultgear(ItemCraftedEvent event) {
      PlayerEntity player = event.getPlayer();
      if (!player.func_130014_f_().func_201670_d()) {
         ItemStack crafted = event.getCrafting();
         if (crafted.func_77973_b() instanceof VaultGear) {
            if (!(crafted.func_77973_b() instanceof EtchingItem)) {
               int slot = SideOnlyFixer.getSlotFor(player.field_71071_by, crafted);
               if (slot != -1) {
                  ModAttributes.GEAR_CRAFTED_BY.create(player.field_71071_by.func_70301_a(slot), player.func_200200_C_().getString());
               }

               ModAttributes.GEAR_CRAFTED_BY.create(crafted, player.func_200200_C_().getString());
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onItemTooltip(ItemTooltipEvent event) {
      if (Minecraft.func_71410_x().field_71439_g == null || !Minecraft.func_71410_x().field_71439_g.func_184812_l_()) {
         for (int i = 0; i < event.getToolTip().size(); i++) {
            ITextComponent txt = (ITextComponent)event.getToolTip().get(i);
            if (txt.getString().contains("the_vault:idol")) {
               event.getToolTip().set(i, new StringTextComponent("the_vault:idol").func_230530_a_(txt.func_150256_b()));
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerEnterVault(PlayerChangedDimensionEvent event) {
      PlayerEntity player = event.getPlayer();
      if (event.getTo() == Vault.VAULT_KEY && player instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
         AdvancementHelper.grantCriterion(serverPlayer, Vault.id("main/root"), "entered_vault");
         AdvancementHelper.grantCriterion(serverPlayer, Vault.id("armors/root"), "entered_vault");
      }
   }

   @SubscribeEvent
   public static void onVaultCharmUse(EntityItemPickupEvent event) {
      if (event.getPlayer() instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
         ItemEntity itemEntity = event.getItem();
         ItemStack stack = itemEntity.func_92059_d();
         if (!stack.func_190926_b()) {
            ServerWorld world = player.func_71121_q();
            if (world.func_234923_W_() == Vault.VAULT_KEY) {
               if (hasVaultCharm(player.field_71071_by)) {
                  List<ResourceLocation> whitelist = VaultCharmData.get(world).getWhitelistedItems(player);
                  if (whitelist.contains(stack.func_77973_b().getRegistryName())) {
                     event.setCanceled(true);
                     itemEntity.func_70106_y();
                     world.func_184133_a(
                        null,
                        player.func_233580_cy_(),
                        SoundEvents.field_187638_cR,
                        SoundCategory.PLAYERS,
                        0.2F,
                        (world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 1.4F + 2.0F
                     );
                  }
               }
            }
         }
      }
   }

   private static boolean hasVaultCharm(PlayerInventory inventory) {
      for (int slot = 0; slot < inventory.func_70302_i_(); slot++) {
         ItemStack stack = inventory.func_70301_a(slot);
         if (!stack.func_190926_b() && stack.func_77973_b() == ModItems.VAULT_CHARM) {
            return true;
         }
      }

      return false;
   }
}
