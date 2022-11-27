package iskallia.vault.util.nbt;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.apache.commons.lang3.ArrayUtils;

public class NBTSerializer {
   public static final <T extends INBTSerializable> CompoundTag serialize(T object) throws IllegalAccessException, UnserializableClassException {
      CompoundTag t = new CompoundTag();
      Class<?> definition = object.getClass();
      Field[] df = definition.getDeclaredFields();

      for (Field f : df) {
         if (f.isAnnotationPresent(NBTSerialize.class)) {
            f.setAccessible(true);
            Object fv = f.get(object);
            if (fv != null) {
               String tn = f.getAnnotation(NBTSerialize.class).name();
               if (tn.equals("")) {
                  tn = f.getName();
               }

               Class fc = f.getType();
               if (fc.isAssignableFrom(byte.class)) {
                  t.putByte(tn, (Byte)fv);
               } else if (fc.isAssignableFrom(boolean.class)) {
                  t.putBoolean(tn, (Boolean)fv);
               } else if (fc.isAssignableFrom(short.class)) {
                  t.putShort(tn, (Short)fv);
               } else if (fc.isAssignableFrom(int.class)) {
                  t.putInt(tn, (Integer)fv);
               } else if (fc.isAssignableFrom(long.class)) {
                  t.putLong(tn, (Long)fv);
               } else if (fc.isAssignableFrom(float.class)) {
                  t.putFloat(tn, (Float)fv);
               } else if (fc.isAssignableFrom(double.class)) {
                  t.putDouble(tn, (Double)fv);
               } else {
                  t.put(tn, objectToTag(fc, fv));
               }
            }
         }
      }

      return t;
   }

   private static final <T, U extends T> Tag objectToTag(Class<T> clazz, U obj) throws IllegalAccessException, UnserializableClassException {
      if (obj == null) {
         return null;
      } else if (clazz.isAssignableFrom(Byte.class)) {
         return ByteTag.valueOf((Byte)obj);
      } else if (clazz.isAssignableFrom(Boolean.class)) {
         return ByteTag.valueOf((byte)((Boolean)obj ? 1 : 0));
      } else if (clazz.isAssignableFrom(Short.class)) {
         return ShortTag.valueOf((Short)obj);
      } else if (clazz.isAssignableFrom(Integer.class)) {
         return IntTag.valueOf((Integer)obj);
      } else if (clazz.isAssignableFrom(Long.class)) {
         return LongTag.valueOf((Long)obj);
      } else if (clazz.isAssignableFrom(Float.class)) {
         return FloatTag.valueOf((Float)obj);
      } else if (clazz.isAssignableFrom(Double.class)) {
         return DoubleTag.valueOf((Double)obj);
      } else if (clazz.isAssignableFrom(byte[].class)) {
         return new ByteArrayTag((byte[])obj);
      } else if (clazz.isAssignableFrom(Byte[].class)) {
         return new ByteArrayTag(ArrayUtils.toPrimitive((Byte[])obj));
      } else if (clazz.isAssignableFrom(String.class)) {
         return StringTag.valueOf((String)obj);
      } else if (clazz.isAssignableFrom(int[].class)) {
         return new IntArrayTag((int[])obj);
      } else if (clazz.isAssignableFrom(Integer[].class)) {
         return new IntArrayTag(ArrayUtils.toPrimitive((Integer[])obj));
      } else if (INBTSerializable.class.isAssignableFrom(clazz)) {
         return serialize((U)((INBTSerializable)obj));
      } else if (Collection.class.isAssignableFrom(clazz)) {
         return serializeCollection((Collection<T>)obj);
      } else if (Entry.class.isAssignableFrom(clazz)) {
         return serializeEntry((Entry)obj);
      } else if (Map.class.isAssignableFrom(clazz)) {
         return serializeCollection(((Map)obj).entrySet());
      } else {
         throw new UnserializableClassException(clazz);
      }
   }

   private static final <T> ListTag serializeCollection(Collection<T> col) throws IllegalAccessException, UnserializableClassException {
      ListTag c = new ListTag();
      if (col.size() <= 0) {
         return c;
      } else {
         Class<T> subclass = (Class<T>)col.iterator().next().getClass();

         for (T element : col) {
            Tag tag = objectToTag(subclass, element);
            if (tag != null) {
               c.add(tag);
            }
         }

         return c;
      }
   }

   private static final <K, V> CompoundTag serializeEntry(Entry<K, V> entry) throws UnserializableClassException, IllegalAccessException {
      Class<K> keyClass = (Class<K>)entry.getKey().getClass();
      Class<V> valueClass = (Class<V>)entry.getValue().getClass();
      return serializeEntry(entry, keyClass, valueClass);
   }

