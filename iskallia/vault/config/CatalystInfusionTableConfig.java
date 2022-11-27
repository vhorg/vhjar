package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CatalystInfusionTableConfig extends Config {
   private static final String NAME = "catalyst_infusion_table";
   private static final ItemStack FALLBACK_ITEM = new ItemStack(Items.LAPIS_LAZULI);
   private static final int DEFAULT_INFUSION_TIME_TICKS = 40;
   @Expose
   private ItemStack infusionItem;
   @Expose
   private int infusionTimeTicks;

   @Override
   public String getName() {
      return "catalyst_infusion_table";
   }

   @Override
   protected void reset() {
      this.infusionItem = FALLBACK_ITEM.copy();
      this.infusionTimeTicks = 40;
   }

   public ItemStack getInfusionItem() {
      return this.infusionItem.copy();
   }

   public int getInfusionTimeTicks() {
      return this.infusionTimeTicks;
   }

   @Override
   protected boolean isValid() {
      if (this.infusionTimeTicks < 0) {
         VaultMod.LOGGER.error("infusionTimeTicks must be >= 0");
         return false;
      } else {
         return true;
      }
   }
}
