package iskallia.vault.util;

import iskallia.vault.Vault;
import iskallia.vault.dump.PlayerSnapshotDump;
import iskallia.vault.init.ModConfigs;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLPaths;

@EventBusSubscriber
public class PlayerSnapshotHelper {
   @SubscribeEvent
   public static void onSave(SaveToFile event) {
      if (event.getPlayer() instanceof ServerPlayerEntity) {
         if (ModConfigs.VAULT_GENERAL.SAVE_PLAYER_SNAPSHOTS) {
            savePlayerSnapshot((ServerPlayerEntity)event.getPlayer());
         }
      }
   }

   private static void savePlayerSnapshot(ServerPlayerEntity sPlayer) {
      savePlayerSnapshot(sPlayer, getSnapshotDirectory());
   }

   private static void savePlayerSnapshot(ServerPlayerEntity sPlayer, File directory) {
      String uuidStr = sPlayer.func_110124_au().toString();
      File playerFile = new File(directory, uuidStr + ".json");
      if (!playerFile.exists()) {
         playerFile.delete();
      }

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile))) {
         writer.write(PlayerSnapshotDump.createAndSerializeSnapshot(sPlayer));
      } catch (IOException var17) {
         Vault.LOGGER.error("Error writing player snapshot file: " + sPlayer.func_200200_C_().getString());
         var17.printStackTrace();
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
