package iskallia.vault.world.vault.logic;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.task.IVaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultChestPity implements INBTSerializable<CompoundNBT>, IVaultTask {
   private final Map<UUID, Integer> ticksElapsed = new HashMap<>();

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      int ticks = this.ticksElapsed.getOrDefault(player.getPlayerId(), 0);
      this.ticksElapsed.put(player.getPlayerId(), ticks + 1);
   }

   public String getRandomChestRarity(WeightedList<String> chestWeights, PlayerEntity player, Random rand) {
      int ticks = this.getTicksElapsed(player);
      String rarity = ModConfigs.VAULT_CHEST_META.getPityAdjustedRarity(chestWeights, ticks).getRandom(rand);
      this.resetTicks(player);
      return rarity;
   }

   public int getTicksElapsed(PlayerEntity player) {
      return this.ticksElapsed.getOrDefault(player.func_110124_au(), 0);
   }

   public void resetTicks(PlayerEntity player) {
      this.ticksElapsed.put(player.func_110124_au(), 0);
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      this.ticksElapsed.forEach((uuid, ticks) -> nbt.func_74768_a(uuid.toString(), ticks));
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.ticksElapsed.clear();

      for (String key : nbt.func_150296_c()) {
         UUID playerUUID;
         try {
            playerUUID = UUID.fromString(key);
         } catch (IllegalArgumentException var6) {
            continue;
         }

         this.ticksElapsed.put(playerUUID, nbt.func_74762_e(key));
      }
   }
}
