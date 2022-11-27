package iskallia.vault.client.gui.framework.spatial;

public record Padding(int left, int right, int top, int bottom) {
   public static final Padding ZERO = of(0, 0, 0, 0);

   public static Padding of(int left, int right, int top, int bottom) {
      return new Padding(left, right, top, bottom);
   }

   public static Padding of(int horizontal, int vertical) {
      return new Padding(horizontal, horizontal, vertical, vertical);
   }

   public int horizontal() {
      return this.left + this.right;
   }

   public int vertical() {
      return this.top + this.bottom;
   }
}
