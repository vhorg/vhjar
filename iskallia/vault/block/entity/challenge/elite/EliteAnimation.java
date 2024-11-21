package iskallia.vault.block.entity.challenge.elite;

import iskallia.vault.VaultMod;
import iskallia.vault.block.challenge.EliteControllerProxyBlock;
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
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableObject;

public class EliteAnimation {
   private EliteAnimation.State state = EliteAnimation.State.IDLE;
   private int ticker;

   public EliteAnimation.State getState() {
      return this.state;
   }

   public void onStart(EliteAnimation.State state) {
      this.state = state;
      this.ticker = 0;
   }

   public void onStop() {
      this.state = EliteAnimation.State.IDLE;
      this.ticker = 0;
   }

   public boolean isCompleted() {
      return this.ticker >= this.state.getTime();
   }

   public void onTick(ServerLevel world, BlockPos origin, Map<BlockPos, EliteChallengeManager.Proxy> proxies) {
      if (this.state == EliteAnimation.State.CLOSE_ROOM) {
         List<BlockPos> offsets = new ArrayList<>();

         for (BlockPos proxy : proxies.keySet()) {
            Direction facing = (Direction)world.getBlockState(proxy.offset(origin)).getOptionalValue(EliteControllerProxyBlock.FACING).orElse(null);
            if (facing != null) {
               offsets.add(proxy.relative(facing.getOpposite(), 2));
            }
         }

         Template template = null;
         if (this.ticker % 5 == 0 && this.ticker / 5 <= 10) {
            int index = this.ticker / 5;
            template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/laboratory1/pod" + index)).get(Version.latest());
            if (index == 10) {
               for (EliteChallengeManager.Proxy proxyx : proxies.values()) {
                  Entity entity = world.getEntity(proxyx.display);
                  if (entity != null) {
                     entity.setRemoved(RemovalReason.DISCARDED);
                  }
               }
            } else if (index > 4) {
               for (EliteChallengeManager.Proxy proxyxx : proxies.values()) {
                  Entity entity = world.getEntity(proxyxx.display);
                  if (entity != null) {
                     entity.setPos(entity.position().add(0.0, -1.0, 0.0));
                  }
               }
            }
         }

         if (template != null) {
            for (BlockPos offset : offsets) {
               world.playSound(null, origin.offset(offset), SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 1.4F, 1.0F);
               this.place(world, template, origin, settings -> {
                  settings.addProcessor(TileProcessor.translate(-1, -6, -1));
                  settings.addProcessor(TileProcessor.translate(origin));
                  settings.addProcessor(TileProcessor.translate(offset));
               });
            }
         }

         Map<BlockPos, Rotation> gates = new HashMap<>();
         gates.put(new BlockPos(22, 2, 0), Rotation.NONE);
         gates.put(new BlockPos(-22, 2, 0), Rotation.NONE);
         gates.put(new BlockPos(0, 2, 22), Rotation.CLOCKWISE_90);
         gates.put(new BlockPos(0, 2, -22), Rotation.CLOCKWISE_90);
         template = null;
         if (this.ticker % 10 == 0 && this.ticker / 10 <= 4) {
            template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/laboratory1/gate" + this.ticker / 10)).get(Version.latest());
         }

         if (template != null) {
            Template finalTemplate = template;
            gates.forEach((offset, rotation) -> {
               world.playSound(null, origin.offset(offset), ModSounds.RAID_IMPACT, SoundSource.BLOCKS, 1.4F, 2.0F);
               this.place(world, finalTemplate, origin, settings -> {
                  settings.addProcessor(TileProcessor.translate(0, 0, -6));
                  settings.addProcessor(TileProcessor.rotate(rotation, BlockPos.ZERO, true));
                  settings.addProcessor(TileProcessor.translate(origin));
                  settings.addProcessor(TileProcessor.translate(offset));
               });
            });
         }
      } else if (this.state == EliteAnimation.State.OPEN_ROOM) {
         Map<BlockPos, Rotation> gatesx = new HashMap<>();
         gatesx.put(new BlockPos(22, 2, 0), Rotation.NONE);
         gatesx.put(new BlockPos(-22, 2, 0), Rotation.NONE);
         gatesx.put(new BlockPos(0, 2, 22), Rotation.CLOCKWISE_90);
         gatesx.put(new BlockPos(0, 2, -22), Rotation.CLOCKWISE_90);
         Template templatex;
         if (this.ticker % 10 == 0 && this.ticker / 10 <= 4) {
            templatex = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/laboratory1/gate" + (4 - this.ticker / 10))).get(Version.latest());
         } else {
            templatex = null;
         }

         if (templatex != null) {
            gatesx.forEach((offset, rotation) -> {
               world.playSound(null, origin.offset(offset), ModSounds.RAID_IMPACT, SoundSource.BLOCKS, 1.4F, 2.0F);
               this.place(world, template, origin, settings -> {
                  settings.addProcessor(TileProcessor.translate(0, 0, -6));
                  settings.addProcessor(TileProcessor.rotate(rotation, BlockPos.ZERO, true));
                  settings.addProcessor(TileProcessor.translate(origin));
                  settings.addProcessor(TileProcessor.translate(offset));
               });
            });
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
      PROXIES(1),
      CLOSE_ROOM(55),
      FIGHT(1),
      OPEN_ROOM(55);

      private final int time;

      private State(int time) {
         this.time = time;
      }

      public int getTime() {
         return this.time;
      }
   }
}
