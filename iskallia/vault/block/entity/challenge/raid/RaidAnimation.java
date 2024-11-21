package iskallia.vault.block.entity.challenge.raid;

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
import iskallia.vault.init.ModSounds;
import iskallia.vault.mixin.AccessorChunkMap;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.world.data.ServerVaults;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableObject;

public class RaidAnimation {
   private RaidAnimation.State state = RaidAnimation.State.IDLE;
   private int ticker;

   public RaidAnimation.State getState() {
      return this.state;
   }

   public void onStart(RaidAnimation.State state) {
      this.state = state;
      this.ticker = 0;
   }

   public void onStop() {
      this.state = RaidAnimation.State.IDLE;
      this.ticker = 0;
   }

   public boolean isCompleted() {
      return this.ticker >= this.state.getTime();
   }

   public void onTick(ServerLevel world, BlockPos pos) {
      if (this.state == RaidAnimation.State.CLOSE_ROOM) {
         Map<BlockPos, Rotation> gates = new HashMap<>();
         gates.put(new BlockPos(22, 2, 0), Rotation.NONE);
         gates.put(new BlockPos(-22, 2, 0), Rotation.NONE);
         gates.put(new BlockPos(0, 2, 22), Rotation.CLOCKWISE_90);
         gates.put(new BlockPos(0, 2, -22), Rotation.CLOCKWISE_90);
         Template template = null;
         if (this.ticker == 0) {
            gates.forEach((offsetx, rotationx) -> world.playSound(null, pos.offset(offsetx), ModSounds.RAID_GATE_OPEN, SoundSource.BLOCKS, 1.4F, 1.5F));
            template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate1")).get(Version.latest());
         } else if (this.ticker == 20) {
            template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate2")).get(Version.latest());
         } else if (this.ticker == 40) {
            template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate3")).get(Version.latest());
         } else if (this.ticker == 60) {
            template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate4")).get(Version.latest());
         } else if (this.ticker == 80) {
            gates.forEach((offsetx, rotationx) -> world.playSound(null, pos.offset(offsetx), ModSounds.RAID_CHAIN_LOCK, SoundSource.BLOCKS, 1.4F, 1.0F));
            template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate5")).get(Version.latest());
         }

         if (template != null) {
            for (Entry<BlockPos, Rotation> entry : gates.entrySet()) {
               BlockPos offset = entry.getKey();
               Rotation rotation = entry.getValue();
               this.place(world, template, pos, settings -> {
                  settings.addProcessor(TileProcessor.translate(0, 0, -1));
                  settings.addProcessor(TileProcessor.rotate(rotation, BlockPos.ZERO, true));
                  settings.addProcessor(TileProcessor.translate(pos));
                  settings.addProcessor(TileProcessor.translate(offset));
               });
            }
         }
      } else if (this.state == RaidAnimation.State.OPEN_ROOM) {
         Map<BlockPos, Rotation> gatesx = new HashMap<>();
         gatesx.put(new BlockPos(22, 2, 0), Rotation.NONE);
         gatesx.put(new BlockPos(-22, 2, 0), Rotation.NONE);
         gatesx.put(new BlockPos(0, 2, 22), Rotation.CLOCKWISE_90);
         gatesx.put(new BlockPos(0, 2, -22), Rotation.CLOCKWISE_90);
         Template templatex = null;
         if (this.ticker == 0) {
            gatesx.forEach((offsetx, rotationx) -> world.playSound(null, pos.offset(offsetx), ModSounds.RAID_GATE_OPEN, SoundSource.BLOCKS, 1.4F, 1.5F));
            templatex = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate5")).get(Version.latest());
         } else if (this.ticker == 20) {
            templatex = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate4")).get(Version.latest());
         } else if (this.ticker == 40) {
            templatex = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate3")).get(Version.latest());
         } else if (this.ticker == 60) {
            templatex = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate2")).get(Version.latest());
         } else if (this.ticker == 80) {
            gatesx.forEach((offsetx, rotationx) -> world.playSound(null, pos.offset(offsetx), ModSounds.RAID_CHAIN_LOCK, SoundSource.BLOCKS, 1.4F, 1.0F));
            templatex = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate1")).get(Version.latest());
         } else if (this.ticker == 100) {
            templatex = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/gate0")).get(Version.latest());
         }

         if (templatex != null) {
            for (Entry<BlockPos, Rotation> entry : gatesx.entrySet()) {
               BlockPos offset = entry.getKey();
               Rotation rotation = entry.getValue();
               this.place(world, templatex, pos, settings -> {
                  settings.addProcessor(TileProcessor.translate(0, 0, -1));
                  settings.addProcessor(TileProcessor.rotate(rotation, BlockPos.ZERO, true));
                  settings.addProcessor(TileProcessor.translate(pos));
                  settings.addProcessor(TileProcessor.translate(offset));
               });
            }
         }
      } else if (this.state == RaidAnimation.State.OPEN_HATCH && this.ticker == 0) {
         ServerScheduler.INSTANCE.schedule(0, () -> {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            this.place(world, VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/hatch0")).get(Version.latest()), pos, settings -> {
               settings.addProcessor(TileProcessor.translate(-7, -1, -7));
               settings.addProcessor(TileProcessor.translate(pos));
            });
         });
         ServerScheduler.INSTANCE.schedule(20, () -> {
            world.playSound(null, pos, ModSounds.RAID_HATCH_LOCK, SoundSource.BLOCKS, 0.8F, 1.0F);
            this.place(world, VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/hatch1")).get(Version.latest()), pos, settings -> {
               settings.addProcessor(TileProcessor.translate(-7, -1, -7));
               settings.addProcessor(TileProcessor.translate(pos));
            });
         });
         ServerScheduler.INSTANCE
            .schedule(
               40, () -> this.place(world, VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/hatch2")).get(Version.latest()), pos, settings -> {
                  settings.addProcessor(TileProcessor.translate(-7, -1, -7));
                  settings.addProcessor(TileProcessor.translate(pos));
               })
            );
         ServerScheduler.INSTANCE
            .schedule(
               60, () -> this.place(world, VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/hatch3")).get(Version.latest()), pos, settings -> {
                  settings.addProcessor(TileProcessor.translate(-7, -1, -7));
                  settings.addProcessor(TileProcessor.translate(pos));
               })
            );
         ServerScheduler.INSTANCE
            .schedule(
               80, () -> this.place(world, VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/hatch4")).get(Version.latest()), pos, settings -> {
                  settings.addProcessor(TileProcessor.translate(-7, -1, -7));
                  settings.addProcessor(TileProcessor.translate(pos));
               })
            );
         ServerScheduler.INSTANCE.schedule(120, () -> {
            world.playSound(null, pos, ModSounds.RAID_IMPACT, SoundSource.BLOCKS, 1.4F, 1.0F);
            world.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.4F, 0.4F);
            this.place(world, VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/hatch5")).get(Version.latest()), pos, settings -> {
               settings.addProcessor(TileProcessor.translate(-7, -1, -7));
               settings.addProcessor(TileProcessor.translate(pos));
            });
         });
         ServerScheduler.INSTANCE.schedule(130, () -> world.playSound(null, pos, ModSounds.RAID_HATCH_OPEN, SoundSource.BLOCKS, 0.8F, 1.2F));
         ServerScheduler.INSTANCE.schedule(150, () -> {
            world.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.4F, 0.4F);
            this.place(world, VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/hatch6")).get(Version.latest()), pos, settings -> {
               settings.addProcessor(TileProcessor.translate(-7, -1, -7));
               settings.addProcessor(TileProcessor.translate(pos));
            });
         });
         ServerScheduler.INSTANCE.schedule(170, () -> {
            world.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.4F, 0.4F);
            this.place(world, VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/hatch7")).get(Version.latest()), pos, settings -> {
               settings.addProcessor(TileProcessor.translate(-7, -1, -7));
               settings.addProcessor(TileProcessor.translate(pos));
            });
         });
         ServerScheduler.INSTANCE.schedule(190, () -> {
            world.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.4F, 0.4F);
            this.place(world, VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/raid/hatch8")).get(Version.latest()), pos, settings -> {
               settings.addProcessor(TileProcessor.translate(-7, -1, -7));
               settings.addProcessor(TileProcessor.translate(pos));
            });
         });
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

   public static enum State {
      IDLE(0),
      CLOSE_ROOM(100),
      OPEN_HATCH(190),
      OPEN_ROOM(120);

      private final int time;

      private State(int time) {
         this.time = time;
      }

      public int getTime() {
         return this.time;
      }
   }
}
