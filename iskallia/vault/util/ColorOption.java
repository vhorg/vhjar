package iskallia.vault.util;

import java.awt.Color;

public class ColorOption {
   ColorOption.HunterSpec hunterSpec;
   float red;
   float green;
   float blue;

   public ColorOption(ColorOption.HunterSpec objective, float red, float green, float blue) {
      this.hunterSpec = objective;
      this.red = red;
      this.green = green;
      this.blue = blue;
   }

   public ColorOption(String hunterSpec, float red, float green, float blue) {
      this(ColorOption.HunterSpec.valueOf(hunterSpec), red, green, blue);
   }

   public Color getColor() {
      return new Color(this.red, this.green, this.blue);
   }

   public ColorOption.HunterSpec getHunterSpec() {
      return this.hunterSpec;
   }

   public void setHunterSpec(ColorOption.HunterSpec hunterSpec) {
      this.hunterSpec = hunterSpec;
   }

   public float getRed() {
      return this.red;
   }

   public void setRed(float red) {
      this.red = red;
   }

   public float getGreen() {
      return this.green;
   }

   public void setGreen(float green) {
      this.green = green;
   }

   public float getBlue() {
      return this.blue;
   }

   public void setBlue(float blue) {
      this.blue = blue;
   }

   public static enum HunterSpec {
      CHEST,
      BLOCK,
      WOODEN,
      GILDED,
      LIVING,
      ORNATE,
      COINS;

      @Override
      public String toString() {
         return "hunter_" + this.name().toLowerCase();
      }
   }
}
