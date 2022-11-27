package iskallia.vault.core.world.processor.entity;

import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class TranslateEntityProcessor extends EntityProcessor {
   public final int offsetX;
   public final int offsetY;
   public final int offsetZ;

   public TranslateEntityProcessor(int offsetX, int offsetY, int offsetZ) {
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.offsetZ = offsetZ;
   }

   public BlockPos translate(BlockPos pos) {
      return new BlockPos(pos.getX() + this.offsetX, pos.getY() + this.offsetY, pos.getZ() + this.offsetZ);
   }

   public Vec3 translate(Vec3 pos) {
      return new Vec3(pos.x + this.offsetX, pos.y + this.offsetY, pos.z + this.offsetZ);
   }

   public PartialEntity process(PartialEntity entity, ProcessorContext context) {
      entity.setBlockPos(this.translate(entity.getBlockPos()));
      entity.setPos(this.translate(entity.getPos()));
      return entity;
   }
}
