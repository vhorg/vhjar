package iskallia.vault.world.data;

import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.network.message.transmog.DiscoveredEntriesMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.Collections;
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
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.HoverEvent.ItemStackInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public class DiscoveredTrinketsData extends SavedData {
   protected static final String DATA_NAME = "the_vault_DiscoveredTrinkets";
   protected Map<UUID, Set<ResourceLocation>> collectedTrinkets = new HashMap<>();

   private DiscoveredTrinketsData() {
   }

   private DiscoveredTrinketsData(CompoundTag tag) {
      this.load(tag);
   }

   public void discoverTrinketAndBroadcast(ItemStack trinketStack, Player player) {
      if (!trinketStack.isEmpty() && trinketStack.getItem() instanceof TrinketItem) {
         AttributeGearData data = AttributeGearData.read(trinketStack);
         data.getFirstValue(ModGearAttributes.TRINKET_EFFECT).ifPresent(trinket -> {
            if (this.discoverTrinket(player.getUUID(), (TrinketEffect<?>)trinket)) {
               this.broadcastDiscovery((TrinketEffect<?>)trinket, player, trinketStack);
            }
         });
      }
   }

   private void broadcastDiscovery(TrinketEffect<?> trinket, Player player, ItemStack trinketStack) {
      MutableComponent ct = new TextComponent("").withStyle(ChatFormatting.WHITE);
      MutableComponent playerCt = player.getDisplayName().copy();
      playerCt.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
      MutableComponent trinketCmp = new TextComponent(trinket.getTrinketConfig().getName());
      trinketCmp.setStyle(
         Style.EMPTY
            .withColor(trinket.getTrinketConfig().getComponentColor())
            .withHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ItemStackInfo(TrinketItem.createBaseTrinket(trinket))))
      );
      MiscUtils.broadcast(ct.append(playerCt).append(" has discovered trinket: ").append(trinketCmp));
   }

   public boolean discoverTrinket(UUID playerId, TrinketEffect<?> trinket) {
      Set<ResourceLocation> trinketKeys = this.collectedTrinkets.computeIfAbsent(playerId, id -> new HashSet<>());
      if (trinketKeys.add(trinket.getRegistryName())) {
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public boolean hasDiscovered(Player player, TrinketEffect<?> trinket) {
      return this.hasDiscovered(player.getUUID(), trinket.getRegistryName());
   }

   public boolean hasDiscovered(UUID playerId, ResourceLocation trinket) {
      return this.collectedTrinkets.getOrDefault(playerId, Collections.emptySet()).contains(trinket);
   }

   public void setDirty(boolean dirty) {
      super.setDirty(dirty);
      if (dirty) {
         MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
         if (srv != null) {
            srv.getPlayerList().getPlayers().forEach(this::syncTo);
         }
      }
   }

   private DiscoveredEntriesMessage getUpdatePacket(UUID playerId) {
      return new DiscoveredEntriesMessage(DiscoveredEntriesMessage.Type.TRINKETS, this.collectedTrinkets.getOrDefault(playerId, Collections.emptySet()));
   }

   public void syncTo(ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(this.getUpdatePacket(player.getUUID()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public void load(CompoundTag tag) {
      this.collectedTrinkets.clear();
      ListTag playerTrinkets = tag.getList("trinkets", 10);

      for (int i = 0; i < playerTrinkets.size(); i++) {
         CompoundTag playerTag = playerTrinkets.getCompound(i);
         UUID playerId = playerTag.getUUID("player");
         Set<ResourceLocation> trinkets = NBTHelper.readSet(playerTag, "trinkets", StringTag.class, strTag -> new ResourceLocation(strTag.getAsString()));
         this.collectedTrinkets.put(playerId, trinkets);
      }
   }

   @Nonnull
   public CompoundTag save(@Nonnull CompoundTag tag) {
      ListTag playerTrinkets = new ListTag();
      this.collectedTrinkets.forEach((playerId, trinkets) -> {
         CompoundTag playerTag = new CompoundTag();
         playerTag.putUUID("player", playerId);
         NBTHelper.writeCollection(playerTag, "trinkets", trinkets, StringTag.class, key -> StringTag.valueOf(key.toString()));
         playerTrinkets.add(playerTag);
      });
      tag.put("trinkets", playerTrinkets);
      return tag;
   }

   public static DiscoveredTrinketsData get(ServerLevel level) {
      return get(level.getServer());
   }

   public static DiscoveredTrinketsData get(MinecraftServer server) {
      return (DiscoveredTrinketsData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(DiscoveredTrinketsData::new, DiscoveredTrinketsData::new, "the_vault_DiscoveredTrinkets");
   }
}
