package iskallia.vault.core.world.template;

import com.google.common.collect.Lists;
import iskallia.vault.VaultMod;
import iskallia.vault.core.util.iterator.MappingIterator;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.PartialState;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.processor.Processor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

public class StructureTemplate extends Template implements INBTSerializable<CompoundTag> {
   public static final Comparator<PartialTile> SORTER = Comparator.<PartialTile>comparingInt(tile -> tile.getPos().getY())
      .thenComparingInt(tile -> tile.getPos().getX())
      .thenComparingInt(tile -> tile.getPos().getZ());
   private Map<Predicate<PartialTile>, List<PartialTile>> tiles = new HashMap<>();
   private Map<Predicate<PartialEntity>, List<PartialEntity>> entities = new HashMap<>();
   private StructureTemplate.IdPalette palette;
   private Vec3i size = Vec3i.ZERO;
   private Set<ResourceLocation> tags = new HashSet<>();
   private String path;

   protected StructureTemplate(String path) {
      this.path = path;
   }

   public static StructureTemplate fromPath(String path) {
      CompoundTag nbt;
      try {
         nbt = NbtIo.readCompressed(new FileInputStream(path));
      } catch (IOException var3) {
         return null;
      }

      StructureTemplate template = new StructureTemplate(path);
      template.deserializeNBT(nbt);
      return template;
   }

   public String getPath() {
      return this.path;
   }

   @Override
   public Iterator<ResourceLocation> getTags() {
      return this.tags.iterator();
   }

   @Override
   public void addTag(ResourceLocation tag) {
      this.tags.add(tag);
   }

   @Override
   public boolean hasTag(ResourceLocation tag) {
      return this.tags.contains(tag);
   }

   @Override
   public Iterator<PartialTile> getTiles(Predicate<PartialTile> filter, PlacementSettings settings) {
      return new MappingIterator<>(this.tiles.get(filter).iterator(), tile -> {
         tile = tile.copy();

         for (Processor<PartialTile> processor : settings.getTileProcessors()) {
            if (tile == null || !filter.test(tile)) {
               tile = null;
               break;
            }

            tile = processor.process(tile, settings.getProcessorContext());
         }

         return tile;
      });
   }

   @Override
   public Iterator<PartialEntity> getEntities(Predicate<PartialEntity> filter, PlacementSettings settings) {
      return new MappingIterator<>(this.entities.get(filter).iterator(), entity -> {
         entity = entity.copy();

         for (Processor<PartialEntity> processor : settings.getEntityProcessors()) {
            if (entity == null || !filter.test(entity)) {
               entity = null;
               break;
            }

            entity = processor.process(entity, settings.getProcessorContext());
         }

         return entity;
      });
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      if (this.tiles.isEmpty()) {
         nbt.put("blocks", new ListTag());
         nbt.put("palette", new ListTag());
      } else {
         ListTag blocksNBT = new ListTag();

         for (PartialTile tile : this.tiles.get(ALL_TILES)) {
            int paletteId = this.palette.getIdFor(tile.getState());
            blocksNBT.add(tile.toPaletteNBT(new CompoundTag(), paletteId));
         }

         nbt.put("blocks", blocksNBT);
         ListTag paletteNBT = new ListTag();

         for (PartialState state : this.palette) {
            paletteNBT.add(state.toNBT(new CompoundTag()));
         }

         nbt.put("palette", paletteNBT);
      }

      ListTag entitiesNBT = new ListTag();

      for (PartialEntity entity : this.entities.get(ALL_ENTITIES)) {
         CompoundTag entityNBT = new CompoundTag();
         ListTag posNBT = new ListTag();
         posNBT.add(DoubleTag.valueOf(entity.getPos().x));
         posNBT.add(DoubleTag.valueOf(entity.getPos().y));
         posNBT.add(DoubleTag.valueOf(entity.getPos().z));
         entityNBT.put("pos", posNBT);
         ListTag blockPosNBT = new ListTag();
         blockPosNBT.add(IntTag.valueOf(entity.getBlockPos().getX()));
         blockPosNBT.add(IntTag.valueOf(entity.getBlockPos().getY()));
         blockPosNBT.add(IntTag.valueOf(entity.getBlockPos().getZ()));
         entityNBT.put("blockPos", blockPosNBT);
         if (entity.getNBT() != null) {
            entityNBT.put("nbt", entity.getNBT());
         }

         entitiesNBT.add(entityNBT);
      }

      nbt.put("entities", entitiesNBT);
      ListTag sizeNBT = new ListTag();
      sizeNBT.add(IntTag.valueOf(this.size.getX()));
      sizeNBT.add(IntTag.valueOf(this.size.getY()));
      sizeNBT.add(IntTag.valueOf(this.size.getZ()));
      nbt.put("size", sizeNBT);
      nbt.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.tiles.clear();
      this.entities.clear();
      this.tiles.put(Template.ALL_TILES, new ArrayList<>());
      this.tiles.put(Template.JIGSAWS, new ArrayList<>());
      this.tiles.put(Template.PLACEHOLDERS, new ArrayList<>());
      this.tiles.put(Template.VAULT_PORTALS, new ArrayList<>());
      this.entities.put(Template.ALL_ENTITIES, new ArrayList<>());
      ListTag sizeNBT = nbt.getList("size", 3);
      this.size = new Vec3i(sizeNBT.getInt(0), sizeNBT.getInt(1), sizeNBT.getInt(2));
      ListTag blocksNBT = nbt.getList("blocks", 10);
      if (nbt.contains("palettes", 9)) {
         this.loadPalette(nbt.getList("palettes", 9).getList(0), blocksNBT);
         VaultMod.LOGGER.error("Template does not support multiple palettes, using the first one instead");
      } else {
         this.loadPalette(nbt.getList("palette", 10), blocksNBT);
      }

      ListTag entitiesNBT = nbt.getList("entities", 10);

      for (int j = 0; j < entitiesNBT.size(); j++) {
         CompoundTag entityNBT = entitiesNBT.getCompound(j);
         ListTag posNBT = entityNBT.getList("pos", 6);
         Vec3 pos = new Vec3(posNBT.getDouble(0), posNBT.getDouble(1), posNBT.getDouble(2));
         ListTag blockPosNBT = entityNBT.getList("blockPos", 3);
         BlockPos blockPos = new BlockPos(blockPosNBT.getInt(0), blockPosNBT.getInt(1), blockPosNBT.getInt(2));
         if (entityNBT.contains("nbt")) {
            PartialEntity entity = PartialEntity.of(pos, blockPos, entityNBT.getCompound("nbt"));
            this.entities.forEach((filter, list) -> {
               if (filter.test(entity)) {
                  list.add(entity);
               }
            });
         }
      }
   }

