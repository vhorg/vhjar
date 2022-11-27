package iskallia.vault.world.vault.logic.objective.architect;

import com.google.common.collect.Iterables;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.util.nbt.NBTHelper;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

public class ArchitectObjective extends VaultObjective {
   private final List<VotingSession> completedSessions = new ArrayList<>();
   private VotingSession activeSession = null;
   private boolean votingLocked = false;
   private int totalRequiredVotes;
   private int voteDowntimeTicks = 400;
   private int ticksUntilNextVote = 0;
   private UUID bossId = null;
   private boolean isBossDead = false;
   private final VListNBT<BlockPos, CompoundTag> exitPortalLocations = VListNBT.ofCodec(BlockPos.CODEC, BlockPos.ZERO);
   private boolean collidedWithExitPortal = false;

   public ArchitectObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
      this.totalRequiredVotes = ModConfigs.ARCHITECT_EVENT.getRandomTotalRequiredPolls();
   }

   public boolean createVotingSession(ServerLevel world, BlockPos origin) {
      if (this.activeSession == null && this.ticksUntilNextVote <= 0 && !this.isVotingLocked()) {
         VaultRaid thisRaid = null;
         return false;
      } else {
         return false;
      }
   }

   @Nullable
   public VotingSession getActiveSession() {
      return this.activeSession;
   }

   public void handleVote(String sender, Direction dir) {
      if (this.activeSession != null) {
         this.activeSession.acceptVote(sender, dir);
      }
   }

   @Override
   public boolean shouldPauseTimer(MinecraftServer srv, VaultRaid vault) {
      return super.shouldPauseTimer(srv, vault) || this.activeSession == null && this.completedSessions.isEmpty();
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerLevel world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.getServer();
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
                  ModNetwork.CHANNEL.sendTo(pkt, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
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
               BlockPos pos = sPlayer.blockPosition();
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

   private void finishVote(VaultRaid vault, VotingSession session, ServerLevel world) {
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
               StructurePoolElement roomPiece = modifiers.stream()
                  .map(modifier -> modifier.getSpecialRoom(this, vault))
                  .filter(Objects::nonNull)
                  .findFirst()
                  .orElse(null);
               List<VaultPiece> generatedPieces = VaultJigsawHelper.expandVault(vault, world, room, choice.getDirection(), roomPiece);
               BreadcrumbFeature.generateVaultBreadcrumb(vault, world, generatedPieces);
               List<VaultPieceProcessor> postProcessors = modifiers.stream()
                  .map(modifier -> modifier.getPostProcessor(this, vault))
                  .filter(Objects::nonNull)
                  .collect(Collectors.toList());
               generatedPieces.forEach(piece -> postProcessors.forEach(processor -> processor.postProcess(vault, world, piece, choice.getDirection())));
               modifiers.forEach(modifier -> modifier.onApply(this, vault, world));
               choice.getModifiers().forEach(modifier -> this.voteDowntimeTicks = this.voteDowntimeTicks + modifier.getVoteLockDurationChangeSeconds() * 20);
               this.voteDowntimeTicks = Math.max(0, this.voteDowntimeTicks);
               ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(choice.getDirectionDisplay());
               VoteModifier displayModifier = (VoteModifier)Iterables.getFirst(modifiers, null);
               ClientboundSetSubtitleTextPacket subtitlePacket;
               if (displayModifier != null) {
                  subtitlePacket = new ClientboundSetSubtitleTextPacket(displayModifier.getDescription());
               } else {
                  subtitlePacket = null;
               }

               vault.getPlayers().forEach(vPlayer -> vPlayer.runIfPresent(world.getServer(), sPlayer -> {
                  sPlayer.connection.send(titlePacket);
                  if (subtitlePacket != null) {
                     sPlayer.connection.send(subtitlePacket);
                  }
               }));
            }
         );
   }

   public void buildPortal(List<BlockPos> portalLocations) {
      this.exitPortalLocations.addAll(portalLocations);
   }

   public void spawnBoss(VaultRaid vault, ServerLevel world, BlockPos pos) {
      LivingEntity boss = VaultBossSpawner.spawnBossLegacy(vault, world, pos);
      this.bossId = boss.getUUID();
   }

   private boolean onBossKill(LivingEntity boss) {
      if (!this.isBossDead && boss.getUUID().equals(this.bossId)) {
         this.isBossDead = true;
         return true;
      } else {
         return false;
      }
   }

   private void dropBossCrate(LivingEntity boss, DamageSource killSrc, ServerLevel world, VaultRaid vault) {
      Optional<UUID> source = Optional.ofNullable(killSrc.getEntity()).map(Entity::getUUID);
      Optional<VaultPlayer> killer = source.flatMap(vault::getPlayer);
      Optional<VaultPlayer> host = vault.getProperties().getBase(VaultRaid.HOST).flatMap(vault::getPlayer);
      if (killer.isPresent()) {
         this.spawnBossLoot(vault, boss.blockPosition(), killer.get(), world, true);
      } else if (host.isPresent() && host.get() instanceof VaultRunner) {
         this.spawnBossLoot(vault, boss.blockPosition(), host.get(), world, true);
      } else {
         vault.getPlayers()
            .stream()
            .filter(player -> player instanceof VaultRunner)
            .findFirst()
            .ifPresent(player -> this.spawnBossLoot(vault, boss.blockPosition(), player, world, true));
      }
   }

   public void spawnBossLoot(VaultRaid vault, BlockPos bossPos, VaultPlayer player, ServerLevel world, boolean isBossKill) {
      player.runIfPresent(
         world.getServer(),
         playerEntity -> {
            Builder builder = new Builder(world)
               .withRandom(world.random)
               .withParameter(LootContextParams.THIS_ENTITY, playerEntity)
               .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(bossPos))
               .withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.playerAttack(playerEntity))
               .withOptionalParameter(LootContextParams.KILLER_ENTITY, playerEntity)
               .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, playerEntity)
               .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, playerEntity)
               .withLuck(playerEntity.getLuck());
            LootContext ctx = builder.create(LootContextParamSets.ENTITY);
            this.spawnRewardCrate(world, bossPos, vault, ctx);

            for (int i = 1; i < vault.getPlayers().size(); i++) {
               if (rand.nextFloat() < 0.5F) {
                  this.spawnRewardCrate(world, bossPos, vault, ctx);
               }
            }

            MiscUtils.broadcast(isBossKill ? this.getBossKillMessage(playerEntity) : this.getEscapeMessage(playerEntity));
            vault.getPlayers()
               .forEach(anyVPlayer -> anyVPlayer.runIfPresent(world.getServer(), anySPlayer -> MiscUtils.broadcast(this.getCompletionMessage(anySPlayer))));
         }
      );
   }

   @Override
   protected void addSpecialLoot(ServerLevel world, VaultRaid vault, LootContext context, NonNullList<ItemStack> stacks) {
      super.addSpecialLoot(world, vault, context, stacks);
   }

   private Component getBossKillMessage(Player player) {
      MutableComponent msgContainer = new TextComponent("").withStyle(ChatFormatting.WHITE);
      MutableComponent playerName = player.getDisplayName().copy();
      playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
      return msgContainer.append(playerName).append(" defeated Boss!");
   }

   private Component getEscapeMessage(Player player) {
      MutableComponent msgContainer = new TextComponent("").withStyle(ChatFormatting.WHITE);
      MutableComponent playerName = player.getDisplayName().copy();
      playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
      return msgContainer.append(playerName).append(" successfully escaped from the Vault!");
   }

   private Component getCompletionMessage(Player player) {
      MutableComponent msgContainer = new TextComponent("").withStyle(ChatFormatting.WHITE);
      MutableComponent playerName = player.getDisplayName().copy();
      playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
      return msgContainer.append(playerName).append(" finished building a Vault!");
   }

   private void spawnRewardCrate(ServerLevel world, Vec3i pos, VaultRaid vault, LootContext context) {
      NonNullList<ItemStack> stacks = this.createLoot(world, vault, context);
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(VaultCrateBlock.Type.BOSS, stacks);
      ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), crate);
      item.setDefaultPickUpDelay();
      world.addFreshEntity(item);
      this.crates.add(new VaultObjective.Crate(stacks));
   }

   public int getTicksUntilNextVote() {
      return this.ticksUntilNextVote;
   }

   public float getCompletedPercent() {
      return Mth.clamp((float)this.completedSessions.size() / this.totalRequiredVotes, 0.0F, 1.0F);
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
   public Component getObjectiveTargetDescription(int amount) {
      return new TextComponent("Required amount of votes: ").append(new TextComponent(String.valueOf(amount)).withStyle(ChatFormatting.AQUA));
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerLevel world, BlockPos pos) {
      return ModBlocks.STABILIZER.defaultBlockState();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LegacyLootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      return null;
   }

   @Override
   public Component getObjectiveDisplayName() {
      return new TextComponent("Build a Vault").withStyle(ChatFormatting.AQUA);
   }

   @Override
   public Component getVaultName() {
      return new TextComponent("Architect Vault");
   }

   @Nonnull
   @Override
   public Supplier<? extends VaultGenerator> getVaultGenerator() {
      return VaultRaid.ARCHITECT_GENERATOR;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      if (this.activeSession != null) {
         tag.put("activeSession", this.activeSession.serialize());
      }

      ListTag sessions = new ListTag();

      for (VotingSession session : this.completedSessions) {
         sessions.add(session.serialize());
      }

      tag.put("completedSessions", sessions);
      tag.putInt("totalRequiredVotes", this.totalRequiredVotes);
      tag.putInt("voteDowntimeTicks", this.voteDowntimeTicks);
      tag.putInt("ticksUntilNextVote", this.ticksUntilNextVote);
      tag.putBoolean("votingLocked", this.votingLocked);
      NBTHelper.writeOptional(tag, "bossId", this.bossId, (nbt, uuid) -> nbt.putUUID("bossId", uuid));
      tag.putBoolean("isBossDead", this.isBossDead);
      tag.put("exitPortalLocations", this.exitPortalLocations.serializeNBT());
      tag.putBoolean("collidedWithExitPortal", this.collidedWithExitPortal);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      if (tag.contains("activeSession", 10)) {
         this.activeSession = new VotingSession(tag.getCompound("activeSession"));
      } else {
         this.activeSession = null;
      }

      this.completedSessions.clear();
      ListTag sessions = tag.getList("completedSessions", 10);

      for (int i = 0; i < sessions.size(); i++) {
         this.completedSessions.add(new VotingSession(sessions.getCompound(i)));
      }

      this.totalRequiredVotes = tag.getInt("totalRequiredVotes");
      this.voteDowntimeTicks = tag.getInt("voteDowntimeTicks");
      this.ticksUntilNextVote = tag.getInt("ticksUntilNextVote");
      this.votingLocked = tag.getBoolean("votingLocked");
      this.bossId = NBTHelper.readOptional(tag, "bossId", nbt -> nbt.getUUID("bossId"));
      this.isBossDead = tag.getBoolean("isBossDead");
      this.exitPortalLocations.deserializeNBT(tag.getList("exitPortalLocations", 10));
      this.collidedWithExitPortal = tag.getBoolean("collidedWithExitPortal");
   }
}
