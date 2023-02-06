package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.influence.LegacyInfluences;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.overlay.VaultOverlay;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.vault.stat.StatsCollector;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.vault.modifier.modifier.GameControlsModifier;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;

public class Vault extends DataObject<Vault> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Version> VERSION = FieldKey.of("version", Version.class)
      .with(Version.v1_0, Adapter.ofEnum(Version.class), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<UUID> ID = FieldKey.of("id", UUID.class).with(Version.v1_0, Adapter.ofUUID(), DISK.all().or(CLIENT.all())).register(FIELDS);
   public static final FieldKey<Long> SEED = FieldKey.of("seed", Long.class).with(Version.v1_0, Adapter.ofLong(), DISK.all()).register(FIELDS);
   public static final FieldKey<UUID> OWNER = FieldKey.of("owner", UUID.class).with(Version.v1_0, Adapter.ofUUID(), DISK.all()).register(FIELDS);
   public static final FieldKey<VaultLevel> LEVEL = FieldKey.of("level", VaultLevel.class)
      .with(Version.v1_0, Adapter.ofCompound(VaultLevel::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<TickClock> CLOCK = FieldKey.of("clock", TickClock.class)
      .with(Version.v1_0, Adapter.ofRegistryValue(() -> VaultRegistry.CLOCK, ISupplierKey::getKey, Supplier::get), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<WorldManager> WORLD = FieldKey.of("world_manager", WorldManager.class)
      .with(Version.v1_0, Adapter.ofCompound(WorldManager::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<VaultOverlay> OVERLAY = FieldKey.of("overlay", VaultOverlay.class)
      .with(Version.v1_0, Adapter.ofCompound(VaultOverlay::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Void> FINISHED = FieldKey.of("finished", Void.class)
      .with(Version.v1_0, Adapter.ofVoid(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Listeners> LISTENERS = FieldKey.of("listeners", Listeners.class)
      .with(Version.v1_0, Adapter.ofCompound(), DISK.all().or(CLIENT.all()), Listeners::new)
      .register(FIELDS);
   public static final FieldKey<Objectives> OBJECTIVES = FieldKey.of("objectives", Objectives.class)
      .with(Version.v1_0, Adapter.ofCompound(), DISK.all().or(CLIENT.all()), Objectives::new)
      .register(FIELDS);
   public static final FieldKey<Modifiers> MODIFIERS = FieldKey.of("modifiers", Modifiers.class)
      .with(Version.v1_0, Adapter.ofCompound(), DISK.all().or(CLIENT.all()), Modifiers::new)
      .register(FIELDS);
   public static final FieldKey<LegacyInfluences> INFLUENCES = FieldKey.of("influences", LegacyInfluences.class)
      .with(Version.v1_0, Adapter.ofCompound(), DISK.all().or(CLIENT.all()), LegacyInfluences::new)
      .register(FIELDS);
   public static final FieldKey<StatsCollector> STATS = FieldKey.of("stats", StatsCollector.class)
      .with(Version.v1_0, Adapter.ofCompound(), DISK.all(), StatsCollector::new)
      .register(FIELDS);
   public static final FieldKey<DiscoveryGoalsManager> DISCOVERY = FieldKey.of("discovery", DiscoveryGoalsManager.class)
      .with(Version.v1_8, Adapter.ofCompound(), DISK.all(), DiscoveryGoalsManager::new)
      .register(FIELDS);
   public static final FieldKey<CompoundTag> CRYSTAL = FieldKey.of("crystal", CompoundTag.class)
      .with(Version.v1_2, Adapter.ofNBT(CompoundTag.class), DISK.all())
      .register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world) {
      CommonEvents.SERVER_TICK.at(Phase.END).register(this, data -> this.tickServer(world));
      this.ifPresent(WORLD, worldSettings -> worldSettings.initServer(world, this));
      this.ifPresent(OBJECTIVES, objectives -> objectives.initServer(world, this));
      this.ifPresent(MODIFIERS, modifiers -> modifiers.initServer(world, this));
      this.ifPresent(LISTENERS, listeners -> listeners.initServer(world, this));
      this.ifPresent(INFLUENCES, influences -> influences.initServer(world, this));
      this.ifPresent(DISCOVERY, discovery -> discovery.initServer(world, this));
      this.ifPresent(STATS, stats -> stats.initServer(world, this));
   }

   protected void tickServer(VirtualWorld world) {
      this.ifPresent(WORLD, worldSettings -> worldSettings.tickServer(world, this));
      this.ifPresent(OBJECTIVES, objectives -> objectives.tickServer(world, this));
      this.ifPresent(CLOCK, clock -> clock.tickServer(world));
      this.ifPresent(MODIFIERS, modifiers -> modifiers.tickServer(world, this));
      this.ifPresent(LISTENERS, listeners -> listeners.tickServer(world, this));
      this.ifPresent(INFLUENCES, influences -> influences.tickServer(world, this));
   }

   public void releaseServer() {
      CommonEvents.release(this);
      this.ifPresent(WORLD, WorldManager::releaseServer);
      this.ifPresent(OBJECTIVES, Objectives::releaseServer);
      this.ifPresent(MODIFIERS, Modifiers::releaseServer);
      this.ifPresent(LISTENERS, Listeners::releaseServer);
      this.ifPresent(INFLUENCES, LegacyInfluences::releaseServer);
      this.ifPresent(DISCOVERY, DiscoveryGoalsManager::releaseServer);
      this.ifPresent(STATS, StatsCollector::releaseServer);
   }

   @OnlyIn(Dist.CLIENT)
   public void initClient() {
      ClientEvents.CLIENT_TICK.at(Phase.END).register(this, data -> this.tickClient());
      this.ifPresent(WORLD, worldManager -> worldManager.initClient(this));
      this.ifPresent(OVERLAY, overlay -> overlay.initClient(this));
      this.get(MODIFIERS).getModifiers().stream().filter(m -> m instanceof GameControlsModifier).forEach(m -> {
         GameControlsModifier modifier = (GameControlsModifier)m;
         GameControlsModifier.Properties currentProps = ClientVaults.CONTROLS_PROPERTIES;
         currentProps.setForward(currentProps.canMoveForward() && modifier.properties().canMoveForward());
         currentProps.setBackward(currentProps.canMoveBackward() && modifier.properties().canMoveBackward());
         currentProps.setJump(currentProps.canJump() && modifier.properties().canJump());
         currentProps.setSwapLeftAndRight(currentProps.isLeftAndRightSwapped() || modifier.properties().isLeftAndRightSwapped());
      });
   }

   @OnlyIn(Dist.CLIENT)
   protected void tickClient() {
      this.ifPresent(CLOCK, TickClock::tickClient);
      if (this.has(FINISHED)) {
         this.releaseClient();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void releaseClient() {
      CommonEvents.release(this);
      ClientEvents.release(this);
      ClientVaults.ACTIVE = new Vault();
      ClientVaults.CONTROLS_PROPERTIES = new GameControlsModifier.Properties(true, true, true, false);
   }
}
