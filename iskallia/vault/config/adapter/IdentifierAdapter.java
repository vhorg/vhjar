package iskallia.vault.config.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.minecraft.resources.ResourceLocation;

public class IdentifierAdapter extends TypeAdapter<ResourceLocation> {
   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         return typeToken.getRawType() == ResourceLocation.class ? new IdentifierAdapter() : null;
      }
   };

   public void write(JsonWriter out, ResourceLocation value) throws IOException {
      if (value == null) {
         out.nullValue();
      } else {
         out.value(value.toString());
      }
   }

   public ResourceLocation read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         return new ResourceLocation(in.nextString());
      }
   }
}
