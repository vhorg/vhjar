package iskallia.vault.world.data;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.set.PlayerSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PhoenixSetSnapshotData extends InventorySnapshotData {
   private static final String RESTORE_FLAG = "the_vault_restore_phoenixset";
   protected static final String DATA_NAME = "the_vault_PhoenixSet";

   public PhoenixSetSnapshotData() {
      super("the_vault_PhoenixSet");
   }

   @Override
   protected boolean shouldSnapshotItem(PlayerEntity player, ItemStack stack) {
      return !stack.func_190926_b() && !ModAttributes.SOULBOUND.getOrDefault(stack, false).getValue(stack);
   }

   @Override
   protected InventorySnapshotData.Builder makeSnapshotBuilder(PlayerEntity player) {
      return new InventorySnapshotData.Builder(player).setStackFilter(this::shouldSnapshotItem);
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      PlayerEntity player = event.player;
      if (player.func_70089_S() && player.field_70170_p instanceof ServerWorld) {
         if (player.func_184216_O().contains("the_vault_restore_phoenixset")) {
            ServerWorld world = (ServerWorld)event.player.field_70170_p;
            PhoenixSetSnapshotData data = get(world);
            if (data.hasSnapshot(player)) {
               data.restoreSnapshot(player);
            }

            player.func_184197_b("the_vault_restore_phoenixset");
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof ServerPlayerEntity && event.getEntity().field_70170_p instanceof ServerWorld) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
         ServerWorld world = (ServerWorld)player.field_70170_p;
         if (PlayerSet.isActive(VaultGear.Set.PHOENIX, player)) {
            PhoenixSetSnapshotData data = get(world);
            if (data.hasSnapshot(player)) {
               player.func_184211_a("the_vault_restore_phoenixset");
            }
         }
      }
   }

   public static PhoenixSetSnapshotData get(ServerWorld world) {
      return get(world.func_73046_m());
   }

   public static PhoenixSetSnapshotData get(MinecraftServer srv) {
      return (PhoenixSetSnapshotData)srv.func_241755_D_().func_217481_x().func_215752_a(PhoenixSetSnapshotData::new, "the_vault_PhoenixSet");
   }
}
