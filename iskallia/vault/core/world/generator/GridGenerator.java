package iskallia.vault.core.world.generator;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.RegistryValueAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.TemplateGenerationEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.util.MathUtils;
import iskallia.vault.core.util.ObjectCache;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.generator.layout.GridLayout;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.core.world.template.EmptyTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.configured.ChunkedTemplate;
import iskallia.vault.core.world.template.configured.ConfiguredTemplate;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.mixin.AccessorChunkMap;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.apache.commons.lang3.mutable.MutableObject;

public class GridGenerator extends VaultGenerator {
   public static final SupplierKey<GridGenerator> KEY = SupplierKey.of("grid", GridGenerator.class).with(Version.v1_0, GridGenerator::new);
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Integer> CELL_X = FieldKey.of("cell_x", Integer.class)
      .with(Version.v1_0, Adapters.ofBoundedInt(1, 256), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Integer> CELL_Z = FieldKey.of("cell_z", Integer.class)
      .with(Version.v1_0, Adapters.ofBoundedInt(1, 256), DISK.all())
      .register(FIELDS);
   public static final FieldKey<GridLayout> LAYOUT = FieldKey.of("layout", GridLayout.class)
      .with(Version.v1_0, RegistryValueAdapter.of(() -> VaultRegistry.GRID_LAYOUT, ISupplierKey::getKey, Supplier::get), DISK.all())
      .register(FIELDS);
   protected ObjectCache<RegionPos, ConfiguredTemplate> cache;

   @Override
   public SupplierKey<GridGenerator> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      this.cache = new ObjectCache<>(
         world.getGameRules().getInt(ModGameRules.VAULT_TEMPLATE_CACHE_SIZE),
         region -> MathUtils.mask(region.getX(), 31) | MathUtils.mask(region.getZ(), 31) << 32
      );
      this.get(LAYOUT).initServer(world, vault, this);
      super.initServer(world, vault);
   }

   @Override
   public void releaseServer() {
      this.cache = null;
      this.get(LAYOUT).releaseServer();
      super.releaseServer();
   }

   @Override
   public void generate(Vault vault, ServerLevelAccessor world, ChunkPos chunkPos) {
      BlockPos pos1 = new BlockPos(chunkPos.x * 16, Integer.MIN_VALUE, chunkPos.z * 16);
      BlockPos pos2 = new BlockPos(chunkPos.x * 16 + 15, Integer.MAX_VALUE, chunkPos.z * 16 + 15);
      int offsetX = Math.floorMod(pos1.getX(), this.get(CELL_X));
      int offsetZ = Math.floorMod(pos1.getZ(), this.get(CELL_Z));

      for (int x = pos1.getX(); x <= pos2.getX(); x += this.get(CELL_X) - offsetX) {
         for (int z = pos1.getZ(); z <= pos2.getZ(); z += this.get(CELL_Z) - offsetZ) {
            RegionPos region = RegionPos.ofBlockPos(new BlockPos(x, 0, z), this.get(CELL_X), this.get(CELL_Z));
            ChunkRandom random = ChunkRandom.any();
            if (vault.get(Vault.VERSION).isOlderThan(Version.v1_7)) {
               random.setCarverSeed(vault.get(Vault.SEED), region.getX(), region.getZ());
            } else {
               random.setRegionSeed(vault.get(Vault.SEED), region.getX(), region.getZ(), 1234567890);
            }

            if (this.cache == null) {
               return;
            }

            ConfiguredTemplate template;
            if (this.cache.has(region)) {
               template = this.cache.get(region);
            } else {
               PlacementSettings settings = new PlacementSettings().setFlags(3);
               settings.getProcessorContext().random = random;
               settings.getProcessorContext().vault = vault;
               template = this.get(LAYOUT).getAt(vault, region, random, settings).configure(ChunkedTemplate::new, settings);
               if (template.getParent() != EmptyTemplate.INSTANCE) {
                  this.cache.set(region, template);
               }
            }

            if (template != null) {
               template = CommonEvents.TEMPLATE_GENERATION.invoke(world, template, region, chunkPos, random, TemplateGenerationEvent.Phase.PRE).getTemplate();
               template.place(world, chunkPos);
               CommonEvents.TEMPLATE_GENERATION.invoke(world, template, region, chunkPos, random, TemplateGenerationEvent.Phase.POST);
            }
         }
      }
   }

   public void generate(Vault vault, ServerLevelAccessor world, RegionPos region) {
      ChunkRandom random = ChunkRandom.any();
      random.setRegionSeed(vault.get(Vault.SEED), region.getX(), region.getZ(), 1234567890);
      PlacementSettings settings = new PlacementSettings().setFlags(272);
      settings.getProcessorContext().random = random;
      settings.getProcessorContext().vault = vault;
      ConfiguredTemplate template = this.get(LAYOUT).getAt(vault, region, random, settings).configure(ConfiguredTemplate::new, settings);
      int minX = region.getX() * region.getSizeX();
      int maxX = minX + region.getSizeX();
      int minZ = region.getZ() * region.getSizeZ();
      int maxZ = minZ + region.getSizeZ();

      for (int x = minX; x < maxX; x += x + 16 < maxX ? 16 : 16 - Math.floorMod(x, 16)) {
         for (int z = minZ; z < maxZ; z += z + 16 < maxZ ? 16 : 16 - Math.floorMod(z, 16)) {
            ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
            CommonEvents.TEMPLATE_GENERATION.invoke(world, template, region, chunkPos, random, TemplateGenerationEvent.Phase.PRE);
         }
      }

      template.place(world, null);

      for (int x = minX; x < maxX; x += x + 16 < maxX ? 16 : 16 - Math.floorMod(x, 16)) {
         for (int z = minZ; z < maxZ; z += z + 16 < maxZ ? 16 : 16 - Math.floorMod(z, 16)) {
            ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
            CommonEvents.TEMPLATE_GENERATION.invoke(world, template, region, chunkPos, random, TemplateGenerationEvent.Phase.POST);
         }
      }

      if (world.getChunkSource() instanceof ServerChunkCache source && world.getServer() != null) {
         for (int x = minX; x < maxX; x += x + 16 < maxX ? 16 : 16 - Math.floorMod(x, 16)) {
            for (int z = minZ; z < maxZ; z += z + 16 < maxZ ? 16 : 16 - Math.floorMod(z, 16)) {
               ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
               world.getServer().tell(new TickTask(world.getServer().getTickCount() + 1, () -> source.chunkMap.getPlayers(chunkPos, false).forEach(player -> {
                  world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
                  ((AccessorChunkMap)source.chunkMap).callUpdateChunkTracking(player, chunkPos, new MutableObject(), false, true);
               })));
            }
         }
      }
   }
}
