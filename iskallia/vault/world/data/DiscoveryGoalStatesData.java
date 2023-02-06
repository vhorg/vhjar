package iskallia.vault.world.data;

import iskallia.vault.discoverylogic.DiscoveryGoalsState;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

public class DiscoveryGoalStatesData extends SavedData {
   protected static final String DATA_NAME = "the_vault_DiscoveryGoalStates";
   private final Map<UUID, DiscoveryGoalsState> playerMap = new HashMap<>();

   public DiscoveryGoalsState getState(ServerPlayer serverPlayer) {
      return this.getState(serverPlayer.getUUID());
   }

   public DiscoveryGoalsState getState(UUID playerUuid) {
      return this.playerMap.computeIfAbsent(playerUuid, DiscoveryGoalsState::new);
   }

   public static DiscoveryGoalStatesData create(CompoundTag nbt) {
      DiscoveryGoalStatesData data = new DiscoveryGoalStatesData();
      data.load(nbt);
      return data;
   }

   public void load(@Nonnull CompoundTag nbt) {
      ListTag playerList = nbt.getList("PlayersList", 8);
      ListTag talentList = nbt.getList("StatesList", 10);
      if (playerList.size() != talentList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getState(playerUUID).deserializeNBT(talentList.getCompound(i));
         }

         this.setDirty();
      }
   }

   @Nonnull
   public CompoundTag save(@Nonnull CompoundTag nbt) {
      ListTag playerList = new ListTag();
      ListTag stateList = new ListTag();
      this.playerMap.forEach((uuid, state) -> {
         playerList.add(StringTag.valueOf(uuid.toString()));
         stateList.add(state.serializeNBT());
      });
      nbt.put("PlayersList", playerList);
      nbt.put("StatesList", stateList);
      return nbt;
   }

   public static DiscoveryGoalStatesData get(ServerLevel world) {
      return (DiscoveryGoalStatesData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(DiscoveryGoalStatesData::create, DiscoveryGoalStatesData::new, "the_vault_DiscoveryGoalStates");
   }
}
