package iskallia.vault.block;

import iskallia.vault.block.entity.AngelBlockTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AngelBlock extends BaseEntityBlock implements EntityBlock {
   private static final int ANGEL_BLOCK_RANGE = 64;
   private final Map<UUID, Set<AngelBlock.DimensionPos>> playerAngelBlocks = new HashMap<>();
   protected static final VoxelShape SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0);

   public AngelBlock() {
      super(Properties.of(Material.GLASS).strength(1.5F, 6.0F).noOcclusion().lightLevel(state -> 15));
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public void addPlayerAngelBlock(UUID owner, ResourceKey<Level> dimension, BlockPos pos) {
      this.playerAngelBlocks.computeIfAbsent(owner, uuid -> new HashSet<>()).add(new AngelBlock.DimensionPos(dimension, pos));
   }

   public boolean isInRange(Player player) {
      for (AngelBlock.DimensionPos dimensionPos : this.playerAngelBlocks.getOrDefault(player.getUUID(), Set.of())) {
         if (player.getLevel().dimension().equals(dimensionPos.dimension()) && dimensionPos.pos().closerThan(player.blockPosition(), 64.0)) {
            return true;
         }
      }

      return false;
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new AngelBlockTileEntity(pPos, pState);
   }

   @javax.annotation.Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return BlockHelper.getTicker(pBlockEntityType, ModBlocks.ANGEL_BLOCK_TILE_ENTITY, AngelBlockTileEntity::tick);
   }

   public void setPlacedBy(Level pLevel, BlockPos pos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      super.setPlacedBy(pLevel, pos, pState, pPlacer, pStack);
      pLevel.getBlockEntity(pos, ModBlocks.ANGEL_BLOCK_TILE_ENTITY).ifPresent(blockEntity -> blockEntity.setOwner(pPlacer.getUUID()));
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.getBlock() != pNewState.getBlock()) {
         pLevel.getBlockEntity(pPos, ModBlocks.ANGEL_BLOCK_TILE_ENTITY)
            .ifPresent(blockEntity -> this.playerAngelBlocks.getOrDefault(blockEntity.getOwner(), new HashSet<>()).remove(pPos));
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   private record DimensionPos(ResourceKey<Level> dimension, BlockPos pos) {
   }
}
