package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.adapter.vault.RegistryValueAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.ThemeKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.world.generator.VaultGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldManager extends DataObject<WorldManager> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<ResourceLocation> KEY = FieldKey.of("key", ResourceLocation.class)
      .with(Version.v1_0, Adapters.IDENTIFIER, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Direction> FACING = FieldKey.of("facing", Direction.class)
      .with(Version.v1_0, Adapters.ofEnum(Direction.class, EnumAdapter.Mode.ORDINAL), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> RANDOM_TICK_SPEED = FieldKey.of("random_tick_speed", Integer.class)
      .with(Version.v1_0, Adapters.ofBoundedInt(0, 31), DISK.all())
      .register(FIELDS);
   public static final FieldKey<ResourceLocation> THEME = FieldKey.of("theme", ResourceLocation.class)
      .with(Version.v1_11, Adapters.IDENTIFIER, DISK.all())
      .register(FIELDS);
   public static final FieldKey<LootLogic> LOOT_LOGIC = FieldKey.of("loot_logic", LootLogic.class)
      .with(Version.v1_0, RegistryValueAdapter.of(() -> VaultRegistry.CHEST_LOGIC, ISupplierKey::getKey, Supplier::get), DISK.all())
      .register(FIELDS);
   public static final FieldKey<PortalLogic> PORTAL_LOGIC = FieldKey.of("portal_logic", PortalLogic.class)
      .with(Version.v1_0, RegistryValueAdapter.of(() -> VaultRegistry.PORTAL_LOGIC, ISupplierKey::getKey, Supplier::get), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<MobLogic> MOB_LOGIC = FieldKey.of("mob_logic", MobLogic.class)
      .with(Version.v1_0, RegistryValueAdapter.of(() -> VaultRegistry.MOB_LOGIC, ISupplierKey::getKey, Supplier::get), DISK.all())
      .register(FIELDS);
   public static final FieldKey<VaultGenerator> GENERATOR = FieldKey.of("generator", VaultGenerator.class)
      .with(Version.v1_0, RegistryValueAdapter.of(() -> VaultRegistry.GENERATOR, ISupplierKey::getKey, Supplier::get), DISK.all())
      .register(FIELDS);
   public static final FieldKey<WorldRenderer> RENDERER = FieldKey.of("renderer", WorldRenderer.class)
      .with(Version.v1_0, CompoundAdapter.of(WorldRenderer::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      this.set(KEY, world.dimension().location());
      this.ifPresent(RANDOM_TICK_SPEED, world::setRandomTickSpeed);
      this.ifPresent(GENERATOR, generator -> generator.initServer(world, vault));
      this.ifPresent(LOOT_LOGIC, lootLogic -> lootLogic.initServer(world, vault));
      this.ifPresent(MOB_LOGIC, mobLogic -> mobLogic.initServer(world, vault));
   }

   public void tickServer(VirtualWorld world, Vault vault) {
      this.ifPresent(RANDOM_TICK_SPEED, world::setRandomTickSpeed);
      this.ifPresent(GENERATOR, generator -> generator.tickServer(world, vault));
      world.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(1, 1), 1, new BlockPos(24, 0, 24));
   }

   public void releaseServer() {
      this.ifPresent(MOB_LOGIC, MobLogic::releaseServer);
      this.ifPresent(GENERATOR, VaultGenerator::releaseServer);
   }

   @OnlyIn(Dist.CLIENT)
   public void initClient(Vault vault) {
      this.ifPresent(RENDERER, renderer -> renderer.initClient(vault));
   }

   public WorldManager setTheme(ThemeKey theme, Version version) {
      this.set(THEME, theme.getId());
      this.ifPresent(RENDERER, renderer -> renderer.setTheme(theme.get(version)));
      return this;
   }
}
