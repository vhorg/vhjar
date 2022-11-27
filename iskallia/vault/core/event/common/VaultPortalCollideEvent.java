package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class VaultPortalCollideEvent extends Event<VaultPortalCollideEvent, VaultPortalCollideEvent.Data> {
   public VaultPortalCollideEvent() {
   }

   protected VaultPortalCollideEvent(VaultPortalCollideEvent parent) {
      super(parent);
   }

   public VaultPortalCollideEvent createChild() {
      return new VaultPortalCollideEvent(this);
   }

   public VaultPortalCollideEvent.Data invoke(ServerLevel world, BlockState state, BlockPos pos, ServerPlayer player) {
      return this.invoke(new VaultPortalCollideEvent.Data(world, state, pos, player));
   }

   public VaultPortalCollideEvent in(Level world) {
      return this.filter(data -> data.getWorld() == world);
   }

   public static class Data {
      private final Level world;
      private final BlockState state;
      private final BlockPos pos;
      private final Player player;

      public Data(ServerLevel world, BlockState state, BlockPos pos, ServerPlayer player) {
         this.world = world;
         this.state = state;
         this.pos = pos;
         this.player = player;
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
   }
}
