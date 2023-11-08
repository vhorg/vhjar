package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;

public class PlaceholderProcessingEvent extends Event<PlaceholderProcessingEvent, PlaceholderProcessingEvent.Data> {
   public PlaceholderProcessingEvent() {
   }

   protected PlaceholderProcessingEvent(PlaceholderProcessingEvent parent) {
      super(parent);
   }

   public PlaceholderProcessingEvent createChild() {
      return new PlaceholderProcessingEvent(this);
   }

   public PlaceholderProcessingEvent.Data invoke(PartialTile tile, ProcessorContext context) {
      return this.invoke(new PlaceholderProcessingEvent.Data(tile, context));
   }

   public static class Data {
      private final PartialTile tile;
      private final ProcessorContext context;

      public Data(PartialTile tile, ProcessorContext context) {
         this.tile = tile;
         this.context = context;
      }

      public PartialTile getTile() {
         return this.tile;
      }

      public ProcessorContext getContext() {
         return this.context;
      }
   }
}
