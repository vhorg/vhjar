package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ScavengerAltarConsumeEvent extends Event<ScavengerAltarConsumeEvent, ScavengerAltarConsumeEvent.Data> {
   public ScavengerAltarConsumeEvent() {
   }

   protected ScavengerAltarConsumeEvent(ScavengerAltarConsumeEvent parent) {
      super(parent);
   }

   public ScavengerAltarConsumeEvent createChild() {
      return new ScavengerAltarConsumeEvent(this);
   }

   public ScavengerAltarConsumeEvent.Data invoke(Level level, BlockEntity tile) {
      return this.invoke(new ScavengerAltarConsumeEvent.Data(level, tile));
   }

   public static class Data {
      private final Level level;
      private final BlockEntity tile;

      public Data(Level level, BlockEntity tile) {
         this.level = level;
         this.tile = tile;
      }

      public Level getLevel() {
         return this.level;
      }

      public BlockEntity getTile() {
         return this.tile;
      }
   }
}
