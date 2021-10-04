package iskallia.vault.world.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ScheduledItemDropData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_ScheduledItemDrops";
   private final Map<UUID, List<ItemStack>> scheduledItems = new HashMap<>();

   public ScheduledItemDropData() {
      super("the_vault_ScheduledItemDrops");
   }

   public void addDrop(PlayerEntity player, ItemStack toDrop) {
      this.addDrop(player.func_110124_au(), toDrop);
   }

   public void addDrop(UUID playerUUID, ItemStack toDrop) {
      this.scheduledItems.computeIfAbsent(playerUUID, key -> new ArrayList<>()).add(toDrop.func_77946_l());
      this.func_76185_a();
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.side == LogicalSide.SERVER && event.phase == Phase.END) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.player;
         ScheduledItemDropData data = get(player.func_71121_q());
         if (data.scheduledItems.isEmpty()) {
            return;
         }

         if (data.scheduledItems.containsKey(player.func_110124_au())) {
            List<ItemStack> drops = data.scheduledItems.get(player.func_110124_au());

            while (!drops.isEmpty() && player.field_71071_by.func_70447_i() != -1) {
               ItemStack drop = drops.get(0);
               if (!player.field_71071_by.func_70441_a(drop)) {
                  break;
               }

               drops.remove(0);
               data.func_76185_a();
            }

            if (drops.isEmpty()) {
               data.scheduledItems.remove(player.func_110124_au());
               data.func_76185_a();
            }
         }
      }
   }

   public void func_76184_a(CompoundNBT tag) {
      this.scheduledItems.clear();
      CompoundNBT savTag = tag.func_74775_l("drops");

      for (String key : savTag.func_150296_c()) {
         UUID playerUUID;
         try {
            playerUUID = UUID.fromString(key);
         } catch (IllegalArgumentException var9) {
            continue;
         }

         List<ItemStack> drops = new ArrayList<>();
         ListNBT dropsList = savTag.func_150295_c(key, 10);

         for (int i = 0; i < dropsList.size(); i++) {
            drops.add(ItemStack.func_199557_a(dropsList.func_150305_b(i)));
         }

         this.scheduledItems.put(playerUUID, drops);
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT tag) {
      CompoundNBT savTag = new CompoundNBT();
      this.scheduledItems.forEach((uuid, drops) -> {
         ListNBT dropsList = new ListNBT();
         drops.forEach(stack -> dropsList.add(stack.serializeNBT()));
         savTag.func_218657_a(uuid.toString(), dropsList);
      });
      tag.func_218657_a("drops", savTag);
      return tag;
   }

   public static ScheduledItemDropData get(ServerWorld world) {
      return get(world.func_73046_m());
   }

   public static ScheduledItemDropData get(MinecraftServer srv) {
      return (ScheduledItemDropData)srv.func_241755_D_().func_217481_x().func_215752_a(ScheduledItemDropData::new, "the_vault_ScheduledItemDrops");
   }
}
