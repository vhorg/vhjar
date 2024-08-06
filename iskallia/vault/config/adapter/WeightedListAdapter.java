package iskallia.vault.config.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal..Gson.Preconditions;
import com.google.gson.internal..Gson.Types;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import iskallia.vault.core.util.WeightedList;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.Map.Entry;

public class WeightedListAdapter<E> extends TypeAdapter<WeightedList<E>> {
   private final TypeAdapter<E> elementTypeAdapter;
   private final ObjectConstructor<? extends WeightedList<E>> constructor;

   public WeightedListAdapter(Gson context, Type elementType, TypeAdapter<E> elementTypeAdapter, ObjectConstructor<? extends WeightedList<E>> constructor) {
      this.elementTypeAdapter = new WeightedListAdapter.TypeAdapterRuntimeTypeWrapper<>(context, elementTypeAdapter, elementType);
      this.constructor = constructor;
   }

   public void write(JsonWriter out, WeightedList<E> value) throws IOException {
      if (value == null) {
         out.nullValue();
      } else {
         out.beginArray();

         for (Entry<E, Double> e : value.entrySet()) {
            out.beginObject();
            out.name("value");
            this.elementTypeAdapter.write(out, e.getKey());
            out.name("weight");
            out.value(e.getValue());
            out.endObject();
         }

         out.endArray();
      }
   }

   public WeightedList<E> read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         WeightedList<E> collection = (WeightedList<E>)this.constructor.construct();
         if (in.peek() == JsonToken.BEGIN_ARRAY) {
            in.beginArray();

            while (in.hasNext()) {
               in.beginObject();
               E instance = null;
               double weight = 1.0;

               while (in.peek() == JsonToken.NAME) {
                  String var6 = in.nextName();
                  switch (var6) {
                     case "value":
                        instance = (E)this.elementTypeAdapter.read(in);
                        break;
                     case "weight":
                        weight = in.nextDouble();
                  }
               }

               collection.put(instance, weight);
               in.endObject();
            }

            in.endArray();
         } else if (in.peek() == JsonToken.BEGIN_OBJECT) {
            in.beginObject();

            while (in.peek() == JsonToken.NAME) {
               collection.put((E)this.elementTypeAdapter.read(in), in.nextDouble());
            }

            in.endObject();
         }

         return collection;
      }
   }

   public static class Factory implements TypeAdapterFactory {
      public static final WeightedListAdapter.Factory INSTANCE = new WeightedListAdapter.Factory();
      private final ConstructorConstructor constructorConstructor = new ConstructorConstructor(Collections.emptyMap());

      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         Type type = typeToken.getType();
         Class<? super T> rawType = typeToken.getRawType();
         if (!WeightedList.class.isAssignableFrom(rawType)) {
            return null;
         } else {
            Type elementType = this.getElementType(type, rawType);
            TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
            ObjectConstructor<T> constructor = this.constructorConstructor.get(typeToken);
            TypeAdapter<T> result = new WeightedListAdapter(gson, elementType, (TypeAdapter<E>)elementTypeAdapter, constructor);
            return result;
         }
      }

      public Type getElementType(Type context, Class<?> contextRawType) {
         Type collectionType = this.getSupertype(context, contextRawType, WeightedList.class);
         if (collectionType instanceof WildcardType) {
            collectionType = ((WildcardType)collectionType).getUpperBounds()[0];
         }

         return (Type)(collectionType instanceof ParameterizedType ? ((ParameterizedType)collectionType).getActualTypeArguments()[0] : Object.class);
      }

      public Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
         if (context instanceof WildcardType) {
            context = ((WildcardType)context).getUpperBounds()[0];
         }

         Preconditions.checkArgument(supertype.isAssignableFrom(contextRawType));
         return Types.resolve(context, contextRawType, this.getGenericSupertype(context, contextRawType, supertype));
      }

      public Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
         if (toResolve == rawType) {
            return context;
         } else {
            if (toResolve.isInterface()) {
               Class<?>[] interfaces = rawType.getInterfaces();
               int i = 0;

               for (int length = interfaces.length; i < length; i++) {
                  if (interfaces[i] == toResolve) {
                     return rawType.getGenericInterfaces()[i];
                  }

                  if (toResolve.isAssignableFrom(interfaces[i])) {
                     return this.getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
                  }
               }
            }

            if (!rawType.isInterface()) {
               while (rawType != Object.class) {
                  Class<?> rawSupertype = rawType.getSuperclass();
                  if (rawSupertype == toResolve) {
                     return rawType.getGenericSuperclass();
                  }

                  if (toResolve.isAssignableFrom(rawSupertype)) {
                     return this.getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
                  }

                  rawType = rawSupertype;
               }
            }

            return toResolve;
         }
      }
   }

   private static class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {
      private final Gson context;
      private final TypeAdapter<T> delegate;
      private final Type type;

      TypeAdapterRuntimeTypeWrapper(Gson context, TypeAdapter<T> delegate, Type type) {
         this.context = context;
         this.delegate = delegate;
         this.type = type;
      }

      public T read(JsonReader in) throws IOException {
         return (T)this.delegate.read(in);
      }

      public void write(JsonWriter out, T value) throws IOException {
         TypeAdapter chosen = this.delegate;
         Type runtimeType = this.getRuntimeTypeIfMoreSpecific(this.type, value);
         if (runtimeType != this.type) {
            TypeAdapter runtimeTypeAdapter = this.context.getAdapter(TypeToken.get(runtimeType));
            if (!(runtimeTypeAdapter instanceof Adapter)) {
               chosen = runtimeTypeAdapter;
            } else if (!(this.delegate instanceof Adapter)) {
               chosen = this.delegate;
            } else {
               chosen = runtimeTypeAdapter;
            }
         }

         chosen.write(out, value);
      }

      private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
         if (value != null && (type == Object.class || type instanceof TypeVariable || type instanceof Class)) {
            type = value.getClass();
         }

         return type;
      }
   }
}
