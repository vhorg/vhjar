package iskallia.vault.block;

import iskallia.vault.Vault;
import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultEscapeMessage;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.raid.VaultRaid;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;

public class VaultPortalBlock extends NetherPortalBlock {
   public static final IntegerProperty RARITY = IntegerProperty.func_177719_a("rarity", 0, VaultRarity.values().length - 1);

   public VaultPortalBlock() {
      super(Properties.func_200950_a(Blocks.field_150427_aO));
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176550_a, Axis.X))
            .func_206870_a(RARITY, VaultRarity.COMMON.ordinal())
      );
   }

   protected static BlockPos getSpawnPoint(ServerWorld p_241092_0_, int p_241092_1_, int p_241092_2_, boolean p_241092_3_) {
      Mutable blockpos$mutable = new Mutable(p_241092_1_, 0, p_241092_2_);
      Biome biome = p_241092_0_.func_226691_t_(blockpos$mutable);
      boolean flag = p_241092_0_.func_230315_m_().func_236037_d_();
      BlockState blockstate = biome.func_242440_e().func_242502_e().func_204108_a();
      if (p_241092_3_ && !blockstate.func_177230_c().func_203417_a(BlockTags.field_205599_H)) {
         return null;
      } else {
         Chunk chunk = p_241092_0_.func_212866_a_(p_241092_1_ >> 4, p_241092_2_ >> 4);
         int i = flag
            ? p_241092_0_.func_72863_F().func_201711_g().func_205470_d()
            : chunk.func_201576_a(Type.MOTION_BLOCKING, p_241092_1_ & 15, p_241092_2_ & 15);
         if (i < 0) {
            return null;
         } else {
            int j = chunk.func_201576_a(Type.WORLD_SURFACE, p_241092_1_ & 15, p_241092_2_ & 15);
            if (j <= i && j > chunk.func_201576_a(Type.OCEAN_FLOOR, p_241092_1_ & 15, p_241092_2_ & 15)) {
               return null;
            } else {
               for (int k = i + 1; k >= 0; k--) {
                  blockpos$mutable.func_181079_c(p_241092_1_, k, p_241092_2_);
                  BlockState blockstate1 = p_241092_0_.func_180495_p(blockpos$mutable);
                  if (!blockstate1.func_204520_s().func_206888_e()) {
                     break;
                  }

                  if (blockstate1.equals(blockstate)) {
                     return blockpos$mutable.func_177984_a().func_185334_h();
                  }
               }

               return null;
            }
         }
      }
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.VAULT_PORTAL_TILE_ENTITY.func_200968_a();
   }

   public void func_225542_b_(BlockState state, ServerWorld world, BlockPos pos, Random random) {
   }

   public BlockState func_196271_a(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      World world = null;
      if (worldIn instanceof World) {
         world = (World)worldIn;
      }

      if (world != null && world.func_234923_W_() == World.field_234918_g_) {
         Axis direction$axis = facing.func_176740_k();
         Axis direction$axis1 = (Axis)stateIn.func_177229_b(field_176550_a);
         boolean flag = direction$axis1 != direction$axis && direction$axis.func_176722_c();
         return !flag && !facingState.func_203425_a(this) && !new VaultPortalSize(worldIn, currentPos, direction$axis1).validatePortal()
            ? Blocks.field_150350_a.func_176223_P()
            : super.func_196271_a(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         return stateIn;
      }
   }

   public void func_196262_a(BlockState state, World world, BlockPos pos, Entity entity) {
      if (!world.field_72995_K && entity instanceof PlayerEntity) {
         if (!entity.func_184218_aH() && !entity.func_184207_aI() && entity.func_184222_aU()) {
            ServerPlayerEntity player = (ServerPlayerEntity)entity;
            VoxelShape playerVoxel = VoxelShapes.func_197881_a(
               player.func_174813_aQ().func_72317_d(-pos.func_177958_n(), -pos.func_177956_o(), -pos.func_177952_p())
            );
            VaultPortalTileEntity portal = this.getPortalTileEntity(world, pos);
            String playerBossName = portal == null ? "" : portal.getPlayerBossName();
            if (VoxelShapes.func_197879_c(playerVoxel, state.func_196954_c(world, pos), IBooleanFunction.field_223238_i_)) {
               RegistryKey<World> worldKey = world.func_234923_W_() == Vault.VAULT_KEY ? World.field_234918_g_ : Vault.VAULT_KEY;
               ServerWorld destination = ((ServerWorld)world).func_73046_m().func_71218_a(worldKey);
               if (destination == null) {
                  return;
               }

               if (player.func_242280_ah()) {
                  player.func_242279_ag();
                  return;
               }

               world.func_73046_m()
                  .func_222817_e(
                     () -> {
                        if (worldKey == World.field_234918_g_) {
                           ServerPlayerEntity playerEntity = (ServerPlayerEntity)entity;
                           VaultRaid raid = VaultRaidData.get(destination).getActiveFor(playerEntity);
                           if (raid != null && raid.cannotExit) {
                              StringTextComponent text = new StringTextComponent("You cannot exit this Vault!");
                              text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)));
                              playerEntity.func_146105_b(text, true);
                              return;
                           }

                           BlockPos blockPos = playerEntity.func_241140_K_();
                           Optional<Vector3d> spawn = blockPos == null
                              ? Optional.empty()
                              : PlayerEntity.func_242374_a(destination, blockPos, playerEntity.func_242109_L(), playerEntity.func_241142_M_(), true);
                           if (spawn.isPresent()) {
                              BlockState blockstate = destination.func_180495_p(blockPos);
                              Vector3d vector3d = spawn.get();
                              if (!blockstate.func_235714_a_(BlockTags.field_219747_F) && !blockstate.func_203425_a(Blocks.field_235400_nj_)) {
                                 playerEntity.func_200619_a(
                                    destination, vector3d.field_72450_a, vector3d.field_72448_b, vector3d.field_72449_c, playerEntity.func_242109_L(), 0.0F
                                 );
                              } else {
                                 Vector3d vector3d1 = Vector3d.func_237492_c_(blockPos).func_178788_d(vector3d).func_72432_b();
                                 playerEntity.func_200619_a(
                                    destination,
                                    vector3d.field_72450_a,
                                    vector3d.field_72448_b,
                                    vector3d.field_72449_c,
                                    (float)MathHelper.func_76138_g(
                                       MathHelper.func_181159_b(vector3d1.field_72449_c, vector3d1.field_72450_a) * 180.0F / (float)Math.PI - 90.0
                                    ),
                                    0.0F
                                 );
                              }

                              ModNetwork.CHANNEL.sendTo(new VaultEscapeMessage(), playerEntity.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
                           } else {
                              this.moveToSpawn(destination, player);
                           }
                        } else if (worldKey == Vault.VAULT_KEY) {
                           VaultRaidData.get(destination).startNew(player, (Integer)state.func_177229_b(RARITY), playerBossName, portal.getData(), false);
                        }
                     }
                  );
               if (worldKey == Vault.VAULT_KEY) {
                  world.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
               }

               player.func_242279_ag();
            }
         }
      }
   }

   private void moveToSpawn(ServerWorld world, ServerPlayerEntity player) {
      BlockPos blockpos = world.func_241135_u_();
      if (world.func_230315_m_().func_218272_d() && world.func_73046_m().func_240793_aU_().func_76077_q() != GameType.ADVENTURE) {
         int i = Math.max(0, world.func_73046_m().func_184108_a(world));
         int j = MathHelper.func_76128_c(world.func_175723_af().func_177729_b(blockpos.func_177958_n(), blockpos.func_177952_p()));
         if (j < i) {
            i = j;
         }

         if (j <= 1) {
            i = 1;
         }

         long k = i * 2 + 1;
         long l = k * k;
         int i1 = l > 2147483647L ? Integer.MAX_VALUE : (int)l;
         int j1 = i1 <= 16 ? i1 - 1 : 17;
         int k1 = new Random().nextInt(i1);

         for (int l1 = 0; l1 < i1; l1++) {
            int i2 = (k1 + j1 * l1) % i1;
            int j2 = i2 % (i * 2 + 1);
            int k2 = i2 / (i * 2 + 1);
            BlockPos blockpos1 = getSpawnPoint(world, blockpos.func_177958_n() + j2 - i, blockpos.func_177952_p() + k2 - i, false);
            if (blockpos1 != null) {
               player.func_200619_a(world, blockpos1.func_177958_n(), blockpos1.func_177956_o(), blockpos1.func_177952_p(), 0.0F, 0.0F);
               if (world.func_226669_j_(player)) {
                  break;
               }
            }
         }
      } else {
         player.func_200619_a(world, blockpos.func_177958_n(), blockpos.func_177956_o(), blockpos.func_177952_p(), 0.0F, 0.0F);

         while (!world.func_226669_j_(player) && player.func_226278_cu_() < 255.0) {
            player.func_200619_a(world, player.func_226277_ct_(), player.func_226278_cu_() + 1.0, player.func_226281_cx_(), 0.0F, 0.0F);
         }
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

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      super.func_206840_a(builder);
      builder.func_206894_a(new Property[]{RARITY});
   }

   private VaultPortalTileEntity getPortalTileEntity(World worldIn, BlockPos pos) {
      TileEntity te = worldIn.func_175625_s(pos);
      return te instanceof VaultPortalTileEntity ? (VaultPortalTileEntity)te : null;
   }
}
