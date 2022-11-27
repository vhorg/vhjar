package iskallia.vault.world.vault.logic.objective.raid.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.entity.entity.FloatingItemEntity;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class FloatingItemModifier extends RaidModifier {
   @Expose
   private final int itemsToSpawn;
   @Expose
   private final WeightedList<SingleItemEntry> itemList;
   @Expose
   private final String itemDescription;

   public FloatingItemModifier(String name, int itemsToSpawn, WeightedList<SingleItemEntry> itemList, String itemDescription) {
      super(false, true, name);
      this.itemsToSpawn = itemsToSpawn;
      this.itemList = itemList;
      this.itemDescription = itemDescription;
   }

   @Override
   public void affectRaidMob(Mob mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerLevel world, BlockPos controller, ActiveRaid raid, float value) {
      WeightedList<ItemStack> items = this.getItemList();
      int toPlace = this.itemsToSpawn * Math.round(value);
      AABB placementBox = raid.getRaidBoundingBox();

      for (int i = 0; i < toPlace; i++) {
         BlockPos at;
         do {
            at = MiscUtils.getRandomPos(placementBox, rand);
         } while (!world.isEmptyBlock(at));

         ItemStack stack = items.getRandom(rand);
         if (stack != null && !stack.isEmpty()) {
            world.addFreshEntity(FloatingItemEntity.create(world, at, stack.copy()));
         }
      }
   }

   public WeightedList<ItemStack> getItemList() {
      WeightedList<ItemStack> itemWeights = new WeightedList<>();
      this.itemList.forEach((itemKey, weight) -> itemWeights.add(itemKey.createItemStack(), weight.intValue()));
      return itemWeights;
   }

   @Override
   public Component getDisplay(float value) {
      int sets = Math.round(value);
      String set = sets > 1 ? "sets" : "set";
      return new TextComponent("+" + sets + " " + set + " of " + this.itemDescription).withStyle(ChatFormatting.GREEN);
   }

   public static WeightedList<SingleItemEntry> defaultGemList() {
      WeightedList<SingleItemEntry> list = new WeightedList<>();
      list.add(new SingleItemEntry(new ItemStack(ModItems.ALEXANDRITE_GEM)), 1);
      list.add(new SingleItemEntry(new ItemStack(ModItems.BENITOITE_GEM)), 1);
      list.add(new SingleItemEntry(new ItemStack(ModItems.LARIMAR_GEM)), 1);
      list.add(new SingleItemEntry(new ItemStack(ModItems.WUTODIE_GEM)), 1);
      list.add(new SingleItemEntry(new ItemStack(ModItems.PAINITE_GEM)), 1);
      list.add(new SingleItemEntry(new ItemStack(ModItems.BLACK_OPAL_GEM)), 1);
      return list;
   }
}
