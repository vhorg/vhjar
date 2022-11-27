package iskallia.vault.client.gui.framework.spatial;

import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.Objects;

public class Spatial implements IMutableSpatial {
   private int x;
   private int y;
   private int z;
   private int width;
   private int height;

   public Spatial() {
      this.positionXYZ(0, 0, 0).size(0, 0);
   }

   public Spatial(int x, int y, int z, int width, int height) {
      this.positionXYZ(x, y, z).size(width, height);
   }

   public Spatial(ISpatial elementSpatial) {
      this(elementSpatial, elementSpatial);
   }

   public Spatial(IPosition position) {
      this.positionXYZ(position);
   }

   public Spatial(ISize size) {
      this.size(size);
   }

   public Spatial(IPosition position, ISize size) {
      this.positionXYZ(position).size(size);
   }

   @Override
   public int x() {
      return this.x;
   }

   @Override
   public int y() {
      return this.y;
   }

   @Override
   public int z() {
      return this.z;
   }

   public Spatial positionX(int x) {
      this.x = x;
      return this;
   }

   public Spatial positionX(IPosition position) {
      return this.positionX(position.x());
   }

   public Spatial positionY(int y) {
      this.y = y;
      return this;
   }

   public Spatial positionY(IPosition position) {
      return this.positionY(position.y());
   }

   public Spatial positionZ(int z) {
      this.z = z;
      return this;
   }

   public Spatial positionZ(IPosition position) {
      return this.positionZ(position.z());
   }

   public Spatial positionXY(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public Spatial positionXY(IPosition position) {
      return this.positionXY(position.x(), position.y());
   }

   public Spatial positionXYZ(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
   }

   public Spatial positionXYZ(IPosition position) {
      return this.positionXYZ(position.x(), position.y(), position.z());
   }

   public Spatial translateX(int x) {
      return this.translateXYZ(x, 0, 0);
   }

   public Spatial translateX(IPosition position) {
      return this.translateX(position.x());
   }

   public Spatial translateY(int y) {
      return this.translateXYZ(0, y, 0);
   }

   public Spatial translateY(IPosition position) {
      return this.translateY(position.y());
   }

   public Spatial translateZ(int z) {
      return this.translateXYZ(0, 0, z);
   }

   public Spatial translateZ(IPosition position) {
      return this.translateZ(position.z());
   }

   public Spatial translateXY(int x, int y) {
      return this.translateXYZ(x, y, 0);
   }

   public Spatial translateXY(IPosition position) {
      return this.translateXY(position.x(), position.y());
   }

   public Spatial translateXYZ(int x, int y, int z) {
      this.x += x;
      this.y += y;
      this.z += z;
      return this;
   }

   public Spatial translateXYZ(IPosition elementPosition) {
      return this.translateXYZ(elementPosition.x(), elementPosition.y(), elementPosition.z());
   }

   @Override
   public int width() {
      return this.width;
   }

   @Override
   public int height() {
      return this.height;
   }

   public Spatial width(int width) {
      this.width = width;
      return this;
   }

   public Spatial width(ISize size) {
      return this.width(size.width());
   }

   public Spatial height(int height) {
      this.height = height;
      return this;
   }

   public Spatial height(ISize size) {
      return this.height(size.height());
   }

   public Spatial size(int width, int height) {
      this.width = width;
      this.height = height;
      return this;
   }

   public Spatial size(ISize size) {
      return this.size(size.width(), size.height());
   }

   @Override
   public int right() {
      return this.x + this.width;
   }

   @Override
   public int left() {
      return this.x;
   }

   @Override
   public int top() {
      return this.y;
   }

   @Override
   public int bottom() {
      return this.y + this.height;
   }

   @Override
   public boolean contains(double x, double y) {
      return x <= this.right() && x >= this.left() && y >= this.top() && y <= this.bottom();
   }

   public Spatial copy() {
      return new Spatial((ISpatial)this);
   }

   public Spatial zero() {
      return this.set(ISpatial.ZERO);
   }

   public Spatial set(ISpatial spatial) {
      return this.positionXYZ(spatial).size(spatial);
   }

   public Spatial add(ISpatial spatial) {
      this.x = this.x + spatial.x();
      this.y = this.y + spatial.y();
      this.z = this.z + spatial.z();
      this.width = this.width + spatial.width();
      this.height = this.height + spatial.height();
      return this;
   }

   public Spatial include(ISpatial spatial) {
      this.width = this.width + Math.max(0, spatial.right() - this.right());
      this.height = this.height + Math.max(0, spatial.bottom() - this.bottom());
      int dX = Math.min(0, spatial.left() - this.left());
      this.x += dX;
      this.width -= dX;
      int dY = Math.min(0, spatial.top() - this.top());
      this.y += dY;
      this.height -= dY;
      return this;
   }

   @Override
   public String toString() {
      return "{(" + this.x + ", " + this.y + ", " + this.z + "), width=" + this.width + ", height=" + this.height + "}";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Spatial that = (Spatial)o;
         return this.x == that.x && this.y == that.y && this.z == that.z && this.width == that.width && this.height == that.height;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.x, this.y, this.z, this.width, this.height);
   }
}
