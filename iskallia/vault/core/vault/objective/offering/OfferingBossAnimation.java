package iskallia.vault.core.vault.objective.offering;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableObject;

public class OfferingBossAnimation {
   private OfferingBossAnimation.State state = OfferingBossAnimation.State.IDLE;
   private int ticker;

   public OfferingBossAnimation.State getState() {
      return this.state;
   }

   public void onStart(OfferingBossAnimation.State state) {
      this.state = state;
      this.ticker = 0;
   }

   public void onStop() {
      this.state = OfferingBossAnimation.State.IDLE;
      this.ticker = 0;
   }

   public boolean isCompleted() {
      return this.ticker >= this.state.getTime();
   }

   public void onTick(ServerLevel world, BlockPos pos, OfferingBossFight.RoomStyle roomStyle, Vault vault) {
      if (this.state == OfferingBossAnimation.State.CLOSE_ROOM || this.state == OfferingBossAnimation.State.OPEN_ROOM) {
         Map<BlockPos, Rotation> gates = new HashMap<>();
         int frames = 1;
         if (roomStyle == OfferingBossFight.RoomStyle.BOSS_1) {
            gates.put(new BlockPos(22, 4, 0), Rotation.CLOCKWISE_180);
            gates.put(new BlockPos(-22, 4, 0), Rotation.NONE);
            gates.put(new BlockPos(0, 4, 22), Rotation.COUNTERCLOCKWISE_90);
            gates.put(new BlockPos(0, 4, -22), Rotation.CLOCKWISE_90);
            frames = 8;
         } else if (roomStyle == OfferingBossFight.RoomStyle.BOSS_2) {
            gates.put(new BlockPos(22, 6, 0), Rotation.CLOCKWISE_180);
            gates.put(new BlockPos(-22, 6, 0), Rotation.NONE);
            gates.put(new BlockPos(0, 6, 22), Rotation.COUNTERCLOCKWISE_90);
            gates.put(new BlockPos(0, 6, -22), Rotation.CLOCKWISE_90);
            frames = 11;
         }

         Template template = null;
         int frameTime = this.state.getTime() / frames - 1;
         if (this.ticker % frameTime == 0 && this.ticker / frameTime < frames) {
            if (this.state == OfferingBossAnimation.State.CLOSE_ROOM) {
               template = VaultRegistry.TEMPLATE.getKey(roomStyle.getGateFrame(this.ticker / frameTime)).get(Version.latest());
            } else {
               template = VaultRegistry.TEMPLATE.getKey(roomStyle.getGateFrame(frames - this.ticker / frameTime - 1)).get(Version.latest());
            }

            gates.forEach((offsetx, rotationx) -> world.playSound(null, pos.offset(offsetx), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.4F, 0.8F));
         }

         if (this.ticker == 0) {
            gates.forEach((offsetx, rotationx) -> world.playSound(null, pos.offset(offsetx), ModSounds.RAID_HATCH_OPEN, SoundSource.BLOCKS, 1.4F, 1.1F));
            world.playSound(null, pos, ModSounds.RAID_HATCH_OPEN, SoundSource.BLOCKS, 1.4F, 0.8F);
         }

         if (template != null) {
            for (Entry<BlockPos, Rotation> entry : gates.entrySet()) {
               BlockPos offset = entry.getKey();
               Rotation rotation = entry.getValue();
               this.place(world, template, pos, settings -> {
                  settings.addProcessor(TileProcessor.translate(0, switch (roomStyle) {
                     case BOSS_1 -> -2;
                     case BOSS_2 -> -4;
                  }, -2));
                  settings.addProcessor(TileProcessor.rotate(rotation, BlockPos.ZERO, true));
                  settings.addProcessor(TileProcessor.translate(pos));
                  settings.addProcessor(TileProcessor.translate(offset));
                  ResourceLocation theme = vault.get(Vault.WORLD).get(WorldManager.THEME);
                  ResourceLocation id = new ResourceLocation(theme.toString().replace("classic_vault_", "universal_"));
                  PaletteKey palette = VaultRegistry.PALETTE.getKey(id);
                  if (palette != null) {
                     settings.addProcessor(palette.get(Version.latest()));
                  }
               });
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

   public void writeBits(BitBuffer buffer, SyncContext context) {
      Adapters.ofEnum(OfferingBossAnimation.State.class, EnumAdapter.Mode.NAME).writeBits(this.state, buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.ticker), buffer);
   }

   public void readBits(BitBuffer buffer, SyncContext context) {
      this.state = Adapters.ofEnum(OfferingBossAnimation.State.class, EnumAdapter.Mode.NAME).readBits(buffer).orElseThrow();
      this.ticker = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   public static enum State {
      IDLE(0),
      CLOSE_ROOM(99),
      FIGHT(0),
      OPEN_ROOM(99);

      private final int time;

      private State(int time) {
         this.time = time;
      }

      public int getTime() {
         return this.time;
      }
   }
}