   private void loadPalette(ListTag paletteNBT, ListTag blocksNBT) {
      this.palette = new StructureTemplate.IdPalette();

      for (int i = 0; i < paletteNBT.size(); i++) {
         this.palette.addMapping(PartialState.fromNBT(paletteNBT.getCompound(i)), i);
      }

      List<PartialTile> normal = Lists.newArrayList();
      List<PartialTile> withNBT = Lists.newArrayList();
      List<PartialTile> withSpecialShape = Lists.newArrayList();

      for (int j = 0; j < blocksNBT.size(); j++) {
         CompoundTag blockNBT = blocksNBT.getCompound(j);
         PartialTile tile = PartialTile.fromPaletteNBT(blockNBT, this.palette::getStateFor);
         if (tile.getNbt() != null) {
            withNBT.add(tile);
         } else if (!tile.getState().getBlock().hasDynamicShape()
            && tile.getState().asBlockState().isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
            normal.add(tile);
         } else {
            withSpecialShape.add(tile);
         }
      }

      for (PartialTile tile : getOrderedTiles(normal, withNBT, withSpecialShape)) {
         this.tiles.forEach((filter, list) -> {
            if (filter.test(tile)) {
               list.add(tile);
            }
         });
      }
   }

   private static List<PartialTile> getOrderedTiles(List<PartialTile> normal, List<PartialTile> withNBT, List<PartialTile> withSpecialShape) {
      normal.sort(SORTER);
      withSpecialShape.sort(SORTER);
      withNBT.sort(SORTER);
      List<PartialTile> tiles = new ArrayList<>();
      tiles.addAll(normal);
      tiles.addAll(withSpecialShape);
      tiles.addAll(withNBT);
      return tiles;
   }

   public static class IdPalette implements Iterable<PartialState> {
      public static final PartialState DEFAULT_STATE = PartialState.of(Blocks.AIR.defaultBlockState());
      private final IdMapper<PartialState> ids = new IdMapper(16);
      private int nextId;

      public int getIdFor(PartialState pState) {
         int i = this.ids.getId(pState);
         if (i == -1) {
            i = this.nextId++;
            this.ids.addMapping(pState, i);
         }

         return i;
      }

      public PartialState getStateFor(int id) {
         PartialState state = (PartialState)this.ids.byId(id);
         return state == null ? DEFAULT_STATE : state;
      }

      public void addMapping(PartialState state, int id) {
         this.ids.addMapping(state, id);
      }

      @Override
      public Iterator<PartialState> iterator() {
         return this.ids.iterator();
      }
   }
}
