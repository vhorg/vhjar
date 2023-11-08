package iskallia.vault.core.event.client;

import iskallia.vault.core.event.Event;
import net.minecraft.resources.ResourceLocation;

public class DimensionEffectLocationEvent extends Event<DimensionEffectLocationEvent, DimensionEffectLocationEvent.Data> {
   public DimensionEffectLocationEvent() {
   }

   protected DimensionEffectLocationEvent(DimensionEffectLocationEvent parent) {
      super(parent);
   }

   public DimensionEffectLocationEvent createChild() {
      return new DimensionEffectLocationEvent(this);
   }

   public DimensionEffectLocationEvent.Data invoke(ResourceLocation id) {
      return this.invoke(new DimensionEffectLocationEvent.Data(id));
   }

   public static class Data {
      private ResourceLocation id;

      public Data(ResourceLocation id) {
         this.id = id;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public void setId(ResourceLocation id) {
         this.id = id;
      }
   }
}
