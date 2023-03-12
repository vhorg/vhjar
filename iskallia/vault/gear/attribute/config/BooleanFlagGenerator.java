package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class BooleanFlagGenerator extends ConstantObjectGenerator<Boolean, BooleanFlagGenerator.BooleanFlag> {
   public BooleanFlagGenerator() {
      super(BooleanFlagGenerator.BooleanFlag.class);
   }

   @Nullable
   public MutableComponent getConfigDisplay(VaultGearModifierReader<Boolean> reader, BooleanFlagGenerator.BooleanFlag object) {
      return new TextComponent(reader.getModifierName()).withStyle(reader.getColoredTextStyle());
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
