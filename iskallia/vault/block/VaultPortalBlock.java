package iskallia.vault.block;

import iskallia.vault.Vault;
import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.VaultUtils;
import iskallia.vault.world.vault.logic.VaultCowOverrides;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.AbstractBlock.IPositionPredicate;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultPortalBlock extends NetherPortalBlock {
   public static final IPositionPredicate FRAME = (state, reader, p) -> Arrays.stream(ModConfigs.VAULT_PORTAL.getValidFrameBlocks())
      .anyMatch(b -> b == state.func_177230_c());
   public static final EnumProperty<VaultPortalBlock.Style> STYLE = EnumProperty.func_177709_a("style", VaultPortalBlock.Style.class);

   public VaultPortalBlock() {
      super(Properties.func_200950_a(Blocks.field_150427_aO));
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176550_a, Axis.X))
            .func_206870_a(STYLE, VaultPortalBlock.Style.RAINBOW)
      );
   }

   public void func_149666_a(ItemGroup group, NonNullList<ItemStack> items) {
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      super.func_206840_a(builder);
      builder.func_206894_a(new Property[]{STYLE});
   }

   public void func_225542_b_(BlockState state, ServerWorld world, BlockPos pos, Random random) {
   }

   public void func_196262_a(BlockState state, World world, BlockPos pos, Entity entity) {
      if (!world.field_72995_K && entity instanceof PlayerEntity) {
         if (!entity.func_184218_aH() && !entity.func_184207_aI() && entity.func_184222_aU()) {
            VoxelShape playerVoxel = VoxelShapes.func_197881_a(
               entity.func_174813_aQ().func_72317_d(-pos.func_177958_n(), -pos.func_177956_o(), -pos.func_177952_p())
            );
            if (VoxelShapes.func_197879_c(playerVoxel, state.func_196954_c(world, pos), IBooleanFunction.field_223238_i_)) {
               RegistryKey<World> destinationKey = world.func_234923_W_() == Vault.VAULT_KEY ? World.field_234918_g_ : Vault.VAULT_KEY;
               ServerWorld destination = ((ServerWorld)world).func_73046_m().func_71218_a(destinationKey);
               if (destination != null) {
                  ServerPlayerEntity player = (ServerPlayerEntity)entity;
                  TileEntity te = world.func_175625_s(pos);
                  VaultPortalTileEntity portal = te instanceof VaultPortalTileEntity ? (VaultPortalTileEntity)te : null;
                  if (player.func_242280_ah()) {
                     player.func_242279_ag();
                  } else {
                     if (destinationKey == World.field_234918_g_) {
                        VaultRaid vault = VaultRaidData.get(destination).getActiveFor(player);
                        if (vault == null) {
                           VaultUtils.exitSafely(destination, player);
                           player.func_242279_ag();
                        }
                     } else if (destinationKey == Vault.VAULT_KEY && portal != null) {
                        CrystalData data = portal.getData();
                        VaultRaid.Builder builder = portal.getData().createVault(destination, player);
                        if (builder != null) {
                           VaultRaid vault = VaultRaidData.get(destination).startVault(destination, builder);
                           if (CrystalData.shouldForceCowVault(data)) {
                              vault.getProperties().create(VaultRaid.COW_VAULT, true);
                              data.clearModifiers();
                              data.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
                              VaultCowOverrides.setupVault(vault);
                           }

                           world.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
                           List<BlockPos> frame = VaultPortalSize.getFrame(world, pos);
                           frame.forEach(frameBlock -> world.func_180501_a(frameBlock, Blocks.field_235406_np_.func_176223_P(), 11));
                        }

                        player.func_242279_ag();
                     }
                  }
               }
            }
         }
      }
   }

   public BlockState func_196271_a(BlockState state, Direction facing, BlockState facingState, IWorld iworld, BlockPos currentPos, BlockPos facingPos) {
      if (!(iworld instanceof ServerWorld)) {
         return state;
      } else if (((World)iworld).func_234923_W_() == Vault.VAULT_KEY) {
         return state;
      } else {
         Axis facingAxis = facing.func_176740_k();
         Axis portalAxis = (Axis)state.func_177229_b(field_176550_a);
         boolean flag = portalAxis != facingAxis && facingAxis.func_176722_c();
         return !flag && !facingState.func_203425_a(this) && !new VaultPortalSize(iworld, currentPos, portalAxis, FRAME).validatePortal()
            ? Blocks.field_150350_a.func_176223_P()
            : super.func_196271_a(state, facing, facingState, iworld, currentPos, facingPos);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_180655_c(BlockState state, World world, BlockPos pos, Random rand) {
      for (int i = 0; i < 4; i++) {
         double d0 = pos.func_177958_n() + rand.nextDouble();
         double d1 = pos.func_177956_o() + rand.nextDouble();
         double d2 = pos.func_177952_p() + rand.nextDouble();
         double d3 = (rand.nextFloat() - 0.5) * 0.5;
         double d4 = (rand.nextFloat() - 0.5) * 0.5;
         double d5 = (rand.nextFloat() - 0.5) * 0.5;
         int j = rand.nextInt(2) * 2 - 1;
         if (!world.func_180495_p(pos.func_177976_e()).func_203425_a(this) && !world.func_180495_p(pos.func_177974_f()).func_203425_a(this)) {
            d0 = pos.func_177958_n() + 0.5 + 0.25 * j;
            d3 = rand.nextFloat() * 2.0F * j;
         } else {
            d2 = pos.func_177952_p() + 0.5 + 0.25 * j;
            d5 = rand.nextFloat() * 2.0F * j;
         }

         world.func_195594_a(ParticleTypes.field_239813_am_, d0, d1, d2, d3, d4, d5);
      }
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.VAULT_PORTAL_TILE_ENTITY.func_200968_a();
   }

   public static enum Style implements IStringSerializable {
      RAINBOW,
      FINAL,
      VELARA,
      TENOS,
      WENDARR,
      IDONA;

      public String func_176610_l() {
         return this.name().toLowerCase();
      }
   }
}
