package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.item.ItemGiftBomb;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

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
      ItemStack fancierApple = new ItemStack(Items.GOLDEN_APPLE);
      fancierApple.setHoverName(new TextComponent("Fancier Apple"));
      fancierApple.setCount(2);
      this.GIFT_LOOTS.add(new ItemEntry(fancierApple));
      ItemStack sword = new ItemStack(Items.IRON_SWORD);
      sword.enchant(Enchantments.SHARPNESS, 10);
      this.GIFT_LOOTS.add(new ItemEntry(sword));
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
      ItemStack stack = ItemStack.EMPTY;
      if (loottable != null && !loottable.isEmpty()) {
         ItemEntry randomEntry = loottable.get(rand.nextInt(loottable.size()));

         try {
            stack = randomEntry.createItemStack();
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         return stack;
      } else {
         return stack;
      }
   }
}
