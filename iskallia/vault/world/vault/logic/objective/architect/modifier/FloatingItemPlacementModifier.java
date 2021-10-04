package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.logic.objective.architect.processor.FloatingItemPostProcessor;
import iskallia.vault.world.vault.logic.objective.architect.processor.VaultPieceProcessor;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public class FloatingItemPlacementModifier extends VoteModifier {
   @Expose
   private final int blocksPerSpawn;
   @Expose
   private final WeightedList<SingleItemEntry> itemList;

   public FloatingItemPlacementModifier(String name, String description, int voteLockDurationChangeSeconds, int blocksPerSpawn, List<ItemStack> itemList) {
      super(name, description, voteLockDurationChangeSeconds);
      this.blocksPerSpawn = blocksPerSpawn;
      this.itemList = new WeightedList<>();
      itemList.forEach(stack -> this.itemList.add(new SingleItemEntry(stack), 1));
   }

   public WeightedList<ItemStack> getItemList() {
      WeightedList<ItemStack> itemWeights = new WeightedList<>();
      this.itemList.forEach((itemKey, weight) -> itemWeights.add(itemKey.createItemStack(), weight.intValue()));
      return itemWeights;
   }

   @Nullable
   @Override
   public VaultPieceProcessor getPostProcessor(ArchitectObjective objective, VaultRaid vault) {
      return new FloatingItemPostProcessor(this.blocksPerSpawn, this.getItemList());
   }

   public static List<ItemStack> defaultGemList() {
      List<ItemStack> list = new ArrayList<>();
      list.add(new ItemStack(ModItems.ALEXANDRITE_GEM));
      list.add(new ItemStack(ModItems.BENITOITE_GEM));
      list.add(new ItemStack(ModItems.LARIMAR_GEM));
      list.add(new ItemStack(ModItems.WUTODIE_GEM));
      list.add(new ItemStack(ModItems.PAINITE_GEM));
      list.add(new ItemStack(ModItems.BLACK_OPAL_GEM));
      return list;
   }

   public static List<ItemStack> defaultPrismaticList() {
      List<ItemStack> list = new ArrayList<>();
      list.add(new ItemStack(ModItems.VAULT_CATALYST_FRAGMENT));
      return list;
   }

   public static List<ItemStack> defaultVaultGearList() {
      List<ItemStack> list = new ArrayList<>();
      list.add(new ItemStack(ModItems.SWORD));
      list.add(new ItemStack(ModItems.AXE));
      list.add(new ItemStack(ModItems.HELMET));
      list.add(new ItemStack(ModItems.CHESTPLATE));
      list.add(new ItemStack(ModItems.LEGGINGS));
      list.add(new ItemStack(ModItems.BOOTS));
      return list;
   }
}
