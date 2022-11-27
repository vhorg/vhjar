package iskallia.vault.item;

import iskallia.vault.VaultMod;
import iskallia.vault.block.OtherSidePortalBlock;
import iskallia.vault.block.VaultPortalSize;
import iskallia.vault.block.entity.OtherSidePortalTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated(
   forRemoval = true
)
public class BurntCrystalItem extends Item {
   public BurntCrystalItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(1));
      this.setRegistryName(id);
   }

   public InteractionResult useOn(UseOnContext context) {
      if (!context.getLevel().isClientSide && context.getPlayer() != null) {
         ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
         if (stack.getItem() == ModItems.BURNT_CRYSTAL) {
            BlockPos pos = context.getClickedPos();
            if (this.tryCreatePortal((ServerLevel)context.getLevel(), pos, context.getClickedFace())) {
               context.getLevel().playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.VAULT_PORTAL_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
               context.getItemInHand().shrink(1);
               return InteractionResult.SUCCESS;
            }
         }

         return super.useOn(context);
      } else {
         return super.useOn(context);
      }
   }

   private boolean tryCreatePortal(ServerLevel world, BlockPos pos, Direction facing) {
      Optional<VaultPortalSize> optional = VaultPortalSize.getPortalSize(
         world,
         pos.relative(facing),
         Axis.X,
         (statex, reader, p) -> Arrays.stream(ModConfigs.OTHER_SIDE.getValidFrameBlocks()).anyMatch(b -> b == statex.getBlock())
      );
      ServerLevel target = world.getServer().getLevel(world.dimension() == VaultMod.OTHER_SIDE_KEY ? Level.OVERWORLD : VaultMod.OTHER_SIDE_KEY);
      if (optional.isPresent()) {
         VaultPortalSize portal = optional.get();
         OtherSideData data = new OtherSideData(null);
         BlockPos start = portal.getBottomLeft();
         BlockPos match = forcePlace(world, start, target, portal);
         data.setLinkedPos(match);
         data.setLinkedDim(target.dimension());
         BlockState state = (BlockState)ModBlocks.OTHER_SIDE_PORTAL.defaultBlockState().setValue(OtherSidePortalBlock.AXIS, portal.getAxis());
         portal.placePortalBlocks(blockPos -> {
            world.setBlock(blockPos, state, 3);
            if (world.getBlockEntity(blockPos) instanceof OtherSidePortalTileEntity portalTE) {
               portalTE.setOtherSideData(data);
            }
         });
         return true;
      } else {
         return false;
      }
   }

   public static BlockPos forcePlace(ServerLevel source, BlockPos sourcePos, ServerLevel target, VaultPortalSize current) {
      BlockPos match = null;

      for (int i = 0; i < target.getMaxBuildHeight(); i++) {
         BlockPos p = new BlockPos(sourcePos.getX(), i, sourcePos.getZ());
         Block block = target.getBlockState(p).getBlock();
         if (block == ModBlocks.OTHER_SIDE_PORTAL) {
            match = p;
            break;
         }
      }

      if (match == null) {
         for (int ix = 0; ix < target.getMaxBuildHeight(); ix++) {
            int yUp = sourcePos.getY() + ix;
            int yDo = sourcePos.getY() - ix;
            if (place(source, sourcePos, target, new BlockPos(sourcePos.getX(), yUp, sourcePos.getZ()), current, false)) {
               match = new BlockPos(sourcePos.getX(), yUp, sourcePos.getZ());
               break;
            }

            if (place(source, sourcePos, target, new BlockPos(sourcePos.getX(), yDo, sourcePos.getZ()), current, false)) {
               match = new BlockPos(sourcePos.getX(), yDo, sourcePos.getZ());
               break;
            }
         }
      }

      if (match == null) {
         place(source, sourcePos, target, new BlockPos(sourcePos.getX(), 128, sourcePos.getZ()), current, true);
         match = new BlockPos(sourcePos.getX(), 128, sourcePos.getZ());
      }

      return match;
   }

   private static boolean place(ServerLevel source, BlockPos sourcePos, ServerLevel target, BlockPos targetPos, VaultPortalSize current, boolean force) {
      if (!force) {
         if (targetPos.getY() >= target.getMaxBuildHeight() || targetPos.getY() < 0) {
            return false;
         }

         if (!target.getBlockState(targetPos).isAir()
            || !target.getBlockState(targetPos.above()).isAir()
            || !target.getBlockState(targetPos.below()).canOcclude()) {
            return false;
         }
      }

      BlockState state = (BlockState)ModBlocks.OTHER_SIDE_PORTAL.defaultBlockState().setValue(OtherSidePortalBlock.AXIS, current.getAxis());
      OtherSideData data = new OtherSideData(null);
      data.setLinkedDim(source.dimension());
      data.setLinkedPos(sourcePos);
      BlockPos.betweenClosed(
            targetPos.relative(Direction.DOWN).relative(current.getRightDir().getOpposite()),
            targetPos.relative(Direction.UP, current.getHeight()).relative(current.getRightDir(), current.getWidth())
         )
         .forEach(blockPos -> target.setBlock(blockPos, Blocks.QUARTZ_BRICKS.defaultBlockState(), 3));
      BlockPos.betweenClosed(targetPos, targetPos.relative(Direction.UP, current.getHeight() - 1).relative(current.getRightDir(), current.getWidth() - 1))
         .forEach(blockPos -> {
            target.setBlock(blockPos, state, 3);
            if (target.getBlockEntity(blockPos) instanceof OtherSidePortalTileEntity portalTE) {
               portalTE.setOtherSideData(data);
            }
         });
      return true;
   }
}
