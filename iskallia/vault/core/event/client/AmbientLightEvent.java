package iskallia.vault.core.event.client;

import iskallia.vault.core.event.Event;
import net.minecraft.world.level.LevelReader;

public class AmbientLightEvent extends Event<AmbientLightEvent, AmbientLightEvent.Data> {
   public AmbientLightEvent() {
   }

   protected AmbientLightEvent(AmbientLightEvent parent) {
      super(parent);
   }

   public AmbientLightEvent createChild() {
      return new AmbientLightEvent(this);
   }

   public AmbientLightEvent.Data invoke(LevelReader world, int lightLevel, float brightness) {
      return this.invoke(new AmbientLightEvent.Data(world, lightLevel, brightness));
   }

   public AmbientLightEvent in(LevelReader world) {
      return this.filter(data -> data.getWorld() == world);
   }

   public static class Data {
      private final LevelReader world;
      private final int lightLevel;
      private float brightness;

      public Data(LevelReader world, int lightLevel, float brightness) {
         this.world = world;
         this.lightLevel = lightLevel;
         this.brightness = brightness;
      }

      public LevelReader getWorld() {
         return this.world;
      }

      public int getLightLevel() {
         return this.lightLevel;
      }

      public float getBrightness() {
         return this.brightness;
      }

      public void setBrightness(float brightness) {
         this.brightness = brightness;
      }
   }
}
