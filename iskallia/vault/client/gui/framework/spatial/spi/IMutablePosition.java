package iskallia.vault.client.gui.framework.spatial.spi;

public interface IMutablePosition extends IPosition {
   IMutablePosition positionX(int var1);

   IMutablePosition positionX(IPosition var1);

   IMutablePosition positionY(int var1);

   IMutablePosition positionY(IPosition var1);

   IMutablePosition positionZ(int var1);

   IMutablePosition positionZ(IPosition var1);

   IMutablePosition positionXY(int var1, int var2);

   IMutablePosition positionXY(IPosition var1);

   IMutablePosition positionXYZ(int var1, int var2, int var3);

   IMutablePosition positionXYZ(IPosition var1);

   IMutablePosition translateX(int var1);

   IMutablePosition translateX(IPosition var1);

   IMutablePosition translateY(int var1);

   IMutablePosition translateY(IPosition var1);

   IMutablePosition translateZ(int var1);

   IMutablePosition translateZ(IPosition var1);

   IMutablePosition translateXY(int var1, int var2);

   IMutablePosition translateXY(IPosition var1);

   IMutablePosition translateXYZ(int var1, int var2, int var3);

   IMutablePosition translateXYZ(IPosition var1);
}
