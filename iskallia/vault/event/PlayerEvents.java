package iskallia.vault.event;

import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
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
      if (!event.getPlayer().field_70170_p.field_72995_K) {
         int level = PlayerVaultStatsData.get((ServerWorld)event.getPlayer().field_70170_p).getVaultStats(event.getPlayer()).getVaultLevel();
         ItemStack stack = event.getPlayer().func_184614_ca();
         if (ModAttributes.MIN_VAULT_LEVEL.exists(stack) && level < ModAttributes.MIN_VAULT_LEVEL.get(stack).get().getValue(stack)) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick2(PlayerTickEvent event) {
      if (event.player.func_70644_a(Effects.field_76426_n)) {
         event.player.func_70066_B();
      }

      if (!event.player.func_130014_f_().func_201670_d()) {
         for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.func_188453_a().equals(Group.ARMOR)) {
               ItemStack stack = event.player.func_184582_a(slot);
               int level = PlayerVaultStatsData.get((ServerWorld)event.player.field_70170_p).getVaultStats(event.player).getVaultLevel();
               if (ModAttributes.MIN_VAULT_LEVEL.exists(stack) && level < ModAttributes.MIN_VAULT_LEVEL.get(stack).get().getValue(stack)) {
                  event.player.func_146097_a(stack.func_77946_l(), false, false);
                  stack.func_190920_e(0);
               }
            }
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
            ModAttributes.GEAR_CRAFTED_BY.create(crafted, player.func_200200_C_().getString());
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
}
