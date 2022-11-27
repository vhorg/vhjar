package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TreasureRoomOpenEvent extends Event<TreasureRoomOpenEvent, TreasureRoomOpenEvent.Data> {
   public TreasureRoomOpenEvent() {
   }

   protected TreasureRoomOpenEvent(TreasureRoomOpenEvent parent) {
      super(parent);
   }

   public TreasureRoomOpenEvent createChild() {
      return new TreasureRoomOpenEvent(this);
   }

   public TreasureRoomOpenEvent.Data invoke(Level level, Player player, BlockPos pos) {
      return this.invoke(new TreasureRoomOpenEvent.Data(level, player, pos));
   }

   public static class Data {
      private final Level level;
      private final Player player;
      private final BlockPos pos;

      public Data(Level level, Player player, BlockPos pos) {
         this.level = level;
         this.player = player;
         this.pos = pos;
      }

      public Level getLevel() {
         return this.level;
      }

      public Player getPlayer() {
         return this.player;
      }

      public BlockPos getPos() {
         return this.pos;
      }
   }
}
