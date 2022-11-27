package iskallia.vault.client.gui.framework.spatial;

import com.google.common.collect.Queues;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.Deque;
import net.minecraft.Util;

public class SpatialStack {
   private final Deque<IMutableSpatial> stack = (Deque<IMutableSpatial>)Util.make(Queues.newArrayDeque(), deque -> deque.offer(Spatials.copy(Spatials.zero())));

   public SpatialStack clear() {
      this.stack.clear();
      this.stack.offer(Spatials.copy(Spatials.zero()));
      return this;
   }

   public SpatialStack push() {
      if (!this.stack.isEmpty()) {
         this.stack.offer(Spatials.copy(this.stack.peekLast()));
      }

      return this;
   }

   public SpatialStack pop() {
      this.stack.pollLast();
      if (this.stack.isEmpty()) {
         this.stack.offer(Spatials.copy(Spatials.zero()));
      }

      return this;
   }

   public ISpatial peek() {
      return this.peekInternal().unmodifiableView();
   }

   public SpatialStack translateX(int x) {
      this.peekInternal().translateX(x);
      return this;
   }

   public SpatialStack translateY(int y) {
      this.peekInternal().translateY(y);
      return this;
   }

   public SpatialStack translateZ(int z) {
      this.peekInternal().translateZ(z);
      return this;
   }

   public SpatialStack translate(int x, int y) {
      this.peekInternal().translateXY(x, y);
      return this;
   }

   public SpatialStack translate(int x, int y, int z) {
      this.peekInternal().translateXYZ(x, y, z);
      return this;
   }

   public SpatialStack translate(IPosition position) {
      this.peekInternal().translateXYZ(position);
      return this;
   }

   public SpatialStack include(ISpatial spatial) {
      this.peekInternal().include(spatial);
      return this;
   }

   public SpatialStack size(ISize size) {
      this.peekInternal().size(size);
      return this;
   }

   private IMutableSpatial peekInternal() {
      return this.stack.peekLast();
   }
}
