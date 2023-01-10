package iskallia.vault.core.event.common;

import iskallia.vault.block.base.FillableAltarTileEntity;
import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AltarProgressEvent extends Event<AltarProgressEvent, AltarProgressEvent.Data> {
   public AltarProgressEvent() {
   }

   protected AltarProgressEvent(AltarProgressEvent parent) {
      super(parent);
   }

   public AltarProgressEvent createChild() {
      return new AltarProgressEvent(this);
   }

   public AltarProgressEvent.Data invoke(
      ServerLevel world, ServerPlayer player, BlockState state, BlockPos pos, FillableAltarTileEntity blockEntity, int progress, int total, boolean consuming
   ) {
      return this.invoke(new AltarProgressEvent.Data(world, player, state, pos, blockEntity, progress, total, consuming));
   }

   public AltarProgressEvent in(Level world) {
      return this.filter(data -> data.getWorld() == world);
   }

   public static class Data {
      private final Level world;
      private final ServerPlayer player;
      private final BlockState state;
      private final BlockPos pos;
      private final FillableAltarTileEntity blockEntity;
      private final int progress;
      private final int total;
      private final boolean consuming;

      public Data(
         ServerLevel world,
         ServerPlayer player,
         BlockState state,
         BlockPos pos,
         FillableAltarTileEntity blockEntity,
         int progress,
         int total,
         boolean consuming
      ) {
         this.world = world;
         this.player = player;
         this.state = state;
         this.pos = pos;
         this.blockEntity = blockEntity;
         this.progress = progress;
         this.total = total;
         this.consuming = consuming;
      }

      public Level getWorld() {
         return this.world;
      }

      public ServerPlayer getPlayer() {
         return this.player;
      }

      public BlockState getState() {
         return this.state;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public FillableAltarTileEntity getBlockEntity() {
         return this.blockEntity;
      }

      public int getProgress() {
         return this.progress;
      }

      public int getTotal() {
         return this.total;
      }

      public boolean isConsuming() {
         return this.consuming;
      }
   }
}
