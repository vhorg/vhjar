package iskallia.vault.core.vault.player;

import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.EntityState;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class ListenersLogic extends DataObject<ListenersLogic> implements ISupplierKey<ListenersLogic> {
   public static final FieldRegistry FIELDS = new FieldRegistry();

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public abstract void initServer(VirtualWorld var1, Vault var2);

   public abstract void tickServer(VirtualWorld var1, Vault var2, Map<UUID, Listener> var3);

   public abstract void releaseServer();

   public abstract boolean onJoin(VirtualWorld var1, Vault var2, Listener var3);

   public abstract boolean onLeave(VirtualWorld var1, Vault var2, Listener var3);

   protected void keepInVault(VirtualWorld world, Vault vault, Collection<Listener> listeners) {
      listeners.forEach(listener -> listener.getPlayer().ifPresent(player -> {
         if (player.level != world) {
            vault.ifPresent(Vault.WORLD, manager -> {
               if (manager.get(WorldManager.PORTAL_LOGIC) instanceof ClassicPortalLogic logic) {
                  logic.getPlayerStart(world, vault).ifPresent(state -> {
                     if (state.isLoaded()) {
                        this.teleportSafe(state, manager.get(WorldManager.FACING), player);
                        this.onTeleport(world, vault, player);
                     }
                  });
               }
            });
         }
      }));
   }

   private void teleportSafe(EntityState state, Direction directionHint, ServerPlayer player) {
      AABB box = player.dimensions.makeBoundingBox(Vec3.ZERO);
      BlockPos startPos = state.getBlockPos();
      if (!state.isColliding(box)) {
         state.teleport(player);
      } else {
         int moveLimit = startPos.get(directionHint.getAxis());

         for (BlockPos offset : BlockPos.spiralAround(startPos, 5, directionHint, directionHint.getClockWise())) {
            int moveOffset = offset.get(directionHint.getAxis());
            int diff = (moveLimit - moveOffset) * directionHint.getNormal().get(directionHint.getAxis());
            if (diff <= 0 && !state.isColliding(box)) {
               state.teleport(player);
               return;
            }
         }

         state.teleport(player);
      }
   }

   protected void onTeleport(VirtualWorld world, Vault vault, ServerPlayer player) {
   }

   protected void recallToJoinState(Stream<Listener> listeners) {
      listeners.forEach(listener -> {
         EntityState state = listener.get(Listener.JOIN_STATE);
         ServerPlayer player = listener.getPlayer().orElse(null);
         if (state != null && player != null) {
            state.teleport(player);
            player.setPortalCooldown();
         }
      });
   }
}
