package iskallia.vault.core.event.client;

import iskallia.vault.core.event.Event;
import net.minecraft.world.level.biome.Biome;

public class BiomeColorsEvent extends Event<BiomeColorsEvent, BiomeColorsEvent.Data> {
   public BiomeColorsEvent() {
   }

   protected BiomeColorsEvent(BiomeColorsEvent parent) {
      super(parent);
   }

   public BiomeColorsEvent createChild() {
      return new BiomeColorsEvent(this);
   }

   public BiomeColorsEvent.Data invoke(Biome biome, double posX, double posZ, int color, BiomeColorsEvent.Type type) {
      return this.invoke(new BiomeColorsEvent.Data(biome, posX, posZ, color, type));
   }

   public static class Data {
      private final Biome biome;
      private final double posX;
      private final double posZ;
      private int color;
      private final BiomeColorsEvent.Type type;

      public Data(Biome biome, double posX, double posZ, int color, BiomeColorsEvent.Type type) {
         this.biome = biome;
         this.posX = posX;
         this.posZ = posZ;
         this.color = color;
         this.type = type;
      }

      public Biome getBiome() {
         return this.biome;
      }

      public double getPosX() {
         return this.posX;
      }

      public double getPosZ() {
         return this.posZ;
      }

      public int getColor() {
         return this.color;
      }

      public BiomeColorsEvent.Type getType() {
         return this.type;
      }

      public void setColor(int color) {
         this.color = color;
      }
   }

   public static enum Type {
      FOG,
      GRASS,
      FOLIAGE,
      WATER,
      WATER_FOG;
   }
}
