package iskallia.vault.world.data;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.DiscoveredAlchemyEffectsMessage;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkDirection;

public class DiscoveredAlchemyEffectsData extends SavedData {
   protected static final String DATA_NAME = "the_vault_DiscoveredAlchemyModifiers";
   protected Map<UUID, Set<String>> discoveredEffects = new HashMap<>();

   private DiscoveredAlchemyEffectsData() {
   }

   private DiscoveredAlchemyEffectsData(CompoundTag tag) {
      this.load(tag);
   }

   public boolean compoundDiscoverEffect(ServerPlayer player, String effectId) {
      return this.discoverAlchemyEffect(player, effectId);
   }

   private boolean discoverAlchemyEffect(ServerPlayer player, String effectId) {
      Set<String> craftKeys = this.discoveredEffects.computeIfAbsent(player.getUUID(), id -> new HashSet<>());
      if (craftKeys.add(effectId)) {
         this.setDirty();
         this.syncTo(player);
         return true;
      } else {
         return false;
      }
   }

   public boolean hasDiscoveredEffect(Player player, String effectId) {
      return this.discoveredEffects.getOrDefault(player.getUUID(), Collections.emptySet()).contains(effectId);
   }

   private DiscoveredAlchemyEffectsMessage getUpdatePacket(UUID playerId) {
      return new DiscoveredAlchemyEffectsMessage(this.discoveredEffects.getOrDefault(playerId, Collections.emptySet()));
   }

   public void syncTo(ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(this.getUpdatePacket(player.getUUID()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public CompoundTag save(CompoundTag tag) {
      ListTag discoveredEffectTag = new ListTag();
      this.discoveredEffects.forEach((playerId, itemCrafts) -> {
         CompoundTag playerTag = new CompoundTag();
         playerTag.putUUID("player", playerId);
         NBTHelper.writeCollection(playerTag, "effects", itemCrafts, StringTag.class, StringTag::valueOf);
         discoveredEffectTag.add(playerTag);
      });
      tag.put("discoveredEffects", discoveredEffectTag);
      return tag;
   }

   public void load(CompoundTag tag) {
      this.discoveredEffects.clear();
      ListTag playerTrinkets = tag.getList("discoveredEffects", 10);

      for (int i = 0; i < playerTrinkets.size(); i++) {
         CompoundTag playerTag = playerTrinkets.getCompound(i);
         UUID playerId = playerTag.getUUID("player");
         Set<String> effects = NBTHelper.readSet(playerTag, "effects", StringTag.class, StringTag::getAsString);
         this.discoveredEffects.put(playerId, effects);
      }
   }

   public static DiscoveredAlchemyEffectsData get(ServerLevel level) {
      return get(level.getServer());
   }

   public static DiscoveredAlchemyEffectsData get(MinecraftServer server) {
      return (DiscoveredAlchemyEffectsData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(DiscoveredAlchemyEffectsData::new, DiscoveredAlchemyEffectsData::new, "the_vault_DiscoveredAlchemyModifiers");
   }
}
