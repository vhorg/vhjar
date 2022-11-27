package iskallia.vault.item;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class LootableItem extends BasicItem {
   private final Supplier<ItemStack> supplier;

   public LootableItem(ResourceLocation id, Properties properties, Supplier<ItemStack> supplier) {
      super(id, properties);
      this.supplier = supplier;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      if (!world.isClientSide) {
         ItemStack heldStack = player.getItemInHand(hand);
         ItemRelicBoosterPack.successEffects(world, player.position());
         ItemStack randomLoot = this.supplier.get();

         while (randomLoot.getCount() > 0) {
            int amount = Math.min(randomLoot.getCount(), randomLoot.getMaxStackSize());
            ItemStack copy = randomLoot.copy();
            copy.setCount(amount);
            randomLoot.shrink(amount);
            player.drop(copy, false, false);
         }

         heldStack.shrink(1);
      }

      return super.use(world, player, hand);
   }
}
