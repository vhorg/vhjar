package iskallia.vault.world.data;

import iskallia.vault.init.ModAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
public class SoulboundSnapshotData extends InventorySnapshotData {
   protected static final String DATA_NAME = "the_vault_Soulbound";

   public SoulboundSnapshotData() {
      super("the_vault_Soulbound");
   }

   @Override
   protected boolean shouldSnapshotItem(PlayerEntity player, ItemStack stack) {
      return ModAttributes.SOULBOUND.getOrDefault(stack, false).getValue(stack);
   }

   @Override
   protected InventorySnapshotData.Builder makeSnapshotBuilder(PlayerEntity player) {
      return super.makeSnapshotBuilder(player).removeSnapshotItems();
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      PlayerEntity player = event.player;
      if (player.func_70089_S() && player.field_70170_p instanceof ServerWorld) {
         ServerWorld world = (ServerWorld)event.player.field_70170_p;
         SoulboundSnapshotData data = get(world);
         if (data.hasSnapshot(player)) {
            data.restoreSnapshot(player);
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof PlayerEntity && event.getEntity().field_70170_p instanceof ServerWorld) {
         PlayerEntity player = (PlayerEntity)event.getEntity();
         ServerWorld world = (ServerWorld)player.field_70170_p;
         SoulboundSnapshotData data = get(world);
         if (!data.hasSnapshot(player)) {
            data.createSnapshot(player);
         }
      }
   }

   public static SoulboundSnapshotData get(ServerWorld world) {
      return (SoulboundSnapshotData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(SoulboundSnapshotData::new, "the_vault_Soulbound");
   }
}
