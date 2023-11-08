package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

public class PlayerActionEvent extends Event<PlayerActionEvent, PlayerActionEvent.Data> {
   public PlayerActionEvent() {
   }

   protected PlayerActionEvent(PlayerActionEvent parent) {
      super(parent);
   }

   public PlayerActionEvent createChild() {
      return new PlayerActionEvent(this);
   }

   public PlayerActionEvent.Data invoke(Player player, Level world, BlockPos pos, GameType gameMode, boolean restricted) {
      return this.invoke(new PlayerActionEvent.Data(player, world, pos, gameMode, restricted));
   }

   public static class Data {
      private final Player player;
      private final Level world;
      private final BlockPos pos;
      private final GameType gameMode;
      private boolean restricted;

      public Data(Player player, Level world, BlockPos pos, GameType gameMode, boolean restricted) {
         this.player = player;
         this.world = world;
         this.pos = pos;
         this.gameMode = gameMode;
         this.restricted = restricted;
      }

      public Player getPlayer() {
         return this.player;
      }

      public Level getWorld() {
         return this.world;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public GameType getGameMode() {
         return this.gameMode;
      }

      public boolean isRestricted() {
         return this.restricted;
      }

      public void setRestricted(boolean restricted) {
         this.restricted = restricted;
      }
   }
}
