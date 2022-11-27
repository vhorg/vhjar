package iskallia.vault.block.entity;

import com.google.common.collect.MapMaker;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import java.util.Collections;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DemagnetizerTileEntity extends BlockEntity {
   private static final Set<DemagnetizerTileEntity> LOADED_DEMAGNETIZERS = Collections.newSetFromMap(new MapMaker().concurrencyLevel(2).weakKeys().makeMap());

   public DemagnetizerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.DEMAGNETIZER_TILE_ENTITY, pos, state);
   }

   public void onLoad() {
      super.onLoad();
      if (!LOADED_DEMAGNETIZERS.contains(this)) {
         LOADED_DEMAGNETIZERS.add(this);
      }
   }

   public void setRemoved() {
      super.setRemoved();
      LOADED_DEMAGNETIZERS.remove(this);
   }

   public static boolean hasDemagnetizerAround(Entity e) {
      int radius = ModConfigs.MAGNET_CONFIG.getDemagnetizerRadius();
      int r = radius * radius;
      return LOADED_DEMAGNETIZERS.stream()
         .filter(f -> f.getLevel() == e.level)
         .anyMatch(f -> f.getBlockPos().distToCenterSqr(e.getX(), e.getY(), e.getZ()) <= r);
   }
}
