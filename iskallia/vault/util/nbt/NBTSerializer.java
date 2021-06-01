package iskallia.vault.util.nbt;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;
import org.apache.commons.lang3.ArrayUtils;

public class NBTSerializer {
   public static final <T extends INBTSerializable> CompoundNBT serialize(T object) throws IllegalAccessException, UnserializableClassException {
      CompoundNBT t = new CompoundNBT();
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
                  t.func_74774_a(tn, (Byte)fv);
               } else if (fc.isAssignableFrom(boolean.class)) {
                  t.func_74757_a(tn, (Boolean)fv);
               } else if (fc.isAssignableFrom(short.class)) {
                  t.func_74777_a(tn, (Short)fv);
               } else if (fc.isAssignableFrom(int.class)) {
                  t.func_74768_a(tn, (Integer)fv);
               } else if (fc.isAssignableFrom(long.class)) {
                  t.func_74772_a(tn, (Long)fv);
               } else if (fc.isAssignableFrom(float.class)) {
                  t.func_74776_a(tn, (Float)fv);
               } else if (fc.isAssignableFrom(double.class)) {
                  t.func_74780_a(tn, (Double)fv);
               } else {
                  t.func_218657_a(tn, objectToTag(fc, fv));
               }
            }
         }
      }

      return t;
   }

   private static final <T, U extends T> INBT objectToTag(Class<T> clazz, U obj) throws IllegalAccessException, UnserializableClassException {
      if (obj == null) {
         return null;
      } else if (clazz.isAssignableFrom(Byte.class)) {
         return ByteNBT.func_229671_a_((Byte)obj);
      } else if (clazz.isAssignableFrom(Boolean.class)) {
         return ByteNBT.func_229671_a_((byte)((Boolean)obj ? 1 : 0));
      } else if (clazz.isAssignableFrom(Short.class)) {
         return ShortNBT.func_229701_a_((Short)obj);
      } else if (clazz.isAssignableFrom(Integer.class)) {
         return IntNBT.func_229692_a_((Integer)obj);
      } else if (clazz.isAssignableFrom(Long.class)) {
         return LongNBT.func_229698_a_((Long)obj);
      } else if (clazz.isAssignableFrom(Float.class)) {
         return FloatNBT.func_229689_a_((Float)obj);
      } else if (clazz.isAssignableFrom(Double.class)) {
         return DoubleNBT.func_229684_a_((Double)obj);
      } else if (clazz.isAssignableFrom(byte[].class)) {
         return new ByteArrayNBT((byte[])obj);
      } else if (clazz.isAssignableFrom(Byte[].class)) {
         return new ByteArrayNBT(ArrayUtils.toPrimitive((Byte[])obj));
      } else if (clazz.isAssignableFrom(String.class)) {
         return StringNBT.func_229705_a_((String)obj);
      } else if (clazz.isAssignableFrom(int[].class)) {
         return new IntArrayNBT((int[])obj);
      } else if (clazz.isAssignableFrom(Integer[].class)) {
         return new IntArrayNBT(ArrayUtils.toPrimitive((Integer[])obj));
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

   private static final <T> ListNBT serializeCollection(Collection<T> col) throws IllegalAccessException, UnserializableClassException {
      ListNBT c = new ListNBT();
      if (col.size() <= 0) {
         return c;
      } else {
         Class<T> subclass = (Class<T>)col.iterator().next().getClass();

         for (T element : col) {
            INBT tag = objectToTag(subclass, element);
            if (tag != null) {
               c.add(tag);
            }
         }

         return c;
      }
   }

   private static final <K, V> CompoundNBT serializeEntry(Entry<K, V> entry) throws UnserializableClassException, IllegalAccessException {
      Class<K> keyClass = (Class<K>)entry.getKey().getClass();
      Class<V> valueClass = (Class<V>)entry.getValue().getClass();
      return serializeEntry(entry, keyClass, valueClass);
   }

   private static final <K, V> CompoundNBT serializeEntry(Entry<? extends K, ? extends V> entry, Class<K> keyClass, Class<V> valueClass) throws IllegalAccessException, UnserializableClassException {
      CompoundNBT te = new CompoundNBT();
      if (entry.getKey() != null) {
         INBT keyTag = objectToTag(keyClass, entry.getKey());
         te.func_218657_a("k", keyTag);
      }

      if (entry.getValue() != null) {
         INBT valueTag = objectToTag(valueClass, entry.getValue());
         te.func_218657_a("v", valueTag);
      }

      return te;
   }

   public static final <T extends INBTSerializable> T deserialize(Class<T> definition, CompoundNBT data) throws IllegalAccessException, InstantiationException, UnserializableClassException {
      T instance = (T)definition.newInstance();
      deserialize(instance, data, true);
      return instance;
   }

   public static final <T extends INBTSerializable> void deserialize(T instance, CompoundNBT data, boolean interpretMissingFieldValuesAsNull) throws IllegalAccessException, InstantiationException, UnserializableClassException {
      Field[] df = instance.getClass().getDeclaredFields();

      for (Field f : df) {
         if (f.isAnnotationPresent(NBTSerialize.class)) {
            String tn = f.getAnnotation(NBTSerialize.class).name();
            if (tn.equals("")) {
               tn = f.getName();
            }

            if (!data.func_74764_b(tn)) {
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
                  f.setByte(instance, data.func_74771_c(tn));
               } else if (fc.isAssignableFrom(boolean.class)) {
                  f.setBoolean(instance, data.func_74767_n(tn));
               } else if (fc.isAssignableFrom(short.class)) {
                  f.setShort(instance, data.func_74765_d(tn));
               } else if (fc.isAssignableFrom(int.class)) {
                  f.setInt(instance, data.func_74762_e(tn));
               } else if (fc.isAssignableFrom(long.class)) {
                  f.setLong(instance, data.func_74763_f(tn));
               } else if (fc.isAssignableFrom(float.class)) {
                  f.setFloat(instance, data.func_74760_g(tn));
               } else if (fc.isAssignableFrom(double.class)) {
                  f.setDouble(instance, data.func_74769_h(tn));
               } else {
                  f.set(instance, tagToObject(data.func_74781_a(tn), fc, f.getGenericType()));
               }
            }
         }
      }
   }

   private static <T> Collection<T> deserializeCollection(ListNBT list, Class<? extends Collection> colClass, Class<T> subclass, Type subtype) throws InstantiationException, IllegalAccessException, UnserializableClassException {
      Collection<T> c = (Collection<T>)colClass.newInstance();

      for (int i = 0; i < list.size(); i++) {
         c.add(tagToObject(list.get(i), subclass, subtype));
      }

      return c;
   }

   private static <K, V> Map<K, V> deserializeMap(
      ListNBT map, Class<? extends Map> mapClass, Class<K> keyClass, Type keyType, Class<V> valueClass, Type valueType
   ) throws InstantiationException, IllegalAccessException, UnserializableClassException {
      Map<K, V> e = (Map<K, V>)mapClass.newInstance();

      for (int i = 0; i < map.size(); i++) {
         CompoundNBT kvp = (CompoundNBT)map.get(i);
         K key;
         if (kvp.func_74764_b("k")) {
            key = tagToObject(kvp.func_74781_a("k"), keyClass, keyType);
         } else {
            key = null;
         }

         V value;
         if (kvp.func_74764_b("v")) {
            value = tagToObject(kvp.func_74781_a("v"), valueClass, valueType);
         } else {
            value = null;
         }

         e.put(key, value);
      }

      return e;
   }

   private static <T> T tagToObject(INBT tag, Class<T> clazz, Type subtype) throws IllegalAccessException, InstantiationException, UnserializableClassException {
      if (clazz.isAssignableFrom(Object.class)
         || clazz.isAssignableFrom(Number.class)
         || clazz.isAssignableFrom(CharSequence.class)
         || clazz.isAssignableFrom(Serializable.class)
         || clazz.isAssignableFrom(Comparable.class)) {
         throw new UnserializableClassException(clazz);
      } else if (clazz.isAssignableFrom(Byte.class)) {
         return (T)((ByteNBT)tag).func_150290_f();
      } else if (clazz.isAssignableFrom(Boolean.class)) {
         return (T)((ByteNBT)tag).func_150290_f() != 0;
      } else if (clazz.isAssignableFrom(Short.class)) {
         return (T)((ShortNBT)tag).func_150289_e();
      } else if (clazz.isAssignableFrom(Integer.class)) {
         return (T)((IntNBT)tag).func_150287_d();
      } else if (clazz.isAssignableFrom(Long.class)) {
         return (T)((LongNBT)tag).func_150291_c();
      } else if (clazz.isAssignableFrom(Float.class)) {
         return (T)((FloatNBT)tag).func_150288_h();
      } else if (clazz.isAssignableFrom(Double.class)) {
         return (T)((DoubleNBT)tag).func_150286_g();
      } else if (clazz.isAssignableFrom(byte[].class)) {
         return (T)((ByteArrayNBT)tag).func_150292_c();
      } else if (clazz.isAssignableFrom(Byte[].class)) {
         return (T)ArrayUtils.toObject(((ByteArrayNBT)tag).func_150292_c());
      } else if (clazz.isAssignableFrom(String.class)) {
         return (T)((StringNBT)tag).func_150285_a_();
      } else if (clazz.isAssignableFrom(int[].class)) {
         return (T)((IntArrayNBT)tag).func_150302_c();
      } else if (clazz.isAssignableFrom(Integer[].class)) {
         return (T)ArrayUtils.toObject(((IntArrayNBT)tag).func_150302_c());
      } else if (INBTSerializable.class.isAssignableFrom(clazz)) {
         CompoundNBT ntc = (CompoundNBT)tag;
         return deserialize(clazz, ntc);
      } else if (Collection.class.isAssignableFrom(clazz)) {
         Type listType = ((ParameterizedType)subtype).getActualTypeArguments()[0];
         Class<?> lct;
         if (listType instanceof ParameterizedType) {
            lct = (Class<?>)((ParameterizedType)listType).getRawType();
         } else {
            lct = (Class<?>)listType;
         }

         ListNBT ntl = (ListNBT)tag;
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

         ListNBT ntl = (ListNBT)tag;
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
