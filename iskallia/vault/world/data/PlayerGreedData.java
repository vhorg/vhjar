package iskallia.vault.world.data;

import iskallia.vault.nbt.VMapNBT;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PlayerGreedData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerGreed";
   protected VMapNBT<UUID, PlayerGreedData.ArtifactData> data = VMapNBT.ofUUID(PlayerGreedData.ArtifactData::new);

   public PlayerGreedData.ArtifactData get(Player player) {
      return this.get(player.getUUID());
   }

   public PlayerGreedData.ArtifactData get(UUID playerId) {
      return this.data.computeIfAbsent(playerId, uuid -> new PlayerGreedData.ArtifactData());
   }

   public void onArtifactCompleted(UUID playerId) {
      if (!this.get(playerId).completedArtifacts) {
         this.get(playerId).completedArtifacts = true;
         this.setDirty();
      }
   }

   public void onHeraldCompleted(UUID playerId) {
      if (!this.get(playerId).completedHerald) {
         this.get(playerId).completedHerald = true;
         this.setDirty();
      }
   }

   private static PlayerGreedData create(CompoundTag tag) {
      PlayerGreedData data = new PlayerGreedData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      this.data.deserializeNBT(nbt.getList("Data", 10));
   }

   public CompoundTag save(CompoundTag nbt) {
      nbt.put("Data", this.data.serializeNBT());
      return nbt;
   }

   public static PlayerGreedData get(MinecraftServer server) {
      return (PlayerGreedData)server.overworld().getDataStorage().computeIfAbsent(PlayerGreedData::create, PlayerGreedData::new, "the_vault_PlayerGreed");
   }

   public static PlayerGreedData get() {
      return get(ServerLifecycleHooks.getCurrentServer());
   }

   public static class ArtifactData implements INBTSerializable<CompoundTag> {
      protected boolean completedArtifacts = false;
      protected boolean completedHerald = false;

      public boolean hasCompletedArtifacts() {
         return this.completedArtifacts;
      }

      public boolean hasCompletedHerald() {
         return this.completedHerald;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putBoolean("CompletedArtifacts", this.completedArtifacts);
         nbt.putBoolean("CompletedHerald", this.completedHerald);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.completedArtifacts = nbt.getBoolean("CompletedArtifacts");
         this.completedHerald = nbt.getBoolean("CompletedHerald");
      }
   }
}
