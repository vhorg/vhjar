package iskallia.vault.item.crystal;

import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalEntry {
   public Collection<CrystalEntry> getChildren() {
      return Collections.emptyList();
   }

   public void configure(Vault vault, RandomSource random) {
      for (CrystalEntry child : this.getChildren()) {
         child.configure(vault, random);
      }
   }

   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      for (CrystalEntry child : this.getChildren()) {
         child.addText(tooltip, minIndex, flag, time);
      }
   }

   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected) {
      for (CrystalEntry child : this.getChildren()) {
         child.onInventoryTick(world, entity, slot, selected);
      }
   }

   public void onPortalTick(Level world, BlockPos pos, BlockState state) {
      for (CrystalEntry child : this.getChildren()) {
         child.onPortalTick(world, pos, state);
      }
   }

   public void onWorldTick(Level world, ItemEntity entity) {
      for (CrystalEntry child : this.getChildren()) {
         child.onWorldTick(world, entity);
      }
   }

   public boolean onPlaced(UseOnContext context) {
      boolean result = true;

      for (CrystalEntry child : this.getChildren()) {
         result &= child.onPlaced(context);
      }

      return result;
   }
}
