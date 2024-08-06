package iskallia.vault.core.event.common;

import iskallia.vault.block.entity.GridGatewayTileEntity;
import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GridGatewayUpdateEvent extends Event<GridGatewayUpdateEvent, GridGatewayUpdateEvent.Data> {
   public GridGatewayUpdateEvent() {
   }

   protected GridGatewayUpdateEvent(GridGatewayUpdateEvent parent) {
      super(parent);
   }

   public GridGatewayUpdateEvent createChild() {
      return new GridGatewayUpdateEvent(this);
   }

   public GridGatewayUpdateEvent.Data invoke(Level level, BlockState state, BlockPos pos, GridGatewayTileEntity entity) {
      return this.invoke(new GridGatewayUpdateEvent.Data(level, state, pos, entity));
   }

   public static class Data {
      private final Level level;
      private final BlockState state;
      private final BlockPos pos;
      private final GridGatewayTileEntity entity;

      public Data(Level level, BlockState state, BlockPos pos, GridGatewayTileEntity entity) {
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

      public GridGatewayTileEntity getEntity() {
         return this.entity;
      }
   }
}
