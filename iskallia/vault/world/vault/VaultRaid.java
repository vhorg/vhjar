package iskallia.vault.world.vault;

import iskallia.vault.VaultMod;
import iskallia.vault.attribute.BlockPosAttribute;
import iskallia.vault.attribute.BooleanAttribute;
import iskallia.vault.attribute.BoundingBoxAttribute;
import iskallia.vault.attribute.CompoundAttribute;
import iskallia.vault.attribute.EnumAttribute;
import iskallia.vault.attribute.IntegerAttribute;
import iskallia.vault.attribute.RegistryKeyAttribute;
import iskallia.vault.attribute.StringAttribute;
import iskallia.vault.attribute.UUIDAttribute;
import iskallia.vault.attribute.VAttribute;
import iskallia.vault.entity.LegacyEntityScaler;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.nbt.NonNullVListNBT;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.network.message.VaultModifierMessage;
import iskallia.vault.network.message.VaultOverlayMessage;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.DiscoveredRelicsData;
import iskallia.vault.world.data.PhoenixModifierSnapshotData;
import iskallia.vault.world.data.PhoenixSetSnapshotData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.SoulboundSnapshotData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.raid.RaidProperties;
import iskallia.vault.world.vault.event.VaultEvent;
import iskallia.vault.world.vault.event.VaultListener;
import iskallia.vault.world.vault.gen.ArchitectEventGenerator;
import iskallia.vault.world.vault.gen.FinalLobbyGenerator;
import iskallia.vault.world.vault.gen.FragmentedVaultGenerator;
import iskallia.vault.world.vault.gen.RaidChallengeGenerator;
import iskallia.vault.world.vault.gen.VaultGenerator;
import iskallia.vault.world.vault.gen.VaultTroveGenerator;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutRegistry;
import iskallia.vault.world.vault.gen.piece.FinalVaultLobby;
import iskallia.vault.world.vault.gen.piece.VaultObelisk;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultPortal;
import iskallia.vault.world.vault.gen.piece.VaultRaidRoom;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.gen.piece.VaultStart;
import iskallia.vault.world.vault.gen.piece.VaultTreasure;
import iskallia.vault.world.vault.gen.piece.VaultTunnel;
import iskallia.vault.world.vault.influence.MobAttributeInfluence;
import iskallia.vault.world.vault.influence.VaultInfluenceRegistry;
import iskallia.vault.world.vault.influence.VaultInfluences;
import iskallia.vault.world.vault.logic.VaultChestPity;
import iskallia.vault.world.vault.logic.VaultCowOverrides;
import iskallia.vault.world.vault.logic.VaultInfluenceHandler;
import iskallia.vault.world.vault.logic.VaultLobby;
import iskallia.vault.world.vault.logic.VaultLogic;
import iskallia.vault.world.vault.logic.VaultSandEvent;
import iskallia.vault.world.vault.logic.VaultSpawner;
import iskallia.vault.world.vault.logic.behaviour.VaultBehaviour;
import iskallia.vault.world.vault.logic.condition.VaultCondition;
import iskallia.vault.world.vault.logic.objective.CakeHuntObjective;
import iskallia.vault.world.vault.logic.objective.LegacyScavengerHuntObjective;
import iskallia.vault.world.vault.logic.objective.SummonAndKillAllBossesObjective;
import iskallia.vault.world.vault.logic.objective.SummonAndKillBossObjective;
import iskallia.vault.world.vault.logic.objective.TreasureHuntObjective;
import iskallia.vault.world.vault.logic.objective.TroveObjective;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.ancient.AncientObjective;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import iskallia.vault.world.vault.logic.objective.raid.RaidChallengeObjective;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.VaultModifiers;
import iskallia.vault.world.vault.modifier.modifier.MobAttributeModifier;
import iskallia.vault.world.vault.modifier.modifier.MobFrenzyModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerNoExitModifier;
import iskallia.vault.world.vault.modifier.modifier.deprecated.ScaleModifier;
import iskallia.vault.world.vault.modifier.spi.IVaultModifierStack;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import iskallia.vault.world.vault.player.VaultMember;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultPlayerType;
import iskallia.vault.world.vault.player.VaultRunner;
import iskallia.vault.world.vault.player.VaultSpectator;
import iskallia.vault.world.vault.time.VaultTimer;
import iskallia.vault.world.vault.time.extension.AccelerationExtension;
import iskallia.vault.world.vault.time.extension.FallbackExtension;
import iskallia.vault.world.vault.time.extension.FavourExtension;
import iskallia.vault.world.vault.time.extension.FruitExtension;
import iskallia.vault.world.vault.time.extension.ModifierExtension;
import iskallia.vault.world.vault.time.extension.RelicExtension;
import iskallia.vault.world.vault.time.extension.RoomGenerationExtension;
import iskallia.vault.world.vault.time.extension.SandExtension;
import iskallia.vault.world.vault.time.extension.TimeAltarExtension;
import iskallia.vault.world.vault.time.extension.TimeExtension;
import iskallia.vault.world.vault.time.extension.WinExtension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class VaultRaid implements INBTSerializable<CompoundTag> {
   protected VaultTimer timer = new VaultTimer().start(Integer.MAX_VALUE);
   protected VaultGenerator generator;
   protected VaultTask initializer;
   protected VaultModifiers modifiers = new VaultModifiers();
   protected RaidProperties properties = new RaidProperties();
   protected VaultInfluences influence = new VaultInfluences();
   protected ActiveRaid activeRaid = null;
   protected final VListNBT<VaultObjective, CompoundTag> objectives = VListNBT.of(VaultObjective::fromNBT);
   protected final VListNBT<VaultEvent<?>, CompoundTag> events = (VListNBT<VaultEvent<?>, CompoundTag>)NonNullVListNBT.of(VaultEvent::fromNBT);
   protected final VListNBT<VaultPlayer, CompoundTag> players = VListNBT.of(VaultPlayer::fromNBT);
   protected long creationTime = System.currentTimeMillis();
   public static final Supplier<FragmentedVaultGenerator> SINGLE_STAR = VaultGenerator.register(() -> new FragmentedVaultGenerator(VaultMod.id("single_star")));
   public static final Supplier<ArchitectEventGenerator> ARCHITECT_GENERATOR = VaultGenerator.register(
      () -> new ArchitectEventGenerator(VaultMod.id("architect"))
   );
   public static final Supplier<VaultTroveGenerator> TROVE_GENERATOR = VaultGenerator.register(() -> new VaultTroveGenerator(VaultMod.id("vault_trove")));
   public static final Supplier<RaidChallengeGenerator> RAID_CHALLENGE_GENERATOR = VaultGenerator.register(
      () -> new RaidChallengeGenerator(VaultMod.id("raid_challenge"))
   );
   public static final Supplier<FinalLobbyGenerator> FINAL_LOBBY = VaultGenerator.register(() -> new FinalLobbyGenerator(VaultMod.id("final_lobby")));
   public static final VAttribute<ResourceKey<Level>, RegistryKeyAttribute<Level>> DIMENSION = new VAttribute<>(
      VaultMod.id("dimension"), RegistryKeyAttribute::new
   );
   public static final VAttribute<BoundingBox, BoundingBoxAttribute> BOUNDING_BOX = new VAttribute<>(VaultMod.id("bounding_box"), BoundingBoxAttribute::new);
   public static final VAttribute<BlockPos, BlockPosAttribute> START_POS = new VAttribute<>(VaultMod.id("start_pos"), BlockPosAttribute::new);
   public static final VAttribute<Direction, EnumAttribute<Direction>> START_FACING = new VAttribute<>(
      VaultMod.id("start_facing"), () -> new EnumAttribute(Direction.class)
   );
   public static final VAttribute<CrystalData, CompoundAttribute<CrystalData>> CRYSTAL_DATA = new VAttribute<>(VaultMod.id("crystal_data"), () -> null);
   public static final VAttribute<Boolean, BooleanAttribute> IS_RAFFLE = new VAttribute<>(VaultMod.id("is_raffle"), BooleanAttribute::new);
   public static final VAttribute<Boolean, BooleanAttribute> COW_VAULT = new VAttribute<>(VaultMod.id("cow"), BooleanAttribute::new);
   public static final VAttribute<UUID, UUIDAttribute> HOST = new VAttribute<>(VaultMod.id("host"), UUIDAttribute::new);
   public static final VAttribute<UUID, UUIDAttribute> IDENTIFIER = new VAttribute<>(VaultMod.id("identifier"), UUIDAttribute::new);
   public static final VAttribute<UUID, UUIDAttribute> PARENT = new VAttribute<>(VaultMod.id("parent"), UUIDAttribute::new);
   public static final VAttribute<VaultLobby, CompoundAttribute<VaultLobby>> LOBBY = new VAttribute<>(
      VaultMod.id("lobby"), () -> CompoundAttribute.of(VaultLobby::new)
   );
   public static final VAttribute<Boolean, BooleanAttribute> FORCE_ACTIVE = new VAttribute<>(VaultMod.id("force_active"), BooleanAttribute::new);
   public static final VAttribute<String, StringAttribute> PLAYER_BOSS_NAME = new VAttribute<>(VaultMod.id("player_boss_name"), StringAttribute::new);
   @Deprecated
   public static final VAttribute<Boolean, BooleanAttribute> CAN_EXIT = new VAttribute<>(VaultMod.id("can_exit"), BooleanAttribute::new);
   public static final VAttribute<VaultSpawner, CompoundAttribute<VaultSpawner>> SPAWNER = new VAttribute<>(
      VaultMod.id("spawner"), () -> CompoundAttribute.of(VaultSpawner::new)
   );
   public static final VAttribute<VaultChestPity, CompoundAttribute<VaultChestPity>> CHEST_PITY = new VAttribute<>(
      VaultMod.id("chest_pity"), () -> CompoundAttribute.of(VaultChestPity::new)
   );
   public static final VAttribute<VaultSandEvent, CompoundAttribute<VaultSandEvent>> SAND_EVENT = new VAttribute<>(
      VaultMod.id("sand_event"), () -> CompoundAttribute.of(VaultSandEvent::new)
   );
   public static final VAttribute<Boolean, BooleanAttribute> SHOW_TIMER = new VAttribute<>(VaultMod.id("show_timer"), BooleanAttribute::new);
   public static final VAttribute<Boolean, BooleanAttribute> CAN_HEAL = new VAttribute<>(VaultMod.id("can_heal"), BooleanAttribute::new);
   public static final VAttribute<Integer, IntegerAttribute> LEVEL = new VAttribute<>(VaultMod.id("level"), IntegerAttribute::new);
   public static final VaultCondition IS_FINISHED = VaultCondition.register(VaultMod.id("is_finished"), (vault, player, world) -> vault.isFinished());
   public static final VaultCondition IS_RUNNER = VaultCondition.register(VaultMod.id("is_runner"), (vault, player, world) -> player instanceof VaultRunner);
   public static final VaultCondition IS_SPECTATOR = VaultCondition.register(
      VaultMod.id("is_spectator"), (vault, player, world) -> player instanceof VaultSpectator
   );
   public static final VaultCondition IS_OUTSIDE = VaultCondition.register(VaultMod.id("is_outside"), (vault, player, world) -> {
      boolean[] outside = new boolean[1];
      player.runIfPresent(world.getServer(), sPlayer -> outside[0] = !VaultUtils.inVault(vault, sPlayer));
      return outside[0];
   });
   public static final VaultCondition AFTER_GRACE_PERIOD = VaultCondition.register(
      VaultMod.id("after_grace_period"), (vault, player, world) -> vault.getTimer().getRunTime() > 300
   );
   public static final VaultCondition IS_DEAD = VaultCondition.register(VaultMod.id("is_dead"), (vault, player, world) -> {
      MutableBoolean dead = new MutableBoolean(false);
      player.runIfPresent(world.getServer(), playerEntity -> dead.setValue(playerEntity.isDeadOrDying()));
      return dead.booleanValue();
   });
   public static final VaultCondition HAS_EXITED = VaultCondition.register(VaultMod.id("has_exited"), (vault, player, world) -> player.hasExited());
   public static final VaultCondition TIME_LEFT = VaultCondition.register(
      VaultMod.id("time_left"), (vault, player, world) -> player.getTimer().getTimeLeft() > 0
   );
   public static final VaultCondition NO_TIME_LEFT = VaultCondition.register(VaultMod.id("no_time_left"), TIME_LEFT.negate());
   public static final VaultCondition OBJECTIVES_LEFT = VaultCondition.register(
      VaultMod.id("objectives_left"), (vault, player, world) -> player.getObjectives().size() > 0 || vault.getActiveObjectives().size() > 0
   );
   public static final VaultCondition NO_OBJECTIVES_LEFT = VaultCondition.register(VaultMod.id("no_objectives_left"), OBJECTIVES_LEFT.negate());
   public static final VaultCondition OBJECTIVES_LEFT_GLOBALLY = VaultCondition.register(
      VaultMod.id("objectives_left_globally"),
      (vault, player, world) -> vault.players.stream().anyMatch(player1 -> OBJECTIVES_LEFT.test(vault, player1, world))
   );
   public static final VaultCondition NO_OBJECTIVES_LEFT_GLOBALLY = VaultCondition.register(
      VaultMod.id("no_objectives_left_globally"), OBJECTIVES_LEFT_GLOBALLY.negate()
   );
   public static final VaultCondition RUNNERS_LEFT = VaultCondition.register(
      VaultMod.id("runners_left"), (vault, player, world) -> vault.players.stream().anyMatch(player1 -> player1 instanceof VaultRunner)
   );
   public static final VaultCondition NO_RUNNERS_LEFT = VaultCondition.register(VaultMod.id("no_runners_left"), RUNNERS_LEFT.negate());
   public static final VaultCondition ACTIVE_RUNNERS_LEFT = VaultCondition.register(
      VaultMod.id("active_runners_left"),
      (vault, player, world) -> vault.players.stream().anyMatch(player1 -> player1 instanceof VaultRunner && !player1.hasExited())
   );
   public static final VaultCondition NO_ACTIVE_RUNNERS_LEFT = VaultCondition.register(VaultMod.id("no_active_runners_left"), ACTIVE_RUNNERS_LEFT.negate());
   public static final VaultTask CHECK_BAIL = VaultTask.register(
      VaultMod.id("check_bail"),
      (vault, player, world) -> {
         if (vault.getTimer().getRunTime() >= 200) {
            player.runIfPresent(
               world.getServer(),
               sPlayer -> {
                  if (!vault.getGenerator().getPiecesAt(sPlayer.blockPosition(), VaultStart.class).isEmpty()) {
                     AABB box = sPlayer.getBoundingBox();
                     BlockPos min = new BlockPos(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
                     BlockPos max = new BlockPos(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
                     MutableBlockPos pos = new MutableBlockPos();
                     if (world.hasChunksAt(min, max) && !sPlayer.isOnPortalCooldown()) {
                        for (int xx = min.getX(); xx <= max.getX(); xx++) {
                           for (int yy = min.getY(); yy <= max.getY(); yy++) {
                              for (int zz = min.getZ(); zz <= max.getZ(); zz++) {
                                 BlockState state = world.getBlockState(pos.set(xx, yy, zz));
                                 if (state.getBlock() == ModBlocks.VAULT_PORTAL) {
                                    if (sPlayer.isOnPortalCooldown()) {
                                       sPlayer.setPortalCooldown();
                                       return;
                                    }

                                    if (!vault.canExit(player)) {
                                       TextComponent text = new TextComponent("You cannot exit this Vault!");
                                       text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
                                       sPlayer.displayClientMessage(text, true);
                                       return;
                                    }

                                    vault.getAllObjectives().forEach(objective -> objective.notifyBail(vault, player, world));
                                    sPlayer.setPortalCooldown();
                                    VaultRaid.REMOVE_SCAVENGER_ITEMS
                                       .then(VaultRaid.REMOVE_INVENTORY_RESTORE_SNAPSHOTS)
                                       .then(VaultRaid.EXIT_SAFELY)
                                       .execute(vault, player, world);
                                    MutableComponent playerName = sPlayer.getDisplayName().copy();
                                    playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
                                    TextComponent suffix = new TextComponent(" survived.");
                                    world.getServer()
                                       .getPlayerList()
                                       .broadcastMessage(new TextComponent("").append(playerName).append(suffix), ChatType.CHAT, player.getPlayerId());
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            );
         }
      }
   );
   public static final VaultTask CHECK_BAIL_COOP = VaultTask.register(
      VaultMod.id("check_bail_coop"),
      (vault, player, world) -> {
         if (vault.getTimer().getRunTime() >= 200) {
            player.runIfPresent(
               world.getServer(),
               sPlayer -> {
                  if (!vault.getGenerator().getPiecesAt(sPlayer.blockPosition(), VaultStart.class).isEmpty()) {
                     AABB box = sPlayer.getBoundingBox();
                     BlockPos min = new BlockPos(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
                     BlockPos max = new BlockPos(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
                     MutableBlockPos pos = new MutableBlockPos();
                     if (world.hasChunksAt(min, max)) {
                        for (int i = min.getX(); i <= max.getX(); i++) {
                           for (int j = min.getY(); j <= max.getY(); j++) {
                              for (int k = min.getZ(); k <= max.getZ(); k++) {
                                 BlockState state = world.getBlockState(pos.set(i, j, k));
                                 if (state.getBlock() == ModBlocks.VAULT_PORTAL) {
                                    if (sPlayer.isOnPortalCooldown()) {
                                       sPlayer.setPortalCooldown();
                                       return;
                                    }

                                    if (!vault.canExit(player)) {
                                       TextComponent text = new TextComponent("You cannot exit this Vault!");
                                       text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
                                       sPlayer.displayClientMessage(text, true);
                                       return;
                                    }

                                    vault.getAllObjectives().forEach(objective -> objective.notifyBail(vault, player, world));
                                    sPlayer.setPortalCooldown();
                                    VaultRaid.RUNNER_TO_SPECTATOR.execute(vault, player, world);
                                    VaultRaid.HIDE_OVERLAY.execute(vault, player, world);
                                    MutableComponent playerName = sPlayer.getDisplayName().copy();
                                    playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
                                    TextComponent suffix = new TextComponent(" survived.");
                                    world.getServer()
                                       .getPlayerList()
                                       .broadcastMessage(new TextComponent("").append(playerName).append(suffix), ChatType.CHAT, player.getPlayerId());
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            );
         }
      }
   );
   public static final VaultTask CHECK_BAIL_FINAL = VaultTask.register(
      VaultMod.id("check_bail_final"),
      (vault, player, world) -> player.runIfPresent(
         world.getServer(),
         sPlayer -> {
            if (!vault.getGenerator().getPiecesAt(sPlayer.blockPosition(), VaultStart.class).isEmpty()) {
               AABB box = sPlayer.getBoundingBox();
               BlockPos min = new BlockPos(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
               BlockPos max = new BlockPos(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
               MutableBlockPos pos = new MutableBlockPos();
               if (world.hasChunksAt(min, max)) {
                  for (int i = min.getX(); i <= max.getX(); i++) {
                     for (int j = min.getY(); j <= max.getY(); j++) {
                        for (int k = min.getZ(); k <= max.getZ(); k++) {
                           BlockState state = world.getBlockState(pos.set(i, j, k));
                           if (state.getBlock() == ModBlocks.VAULT_PORTAL) {
                              if (sPlayer.isOnPortalCooldown()) {
                                 sPlayer.setPortalCooldown();
                                 return;
                              }

                              if (!vault.canExit(player)) {
                                 TextComponent text = new TextComponent("You cannot exit this Vault!");
                                 text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
                                 sPlayer.displayClientMessage(text, true);
                                 return;
                              }

                              vault.getAllObjectives().forEach(objective -> objective.notifyBail(vault, player, world));
                              sPlayer.setPortalCooldown();
                              new ArrayList<>(vault.getPlayers()).forEach(vaultPlayer -> {
                                 if (vaultPlayer instanceof VaultRunner) {
                                    VaultRaid.RUNNER_TO_SPECTATOR.execute(vault, vaultPlayer, world);
                                 }
                              });
                              vault.getProperties().create(FORCE_ACTIVE, false);
                              VaultRaid.HIDE_OVERLAY.execute(vault, player, world);
                              MutableComponent playerName = sPlayer.getDisplayName().copy();
                              playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
                              TextComponent suffix = new TextComponent(" survived.");
                              world.getServer()
                                 .getPlayerList()
                                 .broadcastMessage(new TextComponent("").append(playerName).append(suffix), ChatType.CHAT, player.getPlayerId());
                           }
                        }
                     }
                  }
               }
            }
         }
      )
   );
   public static final VaultTask TICK_SAND_EVENT = VaultTask.register(
      VaultMod.id("tick_sand_event"),
      (vault, player, world) -> vault.getProperties().getBase(SAND_EVENT).ifPresent(event -> event.execute(vault, player, world))
   );
   public static final VaultTask TICK_CHEST_PITY = VaultTask.register(
      VaultMod.id("tick_chest_pity"),
      (vault, player, world) -> player.getProperties().getBase(CHEST_PITY).ifPresent(event -> event.execute(vault, player, world))
   );
   public static final VaultTask TICK_SPAWNER = VaultTask.register(
      VaultMod.id("tick_spawner"),
      (vault, player, world) -> {
         if (!vault.getActiveObjectives().isEmpty()) {
            player.getProperties()
               .get(SPAWNER)
               .ifPresent(
                  attribute -> {
                     VaultSpawner spawner = (VaultSpawner)attribute.getBaseValue();
                     if (player.getTimer().getRunTime() >= 300) {
                        int level = player.getProperties().getValue(LEVEL);
                        VaultSpawner.Config c = null;
                        spawner.configure(
                           config -> config.withStartMaxMobs(c.getStartMaxMobs())
                              .withMinDistance(c.getMinDistance())
                              .withMaxDistance(c.getMaxDistance())
                              .withDespawnDistance(c.getDespawnDistance())
                        );
                     }

                     spawner.execute(vault, player, world);
                     attribute.updateNBT();
                  }
               );
         }
      }
   );
   public static final VaultTask TICK_LOBBY = VaultTask.register(
      VaultMod.id("tick_lobby"), (vault, player, world) -> vault.getProperties().get(LOBBY).ifPresent(attribute -> {
         VaultLobby lobby = (VaultLobby)attribute.getBaseValue();
         lobby.execute(vault, player, world);
         attribute.updateNBT();
      })
   );
   public static final VaultTask TICK_INFLUENCES = VaultTask.register(VaultMod.id("tick_influences"), (vault, player, world) -> {
      if (!vault.getInfluences().isInitialized()) {
         VaultInfluenceHandler.initializeInfluences(vault, world);
         vault.getInfluences().setInitialized();
      }

      vault.getInfluences().tick(vault, player, world);
   });
   public static final VaultTask TP_TO_START = VaultTask.register(
      VaultMod.id("tp_to_start"),
      (vault, player, world) -> player.runIfPresent(
         world.getServer(),
         playerEntity -> {
            BlockPos start = vault.getProperties().getBaseOrDefault(START_POS, (BlockPos)null);
            Direction facing = vault.getProperties().getBaseOrDefault(START_FACING, (Direction)null);
            BoundingBox box = vault.getProperties().getValue(BOUNDING_BOX);
            if (start == null) {
               VaultMod.LOGGER.warn("No vault start was found.");
               playerEntity.teleportTo(
                  world, box.minX() + box.getXSpan() / 2.0F, 256.0, box.minZ() + box.getZSpan() / 2.0F, playerEntity.getYRot(), playerEntity.getXRot()
               );
            } else {
               playerEntity.teleportTo(
                  world,
                  start.getX() + 0.5,
                  start.getY() + 0.2,
                  start.getZ() + 0.5,
                  facing == null ? world.getRandom().nextFloat() * 360.0F : facing.getClockWise().toYRot(),
                  0.0F
               );
            }

            playerEntity.setPortalCooldown();
            playerEntity.setOnGround(true);
         }
      )
   );
   public static final VaultTask INIT_LEVEL = VaultTask.register(VaultMod.id("init_level"), (vault, player, world) -> {
      int currentLevel = vault.getProperties().getBaseOrDefault(LEVEL, 0);
      int playerLevel = PlayerVaultStatsData.get(world).getVaultStats(player.getPlayerId()).getVaultLevel();
      vault.getProperties().create(LEVEL, Math.max(currentLevel, playerLevel));
      player.getProperties().create(LEVEL, playerLevel);
   });
   public static final VaultTask INIT_LEVEL_COOP = VaultTask.register(
      VaultMod.id("init_level_coop"), (vault, player, world) -> vault.getProperties().getBase(HOST).ifPresent(hostId -> {
         int vaultLevel = PlayerVaultStatsData.get(world).getVaultStats(hostId).getVaultLevel();
         vaultLevel += Math.max(vault.getPlayers().size() - 1, 0) * 2;
         vault.getProperties().create(LEVEL, vaultLevel);
         player.getProperties().create(LEVEL, vaultLevel);
      })
   );
   public static final VaultTask INIT_LEVEL_FINAL = VaultTask.register(VaultMod.id("init_level_final"), (vault, player, world) -> {
      vault.getProperties().create(LEVEL, 1000);
      player.getProperties().create(LEVEL, 1000);
   });
   public static final VaultTask INIT_RELIC_TIME = VaultTask.register(
      VaultMod.id("init_relic_extension"),
      (vault, player, world) -> {
         Set<String> sets = new HashSet<>();

         for (VaultPlayer player2 : vault.getPlayers()) {
            Set<String> newSets = DiscoveredRelicsData.get(world)
               .getDiscoveredRelics(player2.getPlayerId())
               .stream()
               .<String>map(ResourceLocation::toString)
               .collect(Collectors.toSet());
            if (newSets.size() > sets.size()) {
               sets = newSets;
            }
         }

         sets.stream().<ResourceLocation>map(ResourceLocation::new).forEach(set -> {});
      }
   );
   @Deprecated
   public static final VaultTask INIT_FAVOUR_TIME = VaultTask.register(VaultMod.id("init_favour_extension"), (vault, player, world) -> {});
   public static final VaultTask INIT_SANDS_EVENT = VaultTask.register(VaultMod.id("init_sand_event"), (vault, player, world) -> {
      if (ModConfigs.SAND_EVENT.isEnabled()) {
         vault.getProperties().create(SAND_EVENT, new VaultSandEvent());
         player.getBehaviours().add(new VaultBehaviour(IS_FINISHED.negate(), TICK_SAND_EVENT));
      }
   });
   public static final VaultTask INIT_COW_VAULT = VaultTask.register(
      VaultMod.id("init_cow_vault"), (vault, player, world) -> VaultCowOverrides.forceSpecialVault = false
   );
   public static final VaultTask INIT_GLOBAL_MODIFIERS = VaultTask.register(VaultMod.id("init_global_modifiers"), (vault, player, world) -> {
      Random rand = world.getRandom();
      if (!vault.getModifiers().isInitialized()) {
      }

      vault.getModifiers().apply(vault, player, world, rand);
      if (!player.getModifiers().isInitialized()) {
         player.getModifiers().setInitialized();
      }

      player.getModifiers().apply(vault, player, world, rand);
   });
   public static final VaultTask RUNNER_TO_SPECTATOR = VaultTask.register(VaultMod.id("runner_to_spectator"), (vault, player, world) -> {
      vault.players.remove(player);
      vault.players.add(new VaultSpectator((VaultRunner)player));
   });
   public static final VaultTask HIDE_OVERLAY = VaultTask.register(
      VaultMod.id("hide_overlay"), (vault, player, world) -> player.sendIfPresent(world.getServer(), VaultOverlayMessage.hide())
   );
   public static final VaultTask PAUSE_IN_ARENA = VaultTask.register(
      VaultMod.id("pause_in_arena"), (vault, player, world) -> player.runIfPresent(world.getServer(), playerEntity -> {
         if (playerEntity.getLevel().dimension() == VaultMod.ARENA_KEY) {
            if (player instanceof VaultRunner) {
               player.getTimer().runTime--;
            }
         }
      })
   );
   public static final VaultTask LEVEL_UP_GEAR = VaultTask.register(VaultMod.id("level_up_gear"), (vault, player, world) -> {
      if (player instanceof VaultRunner) {
         player.runIfPresent(world.getServer(), playerEntity -> {});
      }
   });
   public static final VaultTask REMOVE_SCAVENGER_ITEMS = VaultTask.register(VaultMod.id("remove_scavenger_items"), (vault, player, world) -> {});
   public static final VaultTask SAVE_SOULBOUND_GEAR = VaultTask.register(
      VaultMod.id("save_soulbound_gear"), (vault, player, world) -> player.runIfPresent(world.getServer(), sPlayer -> {
         SoulboundSnapshotData data = SoulboundSnapshotData.get(world);
         if (!data.hasSnapshot(sPlayer)) {
            data.createSnapshot(sPlayer);
         }
      })
   );
   public static final VaultTask REMOVE_INVENTORY_RESTORE_SNAPSHOTS = VaultTask.register(VaultMod.id("remove_inventory_snapshots"), (vault, player, world) -> {
      PhoenixModifierSnapshotData modifierData = PhoenixModifierSnapshotData.get(world);
      if (modifierData.hasSnapshot(player.getPlayerId())) {
         modifierData.removeSnapshot(player.getPlayerId());
      }

      PhoenixSetSnapshotData setSnapshotData = PhoenixSetSnapshotData.get(world);
      if (setSnapshotData.hasSnapshot(player.getPlayerId())) {
         setSnapshotData.removeSnapshot(player.getPlayerId());
      }
   });
   public static final VaultTask FINAL_VICTORY_SCENE = VaultTask.register(
      VaultMod.id("final_victory_scene"),
      (vault, player, world) -> {
         if (player instanceof VaultRunner) {
            player.getTimer().addTime(new WinExtension(player.getTimer(), 400), 0);
            player.runIfPresent(
               world.getServer(),
               playerEntity -> {
                  FireworkRocketEntity fireworks = new FireworkRocketEntity(
                     world, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), new ItemStack(Items.FIREWORK_ROCKET)
                  );
                  world.addFreshEntity(fireworks);
                  world.playSound(
                     null,
                     playerEntity.getX(),
                     playerEntity.getY(),
                     playerEntity.getZ(),
                     SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                     SoundSource.MASTER,
                     1.0F,
                     1.0F
                  );
                  TextComponent title = new TextComponent("Branch Cleared!");
                  title.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(14536734)));
                  TextComponent subtitle = new TextComponent("Place your keystone in the frame.");
                  subtitle.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(14536734)));
                  ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
                  ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
                  playerEntity.connection.send(titlePacket);
                  playerEntity.connection.send(subtitlePacket);
               }
            );
         }
      }
   );
   public static final VaultTask EXIT_SAFELY = VaultTask.register(
      VaultMod.id("exit_safely"), (vault, player, world) -> player.runIfPresent(world.getServer(), playerEntity -> {
         if (player instanceof VaultSpectator) {
            playerEntity.gameMode.changeGameModeForPlayer(((VaultSpectator)player).oldGameType);
         }

         world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSounds.VAULT_PORTAL_LEAVE, SoundSource.PLAYERS, 1.0F, 1.0F);
         world.playSound(null, playerEntity, ModSounds.VAULT_PORTAL_LEAVE, SoundSource.PLAYERS, 1.0F, 1.0F);
         player.exit();
         HIDE_OVERLAY.execute(vault, player, world);
         UUID parent = vault.getProperties().getBase(PARENT).orElse(null);
         VaultRaid parentVault = parent == null ? null : VaultRaidData.get(world).get(parent);
         if (parentVault != null) {
            parentVault.getProperties().getBase(LOBBY).ifPresent(lobby -> {
               VaultMember member = new VaultMember(player.getPlayerId());
               member.getProperties().create(CAN_HEAL, true);
               member.getBehaviours().add(new VaultBehaviour(IS_OUTSIDE, TP_TO_START));
               member.getBehaviours().add(new VaultBehaviour(IS_DEAD.negate(), TICK_LOBBY));
               parentVault.getPlayers().add(member);
               TP_TO_START.execute(parentVault, member, world.getServer().getLevel(parentVault.getProperties().getValue(DIMENSION)));
               if (vault.getActiveObjectives().stream().allMatch(VaultObjective::isCompleted)) {
                  FINAL_VICTORY_SCENE.execute(vault, player, world);
               } else {
                  player.runIfPresent(world.getServer(), sPlayer -> {
                     lobby.snapshots.restoreSnapshot(sPlayer);
                     lobby.snapshots.removeSnapshot(sPlayer);
                  });
               }

               vault.getPlayers().remove(player);
            });
            vault.getProperties().create(FORCE_ACTIVE, false);
         } else {
            VaultUtils.exitSafely(world.getServer().getLevel(Level.OVERWORLD), playerEntity);
         }
      })
   );
   public static final VaultTask EXIT_DEATH = VaultTask.register(
      VaultMod.id("exit_death"), (vault, player, world) -> player.runIfPresent(world.getServer(), playerEntity -> {
         if (player instanceof VaultSpectator) {
            playerEntity.gameMode.changeGameModeForPlayer(((VaultSpectator)player).oldGameType);
         }

         world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSounds.TIMER_KILL_SFX, SoundSource.PLAYERS, 1.0F, 1.0F);
         world.playSound(null, playerEntity, ModSounds.TIMER_KILL_SFX, SoundSource.PLAYERS, 1.0F, 1.0F);
         playerEntity.getInventory().clearOrCountMatchingItems(stack -> true, -1, playerEntity.inventoryMenu.getCraftSlots());
         playerEntity.containerMenu.broadcastChanges();
         playerEntity.inventoryMenu.slotsChanged(playerEntity.getInventory());
         playerEntity.hurt(new DamageSource("vaultFailed").bypassArmor().bypassInvul(), 1.0E8F);
         player.exit();
         HIDE_OVERLAY.execute(vault, player, world);
         UUID parent = vault.getProperties().getBase(PARENT).orElse(null);
         VaultRaid parentVault = parent == null ? null : VaultRaidData.get(world).get(parent);
         if (parentVault != null) {
            parentVault.getProperties().getBase(LOBBY).ifPresent(lobby -> {
               VaultMember member = new VaultMember(player.getPlayerId());
               member.getProperties().create(CAN_HEAL, true);
               member.getBehaviours().add(new VaultBehaviour(IS_OUTSIDE, TP_TO_START));
               member.getBehaviours().add(new VaultBehaviour(IS_DEAD.negate(), TICK_LOBBY));
               parentVault.getPlayers().add(member);
               player.runIfPresent(world.getServer(), sPlayer -> lobby.snapshots.restoreSnapshot(sPlayer));
               vault.getPlayers().remove(player);
            });
            vault.getProperties().create(FORCE_ACTIVE, false);
         }
      })
   );
   public static final VaultTask EXIT_DEATH_ALL = VaultTask.register(
      VaultMod.id("exit_death_all"),
      (vault, player, world) -> new ArrayList<>(vault.players)
         .forEach(vPlayer -> REMOVE_SCAVENGER_ITEMS.then(SAVE_SOULBOUND_GEAR.then(EXIT_DEATH)).execute(vault, vPlayer, world))
   );
   public static final VaultTask EXIT_DEATH_ALL_NO_SAVE = VaultTask.register(
      VaultMod.id("exit_death_all_no_save"),
      (vault, player, world) -> new ArrayList<>(vault.players).forEach(vPlayer -> REMOVE_SCAVENGER_ITEMS.then(EXIT_DEATH).execute(vault, vPlayer, world))
   );
   public static final VaultTask VICTORY_SCENE = VaultTask.register(
      VaultMod.id("victory_scene"),
      (vault, player, world) -> {
         if (player instanceof VaultRunner) {
            player.getTimer().addTime(new WinExtension(player.getTimer(), 400), 0);
            player.runIfPresent(
               world.getServer(),
               playerEntity -> {
                  FireworkRocketEntity fireworks = new FireworkRocketEntity(
                     world, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), new ItemStack(Items.FIREWORK_ROCKET)
                  );
                  world.addFreshEntity(fireworks);
                  world.playSound(
                     null,
                     playerEntity.getX(),
                     playerEntity.getY(),
                     playerEntity.getZ(),
                     SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                     SoundSource.MASTER,
                     1.0F,
                     1.0F
                  );
                  TextComponent title = new TextComponent("Vault Cleared!");
                  title.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(14536734)));
                  TextComponent subtitle = new TextComponent("You'll be teleported back soon...");
                  subtitle.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(14536734)));
                  ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
                  ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
                  playerEntity.connection.send(titlePacket);
                  playerEntity.connection.send(subtitlePacket);
               }
            );
         }
      }
   );
   public static final VaultTask ENTER_DISPLAY = VaultTask.register(
      VaultMod.id("enter_display"),
      (vault, player, world) -> player.runIfPresent(
         world.getServer(),
         playerEntity -> {
            TextComponent title = new TextComponent("The Vault");
            title.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(14536734)));
            MutableComponent subtitle = vault.canExit(player)
               ? new TextComponent("Good luck, ").append(playerEntity.getName()).append(new TextComponent("!"))
               : new TextComponent("No exit this time, ").append(playerEntity.getName()).append(new TextComponent("!"));
            subtitle.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(14536734)));
            ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
            ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
            playerEntity.connection.send(titlePacket);
            playerEntity.connection.send(subtitlePacket);
            TextComponent text = new TextComponent("");
            AtomicBoolean startsWithVowel = new AtomicBoolean(false);
            vault.getModifiers().forEach((i, modifierStack) -> {
               text.append(modifierStack.getSize() + "x ");
               text.append(modifierStack.getModifier().getNameComponentFormatted(modifierStack.getSize()));
               if (i == 0) {
                  char c = modifierStack.getModifier().getDisplayName().toLowerCase().charAt(0);
                  startsWithVowel.set(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
               }

               if (i != vault.getModifiers().size() - 1) {
                  text.append(new TextComponent(", "));
               }
            });
            Component vaultName = vault.getActiveObjectives().stream().findFirst().map(VaultObjective::getVaultName).orElse(new TextComponent("Vault"));
            if (vault.getModifiers().isEmpty()) {
               char c = vaultName.getString().toLowerCase().charAt(0);
               startsWithVowel.set(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
            }

            TextComponent prefix = new TextComponent(startsWithVowel.get() ? " entered an " : " entered a ");
            if (!vault.getModifiers().isEmpty()) {
               text.append(new TextComponent(" "));
            }

            if (vault.getProperties().getBaseOrDefault(COW_VAULT, false)) {
               MutableComponent txt = new TextComponent("Vault that doesn't exist!");
               Component hoverText = new TextComponent(
                  "A vault that doesn't exist.\nThe Vault gods are not responsible for events that transpire here.\n\nThis realm may also harbor additional riches."
               );
               txt.setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, hoverText)).withColor(TextColor.fromRgb(9974168)));
               text.append(txt);
            } else {
               text.append(vaultName).append("!");
            }

            prefix.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)));
            text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)));
            MutableComponent playerName = playerEntity.getDisplayName().copy();
            playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
            world.getServer().getPlayerList().broadcastMessage(playerName.append(prefix).append(text), ChatType.CHAT, player.getPlayerId());
         }
      )
   );
   public static final Supplier<SummonAndKillBossObjective> SUMMON_AND_KILL_BOSS = VaultObjective.register(
      () -> new SummonAndKillBossObjective(VaultMod.id("summon_and_kill_boss"))
   );
   public static final Supplier<LegacyScavengerHuntObjective> SCAVENGER_HUNT = VaultObjective.register(
      () -> new LegacyScavengerHuntObjective(VaultMod.id("scavenger_hunt"))
   );
   public static final Supplier<ArchitectObjective> ARCHITECT_EVENT = VaultObjective.register(() -> new ArchitectObjective(VaultMod.id("architect")));
   public static final Supplier<TroveObjective> VAULT_TROVE = VaultObjective.register(() -> new TroveObjective(VaultMod.id("trove")));
   public static final Supplier<AncientObjective> ANCIENTS = VaultObjective.register(() -> new AncientObjective(VaultMod.id("ancients")));
   public static final Supplier<RaidChallengeObjective> RAID_CHALLENGE = VaultObjective.register(
      () -> new RaidChallengeObjective(VaultMod.id("raid_challenge"))
   );
   public static final Supplier<CakeHuntObjective> CAKE_HUNT = VaultObjective.register(() -> new CakeHuntObjective(VaultMod.id("cake_hunt")));
   public static final Supplier<SummonAndKillAllBossesObjective> SUMMON_AND_KILL_ALL_BOSSES = VaultObjective.register(
      () -> new SummonAndKillAllBossesObjective(VaultMod.id("summon_and_kill_all_bosses"))
   );
   public static final Supplier<TreasureHuntObjective> TREASURE_HUNT = VaultObjective.register(() -> new TreasureHuntObjective(VaultMod.id("treasure_hunt")));
   @Deprecated
   public static final VaultEvent<Event> TRIGGER_BOSS_SUMMON = VaultEvent.register(VaultMod.id("trigger_boss_summon"), Event.class, (vault, event) -> {});
   public static final VaultEvent<LivingUpdateEvent> SCALE_MOB = VaultEvent.register(
      VaultMod.id("scale_mob"), LivingUpdateEvent.class, LegacyEntityScaler::scaleVaultEntity
   );
   public static final VaultEvent<EntityJoinWorldEvent> SCALE_MOB_JOIN = VaultEvent.register(
      VaultMod.id("scale_mob_join"), EntityJoinWorldEvent.class, LegacyEntityScaler::scaleVaultEntity
   );
   public static final VaultEvent<CheckSpawn> BLOCK_NATURAL_SPAWNING = VaultEvent.register(
      VaultMod.id("block_natural_spawning"), CheckSpawn.class, (vault, event) -> {
         if (VaultUtils.inVault(vault, event.getEntity())) {
            event.setResult(Result.DENY);
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> PREVENT_ITEM_PICKUP = VaultEvent.register(
      VaultMod.id("prevent_item_pickup"), EntityJoinWorldEvent.class, (vault, event) -> {
         if (event.getEntity() instanceof Mob) {
            Mob me = (Mob)event.getEntity();
            if (VaultUtils.inVault(vault, event.getEntity())) {
               me.setCanPickUpLoot(false);
            }
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> REPLACE_WITH_COW = VaultEvent.register(
      VaultMod.id("replace_with_cow"), EntityJoinWorldEvent.class, (vault, event) -> {
         if (event.getWorld() instanceof ServerLevel) {
            Entity entity = event.getEntity();
            if (!entity.getTags().contains("replaced_entity")) {
               if (VaultUtils.inVault(vault, event.getEntity())) {
                  if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                     LivingEntity replaced = VaultCowOverrides.replaceVaultEntity(vault, (LivingEntity)entity, (ServerLevel)event.getWorld());
                     if (replaced != null) {
                        Vec3 pos = entity.position();
                        replaced.absMoveTo(pos.x, pos.y, pos.z, entity.getYRot(), entity.getXRot());
                        ServerScheduler.INSTANCE.schedule(1, () -> event.getWorld().addFreshEntity(replaced));
                        event.setCanceled(true);
                     }
                  }
               }
            }
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> APPLY_SCALE_MODIFIER = VaultEvent.register(
      VaultMod.id("apply_scale_modifier"),
      EntityJoinWorldEvent.class,
      (vault, event) -> {
         if (event.getWorld() instanceof ServerLevel) {
            if (VaultUtils.inVault(vault, event.getEntity())) {
               if (event.getEntity() instanceof LivingEntity) {
                  if (!(event.getEntity() instanceof Player)) {
                     if (!(event.getEntity() instanceof EternalEntity)) {
                        LivingEntity entity = (LivingEntity)event.getEntity();
                        vault.withActiveModifiersFor(
                           PlayerFilter.any(),
                           ScaleModifier.class,
                           (scaleModifier, stackSize) -> entity.getAttribute(ModAttributes.SIZE_SCALE).setBaseValue(scaleModifier.properties().getScale())
                        );
                     }
                  }
               }
            }
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> APPLY_FRENZY_MODIFIERS = VaultEvent.register(
      VaultMod.id("frenzy_modifiers"), EntityJoinWorldEvent.class, (vault, event) -> {
         if (event.getWorld() instanceof ServerLevel) {
            if (VaultUtils.inVault(vault, event.getEntity())) {
               if (event.getEntity() instanceof LivingEntity) {
                  if (!(event.getEntity() instanceof Player)) {
                     if (!(event.getEntity() instanceof EternalEntity)) {
                        if (!event.getEntity().getTags().contains("vault_boss")) {
                           LivingEntity entity = (LivingEntity)event.getEntity();
                           if (!entity.getTags().contains("frenzy_scaled")) {
                              vault.withActiveModifiersFor(PlayerFilter.any(), MobFrenzyModifier.class, (mobFrenzyModifier, stackSize) -> {});
                              entity.getTags().add("frenzy_scaled");
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> APPLY_MOB_ATTRIBUTE_MODIFIERS = VaultEvent.register(
      VaultMod.id("mob_attribute_modifiers"), EntityJoinWorldEvent.class, (vault, event) -> {
         if (event.getWorld() instanceof ServerLevel) {
            if (VaultUtils.inVault(vault, event.getEntity())) {
               if (event.getEntity() instanceof LivingEntity livingEntity) {
                  if (!(event.getEntity() instanceof Player)) {
                     if (!(event.getEntity() instanceof EternalEntity)) {
                        vault.withActiveModifiersFor(PlayerFilter.any(), MobAttributeModifier.class, (mobAttributeModifier, stackSize) -> {});
                     }
                  }
               }
            }
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> APPLY_INFLUENCE_MODIFIERS = VaultEvent.register(
      VaultMod.id("influence_modifiers"), EntityJoinWorldEvent.class, (vault, event) -> {
         if (event.getWorld() instanceof ServerLevel) {
            if (VaultUtils.inVault(vault, event.getEntity())) {
               if (event.getEntity() instanceof LivingEntity) {
                  if (!(event.getEntity() instanceof Player)) {
                     if (!(event.getEntity() instanceof EternalEntity)) {
                        LivingEntity entity = (LivingEntity)event.getEntity();
                        if (!entity.getTags().contains("influenced")) {
                           vault.getInfluences().getInfluences(MobAttributeInfluence.class).forEach(influence -> influence.applyTo(entity));
                           entity.getTags().add("influenced");
                        }
                     }
                  }
               }
            }
         }
      }
   );

   public VaultRaid() {
      VaultListener.listen(this);
   }

   public VaultRaid(VaultGenerator generator, VaultTask initializer, RaidProperties properties, List<VaultEvent<?>> events, Iterable<VaultPlayer> players) {
      this.generator = generator;
      this.initializer = initializer;
      this.properties = properties;
      events.forEach(this.events::add);
      players.forEach(this.players::add);
      VaultListener.listen(this);
   }

   public VaultTimer getTimer() {
      return this.timer;
   }

   public VaultGenerator getGenerator() {
      return this.generator;
   }

   public VaultTask getInitializer() {
      return this.initializer;
   }

   public VaultInfluences getInfluences() {
      return this.influence;
   }

   public VaultModifiers getModifiers() {
      return this.modifiers;
   }

   public <T extends VaultModifier<?>> List<VaultModifiers.ActiveModifierStack<T>> getActiveModifiersFor(PlayerFilter filter, Class<T> modifierClass) {
      List<VaultModifiers.ActiveModifierStack<T>> modifierStacks = this.getModifiers().stream(modifierClass).toList();

      for (VaultPlayer player : this.getPlayers()) {
         if (!(player instanceof VaultRunner) && filter.test(player.getPlayerId())) {
            player.getModifiers().stream(modifierClass).forEach(modifierStacks::add);
         }
      }

      return modifierStacks;
   }

   public <T extends VaultModifier<?>> boolean hasActiveModifierFor(PlayerFilter filter, Class<T> modifierClass) {
      return !this.getActiveModifiersFor(filter, modifierClass).isEmpty();
   }

   public <T extends VaultModifier<?>> boolean hasActiveModifierFor(PlayerFilter playerFilter, Class<T> modifierClass, Predicate<T> modifierFilter) {
      return this.getActiveModifiersFor(playerFilter, modifierClass)
         .stream()
         .anyMatch(vaultModifierStack -> modifierFilter.test(vaultModifierStack.getModifier()));
   }

   public <T extends VaultModifier<?>> void withActiveModifiersFor(PlayerFilter filter, Class<T> modifierClass, BiConsumer<T, Integer> consumer) {
      for (IVaultModifierStack modifierStack : this.getActiveModifiersFor(filter, modifierClass)) {
         consumer.accept((T)modifierStack.getModifier(), modifierStack.getSize());
      }
   }

   public boolean canExit(VaultPlayer player) {
      return !this.hasActiveModifierFor(PlayerFilter.of(player), PlayerNoExitModifier.class);
   }

   public boolean triggerRaid(ServerLevel world, BlockPos controller) {
      if (this.activeRaid != null) {
         return false;
      } else {
         this.activeRaid = ActiveRaid.create(this, world, controller);
         return true;
      }
   }

   @Nullable
   public ActiveRaid getActiveRaid() {
      return this.activeRaid;
   }

   public RaidProperties getProperties() {
      return this.properties;
   }

   public List<VaultObjective> getActiveObjectives() {
      return this.getAllObjectives().stream().filter(objective -> !objective.isCompleted()).collect(Collectors.toList());
   }

   public List<VaultObjective> getAllObjectives() {
      return this.objectives;
   }

   public <T extends VaultObjective> Optional<T> getActiveObjective(Class<T> objectiveClass) {
      return this.getAllObjectives()
         .stream()
         .filter(objective -> !objective.isCompleted())
         .filter(objective -> objectiveClass.isAssignableFrom(objective.getClass()))
         .findFirst()
         .map(vaultObjective -> (T)vaultObjective);
   }

   public boolean hasActiveObjective(VaultPlayer player, Class<? extends VaultObjective> objectiveClass) {
      return this.getActiveObjective(objectiveClass).isPresent() || player.getActiveObjective(objectiveClass).isPresent();
   }

   public List<VaultEvent<?>> getEvents() {
      return this.events;
   }

   public List<VaultPlayer> getPlayers() {
      return this.players;
   }

   public long getCreationTime() {
      return this.creationTime;
   }

   public Optional<VaultPlayer> getPlayer(Player player) {
      return this.getPlayer(player.getUUID());
   }

   public Optional<VaultPlayer> getPlayer(UUID playerId) {
      return this.players.stream().filter(player -> player.getPlayerId().equals(playerId)).findFirst();
   }

   public void tick(ServerLevel world) {
      this.getGenerator().tick(world, this);
      if (!this.isFinished()) {
         MinecraftServer srv = world.getServer();
         if (this.getActiveObjectives().stream().noneMatch(objective -> objective.shouldPauseTimer(srv, this))) {
            this.getTimer().tick();
         }

         this.getModifiers().tick(this, world, PlayerFilter.any());
         new ArrayList<>(this.players).forEach(player -> {
            player.tick(this, world);
            player.sendIfPresent(world.getServer(), new VaultModifierMessage(this, player));
         });
         this.getAllObjectives()
            .stream()
            .filter(objective -> objective.isCompleted() && objective.getCompletionTime() < 0)
            .peek(objective -> objective.setCompletionTime(this.getTimer().getRunTime()))
            .forEach(objective -> objective.complete(this, world));
         this.getActiveObjectives().forEach(objective -> objective.tick(this, PlayerFilter.any(), world));
         if (this.activeRaid != null) {
            this.activeRaid.tick(this, world);
            if (this.activeRaid.isFinished()) {
               this.activeRaid.finish(this, world);
               this.activeRaid = null;
            }
         }
      }
   }

   public boolean isFinished() {
      return !this.getProperties().getBaseOrDefault(FORCE_ACTIVE, false) && (this.players.isEmpty() || this.players.stream().allMatch(VaultPlayer::hasExited));
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.put("Timer", this.timer.serializeNBT());
      nbt.put("Generator", this.generator.serializeNBT());
      nbt.put("Modifiers", this.modifiers.serializeNBT());
      nbt.put("influence", this.influence.serializeNBT());
      nbt.put("Properties", this.properties.serializeNBT());
      nbt.put("Objectives", this.objectives.serializeNBT());
      nbt.put("Events", this.events.serializeNBT());
      nbt.put("Players", this.players.serializeNBT());
      nbt.putLong("CreationTime", this.getCreationTime());
      NBTHelper.writeOptional(nbt, "activeRaid", this.activeRaid, (tag, raid) -> raid.serialize(tag));
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.timer.deserializeNBT(nbt.getCompound("Timer"));
      this.generator = VaultGenerator.fromNBT(nbt.getCompound("Generator"));
      this.modifiers.deserializeNBT(nbt.getCompound("Modifiers"));
      this.influence.deserializeNBT(nbt.getCompound("influence"));
      this.properties.deserializeNBT(nbt.getCompound("Properties"));
      this.objectives.deserializeNBT(nbt.getList("Objectives", 10));
      this.events.deserializeNBT(nbt.getList("Events", 10));
      this.players.deserializeNBT(nbt.getList("Players", 10));
      this.creationTime = nbt.getLong("CreationTime");
      this.activeRaid = NBTHelper.readOptional(nbt, "activeRaid", ActiveRaid::deserializeNBT);
   }

   public static VaultRaid classic(
      VaultGenerator generator,
      VaultTask initializer,
      RaidProperties properties,
      VaultObjective objective,
      List<VaultEvent<?>> events,
      Map<VaultPlayerType, Set<ServerPlayer>> playersMap
   ) {
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      VaultRaid vault = new VaultRaid(
         generator,
         initializer,
         properties,
         events,
         playersMap.entrySet()
            .stream()
            .flatMap(
               entry -> {
                  VaultPlayerType type = entry.getKey();
                  Set<ServerPlayer> players = entry.getValue();
                  if (type == VaultPlayerType.RUNNER) {
                     return players.stream()
                        .map(
                           player -> {
                              VaultRunner runner = new VaultRunner(player.getUUID());
                              runner.getBehaviours().add(new VaultBehaviour(IS_RUNNER.and(IS_DEAD.or(NO_TIME_LEFT.and(OBJECTIVES_LEFT))), RUNNER_TO_SPECTATOR));
                              runner.getBehaviours()
                                 .add(
                                    new VaultBehaviour(
                                       NO_OBJECTIVES_LEFT_GLOBALLY.and(NO_TIME_LEFT.or(NO_RUNNERS_LEFT)),
                                       REMOVE_SCAVENGER_ITEMS.then(REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(EXIT_SAFELY)
                                    )
                                 );
                              runner.getBehaviours()
                                 .add(
                                    new VaultBehaviour(
                                       OBJECTIVES_LEFT_GLOBALLY.and(NO_RUNNERS_LEFT), REMOVE_SCAVENGER_ITEMS.then(SAVE_SOULBOUND_GEAR.then(EXIT_DEATH))
                                    )
                                 );
                              runner.getBehaviours().add(new VaultBehaviour(IS_FINISHED.negate(), TICK_SPAWNER.then(TICK_CHEST_PITY)));
                              runner.getBehaviours().add(new VaultBehaviour(AFTER_GRACE_PERIOD.and(IS_FINISHED.negate()), TICK_INFLUENCES));
                              runner.getBehaviours().add(new VaultBehaviour(IS_RUNNER, PAUSE_IN_ARENA.then(CHECK_BAIL)));
                              runner.getProperties().create(SPAWNER, new VaultSpawner());
                              runner.getProperties().create(CHEST_PITY, new VaultChestPity());
                              runner.getTimer().start(objective.getVaultTimerStart(ModConfigs.VAULT_GENERAL.getTickCounter()));
                              return runner;
                           }
                        );
                  } else {
                     if (type == VaultPlayerType.SPECTATOR) {
                     }

                     return Stream.empty();
                  }
               }
            )
            .collect(Collectors.toList())
      );
      vault.getAllObjectives().add(objective.thenComplete(LEVEL_UP_GEAR).thenComplete(VICTORY_SCENE));
      vault.getAllObjectives().forEach(obj -> obj.initialize(srv, vault));
      return vault;
   }

   public static VaultRaid coop(
      VaultGenerator generator,
      VaultTask initializer,
      RaidProperties properties,
      VaultObjective objective,
      List<VaultEvent<?>> events,
      Map<VaultPlayerType, Set<ServerPlayer>> playersMap
   ) {
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      VaultRaid vault = new VaultRaid(
         generator,
         initializer,
         properties,
         events,
         playersMap.entrySet()
            .stream()
            .flatMap(
               entry -> {
                  VaultPlayerType type = entry.getKey();
                  Set<ServerPlayer> players = entry.getValue();
                  if (type == VaultPlayerType.RUNNER) {
                     return players.stream()
                        .map(
                           player -> {
                              VaultRunner runner = new VaultRunner(player.getUUID());
                              runner.getBehaviours()
                                 .add(
                                    new VaultBehaviour(
                                       NO_OBJECTIVES_LEFT_GLOBALLY.and(NO_TIME_LEFT),
                                       REMOVE_SCAVENGER_ITEMS.then(REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(EXIT_SAFELY)
                                    )
                                 );
                              runner.getBehaviours().add(new VaultBehaviour(IS_RUNNER.and(IS_DEAD.or(NO_TIME_LEFT)), EXIT_DEATH_ALL));
                              runner.getBehaviours().add(new VaultBehaviour(IS_FINISHED.negate(), TICK_SPAWNER.then(TICK_CHEST_PITY)));
                              runner.getBehaviours().add(new VaultBehaviour(AFTER_GRACE_PERIOD.and(IS_FINISHED.negate()), TICK_INFLUENCES));
                              runner.getBehaviours().add(new VaultBehaviour(IS_RUNNER, PAUSE_IN_ARENA.then(CHECK_BAIL_COOP)));
                              runner.getBehaviours()
                                 .add(
                                    new VaultBehaviour(
                                       NO_ACTIVE_RUNNERS_LEFT, REMOVE_SCAVENGER_ITEMS.then(REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(EXIT_SAFELY)
                                    )
                                 );
                              runner.getProperties().create(SPAWNER, new VaultSpawner());
                              runner.getProperties().create(CHEST_PITY, new VaultChestPity());
                              runner.getTimer().start(objective.getVaultTimerStart(ModConfigs.VAULT_GENERAL.getTickCounter()));
                              return runner;
                           }
                        );
                  } else {
                     if (type == VaultPlayerType.SPECTATOR) {
                     }

                     return Stream.empty();
                  }
               }
            )
            .collect(Collectors.toList())
      );
      vault.getAllObjectives().add(objective.thenComplete(LEVEL_UP_GEAR).thenComplete(VICTORY_SCENE));
      vault.getAllObjectives().forEach(obj -> obj.initialize(srv, vault));
      return vault;
   }

   public static VaultRaid lobby(
      VaultGenerator generator,
      VaultTask initializer,
      RaidProperties properties,
      VaultObjective objective,
      List<VaultEvent<?>> events,
      Map<VaultPlayerType, Set<ServerPlayer>> playersMap
   ) {
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      VaultRaid vault = new VaultRaid(generator, initializer, properties, events, playersMap.entrySet().stream().flatMap(entry -> {
         Set<ServerPlayer> players = entry.getValue();
         return players.stream().map(player -> {
            VaultMember member = new VaultMember(player.getUUID());
            member.getProperties().create(CAN_HEAL, true);
            member.getBehaviours().add(new VaultBehaviour(IS_OUTSIDE, TP_TO_START));
            member.getBehaviours().add(new VaultBehaviour(IS_DEAD.negate(), TICK_LOBBY));
            return member;
         });
      }).collect(Collectors.toList()));
      vault.getProperties().create(LOBBY, new VaultLobby());
      vault.getProperties().create(FORCE_ACTIVE, true);
      vault.getAllObjectives().forEach(obj -> obj.initialize(srv, vault));
      return vault;
   }

   public static void init() {
   }

   public static VaultRaid.Builder builder(VaultLogic logic, int vaultLevel, @Nullable VaultObjective objective) {
      return new VaultRaid.Builder(logic, vaultLevel, objective);
   }

   static {
      TimeExtension.REGISTRY.put(FruitExtension.ID, FruitExtension::new);
      TimeExtension.REGISTRY.put(RelicExtension.ID, RelicExtension::new);
      TimeExtension.REGISTRY.put(FallbackExtension.ID, FallbackExtension::new);
      TimeExtension.REGISTRY.put(WinExtension.ID, WinExtension::new);
      TimeExtension.REGISTRY.put(ModifierExtension.ID, ModifierExtension::new);
      TimeExtension.REGISTRY.put(TimeAltarExtension.ID, TimeAltarExtension::new);
      TimeExtension.REGISTRY.put(AccelerationExtension.ID, AccelerationExtension::new);
      TimeExtension.REGISTRY.put(RoomGenerationExtension.ID, RoomGenerationExtension::new);
      TimeExtension.REGISTRY.put(FavourExtension.ID, FavourExtension::new);
      TimeExtension.REGISTRY.put(SandExtension.ID, SandExtension::new);
      VaultPlayer.REGISTRY.put(VaultRunner.ID, VaultRunner::new);
      VaultPlayer.REGISTRY.put(VaultSpectator.ID, VaultSpectator::new);
      VaultPlayer.REGISTRY.put(VaultMember.ID, VaultMember::new);
      VaultPiece.REGISTRY.put(VaultObelisk.ID, VaultObelisk::new);
      VaultPiece.REGISTRY.put(VaultRoom.ID, VaultRoom::new);
      VaultPiece.REGISTRY.put(VaultStart.ID, VaultStart::new);
      VaultPiece.REGISTRY.put(VaultTreasure.ID, VaultTreasure::new);
      VaultPiece.REGISTRY.put(VaultTunnel.ID, VaultTunnel::new);
      VaultPiece.REGISTRY.put(VaultRaidRoom.ID, VaultRaidRoom::new);
      VaultPiece.REGISTRY.put(FinalVaultLobby.ID, FinalVaultLobby::new);
      VaultPiece.REGISTRY.put(VaultPortal.ID, VaultPortal::new);
      VaultRoomLayoutRegistry.init();
      VaultInfluenceRegistry.init();
   }

   public static class Builder {
      private final VaultLogic logic;
      private final VaultObjective objective;
      private VaultTask initializer;
      private VaultTask levelInitializer = VaultRaid.INIT_LEVEL;
      private Supplier<? extends VaultGenerator> generator;
      private final RaidProperties attributes = new RaidProperties();
      private final List<VaultEvent<?>> events = new ArrayList<>();
      private final Map<VaultPlayerType, Set<ServerPlayer>> players = new HashMap<>();

      protected Builder(VaultLogic logic, int vaultLevel, @Nullable VaultObjective objective) {
         this.objective = objective == null ? logic.getRandomObjective(vaultLevel) : objective;
         this.generator = this.objective == null ? null : this.objective.getVaultGenerator();
         this.logic = logic;
      }

      public VaultRaid.Builder setInitializer(VaultTask initializer) {
         this.initializer = initializer;
         return this;
      }

      public VaultRaid.Builder setLevelInitializer(VaultTask initializer) {
         this.levelInitializer = initializer;
         return this;
      }

      public VaultTask getLevelInitializer() {
         return this.levelInitializer;
      }

      public VaultRaid.Builder setGenerator(Supplier<? extends VaultGenerator> generator) {
         this.generator = generator;
         return this;
      }

      public VaultRaid.Builder addPlayer(VaultPlayerType type, ServerPlayer player) {
         return this.addPlayers(type, Stream.of(player));
      }

      public VaultRaid.Builder addPlayers(VaultPlayerType type, Collection<ServerPlayer> player) {
         return this.addPlayers(type, player.stream());
      }

      public VaultRaid.Builder addPlayers(VaultPlayerType type, Stream<ServerPlayer> player) {
         Set<ServerPlayer> players = this.players.computeIfAbsent(type, key -> new HashSet<>());
         player.forEach(players::add);
         return this;
      }

      public VaultRaid.Builder addEvents(VaultEvent<?>... events) {
         return this.addEvents(Arrays.asList(events));
      }

      public VaultRaid.Builder addEvents(Collection<VaultEvent<?>> events) {
         this.events.addAll(events);
         return this;
      }

      public <T, I extends VAttribute.Instance<T>> boolean contains(VAttribute<T, I> attribute) {
         return this.attributes.exists(attribute);
      }

      public <T, I extends VAttribute.Instance<T>> VaultRaid.Builder set(VAttribute<T, I> attribute, T value) {
         this.attributes.create(attribute, value);
         return this;
      }

      public VaultRaid build() {
         return this.logic.getFactory().create(this.generator.get(), this.initializer, this.attributes, this.objective, this.events, this.players);
      }
   }

   @FunctionalInterface
   public interface Factory {
      VaultRaid create(
         VaultGenerator var1, VaultTask var2, RaidProperties var3, VaultObjective var4, List<VaultEvent<?>> var5, Map<VaultPlayerType, Set<ServerPlayer>> var6
      );
   }
}
