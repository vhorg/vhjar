package iskallia.vault.world.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class PlayerSpiritRecoveryData extends SavedData {
   private static final String DATA_NAME = "the_vault_PlayerSpiritRecovery";
   private Map<UUID, Integer> playerSpiritRecoveries = new HashMap<>();

   public static PlayerSpiritRecoveryData get(ServerLevel world) {
      return (PlayerSpiritRecoveryData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerSpiritRecoveryData::create, PlayerSpiritRecoveryData::new, "the_vault_PlayerSpiritRecovery");
   }

   private static PlayerSpiritRecoveryData create(CompoundTag tag) {
      PlayerSpiritRecoveryData data = new PlayerSpiritRecoveryData();
      data.load(tag);
      return data;
   }

   private void load(CompoundTag tag) {
      this.playerSpiritRecoveries.clear();
      ListTag data = tag.getList("PlayerData", 10);
      data.forEach(t -> {
         if (t instanceof CompoundTag entry) {
            this.playerSpiritRecoveries.put(entry.getUUID("uuid"), entry.getInt("count"));
         }
      });
   }

   public CompoundTag save(CompoundTag compoundTag) {
      ListTag spiritRecoveries = new ListTag();
      this.playerSpiritRecoveries.forEach((uuid, count) -> {
         CompoundTag entry = new CompoundTag();
         entry.putUUID("uuid", uuid);
         entry.putInt("count", count);
         spiritRecoveries.add(entry);
      });
      compoundTag.put("PlayerData", spiritRecoveries);
      return compoundTag;
   }

   public void incrementSpiritRecovery(UUID playerUuid) {
      this.playerSpiritRecoveries.compute(playerUuid, (uuid, count) -> count == null ? 1 : Integer.valueOf(count + 1));
      this.setDirty();
   }

   public int getSpiritRecoveryCount(UUID playerUuid) {
      return this.playerSpiritRecoveries.getOrDefault(playerUuid, 0);
   }
}
