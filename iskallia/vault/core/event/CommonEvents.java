package iskallia.vault.core.event;

import iskallia.vault.core.event.common.AltarProgressEvent;
import iskallia.vault.core.event.common.ArtifactChanceEvent;
import iskallia.vault.core.event.common.BlockBreakSpeedEvent;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.event.common.CarversGenerationEvent;
import iskallia.vault.core.event.common.ChampionPromoteEvent;
import iskallia.vault.core.event.common.ChestCatalystGenerationEvent;
import iskallia.vault.core.event.common.ChestGenerationEvent;
import iskallia.vault.core.event.common.ChestTrapGenerationEvent;
import iskallia.vault.core.event.common.ClockModifierEvent;
import iskallia.vault.core.event.common.CoinStacksGenerationEvent;
import iskallia.vault.core.event.common.CrateAwardEvent;
import iskallia.vault.core.event.common.EffectAddedEvent;
import iskallia.vault.core.event.common.EffectCheckEvent;
import iskallia.vault.core.event.common.EntityChainAttackedEvent;
import iskallia.vault.core.event.common.EntityCheckSpawnEvent;
import iskallia.vault.core.event.common.EntityCreationEvent;
import iskallia.vault.core.event.common.EntityDamageBlockEvent;
import iskallia.vault.core.event.common.EntityDamageEvent;
import iskallia.vault.core.event.common.EntityDeathEvent;
import iskallia.vault.core.event.common.EntityDropsEvent;
import iskallia.vault.core.event.common.EntityHealEvent;
import iskallia.vault.core.event.common.EntityInitializationEvent;
import iskallia.vault.core.event.common.EntityJoinEvent;
import iskallia.vault.core.event.common.EntityLeechEvent;
import iskallia.vault.core.event.common.EntityPlaceEvent;
import iskallia.vault.core.event.common.EntityReadEvent;
import iskallia.vault.core.event.common.EntitySpawnEvent;
import iskallia.vault.core.event.common.EntityStunnedEvent;
import iskallia.vault.core.event.common.EntityTickEvent;
import iskallia.vault.core.event.common.FruitEatenEvent;
import iskallia.vault.core.event.common.GrantedEffectEvent;
import iskallia.vault.core.event.common.ListenerJoinEvent;
import iskallia.vault.core.event.common.ListenerLeaveEvent;
import iskallia.vault.core.event.common.LootGenerationEvent;
import iskallia.vault.core.event.common.LootableBlockGenerationEvent;
import iskallia.vault.core.event.common.NoiseGenerationEvent;
import iskallia.vault.core.event.common.ObjectivePieceGenerationEvent;
import iskallia.vault.core.event.common.PlaceholderGenerationEvent;
import iskallia.vault.core.event.common.PlayerContainerCloseEvent;
import iskallia.vault.core.event.common.PlayerContainerOpenEvent;
import iskallia.vault.core.event.common.PlayerInteractEvent;
import iskallia.vault.core.event.common.PlayerMineEvent;
import iskallia.vault.core.event.common.PlayerRegenEvent;
import iskallia.vault.core.event.common.PlayerStatEvent;
import iskallia.vault.core.event.common.PlayerTickEvent;
import iskallia.vault.core.event.common.ScavengerAltarConsumeEvent;
import iskallia.vault.core.event.common.ServerTickEvent;
import iskallia.vault.core.event.common.ShopPedestalGenerationEvent;
import iskallia.vault.core.event.common.SoulShardChanceEvent;
import iskallia.vault.core.event.common.SpawnGenerationEvent;
import iskallia.vault.core.event.common.SurfaceGenerationEvent;
import iskallia.vault.core.event.common.TemplateGenerationEvent;
import iskallia.vault.core.event.common.TreasureRoomOpenEvent;
import iskallia.vault.core.event.common.VaultEndEvent;
import iskallia.vault.core.event.common.VaultPortalCollideEvent;
import iskallia.vault.core.event.common.VaultStartEvent;
import iskallia.vault.core.event.common.ZombieReinforcementEvent;
import java.util.ArrayList;
import java.util.List;

