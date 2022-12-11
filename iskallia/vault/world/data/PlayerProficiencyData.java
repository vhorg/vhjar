package iskallia.vault.world.data;

import iskallia.vault.config.gear.VaultGearCraftingConfig;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ProficiencyMessage;
import iskallia.vault.util.MiscUtils;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkDirection;

public class PlayerProficiencyData extends SavedData {
   private static final Random rand = new Random();
   protected static final String DATA_NAME = "the_vault_PlayerProficiency";
   private final Map<UUID, Map<ProficiencyType, Integer>> playerProficiencies = new HashMap<>();

   private PlayerProficiencyData() {
   }

   private PlayerProficiencyData(CompoundTag tag) {
      this.load(tag);
   }

   public Integer getProficiency(Player player, ProficiencyType type) {
      return this.getProficiency(player.getUUID(), type);
   }

   public Integer getProficiency(UUID playerId, ProficiencyType type) {
      return type == ProficiencyType.UNKNOWN ? 0 : this.playerProficiencies.getOrDefault(playerId, Collections.emptyMap()).getOrDefault(type, 0);
   }

   public void setProficiency(UUID playerId, ProficiencyType type, int value) {
      if (type != ProficiencyType.UNKNOWN) {
         VaultGearCraftingConfig cfg = ModConfigs.VAULT_GEAR_CRAFTING_CONFIG;
         this.playerProficiencies
            .computeIfAbsent(playerId, id -> new EnumMap<>(ProficiencyType.class))
            .put(type, Mth.clamp(value, 0, cfg.getTotalCategoryProficiency()));
         this.setDirty();
      }
   }

   public void updateProficiency(ServerPlayer player, ProficiencyType type, int value) {
      if (type != ProficiencyType.UNKNOWN) {
         UUID playerId = player.getUUID();
         int thisProficiency = this.getProficiency(playerId, type);
         VaultGearCraftingConfig cfg = ModConfigs.VAULT_GEAR_CRAFTING_CONFIG;
         int totalProficiency = this.getTotalProficiency(playerId);
         int availableProficiencyGain = Math.max(cfg.getTotalCategoryProficiency() - thisProficiency, 0);
         int valueToAdd = Math.min(availableProficiencyGain, value);
         this.playerProficiencies.computeIfAbsent(playerId, id -> new EnumMap<>(ProficiencyType.class)).put(type, thisProficiency + valueToAdd);
         int valueToCut = Math.max(totalProficiency + valueToAdd - cfg.getTotalMaximumProficiency(), 0);
         this.removeRandomProficiencyExcluding(playerId, valueToCut, type);
         this.sendProficiencyInformation(player);
         this.setDirty();
      }
   }

   private int getTotalProficiency(UUID playerId) {
      return this.playerProficiencies.getOrDefault(playerId, Collections.emptyMap()).values().stream().mapToInt(i -> i).sum();
   }

   private void removeRandomProficiencyExcluding(UUID playerId, int amount, ProficiencyType excluded) {
      if (amount > 0) {
         Map<ProficiencyType, Integer> proficiencies = this.playerProficiencies.getOrDefault(playerId, Collections.emptyMap());

         for (int i = 0; i < amount; i++) {
            List<ProficiencyType> proficiencyTypes = proficiencies.entrySet()
               .stream()
               .filter(entry -> entry.getKey() != excluded)
               .filter(entry -> entry.getValue() > 0)
               .map(Entry::getKey)
               .toList();
            ProficiencyType randomType = MiscUtils.getRandomEntry(proficiencyTypes, rand);
            if (randomType != null) {
               proficiencies.put(randomType, Math.max(proficiencies.get(randomType) - 1, 0));
            }
         }
      }
   }

   public void sendProficiencyInformation(ServerPlayer player) {
      float maxProficiency = ModConfigs.VAULT_GEAR_CRAFTING_CONFIG.getTotalCategoryProficiency();
      Map<ProficiencyType, Float> percentages = new HashMap<>();

      for (ProficiencyType type : ProficiencyType.values()) {
         int amount = this.getProficiency(player, type);
         percentages.put(type, Mth.clamp(amount / maxProficiency, 0.0F, 1.0F));
      }

      ModNetwork.CHANNEL.sendTo(new ProficiencyMessage(percentages), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   protected void load(CompoundTag tag) {
      this.playerProficiencies.clear();

      for (String playerIdStr : tag.getAllKeys()) {
         UUID playerId = UUID.fromString(playerIdStr);
         EnumMap<ProficiencyType, Integer> proficiencies = new EnumMap<>(ProficiencyType.class);
         CompoundTag proficiencyTag = tag.getCompound(playerIdStr);

         for (String proficiencyName : proficiencyTag.getAllKeys()) {
            ProficiencyType type = ProficiencyType.valueOf(proficiencyName);
            proficiencies.put(type, proficiencyTag.getInt(proficiencyName));
         }

         this.playerProficiencies.put(playerId, proficiencies);
      }
   }

   public CompoundTag save(CompoundTag tag) {
      this.playerProficiencies.forEach((playerId, proficiencies) -> {
         CompoundTag proficiencyTag = new CompoundTag();
         proficiencies.forEach((type, amount) -> proficiencyTag.put(type.name(), IntTag.valueOf(amount)));
         tag.put(playerId.toString(), proficiencyTag);
      });
      return tag;
   }

   public static PlayerProficiencyData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static PlayerProficiencyData get(MinecraftServer server) {
      return (PlayerProficiencyData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerProficiencyData::new, PlayerProficiencyData::new, "the_vault_PlayerProficiency");
   }
}
