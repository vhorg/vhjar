package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SingleItemEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;

public class LegendaryTreasureNormalConfig extends Config {
   @Expose
   public List<SingleItemEntry> ITEMS = new ArrayList<>();

   @Override
   public String getName() {
      return "legendary_treasure_normal";
   }

   @Override
   protected void reset() {
      this.ITEMS.add(new SingleItemEntry(new ItemStack(Items.APPLE)));
      ItemStack sword = new ItemStack(Items.WOODEN_SWORD);
      sword.enchant(Enchantments.SHARPNESS, 1);
      this.ITEMS.add(new SingleItemEntry(sword));
   }

   public ItemStack getRandom() {
      Random rand = new Random();
      ItemStack stack = ItemStack.EMPTY;
      SingleItemEntry singleItemEntry = this.ITEMS.get(rand.nextInt(this.ITEMS.size()));

      try {
         Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(singleItemEntry.ITEM));
         stack = new ItemStack(item);
         CompoundTag nbt = TagParser.parseTag(singleItemEntry.NBT);
         stack.setTag(nbt);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      return stack;
   }
}
