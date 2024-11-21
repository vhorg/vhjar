package iskallia.vault.gear.attribute.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;

public abstract class ConfigurableAttributeGenerator<T, C> {
   @Nullable
   public abstract Class<C> getConfigurationObjectClass();

   public abstract T generateRandomValue(C var1, Random var2);

   @Nullable
   public MutableComponent getConfigRangeDisplay(VaultGearModifierReader<T> reader, C object) {
      return this.getConfigRangeDisplay(reader, object, object);
   }

   @Nullable
   public MutableComponent getConfigRangeDisplay(VaultGearModifierReader<T> reader, C min, C max) {
      return null;
   }

   @Nullable
   public MutableComponent getConfigDisplay(VaultGearModifierReader<T> reader, C object) {
      return this.getConfigRangeDisplay(reader, object);
   }

   public abstract Optional<T> getMinimumValue(List<C> var1);

   public abstract Optional<T> getMaximumValue(List<C> var1);

   public Optional<Float> getRollPercentage(T value, List<C> configurations) {
      return Optional.of(1.0F);
   }

   public interface CustomTierConfig {
      void deserializeAdditional(JsonObject var1, JsonDeserializationContext var2);

      void serializeAdditional(JsonObject var1, JsonSerializationContext var2);
   }
}
