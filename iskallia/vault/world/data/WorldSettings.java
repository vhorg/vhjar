package iskallia.vault.world.data;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundUpdateDifficultyMessage;
import iskallia.vault.world.VaultDifficulty;
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
               new ClientboundUpdateDifficultyMessage(worldSettings.getVaultDifficulty(), worldSettings.isVaultDifficultyLocked())
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
      return tag;
   }

   private void load(CompoundTag tag) {
      this.vaultDifficulty = VaultDifficulty.byId(tag.getInt("vaultDifficulty"));
      this.vaultDifficultyLocked = tag.getBoolean("vaultDifficultyLocked");
   }

   public VaultDifficulty getVaultDifficulty() {
      return this.vaultDifficulty;
   }

   public void setVaultDifficulty(VaultDifficulty vaultDifficulty) {
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
}
