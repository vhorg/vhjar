package iskallia.vault.init;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.reader.DecimalModifierReader;
import iskallia.vault.gear.reader.FlagModifierReader;
import iskallia.vault.gear.reader.IntegerModifierReader;
import iskallia.vault.gear.reader.StringConstantModifierReader;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;

public class ModGearAttributeReaders {
   public static IntegerModifierReader addedIntReader(String modifierName, int rgbColor) {
      return new IntegerModifierReader(modifierName, rgbColor);
   }

   public static <T extends Number> DecimalModifierReader<T> percentageReader(String modifierName, int rgbColor) {
      return new DecimalModifierReader.Percentage<>(modifierName, rgbColor);
   }

   public static <T extends Number> DecimalModifierReader<T> addedDecimalReader(String modifierName, int rgbColor) {
      return new DecimalModifierReader.Added<>(modifierName, rgbColor);
   }

   public static <T extends Float> DecimalModifierReader<T> addedRoundedDecimalReader(String modifierName, int rgbColor) {
      return new DecimalModifierReader.Round<>(modifierName, rgbColor);
   }

   public static StringConstantModifierReader stringReader(String modifierName, int rgbColor, String format) {
      return new StringConstantModifierReader(modifierName, rgbColor, format);
   }

   public static FlagModifierReader booleanReader(String modifierName, int rgbColor) {
      return new FlagModifierReader(modifierName, rgbColor);
   }

   public static <T> VaultGearModifierReader<T> none() {
      return new VaultGearModifierReader<T>("", 0) {
         @Nullable
         @Override
         public MutableComponent getDisplay(VaultGearAttributeInstance<T> instance, VaultGearModifier.AffixType type) {
            return null;
         }

         @Nullable
         @Override
         public MutableComponent getValueDisplay(T value) {
            return null;
         }

         @Override
         protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<T> instance, VaultGearModifier.AffixType type) {
         }
      };
   }
}
