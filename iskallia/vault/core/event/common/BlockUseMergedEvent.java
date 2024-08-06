package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.world.data.tile.PartialTile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class BlockUseMergedEvent extends Event<BlockUseMergedEvent, BlockUseMergedEvent.Data> {
   public BlockUseMergedEvent() {
   }

   protected BlockUseMergedEvent(BlockUseMergedEvent parent) {
      super(parent);
   }

   public BlockUseMergedEvent createChild() {
      return new BlockUseMergedEvent(this);
   }

   public BlockUseMergedEvent.Data invoke(Level world, Player player, InteractionHand hand, BlockHitResult hit, PartialTile pre, PartialTile post) {
      return this.invoke(new BlockUseMergedEvent.Data(world, player, hand, hit, pre, post));
   }

   public static class Data {
      private final Level world;
      private final Player player;
      private final InteractionHand hand;
      private final BlockHitResult hit;
      private final PartialTile pre;
      private final PartialTile post;

      public Data(Level world, Player player, InteractionHand hand, BlockHitResult hit, PartialTile pre, PartialTile post) {
         this.world = world;
         this.player = player;
         this.hand = hand;
         this.hit = hit;
         this.pre = pre;
         this.post = post;
      }

      public Level getWorld() {
         return this.world;
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

      public PartialTile getPre() {
         return this.pre;
      }

      public PartialTile getPost() {
         return this.post;
      }
   }
}
