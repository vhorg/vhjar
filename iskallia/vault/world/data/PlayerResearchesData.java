package iskallia.vault.world.data;

import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
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

public class PlayerResearchesData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerResearches";
   private Map<UUID, ResearchTree> playerMap = new HashMap<>();

   public PlayerResearchesData() {
      super("the_vault_PlayerResearches");
   }

   public PlayerResearchesData(String name) {
      super(name);
   }

   public ResearchTree getResearches(PlayerEntity player) {
      return this.getResearches(player.func_110124_au());
   }

   public ResearchTree getResearches(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, ResearchTree::new);
   }

   public PlayerResearchesData research(ServerPlayerEntity player, Research research) {
      ResearchTree researchTree = this.getResearches(player);
      researchTree.research(research.getName());
      researchTree.sync(player.func_184102_h());
      this.func_76185_a();
      return this;
   }

   public PlayerResearchesData resetResearchTree(ServerPlayerEntity player) {
      ResearchTree researchTree = this.getResearches(player);
      researchTree.resetAll();
      researchTree.sync(player.func_184102_h());
      this.func_76185_a();
      return this;
   }

   public void func_76184_a(CompoundNBT nbt) {
      ListNBT playerList = nbt.func_150295_c("PlayerEntries", 8);
      ListNBT researchesList = nbt.func_150295_c("ResearchEntries", 10);
      if (playerList.size() != researchesList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.func_150307_f(i));
            this.getResearches(playerUUID).deserializeNBT(researchesList.func_150305_b(i));
         }
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      ListNBT playerList = new ListNBT();
      ListNBT researchesList = new ListNBT();
      this.playerMap.forEach((uuid, researchTree) -> {
         playerList.add(StringNBT.func_229705_a_(uuid.toString()));
         researchesList.add(researchTree.serializeNBT());
      });
      nbt.func_218657_a("PlayerEntries", playerList);
      nbt.func_218657_a("ResearchEntries", researchesList);
      return nbt;
   }

   public static PlayerResearchesData get(ServerWorld world) {
      return (PlayerResearchesData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(PlayerResearchesData::new, "the_vault_PlayerResearches");
   }
}
