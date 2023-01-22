package iskallia.vault.world.data;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundUpdateDifficultyMessage;
import iskallia.vault.world.VaultDifficulty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber
public class WorldSettings extends SavedData {
   protected static final String DATA_NAME = "the_vault_WorldSettings";
   private static final String PLAYER_DIFFICULTIES_TAG = "playerDifficulties";
   private final Map<UUID, VaultDifficulty> playerDifficulties = new HashMap<>();
   private static final WorldSettings clientSettings = new WorldSettings();
   private VaultDifficulty vaultDifficulty = VaultDifficulty.NORMAL;
   private boolean vaultDifficultyLocked = false;

   @SubscribeEvent
   public static void syncOnLogin(OnDatapackSyncEvent event) {
      ServerPlayer player = event.getPlayer();
      if (player != null) {
         WorldSettings worldSettings = get(player.getLevel());
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.PLAYER.with(() -> player),
               new ClientboundUpdateDifficultyMessage(worldSettings.getGlobalVaultDifficulty(), worldSettings.isVaultDifficultyLocked())
            );
      }
   }

   private static WorldSettings create(CompoundTag tag) {
      WorldSettings data = new WorldSettings();
      data.load(tag);
      return data;
   }

   public static WorldSettings get(@Nullable Level level) {
      return Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER && level instanceof ServerLevel serverLevel
         ? (WorldSettings)serverLevel.getServer()
            .overworld()
            .getDataStorage()
            .computeIfAbsent(WorldSettings::create, WorldSettings::new, "the_vault_WorldSettings")
         : clientSettings;
   }

   public CompoundTag save(CompoundTag tag) {
      tag.putInt("vaultDifficulty", this.vaultDifficulty.getId());
      tag.putBoolean("vaultDifficultyLocked", this.vaultDifficultyLocked);
      tag.put("playerDifficulties", this.serializePlayerDifficulties());
      return tag;
   }

   private CompoundTag serializePlayerDifficulties() {
      CompoundTag tag = new CompoundTag();
      this.playerDifficulties.forEach((playerId, difficulty) -> tag.putInt(playerId.toString(), difficulty.getId()));
      return tag;
   }

   private void load(CompoundTag tag) {
      this.vaultDifficulty = VaultDifficulty.byId(tag.getInt("vaultDifficulty"));
      this.vaultDifficultyLocked = tag.getBoolean("vaultDifficultyLocked");
      this.playerDifficulties.clear();
      if (tag.contains("playerDifficulties")) {
         CompoundTag difficultiesTag = tag.getCompound("playerDifficulties");
         difficultiesTag.getAllKeys()
            .forEach(playerId -> this.playerDifficulties.put(UUID.fromString(playerId), VaultDifficulty.byId(difficultiesTag.getInt(playerId))));
      }
   }

   public VaultDifficulty getPlayerDifficulty(UUID playerId) {
      return this.playerDifficulties.getOrDefault(playerId, this.vaultDifficulty);
   }

   public VaultDifficulty getGlobalVaultDifficulty() {
      return this.vaultDifficulty;
   }

   public void setGlobalVaultDifficulty(VaultDifficulty vaultDifficulty) {
      this.vaultDifficulty = vaultDifficulty;
      this.setDirty();
   }

   public boolean isVaultDifficultyLocked() {
      return this.vaultDifficultyLocked;
   }

   public void setVaultDifficultyLocked(boolean vaultDifficultyLocked) {
      this.vaultDifficultyLocked = vaultDifficultyLocked;
      this.setDirty();
   }

   public void setPlayerDifficulty(UUID playerId, VaultDifficulty difficulty) {
      this.playerDifficulties.put(playerId, difficulty);
      this.setDirty();
   }
}
