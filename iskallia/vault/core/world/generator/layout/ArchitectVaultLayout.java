package iskallia.vault.core.world.generator.layout;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.adapter.vault.RegistryKeyAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.TemplateGenerationEvent;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.core.world.template.EmptyTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

public class ArchitectVaultLayout extends VaultLayout {
   public static final SupplierKey<GridLayout> KEY = SupplierKey.of("architect_vault", GridLayout.class).with(Version.v1_10, ArchitectVaultLayout::new);
   public static final FieldRegistry FIELDS = ClassicVaultLayout.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<TemplatePoolKey> START_POOL = FieldKey.of("start_pool", TemplatePoolKey.class)
      .with(Version.v1_0, RegistryKeyAdapter.of(() -> VaultRegistry.TEMPLATE_POOL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<TemplatePoolKey> COMMON_ROOM_POOL = FieldKey.of("common_room_pool", TemplatePoolKey.class)
      .with(Version.v1_0, RegistryKeyAdapter.of(() -> VaultRegistry.TEMPLATE_POOL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<TemplatePoolKey> CHALLENGE_ROOM_POOL = FieldKey.of("challenge_room_pool", TemplatePoolKey.class)
      .with(Version.v1_0, RegistryKeyAdapter.of(() -> VaultRegistry.TEMPLATE_POOL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<TemplatePoolKey> OMEGA_ROOM_POOL = FieldKey.of("omega_room_pool", TemplatePoolKey.class)
      .with(Version.v1_0, RegistryKeyAdapter.of(() -> VaultRegistry.TEMPLATE_POOL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<TemplatePoolKey> TUNNEL_POOL = FieldKey.of("tunnel_pool", TemplatePoolKey.class)
      .with(Version.v1_0, RegistryKeyAdapter.of(() -> VaultRegistry.TEMPLATE_POOL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Integer> TUNNEL_SPAN = FieldKey.of("tunnel_span", Integer.class).with(Version.v1_0, Adapters.INT, DISK.all()).register(FIELDS);
   public static final FieldKey<ArchitectRoomEntry.List> ROOM_ENTRIES = FieldKey.of("room_entries", ArchitectRoomEntry.List.class)
      .with(Version.v1_0, CompoundAdapter.of(ArchitectRoomEntry.List::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Float> COMPLETION = FieldKey.of("completion", Float.class).with(Version.v1_0, Adapters.FLOAT, DISK.all()).register(FIELDS);

   protected ArchitectVaultLayout() {
      this.set(ROOM_ENTRIES, new ArchitectRoomEntry.List());
   }

   public ArchitectVaultLayout(int tunnelSpan, Collection<ArchitectRoomEntry> roomEntries, float completion) {
      this();
      this.set(TUNNEL_SPAN, Integer.valueOf(tunnelSpan));
      this.get(ROOM_ENTRIES).addAll(roomEntries);
      this.set(COMPLETION, Float.valueOf(completion));
   }

   @Override
   public SupplierKey<GridLayout> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, GridGenerator generator) {
      super.initServer(world, vault, generator);
      CommonEvents.TEMPLATE_GENERATION
         .at(TemplateGenerationEvent.Phase.PRE)
         .in(world)
         .register(this, data -> data.getTemplate().getSettings().addProcessor(TileProcessor.of((tile, context) -> {
            tile.getState().asWhole().ifPresent(state -> {
               if (!ModConfigs.ARCHITECT.isWhitelisted(tile)) {
                  if (ModConfigs.ARCHITECT.isBlacklisted(tile)) {
                     tile.setState(PartialBlockState.of(Blocks.AIR));
                  } else if (context.getRandom(tile.getPos()).nextFloat() >= this.get(COMPLETION)) {
                     if (state.isCollisionShapeFullBlock(data.getWorld(), tile.getPos())) {
                        tile.setState(PartialBlockState.of(ModBlocks.VAULT_BEDROCK));
                     } else {
                        tile.setState(PartialBlockState.of(Blocks.AIR));
                     }
                  }
               }
            });
            return tile;
         })));
   }

   @Override
   public VaultLayout.PieceType getType(Vault vault, RegionPos region) {
      VaultLayout.PieceType type = this.getBaseType(vault, region);
      int unit = this.get(TUNNEL_SPAN) + 1;
      int x = region.getX();
      int z = region.getZ();
      if (type == VaultLayout.PieceType.ROOM) {
         int count = this.get(ROOM_ENTRIES).getTotalCount();
         Direction facing = vault.get(Vault.WORLD).get(WorldManager.FACING);
         int index = this.getSpiralIndex(x / unit, z / unit, facing, Rotation.CLOCKWISE_90) - 1;
         return index < count ? VaultLayout.PieceType.ROOM : VaultLayout.PieceType.NONE;
      } else {
         if (type == VaultLayout.PieceType.TUNNEL_X) {
            int xRoom1 = x - Math.floorMod(x, unit);
            int xRoom2 = xRoom1 + unit;
            if (!this.getType(vault, region.with(xRoom1, z)).connectsToTunnel()) {
               return VaultLayout.PieceType.NONE;
            }

            if (!this.getType(vault, region.with(xRoom2, z)).connectsToTunnel()) {
               return VaultLayout.PieceType.NONE;
            }
         } else if (type == VaultLayout.PieceType.TUNNEL_Z) {
            int zRoom1 = z - Math.floorMod(z, unit);
            int zRoom2 = zRoom1 + unit;
            if (!this.getType(vault, region.with(x, zRoom1)).connectsToTunnel()) {
               return VaultLayout.PieceType.NONE;
            }

            if (!this.getType(vault, region.with(x, zRoom2)).connectsToTunnel()) {
               return VaultLayout.PieceType.NONE;
            }
         }

         return type;
      }
   }

   public VaultLayout.PieceType getBaseType(Vault vault, RegionPos region) {
      int x = region.getX();
      int z = region.getZ();
      int unit = this.get(TUNNEL_SPAN) + 1;
      if (x == 0 && z == 0) {
         return VaultLayout.PieceType.START;
      } else if (x % unit == 0 && z % unit == 0) {
         return VaultLayout.PieceType.ROOM;
      } else if (x % unit != 0 && z % unit != 0) {
         return VaultLayout.PieceType.NONE;
      } else {
         Direction facing = vault.get(Vault.WORLD).get(WorldManager.FACING);
         int distance = Math.abs(x) + Math.abs(z);
         if (x % unit == 0) {
            return distance < unit && z * facing.getStepZ() <= 0 ? VaultLayout.PieceType.NONE : VaultLayout.PieceType.TUNNEL_Z;
         } else if (z % unit != 0) {
            throw new IllegalStateException("You have stumbled upon a number that doesn't exist");
         } else {
            return distance < unit && x * facing.getStepX() <= 0 ? VaultLayout.PieceType.NONE : VaultLayout.PieceType.TUNNEL_X;
         }
      }
   }

   @Override
   public Template getTemplate(VaultLayout.PieceType type, Vault vault, RegionPos region, RandomSource random, PlacementSettings settings) {
      Version version = vault.get(Vault.VERSION);

      return (Template)(switch (this.getType(vault, region)) {
         case NONE -> EmptyTemplate.INSTANCE;
         case START -> this.getStart(
            this.get(START_POOL).get(version), vault.get(Vault.VERSION), region, random, vault.get(Vault.WORLD).get(WorldManager.FACING), settings
         );
         case START_NORTH -> this.getStart(this.get(START_POOL).get(version), vault.get(Vault.VERSION), region, random, Direction.NORTH, settings);
         case START_SOUTH -> this.getStart(this.get(START_POOL).get(version), vault.get(Vault.VERSION), region, random, Direction.SOUTH, settings);
         case START_WEST -> this.getStart(this.get(START_POOL).get(version), vault.get(Vault.VERSION), region, random, Direction.WEST, settings);
         case START_EAST -> this.getStart(this.get(START_POOL).get(version), vault.get(Vault.VERSION), region, random, Direction.EAST, settings);
         case ROOM -> {
            int unit = this.get(TUNNEL_SPAN) + 1;
            int x = region.getX();
            int z = region.getZ();
            Direction facing = vault.get(Vault.WORLD).get(WorldManager.FACING);
            int index = this.getSpiralIndex(x / unit, z / unit, facing, Rotation.CLOCKWISE_90) - 1;
            List<TemplatePoolKey> entries = this.get(ROOM_ENTRIES).flatten(this);
            Collections.shuffle(entries, new Random(vault.get(Vault.SEED)));
            yield this.getRoom(entries.get(index).get(version), vault.get(Vault.VERSION), region, random, settings);
         }
         case TUNNEL_X -> this.getTunnel(this.get(TUNNEL_POOL).get(version), vault.get(Vault.VERSION), region, random, Axis.X, settings);
         case TUNNEL_Z -> this.getTunnel(this.get(TUNNEL_POOL).get(version), vault.get(Vault.VERSION), region, random, Axis.Z, settings);
      });
   }

   public int getSpiralIndex(int x, int z, Direction facing, Rotation rotation) {
      switch (facing) {
         case SOUTH:
            if (rotation == Rotation.COUNTERCLOCKWISE_90) {
               x *= -1;
            }
            break;
         case NORTH:
            if (rotation == Rotation.CLOCKWISE_180) {
               x *= -1;
            }

            z *= -1;
            break;
         case EAST: {
            int temp = x;
            x = rotation == Rotation.CLOCKWISE_90 ? -z : z;
            z = temp;
            break;
         }
         case WEST: {
            int temp = x;
            x = rotation == Rotation.COUNTERCLOCKWISE_90 ? -z : z;
            z = -temp;
         }
      }

      int u = x + z;
      int v = x - z;
      int p;
      if (u > 0) {
         if (v >= 0) {
            int var8;
            p = (var8 = x << 1) * (var8 - 1) + v;
         } else {
            int var10;
            p = (var10 = z << 1) * (var10 - 1) + v;
         }
      } else if (v < 0) {
         int var9;
         p = -(var9 = x << 1) * (1 - var9) - v;
      } else {
         int var11;
         p = -(var11 = z << 1) * (1 - var11) - v;
      }

      return p;
   }
}
