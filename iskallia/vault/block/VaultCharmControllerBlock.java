package iskallia.vault.block;

import iskallia.vault.block.entity.VaultCharmControllerTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.VaultCharmUpgrade;
import iskallia.vault.world.data.VaultCharmData;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class VaultCharmControllerBlock extends Block {
   public VaultCharmControllerBlock() {
      super(Properties.func_200945_a(Material.field_151573_f).func_200948_a(2.0F, 3600000.0F).func_226896_b_());
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.VAULT_CHARM_CONTROLLER_TILE_ENTITY.func_200968_a();
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
      if (world.func_201670_d()) {
         return ActionResultType.SUCCESS;
      } else if (hand != Hand.MAIN_HAND) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity te = world.func_175625_s(pos);
         if (!(te instanceof VaultCharmControllerTileEntity)) {
            return ActionResultType.SUCCESS;
         } else if (!(player instanceof ServerPlayerEntity)) {
            return ActionResultType.SUCCESS;
         } else {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)player;
            VaultCharmData data = VaultCharmData.get(sPlayer.func_71121_q());
            VaultCharmData.VaultCharmInventory inventory = data.getInventory(sPlayer);
            ItemStack heldItem = player.func_184614_ca();
            if (heldItem.func_77973_b() instanceof VaultCharmUpgrade) {
               VaultCharmUpgrade item = (VaultCharmUpgrade)heldItem.func_77973_b();
               int newSize = item.getTier().getSlotAmount();
               if (inventory.canUpgrade(newSize)) {
                  player.field_70170_p.func_184133_a(null, pos, SoundEvents.field_187604_bf, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  data.upgradeInventorySize(sPlayer, item.getTier().getSlotAmount());
                  heldItem.func_190918_g(1);
                  return ActionResultType.SUCCESS;
               } else {
                  player.field_70170_p.func_184133_a(null, pos, SoundEvents.field_187646_bt, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  return ActionResultType.SUCCESS;
               }
            } else {
               NetworkHooks.openGui(
                  (ServerPlayerEntity)player, (VaultCharmControllerTileEntity)te, buffer -> buffer.func_150786_a(data.getInventory(sPlayer).serializeNBT())
               );
               return ActionResultType.SUCCESS;
            }
         }
      }
   }

   public VoxelShape func_220053_a(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.func_216384_a(
         Block.func_208617_a(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         new VoxelShape[]{
            Block.func_208617_a(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
            Block.func_208617_a(6.0, 1.0, 6.0, 10.0, 4.0, 10.0),
            Block.func_208617_a(5.0, 4.0, 5.0, 11.0, 7.0, 11.0),
            Block.func_208617_a(4.0, 7.0, 4.0, 12.0, 9.0, 12.0),
            Block.func_208617_a(1.0, 9.0, 1.0, 15.0, 11.0, 15.0),
            Block.func_208617_a(5.0, 11.0, 5.0, 11.0, 15.0, 11.0)
         }
      );
   }
}
