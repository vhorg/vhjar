package iskallia.vault.block;

import com.google.common.collect.Lists;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.VaultBossSpawner;
import iskallia.vault.world.vault.logic.objective.SummonAndKillAllBossesObjective;
import iskallia.vault.world.vault.logic.objective.SummonAndKillBossObjective;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectSummonAndKillBossesObjective;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ObeliskBlock extends Block {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.field_208163_P;
   public static final IntegerProperty COMPLETION = IntegerProperty.func_177719_a("completion", 0, 4);
   private static final VoxelShape SHAPE = Block.func_208617_a(2.0, 0.0, 2.0, 14.0, 32.0, 14.0);
   private static final VoxelShape SHAPE_TOP = SHAPE.func_197751_a(0.0, -1.0, 0.0);

   public ObeliskBlock() {
      super(Properties.func_200945_a(Material.field_151576_e).func_200947_a(SoundType.field_185852_e).func_200948_a(-1.0F, 3600000.0F).func_222380_e());
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(HALF, DoubleBlockHalf.LOWER)).func_206870_a(COMPLETION, 0)
      );
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.UPPER ? SHAPE_TOP : SHAPE;
   }

   public boolean hasTileEntity(BlockState state) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.LOWER;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return this.hasTileEntity(state) ? ModBlocks.OBELISK_TILE_ENTITY.func_200968_a() : null;
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (state.func_177229_b(HALF) == DoubleBlockHalf.UPPER) {
         BlockState downState = world.func_180495_p(pos.func_177977_b());
         return !(downState.func_177230_c() instanceof ObeliskBlock)
            ? ActionResultType.PASS
            : this.func_225533_a_(downState, world, pos.func_177977_b(), player, hand, hit);
      } else {
         if ((Integer)state.func_177229_b(COMPLETION) != 4 && this.newBlockActivated(state, world, pos, player, hand, hit)) {
            BlockState newState = (BlockState)state.func_206870_a(COMPLETION, 4);
            world.func_175656_a(pos, newState);
            this.spawnParticles(world, pos);
         }

         return ActionResultType.SUCCESS;
      }
   }

   private boolean newBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (world.field_72995_K) {
         return false;
      } else {
         VaultRaid vault = VaultRaidData.get((ServerWorld)world).getAt((ServerWorld)world, pos);
         if (vault == null) {
            return false;
         } else {
            SummonAndKillBossObjective objective = vault.getPlayer(player.func_110124_au())
               .flatMap(vaultPlayer -> vaultPlayer.getActiveObjective(SummonAndKillBossObjective.class))
               .orElseGet(() -> vault.getActiveObjective(SummonAndKillBossObjective.class).orElse(null));
            if (objective != null) {
               if (objective.allObelisksClicked()) {
                  return false;
               } else {
                  objective.addObelisk();
                  if (objective.allObelisksClicked()) {
                     LivingEntity boss = VaultBossSpawner.spawnBoss(vault, (ServerWorld)world, pos);
                     objective.setBoss(boss);
                  }

                  return true;
               }
            } else {
               ArchitectSummonAndKillBossesObjective objective3 = vault.getPlayer(player.func_110124_au())
                  .flatMap(vaultPlayer -> vaultPlayer.getActiveObjective(ArchitectSummonAndKillBossesObjective.class))
                  .orElseGet(() -> vault.getActiveObjective(ArchitectSummonAndKillBossesObjective.class).orElse(null));
               if (objective3 != null) {
                  LivingEntity boss = VaultBossSpawner.spawnBoss(vault, (ServerWorld)world, pos);
                  objective3.setBoss(boss);
                  return true;
               } else {
                  SummonAndKillAllBossesObjective objective2 = vault.getPlayer(player.func_110124_au())
                     .flatMap(vaultPlayer -> vaultPlayer.getActiveObjective(SummonAndKillAllBossesObjective.class))
                     .orElseGet(() -> vault.getActiveObjective(SummonAndKillAllBossesObjective.class).orElse(null));
                  if (objective2 != null) {
                     if (!objective2.allObelisksClicked() && !objective2.allBossesDefeated()) {
                        objective2.addObelisk();
                        LivingEntity boss = VaultBossSpawner.spawnBoss(vault, (ServerWorld)world, pos);
                        objective2.addBoss(boss);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            }
         }
      }
   }

   private void spawnParticles(World world, BlockPos pos) {
      for (int i = 0; i < 20; i++) {
         double d0 = world.field_73012_v.nextGaussian() * 0.02;
         double d1 = world.field_73012_v.nextGaussian() * 0.02;
         double d2 = world.field_73012_v.nextGaussian() * 0.02;
         ((ServerWorld)world)
            .func_195598_a(
               ParticleTypes.field_197598_I,
               pos.func_177958_n() + world.field_73012_v.nextDouble() - d0,
               pos.func_177956_o() + world.field_73012_v.nextDouble() - d1,
               pos.func_177952_p() + world.field_73012_v.nextDouble() - d2,
               10,
               d0,
               d1,
               d2,
               1.0
            );
      }

      world.func_184133_a(null, pos, SoundEvents.field_206933_aM, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{HALF}).func_206894_a(new Property[]{COMPLETION});
   }

   public void func_196243_a(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
      super.func_196243_a(state, world, pos, newState, isMoving);
      if (!state.func_203425_a(newState.func_177230_c())) {
         if (state.func_177229_b(HALF) == DoubleBlockHalf.UPPER) {
            BlockState otherState = world.func_180495_p(pos.func_177977_b());
            if (otherState.func_203425_a(state.func_177230_c())) {
               world.func_217377_a(pos.func_177977_b(), isMoving);
            }
         } else {
            BlockState otherState = world.func_180495_p(pos.func_177984_a());
            if (otherState.func_203425_a(state.func_177230_c())) {
               world.func_217377_a(pos.func_177984_a(), isMoving);
            }
         }
      }
   }

   public List<ItemStack> func_220076_a(BlockState state, net.minecraft.loot.LootContext.Builder builder) {
      return Lists.newArrayList();
   }

   public BlockRenderType func_149645_b(BlockState state) {
      return BlockRenderType.MODEL;
   }
}
