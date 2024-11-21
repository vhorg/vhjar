package iskallia.vault.gear.attribute;

import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.data.GearDataVersion;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

public class VaultGearAttributeSerializer {
   private static final Map<Integer, VaultGearAttributeSerializer.AttributeInstanceSerializer<?>> TYPE_SERIALIZERS = new HashMap<>();
   public static final VaultGearAttributeInstance.Serializer INSTANCE_SERIALIZER = new VaultGearAttributeInstance.Serializer();
   public static final VaultGearModifier.Serializer MODIFIER_SERIALIZER = new VaultGearModifier.Serializer();
   private static final int DEFAULT_SERIALIZER_ID = 0;
   private static final VaultGearAttributeSerializer.AttributeInstanceSerializer<?> DEFAULT_SERIALIZER = INSTANCE_SERIALIZER;

   public static void register(int id, @Nonnull VaultGearAttributeSerializer.AttributeInstanceSerializer<?> serializer) {
      TYPE_SERIALIZERS.put(id, serializer);
   }

   @Nonnull
   private static <T extends VaultGearAttributeInstance<?>> Tuple<Integer, VaultGearAttributeSerializer.AttributeInstanceSerializer<T>> getSerializer(
      VaultGearAttributeInstance<?> instance
   ) {
      for (Entry<Integer, VaultGearAttributeSerializer.AttributeInstanceSerializer<?>> entry : TYPE_SERIALIZERS.entrySet()) {
         if (entry.getValue().accepts(instance)) {
            VaultGearAttributeSerializer.AttributeInstanceSerializer<T> serializer = (VaultGearAttributeSerializer.AttributeInstanceSerializer<T>)entry.getValue();
            return new Tuple(entry.getKey(), serializer);
         }
      }

      return new Tuple(0, DEFAULT_SERIALIZER);
   }

   @Nonnull
   private static <T extends VaultGearAttributeInstance<?>> Tuple<Integer, VaultGearAttributeSerializer.AttributeInstanceSerializer<T>> getSerializer(int id) {
      VaultGearAttributeSerializer.AttributeInstanceSerializer<T> serializer = (VaultGearAttributeSerializer.AttributeInstanceSerializer<T>)TYPE_SERIALIZERS.getOrDefault(
         id, DEFAULT_SERIALIZER
      );
      return new Tuple(id, serializer);
   }

   @Nullable
   public static <T extends VaultGearAttributeInstance<?>> T deserializeTag(CompoundTag tag, GearDataVersion version) {
      if (!tag.contains("type", 3)) {
         return null;
      } else {
         int type = tag.getInt("type");
         Tuple<Integer, VaultGearAttributeSerializer.AttributeInstanceSerializer<T>> serializer = getSerializer(type);
         return (T)((VaultGearAttributeSerializer.AttributeInstanceSerializer)serializer.getB()).deserialize(tag, version);
      }
   }

   public static <T extends VaultGearAttributeInstance<?>> CompoundTag serializeTag(T instance) {
      Tuple<Integer, VaultGearAttributeSerializer.AttributeInstanceSerializer<T>> serializer = getSerializer(instance);
      CompoundTag tag = new CompoundTag();
      tag.putInt("type", (Integer)serializer.getA());
      ((VaultGearAttributeSerializer.AttributeInstanceSerializer)serializer.getB()).serialize(instance, tag);
      return tag;
   }

   @Nullable
   public static <T extends VaultGearAttributeInstance<?>> T deserialize(BitBuffer buf, GearDataVersion version) {
      int type = buf.readInt();
      Tuple<Integer, VaultGearAttributeSerializer.AttributeInstanceSerializer<T>> serializer = getSerializer(type);
      return (T)((VaultGearAttributeSerializer.AttributeInstanceSerializer)serializer.getB()).deserialize(buf, version);
   }

   public static <T extends VaultGearAttributeInstance<?>> void serialize(T instance, BitBuffer buf) {
      Tuple<Integer, VaultGearAttributeSerializer.AttributeInstanceSerializer<T>> serializer = getSerializer(instance);
      buf.writeInt((Integer)serializer.getA());
      ((VaultGearAttributeSerializer.AttributeInstanceSerializer)serializer.getB()).serialize(instance, buf);
   }

   static {
      register(1, MODIFIER_SERIALIZER);
   }

   public interface AttributeInstanceSerializer<T extends VaultGearAttributeInstance<?>> {
      boolean accepts(VaultGearAttributeInstance<?> var1);

      @Nullable
      T deserialize(CompoundTag var1, GearDataVersion var2);

      @Nullable
      T deserialize(BitBuffer var1, GearDataVersion var2);

      void serialize(T var1, CompoundTag var2);

      void serialize(T var1, BitBuffer var2);

      @Nullable
      default T deserializeInto(CompoundTag tag, GearDataVersion version, Function<VaultGearAttribute<?>, T> constructor) {
         ResourceLocation key = new ResourceLocation(tag.getString("key"));
         VaultGearAttribute<?> attribute = VaultGearAttributeRegistry.getAttribute(key);
         if (attribute == null) {
            throw new IllegalArgumentException("Unknown attribute: " + key);
         } else {
            T instance = constructor.apply(attribute);
            instance.fromNbt(tag, version);
            return !instance.isValid() ? null : instance;
         }
      }

      @Nullable
      default T deserializeInto(BitBuffer buf, GearDataVersion version, Function<VaultGearAttribute<?>, T> constructor) {
         ResourceLocation key = buf.readIdentifier();
         VaultGearAttribute<?> attribute = VaultGearAttributeRegistry.getAttribute(key);
         if (attribute == null) {
            throw new IllegalArgumentException("Unknown attribute: " + key);
         } else {
            T instance = constructor.apply(attribute);
            instance.read(buf, version);
            return !instance.isValid() ? null : instance;
         }
      }

      default void serializeInto(T instance, CompoundTag tag) {
         tag.putString("key", instance.getAttribute().getRegistryName().toString());
         instance.toNbt(tag);
      }

      default void serializeInto(T instance, BitBuffer buf) {
         buf.writeIdentifier(instance.getAttribute().getRegistryName());
         instance.write(buf);
      }
   }
}
