package iskallia.vault.client.gui.framework.spatial.spi;

public interface IMutableSpatial extends ISpatial, IMutablePosition, IMutableSize {
   IMutableSpatial zero();

   IMutableSpatial set(ISpatial var1);

   IMutableSpatial add(ISpatial var1);

   IMutableSpatial include(ISpatial var1);

   IMutableSpatial copy();

   IMutableSpatial positionX(int var1);

   IMutableSpatial positionX(IPosition var1);

   IMutableSpatial positionY(int var1);

   IMutableSpatial positionY(IPosition var1);

   IMutableSpatial positionZ(int var1);

   IMutableSpatial positionZ(IPosition var1);

   IMutableSpatial positionXY(int var1, int var2);

   IMutableSpatial positionXY(IPosition var1);

   IMutableSpatial positionXYZ(int var1, int var2, int var3);

   IMutableSpatial positionXYZ(IPosition var1);

   IMutableSpatial translateX(int var1);

   IMutableSpatial translateX(IPosition var1);

   IMutableSpatial translateY(int var1);

   IMutableSpatial translateY(IPosition var1);

   IMutableSpatial translateZ(int var1);

   IMutableSpatial translateZ(IPosition var1);

   IMutableSpatial translateXY(int var1, int var2);

   IMutableSpatial translateXY(IPosition var1);

   IMutableSpatial translateXYZ(int var1, int var2, int var3);

   IMutableSpatial translateXYZ(IPosition var1);

   IMutableSpatial width(int var1);

   IMutableSpatial width(ISize var1);

   IMutableSpatial height(int var1);

   IMutableSpatial height(ISize var1);

   IMutableSpatial size(int var1, int var2);

   IMutableSpatial size(ISize var1);
}
