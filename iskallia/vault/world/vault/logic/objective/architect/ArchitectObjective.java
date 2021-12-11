package iskallia.vault.world.vault.logic.objective.architect;

import com.google.common.collect.Iterables;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.LootTablesConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.gen.decorator.BreadcrumbFeature;
import iskallia.vault.world.gen.structure.VaultJigsawHelper;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.VaultGenerator;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.logic.VaultBossSpawner;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.architect.modifier.RandomVoteModifier;
import iskallia.vault.world.vault.logic.objective.architect.modifier.VoteModifier;
import iskallia.vault.world.vault.logic.objective.architect.processor.VaultPieceProcessor;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.STitlePacket.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ArchitectObjective extends VaultObjective {
   private final List<VotingSession> completedSessions = new ArrayList<>();
   private VotingSession activeSession = null;
   private boolean votingLocked = false;
   private int totalRequiredVotes;
   private int voteDowntimeTicks = 400;
   private int ticksUntilNextVote = 0;
   private UUID bossId = null;
   private boolean isBossDead = false;
   private final VListNBT<BlockPos, CompoundNBT> exitPortalLocations = VListNBT.ofCodec(BlockPos.field_239578_a_, BlockPos.field_177992_a);
   private boolean collidedWithExitPortal = false;

   public ArchitectObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
      this.totalRequiredVotes = ModConfigs.ARCHITECT_EVENT.getRandomTotalRequiredPolls();
   }

   public boolean createVotingSession(VaultRaid vault, ServerWorld world, BlockPos origin) {
      if (this.activeSession == null && this.ticksUntilNextVote <= 0 && !this.isVotingLocked()) {
         VaultRoom room = (VaultRoom)Iterables.getFirst(vault.getGenerator().getPiecesAt(origin, VaultRoom.class), null);
         if (room == null) {
            return false;
         } else {
            List<Direction> availableDirections = new ArrayList<>();

            for (Direction dir : Direction.values()) {
               if (dir.func_176740_k().func_176722_c() && VaultJigsawHelper.canExpand(vault, room, dir)) {
                  availableDirections.add(dir);
               }
            }

            if (availableDirections.size() <= 1) {
               return false;
            } else {
               Direction bossDir = null;
               if (this.completedSessions.size() >= this.totalRequiredVotes) {
                  bossDir = MiscUtils.getRandomEntry(availableDirections, rand);
               }

               List<DirectionChoice> choices = new ArrayList<>();

               for (Direction dirx : availableDirections) {
                  DirectionChoice choice = new DirectionChoice(dirx);
                  if (dirx == bossDir) {
                     choice.addModifier(ModConfigs.ARCHITECT_EVENT.getBossModifier());
                  } else {
                     VoteModifier randomModifier = ModConfigs.ARCHITECT_EVENT.generateRandomModifier();
                     if (randomModifier != null) {
                        choice.addModifier(randomModifier);
                     }
                  }

                  choices.add(choice);
               }

               this.activeSession = new VotingSession(origin, choices);
               if (this.completedSessions.isEmpty()) {
                  IFormattableTextComponent display = new StringTextComponent("").func_240702_b_("Vote with ");
                  List<DirectionChoice> directions = this.activeSession.getDirections();

                  for (int i = 0; i < directions.size(); i++) {
                     if (i != 0) {
                        display.func_240702_b_(", ");
                     }

                     DirectionChoice choice = directions.get(i);
                     display.func_230529_a_(choice.getDirectionDisplay("/"));
                  }

                  display.func_240702_b_("!");
                  vault.getPlayers()
                     .forEach(vPlayer -> vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> sPlayer.func_145747_a(display, Util.field_240973_b_)));
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   @Nullable
   public VotingSession getActiveSession() {
      return this.activeSession;
   }

   public boolean handleVote(String sender, Direction dir) {
      return this.activeSession == null ? false : this.activeSession.acceptVote(sender, dir);
   }

   @Override
   public boolean shouldPauseTimer(MinecraftServer srv, VaultRaid vault) {
      return super.shouldPauseTimer(srv, vault) || this.activeSession == null && this.completedSessions.isEmpty();
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.func_73046_m();
      vault.getPlayers()
         .stream()
         .filter(vPlayer -> filter.test(vPlayer.getPlayerId()))
         .forEach(
            vPlayer -> vPlayer.runIfPresent(
               srv,
               playerEntity -> {
                  VaultGoalMessage pkt = VaultGoalMessage.architectEvent(
                     this.getCompletedPercent(), this.ticksUntilNextVote, this.voteDowntimeTicks, this.activeSession
                  );
                  ModNetwork.CHANNEL.sendTo(pkt, playerEntity.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
               }
            )
         );
      if (!this.isCompleted()) {
         if (this.ticksUntilNextVote > 0) {
            this.ticksUntilNextVote--;
         }

         if (this.activeSession != null) {
            this.activeSession.tick(world);
            if (this.activeSession.isFinished()) {
               this.finishVote(vault, this.activeSession, world);
               this.completedSessions.add(this.activeSession);
               this.activeSession = null;
               if (!this.isVotingLocked()) {
                  this.ticksUntilNextVote = this.voteDowntimeTicks;
               }
            }
         }

         if (!this.exitPortalLocations.isEmpty()) {
            vault.getPlayers().stream().filter(vPlayer -> filter.test(vPlayer.getPlayerId())).forEach(vPlayer -> vPlayer.runIfPresent(srv, sPlayer -> {
               BlockPos pos = sPlayer.func_233580_cy_();
               if (this.exitPortalLocations.contains(pos)) {
                  this.collidedWithExitPortal = true;
                  this.spawnBossLoot(vault, pos, vPlayer, world, false);
               }
            }));
         }

         if (this.bossId != null && this.isBossDead) {
            this.setCompleted();
         }

         if (!this.exitPortalLocations.isEmpty() && this.collidedWithExitPortal) {
            this.setCompleted();
         }
      }
   }

   private void finishVote(VaultRaid vault, VotingSession session, ServerWorld world) {
      vault.getGenerator()
         .getPiecesAt(session.getStabilizerPos(), VaultRoom.class)
         .stream()
         .findFirst()
         .ifPresent(
            room -> {
               DirectionChoice choice = session.getVotedDirection();
               List<VoteModifier> modifiers = new ArrayList<>();
               choice.getModifiers().forEach(modifier -> {
                  if (modifier instanceof RandomVoteModifier) {
                     modifiers.add(((RandomVoteModifier)modifier).rollModifier());
                  } else {
                     modifiers.add(modifier);
                  }
               });
               JigsawPiece roomPiece = modifiers.stream()
                  .map(modifier -> modifier.getSpecialRoom(this, vault))
                  .filter(Objects::nonNull)
                  .findFirst()
                  .orElse(null);
               List<VaultPiece> generatedPieces = VaultJigsawHelper.expandVault(vault, world, room, choice.getDirection(), roomPiece);
               this.generateBreadcrumb(vault, world, generatedPieces);
               List<VaultPieceProcessor> postProcessors = modifiers.stream()
                  .map(modifier -> modifier.getPostProcessor(this, vault))
                  .filter(Objects::nonNull)
                  .collect(Collectors.toList());
               generatedPieces.forEach(piece -> postProcessors.forEach(processor -> processor.postProcess(vault, world, piece, choice.getDirection())));
               modifiers.forEach(modifier -> modifier.onApply(this, vault, world));
               choice.getModifiers().forEach(modifier -> this.voteDowntimeTicks = this.voteDowntimeTicks + modifier.getVoteLockDurationChangeSeconds() * 20);
               this.voteDowntimeTicks = Math.max(0, this.voteDowntimeTicks);
               STitlePacket titlePacket = new STitlePacket(Type.TITLE, choice.getDirectionDisplay());
               VoteModifier displayModifier = (VoteModifier)Iterables.getFirst(modifiers, null);
               STitlePacket subtitlePacket;
               if (displayModifier != null) {
                  subtitlePacket = new STitlePacket(Type.SUBTITLE, displayModifier.getDescription());
               } else {
                  subtitlePacket = null;
               }

               vault.getPlayers().forEach(vPlayer -> vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> {
                  sPlayer.field_71135_a.func_147359_a(titlePacket);
                  if (subtitlePacket != null) {
                     sPlayer.field_71135_a.func_147359_a(subtitlePacket);
                  }
               }));
            }
         );
   }

   private void generateBreadcrumb(VaultRaid vault, ServerWorld sWorld, List<VaultPiece> pieces) {
      Predicate<BlockPos> filter = posx -> false;
      Set<ChunkPos> chunks = new HashSet<>();

      for (VaultPiece piece : pieces) {
         MutableBoundingBox box = piece.getBoundingBox();
         filter = filter.or(box::func_175898_b);
         ChunkPos chMin = new ChunkPos(box.field_78897_a >> 4, box.field_78896_c >> 4);
         ChunkPos chMax = new ChunkPos(box.field_78893_d >> 4, box.field_78892_f >> 4);

         for (int x = chMin.field_77276_a; x <= chMax.field_77276_a; x++) {
            for (int z = chMin.field_77275_b; z <= chMax.field_77275_b; z++) {
               chunks.add(new ChunkPos(x, z));
            }
         }
      }

      Predicate<BlockPos> featurePlacementFilter = filter;

      for (ChunkPos pos : chunks) {
         BlockPos featurePos = pos.func_206849_h();
         BreadcrumbFeature.placeBreadcrumbFeatures(
            vault, sWorld, (at, state) -> featurePlacementFilter.test(at) ? sWorld.func_180501_a(at, state, 2) : false, rand, featurePos
         );
      }
   }

   public void buildPortal(List<BlockPos> portalLocations) {
      this.exitPortalLocations.addAll(portalLocations);
   }

   public void spawnBoss(VaultRaid vault, ServerWorld world, BlockPos pos) {
      LivingEntity boss = VaultBossSpawner.spawnBoss(vault, world, pos);
      this.bossId = boss.func_110124_au();
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onBossDeath(LivingDeathEvent event) {
      LivingEntity died = event.getEntityLiving();
      if (!died.func_130014_f_().func_201670_d() && died.func_130014_f_() instanceof ServerWorld) {
         ServerWorld world = (ServerWorld)died.func_130014_f_();
         VaultRaid vault = VaultRaidData.get(world).getAt(world, died.func_233580_cy_());
         if (vault != null) {
            List<ArchitectObjective> matchingObjectives = vault.getPlayers()
               .stream()
               .map(player -> player.getActiveObjective(ArchitectObjective.class))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .filter(objective -> !objective.isCompleted())
               .filter(objective -> died.func_110124_au().equals(objective.bossId))
               .collect(Collectors.toList());
            if (matchingObjectives.isEmpty()) {
               vault.getActiveObjective(ArchitectObjective.class).ifPresent(objective -> {
                  if (objective.onBossKill(died)) {
                     objective.dropBossCrate(died, event.getSource(), world, vault);
                  }
               });
            } else {
               matchingObjectives.forEach(objective -> objective.onBossKill(died));
            }
         }
      }
   }

   private boolean onBossKill(LivingEntity boss) {
      if (!this.isBossDead && boss.func_110124_au().equals(this.bossId)) {
         this.isBossDead = true;
         return true;
      } else {
         return false;
      }
   }

   private void dropBossCrate(LivingEntity boss, DamageSource killSrc, ServerWorld world, VaultRaid vault) {
      Optional<UUID> source = Optional.ofNullable(killSrc.func_76346_g()).map(Entity::func_110124_au);
      Optional<VaultPlayer> killer = source.flatMap(vault::getPlayer);
      Optional<VaultPlayer> host = vault.getProperties().getBase(VaultRaid.HOST).flatMap(vault::getPlayer);
      if (killer.isPresent()) {
         this.spawnBossLoot(vault, boss.func_233580_cy_(), killer.get(), world, true);
      } else if (host.isPresent() && host.get() instanceof VaultRunner) {
         this.spawnBossLoot(vault, boss.func_233580_cy_(), host.get(), world, true);
      } else {
         vault.getPlayers()
            .stream()
            .filter(player -> player instanceof VaultRunner)
            .findFirst()
            .ifPresent(player -> this.spawnBossLoot(vault, boss.func_233580_cy_(), player, world, true));
      }
   }

   public void spawnBossLoot(VaultRaid vault, BlockPos bossPos, VaultPlayer player, ServerWorld world, boolean isBossKill) {
      player.runIfPresent(
         world.func_73046_m(),
         playerEntity -> {
            Builder builder = new Builder(world)
               .func_216023_a(world.field_73012_v)
               .func_216015_a(LootParameters.field_216281_a, playerEntity)
               .func_216015_a(LootParameters.field_237457_g_, Vector3d.func_237489_a_(bossPos))
               .func_216015_a(LootParameters.field_216283_c, DamageSource.func_76365_a(playerEntity))
               .func_216021_b(LootParameters.field_216284_d, playerEntity)
               .func_216021_b(LootParameters.field_216285_e, playerEntity)
               .func_216015_a(LootParameters.field_216282_b, playerEntity)
               .func_186469_a(playerEntity.func_184817_da());
            LootContext ctx = builder.func_216022_a(LootParameterSets.field_216263_d);
            this.spawnRewardCrate(world, bossPos, vault, ctx);

            for (int i = 1; i < vault.getPlayers().size(); i++) {
               if (rand.nextFloat() < 0.5F) {
                  this.spawnRewardCrate(world, bossPos, vault, ctx);
               }
            }

            MiscUtils.broadcast(isBossKill ? this.getBossKillMessage(playerEntity) : this.getEscapeMessage(playerEntity));
            vault.getPlayers()
               .forEach(anyVPlayer -> anyVPlayer.runIfPresent(world.func_73046_m(), anySPlayer -> MiscUtils.broadcast(this.getCompletionMessage(anySPlayer))));
         }
      );
   }

   @Override
   protected void addSpecialLoot(ServerWorld world, VaultRaid vault, LootContext context, NonNullList<ItemStack> stacks) {
      super.addSpecialLoot(world, vault, context, stacks);
      if (ModConfigs.ARCHITECT_EVENT.isEnabled()) {
         stacks.add(new ItemStack(ModItems.VAULT_GEAR));
      }
   }

   private ITextComponent getBossKillMessage(PlayerEntity player) {
      IFormattableTextComponent msgContainer = new StringTextComponent("").func_240699_a_(TextFormatting.WHITE);
      IFormattableTextComponent playerName = player.func_145748_c_().func_230532_e_();
      playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
      return msgContainer.func_230529_a_(playerName).func_240702_b_(" defeated Boss!");
   }

   private ITextComponent getEscapeMessage(PlayerEntity player) {
      IFormattableTextComponent msgContainer = new StringTextComponent("").func_240699_a_(TextFormatting.WHITE);
      IFormattableTextComponent playerName = player.func_145748_c_().func_230532_e_();
      playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
      return msgContainer.func_230529_a_(playerName).func_240702_b_(" successfully escaped from the Vault!");
   }

   private ITextComponent getCompletionMessage(PlayerEntity player) {
      IFormattableTextComponent msgContainer = new StringTextComponent("").func_240699_a_(TextFormatting.WHITE);
      IFormattableTextComponent playerName = player.func_145748_c_().func_230532_e_();
      playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
      return msgContainer.func_230529_a_(playerName).func_240702_b_(" finished building a Vault!");
   }

   private void spawnRewardCrate(ServerWorld world, Vector3i pos, VaultRaid vault, LootContext context) {
      NonNullList<ItemStack> stacks = this.createLoot(world, vault, context);
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(ModBlocks.VAULT_CRATE, stacks);
      ItemEntity item = new ItemEntity(world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), crate);
      item.func_174869_p();
      world.func_217376_c(item);
      this.crates.add(new VaultObjective.Crate(stacks));
   }

   public int getTicksUntilNextVote() {
      return this.ticksUntilNextVote;
   }

   public float getCompletedPercent() {
      return MathHelper.func_76131_a((float)this.completedSessions.size() / this.totalRequiredVotes, 0.0F, 1.0F);
   }

   public void setVotingLocked() {
      this.votingLocked = true;
   }

   public boolean isVotingLocked() {
      return this.votingLocked;
   }

   @Override
   public void setObjectiveTargetCount(int amount) {
      this.totalRequiredVotes = amount;
   }

   @Nullable
   @Override
   public ITextComponent getObjectiveTargetDescription(int amount) {
      return new StringTextComponent("Required amount of votes: ")
         .func_230529_a_(new StringTextComponent(String.valueOf(amount)).func_240699_a_(TextFormatting.AQUA));
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock() {
      return ModBlocks.STABILIZER.func_176223_P();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      return config != null ? tblResolver.apply(config.getBossCrate()) : LootTable.field_186464_a;
   }

   @Override
   public ITextComponent getObjectiveDisplayName() {
      return new StringTextComponent("Build a Vault").func_240699_a_(TextFormatting.AQUA);
   }

   @Override
   public ITextComponent getVaultName() {
      return new StringTextComponent("Architect Vault");
   }

   @Nonnull
   @Override
   public Supplier<? extends VaultGenerator> getVaultGenerator() {
      return VaultRaid.ARCHITECT_GENERATOR;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      if (this.activeSession != null) {
         tag.func_218657_a("activeSession", this.activeSession.serialize());
      }

      ListNBT sessions = new ListNBT();

      for (VotingSession session : this.completedSessions) {
         sessions.add(session.serialize());
      }

      tag.func_218657_a("completedSessions", sessions);
      tag.func_74768_a("totalRequiredVotes", this.totalRequiredVotes);
      tag.func_74768_a("voteDowntimeTicks", this.voteDowntimeTicks);
      tag.func_74768_a("ticksUntilNextVote", this.ticksUntilNextVote);
      tag.func_74757_a("votingLocked", this.votingLocked);
      NBTHelper.writeOptional(tag, "bossId", this.bossId, (nbt, uuid) -> nbt.func_186854_a("bossId", uuid));
      tag.func_74757_a("isBossDead", this.isBossDead);
      tag.func_218657_a("exitPortalLocations", this.exitPortalLocations.serializeNBT());
      tag.func_74757_a("collidedWithExitPortal", this.collidedWithExitPortal);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      if (tag.func_150297_b("activeSession", 10)) {
         this.activeSession = new VotingSession(tag.func_74775_l("activeSession"));
      } else {
         this.activeSession = null;
      }

      this.completedSessions.clear();
      ListNBT sessions = tag.func_150295_c("completedSessions", 10);

      for (int i = 0; i < sessions.size(); i++) {
         this.completedSessions.add(new VotingSession(sessions.func_150305_b(i)));
      }

      this.totalRequiredVotes = tag.func_74762_e("totalRequiredVotes");
      this.voteDowntimeTicks = tag.func_74762_e("voteDowntimeTicks");
      this.ticksUntilNextVote = tag.func_74762_e("ticksUntilNextVote");
      this.votingLocked = tag.func_74767_n("votingLocked");
      this.bossId = NBTHelper.readOptional(tag, "bossId", nbt -> nbt.func_186857_a("bossId"));
      this.isBossDead = tag.func_74767_n("isBossDead");
      this.exitPortalLocations.deserializeNBT(tag.func_150295_c("exitPortalLocations", 10));
      this.collidedWithExitPortal = tag.func_74767_n("collidedWithExitPortal");
   }
}
