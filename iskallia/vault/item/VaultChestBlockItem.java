package iskallia.vault.item;

import iskallia.vault.client.render.VaultISTER;
import java.util.function.Consumer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

public class VaultChestBlockItem extends BlockItem {
   public VaultChestBlockItem(Block pBlock, Properties pProperties) {
      super(pBlock, pProperties);
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(VaultISTER.INSTANCE);
   }
}
