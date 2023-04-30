package iskallia.vault.research.type;

import iskallia.vault.research.Restrictions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class MinimapResearch extends Research {
   public MinimapResearch(String name, int cost) {
      super(name, cost);
   }

   @Override
   public boolean restricts(ItemStack stack, Restrictions.Type restrictionType) {
      return false;
   }

   @Override
   public boolean restricts(Block block, Restrictions.Type restrictionType) {
      return false;
   }

   @Override
   public boolean restricts(EntityType<?> entityType, Restrictions.Type restrictionType) {
      return false;
   }
}
