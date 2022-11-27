package iskallia.vault.client.gui.framework.text;

public enum LabelAutoResize {
   NONE(false, false),
   WIDTH(true, false),
   HEIGHT(false, true),
   WIDTH_HEIGHT(true, true);

   private final boolean width;
   private final boolean height;

   private LabelAutoResize(boolean width, boolean height) {
      this.width = width;
      this.height = height;
   }

   public boolean isWidth() {
      return this.width;
   }

   public boolean isHeight() {
      return this.height;
   }
}
