package iskallia.vault.core.world.processor.entity;

import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.processor.ProcessorContext;

public class BoundedEntityProcessor extends EntityProcessor {
   public final int minX;
   public final int minY;
   public final int minZ;
   public final int maxX;
   public final int maxY;
   public final int maxZ;

   public BoundedEntityProcessor(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   public PartialEntity process(PartialEntity entity, ProcessorContext context) {
      if (entity.getBlockPos().getX() < this.minX) {
         return null;
      } else if (entity.getBlockPos().getY() < this.minY) {
         return null;
      } else if (entity.getBlockPos().getZ() < this.minZ) {
         return null;
      } else if (entity.getBlockPos().getX() > this.maxX) {
         return null;
      } else if (entity.getBlockPos().getY() > this.maxY) {
         return null;
      } else {
         return entity.getBlockPos().getZ() > this.maxZ ? null : entity;
      }
   }
}
