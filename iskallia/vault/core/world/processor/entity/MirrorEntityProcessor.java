package iskallia.vault.core.world.processor.entity;

import iskallia.vault.core.world.data.PartialEntity;
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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

public class MirrorEntityProcessor extends EntityProcessor {
   public final Mirror mirror;
   public final int plane;
   public final boolean centered;
   private final int term;

   public MirrorEntityProcessor(Mirror mirror, int plane, boolean centered) {
      this.mirror = mirror;
      this.plane = plane;
      this.centered = centered;
      this.term = this.plane + this.plane - (this.centered ? 0 : 1);
   }

   public BlockPos transform(BlockPos pos) {
      return switch (this.mirror) {
         case FRONT_BACK -> new BlockPos(this.term - pos.getX(), pos.getY(), pos.getZ());
         case LEFT_RIGHT -> new BlockPos(pos.getX(), pos.getY(), this.term - pos.getZ());
         case NONE -> pos;
         default -> throw new IncompatibleClassChangeError();
      };
   }

   public Vec3 transform(Vec3 pos) {
      return switch (this.mirror) {
         case FRONT_BACK -> new Vec3(this.term - pos.x + 1.0, pos.y, pos.z);
         case LEFT_RIGHT -> new Vec3(pos.x, pos.y, this.term - pos.z + 1.0);
         case NONE -> pos;
         default -> throw new IncompatibleClassChangeError();
      };
   }

   public float transformEntity(float yaw) {
      yaw = Mth.wrapDegrees(yaw);

      return switch (this.mirror) {
         case FRONT_BACK -> -yaw;
         case LEFT_RIGHT -> -yaw + 180.0F;
         default -> yaw;
      };
   }

   public Tuple<Float, Direction> transformHangingEntity(float yaw, Direction direction) {
      Rotation rotation = this.mirror.getRotation(direction);
      if (direction.getAxis() != Axis.Y) {
         direction = switch (rotation) {
            case CLOCKWISE_180 -> direction.getOpposite();
            case COUNTERCLOCKWISE_90 -> direction.getCounterClockWise();
            case CLOCKWISE_90 -> direction.getClockWise();
            default -> direction;
         };
      }

      yaw = Mth.wrapDegrees(yaw);

      yaw = switch (rotation) {
         case CLOCKWISE_180 -> yaw + 180.0F;
         case COUNTERCLOCKWISE_90 -> yaw + 90.0F;
         case CLOCKWISE_90 -> yaw + 270.0F;
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

         rotation.set(0, FloatTag.valueOf(yaw + rotation.getFloat(0)));
      }

      return entity;
   }
}
