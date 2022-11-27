package iskallia.vault.world.vault.logic;

import iskallia.vault.util.data.WeightedDoubleList;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.task.IVaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultChestPity implements INBTSerializable<CompoundTag>, IVaultTask {
   private final Map<UUID, Integer> ticksElapsed = new HashMap<>();

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      int ticks = this.ticksElapsed.getOrDefault(player.getPlayerId(), 0);
      this.ticksElapsed.put(player.getPlayerId(), ticks + 1);
   }

   public String getRandomChestRarity(WeightedDoubleList<String> chestWeights, Player player, Random rand) {
      int ticks = this.getTicksElapsed(player);
      this.resetTicks(player);
      return null;
   }

   public int getTicksElapsed(Player player) {
      return this.ticksElapsed.getOrDefault(player.getUUID(), 0);
   }

   public void resetTicks(Player player) {
      this.ticksElapsed.put(player.getUUID(), 0);
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      this.ticksElapsed.forEach((uuid, ticks) -> nbt.putInt(uuid.toString(), ticks));
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.ticksElapsed.clear();

      for (String key : nbt.getAllKeys()) {
         UUID playerUUID;
         try {
            playerUUID = UUID.fromString(key);
         } catch (IllegalArgumentException var6) {
            continue;
         }

         this.ticksElapsed.put(playerUUID, nbt.getInt(key));
      }
   }
}
