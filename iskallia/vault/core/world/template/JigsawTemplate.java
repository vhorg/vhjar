package iskallia.vault.core.world.template;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.iterator.FlatteningIterator;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.generator.JigsawData;
import iskallia.vault.core.world.processor.entity.EntityProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.template.data.TemplateEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType;

public class JigsawTemplate extends Template {
   private Template root;
   private List<JigsawTemplate> children = new ArrayList<>();
   private Consumer<PlacementSettings> configurator = settings -> {};

   protected JigsawTemplate(Template root) {
      this.root = root;
   }

   @Override
   public Iterator<ResourceLocation> getTags() {
      return this.root.getTags();
   }

   @Override
   public void addTag(ResourceLocation tag) {
      this.root.addTag(tag);
   }

   @Override
   public boolean hasTag(ResourceLocation tag) {
      return this.root.hasTag(tag);
   }

   public Consumer<PlacementSettings> getConfigurator() {
      return this.configurator;
   }

   public List<JigsawTemplate> getChildren() {
      return this.children;
   }

   public void setChildren(List<JigsawTemplate> children) {
      this.children = children;
   }

   public static JigsawTemplate of(Version version, Template root, Collection<PaletteKey> palettes, int depth, RandomSource random) {
      JigsawTemplate template = new JigsawTemplate(root);
      template.computeChildren(version, depth, random);
      template.configurator = settings -> {
         settings.addProcessor(TileProcessor.ofJigsaw());
         settings.addProcessor(TileProcessor.ofStructureVoid());

         for (PaletteKey palette : palettes) {
            settings.addProcessor(palette.get(version));
         }
      };
      return template;
   }

   public static JigsawTemplate of(Version version, TemplateEntry root, int depth, RandomSource random) {
      JigsawTemplate template = new JigsawTemplate(root.getTemplate().get(version));
      template.computeChildren(version, depth, random);
      template.configurator = settings -> {
         settings.addProcessor(TileProcessor.ofJigsaw());
         settings.addProcessor(TileProcessor.ofStructureVoid());

         for (PaletteKey palette : root.getPalettes()) {
            settings.addProcessor(palette.get(version));
         }
      };
      return template;
   }

   protected JigsawTemplate computeChildren(Version version, int depth, RandomSource random) {
      if (depth < 0) {
         return this;
      } else {
         this.root.getTiles(Template.JIGSAWS).forEachRemaining(start -> {
            JigsawData data1 = new JigsawData(start);
            TemplatePoolKey pool = VaultRegistry.TEMPLATE_POOL.getKey(data1.getPool());
            if (pool != null) {
               TemplateEntry entry = pool.get(version).getRandomFlat(version, random).orElse(null);
               if (entry != null && entry.getTemplate() != null) {
                  Template childTemplate = entry.getTemplate().get(version);
                  JigsawData data2 = null;
                  Iterator<PartialTile> iterator = childTemplate.getTiles(Template.JIGSAWS);
                  int size = 0;

                  while (iterator.hasNext()) {
                     PartialTile tile = iterator.next();
                     JigsawData other = new JigsawData(tile);
                     if (!data1.getTarget().equals(other.getName())) {
                        return;
                     }

                     if (data1.getFacing().getAxis().isHorizontal() != other.getFacing().getAxis().isHorizontal()) {
                        return;
                     }

                     if (data1.getFacing().getAxis().isHorizontal() && data1.getSide() != other.getSide()) {
                        return;
                     }

                     if (random.nextInt(size++ + 1) == 0) {
                        data2 = other;
                     }
                  }

                  if (data2 != null) {
                     BlockPos target = data2.getPos();
                     BlockPos offset = start.getPos().subtract(target).relative(data1.getFacing());
                     Rotation rotation = this.getRotation(data2, data1, random);
                     JigsawTemplate child = new JigsawTemplate(childTemplate);
                     child.configurator = settings -> {
                        int index = 0;
                        settings.getTileProcessors().add(index++, TileProcessor.rotate(rotation, target, true));
                        settings.getTileProcessors().add(index++, TileProcessor.translate(offset));
                        settings.getTileProcessors().add(index++, TileProcessor.ofJigsaw());

                        for (PaletteKey palette : entry.getPalettes()) {
                           for (TileProcessor tileProcessor : palette.get(version).getTileProcessors()) {
                              settings.getTileProcessors().add(index++, tileProcessor);
                           }

                           for (EntityProcessor entityProcessor : palette.get(version).getEntityProcessors()) {
                              settings.getEntityProcessors().add(index++, entityProcessor);
                           }
                        }
                     };
                     child.computeChildren(version, depth - 1, random);
                     this.children.add(child);
                  }
               }
            }
         });
         return this;
      }
   }

