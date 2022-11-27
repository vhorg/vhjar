package iskallia.vault.core.world.generator.layout;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.processor.entity.EntityProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.core.world.template.EmptyTemplate;
import iskallia.vault.core.world.template.JigsawTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import iskallia.vault.core.world.template.data.TemplateEntry;
import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public abstract class VaultLayout extends GridLayout {
   public static final FieldRegistry FIELDS = GridLayout.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_0, Adapter.ofFloat(), DISK.all())
      .register(FIELDS);

   @Override
   public void initServer(VirtualWorld world, Vault vault, GridGenerator generator) {
      CommonEvents.NOISE_GENERATION.in(world).register(vault, data -> {
         MutableBlockPos pos = new MutableBlockPos();
         BlockState state = ModBlocks.VAULT_BEDROCK.defaultBlockState();

         for (int y = 63; y >= 0; y--) {
            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  data.getChunk().setBlockState(pos.set(x, y, z), state, false);
               }
            }
         }
      });
   }

   @Override
   public Template getAt(Vault vault, RegionPos region, RandomSource random, PlacementSettings settings) {
      Template template = this.getTemplate(this.getType(vault, region), vault, region, random, settings);
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
         double var15 = CommonEvents.OBJECTIVE_PIECE_GENERATION.invoke(vault, probability).getProbability();
         if (random.nextFloat() < var15 && target != null) {
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

   public Template getStart(TemplatePoolKey pool, Version version, RegionPos region, RandomSource random, Direction facing, PlacementSettings settings) {
      TemplateEntry entry = pool.get(version).getRandomFlat(version, random).orElse(null);
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

   public Template getRoom(TemplatePoolKey pool, Version version, RegionPos region, RandomSource random, PlacementSettings settings) {
      TemplateEntry entry = pool.get(version).getRandomFlat(version, random).orElse(null);
      if (entry == null) {
         return EmptyTemplate.INSTANCE;
      } else {
         Mirror mirror = random.nextBoolean() ? Mirror.NONE : Mirror.FRONT_BACK;
         Rotation rotation = new Rotation[]{Rotation.NONE, Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180}[random.nextInt(4)];
         BlockPos pos = region.toBlockPos().above(9);
         settings.addProcessors(
            TileProcessor.mirror(mirror, 23, 23, true),
            TileProcessor.rotate(rotation, 23, 23, true),
            TileProcessor.translate(pos),
            EntityProcessor.mirror(mirror, 23, 23, true),
            EntityProcessor.rotate(rotation, 23, 23, true),
            EntityProcessor.translate(pos)
         );
         return JigsawTemplate.of(version, entry, 10, random);
      }
   }

   public Template getTunnel(TemplatePoolKey pool, Version version, RegionPos region, RandomSource random, Axis axis, PlacementSettings settings) {
      TemplateEntry entry = pool.get(version).getRandomFlat(version, random).orElse(null);
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

   public static enum PieceType {
      NONE,
      START,
      START_NORTH,
      START_SOUTH,
      START_WEST,
      START_EAST,
      ROOM,
      TUNNEL_X,
      TUNNEL_Z;

      public boolean isStart() {
         return this == START || this == START_NORTH || this == START_SOUTH || this == START_WEST || this == START_EAST;
      }

      public boolean isTunnel() {
         return this == TUNNEL_X || this == TUNNEL_Z;
      }

      public boolean connectsToTunnel() {
         return this.isStart() || this == ROOM;
      }

      public VaultLayout.PieceType rotate(Rotation rotation) {
         return switch (this) {
            case NONE -> NONE;
            case START -> START;
            case START_NORTH -> ofStart(rotation.rotate(Direction.NORTH));
            case START_SOUTH -> ofStart(rotation.rotate(Direction.SOUTH));
            case START_WEST -> ofStart(rotation.rotate(Direction.WEST));
            case START_EAST -> ofStart(rotation.rotate(Direction.EAST));
            case ROOM -> ROOM;
            case TUNNEL_X -> ofTunnel(rotation.rotate(Direction.WEST));
            case TUNNEL_Z -> ofTunnel(rotation.rotate(Direction.NORTH));
         };
      }

      public static VaultLayout.PieceType ofStart(Direction facing) {
         return switch (facing) {
            case NORTH -> START_NORTH;
            case EAST -> START_EAST;
            case WEST -> START_WEST;
            case SOUTH -> START_SOUTH;
            default -> throw new UnsupportedOperationException("Start cannot face " + facing);
         };
      }

      public static VaultLayout.PieceType ofTunnel(Direction direction) {
         return ofTunnel(direction.getAxis());
      }

      public static VaultLayout.PieceType ofTunnel(Axis axis) {
         return switch (axis) {
            case X -> TUNNEL_X;
            case Z -> TUNNEL_Z;
            default -> throw new UnsupportedOperationException("Tunnel cannot be aligned on " + axis);
         };
      }
   }
}
