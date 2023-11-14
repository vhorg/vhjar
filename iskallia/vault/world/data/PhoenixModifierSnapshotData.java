package iskallia.vault.world.data;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PhoenixModifierSnapshotData extends InventorySnapshotData {
   protected static final String DATA_NAME = "the_vault_PhoenixModifier";

   @Override
   protected boolean shouldSnapshotItem(Player player, ItemStack stack) {
      return !stack.isEmpty()
         && (
            !AttributeGearData.hasData(stack)
               || !AttributeGearData.<AttributeGearData>read(stack).get(ModGearAttributes.SOULBOUND, VaultGearAttributeTypeMerger.anyTrue())
         );
   }

   @Override
   protected InventorySnapshot.Builder makeSnapshotBuilder(Player player) {
      return new InventorySnapshot.Builder(player).setStackFilter(this::shouldSnapshotItem);
   }

   private static PhoenixModifierSnapshotData create(CompoundTag tag) {
      PhoenixModifierSnapshotData data = new PhoenixModifierSnapshotData();
      data.load(tag);
      return data;
   }

   public static PhoenixModifierSnapshotData get(ServerLevel world) {
      return (PhoenixModifierSnapshotData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PhoenixModifierSnapshotData::create, PhoenixModifierSnapshotData::new, "the_vault_PhoenixModifier");
   }
}
