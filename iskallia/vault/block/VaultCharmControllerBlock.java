package iskallia.vault.block;

import iskallia.vault.block.entity.VaultCharmControllerTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.VaultCharmUpgrade;
import iskallia.vault.world.data.VaultCharmData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class VaultCharmControllerBlock extends Block implements EntityBlock {
   public VaultCharmControllerBlock() {
      super(Properties.of(Material.METAL).strength(2.0F, 3600000.0F).noOcclusion());
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.VAULT_CHARM_CONTROLLER_TILE_ENTITY.create(pPos, pState);
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
      if (world.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (hand != InteractionHand.MAIN_HAND) {
         return InteractionResult.SUCCESS;
      } else {
         BlockEntity te = world.getBlockEntity(pos);
         if (!(te instanceof VaultCharmControllerTileEntity)) {
            return InteractionResult.SUCCESS;
         } else if (player instanceof ServerPlayer sPlayer) {
            VaultCharmData data = VaultCharmData.get(sPlayer.getLevel());
            VaultCharmData.VaultCharmInventory inventory = data.getInventory(sPlayer);
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof VaultCharmUpgrade item) {
               int newSize = item.getTier().getSlotAmount();
               System.out.println(newSize);
               System.out.println(inventory.canUpgrade(newSize));
               if (inventory.canUpgrade(newSize)) {
                  player.level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                  data.upgradeInventorySize(sPlayer, item.getTier().getSlotAmount());
                  heldItem.shrink(1);
                  return InteractionResult.SUCCESS;
               } else {
                  player.level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                  return InteractionResult.SUCCESS;
               }
            } else {
               NetworkHooks.openGui(
                  (ServerPlayer)player, (VaultCharmControllerTileEntity)te, buffer -> buffer.writeNbt(data.getInventory(sPlayer).serializeNBT())
               );
               return InteractionResult.SUCCESS;
            }
         } else {
            return InteractionResult.SUCCESS;
         }
      }
   }

   public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      return Shapes.or(
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         new VoxelShape[]{
            Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
            Block.box(6.0, 1.0, 6.0, 10.0, 4.0, 10.0),
            Block.box(5.0, 4.0, 5.0, 11.0, 7.0, 11.0),
            Block.box(4.0, 7.0, 4.0, 12.0, 9.0, 12.0),
            Block.box(1.0, 9.0, 1.0, 15.0, 11.0, 15.0),
            Block.box(5.0, 11.0, 5.0, 11.0, 15.0, 11.0)
         }
      );
   }
}
