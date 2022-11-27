package iskallia.vault.client.gui.framework.spatial.spi;

public interface IPosition {
   IPosition ZERO = new IPosition() {
      @Override
      public int x() {
         return 0;
      }

      @Override
      public int y() {
         return 0;
      }

      @Override
      public int z() {
         return 0;
      }

      @Override
      public String toString() {
         return "IPosition{ZERO}";
      }
   };

   int x();

   int y();

   int z();

   default IPosition unmodifiableView() {
      return new IPosition() {
         @Override
         public int x() {
            return IPosition.this.x();
         }

         @Override
         public int y() {
            return IPosition.this.y();
         }

         @Override
         public int z() {
            return IPosition.this.z();
         }

         @Override
         public String toString() {
            return "Unmodifiable{" + IPosition.this + "}";
         }
      };
   }
}
