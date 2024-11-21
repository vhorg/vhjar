package iskallia.vault.block.challenge;

import iskallia.vault.block.entity.challenge.raid.RaidControllerProxyBlockEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;

public class RaidControllerProxyBlock extends Block implements EntityBlock {
   public RaidControllerProxyBlock() {
      super(Properties.of(Material.GLASS).sound(SoundType.STONE).strength(-1.0F, 3600000.0F).lightLevel(value -> 10).noCollission().noOcclusion().noDrops());
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new RaidControllerProxyBlockEntity(pos, state);
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return BlockHelper.getTicker(pBlockEntityType, ModBlocks.RAID_CONTROLLER_PROXY_TILE_ENTITY, RaidControllerProxyBlockEntity::tick);
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }
}
