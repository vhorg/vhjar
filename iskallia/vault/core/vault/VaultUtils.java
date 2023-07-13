package iskallia.vault.core.vault;

import iskallia.vault.world.data.ServerVaults;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

public class VaultUtils {
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

   public static boolean isVaultLevel(Level level) {
      return level.isClientSide ? ClientVaults.getActive().isPresent() : ServerVaults.get(level).isPresent();
   }
}
