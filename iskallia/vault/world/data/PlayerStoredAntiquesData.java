package iskallia.vault.world.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

public class PlayerStoredAntiquesData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerStoredAntiques";
   private final Map<UUID, PlayerStoredAntiquesData.StoredAntiques> storedAntiques = new HashMap<>();

   private PlayerStoredAntiquesData() {
   }

   private PlayerStoredAntiquesData(CompoundTag tag) {
      this.load(tag);
   }

   public PlayerStoredAntiquesData.StoredAntiques getStoredAntiques(Player player) {
      return this.getStoredAntiques(player.getUUID());
   }

   public PlayerStoredAntiquesData.StoredAntiques getStoredAntiques(UUID playerUUID) {
      return this.storedAntiques.computeIfAbsent(playerUUID, uuid -> new PlayerStoredAntiquesData.StoredAntiques());
   }

   public void setStoredAntiques(Player player, PlayerStoredAntiquesData.StoredAntiques storedAntiques) {
      this.setStoredAntiques(player.getUUID(), storedAntiques);
   }

   public void setStoredAntiques(UUID playerUUID, PlayerStoredAntiquesData.StoredAntiques storedAntiques) {
      this.storedAntiques.put(playerUUID, storedAntiques);
      this.setDirty();
   }

   protected void load(CompoundTag tag) {
      CompoundTag antiquesTag = tag.getCompound("antiques");
      antiquesTag.getAllKeys().forEach(key -> {
         UUID uuid = UUID.fromString(key);
         PlayerStoredAntiquesData.StoredAntiques storedAntiques = new PlayerStoredAntiquesData.StoredAntiques();
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

   public static class StoredAntiques extends HashMap<ResourceLocation, Integer> {
      private void load(CompoundTag tag) {
         this.clear();
         tag.getAllKeys().forEach(key -> {
            ResourceLocation regKey = ResourceLocation.tryParse(key);
            if (regKey != null) {
               this.put(regKey, Integer.valueOf(tag.getInt(key)));
            }
         });
      }

      public void load(FriendlyByteBuf buf) {
         int size = buf.readInt();

         for (int i = 0; i < size; i++) {
            this.put(buf.readResourceLocation(), Integer.valueOf(buf.readInt()));
         }
      }

      private CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         this.forEach((key, value) -> tag.putInt(key.toString(), value));
         return tag;
      }

      public void write(FriendlyByteBuf buf) {
         buf.writeInt(this.size());
         this.forEach((key, value) -> {
            buf.writeResourceLocation(key);
            buf.writeInt(value);
         });
      }
   }
}
