package iskallia.vault.backup;

import iskallia.vault.integration.IntegrationCurios;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.fml.ModList;

public class BackupManager {
   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss");
   private static final Pattern DATE_FORMAT_EXTRACTOR = Pattern.compile("^(.*)\\.dat$");

   private BackupManager() {
   }

   public static boolean createPlayerInventorySnapshot(ServerPlayerEntity playerEntity) {
      MinecraftServer srv = playerEntity.func_184102_h();
      if (srv == null) {
         return false;
      } else {
         ListNBT list = new ListNBT();

         for (int index = 0; index < playerEntity.field_71071_by.func_70302_i_(); index++) {
            ItemStack stack = playerEntity.field_71071_by.func_70301_a(index);
            if (!stack.func_190926_b()) {
               list.add(stack.serializeNBT());
            }
         }

         if (ModList.get().isLoaded("curios")) {
            list.addAll(IntegrationCurios.getSerializedCuriosItemStacks(playerEntity));
         }

         CompoundNBT tag = new CompoundNBT();
         tag.func_218657_a("data", list);
         File datFile = getStoredFile(srv, playerEntity.func_110124_au(), DATE_FORMAT.format(LocalDateTime.now()));

         try {
            CompressedStreamTools.func_74795_b(tag, datFile);
            return true;
         } catch (IOException var6) {
            var6.printStackTrace();
            return false;
         }
      }
   }

   public static Optional<List<ItemStack>> getStoredItemStacks(MinecraftServer server, UUID playerUUID, String timestampRef) {
      File storedFile = getStoredFile(server, playerUUID, timestampRef);
      if (!storedFile.exists()) {
         return Optional.empty();
      } else {
         CompoundNBT tag;
         try {
            tag = CompressedStreamTools.func_74797_a(storedFile);
         } catch (IOException var9) {
            var9.printStackTrace();
            return Optional.empty();
         }

         ListNBT data = tag.func_150295_c("data", 10);
         List<ItemStack> stacks = new ArrayList<>();

         for (int i = 0; i < data.size(); i++) {
            ItemStack stack = ItemStack.func_199557_a(data.func_150305_b(i));
            if (!stack.func_190926_b()) {
               stacks.add(stack);
            }
         }

         return Optional.of(stacks);
      }
   }

   public static List<String> getMostRecentBackupFileTimestamps(MinecraftServer server, UUID playerUUID) {
      return getBackupFileTimestamps(server, playerUUID, 5);
   }

   private static List<String> getBackupFileTimestamps(MinecraftServer server, UUID playerUUID, int count) {
      File dir = getStorageDir(server, playerUUID);
      File[] files = dir.listFiles();
      if (files == null) {
         return Collections.emptyList();
      } else {
         Comparator<? super Tuple<File, LocalDateTime>> tplTimeComparator = Comparator.comparing(Tuple::func_76340_b);
         tplTimeComparator = tplTimeComparator.reversed();
         long limit = count < 0 ? Long.MAX_VALUE : count;
         return Arrays.asList(files)
            .stream()
            .map(file -> {
               Matcher match = DATE_FORMAT_EXTRACTOR.matcher(file.getName());
               if (!match.find()) {
                  return null;
               } else {
                  String dateGroup = match.group(1);

                  LocalDateTime dateTime;
                  try {
                     dateTime = LocalDateTime.parse(dateGroup, DATE_FORMAT);
                  } catch (DateTimeParseException var5x) {
                     return null;
                  }

                  return new Tuple(file, dateTime);
               }
            })
            .filter(Objects::nonNull)
            .sorted(tplTimeComparator)
            .limit(limit)
            .map(tpl -> DATE_FORMAT.format((TemporalAccessor)tpl.func_76340_b()))
            .collect(Collectors.toList());
      }
   }

   private static File getStoredFile(MinecraftServer srv, UUID playerUUID, String timestamp) {
      return new File(getStorageDir(srv, playerUUID), timestamp + ".dat");
   }

   private static File getStorageDir(MinecraftServer server, UUID playerUUID) {
      File dir = server.func_240776_a_(FolderName.field_237253_i_).resolve("vault_inventory_backup").resolve(playerUUID.toString()).toFile();
      if (!dir.exists()) {
         dir.mkdirs();
      }

      return dir;
   }
}
