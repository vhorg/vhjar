package iskallia.vault.client.gui.widget.connect;

import java.awt.geom.Point2D.Double;

public interface ConnectableWidget {
   Double getRenderPosition();

   double getRenderWidth();

   double getRenderHeight();

   default java.awt.geom.Rectangle2D.Double getRenderBox() {
      return new java.awt.geom.Rectangle2D.Double(this.getRenderPosition().x, this.getRenderPosition().y, this.getRenderWidth(), this.getRenderHeight());
   }

   default Double getPointOnEdge(double angleDeg) {
      double twoPI = Math.PI * 2;
      double theta = angleDeg * Math.PI / 180.0;

      while (theta < -Math.PI) {
         theta += twoPI;
      }

      while (theta > Math.PI) {
         theta -= twoPI;
      }

      double width = this.getRenderWidth();
      double height = this.getRenderHeight();
      double rectAtan = Math.atan2(height, width);
      double tanTheta = Math.tan(theta);
      double xFactor = 1.0;
      double yFactor = 1.0;
      boolean horizontal = false;
      if (theta > -rectAtan && theta <= rectAtan) {
         horizontal = true;
         yFactor = -1.0;
      } else if (theta > rectAtan && theta <= Math.PI - rectAtan) {
         yFactor = -1.0;
      } else if (!(theta > Math.PI - rectAtan) && !(theta <= -(Math.PI - rectAtan))) {
         xFactor = -1.0;
      } else {
         horizontal = true;
         xFactor = -1.0;
      }

      Double pos = this.getRenderPosition();
      return horizontal
         ? new Double(pos.x + xFactor * (width / 2.0), pos.y + yFactor * (width / 2.0) * tanTheta)
         : new Double(pos.x + xFactor * (height / (2.0 * tanTheta)), pos.y + yFactor * (height / 2.0));
   }
}
