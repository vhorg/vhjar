package iskallia.vault.world.data;

import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.util.calc.GodAffinityHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PlayerReputationData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerInfluences";
   protected VMapNBT<UUID, PlayerReputationData.Entry> entries = VMapNBT.ofUUID(PlayerReputationData.Entry::new);

   public static void attemptFavour(Player player, VaultGod god, RandomSource random) {
      float chance = GodAffinityHelper.getAffinityPercent(player, god);
      if (!(random.nextFloat() >= chance)) {
         BlockPos pos = player.blockPosition();
         player.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.FAVOUR_UP, SoundSource.PLAYERS, 0.4F, 0.7F);
         Component msg = new TextComponent("You gained a favour!").withStyle(god.getChatColor());
         player.displayClientMessage(msg, true);
         addReputation(player.getUUID(), god, 1);
      }
   }

   public static int getReputation(UUID player, VaultGod god) {
      return !get().entries.containsKey(player) ? 0 : get().entries.get(player).reputation.getOrDefault(god, 0);
   }

   public static void addReputation(UUID playerUUID, VaultGod god, int reputation) {
      PlayerReputationData.Entry entry = get().entries.computeIfAbsent(playerUUID, uuid -> new PlayerReputationData.Entry());
      entry.addReputation(god, reputation);
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      ServerPlayer player = server.getPlayerList().getPlayer(playerUUID);
      if (player != null) {
         entry.reputation.forEach((vaultGod, updatedReputation) -> {
            if (updatedReputation >= 25) {
               DiscoveredModelsData discoveredModelsData = DiscoveredModelsData.get(server);
               if (vaultGod == VaultGod.IDONA) {
                  discoveredModelsData.discoverAllArmorPieceAndBroadcast(player, ModDynamicModels.Armor.IDONAS_ARMOUR);
                  discoveredModelsData.discoverModelAndBroadcast(ModItems.SWORD, ModDynamicModels.Swords.IDONAS_SWORD.getId(), player);
                  discoveredModelsData.discoverModelAndBroadcast(ModItems.AXE, ModDynamicModels.Axes.IDONAS_SCYTHE.getId(), player);
               } else if (vaultGod == VaultGod.TENOS) {
                  discoveredModelsData.discoverAllArmorPieceAndBroadcast(player, ModDynamicModels.Armor.TENOS_ARMOUR);
                  discoveredModelsData.discoverModelAndBroadcast(ModItems.AXE, ModDynamicModels.Axes.TENOS_STAFF.getId(), player);
               } else if (vaultGod == VaultGod.VELARA) {
                  discoveredModelsData.discoverAllArmorPieceAndBroadcast(player, ModDynamicModels.Armor.VELARAS_ARMOUR);
                  discoveredModelsData.discoverModelAndBroadcast(ModItems.SWORD, ModDynamicModels.Swords.VELARAS_GREATSWORD.getId(), player);
                  discoveredModelsData.discoverModelAndBroadcast(ModItems.AXE, ModDynamicModels.Axes.VELARAS_HAMMER.getId(), player);
               } else if (vaultGod == VaultGod.WENDARR) {
                  discoveredModelsData.discoverAllArmorPieceAndBroadcast(player, ModDynamicModels.Armor.WENDARRS_ARMOUR);
                  discoveredModelsData.discoverModelAndBroadcast(ModItems.SWORD, ModDynamicModels.Swords.WENDARRS_GREATSWORD.getId(), player);
                  discoveredModelsData.discoverModelAndBroadcast(ModItems.AXE, ModDynamicModels.Axes.WENDARRS_CLOCKAXE.getId(), player);
               }
            }
         });
      }
   }

   private static PlayerReputationData load(CompoundTag nbt) {
      PlayerReputationData data = new PlayerReputationData();
      data.entries.deserializeNBT(nbt.getList("entries", 10));
      return data;
   }

   public boolean isDirty() {
      return true;
   }

   @Nonnull
   public CompoundTag save(CompoundTag nbt) {
      nbt.put("entries", this.entries.serializeNBT());
      return nbt;
   }

   public static PlayerReputationData get() {
      return (PlayerReputationData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerReputationData::load, PlayerReputationData::new, "the_vault_PlayerInfluences");
   }

   private static class Entry implements INBTSerializable<CompoundTag> {
      private final Map<VaultGod, Integer> reputation = new HashMap<>();
      private int version = 1;

      public void addReputation(VaultGod god, int reputation) {
         reputation = Math.min(25 - this.reputation.getOrDefault(god, 0), reputation);
         this.reputation.put(god, this.reputation.getOrDefault(god, 0) + reputation);
         int total = this.reputation.values().stream().mapToInt(Integer::intValue).sum();
         Random random = new Random();

         for (int i = 0; i < total - 25; i++) {
            List<VaultGod> gods = new ArrayList<>(Arrays.asList(VaultGod.values()));
            gods.remove(god);
            gods.removeIf(god1 -> this.reputation.getOrDefault(god1, 0) <= 0);
            if (!gods.isEmpty()) {
               VaultGod remove = gods.get(random.nextInt(gods.size()));
               this.reputation.put(remove, this.reputation.getOrDefault(remove, 0) - 1);
            }
         }
      }

      public void migrate() {
         if (this.version == 0) {
            this.reputation.clear();
            this.version = 1;
         }
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         ListTag reputationList = new ListTag();
         this.reputation.forEach((vaultGod, reputation) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString("God", vaultGod.getName());
            entry.putInt("Reputation", reputation);
            reputationList.add(entry);
         });
         nbt.put("Reputations", reputationList);
         nbt.putInt("Version", this.version);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.reputation.clear();
         ListTag reputationList = nbt.getList("Reputations", 10);

         for (int i = 0; i < reputationList.size(); i++) {
            CompoundTag entry = reputationList.getCompound(i);
            this.reputation.put(VaultGod.fromName(entry.getString("God")), entry.getInt("Reputation"));
         }

         this.version = nbt.getInt("Version");
         this.migrate();
      }
   }
}
