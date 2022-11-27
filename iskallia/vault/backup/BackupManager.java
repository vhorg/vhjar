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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.ModList;

public class BackupManager {
   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss");
   private static final Pattern DATE_FORMAT_EXTRACTOR = Pattern.compile("^(.*)\\.dat$");

   private BackupManager() {
   }

   public static boolean createPlayerInventorySnapshot(ServerPlayer playerEntity) {
      MinecraftServer srv = playerEntity.getServer();
      if (srv == null) {
         return false;
      } else {
         ListTag list = new ListTag();

         for (int index = 0; index < playerEntity.getInventory().getContainerSize(); index++) {
            ItemStack stack = playerEntity.getInventory().getItem(index);
            if (!stack.isEmpty()) {
               list.add(stack.serializeNBT());
            }
         }

         if (ModList.get().isLoaded("curios")) {
            list.addAll(IntegrationCurios.getSerializedCuriosItemStacks(playerEntity));
         }

         CompoundTag tag = new CompoundTag();
         tag.put("data", list);
         File datFile = getStoredFile(srv, playerEntity.getUUID(), DATE_FORMAT.format(LocalDateTime.now()));

         try {
            NbtIo.write(tag, datFile);
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
         CompoundTag tag;
         try {
            tag = NbtIo.read(storedFile);
         } catch (IOException var9) {
            var9.printStackTrace();
            return Optional.empty();
         }

         ListTag data = tag.getList("data", 10);
         List<ItemStack> stacks = new ArrayList<>();

         for (int i = 0; i < data.size(); i++) {
            ItemStack stack = ItemStack.of(data.getCompound(i));
            if (!stack.isEmpty()) {
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
         Comparator<? super Tuple<File, LocalDateTime>> tplTimeComparator = Comparator.comparing(Tuple::getB);
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
            .map(tpl -> DATE_FORMAT.format((TemporalAccessor)tpl.getB()))
            .collect(Collectors.toList());
      }
   }

   private static File getStoredFile(MinecraftServer srv, UUID playerUUID, String timestamp) {
      return new File(getStorageDir(srv, playerUUID), timestamp + ".dat");
   }

   private static File getStorageDir(MinecraftServer server, UUID playerUUID) {
      File dir = server.getWorldPath(LevelResource.ROOT).resolve("vault_inventory_backup").resolve(playerUUID.toString()).toFile();
      if (!dir.exists()) {
         dir.mkdirs();
      }

      return dir;
   }
}
