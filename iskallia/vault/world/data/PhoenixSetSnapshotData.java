package iskallia.vault.world.data;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PhoenixSetSnapshotData extends InventorySnapshotData {
   private static final String RESTORE_FLAG = "the_vault_restore_phoenixset";
   protected static final String DATA_NAME = "the_vault_PhoenixSet";

   @Override
   protected boolean shouldSnapshotItem(Player player, ItemStack stack) {
      return !stack.isEmpty()
         && (
            !AttributeGearData.hasData(stack)
               || !AttributeGearData.<AttributeGearData>read(stack).get(ModGearAttributes.SOULBOUND, VaultGearAttributeTypeMerger.anyTrue())
         );
   }

   @Override
   protected InventorySnapshotData.Builder makeSnapshotBuilder(Player player) {
      return new InventorySnapshotData.Builder(player).setStackFilter(this::shouldSnapshotItem);
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      Player player = event.player;
      if (player.isAlive() && player.level instanceof ServerLevel) {
         if (player.getTags().contains("the_vault_restore_phoenixset")) {
            ServerLevel world = (ServerLevel)event.player.level;
            PhoenixSetSnapshotData data = get(world);
            if (data.hasSnapshot(player)) {
               data.restoreSnapshot(player);
            }

            player.removeTag("the_vault_restore_phoenixset");
         }
      }
   }

   private static PhoenixSetSnapshotData create(CompoundTag tag) {
      PhoenixSetSnapshotData data = new PhoenixSetSnapshotData();
      data.load(tag);
      return data;
   }

   public static PhoenixSetSnapshotData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static PhoenixSetSnapshotData get(MinecraftServer srv) {
      return (PhoenixSetSnapshotData)srv.overworld()
         .getDataStorage()
         .computeIfAbsent(PhoenixSetSnapshotData::create, PhoenixSetSnapshotData::new, "the_vault_PhoenixSet");
   }
}
