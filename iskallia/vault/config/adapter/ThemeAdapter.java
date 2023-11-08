package iskallia.vault.config.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.generator.theme.ClassicVaultTheme;
import iskallia.vault.core.world.generator.theme.DIYVaultTheme;
import iskallia.vault.core.world.generator.theme.Theme;
import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

public class ThemeAdapter extends TypeAdapter<Theme> {
   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         return typeToken.getRawType() == Theme.class ? new ThemeAdapter() : null;
      }
   };

   public void write(JsonWriter out, Theme theme) throws IOException {
      if (theme == null) {
         out.nullValue();
      } else {
         out.beginObject();
         out.name("type");
         if (theme instanceof ClassicVaultTheme) {
            out.value("classic_vault");
            out.name("starts");
            out.value(((ClassicVaultTheme)theme).getStarts().getId().toString());
            out.name("rooms");
            out.value(((ClassicVaultTheme)theme).getRooms().getId().toString());
            out.name("tunnels");
            out.value(((ClassicVaultTheme)theme).getTunnels().getId().toString());
         }

         out.endObject();
      }
   }

   public Theme read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         in.beginObject();
         in.nextName();
         String type = in.nextString();
         if ("classic_vault".equals(type)) {
            TemplatePoolKey starts = null;
            TemplatePoolKey rooms = null;
            TemplatePoolKey tunnels = null;
            float ambientLight = 0.0F;
            int fogColor = 0;
            int grassColor = 0;
            int foliageColor = 0;
            int waterColor = 0;
            int waterFogColor = 0;
            String particle = null;
            float particleProbability = 0.0F;
            int time = 15000;
            ResourceLocation effects = DimensionType.NETHER_EFFECTS;

            while (in.peek() == JsonToken.NAME) {
               String name = in.nextName();
               switch (name) {
                  case "starts":
                     starts = VaultRegistry.TEMPLATE_POOL.getKey(new ResourceLocation(in.nextString()));
                     break;
                  case "rooms":
                     rooms = VaultRegistry.TEMPLATE_POOL.getKey(new ResourceLocation(in.nextString()));
                     break;
                  case "tunnels":
                     tunnels = VaultRegistry.TEMPLATE_POOL.getKey(new ResourceLocation(in.nextString()));
                     break;
                  case "ambient_light":
                     ambientLight = (float)in.nextDouble();
                     break;
                  case "fog_color":
                     fogColor = in.nextInt();
                     break;
                  case "grass_color":
                     grassColor = in.nextInt();
                     break;
                  case "foliage_color":
                     foliageColor = in.nextInt();
                     break;
                  case "water_color":
                     waterColor = in.nextInt();
                     break;
                  case "water_fog_color":
                     waterFogColor = in.nextInt();
                     break;
                  case "particle":
                     particle = in.nextString();
                     break;
                  case "particle_probability":
                     particleProbability = (float)in.nextDouble();
                     break;
                  case "time":
                     time = in.nextInt();
                     break;
                  case "effects":
                     effects = new ResourceLocation(in.nextString());
               }
            }

            in.endObject();
            return new ClassicVaultTheme(
               starts,
               rooms,
               tunnels,
               ambientLight,
               fogColor,
               grassColor,
               foliageColor,
               waterColor,
               waterFogColor,
               particle,
               particleProbability,
               time,
               effects
            );
         } else if ("diy_vault".equals(type)) {
            TemplatePoolKey starts = null;
            TemplatePoolKey commonRooms = null;
            TemplatePoolKey challengeRooms = null;
            TemplatePoolKey omegaRooms = null;
            TemplatePoolKey tunnels = null;
            float ambientLight = 0.0F;
            int fogColor = 0;
            int grassColor = 0;
            int foliageColor = 0;
            int waterColor = 0;
            int waterFogColor = 0;
            String particle = null;
            float particleProbability = 0.0F;

            while (in.peek() == JsonToken.NAME) {
               String name = in.nextName();
               switch (name) {
                  case "starts":
                     starts = VaultRegistry.TEMPLATE_POOL.getKey(new ResourceLocation(in.nextString()));
                     break;
                  case "common_rooms":
                     commonRooms = VaultRegistry.TEMPLATE_POOL.getKey(new ResourceLocation(in.nextString()));
                     break;
                  case "challenge_rooms":
                     challengeRooms = VaultRegistry.TEMPLATE_POOL.getKey(new ResourceLocation(in.nextString()));
                     break;
                  case "omega_rooms":
                     omegaRooms = VaultRegistry.TEMPLATE_POOL.getKey(new ResourceLocation(in.nextString()));
                     break;
                  case "tunnels":
                     tunnels = VaultRegistry.TEMPLATE_POOL.getKey(new ResourceLocation(in.nextString()));
                     break;
                  case "ambient_light":
                     ambientLight = (float)in.nextDouble();
                     break;
                  case "fog_color":
                     fogColor = in.nextInt();
                     break;
                  case "grass_color":
                     grassColor = in.nextInt();
                     break;
                  case "foliage_color":
                     foliageColor = in.nextInt();
                     break;
                  case "water_color":
                     waterColor = in.nextInt();
                     break;
                  case "water_fog_color":
                     waterFogColor = in.nextInt();
                     break;
                  case "particle":
                     particle = in.nextString();
                     break;
                  case "particle_probability":
                     particleProbability = (float)in.nextDouble();
               }
            }

            in.endObject();
            return new DIYVaultTheme(
               starts,
               commonRooms,
               challengeRooms,
               omegaRooms,
               tunnels,
               ambientLight,
               fogColor,
               grassColor,
               foliageColor,
               waterColor,
               waterFogColor,
               particle,
               particleProbability
            );
         } else {
            return null;
         }
      }
   }
}
