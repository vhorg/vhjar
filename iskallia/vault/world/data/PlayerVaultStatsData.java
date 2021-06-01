package iskallia.vault.world.data;

import iskallia.vault.skill.PlayerVaultStats;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class PlayerVaultStatsData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerVaultLevels";
   private Map<UUID, PlayerVaultStats> playerMap = new HashMap<>();

   public PlayerVaultStatsData() {
      super("the_vault_PlayerVaultLevels");
   }

   public PlayerVaultStatsData(String name) {
      super(name);
   }

   public PlayerVaultStats getVaultStats(PlayerEntity player) {
      return this.getVaultStats(player.func_110124_au());
   }

   public PlayerVaultStats getVaultStats(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, PlayerVaultStats::new);
   }

   public PlayerVaultStatsData setVaultLevel(ServerPlayerEntity player, int level) {
      this.getVaultStats(player).setVaultLevel(player.func_184102_h(), level);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData addVaultExp(ServerPlayerEntity player, int exp) {
      this.getVaultStats(player).addVaultExp(player.func_184102_h(), exp);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData spendSkillPts(ServerPlayerEntity player, int amount) {
      this.getVaultStats(player).spendSkillPoints(player.func_184102_h(), amount);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData spendKnowledgePts(ServerPlayerEntity player, int amount) {
      this.getVaultStats(player).spendKnowledgePoints(player.func_184102_h(), amount);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData addSkillPoint(ServerPlayerEntity player, int amount) {
      this.getVaultStats(player).addSkillPoints(amount).sync(player.func_71121_q().func_73046_m());
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData addKnowledgePoints(ServerPlayerEntity player, int amount) {
      this.getVaultStats(player).addKnowledgePoints(amount).sync(player.func_71121_q().func_73046_m());
      this.func_76185_a();
      return this;
   }

   public PlayerVaultStatsData reset(ServerPlayerEntity player) {
      this.getVaultStats(player).reset(player.func_184102_h());
      this.func_76185_a();
      return this;
   }

   public void func_76184_a(CompoundNBT nbt) {
      ListNBT playerList = nbt.func_150295_c("PlayerEntries", 8);
      ListNBT statEntries = nbt.func_150295_c("StatEntries", 10);
      if (playerList.size() != statEntries.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.func_150307_f(i));
            this.getVaultStats(playerUUID).deserializeNBT(statEntries.func_150305_b(i));
         }
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      ListNBT playerList = new ListNBT();
      ListNBT statsList = new ListNBT();
      this.playerMap.forEach((uuid, stats) -> {
         playerList.add(StringNBT.func_229705_a_(uuid.toString()));
         statsList.add(stats.serializeNBT());
      });
      nbt.func_218657_a("PlayerEntries", playerList);
      nbt.func_218657_a("StatEntries", statsList);
      return nbt;
   }

   public static PlayerVaultStatsData get(ServerWorld world) {
      return (PlayerVaultStatsData)world.func_73046_m()
         .func_241755_D_()
         .func_217481_x()
         .func_215752_a(PlayerVaultStatsData::new, "the_vault_PlayerVaultLevels");
   }
}