   @Override
   public Iterator<PartialTile> getTiles(Predicate<PartialTile> filter, PlacementSettings settings) {
      PlacementSettings copy = settings.copy();
      this.configurator.accept(copy);
      List<Iterator<PartialTile>> iterators = new ArrayList<>();
      iterators.add(this.root.getTiles(filter, copy));
      this.children.forEach(child -> iterators.add(child.getTiles(filter, copy)));
      return new FlatteningIterator<>(iterators.iterator());
   }

   @Override
   public Iterator<PartialEntity> getEntities(Predicate<PartialEntity> filter, PlacementSettings settings) {
      PlacementSettings copy = settings.copy();
      this.configurator.accept(copy);
      List<Iterator<PartialEntity>> iterators = new ArrayList<>();
      iterators.add(this.root.getEntities(filter, copy));
      this.children.forEach(child -> iterators.add(child.getEntities(filter, copy)));
      return new FlatteningIterator<>(iterators.iterator());
   }

   private Rotation getRotation(JigsawData from, JigsawData to, RandomSource random) {
      if (from.getFacing().getAxis() == Axis.Y) {
         return from.getJoint() != JointType.ROLLABLE && to.getJoint() != JointType.ROLLABLE
            ? this.getRotation(from.getSide(), to.getSide())
            : Rotation.values()[random.nextInt(4)];
      } else {
         return this.getRotation(from.getFacing(), to.getFacing().getOpposite());
      }
   }

   private Rotation getRotation(Direction from, Direction to) {
      return switch (from) {
         case NORTH -> {
            switch (to) {
               case NORTH:
                  yield Rotation.NONE;
               case SOUTH:
                  yield Rotation.CLOCKWISE_180;
               case WEST:
                  yield Rotation.COUNTERCLOCKWISE_90;
               case EAST:
                  yield Rotation.CLOCKWISE_90;
               default:
                  throw new UnsupportedOperationException();
            }
         }
         case SOUTH -> {
            switch (to) {
               case NORTH:
                  yield Rotation.CLOCKWISE_180;
               case SOUTH:
                  yield Rotation.NONE;
               case WEST:
                  yield Rotation.CLOCKWISE_90;
               case EAST:
                  yield Rotation.COUNTERCLOCKWISE_90;
               default:
                  throw new UnsupportedOperationException();
            }
         }
         case WEST -> {
            switch (to) {
               case NORTH:
                  yield Rotation.CLOCKWISE_90;
               case SOUTH:
                  yield Rotation.COUNTERCLOCKWISE_90;
               case WEST:
                  yield Rotation.NONE;
               case EAST:
                  yield Rotation.CLOCKWISE_180;
               default:
                  throw new UnsupportedOperationException();
            }
         }
         case EAST -> {
            switch (to) {
               case NORTH:
                  yield Rotation.COUNTERCLOCKWISE_90;
               case SOUTH:
                  yield Rotation.CLOCKWISE_90;
               case WEST:
                  yield Rotation.CLOCKWISE_180;
               case EAST:
                  yield Rotation.NONE;
               default:
                  throw new UnsupportedOperationException();
            }
         }
         default -> throw new UnsupportedOperationException();
      };
   }
}