   private static final <K, V> CompoundTag serializeEntry(Entry<? extends K, ? extends V> entry, Class<K> keyClass, Class<V> valueClass) throws IllegalAccessException, UnserializableClassException {
      CompoundTag te = new CompoundTag();
      if (entry.getKey() != null) {
         Tag keyTag = objectToTag(keyClass, entry.getKey());
         te.put("k", keyTag);
      }

      if (entry.getValue() != null) {
         Tag valueTag = objectToTag(valueClass, entry.getValue());
         te.put("v", valueTag);
      }

      return te;
   }

   public static final <T extends INBTSerializable> T deserialize(Class<T> definition, CompoundTag data) throws IllegalAccessException, InstantiationException, UnserializableClassException {
      T instance = (T)definition.newInstance();
      deserialize(instance, data, true);
      return instance;
   }

   public static final <T extends INBTSerializable> void deserialize(T instance, CompoundTag data, boolean interpretMissingFieldValuesAsNull) throws IllegalAccessException, InstantiationException, UnserializableClassException {
      Field[] df = instance.getClass().getDeclaredFields();

      for (Field f : df) {
         if (f.isAnnotationPresent(NBTSerialize.class)) {
            String tn = f.getAnnotation(NBTSerialize.class).name();
            if (tn.equals("")) {
               tn = f.getName();
            }

            if (!data.contains(tn)) {
               if (interpretMissingFieldValuesAsNull) {
                  f.setAccessible(true);
                  if (f.getType().equals(boolean.class)) {
                     f.set(instance, false);
                  } else if (f.getType().equals(int.class)) {
                     f.set(instance, 0);
                  } else {
                     f.set(instance, null);
                  }
               }
            } else {
               f.setAccessible(true);
               Class<?> forceInstantiateAs = f.getAnnotation(NBTSerialize.class).typeOverride();
               Class<?> fc;
               if (forceInstantiateAs.isAssignableFrom(Object.class)) {
                  fc = f.getType();
               } else {
                  fc = forceInstantiateAs;
               }

               if (fc.isAssignableFrom(byte.class)) {
                  f.setByte(instance, data.getByte(tn));
               } else if (fc.isAssignableFrom(boolean.class)) {
                  f.setBoolean(instance, data.getBoolean(tn));
               } else if (fc.isAssignableFrom(short.class)) {
                  f.setShort(instance, data.getShort(tn));
               } else if (fc.isAssignableFrom(int.class)) {
                  f.setInt(instance, data.getInt(tn));
               } else if (fc.isAssignableFrom(long.class)) {
                  f.setLong(instance, data.getLong(tn));
               } else if (fc.isAssignableFrom(float.class)) {
                  f.setFloat(instance, data.getFloat(tn));
               } else if (fc.isAssignableFrom(double.class)) {
                  f.setDouble(instance, data.getDouble(tn));
               } else {
                  f.set(instance, tagToObject(data.get(tn), fc, f.getGenericType()));
               }
            }
         }
      }
   }

   private static <T> Collection<T> deserializeCollection(ListTag list, Class<? extends Collection> colClass, Class<T> subclass, Type subtype) throws InstantiationException, IllegalAccessException, UnserializableClassException {
      Collection<T> c = (Collection<T>)colClass.newInstance();

      for (int i = 0; i < list.size(); i++) {
         c.add(tagToObject(list.get(i), subclass, subtype));
      }

      return c;
   }

   private static <K, V> Map<K, V> deserializeMap(
      ListTag map, Class<? extends Map> mapClass, Class<K> keyClass, Type keyType, Class<V> valueClass, Type valueType
   ) throws InstantiationException, IllegalAccessException, UnserializableClassException {
      Map<K, V> e = (Map<K, V>)mapClass.newInstance();

      for (int i = 0; i < map.size(); i++) {
         CompoundTag kvp = (CompoundTag)map.get(i);
         K key;
         if (kvp.contains("k")) {
            key = tagToObject(kvp.get("k"), keyClass, keyType);
         } else {
            key = null;
         }

         V value;
         if (kvp.contains("v")) {
            value = tagToObject(kvp.get("v"), valueClass, valueType);
         } else {
            value = null;
         }

         e.put(key, value);
      }

      return e;
   }

