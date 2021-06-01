package iskallia.vault.client.gui.widget;

import iskallia.vault.client.gui.helper.Rectangle;

public class RaffleEntry {
   protected Rectangle bounds;
   protected String occupantName;
   protected int typeIndex;

   public RaffleEntry(String occupantName, int typeIndex) {
      this.occupantName = occupantName;
      this.typeIndex = typeIndex;
   }

   public void setBounds(Rectangle bounds) {
      this.bounds = bounds;
   }

   public Rectangle getBounds() {
      return this.bounds;
   }

   public String getOccupantName() {
      return this.occupantName;
   }

   public int getTypeIndex() {
      return this.typeIndex;
   }
}
