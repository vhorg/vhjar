package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockUseEvent extends Event<BlockUseEvent, BlockUseEvent.Data> {
   public BlockUseEvent() {
   }

   protected BlockUseEvent(BlockUseEvent parent) {
      super(parent);
   }

   public BlockUseEvent createChild() {
      return new BlockUseEvent(this);
   }

   public BlockUseEvent.Data invoke(Level world, BlockState state, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      return this.invoke(new BlockUseEvent.Data(world, state, pos, player, hand, hit));
   }

   public BlockUseEvent of(Block block) {
      return this.filter(data -> data.getState().getBlock() == block);
   }

   public BlockUseEvent in(Level world) {
      return this.filter(data -> data.getWorld() == world);
   }

   public static class Data {
      private final Level world;
      private final BlockState state;
      private final BlockPos pos;
      private final Player player;
      private final InteractionHand hand;
      private final BlockHitResult hit;
      private InteractionResult result;

      public Data(Level world, BlockState state, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
         this.world = world;
         this.state = state;
         this.pos = pos;
         this.player = player;
         this.hand = hand;
         this.hit = hit;
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

      public Player getPlayer() {
         return this.player;
      }

      public InteractionHand getHand() {
         return this.hand;
      }

      public BlockHitResult getHit() {
         return this.hit;
      }

      public InteractionResult getResult() {
         return this.result;
      }

      public void setResult(InteractionResult result) {
         this.result = result;
      }
   }
}
