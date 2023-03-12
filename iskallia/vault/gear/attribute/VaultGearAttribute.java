package iskallia.vault.gear.attribute;

import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class VaultGearAttribute<T> extends ForgeRegistryEntry<VaultGearAttribute<?>> {
   private final VaultGearAttributeType<T> type;
   private final ConfigurableAttributeGenerator<T, ?> generator;
   private final VaultGearModifierReader<T> reader;
   private final VaultGearAttributeComparator<T> attributeComparator;

   public VaultGearAttribute(
      ResourceLocation name,
      VaultGearAttributeType<T> type,
      ConfigurableAttributeGenerator<T, ?> generator,
      VaultGearModifierReader<T> reader,
      @Nullable VaultGearAttributeComparator<T> attributeComparator
   ) {
      this.setRegistryName(name);
      this.generator = generator;
      this.reader = reader;
      this.type = type;
      this.attributeComparator = attributeComparator;
   }

   public VaultGearAttribute(
      ResourceLocation name, VaultGearAttributeType<T> type, ConfigurableAttributeGenerator<T, ?> generator, VaultGearModifierReader<T> reader
   ) {
      this(name, type, generator, reader, null);
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

   @Nullable
   public VaultGearAttributeComparator<T> getAttributeComparator() {
      return this.attributeComparator;
   }

   public Component getValueDisplay(T value) {
      return this.getReader().getValueDisplay(value);
   }

   public String toString() {
      return this.getRegistryName().toString();
   }
}
