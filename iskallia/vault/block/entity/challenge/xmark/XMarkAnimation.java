package iskallia.vault.block.entity.challenge.xmark;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.VaultGenerator;
import iskallia.vault.core.world.processor.ProcessorContext;
import iskallia.vault.core.world.processor.tile.MirrorTileProcessor;
import iskallia.vault.core.world.processor.tile.RotateTileProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.processor.tile.TranslateTileProcessor;
import iskallia.vault.core.world.storage.IZonedWorld;
import iskallia.vault.core.world.template.JigsawTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.mixin.AccessorChunkMap;
import iskallia.vault.world.data.ServerVaults;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableObject;

public class XMarkAnimation {
   private XMarkAnimation.State state = XMarkAnimation.State.IDLE;
   private int ticker;

   public XMarkAnimation.State getState() {
      return this.state;
   }

   public void onStart(XMarkAnimation.State state) {
      this.state = state;
      this.ticker = 0;
   }

   public void onStop() {
      this.state = XMarkAnimation.State.IDLE;
      this.ticker = 0;
   }

   public boolean isCompleted() {
      return this.ticker >= this.state.getTime();
   }

   public void onTick(ServerLevel world, BlockPos pos) {
      if (this.state == XMarkAnimation.State.OPEN_ROOM_TRAP) {
         BlockPos offset = new BlockPos(0, -1, 0);
         Template template = null;
         if (this.ticker % 5 == 0 && this.ticker / 5 <= 6) {
            world.playSound(null, pos.offset(offset), SoundEvents.BONE_BLOCK_PLACE, SoundSource.BLOCKS, 1.4F, 1.0F);
            template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/x-mark1/trapped" + this.ticker / 5)).get(Version.latest());
         }

         if (template != null) {
            this.place(world, template, pos, settings -> {
               settings.addProcessor(TileProcessor.translate(-5, 0, -5));
               settings.addProcessor(TileProcessor.translate(pos));
               settings.addProcessor(TileProcessor.translate(offset));
            });
         }
      } else if (this.state == XMarkAnimation.State.OPEN_ROOM_LOOT) {
         BlockPos offsetx = new BlockPos(0, -1, 0);
         Template templatex = null;
         if (this.ticker % 5 == 0 && this.ticker / 5 <= 6) {
            world.playSound(null, pos.offset(offsetx), SoundEvents.BONE_BLOCK_PLACE, SoundSource.BLOCKS, 1.4F, 1.0F);
            templatex = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/x-mark1/treasure" + this.ticker / 5)).get(Version.latest());
         }

         if (templatex != null) {
            this.place(world, templatex, pos, settings -> {
               settings.addProcessor(TileProcessor.translate(-5, 0, -5));
               settings.addProcessor(TileProcessor.translate(pos));
               settings.addProcessor(TileProcessor.translate(offset));
            });
         }
      } else if (this.state == XMarkAnimation.State.CLOSE_ROOF) {
         for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
               BlockPos p = pos.offset(dx, 4, dz);
               BlockState state = world.getBlockState(p);
               if (!state.isCollisionShapeFullBlock(world, pos)) {
                  IZonedWorld.runWithBypass(world, true, () -> world.setBlock(p, ModBlocks.VAULT_BEDROCK.defaultBlockState(), 3));
               }
            }
         }
      } else if (this.state == XMarkAnimation.State.OPEN_ROOF) {
         for (int dx = -3; dx <= 3; dx++) {
            for (int dzx = -3; dzx <= 3; dzx++) {
               BlockPos p = pos.offset(dx, 4, dzx);
               BlockState state = world.getBlockState(p);
               if (state.getBlock() == ModBlocks.VAULT_BEDROCK) {
                  IZonedWorld.runWithBypass(world, true, () -> world.setBlock(p, Blocks.AIR.defaultBlockState(), 3));
               }
            }
         }
      }

      this.ticker++;
   }

   public void place(ServerLevel world, Template template, BlockPos pos, Consumer<PlacementSettings> configurator) {
      ChunkRandom random = ChunkRandom.any();
      random.setRegionSeed(0L, pos.getX(), pos.getZ(), 32573453L);
      Vault vault = ServerVaults.get(world).orElse(null);
      if (vault != null) {
         VaultGenerator generator = vault.get(Vault.WORLD).get(WorldManager.GENERATOR);
         RegionPos region = RegionPos.of(pos.getX(), pos.getZ(), generator.get(GridGenerator.CELL_X), generator.get(GridGenerator.CELL_Z));
         random.setRegionSeed(vault.get(Vault.SEED), region.getX(), region.getZ(), 32573453L);
      }

      PlacementSettings settings = new PlacementSettings(new ProcessorContext(null, random)).setFlags(3);
      if (vault != null && vault.get(Vault.WORLD).get(WorldManager.GENERATOR) instanceof GridGenerator gen) {
         RegionPos region = RegionPos.ofBlockPos(pos, gen.get(GridGenerator.CELL_X), gen.get(GridGenerator.CELL_Z));
         ChunkRandom chunkRandom = ChunkRandom.any();
         chunkRandom.setRegionSeed(vault.get(Vault.SEED), region.getX(), region.getZ(), 1234567890L);
         JigsawTemplate room = (JigsawTemplate)gen.get(GridGenerator.LAYOUT).getAt(vault, region, chunkRandom, settings);
         room.getConfigurator().accept(settings);
         settings.getTileProcessors().removeIf(processor -> processor instanceof TranslateTileProcessor);
         settings.getTileProcessors().removeIf(processor -> processor instanceof MirrorTileProcessor);
         settings.getTileProcessors().removeIf(processor -> processor instanceof RotateTileProcessor);
         configurator.accept(settings);
      }

      AtomicReference<BoundingBox> pointer = new AtomicReference<>(null);
      settings.addProcessor(TileProcessor.of((_tile, context) -> {
         pointer.getAndUpdate(value -> value == null ? BoundingBox.fromCorners(_tile.getPos(), _tile.getPos()) : value.encapsulate(_tile.getPos()));
         return _tile;
      }));
      IZonedWorld.runWithBypass(world, true, () -> template.place(world, settings));
      BoundingBox box = pointer.get();
      if (box != null) {
         ServerChunkCache source = world.getChunkSource();

         for (int x = box.minX(); x < box.maxX(); x += x + 16 < box.maxX() ? 16 : 16 - Math.floorMod(x, 16)) {
            for (int z = box.minZ(); z < box.maxZ(); z += z + 16 < box.maxZ() ? 16 : 16 - Math.floorMod(z, 16)) {
               ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
               world.getServer().tell(new TickTask(world.getServer().getTickCount() + 1, () -> source.chunkMap.getPlayers(chunkPos, false).forEach(player -> {
                  world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
                  ((AccessorChunkMap)source.chunkMap).callUpdateChunkTracking(player, chunkPos, new MutableObject(), false, true);
               })));
            }
         }
      }
   }

   public static enum State {
      IDLE(0),
      CLOSE_ROOF(1),
      OPEN_ROOM_TRAP(40),
      OPEN_ROOM_LOOT(40),
      FIGHT(0),
      OPEN_ROOF(1);

      private final int time;

      private State(int time) {
         this.time = time;
      }

      public int getTime() {
         return this.time;
      }
   }
}
