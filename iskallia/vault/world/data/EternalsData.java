package iskallia.vault.world.data;

import com.mojang.datafixers.util.Either;
import iskallia.vault.entity.EternalData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.INBTSerializable;

public class EternalsData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_Eternals";
   private Map<UUID, EternalsData.EternalGroup> playerMap = new HashMap<>();

   public EternalsData() {
      this("the_vault_Eternals");
   }

   public EternalsData(String name) {
      super(name);
   }

   public int getTotalEternals() {
      int total = 0;

      for (EternalsData.EternalGroup group : this.playerMap.values()) {
         total += group.getEternals().size();
      }

      return total;
   }

   public EternalsData.EternalGroup getEternals(PlayerEntity player) {
      return this.getEternals(player.func_110124_au());
   }

   public EternalsData.EternalGroup getEternals(UUID player) {
      return this.playerMap.computeIfAbsent(player, uuid -> new EternalsData.EternalGroup());
   }

   public EternalsData.EternalGroup getEternals(Either<UUID, PlayerEntity> owner) {
      return owner.left().isPresent() ? this.getEternals((UUID)owner.left().get()) : this.getEternals((PlayerEntity)owner.right().get());
   }

   public List<String> getAllEternalNamesExcept(String current) {
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

   public UUID add(UUID owner, String name) {
      UUID eternalId = this.getEternals(owner).addEternal(name);
      this.func_76185_a();
      return eternalId;
   }

   public UUID getOwnerOf(UUID eternalId) {
      return this.playerMap
         .entrySet()
         .stream()
         .filter(e -> e.getValue().getEternals().stream().map(EternalData::getId).filter(id -> id.equals(eternalId)).findFirst().orElse(null) != null)
         .map(Entry::getKey)
         .findFirst()
         .orElse(null);
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
      return (EternalsData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(EternalsData::new, "the_vault_Eternals");
   }

   public boolean func_76188_b() {
      return true;
   }

   public static class EternalGroup implements INBTSerializable<CompoundNBT> {
      private Map<UUID, EternalData> eternals = new HashMap<>();

      public List<EternalData> getEternals() {
         return new ArrayList<>(this.eternals.values());
      }

      public UUID addEternal(String name) {
         return this.addEternal(new EternalData(name)).getId();
      }

      private EternalData addEternal(EternalData eternal) {
         this.eternals.put(eternal.getId(), eternal);
         return eternal;
      }

      public EternalData getFromId(UUID eternalId) {
         return this.eternals.get(eternalId);
      }

      public EternalData getRandom(Random random) {
         return this.eternals.isEmpty() ? null : this.getEternals().get(random.nextInt(this.eternals.size()));
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
            this.addEternal(EternalData.fromNBT(eternalsList.func_150305_b(i)));
         }
      }
   }
}
