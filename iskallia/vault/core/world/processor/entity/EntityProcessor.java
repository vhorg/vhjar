package iskallia.vault.core.world.processor.entity;

import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.processor.Processor;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class EntityProcessor implements Processor<PartialEntity> {
   public static WeightedEntityProcessor ofWeighted() {
      return new WeightedEntityProcessor();
   }

   public static MirrorEntityProcessor mirror(Mirror mirror, int plane, boolean centered) {
      return new MirrorEntityProcessor(mirror, plane, centered);
   }

   public static MirrorEntityProcessor mirror(Mirror mirror, int planeX, int planeZ, boolean centered) {
      int plane = switch (mirror) {
         case FRONT_BACK -> planeX;
         case LEFT_RIGHT -> planeZ;
         case NONE -> 0;
         default -> throw new IncompatibleClassChangeError();
      };
      return mirror(mirror, plane, centered);
   }

   public static MirrorEntityProcessor mirror(Mirror mirror, Vec3i planes, boolean centered) {
      return mirror(mirror, planes.getX(), planes.getZ(), centered);
   }

   public static MirrorEntityProcessor mirrorRandomly(RandomSource random, int plane, boolean centered) {
      Mirror mirror = Mirror.values()[random.nextInt(Mirror.values().length)];
      return mirror(mirror, plane, centered);
   }

   public static MirrorEntityProcessor mirrorRandomly(RandomSource random, int planeX, int planeZ, boolean centered) {
      Mirror mirror = Mirror.values()[random.nextInt(Mirror.values().length)];
      return mirror(mirror, planeX, planeZ, centered);
   }

   public static MirrorEntityProcessor mirrorRandomly(RandomSource random, Vec3i planes, boolean centered) {
      return mirrorRandomly(random, planes.getX(), planes.getZ(), centered);
   }

   public static RotateEntityProcessor rotate(Rotation rotation, int pivotX, int pivotZ, boolean centered) {
      return new RotateEntityProcessor(rotation, pivotX, pivotZ, centered);
   }

   public static RotateEntityProcessor rotate(Rotation rotation, Vec3i pivot, boolean centered) {
      return rotate(rotation, pivot.getX(), pivot.getZ(), centered);
   }

   public static RotateEntityProcessor rotateRandomly(RandomSource random, int pivotX, int pivotZ, boolean centered) {
      Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
      return rotate(rotation, pivotX, pivotZ, centered);
   }

   public static RotateEntityProcessor rotateRandomly(RandomSource random, Vec3i pivot, boolean centered) {
      Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
      return rotate(rotation, pivot.getX(), pivot.getZ(), centered);
   }

   public static TranslateEntityProcessor translate(int offsetX, int offsetY, int offsetZ) {
      return new TranslateEntityProcessor(offsetX, offsetY, offsetZ);
   }

   public static TranslateEntityProcessor translate(Vec3i offset) {
      return new TranslateEntityProcessor(offset.getX(), offset.getY(), offset.getZ());
   }

   public static BoundedEntityProcessor bound(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      return new BoundedEntityProcessor(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public static BoundedEntityProcessor bound(BoundingBox box) {
      return bound(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
   }
}
