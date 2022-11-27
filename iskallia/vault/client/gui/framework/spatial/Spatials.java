package iskallia.vault.client.gui.framework.spatial;

import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;

public final class Spatials {
   public static ISpatial zero() {
      return ISpatial.ZERO;
   }

   public static IPosition positionZero() {
      return IPosition.ZERO;
   }

   public static ISize sizeZero() {
      return ISize.ZERO;
   }

   public static IMutableSpatial copy(ISpatial spatial) {
      return new Spatial(spatial);
   }

   public static IMutableSpatial copyPosition(IPosition position) {
      return new Spatial(position);
   }

   public static IMutableSpatial copySize(ISize size) {
      return new Spatial(size);
   }

   public static IMutableSpatial positionX(int x) {
      return new Spatial().positionX(x);
   }

   public static IMutableSpatial positionX(IPosition position) {
      return positionX(position.x());
   }

   public static IMutableSpatial positionY(int y) {
      return new Spatial().positionY(y);
   }

   public static IMutableSpatial positionY(IPosition position) {
      return positionY(position.y());
   }

   public static IMutableSpatial positionZ(int z) {
      return new Spatial().positionZ(z);
   }

   public static IMutableSpatial positionZ(IPosition position) {
      return positionZ(position.z());
   }

   public static IMutableSpatial positionXY(int x, int y) {
      return new Spatial().positionXY(x, y);
   }

   public static IMutableSpatial positionXY(IPosition position) {
      return positionXY(position.x(), position.y());
   }

   public static IMutableSpatial positionXYZ(int x, int y, int z) {
      return new Spatial().positionXYZ(x, y, z);
   }

   public static IMutableSpatial positionXYZ(IPosition position) {
      return new Spatial(position.x(), position.y(), position.z(), 0, 0);
   }

   public static IMutableSpatial size(int width, int height) {
      return new Spatial().size(width, height);
   }

   public static IMutableSpatial size(ISize size) {
      return new Spatial().size(size);
   }

   public static IMutableSpatial width(int width) {
      return new Spatial().width(width);
   }

   public static IMutableSpatial width(ISize size) {
      return width(size.width());
   }

   public static IMutableSpatial height(int height) {
      return new Spatial().height(height);
   }

   public static IMutableSpatial height(ISize size) {
      return height(size.height());
   }

   public static ISpatial unmodifiable(ISpatial spatial) {
      return spatial.unmodifiableView();
   }

   public static IPosition unmodifiablePosition(IPosition position) {
      return position.unmodifiableView();
   }

   public static ISize unmodifiableSize(ISize size) {
      return size.unmodifiableView();
   }

   private Spatials() {
   }
}
