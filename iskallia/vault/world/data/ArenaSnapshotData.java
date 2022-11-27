package iskallia.vault.world.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArenaSnapshotData extends InventorySnapshotData {
   protected static final String DATA_NAME = "the_vault_ArenaSnapshots";

   @Override
   protected boolean shouldSnapshotItem(Player player, ItemStack stack) {
      return true;
   }

   private static ArenaSnapshotData create(CompoundTag tag) {
      ArenaSnapshotData data = new ArenaSnapshotData();
      data.load(tag);
      return data;
   }

   public static ArenaSnapshotData get(ServerLevel world) {
      return (ArenaSnapshotData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(ArenaSnapshotData::create, ArenaSnapshotData::new, "the_vault_ArenaSnapshots");
   }
}
