package iskallia.vault.config.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import iskallia.vault.core.world.data.tile.PartialTile;
import java.io.IOException;

public class PartialTileAdapter extends TypeAdapter<PartialTile> {
   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         return typeToken.getRawType() == PartialTile.class ? new PartialTileAdapter() : null;
      }
   };

   public void write(JsonWriter out, PartialTile value) throws IOException {
      if (value == null) {
         out.nullValue();
      } else {
         out.value(value.toString());
      }
   }

   public PartialTile read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         return PartialTile.parse(in.nextString(), true).orElse(PartialTile.ERROR);
      }
   }
}
