package iskallia.vault.world.data;

import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

public class PlayerSpiritRecoveryData extends SavedData {
   private static final String DATA_NAME = "the_vault_PlayerSpiritRecovery";
   private static final String PLAYER_DROPS_TAG = "playerDrops";
   private static final String SPIRIT_RECOVERY_MULTIPLIERS_TAG = "spiritRecoveryMultipliers";
   private static final String HERO_DISCOUNTS_TAG = "heroDiscounts";
   private static final String LAST_VAULT_LEVELS_TAG = "lastVaultLevels";
   private final Map<UUID, List<ItemStack>> playerDrops = new HashMap<>();
   private final Map<UUID, Integer> lastVaultLevels = new HashMap<>();
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

   public void addDrop(UUID playerId, ItemStack dropStack) {
      this.playerDrops.computeIfAbsent(playerId, id -> new ArrayList<>()).add(dropStack);
      this.setDirty();
   }

   public void removeDrops(UUID playerId) {
      this.playerDrops.remove(playerId);
      this.setDirty();
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

      return compoundTag;
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
      this.playerDrops.forEach((playerId, drops) -> tag.put(playerId.toString(), this.serializeStacks((List<ItemStack>)drops)));
      return tag;
   }

   private ListTag serializeStacks(List<ItemStack> lootStacks) {
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

   public List<ItemStack> getDrops(UUID playerId) {
      return this.playerDrops.getOrDefault(playerId, Collections.emptyList());
   }

   public void setLastVaultLevel(UUID playerId, int vaultLevel) {
      this.lastVaultLevels.put(playerId, vaultLevel);
      this.setDirty();
   }

   public void removeLastVaultLevel(UUID playerId) {
      this.lastVaultLevels.remove(playerId);
      this.setDirty();
   }

   public Optional<Integer> getLastVaultLevel(UUID playerId) {
      return Optional.ofNullable(this.lastVaultLevels.get(playerId));
   }
}
