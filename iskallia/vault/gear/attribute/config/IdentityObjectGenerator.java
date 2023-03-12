package iskallia.vault.gear.attribute.config;

import iskallia.vault.gear.reader.VaultGearModifierReader;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class IdentityObjectGenerator<T> extends ConfigurableAttributeGenerator<T, T> {
   private final Class<T> configObjectClass;

   public IdentityObjectGenerator(Class<T> configObjectClass) {
      this.configObjectClass = configObjectClass;
   }

   @Nullable
   @Override
   public Class<T> getConfigurationObjectClass() {
      return this.configObjectClass;
   }

   @Override
   public T generateRandomValue(T object, Random random) {
      return object;
   }

   @Nullable
   @Override
   public MutableComponent getConfigDisplay(VaultGearModifierReader<T> reader, T object) {
      return new TextComponent(reader.getModifierName()).withStyle(reader.getColoredTextStyle());
   }
}
