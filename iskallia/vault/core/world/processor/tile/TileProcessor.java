package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.processor.Processor;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class TileProcessor implements Processor<PartialTile> {
   public static TileProcessor of(final Processor<PartialTile> processor) {
      return new TileProcessor() {
         public PartialTile process(PartialTile value, ProcessorContext context) {
            return processor.process(value, context);
         }
      };
   }

   public static IdentityTileProcessor ofIdentity() {
      return new IdentityTileProcessor();
   }

   public static WeightedTileProcessor ofWeighted() {
      return new WeightedTileProcessor();
   }

   public static MirrorTileProcessor mirror(Mirror mirror, int plane, boolean centered) {
      return new MirrorTileProcessor(mirror, plane, centered);
   }

   public static MirrorTileProcessor mirror(Mirror mirror, int planeX, int planeZ, boolean centered) {
      int plane = switch (mirror) {
         case FRONT_BACK -> planeX;
         case LEFT_RIGHT -> planeZ;
         case NONE -> 0;
         default -> throw new IncompatibleClassChangeError();
      };
      return mirror(mirror, plane, centered);
   }

   public static MirrorTileProcessor mirror(Mirror mirror, Vec3i planes, boolean centered) {
      return mirror(mirror, planes.getX(), planes.getZ(), centered);
   }

   public static MirrorTileProcessor mirrorRandomly(RandomSource random, int plane, boolean centered) {
      Mirror mirror = Mirror.values()[random.nextInt(Mirror.values().length)];
      return mirror(mirror, plane, centered);
   }

   public static MirrorTileProcessor mirrorRandomly(RandomSource random, int planeX, int planeZ, boolean centered) {
      Mirror mirror = Mirror.values()[random.nextInt(Mirror.values().length)];
      return mirror(mirror, planeX, planeZ, centered);
   }

   public static MirrorTileProcessor mirrorRandomly(RandomSource random, Vec3i planes, boolean centered) {
      return mirrorRandomly(random, planes.getX(), planes.getZ(), centered);
   }

   public static RotateTileProcessor rotate(Rotation rotation, int pivotX, int pivotZ, boolean centered) {
      return new RotateTileProcessor(rotation, pivotX, pivotZ, centered);
   }

   public static RotateTileProcessor rotate(Rotation rotation, Vec3i pivot, boolean centered) {
      return rotate(rotation, pivot.getX(), pivot.getZ(), centered);
   }

   public static RotateTileProcessor rotateRandomly(RandomSource random, int pivotX, int pivotZ, boolean centered) {
      Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
      return rotate(rotation, pivotX, pivotZ, centered);
   }

   public static RotateTileProcessor rotateRandomly(RandomSource random, Vec3i pivot, boolean centered) {
      Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
      return rotate(rotation, pivot.getX(), pivot.getZ(), centered);
   }

   public static TranslateTileProcessor translate(int offsetX, int offsetY, int offsetZ) {
      return new TranslateTileProcessor(offsetX, offsetY, offsetZ);
   }

   public static TranslateTileProcessor translate(Vec3i offset) {
      return new TranslateTileProcessor(offset.getX(), offset.getY(), offset.getZ());
   }

   public static BoundedTileProcessor bound(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      return new BoundedTileProcessor(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public static BoundedTileProcessor bound(BoundingBox box) {
      return bound(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
   }

   public static BoundedTileProcessor bound(ChunkPos chunkPos, int minY, int maxY) {
      return new BoundedTileProcessor(chunkPos.x * 16, minY, chunkPos.z * 16, chunkPos.x * 16 + 15, maxY, chunkPos.z * 16 + 15);
   }

   public static JigsawTileProcessor ofJigsaw() {
      return new JigsawTileProcessor();
   }

   public static StructureVoidTileProcessor ofStructureVoid() {
      return new StructureVoidTileProcessor();
   }
}
