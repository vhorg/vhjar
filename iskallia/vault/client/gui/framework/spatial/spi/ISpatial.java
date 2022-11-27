package iskallia.vault.client.gui.framework.spatial.spi;

public interface ISpatial extends IPosition, ISize {
   ISpatial ZERO = new ISpatial() {
      @Override
      public boolean contains(double x, double y) {
         return false;
      }

      @Override
      public int right() {
         return 0;
      }

      @Override
      public int left() {
         return 0;
      }

      @Override
      public int top() {
         return 0;
      }

      @Override
      public int bottom() {
         return 0;
      }

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
      public int width() {
         return 0;
      }

      @Override
      public int height() {
         return 0;
      }

      @Override
      public String toString() {
         return "ISpatial{ZERO}";
      }
   };

   boolean contains(double var1, double var3);

   int right();

   int left();

   int top();

   int bottom();

   default ISpatial unmodifiableView() {
      return new ISpatial() {
         @Override
         public boolean contains(double x, double y) {
            return ISpatial.this.contains(x, y);
         }

         @Override
         public int right() {
            return ISpatial.this.right();
         }

         @Override
         public int left() {
            return ISpatial.this.left();
         }

         @Override
         public int top() {
            return ISpatial.this.top();
         }

         @Override
         public int bottom() {
            return ISpatial.this.bottom();
         }

         @Override
         public int x() {
            return ISpatial.this.x();
         }

         @Override
         public int y() {
            return ISpatial.this.y();
         }

         @Override
         public int z() {
            return ISpatial.this.z();
         }

         @Override
         public int width() {
            return ISpatial.this.width();
         }

         @Override
         public int height() {
            return ISpatial.this.height();
         }

         @Override
         public String toString() {
            return "Unmodifiable{" + ISpatial.this + "}";
         }
      };
   }
}
