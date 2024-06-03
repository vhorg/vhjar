package iskallia.vault.block.entity.hologram;

import iskallia.vault.block.HologramBlock;
import iskallia.vault.block.entity.HologramTileEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class RootHologramElement extends HologramElement {
   private HologramTileEntity entity;

   public RootHologramElement() {
   }

   public RootHologramElement(HologramTileEntity entity) {
      this.entity = entity;
   }

   public HologramTileEntity getEntity() {
      return this.entity;
   }

   public void setEntity(HologramTileEntity entity) {
      this.entity = entity;
   }

   @Override
   public Vec3 getEulerRotation() {
      return switch ((Direction)this.entity.getBlockState().getOptionalValue(HologramBlock.FACING).orElse(Direction.SOUTH)) {
         case SOUTH -> this.rotation;
         case NORTH -> this.rotation.add(0.0, 180.0, 0.0);
         case EAST -> this.rotation.add(0.0, -90.0, 0.0);
         case WEST -> this.rotation.add(0.0, 90.0, 0.0);
         case UP -> this.rotation.add(90.0, 0.0, 0.0);
         case DOWN -> this.rotation.add(-90.0, 0.0, 0.0);
         default -> throw new IncompatibleClassChangeError();
      };
   }
}
