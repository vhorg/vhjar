package iskallia.vault.world.vault.logic.objective.raid;

import com.google.common.collect.Lists;
import iskallia.vault.block.entity.VaultRaidControllerTileEntity;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.gen.decorator.BreadcrumbFeature;
import iskallia.vault.world.gen.structure.VaultJigsawHelper;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.VaultGenerator;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultRaidRoom;
import iskallia.vault.world.vault.logic.objective.VaultModifierVotingSession;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.raid.modifier.FloatingItemModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.ModifierDoublingModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.RaidModifier;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.modifier.ChanceCatalystModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.network.NetworkDirection;

public class RaidChallengeObjective extends VaultObjective {
   private final LinkedHashMap<RaidModifier, Float> modifierValues = new LinkedHashMap<>();
   private int completedRaids = 0;
   private int targetRaids = -1;
   private boolean started = false;
   private VaultModifierVotingSession session = null;

   public RaidChallengeObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerLevel world, BlockPos pos) {
      return Blocks.AIR.defaultBlockState();
   }

   @Nonnull
   @Override
   public Supplier<? extends VaultGenerator> getVaultGenerator() {
      return VaultRaid.RAID_CHALLENGE_GENERATOR;
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      return null;
   }

   @Override
   public boolean shouldPauseTimer(MinecraftServer srv, VaultRaid vault) {
      return !this.started;
   }

   @Override
   public Component getObjectiveDisplayName() {
      return new TextComponent("Raid").withStyle(ChatFormatting.RED);
   }

   @Nullable
   @Override
   public Component getObjectiveTargetDescription(int amount) {
      return amount < 0 ? null : new TextComponent("Raids to complete: ").append(new TextComponent(String.valueOf(amount)).withStyle(ChatFormatting.RED));
   }

   @Override
   public void setObjectiveTargetCount(int amount) {
      this.targetRaids = amount;
   }

   @Override
   public Component getVaultName() {
      return new TextComponent("Vault Raid");
   }

   public int getCompletedRaids() {
      return this.completedRaids;
   }

   @Nullable
   public VaultModifierVotingSession getVotingSession() {
      return this.session;
   }

   public void addModifier(RaidModifier modifier, float value) {
      if (modifier instanceof ModifierDoublingModifier) {
         this.modifierValues.forEach(this::addModifier);
      } else {
         for (RaidModifier existing : this.modifierValues.keySet()) {
            if (existing.getName().equals(modifier.getName())) {
               float existingValue = this.modifierValues.get(existing);
               this.modifierValues.put(modifier, Float.valueOf(existingValue + value));
               return;
            }
         }

         this.modifierValues.put(modifier, Float.valueOf(value));
      }
   }

   public Map<RaidModifier, Float> getAllModifiers() {
      return Collections.unmodifiableMap(this.modifierValues);
   }

   public <T extends RaidModifier> Map<T, Float> getModifiersOfType(Class<T> modifierClass) {
      return this.modifierValues
         .entrySet()
         .stream()
         .filter(modifierTpl -> modifierClass.isAssignableFrom(modifierTpl.getKey().getClass()))
         .map(tpl -> tpl)
         .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
   }

   public LinkedHashMap<RaidModifier, Float> getModifiers(boolean positive) {
      LinkedHashMap<RaidModifier, Float> modifiers = new LinkedHashMap<>();
      this.modifierValues.forEach((modifier, value) -> {
         if (positive && modifier.isPositive() || !positive && !modifier.isPositive()) {
            modifiers.put(modifier, value);
         }
      });
      return modifiers;
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerLevel world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.getServer();
      ActiveRaid raid = vault.getActiveRaid();
      if (raid != null) {
         this.started = true;
      }

      if (this.session != null) {
         this.session.tick();
         if (this.session.isFinished()) {
            this.session.finish(vault, world);
            this.session = null;
         }
      }

      this.sendRaidMessage(vault, filter, srv, raid);
   }

   private void sendRaidMessage(VaultRaid vault, PlayerFilter filter, MinecraftServer srv, @Nullable ActiveRaid raid) {
      int wave = raid == null ? 0 : raid.getWave();
      int totalWaves = raid == null ? 0 : raid.getTotalWaves();
      int mobs = raid == null ? 0 : raid.getAliveEntities();
      int totalMobs = raid == null ? 0 : raid.getTotalWaveEntities();
      int startDelay = raid == null ? 0 : raid.getStartDelay();
      List<Component> positives = new ArrayList<>();
      List<Component> negatives = new ArrayList<>();
      this.modifierValues.forEach((modifier, value) -> {
         Component display = modifier.getDisplay(value);
         if (modifier.isPositive()) {
            positives.add(display);
         } else {
            negatives.add(display);
         }
      });
      vault.getPlayers()
         .stream()
         .filter(vPlayer -> filter.test(vPlayer.getPlayerId()))
         .forEach(
            vPlayer -> vPlayer.runIfPresent(
               srv,
               playerEntity -> {
                  VaultGoalMessage pkt = VaultGoalMessage.raidChallenge(
                     wave, totalWaves, mobs, totalMobs, startDelay, this.completedRaids, this.targetRaids, positives, negatives, this.session
                  );
                  ModNetwork.CHANNEL.sendTo(pkt, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
               }
            )
         );
   }

   public void onRaidStart(VaultRaid vault, ServerLevel world, ActiveRaid raid, BlockPos controller) {
      if (world.getBlockEntity(controller) instanceof VaultRaidControllerTileEntity raidController) {
         raidController.getRaidModifiers().forEach((modifierName, value) -> {
            RaidModifier mod = ModConfigs.RAID_MODIFIER_CONFIG.getByName(modifierName);
            if (mod != null) {
               this.addModifier(mod, value);
            }
         });
         if (this.session == null && ModConfigs.RAID_EVENT_CONFIG.isEnabled() && rand.nextFloat() < ModConfigs.RAID_EVENT_CONFIG.getViewerVoteChance()) {
            this.session = VaultModifierVotingSession.create();
         }
      }
   }

   public void onRaidFinish(VaultRaid vault, ServerLevel world, ActiveRaid raid, BlockPos controller) {
      this.completedRaids++;
      if (this.targetRaids >= 0 && this.completedRaids >= this.targetRaids) {
         this.setCompleted();
      } else {
         if (this.completedRaids % 10 == 0) {
            RaidModifier modifier = ModConfigs.RAID_MODIFIER_CONFIG.getByName("artifactFragment");
            if (modifier != null) {
               boolean canGetArtifact = !vault.hasActiveModifierFor(
                  PlayerFilter.any(), PlayerInventoryRestoreModifier.class, m -> m.properties().preventsArtifact()
               );
               if (canGetArtifact) {
                  this.addModifier(modifier, MathUtilities.randomFloat(0.02F, 0.05F));
               }
            }
         }

         VaultRaidRoom raidRoom = vault.getGenerator().getPiecesAt(controller, VaultRaidRoom.class).stream().findFirst().orElse(null);
         if (raidRoom != null) {
            for (Direction direction : Direction.values()) {
               if (direction.getAxis() != Axis.Y && VaultJigsawHelper.canExpand(vault, raidRoom, direction)) {
                  VaultJigsawHelper.expandVault(vault, world, raidRoom, direction, VaultJigsawHelper.getRaidChallengeRoom());
               }
            }

            raidRoom.setRaidFinished();
            this.getAllModifiers().forEach((modifier, value) -> modifier.onVaultRaidFinish(vault, world, controller, raid, value));
            FloatingItemModifier catalystPlacement = new FloatingItemModifier(
               "", 4, new WeightedList<SingleItemEntry>().add(new SingleItemEntry(ModItems.VAULT_CATALYST_FRAGMENT), 1), ""
            );
            vault.withActiveModifiersFor(
               PlayerFilter.any(),
               ChanceCatalystModifier.class,
               (chanceCatalystModifier, stackSize) -> catalystPlacement.onVaultRaidFinish(vault, world, controller, raid, 1.0F)
            );
            BreadcrumbFeature.generateVaultBreadcrumb(vault, world, Lists.newArrayList(new VaultPiece[]{raidRoom}));
         }
      }
   }

   @Override
   public boolean isCompleted() {
      return super.isCompleted();
   }

   @Override
   public boolean preventsMobSpawning() {
      return true;
   }

   @Override
   public boolean preventsInfluences() {
      return true;
   }

   @Override
   public boolean preventsNormalMonsterDrops() {
      return true;
   }

   @Override
   public boolean preventsCatalystFragments() {
      return true;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      ListTag modifiers = new ListTag();
      this.modifierValues.forEach((modifier, value) -> {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("name", modifier.getName());
         nbt.putFloat("value", value);
         modifiers.add(nbt);
      });
      tag.put("raidModifiers", modifiers);
      tag.putInt("completedRaids", this.completedRaids);
      if (this.targetRaids >= 0) {
         tag.putInt("targetRaids", this.targetRaids);
      }

      tag.putBoolean("started", this.started);
      if (this.session != null) {
         tag.put("votingSession", this.session.serialize());
      }

      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      ListTag modifiers = nbt.getList("raidModifiers", 10);

      for (int i = 0; i < modifiers.size(); i++) {
         CompoundTag modifierTag = modifiers.getCompound(i);
         RaidModifier modifier = ModConfigs.RAID_MODIFIER_CONFIG.getByName(modifierTag.getString("name"));
         if (modifier != null) {
            float val = modifierTag.getFloat("value");
            this.modifierValues.put(modifier, Float.valueOf(val));
         }
      }

      this.completedRaids = nbt.getInt("completedRaids");
      if (nbt.contains("targetRaids", 3)) {
         this.targetRaids = nbt.getInt("targetRaids");
      }

      this.started = nbt.getBoolean("started");
      if (nbt.contains("votingSession", 10)) {
         this.session = VaultModifierVotingSession.deserialize(nbt.getCompound("votingSession"));
      }
   }
}
