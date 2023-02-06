package iskallia.vault.core.vault;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ClassicPortalLogic extends PortalLogic {
   public static final SupplierKey<PortalLogic> KEY = SupplierKey.of("classic", PortalLogic.class).with(Version.v1_0, ClassicPortalLogic::new);
   public static final ResourceLocation ENTRANCE = VaultMod.id("entrance");
   public static final ResourceLocation EXIT = VaultMod.id("exit");
   public static final FieldRegistry FIELDS = PortalLogic.FIELDS.merge(new FieldRegistry());

   @Override
   public SupplierKey<PortalLogic> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public Optional<BlockPos> getStart(VirtualWorld world, Vault vault) {
      return this.getPortals(ENTRANCE).findAny().map(portalData -> {
         BlockPos min = portalData.get(PortalData.MIN);
         BlockPos max = portalData.get(PortalData.MAX);
         return new BlockPos((min.getX() + max.getX()) / 2, min.getY(), (min.getZ() + max.getZ()) / 2);
      });
   }

   public Optional<EntityState> getPlayerStart(Level world, Vault vault) {
      return this.getPlayerStartPos(vault)
         .map(
            pos -> {
               Direction facing = vault.get(Vault.WORLD).get(WorldManager.FACING);
               return new EntityState()
                  .set(EntityState.POS_X, Double.valueOf(pos.getX() + 0.5))
                  .set(EntityState.POS_Y, Double.valueOf((double)pos.getY()))
                  .set(EntityState.POS_Z, Double.valueOf(pos.getZ() + 0.5))
                  .set(EntityState.PITCH, Float.valueOf(0.0F))
                  .set(EntityState.YAW, Float.valueOf(facing.toYRot()))
                  .set(EntityState.WORLD, world.dimension());
            }
         );
   }

   public Optional<BlockPos> getPlayerStartPos(Vault vault) {
      return this.getPortals(ENTRANCE).findAny().map(portalData -> {
         BlockPos min = portalData.get(PortalData.MIN);
         BlockPos max = portalData.get(PortalData.MAX);
         BlockPos pos = new BlockPos((min.getX() + max.getX()) / 2, min.getY(), (min.getZ() + max.getZ()) / 2);
         Direction facing = vault.get(Vault.WORLD).get(WorldManager.FACING);
         return pos.relative(facing, 5);
      });
   }
}
