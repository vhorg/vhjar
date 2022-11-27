package iskallia.vault.world.data;

import iskallia.vault.altar.AltarInfusionRecipe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

public class PlayerVaultAltarData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerAltarRecipes";
   private Map<UUID, AltarInfusionRecipe> playerMap = new HashMap<>();
   private HashMap<UUID, List<BlockPos>> playerAltars = new HashMap<>();

   public AltarInfusionRecipe getRecipe(Player player) {
      return this.getRecipe(player.getUUID());
   }

   public AltarInfusionRecipe getRecipe(UUID uuid) {
      return this.playerMap.get(uuid);
   }

   public AltarInfusionRecipe getRecipe(ServerLevel world, BlockPos pos, ServerPlayer player) {
      AltarInfusionRecipe recipe = this.playerMap.computeIfAbsent(player.getUUID(), k -> new AltarInfusionRecipe(world, pos, player));
      this.setDirty();
      return recipe;
   }

   public boolean hasRecipe(UUID uuid) {
      return this.playerMap.containsKey(uuid);
   }

   public PlayerVaultAltarData addRecipe(UUID uuid, AltarInfusionRecipe recipe) {
      this.playerMap.put(uuid, recipe);
      this.setDirty();
      return this;
   }

   public PlayerVaultAltarData removeRecipe(UUID uuid) {
      this.playerMap.remove(uuid);
      this.setDirty();
      return this;
   }

   public List<BlockPos> getAltars(UUID uuid) {
      this.playerAltars.computeIfAbsent(uuid, k -> new ArrayList<>());
      this.setDirty();
      return this.playerAltars.get(uuid);
   }

   public PlayerVaultAltarData addAltar(UUID uuid, BlockPos altarPos) {
      this.getAltars(uuid).add(altarPos);
      this.setDirty();
      return this;
   }

   public PlayerVaultAltarData removeAltar(UUID uuid, BlockPos altarPos) {
      this.getAltars(uuid).remove(altarPos);
      this.setDirty();
      return this;
   }

   private static PlayerVaultAltarData create(CompoundTag tag) {
      PlayerVaultAltarData data = new PlayerVaultAltarData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      ListTag playerList = nbt.getList("PlayerEntries", 8);
      ListTag recipeList = nbt.getList("AltarRecipeEntries", 10);
      ListTag playerBlockPosList = nbt.getList("PlayerBlockPosEntries", 8);
      ListTag blockPosList = nbt.getList("BlockPosEntries", 9);
      if (playerList.size() == recipeList.size() && playerBlockPosList.size() == blockPosList.size()) {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.playerMap.put(playerUUID, AltarInfusionRecipe.deserialize(recipeList.getCompound(i)));
         }

         for (int i = 0; i < playerBlockPosList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerBlockPosList.getString(i));
            List<BlockPos> positions = new ArrayList<>();

            for (Tag compound : blockPosList.getList(i)) {
               CompoundTag posTag = (CompoundTag)compound;
               BlockPos pos = NbtUtils.readBlockPos(posTag);
               positions.add(pos);
            }

            this.playerAltars.put(playerUUID, positions);
         }
      } else {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      }
   }

   public CompoundTag save(CompoundTag nbt) {
      ListTag playerList = new ListTag();
      ListTag recipeList = new ListTag();
      ListTag playerBlockPosList = new ListTag();
      ListTag blockPosList = new ListTag();
      this.playerMap.forEach((uuid, recipe) -> {
         playerList.add(StringTag.valueOf(uuid.toString()));
         recipeList.add(recipe.serialize());
      });
      this.playerAltars.forEach((uuid, altarPositions) -> {
         playerBlockPosList.add(StringTag.valueOf(uuid.toString()));
         ListTag positions = new ListTag();
         altarPositions.forEach(pos -> positions.add(NbtUtils.writeBlockPos(pos)));
         blockPosList.add(positions);
      });
      nbt.put("PlayerEntries", playerList);
      nbt.put("AltarRecipeEntries", recipeList);
      nbt.put("PlayerBlockPosEntries", playerBlockPosList);
      nbt.put("BlockPosEntries", blockPosList);
      return nbt;
   }

   public static PlayerVaultAltarData get(ServerLevel world) {
      return (PlayerVaultAltarData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerVaultAltarData::create, PlayerVaultAltarData::new, "the_vault_PlayerAltarRecipes");
   }
}
