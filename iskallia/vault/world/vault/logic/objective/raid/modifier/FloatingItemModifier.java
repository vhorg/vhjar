package iskallia.vault.world.vault.logic.objective.raid.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.entity.FloatingItemEntity;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

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
   public void affectRaidMob(MobEntity mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerWorld world, BlockPos controller, ActiveRaid raid, float value) {
      WeightedList<ItemStack> items = this.getItemList();
      int toPlace = this.itemsToSpawn * Math.round(value);
      AxisAlignedBB placementBox = raid.getRaidBoundingBox();

      for (int i = 0; i < toPlace; i++) {
         BlockPos at;
         do {
            at = MiscUtils.getRandomPos(placementBox, rand);
         } while (!world.func_175623_d(at));

         ItemStack stack = items.getRandom(rand);
         if (stack != null && !stack.func_190926_b()) {
            world.func_217376_c(FloatingItemEntity.create(world, at, stack.func_77946_l()));
         }
      }
   }

   public WeightedList<ItemStack> getItemList() {
      WeightedList<ItemStack> itemWeights = new WeightedList<>();
      this.itemList.forEach((itemKey, weight) -> itemWeights.add(itemKey.createItemStack(), weight.intValue()));
      return itemWeights;
   }

   @Override
   public ITextComponent getDisplay(float value) {
      int sets = Math.round(value);
      String set = sets > 1 ? "sets" : "set";
      return new StringTextComponent("+" + sets + " " + set + " of " + this.itemDescription).func_240699_a_(TextFormatting.GREEN);
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
