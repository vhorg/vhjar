package iskallia.vault.event.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ShopPedestalPriceEvent extends PlayerEvent {
   private final ItemStack offerStack;
   private final ItemStack originalCost;
   private ItemStack newCost;

   public ShopPedestalPriceEvent(Player player, ItemStack offerStack, ItemStack originalCost) {
      super(player);
      this.offerStack = offerStack.copy();
      this.originalCost = originalCost.copy();
      this.newCost = this.originalCost.copy();
   }

   public ItemStack getOfferStack() {
      return this.offerStack.copy();
   }

   public ItemStack getOriginalCost() {
      return this.originalCost.copy();
   }

   public ItemStack getCost() {
      return this.newCost.copy();
   }

   public void setNewCost(ItemStack newCost) {
      this.newCost = newCost;
   }
}
