package iskallia.vault.item;

import java.util.function.Supplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class LootableItem extends BasicItem {
   private final Supplier<ItemStack> supplier;

   public LootableItem(ResourceLocation id, Properties properties, Supplier<ItemStack> supplier) {
      super(id, properties);
      this.supplier = supplier;
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      if (!world.field_72995_K) {
         ItemStack heldStack = player.func_184586_b(hand);
         ItemRelicBoosterPack.successEffects(world, player.func_213303_ch());
         ItemStack randomLoot = this.supplier.get();

         while (randomLoot.func_190916_E() > 0) {
            int amount = Math.min(randomLoot.func_190916_E(), randomLoot.func_77976_d());
            ItemStack copy = randomLoot.func_77946_l();
            copy.func_190920_e(amount);
            randomLoot.func_190918_g(amount);
            player.func_146097_a(copy, false, false);
         }

         heldStack.func_190918_g(1);
      }

      return super.func_77659_a(world, player, hand);
   }
}
