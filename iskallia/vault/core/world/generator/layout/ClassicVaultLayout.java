package iskallia.vault.core.world.generator.layout;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.template.EmptyTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;

public abstract class ClassicVaultLayout extends VaultLayout {
   public static final FieldRegistry FIELDS = VaultLayout.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<TemplatePoolKey> START_POOL = FieldKey.of("start_pool", TemplatePoolKey.class)
      .with(Version.v1_0, Adapter.ofRegistryKey(() -> VaultRegistry.TEMPLATE_POOL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<TemplatePoolKey> ROOM_POOL = FieldKey.of("room_pool", TemplatePoolKey.class)
      .with(Version.v1_0, Adapter.ofRegistryKey(() -> VaultRegistry.TEMPLATE_POOL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<TemplatePoolKey> TUNNEL_POOL = FieldKey.of("tunnel_pool", TemplatePoolKey.class)
      .with(Version.v1_0, Adapter.ofRegistryKey(() -> VaultRegistry.TEMPLATE_POOL), DISK.all())
      .register(FIELDS);

   protected ClassicVaultLayout() {
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public Template getTemplate(VaultLayout.PieceType type, Vault vault, RegionPos region, RandomSource random, PlacementSettings settings) {
      return (Template)(switch (this.getType(vault, region)) {
         case NONE -> EmptyTemplate.INSTANCE;
         case START -> this.getStart(this.get(START_POOL), vault.get(Vault.VERSION), region, random, vault.get(Vault.WORLD).get(WorldManager.FACING), settings);
         case START_NORTH -> this.getStart(this.get(START_POOL), vault.get(Vault.VERSION), region, random, Direction.NORTH, settings);
         case START_SOUTH -> this.getStart(this.get(START_POOL), vault.get(Vault.VERSION), region, random, Direction.SOUTH, settings);
         case START_WEST -> this.getStart(this.get(START_POOL), vault.get(Vault.VERSION), region, random, Direction.WEST, settings);
         case START_EAST -> this.getStart(this.get(START_POOL), vault.get(Vault.VERSION), region, random, Direction.EAST, settings);
         case ROOM -> this.getRoom(this.get(ROOM_POOL), vault.get(Vault.VERSION), region, random, settings);
         case TUNNEL_X -> this.getTunnel(this.get(TUNNEL_POOL), vault.get(Vault.VERSION), region, random, Axis.X, settings);
         case TUNNEL_Z -> this.getTunnel(this.get(TUNNEL_POOL), vault.get(Vault.VERSION), region, random, Axis.Z, settings);
      });
   }
}
