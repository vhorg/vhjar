package iskallia.vault.util;

import iskallia.vault.VaultMod;
import iskallia.vault.dump.PlayerSnapshotDump;
import iskallia.vault.init.ModConfigs;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLPaths;

@EventBusSubscriber
public class PlayerDataSnapshotHelper {
   @SubscribeEvent
   public static void onSave(SaveToFile event) {
      if (event.getPlayer() instanceof ServerPlayer) {
         if (ModConfigs.VAULT_GENERAL.SAVE_PLAYER_SNAPSHOTS) {
            savePlayerSnapshot((ServerPlayer)event.getPlayer());
         }
      }
   }

   private static void savePlayerSnapshot(ServerPlayer sPlayer) {
      savePlayerSnapshot(sPlayer, getSnapshotDirectory());
   }

   private static void savePlayerSnapshot(ServerPlayer sPlayer, File directory) {
      String uuidStr = sPlayer.getUUID().toString();
      File playerFile = new File(directory, uuidStr + ".json");
      if (!playerFile.exists()) {
         playerFile.delete();
      }

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile))) {
         writer.write(PlayerSnapshotDump.createAndSerializeSnapshot(sPlayer));
      } catch (IOException var9) {
         VaultMod.LOGGER.error("Error writing player snapshot file: " + sPlayer.getName().getString());
         var9.printStackTrace();
      }
   }

   private static File getSnapshotDirectory() {
      File snapshotDirectory = FMLPaths.GAMEDIR.get().resolve("playerSnapshots").toFile();
      if (!snapshotDirectory.exists()) {
         snapshotDirectory.mkdirs();
      }

      return snapshotDirectory;
   }
}
