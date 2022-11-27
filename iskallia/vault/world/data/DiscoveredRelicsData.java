package iskallia.vault.world.data;

import iskallia.vault.dynamodel.model.item.PlainItemModel;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModRelics;
import iskallia.vault.util.MiscUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;

public class DiscoveredRelicsData extends SavedData {
   protected static final String DATA_NAME = "the_vault_DiscoveredRelics";
   private Map<UUID, Set<ResourceLocation>> discoveredRelics = new HashMap<>();

   private DiscoveredRelicsData() {
   }

   private DiscoveredRelicsData(CompoundTag tag) {
      this.load(tag);
   }

   public Set<ResourceLocation> getDiscoveredRelics(UUID playerId) {
      return this.discoveredRelics.computeIfAbsent(playerId, uuid -> new HashSet<>());
   }

   public static Set<ResourceLocation> getRelics(UUID playerId) {
      return get(ServerLifecycleHooks.getCurrentServer()).discoveredRelics.computeIfAbsent(playerId, uuid -> new HashSet<>());
   }

   public boolean discoverRelicAndBroadcast(Player player, ModRelics.RelicRecipe relicRecipe) {
      if (this.getDiscoveredRelics(player.getUUID()).add(relicRecipe.getResultingRelic())) {
         this.broadcastRelicDiscovery(player, relicRecipe);
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   private void broadcastRelicDiscovery(Player player, ModRelics.RelicRecipe relicRecipe) {
      PlainItemModel relicModel = ModDynamicModels.Relics.RELIC_REGISTRY.get(relicRecipe.getResultingRelic()).orElse(null);
      if (relicModel != null) {
         MutableComponent msgContainer = new TextComponent("").withStyle(ChatFormatting.WHITE);
         MutableComponent playerName = player.getDisplayName().copy();
         playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
         MutableComponent relicName = new TextComponent(relicModel.getDisplayName());
         relicName.setStyle(Style.EMPTY.withColor(-2505149));
         MiscUtils.broadcast(msgContainer.append(playerName).append(" has assembled ").append(relicName).append(" for the first time."));
      }
   }

   @Nonnull
   public CompoundTag save(@Nonnull CompoundTag compound) {
      this.discoveredRelics.forEach((uuid, modelIds) -> {
         ListTag modelIdsTag = new ListTag();
         modelIds.forEach(modelId -> modelIdsTag.add(StringTag.valueOf(modelId.toString())));
         compound.put(uuid.toString(), modelIdsTag);
      });
      return compound;
   }

   public void load(CompoundTag compound) {
      this.discoveredRelics.clear();

      for (String uuidKey : compound.getAllKeys()) {
         UUID uuid = UUID.fromString(uuidKey);
         Set<ResourceLocation> modelIds = new HashSet<>();
         compound.getList(uuidKey, 8).stream().map(Tag::getAsString).map(ResourceLocation::new).forEach(modelIds::add);
         this.discoveredRelics.put(uuid, modelIds);
      }
   }

   public static DiscoveredRelicsData get(ServerLevel level) {
      return get(level.getServer());
   }

   public static DiscoveredRelicsData get(MinecraftServer server) {
      return (DiscoveredRelicsData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(DiscoveredRelicsData::new, DiscoveredRelicsData::new, "the_vault_DiscoveredRelics");
   }
}
