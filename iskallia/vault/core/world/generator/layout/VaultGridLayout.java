package iskallia.vault.core.world.generator.layout;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.VaultGenerator;
import iskallia.vault.core.world.processor.ProcessorContext;
import iskallia.vault.core.world.processor.entity.EntityProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.core.world.template.EmptyTemplate;
import iskallia.vault.core.world.template.JigsawTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import iskallia.vault.core.world.template.data.TemplateEntry;
import iskallia.vault.core.world.template.data.TemplatePool;
import iskallia.vault.init.ModBlocks;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public abstract class VaultGridLayout extends GridLayout {
   public static final FieldRegistry FIELDS = GridLayout.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_0, Adapters.FLOAT, DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> FILL_AIR = FieldKey.of("fill_stone", Void.class).with(Version.v1_19, Adapters.ofVoid(), DISK.all()).register(FIELDS);

   @Override
   public void initServer(VirtualWorld world, Vault vault, GridGenerator generator) {
      CommonEvents.NOISE_GENERATION.in(world).register(this, data -> {
         if (!this.has(FILL_AIR)) {
            MutableBlockPos pos = new MutableBlockPos();
            BlockState state = ModBlocks.VAULT_BEDROCK.defaultBlockState();

            for (int y = 63; y >= 0; y--) {
               for (int x = 0; x < 16; x++) {
                  for (int z = 0; z < 16; z++) {
                     data.getChunk().setBlockState(pos.set(x, y, z), state, false);
                  }
               }
            }
         }
      });
   }

   @Override
   public void releaseServer() {
      CommonEvents.NOISE_GENERATION.release(this);
   }

   @Override
   public Template getAt(Vault vault, RegionPos region, RandomSource random, PlacementSettings settings) {
      VaultLayout.PieceType type = this.getType(vault, region);
      Template template = CommonEvents.LAYOUT_TEMPLATE_GENERATION.invoke(this, vault, region, random, settings, type, null).getTemplate();
      if (template == null) {
         template = this.getTemplate(type, vault, region, random, settings);
      }

      if (template instanceof JigsawTemplate jigsaw) {
         Iterator<JigsawTemplate> iterator = jigsaw.getChildren().iterator();
         JigsawTemplate target = null;
         int i = 0;

         while (iterator.hasNext()) {
            JigsawTemplate child = iterator.next();
            if (child.hasTag(VaultMod.id("objective_piece"))) {
               if (random.nextInt(++i) == 0) {
                  target = child;
               }

               iterator.remove();
            }
         }

         double probability = 1.0;
         double var16 = CommonEvents.OBJECTIVE_PIECE_GENERATION.invoke(vault, probability).getProbability();
         if (random.nextFloat() < var16 && target != null) {
            jigsaw.getChildren().add(target);
         }

         for (JigsawTemplate child : jigsaw.getChildren()) {
            if (child.hasTag(VaultMod.id("portal_piece"))) {
               List<ResourceLocation> tags = new ArrayList<>();
               if (region.getX() == 0 && region.getZ() == 0) {
                  tags.add(ClassicPortalLogic.ENTRANCE);
               }

               tags.add(ClassicPortalLogic.EXIT);
               vault.get(Vault.WORLD).get(WorldManager.PORTAL_LOGIC).addPortal(child, settings, tags);
            }
         }
      }

      return template;
   }

   public abstract Template getTemplate(VaultLayout.PieceType var1, Vault var2, RegionPos var3, RandomSource var4, PlacementSettings var5);

   public abstract VaultLayout.PieceType getType(Vault var1, RegionPos var2);

   public Template getStart(TemplatePool pool, Version version, RegionPos region, RandomSource random, Direction facing, PlacementSettings settings) {
      if (pool == null) {
         return EmptyTemplate.INSTANCE;
      } else {
         TemplateEntry entry = pool.getRandomFlat(version, random).orElse(null);
         if (entry == null) {
            return EmptyTemplate.INSTANCE;
         } else {
            Mirror mirror = random.nextBoolean() ? Mirror.FRONT_BACK : Mirror.NONE;

            Rotation rotation = switch (facing) {
               case NORTH -> Rotation.CLOCKWISE_180;
               case EAST -> Rotation.COUNTERCLOCKWISE_90;
               case WEST -> Rotation.CLOCKWISE_90;
               case SOUTH -> Rotation.NONE;
               default -> throw new UnsupportedOperationException("Cannot place start facing " + facing);
            };
            BlockPos offset = new BlockPos(13, 0, 26);
            BlockPos pos = region.toBlockPos().above(22);
            settings.addProcessors(
               TileProcessor.translate(offset),
               TileProcessor.mirror(mirror, 23, 23, true),
               TileProcessor.rotate(rotation, 23, 23, true),
               TileProcessor.translate(pos),
               EntityProcessor.translate(offset),
               EntityProcessor.mirror(mirror, 23, 23, true),
               EntityProcessor.rotate(rotation, 23, 23, true),
               EntityProcessor.translate(pos)
            );
            return JigsawTemplate.of(version, entry, 10, random);
         }
      }
   }

   public Template getRoom(TemplatePool pool, Version version, RegionPos region, RandomSource random, PlacementSettings settings) {
      if (pool == null) {
         return EmptyTemplate.INSTANCE;
      } else {
         TemplateEntry entry = pool.getRandomFlat(version, random).orElse(null);
         if (entry == null) {
            return EmptyTemplate.INSTANCE;
         } else {
            Mirror mirror = random.nextBoolean() ? Mirror.NONE : Mirror.FRONT_BACK;
            Rotation rotation = new Rotation[]{Rotation.NONE, Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180}[random.nextInt(4)];
            BlockPos pos = region.toBlockPos().above(region.getSizeX() > 47 ? 0 : 9);
            int offsetX = region.getSizeX() / 2;
            int offsetZ = region.getSizeZ() / 2;
            settings.addProcessors(
               TileProcessor.mirror(mirror, offsetX, offsetZ, true),
               TileProcessor.rotate(rotation, offsetX, offsetZ, true),
               TileProcessor.translate(pos),
               EntityProcessor.mirror(mirror, offsetX, offsetZ, true),
               EntityProcessor.rotate(rotation, offsetX, offsetZ, true),
               EntityProcessor.translate(pos)
            );
            return JigsawTemplate.of(version, entry, 10, random);
         }
      }
   }

   public Template getTunnel(TemplatePool pool, Version version, RegionPos region, RandomSource random, Axis axis, PlacementSettings settings) {
      if (pool == null) {
         return EmptyTemplate.INSTANCE;
      } else {
         TemplateEntry entry = pool.getRandomFlat(version, random).orElse(null);
         if (entry == null) {
            return EmptyTemplate.INSTANCE;
         } else {
            int index = random.nextInt(4);
            Mirror mirror = new Mirror[]{Mirror.NONE, Mirror.FRONT_BACK, Mirror.LEFT_RIGHT, Mirror.NONE}[index];
            Rotation rotation = index == 3 ? Rotation.CLOCKWISE_180 : Rotation.NONE;
            if (axis == Axis.X) {
               rotation = rotation.getRotated(Rotation.CLOCKWISE_90);
            }

            BlockPos offset = new BlockPos(18, 0, 0);
            BlockPos pos = region.toBlockPos().above(27);
            settings.addProcessors(
               TileProcessor.translate(offset),
               TileProcessor.mirror(mirror, 23, 23, true),
               TileProcessor.rotate(rotation, 23, 23, true),
               TileProcessor.translate(pos),
               EntityProcessor.translate(offset),
               EntityProcessor.mirror(mirror, 23, 23, true),
               EntityProcessor.rotate(rotation, 23, 23, true),
               EntityProcessor.translate(pos)
            );
            return JigsawTemplate.of(version, entry, 10, random);
         }
      }
   }

   @Override
   public Iterator<VaultLayout.LayoutEntry> expandingIterator(Vault vault, int maxDistance) {
      if (maxDistance < 0) {
         return Collections.emptyIterator();
      } else {
         VaultGenerator generator = vault.get(Vault.WORLD).get(WorldManager.GENERATOR);
         if (generator instanceof GridGenerator gridGenerator) {
            int cellWidthX = gridGenerator.get(GridGenerator.CELL_X);
            int cellWidthZ = gridGenerator.get(GridGenerator.CELL_Z);
            return new VaultGridLayout.LayoutIterator(vault, maxDistance, cellWidthX, cellWidthZ);
         } else {
            return Collections.emptyIterator();
         }
      }
   }

   public class LayoutIterator implements Iterator<VaultLayout.LayoutEntry> {
      private final Vault vault;
      private final int maxLayer;
      private final int cellWidthX;
      private final int cellWidthZ;
      private Deque<VaultLayout.LayoutEntry> collectedEntries = new ArrayDeque<>(1);
      private int layer = 0;

      public LayoutIterator(Vault vault, int maxCellDistance, int cellWidthX, int cellWidthZ) {
         this.vault = vault;
         this.maxLayer = maxCellDistance;
         this.cellWidthX = cellWidthX;
         this.cellWidthZ = cellWidthZ;
         this.appendEntry(0, 0);
      }

      @Override
      public boolean hasNext() {
         return !this.collectedEntries.isEmpty() || this.layer < this.maxLayer;
      }

      public VaultLayout.LayoutEntry next() {
         if (!this.collectedEntries.isEmpty()) {
            return this.collectedEntries.poll();
         } else if (this.layer >= this.maxLayer) {
            throw new NoSuchElementException();
         } else {
            this.layer++;
            this.populateLayer();
            return this.next();
         }
      }

      private void populateLayer() {
         this.collectedEntries = new ArrayDeque<>(this.layer * 8);
         int minX = -this.layer;
         int minZ = -this.layer;
         int maxX = this.layer;
         int maxZ = this.layer;

         for (int x = minX; x <= maxX; x++) {
            this.appendEntry(x, minZ);
            this.appendEntry(x, maxZ);
         }

         for (int z = minZ + 1; z <= maxZ - 1; z++) {
            this.appendEntry(minX, z);
            this.appendEntry(maxX, z);
         }
      }

      private void appendEntry(int cellX, int cellZ) {
         RegionPos region = RegionPos.of(cellX, cellZ, this.cellWidthX, this.cellWidthZ);
         ChunkRandom rand = ChunkRandom.any();
         rand.setRegionSeed(this.vault.get(Vault.SEED), region.getX(), region.getZ(), 1234567890L);
         PlacementSettings settings = new PlacementSettings(new ProcessorContext(this.vault, rand)).setFlags(272);
         VaultLayout.PieceType type = VaultGridLayout.this.getType(this.vault, region);
         Template tpl = VaultGridLayout.this.getAt(this.vault, region, rand, settings);
         this.collectedEntries.add(new VaultLayout.LayoutEntry(type, tpl));
      }
   }
}
