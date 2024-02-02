package iskallia.vault.core.event.common;

import iskallia.vault.block.entity.MonolithTileEntity;
import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MonolithUpdateEvent extends Event<MonolithUpdateEvent, MonolithUpdateEvent.Data> {
   public MonolithUpdateEvent() {
   }

   protected MonolithUpdateEvent(MonolithUpdateEvent parent) {
      super(parent);
   }

   public MonolithUpdateEvent createChild() {
      return new MonolithUpdateEvent(this);
   }

   public MonolithUpdateEvent.Data invoke(Level world, BlockState state, BlockPos pos, MonolithTileEntity entity) {
      return this.invoke(new MonolithUpdateEvent.Data(world, state, pos, entity));
   }

   public static class Data {
      private final Level world;
      private final BlockState state;
      private final BlockPos pos;
      private final MonolithTileEntity entity;

      public Data(Level world, BlockState state, BlockPos pos, MonolithTileEntity entity) {
         this.world = world;
         this.state = state;
         this.pos = pos;
         this.entity = entity;
      }

      public Level getWorld() {
         return this.world;
      }

      public BlockState getState() {
         return this.state;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public MonolithTileEntity getEntity() {
         return this.entity;
      }
   }
}
