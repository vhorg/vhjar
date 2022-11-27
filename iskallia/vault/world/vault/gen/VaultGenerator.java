package iskallia.vault.world.vault.gen;

import iskallia.vault.nbt.VListNBT;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class VaultGenerator implements INBTSerializable<CompoundTag> {
   public static Map<ResourceLocation, Supplier<? extends VaultGenerator>> REGISTRY = new HashMap<>();
   protected static final Random rand = new Random();
   protected VListNBT<VaultPiece, CompoundTag> pieces = VListNBT.of(VaultPiece::fromNBT);
   private ResourceLocation id;
   protected ChunkPos startChunk;

   public VaultGenerator(ResourceLocation id) {
      this.id = id;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public ChunkPos getStartChunk() {
      return this.startChunk;
   }

   public abstract boolean generate(ServerLevel var1, VaultRaid var2, MutableBlockPos var3);

   public void tick(ServerLevel world, VaultRaid vault) {
      world.getChunkSource().addRegionTicket(TicketType.PORTAL, this.startChunk, 3, this.startChunk.getWorldPosition());
      this.pieces.forEach(piece -> piece.tick(world, vault));
   }

   public void addPieces(VaultPiece... pieces) {
      this.addPieces(Arrays.asList(pieces));
   }

   public void addPieces(Collection<VaultPiece> pieces) {
      this.pieces.addAll(pieces);
   }

   public Collection<VaultPiece> getPiecesAt(BlockPos pos) {
      return this.pieces.stream().filter(piece -> piece.contains(pos)).collect(Collectors.toSet());
   }

   public <T extends VaultPiece> Collection<T> getPiecesAt(BlockPos pos, Class<T> pieceClass) {
      return this.pieces
         .stream()
         .filter(piece -> pieceClass.isAssignableFrom(piece.getClass()))
         .filter(piece -> piece.contains(pos))
         .map(piece -> (VaultPiece)piece)
         .collect(Collectors.toSet());
   }

   public <T extends VaultPiece> Collection<T> getPieces(Class<T> pieceClass) {
      return this.pieces.stream().filter(piece -> pieceClass.isAssignableFrom(piece.getClass())).map(piece -> (VaultPiece)piece).collect(Collectors.toSet());
   }

   public boolean intersectsWithAnyPiece(BoundingBox box) {
      return this.pieces.stream().map(VaultPiece::getBoundingBox).anyMatch(pieceBox -> pieceBox.intersects(box));
   }

   public boolean isObjectivePiece(StructurePiece piece) {
      if (!(piece instanceof PoolElementStructurePiece)) {
         return false;
      } else {
         return ((PoolElementStructurePiece)piece).getElement() instanceof PalettedSinglePoolElement element
            ? ((ResourceLocation)element.getTemplate().left().get()).toString().startsWith("the_vault:vault/prefab/decor/generic/obelisk")
            : false;
      }
   }

   protected boolean findStartPosition(ServerLevel world, VaultRaid vault, ChunkPos startChunk, Supplier<PortalPlacer> portalPlacer) {
      for (int x = -96; x < 96; x++) {
         for (int z = -96; z < 96; z++) {
            for (int y = 0; y < 48; y++) {
               BlockPos pos = startChunk.getWorldPosition().offset(x, 19 + y, z);
               if (world.getBlockState(pos).getBlock() == Blocks.CRIMSON_PRESSURE_PLATE) {
                  world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                  vault.getProperties().create(VaultRaid.START_POS, pos);

                  for (Direction direction : Plane.HORIZONTAL) {
                     int count;
                     for (count = 1; world.getBlockState(pos.relative(direction, count)).getBlock() == Blocks.WARPED_PRESSURE_PLATE; count++) {
                        world.setBlockAndUpdate(pos.relative(direction, count), Blocks.AIR.defaultBlockState());
                     }

                     if (count > 1) {
                        PortalPlacer placer = portalPlacer.get();
                        if (placer != null) {
                           vault.getProperties().create(VaultRaid.START_FACING, direction);
                           placer.place(world, pos, direction, count, count + 1);
                           return true;
                        }

                        return false;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Id", this.getId().toString());
      if (this.startChunk != null) {
         nbt.putInt("StartChunkX", this.startChunk.x);
         nbt.putInt("StartChunkZ", this.startChunk.z);
      }

      nbt.put("Pieces", this.pieces.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.id = new ResourceLocation(nbt.getString("Id"));
      this.startChunk = new ChunkPos(nbt.getInt("StartChunkX"), nbt.getInt("StartChunkZ"));
      this.pieces.deserializeNBT(nbt.getList("Pieces", 10));
   }

   public static VaultGenerator fromNBT(CompoundTag nbt) {
      VaultGenerator generator = REGISTRY.get(new ResourceLocation(nbt.getString("Id"))).get();
      generator.deserializeNBT(nbt);
      return generator;
   }

   public static <T extends VaultGenerator> Supplier<T> register(Supplier<T> generator) {
      REGISTRY.put(generator.get().getId(), generator);
      return generator;
   }
}
