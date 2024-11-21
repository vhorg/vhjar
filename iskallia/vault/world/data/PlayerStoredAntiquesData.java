package iskallia.vault.world.data;

import iskallia.vault.item.AntiqueStampCollectorBook;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

@Deprecated
public class PlayerStoredAntiquesData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerStoredAntiques";
   private final Map<UUID, AntiqueStampCollectorBook.StoredAntiques> storedAntiques = new HashMap<>();

   private PlayerStoredAntiquesData() {
   }

   private PlayerStoredAntiquesData(CompoundTag tag) {
      this.load(tag);
   }

   @Nullable
   public AntiqueStampCollectorBook.StoredAntiques getStoredAntiques(Player player) {
      return this.getStoredAntiques(player.getUUID());
   }

   protected AntiqueStampCollectorBook.StoredAntiques getStoredAntiques(UUID playerUUID) {
      return this.storedAntiques.get(playerUUID);
   }

   public void removeStoredAntiques(Player player) {
      if (this.storedAntiques.remove(player.getUUID()) != null) {
         this.setDirty();
      }
   }

   protected void load(CompoundTag tag) {
      CompoundTag antiquesTag = tag.getCompound("antiques");
      antiquesTag.getAllKeys().forEach(key -> {
         UUID uuid = UUID.fromString(key);
         AntiqueStampCollectorBook.StoredAntiques storedAntiques = new AntiqueStampCollectorBook.StoredAntiques();
         storedAntiques.load(antiquesTag.getCompound(key));
         this.storedAntiques.put(uuid, storedAntiques);
      });
   }

   public CompoundTag save(CompoundTag tag) {
      CompoundTag antiquesTag = new CompoundTag();
      this.storedAntiques.forEach((uuid, storedAntiques) -> antiquesTag.put(uuid.toString(), storedAntiques.serialize()));
      tag.put("antiques", antiquesTag);
      return tag;
   }

   public static PlayerStoredAntiquesData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static PlayerStoredAntiquesData get(MinecraftServer server) {
      return (PlayerStoredAntiquesData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerStoredAntiquesData::new, PlayerStoredAntiquesData::new, "the_vault_PlayerStoredAntiques");
   }
}
