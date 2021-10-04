package iskallia.vault.block;

import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.block.entity.TrophyStatueTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.StatueType;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.PlayerVaultStatsData;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class TrophyBlock extends LootStatueBlock {
   public static final VoxelShape SHAPE = Block.func_208617_a(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

   public TrophyBlock() {
      super(
         StatueType.TROPHY,
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151647_F).func_200948_a(5.0F, 3600000.0F).func_226896_b_().func_200942_a()
      );
   }

   @Override
   public VoxelShape func_220053_a(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   @Override
   protected void setStatueTileData(LootStatueTileEntity lootStatue, CompoundNBT blockEntityTag) {
      super.setStatueTileData(lootStatue, blockEntityTag);
      if (lootStatue instanceof TrophyStatueTileEntity) {
         TrophyStatueTileEntity trophyStatue = (TrophyStatueTileEntity)lootStatue;
         WeekKey week = WeekKey.deserialize(blockEntityTag.func_74775_l("trophyWeek"));
         PlayerVaultStatsData.PlayerRecordEntry recordEntry = PlayerVaultStatsData.PlayerRecordEntry.deserialize(blockEntityTag.func_74775_l("recordEntry"));
         trophyStatue.setWeek(week);
         trophyStatue.setRecordEntry(recordEntry);
      }
   }

   @Nullable
   @Override
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.TROPHY_STATUE_TILE_ENTITY.func_200968_a();
   }
}
