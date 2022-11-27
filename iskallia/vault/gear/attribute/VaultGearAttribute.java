package iskallia.vault.gear.attribute;

import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class VaultGearAttribute<T> extends ForgeRegistryEntry<VaultGearAttribute<?>> {
   private final VaultGearAttributeType<T> type;
   private final ConfigurableAttributeGenerator<T, ?> generator;
   private final VaultGearModifierReader<T> reader;

   public VaultGearAttribute(
      ResourceLocation name, VaultGearAttributeType<T> type, ConfigurableAttributeGenerator<T, ?> generator, VaultGearModifierReader<T> reader
   ) {
      this.setRegistryName(name);
      this.generator = generator;
      this.reader = reader;
      this.type = type;
   }

   public VaultGearAttributeType<T> getType() {
      return this.type;
   }

   public ConfigurableAttributeGenerator<T, ?> getGenerator() {
      return this.generator;
   }

   public VaultGearModifierReader<T> getReader() {
      return this.reader;
   }

   public Component getValueDisplay(T value) {
      return this.getReader().getValueDisplay(value);
   }

   public String toString() {
      return this.getRegistryName().toString();
   }
}
