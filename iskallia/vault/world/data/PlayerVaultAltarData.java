package iskallia.vault.world.data;

import iskallia.vault.altar.AltarInfusionRecipe;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class PlayerVaultAltarData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerAltarRecipes";
   private Map<UUID, AltarInfusionRecipe> playerMap = new HashMap<>();

   public PlayerVaultAltarData() {
      super("the_vault_PlayerAltarRecipes");
   }

   public PlayerVaultAltarData(String name) {
      super(name);
   }

   public static PlayerVaultAltarData get(ServerWorld world) {
      return (PlayerVaultAltarData)world.func_73046_m()
         .func_241755_D_()
         .func_217481_x()
         .func_215752_a(PlayerVaultAltarData::new, "the_vault_PlayerAltarRecipes");
   }

   public AltarInfusionRecipe getRecipe(PlayerEntity player) {
      return this.getRecipe(player.func_110124_au());
   }

   public AltarInfusionRecipe getRecipe(UUID uuid) {
      return this.playerMap.get(uuid);
   }

   public AltarInfusionRecipe getRecipe(ServerWorld world, PlayerEntity player) {
      AltarInfusionRecipe recipe = this.playerMap.computeIfAbsent(player.func_110124_au(), k -> new AltarInfusionRecipe(world, player));
      this.func_76185_a();
      return recipe;
   }

   public boolean hasRecipe(UUID uuid) {
      return this.playerMap.containsKey(uuid);
   }

   public PlayerVaultAltarData add(UUID uuid, AltarInfusionRecipe recipe) {
      this.playerMap.put(uuid, recipe);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultAltarData remove(UUID uuid) {
      this.playerMap.remove(uuid);
      this.func_76185_a();
      return this;
   }

   public PlayerVaultAltarData update(UUID id, AltarInfusionRecipe recipe) {
      this.remove(id);
      this.add(id, recipe);
      this.func_76185_a();
      return this;
   }

   public void func_76184_a(CompoundNBT nbt) {
      ListNBT playerList = nbt.func_150295_c("PlayerEntries", 8);
      ListNBT recipeList = nbt.func_150295_c("AltarRecipeEntries", 10);
      if (playerList.size() != recipeList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.func_150307_f(i));
            this.playerMap.put(playerUUID, AltarInfusionRecipe.deserialize(recipeList.func_150305_b(i)));
         }
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      ListNBT playerList = new ListNBT();
      ListNBT recipeList = new ListNBT();
      this.playerMap.forEach((uuid, recipe) -> {
         playerList.add(StringNBT.func_229705_a_(uuid.toString()));
         recipeList.add(AltarInfusionRecipe.serialize(recipe));
      });
      nbt.func_218657_a("PlayerEntries", playerList);
      nbt.func_218657_a("AltarRecipeEntries", recipeList);
      return nbt;
   }
}
