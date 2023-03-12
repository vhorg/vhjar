package iskallia.vault.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class InscriptionPieceItem extends Item {
   public InscriptionPieceItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group));
      this.setRegistryName(id);
   }
}
