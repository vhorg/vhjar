package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBreakSpeedEvent extends Event<BlockBreakSpeedEvent, BlockBreakSpeedEvent.Data> {
   public BlockBreakSpeedEvent() {
   }

   protected BlockBreakSpeedEvent(BlockBreakSpeedEvent parent) {
      super(parent);
   }

   public BlockBreakSpeedEvent createChild() {
      return new BlockBreakSpeedEvent(this);
   }

   public BlockBreakSpeedEvent.Data invoke(Player player, BlockPos pos, BlockState state, float speed) {
      return this.invoke(new BlockBreakSpeedEvent.Data(player, pos, state, speed));
   }

   public BlockBreakSpeedEvent of(Block block) {
      return this.filter(data -> data.getState().getBlock() == block);
   }

   public BlockBreakSpeedEvent of(BlockState state) {
      return this.filter(data -> data.getState() == state);
   }

   public BlockBreakSpeedEvent in(LevelWriter world) {
      return this.filter(data -> data.getPlayer().getLevel() == world);
   }

   public static class Data {
      private final Player player;
      private final BlockPos pos;
      private final BlockState state;
      private float speed;

      public Data(Player player, BlockPos pos, BlockState state, float speed) {
         this.player = player;
         this.pos = pos;
         this.state = state;
         this.speed = speed;
      }

      public Player getPlayer() {
         return this.player;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public BlockState getState() {
         return this.state;
      }

      public float getSpeed() {
         return this.speed;
      }

      public void setSpeed(float speed) {
         this.speed = speed;
      }
   }
}
