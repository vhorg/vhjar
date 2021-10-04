package iskallia.vault.world.data;

import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.EternalSyncMessage;
import java.util.ArrayList;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

public class EternalsData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_Eternals";
   private final Map<UUID, EternalsData.EternalGroup> playerMap = new HashMap<>();

   public EternalsData() {
      this("the_vault_Eternals");
   }

   public EternalsData(String name) {
      super(name);
   }

   public int getTotalEternals() {
      int total = 0;

      for (EternalsData.EternalGroup group : this.playerMap.values()) {
         total = (int)(total + group.getEternals().stream().filter(eternal -> !eternal.isAncient()).count());
      }

      return total;
   }

   @Nonnull
   public EternalsData.EternalGroup getEternals(PlayerEntity player) {
      return this.getEternals(player.func_110124_au());
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

   public UUID add(UUID owner, String name, boolean isAncient) {
      UUID eternalId = this.getEternals(owner).addEternal(name, isAncient);
      this.func_76185_a();
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
      this.playerMap.forEach((playerUUID, eternalGrp) -> {
         List var10000 = eternalDataSet.put(playerUUID, eternalGrp.getEternalSnapshots());
      });
      return eternalDataSet;
   }

   public void syncTo(ServerPlayerEntity sPlayer) {
      EternalSyncMessage pkt = new EternalSyncMessage(this.getEternalDataSnapshots());
      ModNetwork.CHANNEL.sendTo(pkt, sPlayer.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
   }

   public void syncAll() {
      EternalSyncMessage pkt = new EternalSyncMessage(this.getEternalDataSnapshots());
      ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), pkt);
   }

   public void func_76185_a() {
      super.func_76185_a();
      this.syncAll();
   }

   public void func_76184_a(CompoundNBT nbt) {
      ListNBT playerList = nbt.func_150295_c("PlayerEntries", 8);
      ListNBT eternalsList = nbt.func_150295_c("EternalEntries", 10);
      if (playerList.size() != eternalsList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.func_150307_f(i));
            this.getEternals(playerUUID).deserializeNBT(eternalsList.func_150305_b(i));
         }
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      ListNBT playerList = new ListNBT();
      ListNBT eternalsList = new ListNBT();
      this.playerMap.forEach((uuid, eternalGroup) -> {
         playerList.add(StringNBT.func_229705_a_(uuid.toString()));
         eternalsList.add(eternalGroup.serializeNBT());
      });
      nbt.func_218657_a("PlayerEntries", playerList);
      nbt.func_218657_a("EternalEntries", eternalsList);
      return nbt;
   }

   public static EternalsData get(ServerWorld world) {
      return get(world.func_73046_m());
   }

   public static EternalsData get(MinecraftServer srv) {
      return (EternalsData)srv.func_241755_D_().func_217481_x().func_215752_a(EternalsData::new, "the_vault_Eternals");
   }

   public boolean func_76188_b() {
      return true;
   }

   public class EternalGroup implements INBTSerializable<CompoundNBT> {
      private final Map<UUID, EternalData> eternals = new HashMap<>();

      public List<EternalData> getEternals() {
         return new ArrayList<>(this.eternals.values());
      }

      public int getNonAncientEternalCount() {
         return (int)this.eternals.entrySet().stream().filter(entry -> !entry.getValue().isAncient()).count();
      }

      public UUID addEternal(String name, boolean isAncient) {
         return this.addEternal(EternalData.createEternal(EternalsData.this, name, isAncient)).getId();
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
            EternalsData.this.func_76185_a();
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

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         ListNBT eternalsList = new ListNBT();
         this.eternals.values().forEach(eternal -> eternalsList.add(eternal.serializeNBT()));
         nbt.func_218657_a("EternalsList", eternalsList);
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.eternals.clear();
         ListNBT eternalsList = nbt.func_150295_c("EternalsList", 10);

         for (int i = 0; i < eternalsList.size(); i++) {
            this.addEternal(EternalData.fromNBT(EternalsData.this, eternalsList.func_150305_b(i)));
         }
      }
   }
}
