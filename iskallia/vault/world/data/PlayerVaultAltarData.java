package iskallia.vault.world.data;

import iskallia.vault.altar.AltarInfusionRecipe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class PlayerVaultAltarData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerAltarRecipes";
   private Map<UUID, AltarInfusionRecipe> playerMap = new HashMap<>();
   private HashMap<UUID, List<BlockPos>> playerAltars = new HashMap<>();

   public PlayerVaultAltarData() {
      super("the_vault_PlayerAltarRecipes");
   }

   public PlayerVaultAltarData(String name) {
      super(name);
   }

   public AltarInfusionRecipe getRecipe(PlayerEntity player) {
      return this.getRecipe(player.func_110124_au());
   }

   public AltarInfusionRecipe getRecipe(UUID uuid) {
      return this.playerMap.get(uuid);
   }

   public AltarInfusionRecipe getRecipe(ServerWorld world, BlockPos pos, ServerPlayerEntity player) {
      AltarInfusionRecipe recipe = this.playerMap.computeIfAbsent(player.func_110124_au(), k -> new AltarInfusionRecipe(world, pos, player));
      this.func_76185_a();
      return recipe;
   }

   public boolean hasRecipe(UUID uuid) {
      return this.playerMap.containsKey(uuid);
   }

   public PlayerVaultAltarData addRecipe(UUID uuid, AltarInfusionRecipe recipe) {
      this.playerMap.put(uuid, recipe);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultAltarData removeRecipe(UUID uuid) {
      this.playerMap.remove(uuid);
      this.func_76185_a();
      return this;
   }

   public List<BlockPos> getAltars(UUID uuid) {
      if (uuid == null) {
         return new ArrayList<>();
      } else {
         this.playerAltars.computeIfAbsent(uuid, k -> new ArrayList<>());
         this.func_76185_a();
         return this.playerAltars.get(uuid);
      }
   }

   public PlayerVaultAltarData addAltar(UUID uuid, BlockPos altarPos) {
      this.getAltars(uuid).add(altarPos);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultAltarData removeAltar(UUID uuid, BlockPos altarPos) {
      this.getAltars(uuid).remove(altarPos);
      this.func_76185_a();
      return this;
   }

   public void func_76184_a(CompoundNBT nbt) {
      ListNBT playerList = nbt.func_150295_c("PlayerEntries", 8);
      ListNBT recipeList = nbt.func_150295_c("AltarRecipeEntries", 10);
      ListNBT playerBlockPosList = nbt.func_150295_c("PlayerBlockPosEntries", 8);
      ListNBT blockPosList = nbt.func_150295_c("BlockPosEntries", 9);
      if (playerList.size() == recipeList.size() && playerBlockPosList.size() == blockPosList.size()) {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.func_150307_f(i));
            this.playerMap.put(playerUUID, AltarInfusionRecipe.deserialize(recipeList.func_150305_b(i)));
         }

         for (int i = 0; i < playerBlockPosList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerBlockPosList.func_150307_f(i));
            List<BlockPos> positions = new ArrayList<>();

            for (INBT compound : blockPosList.func_202169_e(i)) {
               CompoundNBT posTag = (CompoundNBT)compound;
               BlockPos pos = NBTUtil.func_186861_c(posTag);
               positions.add(pos);
            }

            this.playerAltars.put(playerUUID, positions);
         }
      } else {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      ListNBT playerList = new ListNBT();
      ListNBT recipeList = new ListNBT();
      ListNBT playerBlockPosList = new ListNBT();
      ListNBT blockPosList = new ListNBT();
      this.playerMap.forEach((uuid, recipe) -> {
         playerList.add(StringNBT.func_229705_a_(uuid.toString()));
         recipeList.add(recipe.serialize());
      });
      this.playerAltars.forEach((uuid, altarPositions) -> {
         playerBlockPosList.add(StringNBT.func_229705_a_(uuid.toString()));
         ListNBT positions = new ListNBT();
         altarPositions.forEach(pos -> positions.add(NBTUtil.func_186859_a(pos)));
         blockPosList.add(positions);
      });
      nbt.func_218657_a("PlayerEntries", playerList);
      nbt.func_218657_a("AltarRecipeEntries", recipeList);
      nbt.func_218657_a("PlayerBlockPosEntries", playerBlockPosList);
      nbt.func_218657_a("BlockPosEntries", blockPosList);
      return nbt;
   }

   public static PlayerVaultAltarData get(ServerWorld world) {
      return (PlayerVaultAltarData)world.func_73046_m()
         .func_241755_D_()
         .func_217481_x()
         .func_215752_a(PlayerVaultAltarData::new, "the_vault_PlayerAltarRecipes");
   }
}
