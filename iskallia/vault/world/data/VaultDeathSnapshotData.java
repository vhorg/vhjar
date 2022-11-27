package iskallia.vault.world.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class VaultDeathSnapshotData extends TimestampedInventorySnapshotData {
   protected static final String DATA_NAME = "the_vault_VaultDeathSnapshots";

   private static VaultDeathSnapshotData create(CompoundTag tag) {
      VaultDeathSnapshotData data = new VaultDeathSnapshotData();
      data.load(tag);
      return data;
   }

   public static VaultDeathSnapshotData get(ServerLevel serverLevel) {
      return (VaultDeathSnapshotData)serverLevel.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(VaultDeathSnapshotData::create, VaultDeathSnapshotData::new, "the_vault_VaultDeathSnapshots");
   }

   @SubscribeEvent
   public static void onPlayerDeath(LivingDeathEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayer player) {
         get(player.getLevel()).createSnapshot(player);
      }
   }
}
