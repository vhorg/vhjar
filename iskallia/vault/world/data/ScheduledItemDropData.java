package iskallia.vault.world.data;

import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.VHSmpUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ScheduledItemDropData extends SavedData {
   protected static final String DATA_NAME = "the_vault_ScheduledItemDrops";
   private final Map<UUID, List<ItemStack>> scheduledItems = new HashMap<>();

   public void addDrop(Player player, ItemStack toDrop) {
      this.addDrop(player.getUUID(), toDrop);
   }

   public void addDrop(UUID playerUUID, ItemStack toDrop) {
      this.scheduledItems.computeIfAbsent(playerUUID, key -> new ArrayList<>()).add(toDrop.copy());
      this.setDirty();
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.side == LogicalSide.SERVER && event.phase == Phase.END) {
         ServerPlayer player = (ServerPlayer)event.player;
         if (VHSmpUtil.isArenaWorld(player)) {
            return;
         }

         ScheduledItemDropData data = get(player.getLevel());
         if (data.scheduledItems.containsKey(player.getUUID())) {
            List<ItemStack> drops = data.scheduledItems.get(player.getUUID());

            while (!drops.isEmpty() && MiscUtils.hasEmptySlot(player.getInventory())) {
               ItemStack drop = drops.remove(0);
               player.getInventory().add(drop);
               data.setDirty();
            }

            if (drops.isEmpty()) {
               data.scheduledItems.remove(player.getUUID());
               data.setDirty();
            }
         }
      }
   }

   private static ScheduledItemDropData create(CompoundTag tag) {
      ScheduledItemDropData data = new ScheduledItemDropData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag tag) {
      this.scheduledItems.clear();
      CompoundTag savTag = tag.getCompound("drops");

      for (String key : savTag.getAllKeys()) {
         UUID playerUUID;
         try {
            playerUUID = UUID.fromString(key);
         } catch (IllegalArgumentException var9) {
            continue;
         }

         List<ItemStack> drops = new ArrayList<>();
         ListTag dropsList = savTag.getList(key, 10);

         for (int i = 0; i < dropsList.size(); i++) {
            drops.add(ItemStack.of(dropsList.getCompound(i)));
         }

         this.scheduledItems.put(playerUUID, drops);
      }
   }

   public CompoundTag save(CompoundTag tag) {
      CompoundTag savTag = new CompoundTag();
      this.scheduledItems.forEach((uuid, drops) -> {
         ListTag dropsList = new ListTag();
         drops.forEach(stack -> dropsList.add(stack.serializeNBT()));
         savTag.put(uuid.toString(), dropsList);
      });
      tag.put("drops", savTag);
      return tag;
   }

   public static ScheduledItemDropData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static ScheduledItemDropData get(MinecraftServer srv) {
      return (ScheduledItemDropData)srv.overworld()
         .getDataStorage()
         .computeIfAbsent(ScheduledItemDropData::create, ScheduledItemDropData::new, "the_vault_ScheduledItemDrops");
   }
}
