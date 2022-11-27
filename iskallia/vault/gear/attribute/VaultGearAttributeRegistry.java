package iskallia.vault.gear.attribute;

import iskallia.vault.VaultMod;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.data.GearDataVersion;
import iskallia.vault.util.MiscUtils;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class VaultGearAttributeRegistry {
   public static ResourceKey<Registry<VaultGearAttribute<?>>> ATTRIBUTE_REGISTRY_KEY = ResourceKey.createRegistryKey(VaultMod.id("gear_attributes"));
   private static IForgeRegistry<VaultGearAttribute<?>> attributeRegistry;

   public static IForgeRegistry<VaultGearAttribute<?>> getRegistry() {
      return attributeRegistry;
   }

   @Nullable
   public static VaultGearAttribute<?> getAttribute(ResourceLocation key) {
      return (VaultGearAttribute<?>)attributeRegistry.getValue(key);
   }

   public static CompoundTag serializeAttributeInstance(VaultGearAttributeInstance<?> instance) {
      CompoundTag tag = new CompoundTag();
      tag.putString("key", instance.getAttribute().getRegistryName().toString());
      instance.toNbt(tag);
      return tag;
   }

   @Nullable
   public static VaultGearAttributeInstance<?> deserializeAttributeInstance(CompoundTag tag, GearDataVersion version) {
      return deserializeTagInto(tag, version, VaultGearAttributeInstance::new);
   }

   @Nullable
   public static VaultGearModifier<?> deserializeModifier(CompoundTag tag, GearDataVersion version) {
      return deserializeTagInto(tag, version, VaultGearModifier::new);
   }

   @Nullable
   private static <V extends VaultGearAttributeInstance<?>> V deserializeTagInto(
      CompoundTag tag, GearDataVersion version, Function<VaultGearAttribute<?>, V> constructor
   ) {
      ResourceLocation key = new ResourceLocation(tag.getString("key"));
      VaultGearAttribute<?> attribute = getAttribute(key);
      if (attribute == null) {
         return null;
      } else {
         V instance = (V)constructor.apply(attribute);
         instance.fromNbt(tag, version);
         return !instance.isValid() ? null : instance;
      }
   }

   public static void writeAttributeInstance(VaultGearAttributeInstance<?> instance, BitBuffer buf) {
      buf.writeIdentifier(instance.getAttribute().getRegistryName());
      instance.write(buf);
   }

   @Nullable
   public static VaultGearAttributeInstance<?> readAttributeInstance(BitBuffer buf, GearDataVersion version) {
      return deserializeInto(buf, version, VaultGearAttributeInstance::new);
   }

   @Nullable
   public static VaultGearModifier<?> readModifier(BitBuffer buf, GearDataVersion version) {
      return deserializeInto(buf, version, VaultGearModifier::new);
   }

   @Nullable
   private static <V extends VaultGearAttributeInstance<?>> V deserializeInto(
      BitBuffer buf, GearDataVersion version, Function<VaultGearAttribute<?>, V> constructor
   ) {
      ResourceLocation key = buf.readIdentifier();
      VaultGearAttribute<?> attribute = getAttribute(key);
      if (attribute == null) {
         return null;
      } else {
         V instance = (V)constructor.apply(attribute);
         instance.read(buf, version);
         return !instance.isValid() ? null : instance;
      }
   }

   public static void buildRegistry(NewRegistryEvent event) {
      event.create(
         new RegistryBuilder().setName(ATTRIBUTE_REGISTRY_KEY.location()).setType(MiscUtils.cast(VaultGearAttribute.class)).disableSaving().disableOverrides(),
         registry -> attributeRegistry = registry
      );
   }
}
