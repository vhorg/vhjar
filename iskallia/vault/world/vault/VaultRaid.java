package iskallia.vault.world.vault;

import iskallia.vault.Vault;
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
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.entity.EntityScaler;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.BasicScavengerItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.nbt.NonNullVListNBT;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.network.message.VaultModifierMessage;
import iskallia.vault.network.message.VaultOverlayMessage;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.util.RelicSet;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.PhoenixModifierSnapshotData;
import iskallia.vault.world.data.PhoenixSetSnapshotData;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.SoulboundSnapshotData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.data.VaultSetsData;
import iskallia.vault.world.raid.RaidProperties;
import iskallia.vault.world.vault.event.VaultEvent;
import iskallia.vault.world.vault.event.VaultListener;
import iskallia.vault.world.vault.gen.ArchitectEventGenerator;
import iskallia.vault.world.vault.gen.FinalBossGenerator;
import iskallia.vault.world.vault.gen.FinalLobbyGenerator;
import iskallia.vault.world.vault.gen.FragmentedVaultGenerator;
import iskallia.vault.world.vault.gen.RaidChallengeGenerator;
import iskallia.vault.world.vault.gen.VaultGenerator;
import iskallia.vault.world.vault.gen.VaultTroveGenerator;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutRegistry;
import iskallia.vault.world.vault.gen.piece.FinalVaultBoss;
import iskallia.vault.world.vault.gen.piece.FinalVaultLobby;
import iskallia.vault.world.vault.gen.piece.VaultGodEye;
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
import iskallia.vault.world.vault.logic.objective.KillTheBossObjective;
import iskallia.vault.world.vault.logic.objective.ScavengerHuntObjective;
import iskallia.vault.world.vault.logic.objective.SummonAndKillAllBossesObjective;
import iskallia.vault.world.vault.logic.objective.SummonAndKillBossObjective;
import iskallia.vault.world.vault.logic.objective.TreasureHuntObjective;
import iskallia.vault.world.vault.logic.objective.TroveObjective;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.ancient.AncientObjective;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectSummonAndKillBossesObjective;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import iskallia.vault.world.vault.logic.objective.raid.RaidChallengeObjective;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.FrenzyModifier;
import iskallia.vault.world.vault.modifier.NoExitModifier;
import iskallia.vault.world.vault.modifier.ScaleModifier;
import iskallia.vault.world.vault.modifier.VaultModifier;
import iskallia.vault.world.vault.modifier.VaultModifiers;
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
import iskallia.vault.world.vault.time.extension.RelicSetExtension;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.STitlePacket.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class VaultRaid implements INBTSerializable<CompoundNBT> {
   protected VaultTimer timer = new VaultTimer().start(Integer.MAX_VALUE);
   protected VaultGenerator generator;
   protected VaultTask initializer;
   protected VaultModifiers modifiers = new VaultModifiers();
   protected RaidProperties properties = new RaidProperties();
   protected VaultInfluences influence = new VaultInfluences();
   protected ActiveRaid activeRaid = null;
   protected final VListNBT<VaultObjective, CompoundNBT> objectives = VListNBT.of(VaultObjective::fromNBT);
   protected final VListNBT<VaultEvent<?>, CompoundNBT> events = (VListNBT<VaultEvent<?>, CompoundNBT>)NonNullVListNBT.of(VaultEvent::fromNBT);
   protected final VListNBT<VaultPlayer, CompoundNBT> players = VListNBT.of(VaultPlayer::fromNBT);
   protected long creationTime = System.currentTimeMillis();
   public static final Supplier<FragmentedVaultGenerator> SINGLE_STAR = VaultGenerator.register(() -> new FragmentedVaultGenerator(Vault.id("single_star")));
   public static final Supplier<ArchitectEventGenerator> ARCHITECT_GENERATOR = VaultGenerator.register(() -> new ArchitectEventGenerator(Vault.id("architect")));
   public static final Supplier<VaultTroveGenerator> TROVE_GENERATOR = VaultGenerator.register(() -> new VaultTroveGenerator(Vault.id("vault_trove")));
   public static final Supplier<RaidChallengeGenerator> RAID_CHALLENGE_GENERATOR = VaultGenerator.register(
      () -> new RaidChallengeGenerator(Vault.id("raid_challenge"))
   );
   public static final Supplier<FinalLobbyGenerator> FINAL_LOBBY = VaultGenerator.register(() -> new FinalLobbyGenerator(Vault.id("final_lobby")));
   public static final Supplier<FinalBossGenerator> FINAL_BOSS = VaultGenerator.register(() -> new FinalBossGenerator(Vault.id("final_boss")));
   public static final VAttribute<RegistryKey<World>, RegistryKeyAttribute<World>> DIMENSION = new VAttribute<>(
      Vault.id("dimension"), RegistryKeyAttribute::new
   );
   public static final VAttribute<MutableBoundingBox, BoundingBoxAttribute> BOUNDING_BOX = new VAttribute<>(Vault.id("bounding_box"), BoundingBoxAttribute::new);
   public static final VAttribute<BlockPos, BlockPosAttribute> START_POS = new VAttribute<>(Vault.id("start_pos"), BlockPosAttribute::new);
   public static final VAttribute<Direction, EnumAttribute<Direction>> START_FACING = new VAttribute<>(
      Vault.id("start_facing"), () -> new EnumAttribute(Direction.class)
   );
   public static final VAttribute<CrystalData, CompoundAttribute<CrystalData>> CRYSTAL_DATA = new VAttribute<>(
      Vault.id("crystal_data"), () -> CompoundAttribute.of(CrystalData::new)
   );
   public static final VAttribute<Boolean, BooleanAttribute> IS_RAFFLE = new VAttribute<>(Vault.id("is_raffle"), BooleanAttribute::new);
   public static final VAttribute<Boolean, BooleanAttribute> COW_VAULT = new VAttribute<>(Vault.id("cow"), BooleanAttribute::new);
   public static final VAttribute<UUID, UUIDAttribute> HOST = new VAttribute<>(Vault.id("host"), UUIDAttribute::new);
   public static final VAttribute<UUID, UUIDAttribute> IDENTIFIER = new VAttribute<>(Vault.id("identifier"), UUIDAttribute::new);
   public static final VAttribute<UUID, UUIDAttribute> PARENT = new VAttribute<>(Vault.id("parent"), UUIDAttribute::new);
   public static final VAttribute<VaultLobby, CompoundAttribute<VaultLobby>> LOBBY = new VAttribute<>(
      Vault.id("lobby"), () -> CompoundAttribute.of(VaultLobby::new)
   );
   public static final VAttribute<Boolean, BooleanAttribute> FORCE_ACTIVE = new VAttribute<>(Vault.id("force_active"), BooleanAttribute::new);
   public static final VAttribute<Integer, IntegerAttribute> RAID_INDEX = new VAttribute<>(Vault.id("raid_index"), IntegerAttribute::new);
   public static final VAttribute<Boolean, BooleanAttribute> GRANTED_EXP = new VAttribute<>(Vault.id("granted_exp"), BooleanAttribute::new);
   public static final VAttribute<String, StringAttribute> PLAYER_BOSS_NAME = new VAttribute<>(Vault.id("player_boss_name"), StringAttribute::new);
   @Deprecated
   public static final VAttribute<Boolean, BooleanAttribute> CAN_EXIT = new VAttribute<>(Vault.id("can_exit"), BooleanAttribute::new);
   public static final VAttribute<VaultSpawner, CompoundAttribute<VaultSpawner>> SPAWNER = new VAttribute<>(
      Vault.id("spawner"), () -> CompoundAttribute.of(VaultSpawner::new)
   );
   public static final VAttribute<VaultChestPity, CompoundAttribute<VaultChestPity>> CHEST_PITY = new VAttribute<>(
      Vault.id("chest_pity"), () -> CompoundAttribute.of(VaultChestPity::new)
   );
   public static final VAttribute<VaultSandEvent, CompoundAttribute<VaultSandEvent>> SAND_EVENT = new VAttribute<>(
      Vault.id("sand_event"), () -> CompoundAttribute.of(VaultSandEvent::new)
   );
   public static final VAttribute<Boolean, BooleanAttribute> SHOW_TIMER = new VAttribute<>(Vault.id("show_timer"), BooleanAttribute::new);
   public static final VAttribute<Boolean, BooleanAttribute> CAN_HEAL = new VAttribute<>(Vault.id("can_heal"), BooleanAttribute::new);
   public static final VAttribute<Integer, IntegerAttribute> LEVEL = new VAttribute<>(Vault.id("level"), IntegerAttribute::new);
   public static final VaultCondition IS_FINISHED = VaultCondition.register(Vault.id("is_finished"), (vault, player, world) -> vault.isFinished());
   public static final VaultCondition IS_RUNNER = VaultCondition.register(Vault.id("is_runner"), (vault, player, world) -> player instanceof VaultRunner);
   public static final VaultCondition IS_SPECTATOR = VaultCondition.register(
      Vault.id("is_spectator"), (vault, player, world) -> player instanceof VaultSpectator
   );
   public static final VaultCondition IS_OUTSIDE = VaultCondition.register(Vault.id("is_outside"), (vault, player, world) -> {
      boolean[] outside = new boolean[1];
      player.runIfPresent(world.func_73046_m(), sPlayer -> outside[0] = !VaultUtils.inVault(vault, sPlayer));
      return outside[0];
   });
   public static final VaultCondition AFTER_GRACE_PERIOD = VaultCondition.register(
      Vault.id("after_grace_period"), (vault, player, world) -> vault.getTimer().getRunTime() > 300
   );
   public static final VaultCondition IS_DEAD = VaultCondition.register(Vault.id("is_dead"), (vault, player, world) -> {
      MutableBoolean dead = new MutableBoolean(false);
      player.runIfPresent(world.func_73046_m(), playerEntity -> dead.setValue(playerEntity.func_233643_dh_()));
      return dead.booleanValue();
   });
   public static final VaultCondition HAS_EXITED = VaultCondition.register(Vault.id("has_exited"), (vault, player, world) -> player.hasExited());
   public static final VaultCondition TIME_LEFT = VaultCondition.register(Vault.id("time_left"), (vault, player, world) -> player.getTimer().getTimeLeft() > 0);
   public static final VaultCondition NO_TIME_LEFT = VaultCondition.register(Vault.id("no_time_left"), TIME_LEFT.negate());
   public static final VaultCondition OBJECTIVES_LEFT = VaultCondition.register(
      Vault.id("objectives_left"), (vault, player, world) -> player.getObjectives().size() > 0 || vault.getActiveObjectives().size() > 0
   );
   public static final VaultCondition NO_OBJECTIVES_LEFT = VaultCondition.register(Vault.id("no_objectives_left"), OBJECTIVES_LEFT.negate());
   public static final VaultCondition OBJECTIVES_LEFT_GLOBALLY = VaultCondition.register(
      Vault.id("objectives_left_globally"), (vault, player, world) -> vault.players.stream().anyMatch(player1 -> OBJECTIVES_LEFT.test(vault, player1, world))
   );
   public static final VaultCondition NO_OBJECTIVES_LEFT_GLOBALLY = VaultCondition.register(
      Vault.id("no_objectives_left_globally"), OBJECTIVES_LEFT_GLOBALLY.negate()
   );
   public static final VaultCondition RUNNERS_LEFT = VaultCondition.register(
      Vault.id("runners_left"), (vault, player, world) -> vault.players.stream().anyMatch(player1 -> player1 instanceof VaultRunner)
   );
   public static final VaultCondition NO_RUNNERS_LEFT = VaultCondition.register(Vault.id("no_runners_left"), RUNNERS_LEFT.negate());
   public static final VaultCondition ACTIVE_RUNNERS_LEFT = VaultCondition.register(
      Vault.id("active_runners_left"),
      (vault, player, world) -> vault.players.stream().anyMatch(player1 -> player1 instanceof VaultRunner && !player1.hasExited())
   );
   public static final VaultCondition NO_ACTIVE_RUNNERS_LEFT = VaultCondition.register(Vault.id("no_active_runners_left"), ACTIVE_RUNNERS_LEFT.negate());
   public static final VaultTask GRANT_EXP_COMPLETE = VaultTask.register(Vault.id("public_grant_exp_complete"), (vault, player, world) -> {
      if (!player.getProperties().exists(GRANTED_EXP)) {
         player.grantVaultExp(world.func_73046_m(), 1.0F);
         player.getProperties().create(GRANTED_EXP, true);
      }
   });
   public static final VaultTask GRANT_EXP_BAIL = VaultTask.register(Vault.id("public_grant_exp_bail"), (vault, player, world) -> {
      if (!player.getProperties().exists(GRANTED_EXP)) {
         player.grantVaultExp(world.func_73046_m(), 0.5F);
         player.getProperties().create(GRANTED_EXP, true);
      }
   });
   public static final VaultTask GRANT_EXP_DEATH = VaultTask.register(Vault.id("public_grant_exp_death"), (vault, player, world) -> {
      if (!player.getProperties().exists(GRANTED_EXP)) {
         player.grantVaultExp(world.func_73046_m(), 0.25F);
         player.getProperties().create(GRANTED_EXP, true);
      }
   });
   public static final VaultTask CHECK_BAIL = VaultTask.register(
      Vault.id("check_bail"),
      (vault, player, world) -> {
         if (vault.getTimer().getRunTime() >= 200) {
            player.runIfPresent(
               world.func_73046_m(),
               sPlayer -> {
                  if (!vault.getGenerator().getPiecesAt(sPlayer.func_233580_cy_(), VaultStart.class).isEmpty()) {
                     AxisAlignedBB box = sPlayer.func_174813_aQ();
                     BlockPos min = new BlockPos(box.field_72340_a + 0.001, box.field_72338_b + 0.001, box.field_72339_c + 0.001);
                     BlockPos max = new BlockPos(box.field_72336_d - 0.001, box.field_72337_e - 0.001, box.field_72334_f - 0.001);
                     Mutable pos = new Mutable();
                     if (world.func_175707_a(min, max) && !sPlayer.func_242280_ah()) {
                        for (int xx = min.func_177958_n(); xx <= max.func_177958_n(); xx++) {
                           for (int yy = min.func_177956_o(); yy <= max.func_177956_o(); yy++) {
                              for (int zz = min.func_177952_p(); zz <= max.func_177952_p(); zz++) {
                                 BlockState state = world.func_180495_p(pos.func_181079_c(xx, yy, zz));
                                 if (state.func_177230_c() == ModBlocks.VAULT_PORTAL) {
                                    if (sPlayer.func_242280_ah()) {
                                       sPlayer.func_242279_ag();
                                       return;
                                    }

                                    if (!vault.canExit(player)) {
                                       StringTextComponent text = new StringTextComponent("You cannot exit this Vault!");
                                       text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)));
                                       sPlayer.func_146105_b(text, true);
                                       return;
                                    }

                                    vault.getAllObjectives().forEach(objective -> objective.notifyBail(vault, player, world));
                                    sPlayer.func_242279_ag();
                                    VaultRaid.REMOVE_SCAVENGER_ITEMS
                                       .then(VaultRaid.REMOVE_INVENTORY_RESTORE_SNAPSHOTS)
                                       .then(VaultRaid.EXIT_SAFELY)
                                       .execute(vault, player, world);
                                    IFormattableTextComponent playerName = sPlayer.func_145748_c_().func_230532_e_();
                                    playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
                                    StringTextComponent suffix = new StringTextComponent(" bailed.");
                                    world.func_73046_m()
                                       .func_184103_al()
                                       .func_232641_a_(
                                          new StringTextComponent("").func_230529_a_(playerName).func_230529_a_(suffix), ChatType.CHAT, player.getPlayerId()
                                       );
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
      Vault.id("check_bail_coop"),
      (vault, player, world) -> {
         if (vault.getTimer().getRunTime() >= 200) {
            player.runIfPresent(
               world.func_73046_m(),
               sPlayer -> {
                  if (!vault.getGenerator().getPiecesAt(sPlayer.func_233580_cy_(), VaultStart.class).isEmpty()) {
                     AxisAlignedBB box = sPlayer.func_174813_aQ();
                     BlockPos min = new BlockPos(box.field_72340_a + 0.001, box.field_72338_b + 0.001, box.field_72339_c + 0.001);
                     BlockPos max = new BlockPos(box.field_72336_d - 0.001, box.field_72337_e - 0.001, box.field_72334_f - 0.001);
                     Mutable pos = new Mutable();
                     if (world.func_175707_a(min, max)) {
                        for (int i = min.func_177958_n(); i <= max.func_177958_n(); i++) {
                           for (int j = min.func_177956_o(); j <= max.func_177956_o(); j++) {
                              for (int k = min.func_177952_p(); k <= max.func_177952_p(); k++) {
                                 BlockState state = world.func_180495_p(pos.func_181079_c(i, j, k));
                                 if (state.func_177230_c() == ModBlocks.VAULT_PORTAL) {
                                    if (sPlayer.func_242280_ah()) {
                                       sPlayer.func_242279_ag();
                                       return;
                                    }

                                    if (!vault.canExit(player)) {
                                       StringTextComponent text = new StringTextComponent("You cannot exit this Vault!");
                                       text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)));
                                       sPlayer.func_146105_b(text, true);
                                       return;
                                    }

                                    vault.getAllObjectives().forEach(objective -> objective.notifyBail(vault, player, world));
                                    sPlayer.func_242279_ag();
                                    VaultRaid.RUNNER_TO_SPECTATOR.execute(vault, player, world);
                                    VaultRaid.HIDE_OVERLAY.execute(vault, player, world);
                                    IFormattableTextComponent playerName = sPlayer.func_145748_c_().func_230532_e_();
                                    playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
                                    StringTextComponent suffix = new StringTextComponent(" bailed.");
                                    world.func_73046_m()
                                       .func_184103_al()
                                       .func_232641_a_(
                                          new StringTextComponent("").func_230529_a_(playerName).func_230529_a_(suffix), ChatType.CHAT, player.getPlayerId()
                                       );
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
      Vault.id("check_bail_final"),
      (vault, player, world) -> player.runIfPresent(
         world.func_73046_m(),
         sPlayer -> {
            if (!vault.getGenerator().getPiecesAt(sPlayer.func_233580_cy_(), VaultStart.class).isEmpty()) {
               AxisAlignedBB box = sPlayer.func_174813_aQ();
               BlockPos min = new BlockPos(box.field_72340_a + 0.001, box.field_72338_b + 0.001, box.field_72339_c + 0.001);
               BlockPos max = new BlockPos(box.field_72336_d - 0.001, box.field_72337_e - 0.001, box.field_72334_f - 0.001);
               Mutable pos = new Mutable();
               if (world.func_175707_a(min, max)) {
                  for (int i = min.func_177958_n(); i <= max.func_177958_n(); i++) {
                     for (int j = min.func_177956_o(); j <= max.func_177956_o(); j++) {
                        for (int k = min.func_177952_p(); k <= max.func_177952_p(); k++) {
                           BlockState state = world.func_180495_p(pos.func_181079_c(i, j, k));
                           if (state.func_177230_c() == ModBlocks.VAULT_PORTAL) {
                              if (sPlayer.func_242280_ah()) {
                                 sPlayer.func_242279_ag();
                                 return;
                              }

                              if (!vault.canExit(player)) {
                                 StringTextComponent text = new StringTextComponent("You cannot exit this Vault!");
                                 text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)));
                                 sPlayer.func_146105_b(text, true);
                                 return;
                              }

                              vault.getAllObjectives().forEach(objective -> objective.notifyBail(vault, player, world));
                              sPlayer.func_242279_ag();
                              new ArrayList<>(vault.getPlayers()).forEach(vaultPlayer -> {
                                 if (vaultPlayer instanceof VaultRunner) {
                                    VaultRaid.RUNNER_TO_SPECTATOR.execute(vault, vaultPlayer, world);
                                 }
                              });
                              vault.getProperties().create(FORCE_ACTIVE, false);
                              VaultRaid.HIDE_OVERLAY.execute(vault, player, world);
                              IFormattableTextComponent playerName = sPlayer.func_145748_c_().func_230532_e_();
                              playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
                              StringTextComponent suffix = new StringTextComponent(" bailed.");
                              world.func_73046_m()
                                 .func_184103_al()
                                 .func_232641_a_(
                                    new StringTextComponent("").func_230529_a_(playerName).func_230529_a_(suffix), ChatType.CHAT, player.getPlayerId()
                                 );
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
      Vault.id("tick_sand_event"), (vault, player, world) -> vault.getProperties().getBase(SAND_EVENT).ifPresent(event -> event.execute(vault, player, world))
   );
   public static final VaultTask TICK_CHEST_PITY = VaultTask.register(
      Vault.id("tick_chest_pity"), (vault, player, world) -> player.getProperties().getBase(CHEST_PITY).ifPresent(event -> event.execute(vault, player, world))
   );
   public static final VaultTask TICK_SPAWNER = VaultTask.register(
      Vault.id("tick_spawner"),
      (vault, player, world) -> {
         if (!vault.getActiveObjectives().isEmpty()) {
            player.getProperties()
               .get(SPAWNER)
               .ifPresent(
                  attribute -> {
                     VaultSpawner spawner = (VaultSpawner)attribute.getBaseValue();
                     if (player.getTimer().getRunTime() >= 300) {
                        int level = player.getProperties().getValue(LEVEL);
                        VaultSpawner.Config c = ModConfigs.VAULT_MOBS.getForLevel(level).MOB_MISC.SPAWNER;
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
      Vault.id("tick_lobby"), (vault, player, world) -> vault.getProperties().get(LOBBY).ifPresent(attribute -> {
         VaultLobby lobby = (VaultLobby)attribute.getBaseValue();
         lobby.execute(vault, player, world);
         vault.getProperties().create(LOBBY, lobby);
      })
   );
   public static final VaultTask TICK_INFLUENCES = VaultTask.register(Vault.id("tick_influences"), (vault, player, world) -> {
      if (!vault.getInfluences().isInitialized()) {
         VaultInfluenceHandler.initializeInfluences(vault, world);
         vault.getInfluences().setInitialized();
      }

      vault.getInfluences().tick(vault, player, world);
   });
   public static final VaultTask TP_TO_START = VaultTask.register(
      Vault.id("tp_to_start"),
      (vault, player, world) -> player.runIfPresent(
         world.func_73046_m(),
         playerEntity -> {
            BlockPos start = vault.getProperties().getBaseOrDefault(START_POS, (BlockPos)null);
            Direction facing = vault.getProperties().getBaseOrDefault(START_FACING, (Direction)null);
            MutableBoundingBox box = vault.getProperties().getValue(BOUNDING_BOX);
            if (start == null) {
               Vault.LOGGER.warn("No vault start was found.");
               playerEntity.func_200619_a(
                  world,
                  box.field_78897_a + box.func_78883_b() / 2.0F,
                  256.0,
                  box.field_78896_c + box.func_78880_d() / 2.0F,
                  playerEntity.field_70177_z,
                  playerEntity.field_70125_A
               );
            } else {
               playerEntity.func_200619_a(
                  world,
                  start.func_177958_n() + 0.5,
                  start.func_177956_o() + 0.2,
                  start.func_177952_p() + 0.5,
                  facing == null ? world.func_201674_k().nextFloat() * 360.0F : facing.func_176746_e().func_185119_l(),
                  0.0F
               );
            }

            playerEntity.func_242279_ag();
            playerEntity.func_230245_c_(true);
         }
      )
   );
   public static final VaultTask INIT_LEVEL = VaultTask.register(Vault.id("init_level"), (vault, player, world) -> {
      int currentLevel = vault.getProperties().getBaseOrDefault(LEVEL, 0);
      int playerLevel = PlayerVaultStatsData.get(world).getVaultStats(player.getPlayerId()).getVaultLevel();
      vault.getProperties().create(LEVEL, Math.max(currentLevel, playerLevel));
      player.getProperties().create(LEVEL, playerLevel);
   });
   public static final VaultTask INIT_LEVEL_COOP = VaultTask.register(
      Vault.id("init_level_coop"), (vault, player, world) -> vault.getProperties().getBase(HOST).ifPresent(hostId -> {
         int vaultLevel = PlayerVaultStatsData.get(world).getVaultStats(hostId).getVaultLevel();
         vaultLevel += Math.max(vault.getPlayers().size() - 1, 0) * 2;
         vault.getProperties().create(LEVEL, vaultLevel);
         player.getProperties().create(LEVEL, vaultLevel);
      })
   );
   public static final VaultTask INIT_LEVEL_FINAL = VaultTask.register(Vault.id("init_level_final"), (vault, player, world) -> {
      vault.getProperties().create(LEVEL, 1000);
      player.getProperties().create(LEVEL, 1000);
   });
   public static final VaultTask INIT_RELIC_TIME = VaultTask.register(
      Vault.id("init_relic_extension"),
      (vault, player, world) -> {
         Set<String> sets = new HashSet<>();

         for (VaultPlayer player2 : vault.getPlayers()) {
            Set<String> newSets = VaultSetsData.get(world).getCraftedSets(player2.getPlayerId());
            if (newSets.size() > sets.size()) {
               sets = newSets;
            }
         }

         sets.stream()
            .<ResourceLocation>map(ResourceLocation::new)
            .forEach(set -> player.getTimer().addTime(new RelicSetExtension(RelicSet.REGISTRY.get(set), ModConfigs.VAULT_RELICS.getExtraTickPerSet()), 0));
      }
   );
   @Deprecated
   public static final VaultTask INIT_FAVOUR_TIME = VaultTask.register(Vault.id("init_favour_extension"), (vault, player, world) -> {});
   public static final VaultTask INIT_SANDS_EVENT = VaultTask.register(Vault.id("init_sand_event"), (vault, player, world) -> {
      if (ModConfigs.SAND_EVENT.isEnabled()) {
         vault.getProperties().create(SAND_EVENT, new VaultSandEvent());
         player.getBehaviours().add(new VaultBehaviour(IS_FINISHED.negate(), TICK_SAND_EVENT));
      }
   });
   public static final VaultTask INIT_COW_VAULT = VaultTask.register(
      Vault.id("init_cow_vault"),
      (vault, player, world) -> {
         if (!vault.getProperties().exists(COW_VAULT)) {
            CrystalData crystalData = vault.getProperties().getBase(CRYSTAL_DATA).orElse(CrystalData.EMPTY);
            if (crystalData.getType().canBeCowVault()
               && crystalData.getSelectedObjective() == null
               && crystalData.getModifiers().isEmpty()
               && !vault.getProperties().getBaseOrDefault(IS_RAFFLE, false)) {
               boolean isCowVault = VaultCowOverrides.forceSpecialVault;
               vault.getProperties().create(COW_VAULT, isCowVault);
               if (isCowVault) {
                  VaultCowOverrides.setupVault(vault);
                  vault.getModifiers().setInitialized();
                  vault.getAllObjectives().clear();
                  VaultObjective objective = new SummonAndKillBossObjective(Vault.id("summon_and_kill_boss"));
                  vault.getAllObjectives().add(objective.thenComplete(VaultRaid.LEVEL_UP_GEAR).thenComplete(VaultRaid.VICTORY_SCENE));
               }
            } else {
               vault.getProperties().create(COW_VAULT, false);
            }
         }

         VaultCowOverrides.forceSpecialVault = false;
      }
   );
   public static final VaultTask INIT_GLOBAL_MODIFIERS = VaultTask.register(Vault.id("init_global_modifiers"), (vault, player, world) -> {
      Random rand = world.func_201674_k();
      if (!vault.getModifiers().isInitialized()) {
         CrystalData crystalData = vault.getProperties().getBase(CRYSTAL_DATA).orElse(CrystalData.EMPTY);
         crystalData.apply(vault, rand);
         if (!crystalData.preventsRandomModifiers()) {
            vault.getModifiers().generateGlobal(vault, world, rand);
         }

         vault.getModifiers().setInitialized();
      }

      vault.getModifiers().apply(vault, player, world, rand);
      if (!player.getModifiers().isInitialized()) {
         player.getModifiers().setInitialized();
      }

      player.getModifiers().apply(vault, player, world, rand);
   });
   public static final VaultTask RUNNER_TO_SPECTATOR = VaultTask.register(Vault.id("runner_to_spectator"), (vault, player, world) -> {
      vault.players.remove(player);
      vault.players.add(new VaultSpectator((VaultRunner)player));
   });
   public static final VaultTask HIDE_OVERLAY = VaultTask.register(
      Vault.id("hide_overlay"), (vault, player, world) -> player.sendIfPresent(world.func_73046_m(), VaultOverlayMessage.hide())
   );
   public static final VaultTask PAUSE_IN_ARENA = VaultTask.register(Vault.id("pause_in_arena"), (vault, player, world) -> {});
   public static final VaultTask LEVEL_UP_GEAR = VaultTask.register(Vault.id("level_up_gear"), (vault, player, world) -> {
      if (player instanceof VaultRunner) {
         player.runIfPresent(world.func_73046_m(), playerEntity -> {
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
               ItemStack stack = playerEntity.func_184582_a(slot);
               if (stack.func_77973_b() instanceof VaultGear && ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
                  VaultGear.addLevel(stack, 1.0F);
               }
            }
         });
      }
   });
   public static final VaultTask REMOVE_SCAVENGER_ITEMS = VaultTask.register(
      Vault.id("remove_scavenger_items"), (vault, player, world) -> player.runIfPresent(world.func_73046_m(), playerEntity -> {
         PlayerInventory inventory = playerEntity.field_71071_by;

         for (int slot = 0; slot < inventory.func_70302_i_(); slot++) {
            ItemStack stack = inventory.func_70301_a(slot);
            if (stack.func_77973_b() instanceof BasicScavengerItem) {
               inventory.func_70299_a(slot, ItemStack.field_190927_a);
            }

            LazyOptional<IItemHandler> itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            itemHandler.ifPresent(handler -> {
               if (handler instanceof IItemHandlerModifiable) {
                  IItemHandlerModifiable invHandler = (IItemHandlerModifiable)handler;

                  for (int nestedSlot = 0; nestedSlot < invHandler.getSlots(); nestedSlot++) {
                     ItemStack nestedStack = invHandler.getStackInSlot(nestedSlot);
                     if (nestedStack.func_77973_b() instanceof BasicScavengerItem) {
                        invHandler.setStackInSlot(nestedSlot, ItemStack.field_190927_a);
                     }
                  }
               }
            });
            if (stack.func_77973_b() instanceof BlockItem && ((BlockItem)stack.func_77973_b()).func_179223_d() instanceof VaultCrateBlock) {
               CompoundNBT tag = stack.func_179543_a("BlockEntityTag");
               if (tag != null) {
                  NonNullList<ItemStack> stacks = NonNullList.func_191197_a(54, ItemStack.field_190927_a);
                  ItemStackHelper.func_191283_b(tag, stacks);

                  for (int i = 0; i < stacks.size(); i++) {
                     if (((ItemStack)stacks.get(i)).func_77973_b() instanceof BasicScavengerItem) {
                        stacks.set(i, ItemStack.field_190927_a);
                     }
                  }

                  ItemStackHelper.func_191282_a(tag, stacks);
               }
            }
         }
      })
   );
   public static final VaultTask SAVE_SOULBOUND_GEAR = VaultTask.register(
      Vault.id("save_soulbound_gear"), (vault, player, world) -> player.runIfPresent(world.func_73046_m(), sPlayer -> {
         if (!vault.getProperties().exists(PARENT)) {
            SoulboundSnapshotData data = SoulboundSnapshotData.get(world);
            if (!data.hasSnapshot(sPlayer)) {
               data.createSnapshot(sPlayer);
            }
         }
      })
   );
   public static final VaultTask REMOVE_INVENTORY_RESTORE_SNAPSHOTS = VaultTask.register(Vault.id("remove_inventory_snapshots"), (vault, player, world) -> {
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
      Vault.id("final_victory_scene"),
      (vault, player, world) -> {
         if (player instanceof VaultRunner) {
            player.getTimer().addTime(new WinExtension(player.getTimer(), 400), 0);
            player.runIfPresent(
               world.func_73046_m(),
               playerEntity -> {
                  FireworkRocketEntity fireworks = new FireworkRocketEntity(
                     world,
                     playerEntity.func_226277_ct_(),
                     playerEntity.func_226278_cu_(),
                     playerEntity.func_226281_cx_(),
                     new ItemStack(Items.field_196152_dE)
                  );
                  world.func_217376_c(fireworks);
                  world.func_184148_a(
                     null,
                     playerEntity.func_226277_ct_(),
                     playerEntity.func_226278_cu_(),
                     playerEntity.func_226281_cx_(),
                     SoundEvents.field_194228_if,
                     SoundCategory.MASTER,
                     1.0F,
                     1.0F
                  );
                  StringTextComponent title = new StringTextComponent("Branch Cleared!");
                  title.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
                  StringTextComponent subtitle = new StringTextComponent("Place your essence in the eye.");
                  subtitle.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
                  if (vault.getProperties().getValue(CRYSTAL_DATA).getType() == CrystalData.Type.FINAL_BOSS) {
                     title = new StringTextComponent("The End...");
                  }

                  STitlePacket titlePacket = new STitlePacket(Type.TITLE, title);
                  playerEntity.field_71135_a.func_147359_a(titlePacket);
               }
            );
         }
      }
   );
   public static final VaultTask EXIT_SAFELY = VaultTask.register(
      Vault.id("exit_safely"),
      (vault, player, world) -> player.runIfPresent(
         world.func_73046_m(),
         playerEntity -> {
            if (player instanceof VaultSpectator) {
               playerEntity.field_71134_c.func_73076_a(((VaultSpectator)player).oldGameType);
            }

            world.func_184148_a(
               null,
               playerEntity.func_226277_ct_(),
               playerEntity.func_226278_cu_(),
               playerEntity.func_226281_cx_(),
               ModSounds.VAULT_PORTAL_LEAVE,
               SoundCategory.PLAYERS,
               1.0F,
               1.0F
            );
            world.func_217384_a(null, playerEntity, ModSounds.VAULT_PORTAL_LEAVE, SoundCategory.PLAYERS, 1.0F, 1.0F);
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
                  TP_TO_START.execute(parentVault, member, world.func_73046_m().func_71218_a(parentVault.getProperties().getValue(DIMENSION)));
                  if (vault.getActiveObjectives().stream().allMatch(VaultObjective::isCompleted)) {
                     FINAL_VICTORY_SCENE.execute(vault, player, world);
                     player.runIfPresent(world.func_73046_m(), sPlayer -> lobby.snapshots.removeSnapshot(sPlayer));
                  } else {
                     player.runIfPresent(world.func_73046_m(), sPlayer -> {
                        lobby.snapshots.restoreSnapshot(sPlayer);
                        lobby.snapshots.removeSnapshot(sPlayer);
                     });
                  }

                  parentVault.getProperties().create(LOBBY, lobby);
                  lobby.exitVault(vault, world, parentVault, member, playerEntity, false);
                  vault.getPlayers().remove(player);
               });
               vault.getProperties().create(FORCE_ACTIVE, false);
            } else {
               VaultUtils.exitSafely(world.func_73046_m().func_71218_a(World.field_234918_g_), playerEntity);
            }
         }
      )
   );
   public static final VaultTask EXIT_DEATH = VaultTask.register(
      Vault.id("exit_death"),
      (vault, player, world) -> player.runIfPresent(
         world.func_73046_m(),
         playerEntity -> {
            if (player instanceof VaultSpectator) {
               playerEntity.field_71134_c.func_73076_a(((VaultSpectator)player).oldGameType);
            }

            world.func_184148_a(
               null,
               playerEntity.func_226277_ct_(),
               playerEntity.func_226278_cu_(),
               playerEntity.func_226281_cx_(),
               ModSounds.TIMER_KILL_SFX,
               SoundCategory.PLAYERS,
               1.0F,
               1.0F
            );
            world.func_217384_a(null, playerEntity, ModSounds.TIMER_KILL_SFX, SoundCategory.PLAYERS, 1.0F, 1.0F);
            playerEntity.field_71071_by.func_234564_a_(stack -> true, -1, playerEntity.field_71069_bz.func_234641_j_());
            playerEntity.field_71070_bA.func_75142_b();
            playerEntity.field_71069_bz.func_75130_a(playerEntity.field_71071_by);
            playerEntity.func_71113_k();
            playerEntity.func_70097_a(new DamageSource("vaultFailed").func_76348_h().func_76359_i(), 1.0E8F);
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
                  player.runIfPresent(world.func_73046_m(), sPlayer -> lobby.snapshots.restoreSnapshot(sPlayer));
                  parentVault.getProperties().create(LOBBY, lobby);
                  lobby.exitVault(vault, world, parentVault, member, playerEntity, true);
                  vault.getPlayers().remove(player);
               });
               vault.getProperties().create(FORCE_ACTIVE, false);
            }
         }
      )
   );
   public static final VaultTask EXIT_DEATH_ALL = VaultTask.register(
      Vault.id("exit_death_all"),
      (vault, player, world) -> new ArrayList<>(vault.players)
         .forEach(vPlayer -> REMOVE_SCAVENGER_ITEMS.then(SAVE_SOULBOUND_GEAR.then(GRANT_EXP_DEATH.then(EXIT_DEATH))).execute(vault, vPlayer, world))
   );
   public static final VaultTask EXIT_DEATH_ALL_NO_SAVE = VaultTask.register(
      Vault.id("exit_death_all_no_save"),
      (vault, player, world) -> new ArrayList<>(vault.players).forEach(vPlayer -> REMOVE_SCAVENGER_ITEMS.then(EXIT_DEATH).execute(vault, vPlayer, world))
   );
   public static final VaultTask VICTORY_SCENE = VaultTask.register(
      Vault.id("victory_scene"),
      (vault, player, world) -> {
         if (player instanceof VaultRunner) {
            player.getTimer().addTime(new WinExtension(player.getTimer(), 400), 0);
            player.runIfPresent(
               world.func_73046_m(),
               playerEntity -> {
                  FireworkRocketEntity fireworks = new FireworkRocketEntity(
                     world,
                     playerEntity.func_226277_ct_(),
                     playerEntity.func_226278_cu_(),
                     playerEntity.func_226281_cx_(),
                     new ItemStack(Items.field_196152_dE)
                  );
                  world.func_217376_c(fireworks);
                  world.func_184148_a(
                     null,
                     playerEntity.func_226277_ct_(),
                     playerEntity.func_226278_cu_(),
                     playerEntity.func_226281_cx_(),
                     SoundEvents.field_194228_if,
                     SoundCategory.MASTER,
                     1.0F,
                     1.0F
                  );
                  StringTextComponent title = new StringTextComponent("Vault Cleared!");
                  title.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
                  StringTextComponent subtitle = new StringTextComponent("You'll be teleported back soon...");
                  subtitle.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
                  STitlePacket titlePacket = new STitlePacket(Type.TITLE, title);
                  STitlePacket subtitlePacket = new STitlePacket(Type.SUBTITLE, subtitle);
                  playerEntity.field_71135_a.func_147359_a(titlePacket);
                  playerEntity.field_71135_a.func_147359_a(subtitlePacket);
               }
            );
         }
      }
   );
   public static final VaultTask ENTER_DISPLAY = VaultTask.register(
      Vault.id("enter_display"),
      (vault, player, world) -> player.runIfPresent(
         world.func_73046_m(),
         playerEntity -> {
            CrystalData data = vault.getProperties().getBaseOrDefault(CRYSTAL_DATA, CrystalData.EMPTY);
            if (!data.getType().isFinalType()) {
               StringTextComponent title = new StringTextComponent("The Vault");
               title.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
               IFormattableTextComponent subtitle = vault.canExit(player)
                  ? new StringTextComponent("Good luck, ").func_230529_a_(playerEntity.func_200200_C_()).func_230529_a_(new StringTextComponent("!"))
                  : new StringTextComponent("No exit this time, ").func_230529_a_(playerEntity.func_200200_C_()).func_230529_a_(new StringTextComponent("!"));
               subtitle.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
               STitlePacket titlePacket = new STitlePacket(Type.TITLE, title);
               STitlePacket subtitlePacket = new STitlePacket(Type.SUBTITLE, subtitle);
               playerEntity.field_71135_a.func_147359_a(titlePacket);
               playerEntity.field_71135_a.func_147359_a(subtitlePacket);
            }

            StringTextComponent text = new StringTextComponent("");
            AtomicBoolean startsWithVowel = new AtomicBoolean(false);
            vault.getModifiers().forEach((i, modifier) -> {
               text.func_230529_a_(modifier.getNameComponent());
               if (i == 0) {
                  char c = modifier.getName().toLowerCase().charAt(0);
                  startsWithVowel.set(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
               }

               if (i != vault.getModifiers().size() - 1) {
                  text.func_230529_a_(new StringTextComponent(", "));
               }
            });
            ITextComponent vaultName = vault.getActiveObjectives()
               .stream()
               .findFirst()
               .map(VaultObjective::getVaultName)
               .orElse(new StringTextComponent("Vault"));
            if (vault.getModifiers().isEmpty()) {
               char c = vaultName.getString().toLowerCase().charAt(0);
               startsWithVowel.set(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
            }

            StringTextComponent prefix = new StringTextComponent(startsWithVowel.get() ? " entered an " : " entered a ");
            switch (data.getType()) {
               case FINAL_LOBBY:
                  prefix = new StringTextComponent(" entered ");
                  vaultName = new StringTextComponent("the Final Vault").func_240699_a_(TextFormatting.DARK_PURPLE);
                  break;
               case FINAL_BOSS:
                  prefix = new StringTextComponent(" is facing ");
                  vaultName = new StringTextComponent("the Final Challenge").func_240699_a_(TextFormatting.DARK_RED);
                  break;
               case FINAL_VELARA:
                  prefix = new StringTextComponent(" entered ");
                  vaultName = new StringTextComponent("Velara's Gluttony").func_240699_a_(PlayerFavourData.VaultGodType.BENEVOLENT.getChatColor());
                  break;
               case FINAL_TENOS:
                  prefix = new StringTextComponent(" entered ");
                  vaultName = new StringTextComponent("Tenos' Puzzle").func_240699_a_(PlayerFavourData.VaultGodType.OMNISCIENT.getChatColor());
                  break;
               case FINAL_WENDARR:
                  prefix = new StringTextComponent(" entered ");
                  vaultName = new StringTextComponent("Wendarr's Passage").func_240699_a_(PlayerFavourData.VaultGodType.TIMEKEEPER.getChatColor());
                  break;
               case FINAL_IDONA:
                  prefix = new StringTextComponent(" entered ");
                  vaultName = new StringTextComponent("Idona's Wrath").func_240699_a_(PlayerFavourData.VaultGodType.MALEVOLENCE.getChatColor());
            }

            if (!vault.getModifiers().isEmpty()) {
               text.func_230529_a_(new StringTextComponent(" "));
            }

            if (vault.getProperties().getBaseOrDefault(COW_VAULT, false) && !vault.getProperties().exists(PARENT)) {
               IFormattableTextComponent txt = new StringTextComponent("Vault that doesn't exist!");
               ITextComponent hoverText = new StringTextComponent(
                  "A vault that doesn't exist.\nThe Vault gods are not responsible for events that transpire here.\n\nThis realm may also harbor additional riches."
               );
               txt.func_230530_a_(
                  Style.field_240709_b_.func_240716_a_(new HoverEvent(Action.field_230550_a_, hoverText)).func_240718_a_(Color.func_240743_a_(9974168))
               );
               text.func_230529_a_(txt);
            } else {
               text.func_230529_a_(vaultName).func_240702_b_("!");
            }

            prefix.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16777215)));
            text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16777215)));
            IFormattableTextComponent playerName = playerEntity.func_145748_c_().func_230532_e_();
            playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
            world.func_73046_m().func_184103_al().func_232641_a_(playerName.func_230529_a_(prefix).func_230529_a_(text), ChatType.CHAT, player.getPlayerId());
         }
      )
   );
   public static final Supplier<SummonAndKillBossObjective> SUMMON_AND_KILL_BOSS = VaultObjective.register(
      () -> new SummonAndKillBossObjective(Vault.id("summon_and_kill_boss"))
   );
   public static final Supplier<ScavengerHuntObjective> SCAVENGER_HUNT = VaultObjective.register(() -> new ScavengerHuntObjective(Vault.id("scavenger_hunt")));
   public static final Supplier<ArchitectObjective> ARCHITECT_EVENT = VaultObjective.register(() -> new ArchitectObjective(Vault.id("architect")));
   public static final Supplier<TroveObjective> VAULT_TROVE = VaultObjective.register(() -> new TroveObjective(Vault.id("trove")));
   public static final Supplier<AncientObjective> ANCIENTS = VaultObjective.register(() -> new AncientObjective(Vault.id("ancients")));
   public static final Supplier<RaidChallengeObjective> RAID_CHALLENGE = VaultObjective.register(() -> new RaidChallengeObjective(Vault.id("raid_challenge")));
   public static final Supplier<CakeHuntObjective> CAKE_HUNT = VaultObjective.register(() -> new CakeHuntObjective(Vault.id("cake_hunt")));
   public static final Supplier<SummonAndKillAllBossesObjective> SUMMON_AND_KILL_ALL_BOSSES = VaultObjective.register(
      () -> new SummonAndKillAllBossesObjective(Vault.id("summon_and_kill_all_bosses"))
   );
   public static final Supplier<ArchitectSummonAndKillBossesObjective> ARCHITECT_KILL_ALL_BOSSES = VaultObjective.register(
      () -> new ArchitectSummonAndKillBossesObjective(Vault.id("architect_kill_all_bosses"))
   );
   public static final Supplier<TreasureHuntObjective> TREASURE_HUNT = VaultObjective.register(() -> new TreasureHuntObjective(Vault.id("treasure_hunt")));
   public static final Supplier<KillTheBossObjective> KILL_THE_BOSS = VaultObjective.register(() -> new KillTheBossObjective(Vault.id("kill_the_boss")));
   @Deprecated
   public static final VaultEvent<Event> TRIGGER_BOSS_SUMMON = VaultEvent.register(Vault.id("trigger_boss_summon"), Event.class, (vault, event) -> {});
   public static final VaultEvent<LivingUpdateEvent> SCALE_MOB = VaultEvent.register(
      Vault.id("scale_mob"), LivingUpdateEvent.class, EntityScaler::scaleVaultEntity
   );
   public static final VaultEvent<EntityJoinWorldEvent> SCALE_MOB_JOIN = VaultEvent.register(
      Vault.id("scale_mob_join"), EntityJoinWorldEvent.class, EntityScaler::scaleVaultEntity
   );
   public static final VaultEvent<CheckSpawn> BLOCK_NATURAL_SPAWNING = VaultEvent.register(
      Vault.id("block_natural_spawning"), CheckSpawn.class, (vault, event) -> {
         if (VaultUtils.inVault(vault, event.getEntity())) {
            event.setResult(Result.DENY);
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> PREVENT_ITEM_PICKUP = VaultEvent.register(
      Vault.id("prevent_item_pickup"), EntityJoinWorldEvent.class, (vault, event) -> {
         if (event.getEntity() instanceof MobEntity) {
            MobEntity me = (MobEntity)event.getEntity();
            if (VaultUtils.inVault(vault, event.getEntity())) {
               me.func_98053_h(false);
            }
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> REPLACE_WITH_COW = VaultEvent.register(
      Vault.id("replace_with_cow"), EntityJoinWorldEvent.class, (vault, event) -> {
         if (event.getWorld() instanceof ServerWorld) {
            Entity entity = event.getEntity();
            if (!entity.func_184216_O().contains("replaced_entity")) {
               if (VaultUtils.inVault(vault, event.getEntity())) {
                  if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
                     LivingEntity replaced = VaultCowOverrides.replaceVaultEntity(vault, (LivingEntity)entity, (ServerWorld)event.getWorld());
                     if (replaced != null) {
                        Vector3d pos = entity.func_213303_ch();
                        replaced.func_70080_a(pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, entity.field_70177_z, entity.field_70125_A);
                        ServerScheduler.INSTANCE.schedule(1, () -> event.getWorld().func_217376_c(replaced));
                        event.setCanceled(true);
                     }
                  }
               }
            }
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> APPLY_SCALE_MODIFIER = VaultEvent.register(
      Vault.id("apply_scale_modifier"),
      EntityJoinWorldEvent.class,
      (vault, event) -> {
         if (event.getWorld() instanceof ServerWorld) {
            if (VaultUtils.inVault(vault, event.getEntity())) {
               if (event.getEntity() instanceof LivingEntity) {
                  if (!(event.getEntity() instanceof PlayerEntity)) {
                     if (!(event.getEntity() instanceof EternalEntity)) {
                        LivingEntity entity = (LivingEntity)event.getEntity();
                        vault.getActiveModifiersFor(PlayerFilter.any(), ScaleModifier.class)
                           .forEach(modifier -> entity.func_110148_a(ModAttributes.SIZE_SCALE).func_111128_a(modifier.getScale()));
                     }
                  }
               }
            }
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> APPLY_FRENZY_MODIFIERS = VaultEvent.register(
      Vault.id("frenzy_modifiers"), EntityJoinWorldEvent.class, (vault, event) -> {
         if (event.getWorld() instanceof ServerWorld) {
            if (VaultUtils.inVault(vault, event.getEntity())) {
               if (event.getEntity() instanceof LivingEntity) {
                  if (!(event.getEntity() instanceof PlayerEntity)) {
                     if (!(event.getEntity() instanceof EternalEntity)) {
                        if (!event.getEntity().func_184216_O().contains("vault_boss")) {
                           LivingEntity entity = (LivingEntity)event.getEntity();
                           if (!entity.func_184216_O().contains("frenzy_scaled")) {
                              vault.getActiveModifiersFor(PlayerFilter.any(), FrenzyModifier.class).forEach(modifier -> modifier.applyToEntity(entity));
                              entity.func_184216_O().add("frenzy_scaled");
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   );
   public static final VaultEvent<EntityJoinWorldEvent> APPLY_INFLUENCE_MODIFIERS = VaultEvent.register(
      Vault.id("influence_modifiers"), EntityJoinWorldEvent.class, (vault, event) -> {
         if (event.getWorld() instanceof ServerWorld) {
            if (VaultUtils.inVault(vault, event.getEntity())) {
               if (event.getEntity() instanceof LivingEntity) {
                  if (!(event.getEntity() instanceof PlayerEntity)) {
                     if (!(event.getEntity() instanceof EternalEntity)) {
                        LivingEntity entity = (LivingEntity)event.getEntity();
                        if (!entity.func_184216_O().contains("influenced")) {
                           vault.getInfluences().getInfluences(MobAttributeInfluence.class).forEach(influence -> influence.applyTo(entity));
                           entity.func_184216_O().add("influenced");
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

   public <T extends VaultModifier> List<T> getActiveModifiersFor(PlayerFilter filter, Class<T> modifierClass) {
      List<T> modifiers = this.getModifiers()
         .stream()
         .filter(modifier -> modifierClass.isAssignableFrom(modifier.getClass()))
         .map(modifier -> (VaultModifier)modifier)
         .collect(Collectors.toList());

      for (VaultPlayer player : this.getPlayers()) {
         if (!(player instanceof VaultRunner) && filter.test(player.getPlayerId())) {
            player.getModifiers()
               .stream()
               .filter(modifier -> modifierClass.isAssignableFrom(modifier.getClass()))
               .map(modifier -> (VaultModifier)modifier)
               .forEach(modifiers::add);
         }
      }

      return modifiers;
   }

   public boolean canExit(VaultPlayer player) {
      return this.getActiveModifiersFor(PlayerFilter.of(player), NoExitModifier.class).isEmpty();
   }

   public boolean triggerRaid(ServerWorld world, BlockPos controller) {
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

   public <T extends VaultObjective> Optional<T> getObjective(Class<T> objectiveClass) {
      return this.getAllObjectives()
         .stream()
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

   public Optional<VaultPlayer> getPlayer(PlayerEntity player) {
      return this.getPlayer(player.func_110124_au());
   }

   public Optional<VaultPlayer> getPlayer(UUID playerId) {
      return this.players.stream().filter(player -> player.getPlayerId().equals(playerId)).findFirst();
   }

   public void tick(ServerWorld world) {
      this.getGenerator().tick(world, this);
      if (!this.isFinished()) {
         MinecraftServer srv = world.func_73046_m();
         if (this.getActiveObjectives().stream().noneMatch(objective -> objective.shouldPauseTimer(srv, this))) {
            this.getTimer().tick();
         }

         this.getModifiers().tick(this, world, PlayerFilter.any());
         new ArrayList<>(this.players).forEach(player -> {
            player.tick(this, world);
            player.sendIfPresent(world.func_73046_m(), new VaultModifierMessage(this, player));
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

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_218657_a("Timer", this.timer.serializeNBT());
      nbt.func_218657_a("Generator", this.generator.serializeNBT());
      nbt.func_218657_a("Modifiers", this.modifiers.serializeNBT());
      nbt.func_218657_a("influence", this.influence.serializeNBT());
      nbt.func_218657_a("Properties", this.properties.serializeNBT());
      nbt.func_218657_a("Objectives", this.objectives.serializeNBT());
      nbt.func_218657_a("Events", this.events.serializeNBT());
      nbt.func_218657_a("Players", this.players.serializeNBT());
      nbt.func_74772_a("CreationTime", this.getCreationTime());
      NBTHelper.writeOptional(nbt, "activeRaid", this.activeRaid, (tag, raid) -> raid.serialize(tag));
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.timer.deserializeNBT(nbt.func_74775_l("Timer"));
      this.generator = VaultGenerator.fromNBT(nbt.func_74775_l("Generator"));
      this.modifiers.deserializeNBT(nbt.func_74775_l("Modifiers"));
      this.influence.deserializeNBT(nbt.func_74775_l("influence"));
      this.properties.deserializeNBT(nbt.func_74775_l("Properties"));
      this.objectives.deserializeNBT(nbt.func_150295_c("Objectives", 10));
      this.events.deserializeNBT(nbt.func_150295_c("Events", 10));
      this.players.deserializeNBT(nbt.func_150295_c("Players", 10));
      this.creationTime = nbt.func_74763_f("CreationTime");
      this.activeRaid = NBTHelper.readOptional(nbt, "activeRaid", ActiveRaid::deserializeNBT);
   }

   public static VaultRaid classic(
      VaultGenerator generator,
      VaultTask initializer,
      RaidProperties properties,
      VaultObjective objective,
      List<VaultEvent<?>> events,
      Map<VaultPlayerType, Set<ServerPlayerEntity>> playersMap
   ) {
      MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
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
                  Set<ServerPlayerEntity> players = entry.getValue();
                  if (type == VaultPlayerType.RUNNER) {
                     return players.stream()
                        .map(
                           player -> {
                              VaultRunner runner = new VaultRunner(player.func_110124_au());
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
      Map<VaultPlayerType, Set<ServerPlayerEntity>> playersMap
   ) {
      MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
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
                  Set<ServerPlayerEntity> players = entry.getValue();
                  if (type == VaultPlayerType.RUNNER) {
                     return players.stream()
                        .map(
                           player -> {
                              VaultRunner runner = new VaultRunner(player.func_110124_au());
                              runner.getBehaviours()
                                 .add(
                                    new VaultBehaviour(
                                       NO_OBJECTIVES_LEFT_GLOBALLY.and(NO_TIME_LEFT),
                                       REMOVE_SCAVENGER_ITEMS.then(REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(GRANT_EXP_COMPLETE.then(EXIT_SAFELY))
                                    )
                                 );
                              runner.getBehaviours().add(new VaultBehaviour(IS_RUNNER.and(IS_DEAD.or(NO_TIME_LEFT)), EXIT_DEATH_ALL));
                              runner.getBehaviours().add(new VaultBehaviour(IS_FINISHED.negate(), TICK_SPAWNER.then(TICK_CHEST_PITY)));
                              runner.getBehaviours().add(new VaultBehaviour(AFTER_GRACE_PERIOD.and(IS_FINISHED.negate()), TICK_INFLUENCES));
                              runner.getBehaviours().add(new VaultBehaviour(IS_RUNNER, CHECK_BAIL_COOP));
                              runner.getBehaviours()
                                 .add(
                                    new VaultBehaviour(
                                       NO_ACTIVE_RUNNERS_LEFT,
                                       REMOVE_SCAVENGER_ITEMS.then(REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(GRANT_EXP_BAIL.then(EXIT_SAFELY))
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
      Map<VaultPlayerType, Set<ServerPlayerEntity>> playersMap
   ) {
      MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
      VaultRaid vault = new VaultRaid(generator, initializer, properties, events, playersMap.entrySet().stream().flatMap(entry -> {
         Set<ServerPlayerEntity> players = entry.getValue();
         return players.stream().map(player -> {
            VaultMember member = new VaultMember(player.func_110124_au());
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

   public static VaultRaid boss(
      VaultGenerator generator,
      VaultTask initializer,
      RaidProperties properties,
      VaultObjective objective,
      List<VaultEvent<?>> events,
      Map<VaultPlayerType, Set<ServerPlayerEntity>> playersMap
   ) {
      MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
      VaultRaid vault = new VaultRaid(generator, initializer, properties, events, playersMap.entrySet().stream().flatMap(entry -> {
         Set<ServerPlayerEntity> players = entry.getValue();
         return players.stream().map(player -> {
            VaultRunner runner = new VaultRunner(player.func_110124_au());
            runner.getBehaviours().add(new VaultBehaviour(IS_OUTSIDE, TP_TO_START));
            runner.getBehaviours().add(new VaultBehaviour(IS_DEAD.and(IS_RUNNER), RUNNER_TO_SPECTATOR));
            runner.getBehaviours().add(new VaultBehaviour(NO_RUNNERS_LEFT, EXIT_DEATH));
            runner.getBehaviours().add(new VaultBehaviour(NO_OBJECTIVES_LEFT_GLOBALLY, EXIT_SAFELY));
            runner.getProperties().create(SPAWNER, new VaultSpawner());
            runner.getProperties().create(SHOW_TIMER, false);
            runner.getTimer().start(30000);
            return runner;
         });
      }).collect(Collectors.toList()));
      vault.getAllObjectives().add(objective);
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
      TimeExtension.REGISTRY.put(RelicSetExtension.ID, RelicSetExtension::new);
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
      VaultPiece.REGISTRY.put(VaultGodEye.ID, VaultGodEye::new);
      VaultPiece.REGISTRY.put(FinalVaultBoss.ID, FinalVaultBoss::new);
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
      private final Map<VaultPlayerType, Set<ServerPlayerEntity>> players = new HashMap<>();

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

      public VaultRaid.Builder addPlayer(VaultPlayerType type, ServerPlayerEntity player) {
         return this.addPlayers(type, Stream.of(player));
      }

      public VaultRaid.Builder addPlayers(VaultPlayerType type, Collection<ServerPlayerEntity> player) {
         return this.addPlayers(type, player.stream());
      }

      public VaultRaid.Builder addPlayers(VaultPlayerType type, Stream<ServerPlayerEntity> player) {
         Set<ServerPlayerEntity> players = this.players.computeIfAbsent(type, key -> new HashSet<>());
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
         VaultGenerator var1,
         VaultTask var2,
         RaidProperties var3,
         VaultObjective var4,
         List<VaultEvent<?>> var5,
         Map<VaultPlayerType, Set<ServerPlayerEntity>> var6
      );
   }
}
