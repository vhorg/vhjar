package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import java.util.function.Supplier;

public class BooleanFlagGenerator extends ConstantObjectGenerator<Boolean, BooleanFlagGenerator.BooleanFlag> {
   public BooleanFlagGenerator() {
      super(BooleanFlagGenerator.BooleanFlag.class);
   }

   public static class BooleanFlag implements Supplier<Boolean> {
      @Expose
      private boolean flag;

      public BooleanFlag(boolean flag) {
         this.flag = flag;
      }

      public Boolean get() {
         return this.flag;
      }
   }
}
