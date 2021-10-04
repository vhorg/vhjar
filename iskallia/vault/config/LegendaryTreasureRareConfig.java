package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SingleItemEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class LegendaryTreasureRareConfig extends Config {
   @Expose
   public List<SingleItemEntry> ITEMS = new ArrayList<>();

   @Override
   public String getName() {
      return "legendary_treasure_rare";
   }

   @Override
   protected void reset() {
      ItemStack fancyApple = new ItemStack(Items.field_151034_e);
      fancyApple.func_200302_a(new StringTextComponent("Fancy Apple"));
      this.ITEMS.add(new SingleItemEntry(fancyApple));
      ItemStack sword = new ItemStack(Items.field_151010_B);
      sword.func_77966_a(Enchantments.field_185302_k, 5);
      this.ITEMS.add(new SingleItemEntry(sword));
   }

   public ItemStack getRandom() {
      Random rand = new Random();
      ItemStack stack = ItemStack.field_190927_a;
      SingleItemEntry singleItemEntry = this.ITEMS.get(rand.nextInt(this.ITEMS.size()));

      try {
         Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(singleItemEntry.ITEM));
         stack = new ItemStack(item);
         CompoundNBT nbt = JsonToNBT.func_180713_a(singleItemEntry.NBT);
         stack.func_77982_d(nbt);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      return stack;
   }
}
