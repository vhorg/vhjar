package iskallia.vault.world.data;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.modifier.InventoryRestoreModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PhoenixModifierSnapshotData extends InventorySnapshotData {
   private static final String RESTORE_FLAG = "the_vault_restore_inventory";
   protected static final String DATA_NAME = "the_vault_PhoenixModifier";

   public PhoenixModifierSnapshotData() {
      super("the_vault_PhoenixModifier");
   }

   @Override
   protected boolean shouldSnapshotItem(PlayerEntity player, ItemStack stack) {
      return !stack.func_190926_b() && !ModAttributes.SOULBOUND.getOrDefault(stack, false).getValue(stack);
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      PlayerEntity player = event.player;
      if (player.func_70089_S() && player.field_70170_p instanceof ServerWorld) {
         if (player.func_184216_O().contains("the_vault_restore_inventory")) {
            ServerWorld world = (ServerWorld)event.player.field_70170_p;
            PhoenixModifierSnapshotData data = get(world);
            if (data.hasSnapshot(player)) {
               data.restoreSnapshot(player);
            }

            player.func_184197_b("the_vault_restore_inventory");
         }
      }
   }

   @SubscribeEvent
   public static void onDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof ServerPlayerEntity && event.getEntity().field_70170_p instanceof ServerWorld) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
         ServerWorld world = (ServerWorld)player.field_70170_p;
         VaultRaid currentRaid = VaultRaidData.get(world).getActiveFor(player);
         if (currentRaid != null && !currentRaid.getActiveModifiersFor(PlayerFilter.of(player), InventoryRestoreModifier.class).isEmpty()) {
            PhoenixModifierSnapshotData data = get(world);
            if (data.hasSnapshot(player)) {
               player.func_184211_a("the_vault_restore_inventory");
            }
         }
      }
   }

   public static PhoenixModifierSnapshotData get(ServerWorld world) {
      return (PhoenixModifierSnapshotData)world.func_73046_m()
         .func_241755_D_()
         .func_217481_x()
         .func_215752_a(PhoenixModifierSnapshotData::new, "the_vault_PhoenixModifier");
   }
}
