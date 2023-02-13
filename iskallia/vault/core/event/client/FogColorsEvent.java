package iskallia.vault.core.event.client;

import iskallia.vault.core.event.Event;

public class FogColorsEvent extends Event<FogColorsEvent, FogColorsEvent.Data> {
   public FogColorsEvent() {
   }

   protected FogColorsEvent(FogColorsEvent parent) {
      super(parent);
   }

   public FogColorsEvent createChild() {
      return new FogColorsEvent(this);
   }

   public FogColorsEvent.Data invoke(float fogRed, float fogGreen, float fogBlue) {
      return this.invoke(new FogColorsEvent.Data(fogRed, fogGreen, fogBlue));
   }

   public static class Data {
      private float fogRed;
      private float fogGreen;
      private float fogBlue;

      public Data(float fogRed, float fogGreen, float fogBlue) {
         this.fogRed = fogRed;
         this.fogGreen = fogGreen;
         this.fogBlue = fogBlue;
      }

      public float getRed() {
         return this.fogRed;
      }

      public void setRed(float fogRed) {
         this.fogRed = fogRed;
      }

      public float getGreen() {
         return this.fogGreen;
      }

      public void setGreen(float fogGreen) {
         this.fogGreen = fogGreen;
      }

      public float getBlue() {
         return this.fogBlue;
      }

      public void setBlue(float fogBlue) {
         this.fogBlue = fogBlue;
      }
   }
}
