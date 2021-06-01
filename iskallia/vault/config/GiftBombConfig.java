package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.item.ItemGiftBomb;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class GiftBombConfig extends Config {
   @Expose
   private List<ItemEntry> GIFT_LOOTS;
   @Expose
   private List<ItemEntry> SUPER_GIFT_LOOTS;
   @Expose
   private List<ItemEntry> MEGA_GIFT_LOOTS;
   @Expose
   private List<ItemEntry> OMEGA_GIFT_LOOTS;

   @Override
   public String getName() {
      return "gift_bomb";
   }

   @Override
   protected void reset() {
      this.GIFT_LOOTS = new LinkedList<>();
      this.GIFT_LOOTS.add(new ItemEntry("minecraft:golden_apple", 2, "{display:{Name:'{\"text\":\"Fancier Apple\"}'}}"));
      this.GIFT_LOOTS.add(new ItemEntry("minecraft:iron_sword", 1, "{Enchantments:[{id:\"minecraft:sharpness\",lvl:10s}]}"));
      this.SUPER_GIFT_LOOTS = new LinkedList<>();
      this.MEGA_GIFT_LOOTS = new LinkedList<>();
      this.OMEGA_GIFT_LOOTS = new LinkedList<>();
   }

   public ItemStack randomLoot(ItemGiftBomb.Variant variant) {
      switch (variant) {
         case NORMAL:
            return this.getRandom(this.GIFT_LOOTS);
         case SUPER:
            return this.getRandom(this.SUPER_GIFT_LOOTS);
         case MEGA:
            return this.getRandom(this.MEGA_GIFT_LOOTS);
         case OMEGA:
            return this.getRandom(this.OMEGA_GIFT_LOOTS);
         default:
            throw new InternalError("Unknown Gift Bomb variant: " + variant);
      }
   }

   private ItemStack getRandom(List<ItemEntry> loottable) {
      Random rand = new Random();
      ItemStack stack = ItemStack.field_190927_a;
      if (loottable != null && !loottable.isEmpty()) {
         ItemEntry randomEntry = loottable.get(rand.nextInt(loottable.size()));

         try {
            Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(randomEntry.ITEM));
            stack = new ItemStack(item, randomEntry.AMOUNT);
            if (randomEntry.NBT != null) {
               CompoundNBT nbt = JsonToNBT.func_180713_a(randomEntry.NBT);
               stack.func_77982_d(nbt);
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }

         return stack;
      } else {
         return stack;
      }
   }
}
