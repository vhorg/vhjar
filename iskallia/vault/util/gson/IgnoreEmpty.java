package iskallia.vault.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public class IgnoreEmpty {
   public static class DoubleAdapter extends TypeAdapter<Double> {
      public void write(JsonWriter out, Double value) throws IOException {
         if (value != null && value != 0.0) {
            out.value(value);
         } else {
            out.nullValue();
         }
      }

      public Double read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0.0;
         } else {
            return in.nextDouble();
         }
      }
   }

   public static class IntegerAdapter extends TypeAdapter<Integer> {
      public void write(JsonWriter out, Integer value) throws IOException {
         if (value != null && value != 0) {
            out.value(value);
         } else {
            out.nullValue();
         }
      }

      public Integer read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0;
         } else {
            return in.nextInt();
         }
      }
   }

   public static class StringAdapter extends TypeAdapter<String> {
      public void write(JsonWriter out, String value) throws IOException {
         if (value != null && !value.isEmpty()) {
            out.value(value);
         } else {
            out.nullValue();
         }
      }

      public String read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return "";
         } else {
            return in.nextString();
         }
      }
   }
}
