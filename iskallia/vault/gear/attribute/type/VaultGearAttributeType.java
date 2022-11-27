package iskallia.vault.gear.attribute.type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.config.adapter.ColorAdapter;
import iskallia.vault.config.adapter.RegistryCodecAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.net.IBitSerializer;
import iskallia.vault.util.NetcodeUtils;
import java.awt.Color;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class VaultGearAttributeType<T> implements IBitSerializer<T> {
   public static final Gson GSON = new GsonBuilder()
      .registerTypeHierarchyAdapter(MobEffect.class, RegistryCodecAdapter.of(ForgeRegistries.MOB_EFFECTS))
      .registerTypeHierarchyAdapter(Color.class, ColorAdapter.INSTANCE)
      .create();

   public abstract void netWrite(ByteBuf var1, T var2);

   public abstract T netRead(ByteBuf var1);

   public abstract JsonElement serialize(T var1);

   public abstract T nbtRead(Tag var1);

   public abstract Tag nbtWrite(T var1);

   public abstract T cast(Object var1);

   public static <T> VaultGearAttributeType<T> of(
      BiConsumer<BitBuffer, T> writer,
      Function<BitBuffer, T> reader,
      BiConsumer<ByteBuf, T> netWriter,
      Function<ByteBuf, T> netReader,
      Function<T, JsonElement> serializer,
      Function<Tag, T> nbtRead,
      Function<T, Tag> nbtWrite
   ) {
      return of(writer, reader, netWriter, netReader, serializer, nbtRead, nbtWrite, o -> (T)o);
   }

   public static <T> VaultGearAttributeType<T> of(
      final BiConsumer<BitBuffer, T> writer,
      final Function<BitBuffer, T> reader,
      final BiConsumer<ByteBuf, T> netWriter,
      final Function<ByteBuf, T> netReader,
      final Function<T, JsonElement> serializer,
      final Function<Tag, T> nbtRead,
      final Function<T, Tag> nbtWrite,
      final Function<Object, T> valueCast
   ) {
      return new VaultGearAttributeType<T>() {
         @Override
         public void write(BitBuffer buf, T value) {
            writer.accept(buf, value);
         }

         @Override
         public T read(BitBuffer buf) {
            return reader.apply(buf);
         }

         @Override
         public void netWrite(ByteBuf buf, T value) {
            netWriter.accept(buf, value);
         }

         @Override
         public T netRead(ByteBuf buf) {
            return netReader.apply(buf);
         }

         @Override
         public JsonElement serialize(T value) {
            return serializer.apply(value);
         }

         @Override
         public T nbtRead(Tag tag) {
            return nbtRead.apply(tag);
         }

         @Override
         public Tag nbtWrite(T value) {
            return nbtWrite.apply(value);
         }

         @Override
         public T cast(Object value) {
            return valueCast.apply(value);
         }
      };
   }

   public static <T> VaultGearAttributeType<T> of(
      IBitSerializer<T> serializer,
      BiConsumer<ByteBuf, T> netWriter,
      Function<ByteBuf, T> netReader,
      Function<T, JsonElement> jsonSerializer,
      Function<Tag, T> nbtRead,
      Function<T, Tag> nbtWrite
   ) {
      return of(serializer::write, serializer::read, netWriter, netReader, jsonSerializer, nbtRead, nbtWrite);
   }

   public static VaultGearAttributeType<Integer> intType() {
      return of(
         BitBuffer::writeInt,
         BitBuffer::readInt,
         ByteBuf::writeInt,
         ByteBuf::readInt,
         JsonPrimitive::new,
         numericTag(NumericTag::getAsInt),
         IntTag::valueOf,
         number(Number::intValue, Integer.class)
      );
   }

   public static VaultGearAttributeType<Float> floatType() {
      return of(
         BitBuffer::writeFloat,
         BitBuffer::readFloat,
         ByteBuf::writeFloat,
         ByteBuf::readFloat,
         JsonPrimitive::new,
         numericTag(NumericTag::getAsFloat),
         FloatTag::valueOf,
         number(Number::floatValue, Float.class)
      );
   }

   public static VaultGearAttributeType<Double> doubleType() {
      return of(
         BitBuffer::writeDouble,
         BitBuffer::readDouble,
         ByteBuf::writeDouble,
         ByteBuf::readDouble,
         JsonPrimitive::new,
         numericTag(NumericTag::getAsDouble),
         DoubleTag::valueOf,
         number(Number::doubleValue, Double.class)
      );
   }

   public static VaultGearAttributeType<Boolean> booleanType() {
      return of(
         BitBuffer::writeBoolean,
         BitBuffer::readBoolean,
         ByteBuf::writeBoolean,
         ByteBuf::readBoolean,
         JsonPrimitive::new,
         stringTag(Boolean::parseBoolean),
         flag -> StringTag.valueOf(Boolean.toString(flag))
      );
   }

   public static VaultGearAttributeType<String> stringType() {
      return of(
         BitBuffer::writeString,
         BitBuffer::readString,
         NetcodeUtils::writeString,
         NetcodeUtils::readString,
         JsonPrimitive::new,
         stringTag(),
         StringTag::valueOf
      );
   }

   public static VaultGearAttributeType<ResourceLocation> identifierType() {
      return of(
         BitBuffer::writeIdentifier,
         BitBuffer::readIdentifier,
         NetcodeUtils::writeIdentifier,
         NetcodeUtils::readIdentifier,
         id -> new JsonPrimitive(id.toString()),
         stringTag(ResourceLocation::new),
         key -> StringTag.valueOf(key.toString())
      );
   }

   public static <E extends Enum<E>> VaultGearAttributeType<E> enumType(Class<E> enumClazz) {
      return (VaultGearAttributeType<E>)of(
         BitBuffer::writeEnum,
         buf -> buf.readEnum(enumClazz),
         (buf, value) -> buf.writeInt(value.ordinal()),
         buf -> ((Enum[])enumClazz.getEnumConstants())[buf.readInt()],
         value -> new JsonPrimitive(value.name()),
         numericTag(numberTag -> ((Enum[])enumClazz.getEnumConstants())[numberTag.getAsInt()]),
         val -> IntTag.valueOf(val.ordinal())
      );
   }

   public static <T extends IForgeRegistryEntry<T>> VaultGearAttributeType<T> registryType(IForgeRegistry<T> registry) {
      return of(
         (buf, entry) -> buf.writeIdentifier(entry.getRegistryName()),
         buf -> (T)registry.getValue(buf.readIdentifier()),
         (buf, entry) -> NetcodeUtils.writeIdentifier(buf, entry.getRegistryName()),
         buf -> (T)registry.getValue(NetcodeUtils.readIdentifier(buf)),
         entry -> new JsonPrimitive(entry.getRegistryName().toString()),
         stringTag(str -> (T)registry.getValue(new ResourceLocation(str))),
         key -> StringTag.valueOf(key.toString())
      );
   }

   public static <T> VaultGearAttributeType<T> customRegistryType(Function<T, ResourceLocation> idFunction, Function<ResourceLocation, T> registryLookup) {
      return of(
         (buf, entry) -> buf.writeIdentifier(idFunction.apply(entry)),
         buf -> registryLookup.apply(buf.readIdentifier()),
         (buf, entry) -> NetcodeUtils.writeIdentifier(buf, idFunction.apply(entry)),
         buf -> registryLookup.apply(NetcodeUtils.readIdentifier(buf)),
         entry -> new JsonPrimitive(idFunction.apply(entry).toString()),
         stringTag(str -> registryLookup.apply(new ResourceLocation(str))),
         value -> StringTag.valueOf(idFunction.apply(value).toString())
      );
   }

   private static <T> Function<Tag, T> numericTag(Function<NumericTag, T> numberFn) {
      return tag -> numberFn.apply((NumericTag)tag);
   }

   private static Function<Tag, String> stringTag() {
      return stringTag(Function.identity());
   }

   private static <T> Function<Tag, T> stringTag(Function<String, T> stringFn) {
      return tag -> stringFn.apply(tag.getAsString());
   }

   private static <T extends Number> Function<Object, T> number(Function<Number, T> numberFn, Class<T> expected) {
      return object -> {
         if (object instanceof Number nbr) {
            return numberFn.apply(nbr);
         } else {
            throw new ClassCastException("%s cannot be converted to %s!".formatted(object.getClass().getName(), expected.getSimpleName()));
         }
      };
   }
}