   private static <T> T tagToObject(Tag tag, Class<T> clazz, Type subtype) throws IllegalAccessException, InstantiationException, UnserializableClassException {
      if (clazz.isAssignableFrom(Object.class)
         || clazz.isAssignableFrom(Number.class)
         || clazz.isAssignableFrom(CharSequence.class)
         || clazz.isAssignableFrom(Serializable.class)
         || clazz.isAssignableFrom(Comparable.class)) {
         throw new UnserializableClassException(clazz);
      } else if (clazz.isAssignableFrom(Byte.class)) {
         return (T)((ByteTag)tag).getAsByte();
      } else if (clazz.isAssignableFrom(Boolean.class)) {
         return (T)((ByteTag)tag).getAsByte() != 0;
      } else if (clazz.isAssignableFrom(Short.class)) {
         return (T)((ShortTag)tag).getAsShort();
      } else if (clazz.isAssignableFrom(Integer.class)) {
         return (T)((IntTag)tag).getAsInt();
      } else if (clazz.isAssignableFrom(Long.class)) {
         return (T)((LongTag)tag).getAsLong();
      } else if (clazz.isAssignableFrom(Float.class)) {
         return (T)((FloatTag)tag).getAsFloat();
      } else if (clazz.isAssignableFrom(Double.class)) {
         return (T)((DoubleTag)tag).getAsDouble();
      } else if (clazz.isAssignableFrom(byte[].class)) {
         return (T)((ByteArrayTag)tag).getAsByteArray();
      } else if (clazz.isAssignableFrom(Byte[].class)) {
         return (T)ArrayUtils.toObject(((ByteArrayTag)tag).getAsByteArray());
      } else if (clazz.isAssignableFrom(String.class)) {
         return (T)((StringTag)tag).getAsString();
      } else if (clazz.isAssignableFrom(int[].class)) {
         return (T)((IntArrayTag)tag).getAsIntArray();
      } else if (clazz.isAssignableFrom(Integer[].class)) {
         return (T)ArrayUtils.toObject(((IntArrayTag)tag).getAsIntArray());
      } else if (INBTSerializable.class.isAssignableFrom(clazz)) {
         CompoundTag ntc = (CompoundTag)tag;
         return deserialize(clazz, ntc);
      } else if (Collection.class.isAssignableFrom(clazz)) {
         Type listType = ((ParameterizedType)subtype).getActualTypeArguments()[0];
         Class<?> lct;
         if (listType instanceof ParameterizedType) {
            lct = (Class<?>)((ParameterizedType)listType).getRawType();
         } else {
            lct = (Class<?>)listType;
         }

         ListTag ntl = (ListTag)tag;
         return (T)deserializeCollection(ntl, clazz, lct, listType);
      } else if (Map.class.isAssignableFrom(clazz)) {
         Type[] types = ((ParameterizedType)subtype).getActualTypeArguments();
         Type keyType = types[0];
         Type valueType = types[1];
         Class<?> keyClass;
         if (keyType instanceof ParameterizedType) {
            keyClass = (Class<?>)((ParameterizedType)keyType).getRawType();
         } else {
            keyClass = (Class<?>)keyType;
         }

         Class<?> valueClass;
         if (valueType instanceof ParameterizedType) {
            valueClass = (Class<?>)((ParameterizedType)valueType).getRawType();
         } else {
            valueClass = (Class<?>)valueType;
         }

         ListTag ntl = (ListTag)tag;
         return (T)deserializeMap(ntl, clazz, keyClass, keyType, valueClass, valueType);
      } else {
         throw new UnserializableClassException(clazz);
      }
   }

   private static int getIDFromClass(Class<?> clazz) {
      if (clazz.isAssignableFrom(byte.class)
         || clazz.isAssignableFrom(Byte.class)
         || clazz.isAssignableFrom(boolean.class)
         || clazz.isAssignableFrom(Boolean.class)) {
         return 1;
      } else if (clazz.isAssignableFrom(short.class) || clazz.isAssignableFrom(Short.class)) {
         return 2;
      } else if (clazz.isAssignableFrom(int.class) || clazz.isAssignableFrom(Integer.class)) {
         return 3;
      } else if (clazz.isAssignableFrom(long.class) || clazz.isAssignableFrom(Long.class)) {
         return 4;
      } else if (clazz.isAssignableFrom(float.class) || clazz.isAssignableFrom(Float.class)) {
         return 5;
      } else if (clazz.isAssignableFrom(double.class) || clazz.isAssignableFrom(Double.class)) {
         return 6;
      } else if (clazz.isAssignableFrom(byte[].class) || clazz.isAssignableFrom(Byte[].class)) {
         return 7;
      } else if (clazz.isAssignableFrom(String.class)) {
         return 8;
      } else if (clazz.isAssignableFrom(int[].class) || clazz.isAssignableFrom(Integer[].class)) {
         return 11;
      } else if (INBTSerializable.class.isAssignableFrom(clazz)) {
         return 10;
      } else {
         return !Collection.class.isAssignableFrom(clazz) && !Map.class.isAssignableFrom(clazz) ? 10 : 9;
      }
   }
}
