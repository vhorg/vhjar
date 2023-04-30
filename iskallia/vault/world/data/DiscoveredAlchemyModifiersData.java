package iskallia.vault.world.data;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.DiscoveredAlchemyModifierCraftsMessage;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkDirection;

public class DiscoveredAlchemyModifiersData extends SavedData {
   protected static final String DATA_NAME = "the_vault_DiscoveredAlchemyModifiers";
   protected Map<UUID, Set<ResourceLocation>> discoveredCrafts = new HashMap<>();

   private DiscoveredAlchemyModifiersData() {
   }

   private DiscoveredAlchemyModifiersData(CompoundTag tag) {
      this.load(tag);
   }

   public boolean compoundDiscoverWorkbenchCraft(ServerPlayer player, ResourceLocation key) {
      return this.discoverAlchemyCraft(player, key);
   }

   private boolean discoverAlchemyCraft(ServerPlayer player, ResourceLocation key) {
      Set<ResourceLocation> craftKeys = this.discoveredCrafts.computeIfAbsent(player.getUUID(), id -> new HashSet<>());
      if (craftKeys.add(key)) {
         this.setDirty();
         this.syncTo(player);
         return true;
      } else {
         return false;
      }
   }

   public boolean hasDiscoveredCraft(Player player, ResourceLocation key) {
      return this.discoveredCrafts.getOrDefault(player.getUUID(), Collections.emptySet()).contains(key);
   }

   private DiscoveredAlchemyModifierCraftsMessage getUpdatePacket(UUID playerId) {
      return new DiscoveredAlchemyModifierCraftsMessage(this.discoveredCrafts.getOrDefault(playerId, Collections.emptySet()));
   }

   public void syncTo(ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(this.getUpdatePacket(player.getUUID()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public CompoundTag save(CompoundTag tag) {
      ListTag playerTrinkets = new ListTag();
      this.discoveredCrafts.forEach((playerId, itemCrafts) -> {
         CompoundTag playerTag = new CompoundTag();
         playerTag.putUUID("player", playerId);
         NBTHelper.writeCollection(playerTag, "itemCrafts", itemCrafts, StringTag.class, key -> StringTag.valueOf(key.toString()));
         playerTrinkets.add(playerTag);
      });
      tag.put("crafts", playerTrinkets);
      return tag;
   }

   public void load(CompoundTag tag) {
      this.discoveredCrafts.clear();
      ListTag playerTrinkets = tag.getList("crafts", 10);

      for (int i = 0; i < playerTrinkets.size(); i++) {
         CompoundTag playerTag = playerTrinkets.getCompound(i);
         UUID playerId = playerTag.getUUID("player");
         Set<ResourceLocation> crafts = NBTHelper.readSet(playerTag, "itemCrafts", StringTag.class, strTag -> new ResourceLocation(strTag.getAsString()));
         this.discoveredCrafts.put(playerId, crafts);
      }
   }

   public static DiscoveredAlchemyModifiersData get(ServerLevel level) {
      return get(level.getServer());
   }

   public static DiscoveredAlchemyModifiersData get(MinecraftServer server) {
      return (DiscoveredAlchemyModifiersData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(DiscoveredAlchemyModifiersData::new, DiscoveredAlchemyModifiersData::new, "the_vault_DiscoveredAlchemyModifiers");
   }
}