public class CommonEvents {
   public static final List<Event<?, ?>> REGISTRY = new ArrayList<>();
   public static final ServerTickEvent SERVER_TICK = register(new ServerTickEvent());
   public static final NoiseGenerationEvent NOISE_GENERATION = register(new NoiseGenerationEvent());
   public static final SurfaceGenerationEvent SURFACE_GENERATION = register(new SurfaceGenerationEvent());
   public static final CarversGenerationEvent CARVERS_GENERATION = register(new CarversGenerationEvent());
   public static final SpawnGenerationEvent SPAWN_GENERATION = register(new SpawnGenerationEvent());
   public static final LootGenerationEvent LOOT_GENERATION = register(new LootGenerationEvent());
   public static final TemplateGenerationEvent TEMPLATE_GENERATION = register(new TemplateGenerationEvent());
   public static final PlaceholderGenerationEvent PLACEHOLDER_GENERATION = register(new PlaceholderGenerationEvent());
   public static final ObjectivePieceGenerationEvent OBJECTIVE_PIECE_GENERATION = register(new ObjectivePieceGenerationEvent());
   public static final ChestGenerationEvent CHEST_LOOT_GENERATION = register(new ChestGenerationEvent());
   public static final ShopPedestalGenerationEvent SHOP_PEDESTAL_LOOT_GENERATION = register(new ShopPedestalGenerationEvent());
   public static final ChestTrapGenerationEvent CHEST_TRAP_GENERATION = register(new ChestTrapGenerationEvent());
   public static final ChestCatalystGenerationEvent CHEST_CATALYST_GENERATION = register(new ChestCatalystGenerationEvent());
   public static final CoinStacksGenerationEvent COIN_STACK_LOOT_GENERATION = register(new CoinStacksGenerationEvent());
   public static final LootableBlockGenerationEvent LOOTABLE_BLOCK_GENERATION_EVENT = register(new LootableBlockGenerationEvent());
   public static final ChampionPromoteEvent CHAMPION_PROMOTE = register(new ChampionPromoteEvent());
   public static final CrateAwardEvent CRATE_AWARD_EVENT = register(new CrateAwardEvent());
   public static final BlockUseEvent BLOCK_USE = register(new BlockUseEvent());
   public static final BlockSetEvent BLOCK_SET = register(new BlockSetEvent());
   public static final BlockBreakSpeedEvent BLOCK_BREAK_SPEED = register(new BlockBreakSpeedEvent());
   public static final VaultPortalCollideEvent VAULT_PORTAL_COLLIDE = register(new VaultPortalCollideEvent());
   public static final AltarProgressEvent ALTAR_PROGRESS = register(new AltarProgressEvent());
   public static final EntityCreationEvent ENTITY_CREATION = register(new EntityCreationEvent());
   public static final EntityCheckSpawnEvent ENTITY_CHECK_SPAWN = register(new EntityCheckSpawnEvent());
   public static final EntityInitializationEvent ENTITY_INITIALIZE = register(new EntityInitializationEvent());
   public static final EntityReadEvent ENTITY_READ = register(new EntityReadEvent());
   public static final EntityJoinEvent ENTITY_JOIN = register(new EntityJoinEvent());
   public static final EntitySpawnEvent ENTITY_SPAWN = register(new EntitySpawnEvent());
   public static final EntityTickEvent ENTITY_TICK = register(new EntityTickEvent());
   public static final EntityPlaceEvent ENTITY_PLACE = register(new EntityPlaceEvent());
   public static final EntityDamageEvent ENTITY_DAMAGE = register(new EntityDamageEvent());
   public static final EntityDropsEvent ENTITY_DROPS = register(new EntityDropsEvent());
   public static final EntityDeathEvent ENTITY_DEATH = register(new EntityDeathEvent());
   public static final EntityHealEvent ENTITY_HEAL = register(new EntityHealEvent());
   public static final ZombieReinforcementEvent ZOMBIE_REINFORCEMENT = register(new ZombieReinforcementEvent());
   public static final EntityStunnedEvent ENTITY_STUNNED = register(new EntityStunnedEvent());
   public static final EntityChainAttackedEvent ENTITY_CHAIN_ATTACKED = register(new EntityChainAttackedEvent());
   public static final EntityDamageBlockEvent ENTITY_DAMAGE_BLOCK = register(new EntityDamageBlockEvent());
   public static final EntityLeechEvent ENTITY_LEECH = register(new EntityLeechEvent());
   public static final FruitEatenEvent FRUIT_EATEN = register(new FruitEatenEvent());
   public static final EffectCheckEvent EFFECT_CHECK = register(new EffectCheckEvent());
   public static final EffectAddedEvent EFFECT_ADD = register(new EffectAddedEvent());
   public static final ClockModifierEvent CLOCK_MODIFIER = register(new ClockModifierEvent());
   public static final GrantedEffectEvent GRANTED_EFFECT = register(new GrantedEffectEvent());
   public static final ArtifactChanceEvent ARTIFACT_CHANCE = register(new ArtifactChanceEvent());
   public static final PlayerTickEvent PLAYER_TICK = register(new PlayerTickEvent());
   public static final PlayerInteractEvent PLAYER_INTERACT = register(new PlayerInteractEvent());
   public static final PlayerMineEvent PLAYER_MINE = register(new PlayerMineEvent());
   public static final PlayerStatEvent PLAYER_STAT = register(new PlayerStatEvent());
   public static final PlayerRegenEvent PLAYER_REGEN = register(new PlayerRegenEvent());
   public static final PlayerContainerOpenEvent PLAYER_CONTAINER_OPEN = register(new PlayerContainerOpenEvent());
   public static final PlayerContainerCloseEvent PLAYER_CONTAINER_CLOSE = register(new PlayerContainerCloseEvent());
   public static final ScavengerAltarConsumeEvent SCAVENGER_ALTAR_CONSUME = register(new ScavengerAltarConsumeEvent());
   public static final TreasureRoomOpenEvent TREASURE_ROOM_OPEN = register(new TreasureRoomOpenEvent());
   public static final SoulShardChanceEvent SOUL_SHARD_CHANCE = register(new SoulShardChanceEvent());
   public static final VaultStartEvent VAULT_START = register(new VaultStartEvent());
   public static final VaultEndEvent VAULT_END = register(new VaultEndEvent());
   public static final ListenerJoinEvent LISTENER_JOIN = register(new ListenerJoinEvent());
   public static final ListenerLeaveEvent LISTENER_LEAVE = register(new ListenerLeaveEvent());

   public static void release(Object reference) {
      REGISTRY.forEach(event -> event.release(reference));
   }

   private static <T extends Event<?, ?>> T register(T event) {
      REGISTRY.add(event);
      return event;
   }

   public static void init() {
   }
}
