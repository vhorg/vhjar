package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.processor.tile.VaultLootTileProcessor;

public class PlaceholderGenerationEvent extends Event<PlaceholderGenerationEvent, PlaceholderGenerationEvent.Data> {
   public PlaceholderGenerationEvent() {
   }

   protected PlaceholderGenerationEvent(PlaceholderGenerationEvent parent) {
      super(parent);
   }

   public PlaceholderGenerationEvent createChild() {
      return new PlaceholderGenerationEvent(this);
   }

   public PlaceholderGenerationEvent.Data invoke(
      VaultLootTileProcessor parent, PartialTile tile, double probability, WeightedList<PartialTile> success, WeightedList<PartialTile> failure
   ) {
      return this.invoke(new PlaceholderGenerationEvent.Data(parent, tile, probability, success, failure));
   }

   public static class Data {
      private final VaultLootTileProcessor parent;
      private final PartialTile tile;
      private double probability;
      private final WeightedList<PartialTile> success;
      private final WeightedList<PartialTile> failure;

      public Data(VaultLootTileProcessor parent, PartialTile tile, double probability, WeightedList<PartialTile> success, WeightedList<PartialTile> failure) {
         this.parent = parent;
         this.tile = tile;
         this.probability = probability;
         this.success = success;
         this.failure = failure;
      }

      public VaultLootTileProcessor getParent() {
         return this.parent;
      }

      public PartialTile getTile() {
         return this.tile;
      }

      public double getProbability() {
         return this.probability;
      }

      public WeightedList<PartialTile> getSuccess() {
         return this.success;
      }

      public WeightedList<PartialTile> getFailure() {
         return this.failure;
      }

      public void setProbability(double probability) {
         this.probability = probability;
      }
   }
}
