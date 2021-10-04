package iskallia.vault.block;

import iskallia.vault.config.ScavengerHuntConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.BasicScavengerItem;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

public class ScavengerTreasureBlock extends ContainerBlock {
   private static final VoxelShape BOX = Block.func_208617_a(0.0, 0.0, 0.0, 16.0, 5.0, 16.0);

   public ScavengerTreasureBlock() {
      super(
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151647_F)
            .harvestLevel(0)
            .harvestTool(ToolType.PICKAXE)
            .func_200948_a(10.0F, 1.0F)
            .func_200947_a(ModSounds.VAULT_GEM)
      );
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return BOX;
   }

   @Nullable
   public TileEntity func_196283_a_(IBlockReader worldIn) {
      return ModBlocks.SCAVENGER_TREASURE_TILE_ENTITY.func_200968_a();
   }

   public BlockRenderType func_149645_b(BlockState state) {
      return BlockRenderType.MODEL;
   }

   public List<ItemStack> func_220076_a(BlockState state, Builder builder) {
      ServerWorld world = builder.func_216018_a();
      BlockPos pos = new BlockPos((Vector3d)builder.func_216019_b(LootParameters.field_237457_g_));
      VaultRaid vault = VaultRaidData.get(world).getAt(world, pos);
      if (vault == null) {
         return super.func_220076_a(state, builder);
      } else {
         List<ItemStack> drops = new ArrayList<>(super.func_220076_a(state, builder));
         ModConfigs.SCAVENGER_HUNT
            .generateTreasureLoot()
            .stream()
            .map(ScavengerHuntConfig.ItemEntry::createItemStack)
            .peek(
               stack -> vault.getProperties().getBase(VaultRaid.IDENTIFIER).ifPresent(identifier -> BasicScavengerItem.setVaultIdentifier(stack, identifier))
            )
            .forEach(drops::add);
         return drops;
      }
   }
}
