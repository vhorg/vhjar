package iskallia.vault.core.event.common;

import iskallia.vault.block.entity.ScavengerAltarTileEntity;
import iskallia.vault.core.event.Event;
import net.minecraft.world.level.Level;

public class ScavengerAltarConsumeEvent extends Event<ScavengerAltarConsumeEvent, ScavengerAltarConsumeEvent.Data> {
   public ScavengerAltarConsumeEvent() {
   }

   protected ScavengerAltarConsumeEvent(ScavengerAltarConsumeEvent parent) {
      super(parent);
   }

   public ScavengerAltarConsumeEvent createChild() {
      return new ScavengerAltarConsumeEvent(this);
   }

   public ScavengerAltarConsumeEvent.Data invoke(Level level, ScavengerAltarTileEntity tile) {
      return this.invoke(new ScavengerAltarConsumeEvent.Data(level, tile));
   }

   public static class Data {
      private final Level level;
      private final ScavengerAltarTileEntity tile;

      public Data(Level level, ScavengerAltarTileEntity tile) {
         this.level = level;
         this.tile = tile;
      }

      public Level getLevel() {
         return this.level;
      }

      public ScavengerAltarTileEntity getTile() {
         return this.tile;
      }
   }
}
