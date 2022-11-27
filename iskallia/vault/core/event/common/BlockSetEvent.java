package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSetEvent extends Event<BlockSetEvent, BlockSetEvent.Data> {
   public BlockSetEvent() {
   }

   protected BlockSetEvent(BlockSetEvent parent) {
      super(parent);
   }

   public BlockSetEvent createChild() {
      return new BlockSetEvent(this);
   }

   public BlockSetEvent.Data invoke(LevelWriter world, BlockPos pos, BlockState state, int flags, int recursionLeft, BlockSetEvent.Type type) {
      return this.invoke(new BlockSetEvent.Data(world, pos, state, flags, recursionLeft, type));
   }

   public BlockSetEvent of(Block block) {
      return this.filter(data -> data.getState().getBlock() == block);
   }

   public BlockSetEvent of(BlockState state) {
      return this.filter(data -> data.getState() == state);
   }

   public BlockSetEvent in(LevelWriter world) {
      return this.filter(data -> data.getWorld() == world || data.getWorld() instanceof WorldGenRegion genRegion && genRegion.getLevel() == world);
   }

   public BlockSetEvent inWorld() {
      return this.filter(data -> data.getWorld() instanceof Level);
   }

   public BlockSetEvent inGenRegion() {
      return this.filter(data -> data.getWorld() instanceof WorldGenRegion);
   }

   public BlockSetEvent at(BlockSetEvent.Type type) {
      return this.filter(data -> data.getType() == type);
   }

   public static class Data {
      private final LevelWriter world;
      private final BlockPos pos;
      private BlockState state;
      private int flags;
      private final int recursionLeft;
      private final BlockSetEvent.Type type;

      public Data(LevelWriter world, BlockPos pos, BlockState state, int flags, int recursionLeft, BlockSetEvent.Type type) {
         this.world = world;
         this.pos = pos;
         this.state = state;
         this.flags = flags;
         this.recursionLeft = recursionLeft;
         this.type = type;
      }

      public LevelWriter getWorld() {
         return this.world;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public BlockState getState() {
         return this.state;
      }

      public int getFlags() {
         return this.flags;
      }

      public int getRecursionLeft() {
         return this.recursionLeft;
      }

      public BlockSetEvent.Type getType() {
         return this.type;
      }

      public void setState(BlockState state) {
         this.state = state;
      }

      public void setFlags(int flags) {
         this.flags = flags;
      }
   }

   public static enum Type {
      HEAD,
      RETURN;
   }
}
