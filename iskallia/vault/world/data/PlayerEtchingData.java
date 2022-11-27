package iskallia.vault.world.data;

import iskallia.vault.etching.EtchingHelper;
import iskallia.vault.etching.EtchingRegistry;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.util.NetcodeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

public class PlayerEtchingData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerEtchings";
   private final Map<UUID, List<EtchingSet<?>>> equippedEtchingSets = new HashMap<>();

   private PlayerEtchingData() {
   }

   private PlayerEtchingData(CompoundTag tag) {
      this.load(tag);
   }

   public List<EtchingSet<?>> getEtchingSets(Player player) {
      return this.getEtchingSets(player.getUUID());
   }

   private List<EtchingSet<?>> getEtchingSets(UUID playerId) {
      return this.equippedEtchingSets.getOrDefault(playerId, new ArrayList<>());
   }

   public void tick(MinecraftServer srv) {
      this.equippedEtchingSets
         .forEach((playerId, etchingSets) -> NetcodeUtils.runIfPresent(srv, playerId, sPlayer -> etchingSets.forEach(set -> set.tick(sPlayer))));
   }

   public void refreshEtchingSets(ServerPlayer player) {
      List<EtchingSet<?>> knownEtchings = this.equippedEtchingSets.computeIfAbsent(player.getUUID(), id -> new ArrayList<>());
      List<EtchingSet<?>> foundEtchings = EtchingHelper.getEquippedEtchings(player);
      knownEtchings.removeIf(existingSet -> {
         if (!foundEtchings.contains(existingSet)) {
            existingSet.remove(player);
            return true;
         } else {
            return false;
         }
      });
      foundEtchings.forEach(newSet -> {
         if (!knownEtchings.contains(newSet)) {
            newSet.apply(player);
            knownEtchings.add((EtchingSet<?>)newSet);
         }
      });
   }

   protected void load(CompoundTag tag) {
      this.equippedEtchingSets.clear();
      ListTag playerEtchings = tag.getList("players", 10);

      for (int i = 0; i < playerEtchings.size(); i++) {
         CompoundTag playerTag = playerEtchings.getCompound(i);
         UUID playerId = playerTag.getUUID("id");
         List<EtchingSet<?>> etchingSets = new ArrayList<>();
         ListTag etchings = tag.getList("etchings", 8);

         for (int j = 0; j < etchings.size(); j++) {
            ResourceLocation etchingKey = new ResourceLocation(etchings.getString(j));
            EtchingSet<?> set = EtchingRegistry.getEtchingSet(etchingKey);
            if (set != null) {
               etchingSets.add(set);
            }
         }

         this.equippedEtchingSets.put(playerId, etchingSets);
      }
   }

   public CompoundTag save(CompoundTag tag) {
      ListTag playerEtchings = new ListTag();
      this.equippedEtchingSets.forEach((playerId, etchingSets) -> {
         CompoundTag playerTag = new CompoundTag();
         playerTag.putUUID("id", playerId);
         ListTag etchings = new ListTag();
         etchingSets.forEach(set -> etchings.add(StringTag.valueOf(set.getRegistryName().toString())));
         playerTag.put("etchings", etchings);
         playerEtchings.add(playerTag);
      });
      tag.put("players", playerEtchings);
      return tag;
   }

   public static PlayerEtchingData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static PlayerEtchingData get(MinecraftServer server) {
      return (PlayerEtchingData)server.overworld().getDataStorage().computeIfAbsent(PlayerEtchingData::new, PlayerEtchingData::new, "the_vault_PlayerEtchings");
   }
}
