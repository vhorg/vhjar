package iskallia.vault.gear.attribute.config;

import iskallia.vault.gear.reader.VaultGearModifierReader;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public abstract class ConfigurableAttributeGenerator<T, C> {
   @Nullable
   public abstract Class<C> getConfigurationObjectClass();

   public abstract T generateRandomValue(C var1, Random var2);

   public MutableComponent getConfigDisplay(VaultGearModifierReader<T> reader, C object) {
      return new TextComponent("");
   }
}
