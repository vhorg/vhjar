package iskallia.vault.core.world.processor.entity;

import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

public class RotateEntityProcessor extends EntityProcessor {
   public final Rotation rotation;
   public final int pivotX;
   public final int pivotZ;
   public final boolean centered;
   private final int termX;
   private final int termZ;

   public RotateEntityProcessor(Rotation rotation, int pivotX, int pivotZ, boolean centered) {
      this.rotation = rotation;
      this.pivotX = pivotX;
      this.pivotZ = pivotZ;
      this.centered = centered;
      switch (this.rotation) {
         case COUNTERCLOCKWISE_90:
            this.termX = this.pivotX - this.pivotZ - (this.centered ? 0 : 1);
            this.termZ = this.pivotX + this.pivotZ;
            break;
         case CLOCKWISE_90:
            this.termX = this.pivotX + this.pivotZ;
            this.termZ = this.pivotZ - this.pivotX - (this.centered ? 0 : 1);
            break;
         case CLOCKWISE_180:
            this.termX = this.pivotX + this.pivotX - (this.centered ? 0 : 1);
            this.termZ = this.pivotZ + this.pivotZ - (this.centered ? 0 : 1);
            break;
         default:
            this.termX = 0;
            this.termZ = 0;
      }
   }

   public BlockPos transform(BlockPos pos) {
      return switch (this.rotation) {
         case COUNTERCLOCKWISE_90 -> new BlockPos(this.termX + pos.getZ(), pos.getY(), this.termZ - pos.getX());
         case CLOCKWISE_90 -> new BlockPos(this.termX - pos.getZ(), pos.getY(), this.termZ + pos.getX());
         case CLOCKWISE_180 -> new BlockPos(this.termX - pos.getX(), pos.getY(), this.termZ - pos.getZ());
         default -> pos;
      };
   }

   public Vec3 transform(Vec3 pos) {
      return switch (this.rotation) {
         case COUNTERCLOCKWISE_90 -> new Vec3(this.termX + pos.z, pos.y, this.termZ - pos.x + 1.0);
         case CLOCKWISE_90 -> new Vec3(this.termX - pos.z + 1.0, pos.y, this.termZ + pos.x);
         case CLOCKWISE_180 -> new Vec3(this.termX - pos.x + 1.0, pos.y, this.termZ - pos.z + 1.0);
         default -> pos;
      };
   }

   public float transformEntity(float yaw) {
      yaw = Mth.wrapDegrees(yaw);

      return switch (this.rotation) {
         case COUNTERCLOCKWISE_90 -> yaw + 270.0F;
         case CLOCKWISE_90 -> yaw + 90.0F;
         case CLOCKWISE_180 -> yaw + 180.0F;
         default -> yaw;
      };
   }

   public Tuple<Float, Direction> transformHangingEntity(float yaw, Direction direction) {
      if (direction.getAxis() != Axis.Y) {
         direction = switch (this.rotation) {
            case COUNTERCLOCKWISE_90 -> direction.getCounterClockWise();
            case CLOCKWISE_90 -> direction.getClockWise();
            case CLOCKWISE_180 -> direction.getOpposite();
            default -> direction;
         };
      }

      yaw = Mth.wrapDegrees(yaw);

      yaw = switch (this.rotation) {
         case COUNTERCLOCKWISE_90 -> yaw + 90.0F;
         case CLOCKWISE_90 -> yaw + 270.0F;
         case CLOCKWISE_180 -> yaw + 180.0F;
         default -> yaw;
      };
      return new Tuple(yaw, direction);
   }

   public PartialEntity process(PartialEntity entity, ProcessorContext context) {
      entity.setBlockPos(this.transform(entity.getBlockPos()));
      entity.setPos(this.transform(entity.getPos()));
      CompoundTag nbt = entity.getNbt().asWhole().orElse(null);
      if (nbt != null && nbt.contains("Rotation", 5)) {
         ListTag rotation = nbt.getList("Rotation", 5);
         float yaw = rotation.getFloat(0);
         EntityType<?> type = EntityType.by(nbt).orElse(EntityType.ARMOR_STAND);
         if (type == EntityType.ITEM_FRAME || type == EntityType.GLOW_ITEM_FRAME) {
            Direction direction = Direction.from3DDataValue(nbt.getByte("Facing"));
            Tuple<Float, Direction> result = this.transformHangingEntity(yaw, direction);
            yaw = (Float)result.getA();
            direction = (Direction)result.getB();
            nbt.putByte("Facing", (byte)direction.get3DDataValue());
         } else if (type == EntityType.PAINTING) {
            Direction direction = Direction.from2DDataValue(nbt.getByte("Facing"));
            Tuple<Float, Direction> result = this.transformHangingEntity(yaw, direction);
            yaw = (Float)result.getA();
            direction = (Direction)result.getB();
            nbt.putByte("Facing", (byte)direction.get2DDataValue());
         } else if (type == EntityType.LEASH_KNOT) {
            yaw = (Float)this.transformHangingEntity(yaw, Direction.SOUTH).getA();
         } else {
            yaw = this.transformEntity(yaw);
         }

         rotation.set(0, FloatTag.valueOf(yaw - rotation.getFloat(0)));
      }

      return entity;
   }
}
