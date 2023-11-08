package iskallia.vault.core.event.common;

import iskallia.vault.block.entity.GateLockTileEntity;
import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class GateLockOpenEvent extends Event<GateLockOpenEvent, GateLockOpenEvent.Data> {
   public GateLockOpenEvent() {
   }

   protected GateLockOpenEvent(GateLockOpenEvent parent) {
      super(parent);
   }

   public GateLockOpenEvent createChild() {
      return new GateLockOpenEvent(this);
   }

   public GateLockOpenEvent.Data invoke(Level world, Player player, BlockPos pos, GateLockTileEntity entity) {
      return this.invoke(new GateLockOpenEvent.Data(world, player, pos, entity));
   }

   public static class Data {
      private final Level world;
      private final Player player;
      private final BlockPos pos;
      private final GateLockTileEntity entity;

      public Data(Level world, Player player, BlockPos pos, GateLockTileEntity entity) {
         this.world = world;
         this.player = player;
         this.pos = pos;
         this.entity = entity;
      }

      public Level getWorld() {
         return this.world;
      }

      public Player getPlayer() {
         return this.player;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public GateLockTileEntity getEntity() {
         return this.entity;
      }
   }
}
