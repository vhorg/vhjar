package iskallia.vault.core.world.generator.theme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iskallia.vault.config.adapter.ThemeAdapter;
import java.io.FileNotFoundException;
import java.io.FileReader;

public abstract class Theme {
   private static final Gson GSON = new GsonBuilder().registerTypeAdapterFactory(ThemeAdapter.FACTORY).setPrettyPrinting().create();
   protected String path;
   protected float ambientLight;
   protected int fogColor;
   protected int grassColor;
   protected int foliageColor;
   protected int waterColor;
   protected int waterFogColor;
   protected String particle;
   protected float particleProbability;

   public Theme(
      float ambientLight, int fogColor, int grassColor, int foliageColor, int waterColor, int waterFogColor, String particle, float particleProbability
   ) {
      this.ambientLight = ambientLight;
      this.fogColor = fogColor;
      this.grassColor = grassColor;
      this.foliageColor = foliageColor;
      this.waterColor = waterColor;
      this.waterFogColor = waterFogColor;
      this.particle = particle;
      this.particleProbability = particleProbability;
   }

   public float getAmbientLight() {
      return this.ambientLight;
   }

   public int getFogColor() {
      return this.fogColor;
   }

   public int getGrassColor() {
      return this.grassColor;
   }

   public int getFoliageColor() {
      return this.foliageColor;
   }

   public int getWaterColor() {
      return this.waterColor;
   }

   public int getWaterFogColor() {
      return this.waterFogColor;
   }

   public String getParticle() {
      return this.particle;
   }

   public float getParticleProbability() {
      return this.particleProbability;
   }

   public static Theme fromPath(String path) {
      Theme theme;
      try {
         theme = (Theme)GSON.fromJson(new FileReader(path), Theme.class);
      } catch (FileNotFoundException var3) {
         return null;
      }

      theme.path = path;
      return theme;
   }

   public String getPath() {
      return this.path;
   }
}
