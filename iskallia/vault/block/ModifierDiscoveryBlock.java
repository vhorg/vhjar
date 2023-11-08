package iskallia.vault.block;

import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import iskallia.vault.core.vault.VaultUtils;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class ModifierDiscoveryBlock extends Block implements EntityBlock {
   public ModifierDiscoveryBlock() {
      super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(-1.0F, 3600000.0F).noDrops().noOcclusion());
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return level.isClientSide() ? BlockHelper.getTicker(type, ModBlocks.MODIFIER_DISCOVERY_ENTITY, ModifierDiscoveryTileEntity::clientBookTick) : null;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.MODIFIER_DISCOVERY_ENTITY.create(pos, state);
   }

   @Nonnull
   public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (!VaultUtils.isVaultLevel(level)) {
         return InteractionResult.FAIL;
      } else if (player instanceof ServerPlayer sPlayer) {
         if (level.getBlockEntity(pos) instanceof ModifierDiscoveryTileEntity modifierDiscovery && modifierDiscovery.canBeUsed(player)) {
            modifierDiscovery.use(sPlayer);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }
}
