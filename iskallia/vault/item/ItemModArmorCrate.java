package iskallia.vault.item;

import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemModArmorCrate extends BasicItem {
   public ItemModArmorCrate(ResourceLocation id, Properties properties) {
      super(id, properties);
   }

   @Nonnull
   public InteractionResultHolder<ItemStack> use(Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
      if (!world.isClientSide) {
      }

      return super.use(world, player, hand);
   }
}
