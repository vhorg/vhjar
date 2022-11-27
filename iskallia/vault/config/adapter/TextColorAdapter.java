package iskallia.vault.config.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.minecraft.network.chat.TextColor;

public class TextColorAdapter extends TypeAdapter<TextColor> {
   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
         return type.getRawType() == TextColor.class ? new TextColorAdapter() : null;
      }
   };

   public void write(JsonWriter out, TextColor value) throws IOException {
      if (value == null) {
         out.nullValue();
      } else {
         out.value(value.toString());
      }
   }

   public TextColor read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         return TextColor.parseColor(in.nextString());
      }
   }
}
