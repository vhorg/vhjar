package iskallia.vault.event;

import iskallia.vault.Vault;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.GameRules.BooleanValue;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerEvents {
   public static boolean NATURAL_REGEN_OLD_VALUE = false;
   public static boolean MODIFIED_GAMERULE = false;

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
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.side != LogicalSide.CLIENT) {
         RegistryKey<World> dimensionKey = event.player.field_70170_p.func_234923_W_();
         GameRules gameRules = event.player.field_70170_p.func_82736_K();
         if (MODIFIED_GAMERULE && dimensionKey != Vault.VAULT_KEY) {
            ((BooleanValue)gameRules.func_223585_a(GameRules.field_223606_i)).func_223570_a(NATURAL_REGEN_OLD_VALUE, event.player.func_184102_h());
            MODIFIED_GAMERULE = false;
         } else if (dimensionKey == Vault.VAULT_KEY) {
            if (event.phase == Phase.START) {
               NATURAL_REGEN_OLD_VALUE = gameRules.func_223586_b(GameRules.field_223606_i);
               ((BooleanValue)gameRules.func_223585_a(GameRules.field_223606_i)).func_223570_a(false, event.player.func_184102_h());
               MODIFIED_GAMERULE = true;
            } else if (event.phase == Phase.END) {
               ((BooleanValue)gameRules.func_223585_a(GameRules.field_223606_i)).func_223570_a(NATURAL_REGEN_OLD_VALUE, event.player.func_184102_h());
               MODIFIED_GAMERULE = false;
            }
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
      if (!event.player.field_70170_p.field_72995_K) {
         EquipmentSlotType[] slots = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

         for (EquipmentSlotType slot : slots) {
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
