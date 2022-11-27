package iskallia.vault.client.gui.framework.spatial.spi;

public interface ISize {
   ISize ZERO = new ISize() {
      @Override
      public int width() {
         return 0;
      }

      @Override
      public int height() {
         return 0;
      }

      @Override
      public String toString() {
         return "ISize{ZERO}";
      }
   };

   int width();

   int height();

   default ISize unmodifiableView() {
      return new ISize() {
         @Override
         public int width() {
            return ISize.this.width();
         }

         @Override
         public int height() {
            return ISize.this.height();
         }

         @Override
         public String toString() {
            return "Unmodifiable{" + ISize.this + "}";
         }
      };
   }
}
