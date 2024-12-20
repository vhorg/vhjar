package iskallia.vault.world.data;

import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.EternalSyncMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class EternalsData extends SavedData {
   protected static final String DATA_NAME = "the_vault_Eternals";
   private final Map<UUID, EternalsData.EternalGroup> playerMap = new HashMap<>();

   public int getTotalEternals() {
      int total = 0;

      for (EternalsData.EternalGroup group : this.playerMap.values()) {
         total = (int)(total + group.getEternals().stream().filter(eternal -> !eternal.isAncient()).count());
      }

      return total;
   }

   @Nonnull
   public EternalsData.EternalGroup getEternals(Player player) {
      return this.getEternals(player.getUUID());
   }

   @Nonnull
   public EternalsData.EternalGroup getEternals(UUID player) {
      return this.playerMap.computeIfAbsent(player, uuid -> new EternalsData.EternalGroup());
   }

   public List<String> getAllEternalNamesExcept(@Nullable String current) {
      Set<String> names = new HashSet<>();

      for (UUID id : this.playerMap.keySet()) {
         EternalsData.EternalGroup group = this.playerMap.get(id);

         for (EternalData data : group.getEternals()) {
            names.add(data.getName());
         }
      }

      if (current != null && !current.isEmpty()) {
         names.remove(current);
      }

      return new ArrayList<>(names);
   }

   public UUID add(UUID owner, String name, boolean isAncient, EternalsData.EternalVariant variant, boolean isUsingPlayerSkin) {
      UUID eternalId = this.getEternals(owner).addEternal(name, isAncient, variant, isUsingPlayerSkin);
      this.setDirty();
      return eternalId;
   }

   @Nullable
   public UUID getOwnerOf(UUID eternalId) {
      return this.playerMap.entrySet().stream().filter(e -> e.getValue().containsEternal(eternalId)).map(Entry::getKey).findFirst().orElse(null);
   }

   @Nullable
   public EternalData getEternal(UUID eternalId) {
      for (EternalsData.EternalGroup eternalGroup : this.playerMap.values()) {
         EternalData eternal = eternalGroup.get(eternalId);
         if (eternal != null) {
            return eternal;
         }
      }

      return null;
   }

   public boolean removeEternal(UUID eternalId) {
      for (EternalsData.EternalGroup eternalGroup : this.playerMap.values()) {
         if (eternalGroup.removeEternal(eternalId)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public EternalData.EquipmentInventory getEternalEquipmentInventory(UUID eternalId, Runnable onChange) {
      EternalData eternal = this.getEternal(eternalId);
      return eternal == null ? null : eternal.getEquipmentInventory(onChange);
   }

   public Map<UUID, List<EternalDataSnapshot>> getEternalDataSnapshots() {
      Map<UUID, List<EternalDataSnapshot>> eternalDataSet = new HashMap<>();
      this.playerMap.forEach((playerUUID, eternalGrp) -> eternalDataSet.put(playerUUID, eternalGrp.getEternalSnapshots()));
      return eternalDataSet;
   }

   public void syncTo(ServerPlayer sPlayer) {
      EternalSyncMessage pkt = new EternalSyncMessage(this.getEternalDataSnapshots());
      ModNetwork.CHANNEL.sendTo(pkt, sPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public void syncAll() {
      EternalSyncMessage pkt = new EternalSyncMessage(this.getEternalDataSnapshots());
      ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), pkt);
   }

   public void setDirty() {
      super.setDirty();
      this.syncAll();
   }

   private static EternalsData create(CompoundTag tag) {
      EternalsData data = new EternalsData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      ListTag playerList = nbt.getList("PlayerEntries", 8);
      ListTag eternalsList = nbt.getList("EternalEntries", 10);
      if (playerList.size() != eternalsList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getEternals(playerUUID).deserializeNBT(eternalsList.getCompound(i));
         }
      }
   }

   @NotNull
   public CompoundTag save(CompoundTag nbt) {
      ListTag playerList = new ListTag();
      ListTag eternalsList = new ListTag();
      this.playerMap.forEach((uuid, eternalGroup) -> {
         playerList.add(StringTag.valueOf(uuid.toString()));
         eternalsList.add(eternalGroup.serializeNBT());
      });
      nbt.put("PlayerEntries", playerList);
      nbt.put("EternalEntries", eternalsList);
      return nbt;
   }

   public static EternalsData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static EternalsData get(MinecraftServer srv) {
      return (EternalsData)srv.overworld().getDataStorage().computeIfAbsent(EternalsData::create, EternalsData::new, "the_vault_Eternals");
   }

   public boolean isDirty() {
      return true;
   }

   public class EternalGroup implements INBTSerializable<CompoundTag> {
      private final Map<UUID, EternalData> eternals = new HashMap<>();

      public List<EternalData> getEternals() {
         return new ArrayList<>(this.eternals.values());
      }

      public int getNonAncientEternalCount() {
         return (int)this.eternals.entrySet().stream().filter(entry -> !entry.getValue().isAncient()).count();
      }

      public UUID addEternal(String name, boolean isAncient, EternalsData.EternalVariant variant, boolean isUsingPlayerSkin) {
         return this.addEternal(EternalData.createEternal(EternalsData.this, name, isAncient, variant, isUsingPlayerSkin)).getId();
      }

      private EternalData addEternal(EternalData newEternal) {
         this.eternals.put(newEternal.getId(), newEternal);
         this.eternals.values().forEach(eternal -> {
            if (eternal.isAncient()) {
               eternal.setLevel(eternal.getMaxLevel());
            }
         });
         return newEternal;
      }

      @Nullable
      public EternalData get(UUID eternalId) {
         return this.eternals.get(eternalId);
      }

      public boolean containsEternal(UUID eternalId) {
         return this.get(eternalId) != null;
      }

      public boolean containsEternal(String name) {
         for (EternalData eternal : this.eternals.values()) {
            if (eternal.getName().equalsIgnoreCase(name)) {
               return true;
            }
         }

         return false;
      }

      public boolean containsOriginalEternal(String name, boolean onlyAncients) {
         for (EternalData eternal : this.eternals.values()) {
            if ((!onlyAncients || eternal.isAncient()) && eternal.getOriginalName().equalsIgnoreCase(name)) {
               return true;
            }
         }

         return false;
      }

      public boolean removeEternal(UUID eternalId) {
         EternalData eternal = this.eternals.remove(eternalId);
         if (eternal != null) {
            EternalsData.this.setDirty();
            return true;
         } else {
            return false;
         }
      }

      @Nullable
      public EternalData getRandomAlive(Random random, Predicate<EternalData> eternalFilter) {
         List<EternalData> aliveEternals = this.getEternals().stream().filter(EternalData::isAlive).filter(eternalFilter).collect(Collectors.toList());
         return aliveEternals.isEmpty() ? null : aliveEternals.get(random.nextInt(aliveEternals.size()));
      }

      @Nullable
      public EternalData getRandomAliveAncient(Random random, Predicate<EternalData> eternalFilter) {
         List<EternalData> aliveEternals = this.getEternals()
            .stream()
            .filter(EternalData::isAlive)
            .filter(EternalData::isAncient)
            .filter(eternalFilter)
            .collect(Collectors.toList());
         return aliveEternals.isEmpty() ? null : aliveEternals.get(random.nextInt(aliveEternals.size()));
      }

      public List<EternalDataSnapshot> getEternalSnapshots() {
         List<EternalDataSnapshot> snapshots = new ArrayList<>();
         this.getEternals().forEach(eternal -> snapshots.add(this.getEternalSnapshot(eternal)));
         return snapshots;
      }

      @Nullable
      public EternalDataSnapshot getEternalSnapshot(UUID eternalId) {
         EternalData eternal = this.get(eternalId);
         return eternal == null ? null : this.getEternalSnapshot(eternal);
      }

      public EternalDataSnapshot getEternalSnapshot(EternalData eternal) {
         return EternalDataSnapshot.getFromEternal(this, eternal);
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         ListTag eternalsList = new ListTag();
         this.eternals.values().forEach(eternal -> eternalsList.add(eternal.serializeNBT()));
         nbt.put("EternalsList", eternalsList);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.eternals.clear();
         ListTag eternalsList = nbt.getList("EternalsList", 10);

         for (int i = 0; i < eternalsList.size(); i++) {
            this.addEternal(EternalData.fromNBT(EternalsData.this, eternalsList.getCompound(i)));
         }
      }
   }

   public static enum EternalVariant {
      CAVE(0),
      DESERT(1),
      HELL(2),
      ICE(3),
      LUSH(4),
      VOID(5);

      private static final EternalsData.EternalVariant[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(EternalsData.EternalVariant::getId))
         .toArray(EternalsData.EternalVariant[]::new);
      private final int id;

      private EternalVariant(int pId) {
         this.id = pId;
      }

      public int getId() {
         return this.id;
      }

      public static EternalsData.EternalVariant byId(int pId) {
         return BY_ID[pId % BY_ID.length];
      }
   }
}
