package iskallia.vault.world.vault;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

public class VaultUtils {
   public static void exitSafely(final ServerWorld world, final ServerPlayerEntity player) {
      BlockPos rawSpawnPoint = player.func_241140_K_();
      final Optional<Vector3d> spawnPoint = rawSpawnPoint != null
         ? PlayerEntity.func_242374_a(world, rawSpawnPoint, player.func_242109_L(), player.func_241142_M_(), true)
         : Optional.empty();
      RegistryKey<World> targetDim = world.func_234923_W_();
      RegistryKey<World> sourceDim = player.func_130014_f_().func_234923_W_();
      if (!targetDim.equals(sourceDim)) {
         player.changeDimension(
            world,
            new ITeleporter() {
               public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                  Entity repositionedEntity = repositionEntity.apply(false);
                  if (spawnPoint.isPresent()) {
                     Vector3d spawnPos = spawnPoint.get();
                     repositionedEntity.func_70634_a(spawnPos.func_82615_a(), spawnPos.func_82617_b(), spawnPos.func_82616_c());
                  } else {
                     VaultUtils.moveToWorldSpawn(world, player);
                  }

                  if (repositionedEntity instanceof ServerPlayerEntity) {
                     ((ServerPlayerEntity)repositionedEntity)
                        .func_71121_q()
                        .func_73046_m()
                        .func_212871_a_(new TickDelayedTask(20, () -> ((ServerPlayerEntity)repositionedEntity).func_195068_e(0)));
                  }

                  return repositionedEntity;
               }
            }
         );
      } else if (spawnPoint.isPresent()) {
         BlockState blockstate = world.func_180495_p(rawSpawnPoint);
         Vector3d spawnPos = spawnPoint.get();
         if (!blockstate.func_235714_a_(BlockTags.field_219747_F) && !blockstate.func_203425_a(Blocks.field_235400_nj_)) {
            player.func_200619_a(world, spawnPos.field_72450_a, spawnPos.field_72448_b, spawnPos.field_72449_c, player.func_242109_L(), 0.0F);
         } else {
            Vector3d vector3d1 = Vector3d.func_237492_c_(rawSpawnPoint).func_178788_d(spawnPos).func_72432_b();
            player.func_200619_a(
               world,
               spawnPos.field_72450_a,
               spawnPos.field_72448_b,
               spawnPos.field_72449_c,
               (float)MathHelper.func_76138_g(MathHelper.func_181159_b(vector3d1.field_72449_c, vector3d1.field_72450_a) * (180.0 / Math.PI) - 90.0),
               0.0F
            );
         }

         player.func_70634_a(spawnPos.field_72450_a, spawnPos.field_72448_b, spawnPos.field_72449_c);
      } else {
         moveToWorldSpawn(world, player);
      }
   }

   public static void moveToWorldSpawn(ServerWorld world, ServerPlayerEntity player) {
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
            BlockPos pos = new BlockPos(blockpos.func_177958_n() + j2 - i, 0, blockpos.func_177952_p() + k2 - i);
            OptionalInt height = getSpawnHeight(world, pos.func_177958_n(), pos.func_177952_p());
            if (height.isPresent()) {
               player.func_200619_a(world, pos.func_177958_n(), height.getAsInt(), pos.func_177952_p(), 0.0F, 0.0F);
               player.func_70634_a(pos.func_177958_n(), height.getAsInt(), pos.func_177952_p());
               if (world.func_226669_j_(player)) {
                  break;
               }
            }
         }
      } else {
         player.func_200619_a(world, blockpos.func_177958_n(), blockpos.func_177956_o(), blockpos.func_177952_p(), 0.0F, 0.0F);
         player.func_70634_a(blockpos.func_177958_n(), blockpos.func_177956_o(), blockpos.func_177952_p());

         while (!world.func_226669_j_(player) && player.func_226278_cu_() < 255.0) {
            player.func_200619_a(world, player.func_226277_ct_(), player.func_226278_cu_() + 1.0, player.func_226281_cx_(), 0.0F, 0.0F);
            player.func_70634_a(player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_());
         }
      }
   }

   public static OptionalInt getSpawnHeight(ServerWorld world, int posX, int posZ) {
      Mutable pos = new Mutable(posX, 0, posZ);
      Chunk chunk = world.func_212866_a_(posX >> 4, posZ >> 4);
      int top = world.func_230315_m_().func_236037_d_()
         ? world.func_72863_F().func_201711_g().func_205470_d()
         : chunk.func_201576_a(Type.MOTION_BLOCKING, posX & 15, posZ & 15);
      if (top >= 0) {
         int j = chunk.func_201576_a(Type.WORLD_SURFACE, posX & 15, posZ & 15);
         if (j > top || j <= chunk.func_201576_a(Type.OCEAN_FLOOR, posX & 15, posZ & 15)) {
            for (int k = top + 1; k >= 0; k--) {
               pos.func_181079_c(posX, k, posZ);
               BlockState state = world.func_180495_p(pos);
               if (!state.func_204520_s().func_206888_e()) {
                  break;
               }

               if (state.equals(world.func_226691_t_(pos).func_242440_e().func_242502_e().func_204108_a())) {
                  return OptionalInt.of(pos.func_177984_a().func_177956_o());
               }
            }
         }
      }

      return OptionalInt.empty();
   }

   public static boolean matchesDimension(VaultRaid vault, World world) {
      return vault.getProperties().getBase(VaultRaid.DIMENSION).filter(key -> key == world.func_234923_W_()).isPresent();
   }

   public static boolean inVault(VaultRaid vault, Entity entity) {
      return inVault(vault, entity.func_130014_f_(), entity.func_233580_cy_());
   }

   public static boolean inVault(VaultRaid vault, World world, BlockPos pos) {
      if (vault == null) {
         return false;
      } else {
         Optional<RegistryKey<World>> dimension = vault.getProperties().getBase(VaultRaid.DIMENSION);
         return dimension.isPresent() && world.func_234923_W_() == dimension.get()
            ? vault.getProperties().getBase(VaultRaid.BOUNDING_BOX).map(box -> box.func_175898_b(pos)).orElse(false)
            : false;
      }
   }
}
