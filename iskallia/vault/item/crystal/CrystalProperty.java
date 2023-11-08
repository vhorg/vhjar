package iskallia.vault.item.crystal;

import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalProperty {
   public Collection<CrystalProperty> getChildren() {
      return Collections.emptyList();
   }

   public void configure(Vault vault, RandomSource random) {
      for (CrystalProperty child : this.getChildren()) {
         child.configure(vault, random);
      }
   }

   public void addText(List<Component> tooltip, TooltipFlag flag, float time) {
      for (CrystalProperty child : this.getChildren()) {
         child.addText(tooltip, flag, time);
      }
   }

   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected) {
      for (CrystalProperty child : this.getChildren()) {
         child.onInventoryTick(world, entity, slot, selected);
      }
   }

   public void onWorldTick(Level world, BlockPos pos, BlockState state) {
      for (CrystalProperty child : this.getChildren()) {
         child.onWorldTick(world, pos, state);
      }
   }

   public boolean onPlaced(UseOnContext context) {
      boolean result = true;

      for (CrystalProperty child : this.getChildren()) {
         result &= child.onPlaced(context);
      }

      return result;
   }
}
