package iskallia.vault.container.base;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;

public abstract class SimpleSidedContainer extends SimpleContainer implements WorldlyContainer {
   private final Map<Direction, Set<Integer>> cachedSidedSlots = new HashMap<>();

   public SimpleSidedContainer(int size) {
      super(size);
      this.cacheSlots();
   }

   public SimpleSidedContainer(ItemStack... items) {
      super(items);
      this.cacheSlots();
   }

   private void cacheSlots() {
      IntStream.range(0, this.getContainerSize())
         .forEach(slot -> this.getAccessibleSlots(slot).forEach(dir -> this.cachedSidedSlots.computeIfAbsent(dir, side -> new HashSet<>()).add(slot)));
   }

   public abstract List<Direction> getAccessibleSlots(int var1);

   public int[] getSlotsForFace(Direction side) {
      return Optional.ofNullable(this.cachedSidedSlots.get(side)).map(Collection::stream).orElse(Stream.empty()).mapToInt(Integer::intValue).toArray();
   }

   public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
      return this.cachedSidedSlots.getOrDefault(side, Collections.emptySet()).contains(slot);
   }

   public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
      return this.cachedSidedSlots.getOrDefault(side, Collections.emptySet()).contains(slot);
   }
}
