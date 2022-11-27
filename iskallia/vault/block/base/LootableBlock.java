package iskallia.vault.block.base;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;

public abstract class LootableBlock extends Block implements EntityBlock {
   public LootableBlock(Properties properties) {
      super(properties);
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      if (level.isClientSide()) {
         return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
      } else if (level.getBlockEntity(pos) instanceof LootableTileEntity te) {
         if (te.getLootTable() == null) {
            return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
         } else {
            this.playerWillDestroy(level, pos, state, player);
            List<ItemStack> loot = this.generateLoot(te, player);
            this.dropLoot(level, pos, loot);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return false;
         }
      } else {
         return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
      }
   }

   protected void dropLoot(Level level, BlockPos pos, List<ItemStack> loot) {
      loot.forEach(stack -> popResource(level, pos, stack));
   }

   protected List<ItemStack> generateLoot(LootableTileEntity te, Player player) {
      return player instanceof ServerPlayer serverPlayer ? te.generateLoot(serverPlayer) : List.of();
   }

   public PushReaction getPistonPushReaction(BlockState pState) {
      return PushReaction.DESTROY;
   }
}
