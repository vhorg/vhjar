package iskallia.vault.block;

import iskallia.vault.block.entity.CapacitorTileEntity;
import iskallia.vault.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CapacitorBlock extends Block {
   public CapacitorBlock() {
      super(
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151668_h)
            .func_200948_a(5.0F, 3600000.0F)
            .func_200947_a(SoundType.field_185852_e)
      );
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.CAPACITOR_TILE_ENTITY.func_200968_a();
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (world.field_72995_K) {
         return super.func_225533_a_(state, world, pos, player, hand, hit);
      } else {
         CapacitorTileEntity te = getCapacitorTileEntity(world, pos);
         if (te != null) {
            player.func_146105_b(new StringTextComponent("Energy Stored: " + te.getEnergyStorage().getEnergyStored()), true);
         }

         return super.func_225533_a_(state, world, pos, player, hand, hit);
      }
   }

   public static CapacitorTileEntity getCapacitorTileEntity(World world, BlockPos pos) {
      TileEntity tileEntity = world.func_175625_s(pos);
      return !(tileEntity instanceof CapacitorTileEntity) ? null : (CapacitorTileEntity)tileEntity;
   }
}
