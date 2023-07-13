package iskallia.vault.world.data;

import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.network.message.DiscoveredWorkbenchModifierCraftsMessage;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

public class DiscoveredWorkbenchModifiersData extends SavedData {
   protected static final String DATA_NAME = "the_vault_DiscoveredWorkbenchModifiers";
   protected Map<UUID, Map<Item, Set<ResourceLocation>>> discoveredCrafts = new HashMap<>();

   private DiscoveredWorkbenchModifiersData() {
   }

   private DiscoveredWorkbenchModifiersData(CompoundTag tag) {
      this.load(tag);
   }

   public boolean compoundDiscoverWorkbenchCraft(ServerPlayer player, Item gearItem, ResourceLocation key) {
      if (gearItem instanceof IdolItem) {
         List<IdolItem> idols = List.of(ModItems.IDOL_TIMEKEEPER, ModItems.IDOL_BENEVOLENT, ModItems.IDOL_MALEVOLENCE, ModItems.IDOL_OMNISCIENT);
         boolean discovered = false;

         for (IdolItem idol : idols) {
            if (this.discoverWorkbenchCraft(player, idol, key)) {
               discovered = true;
            }
         }

         return discovered;
      } else {
         return this.discoverWorkbenchCraft(player, gearItem, key);
      }
   }

   private boolean discoverWorkbenchCraft(ServerPlayer player, Item gearItem, ResourceLocation key) {
      Set<ResourceLocation> craftKeys = this.discoveredCrafts
         .computeIfAbsent(player.getUUID(), id -> new LinkedHashMap<>())
         .computeIfAbsent(gearItem, item -> new HashSet<>());
      if (craftKeys.add(key)) {
         this.setDirty();
         this.syncTo(player);
         return true;
      } else {
         return false;
      }
   }

   public boolean hasDiscoveredCraft(Player player, Item gearItem, ResourceLocation key) {
      return this.discoveredCrafts.getOrDefault(player.getUUID(), Collections.emptyMap()).getOrDefault(gearItem, Collections.emptySet()).contains(key);
   }

   private DiscoveredWorkbenchModifierCraftsMessage getUpdatePacket(UUID playerId) {
      return new DiscoveredWorkbenchModifierCraftsMessage(this.discoveredCrafts.getOrDefault(playerId, Collections.emptyMap()));
   }

   public void syncTo(ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(this.getUpdatePacket(player.getUUID()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public CompoundTag save(CompoundTag tag) {
      ListTag playerTrinkets = new ListTag();
      this.discoveredCrafts
         .forEach(
            (playerId, itemCrafts) -> {
               CompoundTag playerTag = new CompoundTag();
               playerTag.putUUID("player", playerId);
               CompoundTag craftsTag = new CompoundTag();
               itemCrafts.forEach(
                  (item, keys) -> NBTHelper.writeCollection(
                     craftsTag, item.getRegistryName().toString(), keys, StringTag.class, key -> StringTag.valueOf(key.toString())
                  )
               );
               playerTag.put("itemCrafts", craftsTag);
               playerTrinkets.add(playerTag);
            }
         );
      tag.put("crafts", playerTrinkets);
      return tag;
   }

   public void load(CompoundTag tag) {
      this.discoveredCrafts.clear();
      ListTag playerTrinkets = tag.getList("crafts", 10);

      for (int i = 0; i < playerTrinkets.size(); i++) {
         CompoundTag playerTag = playerTrinkets.getCompound(i);
         UUID playerId = playerTag.getUUID("player");
         CompoundTag craftsTag = playerTag.getCompound("itemCrafts");
         Set<ResourceLocation> idolCrafts = new HashSet<>();
         Map<Item, Set<ResourceLocation>> itemCrafts = new LinkedHashMap<>();

         for (String itemKey : craftsTag.getAllKeys()) {
            ResourceLocation key = ResourceLocation.tryParse(itemKey);
            if (key != null) {
               Item item = (Item)ForgeRegistries.ITEMS.getValue(key);
               if (item != null) {
                  Set<ResourceLocation> crafts = NBTHelper.readSet(craftsTag, itemKey, StringTag.class, strTag -> new ResourceLocation(strTag.getAsString()));
                  itemCrafts.put(item, crafts);
                  if (item instanceof IdolItem) {
                     idolCrafts.addAll(crafts);
                  }
               }
            }
         }

         if (!idolCrafts.isEmpty()) {
            itemCrafts.put(ModItems.IDOL_TIMEKEEPER, idolCrafts);
            itemCrafts.put(ModItems.IDOL_BENEVOLENT, idolCrafts);
            itemCrafts.put(ModItems.IDOL_MALEVOLENCE, idolCrafts);
            itemCrafts.put(ModItems.IDOL_OMNISCIENT, idolCrafts);
         }

         this.discoveredCrafts.put(playerId, itemCrafts);
      }
   }

   public static DiscoveredWorkbenchModifiersData get(ServerLevel level) {
      return get(level.getServer());
   }

   public static DiscoveredWorkbenchModifiersData get(MinecraftServer server) {
      return (DiscoveredWorkbenchModifiersData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(DiscoveredWorkbenchModifiersData::new, DiscoveredWorkbenchModifiersData::new, "the_vault_DiscoveredWorkbenchModifiers");
   }
}
