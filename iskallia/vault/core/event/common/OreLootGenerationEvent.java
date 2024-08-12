package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.random.RandomSource;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class OreLootGenerationEvent extends Event<OreLootGenerationEvent, OreLootGenerationEvent.Data> {
   public OreLootGenerationEvent() {
   }

   public OreLootGenerationEvent(OreLootGenerationEvent parent) {
      super(parent);
   }

   public OreLootGenerationEvent createChild() {
      return new OreLootGenerationEvent(this);
   }

   public OreLootGenerationEvent.Data invoke(
      @Nullable ServerPlayer player, BlockState state, BlockPos pos, BlockEntity tileEntity, List<ItemStack> loot, RandomSource random
   ) {
      return this.invoke(new OreLootGenerationEvent.Data(player, state, pos, tileEntity, loot, random));
   }

   public static class Data {
      @Nullable
      private final ServerPlayer player;
      private final BlockState state;
      private final BlockPos pos;
      private final List<ItemStack> loot;
      private RandomSource random;
      private final BlockEntity tileEntity;

      public Data(@Nullable ServerPlayer player, BlockState state, BlockPos pos, BlockEntity tileEntity, List<ItemStack> loot, RandomSource random) {
         this.player = player;
         this.state = state;
         this.pos = pos;
         this.loot = loot;
         this.random = random;
         this.tileEntity = tileEntity;
      }

      @Nullable
      public ServerPlayer getPlayer() {
         return this.player;
      }

      public BlockState getState() {
         return this.state;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public List<ItemStack> getLoot() {
         return this.loot;
      }

      public RandomSource getRandom() {
         return this.random;
      }

      public BlockEntity getTileEntity() {
         return this.tileEntity;
      }

      public void setRandom(RandomSource random) {
         this.random = random;
      }
   }
}
