package iskallia.vault.core.event.common;

import iskallia.vault.block.entity.GateLockTileEntity;
import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GateLockUpdateEvent extends Event<GateLockUpdateEvent, GateLockUpdateEvent.Data> {
   public GateLockUpdateEvent() {
   }

   protected GateLockUpdateEvent(GateLockUpdateEvent parent) {
      super(parent);
   }

   public GateLockUpdateEvent createChild() {
      return new GateLockUpdateEvent(this);
   }

   public GateLockUpdateEvent.Data invoke(Level level, BlockState state, BlockPos pos, GateLockTileEntity entity) {
      return this.invoke(new GateLockUpdateEvent.Data(level, state, pos, entity));
   }

   public static class Data {
      private final Level level;
      private final BlockState state;
      private final BlockPos pos;
      private final GateLockTileEntity entity;

      public Data(Level level, BlockState state, BlockPos pos, GateLockTileEntity entity) {
         this.level = level;
         this.state = state;
         this.pos = pos;
         this.entity = entity;
      }

      public Level getLevel() {
         return this.level;
      }

      public BlockState getState() {
         return this.state;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public GateLockTileEntity getEntity() {
         return this.entity;
      }
   }
}
