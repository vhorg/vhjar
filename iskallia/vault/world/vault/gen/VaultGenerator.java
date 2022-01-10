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
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class VaultGenerator implements INBTSerializable<CompoundNBT> {
   public static Map<ResourceLocation, Supplier<? extends VaultGenerator>> REGISTRY = new HashMap<>();
   protected static final Random rand = new Random();
   protected VListNBT<VaultPiece, CompoundNBT> pieces = VListNBT.of(VaultPiece::fromNBT);
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

   public abstract boolean generate(ServerWorld var1, VaultRaid var2, Mutable var3);

   public void tick(ServerWorld world, VaultRaid vault) {
      world.func_72863_F().func_217228_a(TicketType.field_219493_f, this.startChunk, 3, this.startChunk.func_206849_h());
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

   public boolean intersectsWithAnyPiece(MutableBoundingBox box) {
      return this.pieces.stream().map(VaultPiece::getBoundingBox).anyMatch(pieceBox -> pieceBox.func_78884_a(box));
   }

   public boolean isObjectivePiece(StructurePiece piece) {
      if (!(piece instanceof AbstractVillagePiece)) {
         return false;
      } else {
         JigsawPiece jigsaw = ((AbstractVillagePiece)piece).func_214826_b();
         if (!(jigsaw instanceof PalettedSinglePoolElement)) {
            return false;
         } else {
            PalettedSinglePoolElement element = (PalettedSinglePoolElement)jigsaw;
            return ((ResourceLocation)element.getTemplate().left().get()).toString().startsWith("the_vault:vault/prefab/decor/generic/obelisk");
         }
      }
   }

   protected boolean findStartPosition(ServerWorld world, VaultRaid vault, ChunkPos startChunk, Supplier<PortalPlacer> portalPlacer) {
      for (int x = -48; x < 48; x++) {
         for (int z = -48; z < 48; z++) {
            for (int y = 0; y < 48; y++) {
               BlockPos pos = startChunk.func_206849_h().func_177982_a(x, 19 + y, z);
               if (world.func_180495_p(pos).func_177230_c() == Blocks.field_235348_mG_) {
                  world.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
                  vault.getProperties().create(VaultRaid.START_POS, pos);

                  for (Direction direction : Plane.HORIZONTAL) {
                     int count;
                     for (count = 1; world.func_180495_p(pos.func_177967_a(direction, count)).func_177230_c() == Blocks.field_235349_mH_; count++) {
                        world.func_175656_a(pos.func_177967_a(direction, count), Blocks.field_150350_a.func_176223_P());
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

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Id", this.getId().toString());
      if (this.startChunk != null) {
         nbt.func_74768_a("StartChunkX", this.startChunk.field_77276_a);
         nbt.func_74768_a("StartChunkZ", this.startChunk.field_77275_b);
      }

      nbt.func_218657_a("Pieces", this.pieces.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.id = new ResourceLocation(nbt.func_74779_i("Id"));
      this.startChunk = new ChunkPos(nbt.func_74762_e("StartChunkX"), nbt.func_74762_e("StartChunkZ"));
      this.pieces.deserializeNBT(nbt.func_150295_c("Pieces", 10));
   }

   public static VaultGenerator fromNBT(CompoundNBT nbt) {
      VaultGenerator generator = REGISTRY.get(new ResourceLocation(nbt.func_74779_i("Id"))).get();
      generator.deserializeNBT(nbt);
      return generator;
   }

   public static <T extends VaultGenerator> Supplier<T> register(Supplier<T> generator) {
      REGISTRY.put(generator.get().getId(), generator);
      return generator;
   }
}
