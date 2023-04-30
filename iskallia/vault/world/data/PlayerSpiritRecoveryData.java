package iskallia.vault.world.data;

import com.mojang.authlib.GameProfile;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class PlayerSpiritRecoveryData extends SavedData {
   private static final String DATA_NAME = "the_vault_PlayerSpiritRecovery";
   private static final String PLAYER_DROPS_TAG = "playerDrops";
   private static final String SPIRIT_RECOVERY_MULTIPLIERS_TAG = "spiritRecoveryMultipliers";
   private static final String HERO_DISCOUNTS_TAG = "heroDiscounts";
   private static final String LAST_VAULT_LEVELS_TAG = "lastVaultLevels";
   private static final String VAULT_SPIRIT_DATA_TAG = "vaultSpiritData";
   private static final String PLAYER_IMMEDIATE_RESPAWN_SPIRIT_DATA_TAG = "playerImmediateRespawnSpiritData";
   @Deprecated
   private final Map<UUID, List<ItemStack>> playerDrops = new HashMap<>();
   @Deprecated
   private final Map<UUID, Integer> lastVaultLevels = new HashMap<>();
   private final Map<UUID, Set<PlayerSpiritRecoveryData.SpiritData>> vaultSpiritData = new HashMap<>();
   private final Map<UUID, Float> spiritRecoveryMultipliers = new HashMap<>();
   private final Map<UUID, Float> heroDiscounts = new HashMap<>();
   private final Map<UUID, Integer> playerSpiritRecoveries = new HashMap<>();

   public static PlayerSpiritRecoveryData get(ServerLevel world) {
      return (PlayerSpiritRecoveryData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerSpiritRecoveryData::create, PlayerSpiritRecoveryData::new, "the_vault_PlayerSpiritRecovery");
   }

   private static PlayerSpiritRecoveryData create(CompoundTag tag) {
      PlayerSpiritRecoveryData data = new PlayerSpiritRecoveryData();
      data.load(tag);
      return data;
   }

   @Deprecated
   public void removeDrops(UUID playerId) {
      this.playerDrops.remove(playerId);
      this.setDirty();
   }

   @Deprecated
   public void removeLastVaultLevel(UUID playerId) {
      this.lastVaultLevels.remove(playerId);
      this.setDirty();
   }

   @Deprecated
   public Optional<Integer> getLastVaultLevel(UUID playerId) {
      return Optional.ofNullable(this.lastVaultLevels.get(playerId));
   }

   @Deprecated
   public List<ItemStack> getDrops(UUID playerId) {
      return this.playerDrops.getOrDefault(playerId, Collections.emptyList());
   }

   private void load(CompoundTag tag) {
      this.playerSpiritRecoveries.clear();
      ListTag data = tag.getList("PlayerData", 10);
      data.forEach(t -> {
         if (t instanceof CompoundTag entry) {
            this.playerSpiritRecoveries.put(entry.getUUID("uuid"), entry.getInt("count"));
         }
      });
      this.playerDrops.clear();
      if (tag.contains("playerDrops", 10)) {
         CompoundTag playerDropsTag = tag.getCompound("playerDrops");
         playerDropsTag.getAllKeys().forEach(playerId -> {
            ListTag dropsListTag = playerDropsTag.getList(playerId, 10);
            List<ItemStack> drops = new ArrayList<>();
            dropsListTag.forEach(dropTag -> drops.add(ItemStack.of((CompoundTag)dropTag)));
            this.playerDrops.put(UUID.fromString(playerId), drops);
         });
      }

      this.spiritRecoveryMultipliers.clear();
      if (tag.contains("spiritRecoveryMultipliers")) {
         CompoundTag multipliersTag = tag.getCompound("spiritRecoveryMultipliers");
         multipliersTag.getAllKeys().forEach(playerId -> this.spiritRecoveryMultipliers.put(UUID.fromString(playerId), multipliersTag.getFloat(playerId)));
      }

      if (tag.contains("heroDiscounts")) {
         CompoundTag discountsTag = tag.getCompound("heroDiscounts");
         discountsTag.getAllKeys().forEach(playerId -> this.heroDiscounts.put(UUID.fromString(playerId), discountsTag.getFloat(playerId)));
      }

      this.lastVaultLevels.clear();
      if (tag.contains("lastVaultLevels")) {
         CompoundTag levelsTag = tag.getCompound("lastVaultLevels");
         levelsTag.getAllKeys().forEach(playerId -> this.lastVaultLevels.put(UUID.fromString(playerId), levelsTag.getInt(playerId)));
      }

      this.vaultSpiritData.clear();
      if (tag.contains("vaultSpiritData")) {
         CompoundTag dataTag = tag.getCompound("vaultSpiritData");
         dataTag.getAllKeys()
            .forEach(
               playerId -> {
                  CompoundTag vaultSpiritDataTag = dataTag.getCompound(playerId);
                  Set<PlayerSpiritRecoveryData.SpiritData> vaultSpiritDataSet = new HashSet<>();
                  vaultSpiritDataTag.getAllKeys()
                     .forEach(vaultId -> vaultSpiritDataSet.add(PlayerSpiritRecoveryData.SpiritData.deserialize(vaultSpiritDataTag.getCompound(vaultId))));
                  this.vaultSpiritData.put(UUID.fromString(playerId), vaultSpiritDataSet);
               }
            );
      }
   }

   public CompoundTag save(CompoundTag compoundTag) {
      compoundTag.put("PlayerData", this.serializeSpiritRecoveries());
      if (!this.playerDrops.isEmpty()) {
         compoundTag.put("playerDrops", this.serializePlayerDrops());
      }

      if (!this.spiritRecoveryMultipliers.isEmpty()) {
         compoundTag.put("spiritRecoveryMultipliers", this.serializeSpiritRecoveryMultipliers());
      }

      if (!this.heroDiscounts.isEmpty()) {
         compoundTag.put("heroDiscounts", this.serializeHeroDiscounts());
      }

      if (!this.lastVaultLevels.isEmpty()) {
         compoundTag.put("lastVaultLevels", this.serializeLastVaultLevels());
      }

      if (!this.vaultSpiritData.isEmpty()) {
         compoundTag.put("vaultSpiritData", this.serializeVaultSpiritData());
      }

      return compoundTag;
   }

   private CompoundTag serializeVaultSpiritData() {
      CompoundTag ret = new CompoundTag();
      this.vaultSpiritData.forEach((playerId, dataSet) -> {
         CompoundTag dataNbt = new CompoundTag();
         dataSet.forEach(data -> dataNbt.put(data.vaultId.toString(), data.serialize()));
         ret.put(playerId.toString(), dataNbt);
      });
      return ret;
   }

   private ListTag serializeSpiritRecoveries() {
      ListTag spiritRecoveries = new ListTag();
      this.playerSpiritRecoveries.forEach((uuid, count) -> {
         CompoundTag entry = new CompoundTag();
         entry.putUUID("uuid", uuid);
         entry.putInt("count", count);
         spiritRecoveries.add(entry);
      });
      return spiritRecoveries;
   }

   private CompoundTag serializeHeroDiscounts() {
      CompoundTag tag = new CompoundTag();
      this.heroDiscounts.forEach((playerId, discount) -> tag.putFloat(playerId.toString(), discount));
      return tag;
   }

   private CompoundTag serializePlayerDrops() {
      CompoundTag tag = new CompoundTag();
      this.playerDrops.forEach((playerId, drops) -> tag.put(playerId.toString(), serializeStacks((List<ItemStack>)drops)));
      return tag;
   }

   private static ListTag serializeStacks(List<ItemStack> lootStacks) {
      ListTag lootStacksNbt = new ListTag();
      lootStacks.forEach(stack -> lootStacksNbt.add(stack.save(new CompoundTag())));
      return lootStacksNbt;
   }

   private CompoundTag serializeSpiritRecoveryMultipliers() {
      CompoundTag tag = new CompoundTag();
      this.spiritRecoveryMultipliers.forEach((playerId, multiplier) -> tag.putFloat(playerId.toString(), multiplier));
      return tag;
   }

   private CompoundTag serializeLastVaultLevels() {
      CompoundTag tag = new CompoundTag();
      this.lastVaultLevels.forEach((playerId, level) -> tag.putInt(playerId.toString(), level));
      return tag;
   }

   public void setSpiritRecoveryMultiplier(UUID playerId, float multiplier) {
      this.spiritRecoveryMultipliers.put(playerId, Math.max(1.0F, multiplier));
      this.setDirty();
   }

   public void increaseMultiplierOnRecovery(UUID playerId) {
      this.setSpiritRecoveryMultiplier(playerId, this.getSpiritRecoveryMultiplier(playerId) + ModConfigs.SPIRIT.perRecoveryMultiplierIncrease);
      this.playerSpiritRecoveries.compute(playerId, (uuid, count) -> count == null ? 1 : Integer.valueOf(count + 1));
      this.setDirty();
   }

   public int getSpiritRecoveryCount(UUID playerUuid) {
      return this.playerSpiritRecoveries.getOrDefault(playerUuid, 0);
   }

   public void decreaseMultiplierOnCompletion(UUID playerId) {
      this.setSpiritRecoveryMultiplier(playerId, this.getSpiritRecoveryMultiplier(playerId) * ModConfigs.SPIRIT.getCompletionMultiplierDecrease());
   }

   public void setHeroDiscount(UUID heroId, Random random) {
      this.heroDiscounts.put(heroId, Math.max(this.getHeroDiscount(heroId), ModConfigs.SPIRIT.getHeroDiscount(random)));
      this.setDirty();
   }

   public float getHeroDiscount(UUID heroId) {
      return this.heroDiscounts.getOrDefault(heroId, 0.0F);
   }

   public void removeHeroDiscount(UUID playerId) {
      this.heroDiscounts.remove(playerId);
      this.setDirty();
   }

   public float getSpiritRecoveryMultiplier(UUID playerId) {
      return this.spiritRecoveryMultipliers.getOrDefault(playerId, 1.0F);
   }

   public void putVaultSpiritData(PlayerSpiritRecoveryData.SpiritData spiritData) {
      this.vaultSpiritData.computeIfAbsent(spiritData.vaultId, u -> new HashSet<>()).add(spiritData);
      this.setDirty();
   }

   public void removeVaultSpiritData(UUID vaultId) {
      this.vaultSpiritData.remove(vaultId);
      this.setDirty();
   }

   public void removeVaultSpiritData(UUID playerId, UUID vaultId) {
      this.vaultSpiritData.computeIfPresent(vaultId, (p, dataSet) -> {
         dataSet.removeIf(data -> data.playerId.equals(playerId));
         return dataSet;
      });
      this.setDirty();
   }

   public Set<PlayerSpiritRecoveryData.SpiritData> getVaultSpiritData(UUID vaultId) {
      return this.vaultSpiritData.getOrDefault(vaultId, Collections.emptySet());
   }

   public record SpiritData(
      UUID vaultId,
      UUID playerId,
      List<ItemStack> drops,
      int vaultLevel,
      int playerLevel,
      ResourceKey<Level> respawnDimension,
      BlockPos respawnPos,
      GameProfile playerGameProfile
   ) {
      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.putUUID("vaultId", this.vaultId);
         tag.putUUID("playerId", this.playerId);
         tag.put("drops", PlayerSpiritRecoveryData.serializeStacks(this.drops));
         tag.putInt("vaultLevel", this.vaultLevel);
         tag.putInt("playerLevel", this.playerLevel);
         ResourceLocation.CODEC
            .encodeStart(NbtOps.INSTANCE, this.respawnDimension.location())
            .resultOrPartial(VaultMod.LOGGER::error)
            .ifPresent(dimNbt -> tag.put("respawnDimension", dimNbt));
         tag.putLong("respawnPos", this.respawnPos.asLong());
         tag.put("playerGameProfile", NbtUtils.writeGameProfile(new CompoundTag(), this.playerGameProfile));
         return tag;
      }

      public static PlayerSpiritRecoveryData.SpiritData deserialize(CompoundTag tag) {
         return new PlayerSpiritRecoveryData.SpiritData(
            tag.getUUID("vaultId"),
            tag.getUUID("playerId"),
            deserializeStacks(tag.getList("drops", 10)),
            tag.getInt("vaultLevel"),
            tag.getInt("playerLevel"),
            Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get("respawnDimension")).resultOrPartial(VaultMod.LOGGER::error).orElse(Level.OVERWORLD),
            BlockPos.of(tag.getLong("respawnPos")),
            NbtUtils.readGameProfile(tag.getCompound("playerGameProfile"))
         );
      }

      private static List<ItemStack> deserializeStacks(ListTag lootStacksNbt) {
         List<ItemStack> lootStacks = new ArrayList<>();
         lootStacksNbt.forEach(nbt -> lootStacks.add(ItemStack.of((CompoundTag)nbt)));
         return lootStacks;
      }
   }
}
