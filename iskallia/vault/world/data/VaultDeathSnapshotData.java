package iskallia.vault.world.data;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModGameRules;
import java.io.File;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

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

   public void save(File file) {
      long timeMs = System.currentTimeMillis();
      super.save(file);
      if (System.currentTimeMillis() - timeMs > 50L) {
         MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
         if (srv != null && srv.getGameRules().getBoolean(ModGameRules.PRINT_SAVE_DATA_TIMING)) {
            VaultMod.LOGGER.info("VaultDeathSnapshots saving took %s ms".formatted(System.currentTimeMillis() - timeMs));
         }
      }
   }
}
