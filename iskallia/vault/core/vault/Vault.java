package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.adapter.vault.LegacyNbtAdapter;
import iskallia.vault.core.data.adapter.vault.RegistryValueAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.abyss.LegacyAbyssManager;
import iskallia.vault.core.vault.enhancement.EnhancementTaskManager;
import iskallia.vault.core.vault.influence.LegacyInfluences;
import iskallia.vault.core.vault.modifier.modifier.GameControlsModifier;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.overlay.VaultOverlay;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.vault.stat.StatsCollector;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;

public class Vault extends DataObject<Vault> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Version> VERSION = FieldKey.of("version", Version.class)
      .with(Version.v1_0, Adapters.ofEnum(Version.class, EnumAdapter.Mode.ORDINAL), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<UUID> ID = FieldKey.of("id", UUID.class).with(Version.v1_0, Adapters.UUID, DISK.all().or(CLIENT.all())).register(FIELDS);
   public static final FieldKey<Long> SEED = FieldKey.of("seed", Long.class).with(Version.v1_0, Adapters.LONG, DISK.all()).register(FIELDS);
   public static final FieldKey<UUID> OWNER = FieldKey.of("owner", UUID.class).with(Version.v1_0, Adapters.UUID, DISK.all()).register(FIELDS);
   public static final FieldKey<VaultLevel> LEVEL = FieldKey.of("level", VaultLevel.class)
      .with(Version.v1_0, CompoundAdapter.of(VaultLevel::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<TickClock> CLOCK = FieldKey.of("clock", TickClock.class)
      .with(Version.v1_0, RegistryValueAdapter.of(() -> VaultRegistry.CLOCK, ISupplierKey::getKey, Supplier::get), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<WorldManager> WORLD = FieldKey.of("world_manager", WorldManager.class)
      .with(Version.v1_0, CompoundAdapter.of(WorldManager::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<VaultOverlay> OVERLAY = FieldKey.of("overlay", VaultOverlay.class)
      .with(Version.v1_0, CompoundAdapter.of(VaultOverlay::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Void> FINISHED = FieldKey.of("finished", Void.class)
      .with(Version.v1_0, Adapters.ofVoid(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Listeners> LISTENERS = FieldKey.of("listeners", Listeners.class)
      .with(Version.v1_0, CompoundAdapter.of(Listeners::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Objectives> OBJECTIVES = FieldKey.of("objectives", Objectives.class)
      .with(Version.v1_0, CompoundAdapter.of(Objectives::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Modifiers> MODIFIERS = FieldKey.of("modifiers", Modifiers.class)
      .with(Version.v1_0, CompoundAdapter.of(Modifiers::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<LegacyInfluences> INFLUENCES = FieldKey.of("influences", LegacyInfluences.class)
      .with(Version.v1_0, CompoundAdapter.of(LegacyInfluences::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<StatsCollector> STATS = FieldKey.of("stats", StatsCollector.class)
      .with(Version.v1_0, CompoundAdapter.of(StatsCollector::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<DiscoveryGoalsManager> DISCOVERY = FieldKey.of("discovery", DiscoveryGoalsManager.class)
      .with(Version.v1_8, CompoundAdapter.of(DiscoveryGoalsManager::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<QuestManager> QUESTS = FieldKey.of("quests", QuestManager.class)
      .with(Version.v1_16, CompoundAdapter.of(QuestManager::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<LegacyAbyssManager> ABYSS = FieldKey.of("abyssal", LegacyAbyssManager.class)
      .with(Version.v1_9, CompoundAdapter.of(LegacyAbyssManager::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<EnhancementTaskManager> ENHANCEMENT_TASKS = FieldKey.of("enhancement_tasks", EnhancementTaskManager.class)
      .with(Version.v1_12, CompoundAdapter.of(EnhancementTaskManager::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<CompoundTag> CRYSTAL = FieldKey.of("crystal", CompoundTag.class)
      .with(Version.v1_2, LegacyNbtAdapter.COMPOUND, DISK.all())
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
      this.ifPresent(DISCOVERY, discovery -> discovery.initServer(world, this));
      this.ifPresent(QUESTS, quests -> quests.initServer(world, this));
      this.ifPresent(STATS, stats -> stats.initServer(world, this));
      this.ifPresent(ENHANCEMENT_TASKS, tasksMgr -> tasksMgr.initServer(world, this));
   }

   protected void tickServer(VirtualWorld world) {
      this.ifPresent(WORLD, worldSettings -> worldSettings.tickServer(world, this));
      this.ifPresent(OBJECTIVES, objectives -> objectives.tickServer(world, this));
      this.ifPresent(CLOCK, clock -> clock.tickServer(world));
      this.ifPresent(MODIFIERS, modifiers -> modifiers.tickServer(world, this));
      this.ifPresent(LISTENERS, listeners -> listeners.tickServer(world, this));
   }

   public void releaseServer() {
      CommonEvents.release(this);
      this.ifPresent(WORLD, WorldManager::releaseServer);
      this.ifPresent(OBJECTIVES, Objectives::releaseServer);
      this.ifPresent(MODIFIERS, Modifiers::releaseServer);
      this.ifPresent(LISTENERS, Listeners::releaseServer);
      this.ifPresent(DISCOVERY, DiscoveryGoalsManager::releaseServer);
      this.ifPresent(QUESTS, QuestManager::releaseServer);
      this.ifPresent(STATS, StatsCollector::releaseServer);
      this.ifPresent(ENHANCEMENT_TASKS, EnhancementTaskManager::releaseServer);
   }

   @OnlyIn(Dist.CLIENT)
   public void initClient() {
      ClientEvents.CLIENT_TICK.at(Phase.END).register(this, data -> this.tickClient());
      this.ifPresent(WORLD, worldManager -> worldManager.initClient(this));
      this.ifPresent(OVERLAY, overlay -> overlay.initClient(this));
      this.ifPresent(OBJECTIVES, objectives -> objectives.initClient(this));
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
