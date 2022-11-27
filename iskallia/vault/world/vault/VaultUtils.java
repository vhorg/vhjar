package iskallia.vault.world.vault;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

public class VaultUtils {
   public static void exitSafely(final ServerLevel world, final ServerPlayer player) {
      BlockPos rawSpawnPoint = player.getRespawnPosition();
      final Optional<Vec3> spawnPoint = rawSpawnPoint != null
         ? Player.findRespawnPositionAndUseSpawnBlock(world, rawSpawnPoint, player.getRespawnAngle(), player.isRespawnForced(), true)
         : Optional.empty();
      ResourceKey<Level> targetDim = world.dimension();
      ResourceKey<Level> sourceDim = player.getCommandSenderWorld().dimension();
      if (!targetDim.equals(sourceDim)) {
         player.changeDimension(
            world,
            new ITeleporter() {
               public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                  Entity repositionedEntity = repositionEntity.apply(false);
                  if (spawnPoint.isPresent()) {
                     Vec3 spawnPos = spawnPoint.get();
                     repositionedEntity.teleportTo(spawnPos.x(), spawnPos.y(), spawnPos.z());
                  } else {
                     VaultUtils.moveToWorldSpawn(world, player);
                  }

                  if (repositionedEntity instanceof ServerPlayer) {
                     ((ServerPlayer)repositionedEntity)
                        .getLevel()
                        .getServer()
                        .tell(new TickTask(20, () -> ((ServerPlayer)repositionedEntity).giveExperiencePoints(0)));
                  }

                  return repositionedEntity;
               }

               public boolean playTeleportSound(ServerPlayer playerx, ServerLevel sourceWorld, ServerLevel destWorld) {
                  return false;
               }
            }
         );
      } else if (spawnPoint.isPresent()) {
         BlockState blockstate = world.getBlockState(rawSpawnPoint);
         Vec3 spawnPos = spawnPoint.get();
         if (!blockstate.is(BlockTags.BEDS) && !blockstate.is(Blocks.RESPAWN_ANCHOR)) {
            player.teleportTo(world, spawnPos.x, spawnPos.y, spawnPos.z, player.getRespawnAngle(), 0.0F);
         } else {
            Vec3 vector3d1 = Vec3.atBottomCenterOf(rawSpawnPoint).subtract(spawnPos).normalize();
            player.teleportTo(
               world, spawnPos.x, spawnPos.y, spawnPos.z, (float)Mth.wrapDegrees(Mth.atan2(vector3d1.z, vector3d1.x) * (180.0 / Math.PI) - 90.0), 0.0F
            );
         }

         player.teleportTo(spawnPos.x, spawnPos.y, spawnPos.z);
      } else {
         moveToWorldSpawn(world, player);
      }
   }

   public static <T extends Entity> void changeDimension(
      ServerLevel world, T entity, final Vec3 position, final Vec3 velocity, final float yaw, final float pitch, final Consumer<T> runnable
   ) {
      final MinecraftServer server = world.getServer();
      entity.changeDimension(world, new ITeleporter() {
         public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yawx, Function<Boolean, Entity> repositionEntity) {
            Entity repositionedEntity = repositionEntity.apply(false);
            if (repositionedEntity instanceof ServerPlayer player) {
               server.tell(new TickTask(server.getTickCount() + 20, () -> player.giveExperiencePoints(0)));
            }

            runnable.accept((T)repositionedEntity);
            return repositionedEntity;
         }

         public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
            return destWorld.dimension() == Level.OVERWORLD;
         }

         @Nullable
         public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
            return new PortalInfo(position, velocity, yaw, pitch);
         }
      });
   }

   public static void moveTo(ServerLevel world, Entity entity, final Vec3 pos, Vec2 rotation) {
      entity.changeDimension(
         world,
         new ITeleporter() {
            public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
               Entity repositionedEntity = repositionEntity.apply(false);
               repositionedEntity.teleportTo(pos.x, pos.y, pos.z);
               if (repositionedEntity instanceof ServerPlayer) {
                  ((ServerPlayer)repositionedEntity)
                     .getLevel()
                     .getServer()
                     .tell(new TickTask(20, () -> ((ServerPlayer)repositionedEntity).giveExperiencePoints(0)));
               }

               return repositionedEntity;
            }
         }
      );
   }

   public static void moveToWorldSpawn(ServerLevel world, ServerPlayer player) {
      BlockPos blockpos = world.getSharedSpawnPos();
      if (world.dimensionType().hasSkyLight() && world.getServer().getWorldData().getGameType() != GameType.ADVENTURE) {
         int i = Math.max(0, world.getServer().getSpawnRadius(world));
         int j = Mth.floor(world.getWorldBorder().getDistanceToBorder(blockpos.getX(), blockpos.getZ()));
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
            BlockPos pos = new BlockPos(blockpos.getX() + j2 - i, 0, blockpos.getZ() + k2 - i);
            OptionalInt height = getSpawnHeight(world, pos.getX(), pos.getZ());
            if (height.isPresent()) {
               player.teleportTo(world, pos.getX(), height.getAsInt(), pos.getZ(), 0.0F, 0.0F);
               player.teleportTo(pos.getX(), height.getAsInt(), pos.getZ());
               if (world.noCollision(player)) {
                  break;
               }
            }
         }
      } else {
         player.teleportTo(world, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 0.0F, 0.0F);
         player.teleportTo(blockpos.getX(), blockpos.getY(), blockpos.getZ());

         while (!world.noCollision(player) && player.getY() < 255.0) {
            player.teleportTo(world, player.getX(), player.getY() + 1.0, player.getZ(), 0.0F, 0.0F);
            player.teleportTo(player.getX(), player.getY(), player.getZ());
         }
      }
   }

   public static OptionalInt getSpawnHeight(ServerLevel world, int posX, int posZ) {
      MutableBlockPos pos = new MutableBlockPos(posX, 0, posZ);
      LevelChunk chunk = world.getChunk(posX >> 4, posZ >> 4);
      int top = world.dimensionType().hasCeiling()
         ? world.getChunkSource().getGenerator().getSpawnHeight(world)
         : chunk.getHeight(Types.MOTION_BLOCKING, posX & 15, posZ & 15);
      if (top >= 0) {
         int j = chunk.getHeight(Types.WORLD_SURFACE, posX & 15, posZ & 15);
         if (j > top || j <= chunk.getHeight(Types.OCEAN_FLOOR, posX & 15, posZ & 15)) {
            for (int k = top + 1; k >= 0; k--) {
               pos.set(posX, k, posZ);
               BlockState state = world.getBlockState(pos);
               if (!state.getFluidState().isEmpty()) {
                  break;
               }
            }
         }
      }

      return OptionalInt.empty();
   }

   public static boolean matchesDimension(VaultRaid vault, Level world) {
      return vault.getProperties().getBase(VaultRaid.DIMENSION).filter(key -> key == world.dimension()).isPresent();
   }

   public static boolean inVault(VaultRaid vault, Entity entity) {
      return inVault(vault, entity.getCommandSenderWorld(), entity.blockPosition());
   }

   public static boolean inVault(VaultRaid vault, Level world, BlockPos pos) {
      if (vault == null) {
         return false;
      } else {
         Optional<ResourceKey<Level>> dimension = vault.getProperties().getBase(VaultRaid.DIMENSION);
         return dimension.isPresent() && world.dimension() == dimension.get()
            ? vault.getProperties().getBase(VaultRaid.BOUNDING_BOX).map(box -> box.isInside(pos)).orElse(false)
            : false;
      }
   }

   public static boolean inVault(ServerPlayer player) {
      return true;
   }
}
