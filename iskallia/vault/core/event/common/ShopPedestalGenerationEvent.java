package iskallia.vault.core.event.common;

import iskallia.vault.block.entity.ShopPedestalBlockTile;
import iskallia.vault.core.event.Event;
import iskallia.vault.core.random.RandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class ShopPedestalGenerationEvent extends Event<ShopPedestalGenerationEvent, ShopPedestalGenerationEvent.Data> {
   public ShopPedestalGenerationEvent() {
   }

   protected ShopPedestalGenerationEvent(ShopPedestalGenerationEvent parent) {
      super(parent);
   }

   public ShopPedestalGenerationEvent createChild() {
      return new ShopPedestalGenerationEvent(this);
   }

   public ShopPedestalGenerationEvent.Data invoke(ServerLevel world, BlockState state, BlockPos pos, ShopPedestalBlockTile tileEntity, RandomSource random) {
      return this.invoke(new ShopPedestalGenerationEvent.Data(world, state, pos, tileEntity, random));
   }

   public static class Data {
      private final ServerLevel world;
      private final BlockState state;
      private final BlockPos pos;
      private final ShopPedestalBlockTile tileEntity;
      private RandomSource random;

      public Data(ServerLevel world, BlockState state, BlockPos pos, ShopPedestalBlockTile tileEntity, RandomSource random) {
         this.world = world;
         this.state = state;
         this.pos = pos;
         this.tileEntity = tileEntity;
         this.random = random;
      }

      public ServerLevel getWorld() {
         return this.world;
      }

      public BlockState getState() {
         return this.state;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public ShopPedestalBlockTile getTileEntity() {
         return this.tileEntity;
      }

      public RandomSource getRandom() {
         return this.random;
      }

      public void setRandom(RandomSource random) {
         this.random = random;
      }
   }
}
