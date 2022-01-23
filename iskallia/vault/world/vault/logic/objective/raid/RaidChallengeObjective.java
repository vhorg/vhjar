package iskallia.vault.world.vault.logic.objective.raid;

import com.google.common.collect.Lists;
import iskallia.vault.block.entity.VaultRaidControllerTileEntity;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.init.ModBlocks;
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
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.raid.modifier.BlockPlacementModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.FloatingItemModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.ModifierDoublingModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.RaidModifier;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.CatalystChanceModifier;
import iskallia.vault.world.vault.modifier.InventoryRestoreModifier;
import iskallia.vault.world.vault.modifier.LootableModifier;
import iskallia.vault.world.vault.modifier.VaultModifier;
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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;

public class RaidChallengeObjective extends VaultObjective {
   private final LinkedHashMap<RaidModifier, Float> modifierValues = new LinkedHashMap<>();
   private int completedRaids = 0;
   private boolean started = false;

   public RaidChallengeObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock() {
      return Blocks.field_150350_a.func_176223_P();
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
   public ITextComponent getObjectiveDisplayName() {
      return new StringTextComponent("Raid").func_240699_a_(TextFormatting.RED);
   }

   @Override
   public ITextComponent getVaultName() {
      return new StringTextComponent("Vault Raid");
   }

   public int getCompletedRaids() {
      return this.completedRaids;
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
   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.func_73046_m();
      ActiveRaid raid = vault.getActiveRaid();
      if (raid != null) {
         this.started = true;
      }

      this.sendRaidMessage(vault, filter, srv, raid);
   }

   private void sendRaidMessage(VaultRaid vault, PlayerFilter filter, MinecraftServer srv, @Nullable ActiveRaid raid) {
      int wave = raid == null ? 0 : raid.getWave();
      int totalWaves = raid == null ? 0 : raid.getTotalWaves();
      int mobs = raid == null ? 0 : raid.getAliveEntities();
      int totalMobs = raid == null ? 0 : raid.getTotalWaveEntities();
      int startDelay = raid == null ? 0 : raid.getStartDelay();
      List<ITextComponent> positives = new ArrayList<>();
      List<ITextComponent> negatives = new ArrayList<>();
      this.modifierValues.forEach((modifier, value) -> {
         ITextComponent display = modifier.getDisplay(value);
         if (modifier.isPositive()) {
            positives.add(display);
         } else {
            negatives.add(display);
         }
      });
      vault.getPlayers().stream().filter(vPlayer -> filter.test(vPlayer.getPlayerId())).forEach(vPlayer -> vPlayer.runIfPresent(srv, playerEntity -> {
         VaultGoalMessage pkt = VaultGoalMessage.raidChallenge(wave, totalWaves, mobs, totalMobs, startDelay, this.completedRaids, positives, negatives);
         ModNetwork.CHANNEL.sendTo(pkt, playerEntity.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
      }));
   }

   public void onRaidStart(VaultRaid vault, ServerWorld world, ActiveRaid raid, BlockPos controller) {
      TileEntity te = world.func_175625_s(controller);
      if (te instanceof VaultRaidControllerTileEntity) {
         VaultRaidControllerTileEntity raidController = (VaultRaidControllerTileEntity)te;
         raidController.getRaidModifiers().forEach((modifierName, value) -> {
            RaidModifier mod = ModConfigs.RAID_MODIFIER_CONFIG.getByName(modifierName);
            if (mod != null) {
               this.addModifier(mod, value);
            }
         });
         if (ModConfigs.RAID_EVENT_CONFIG.isEnabled() && rand.nextFloat() < ModConfigs.RAID_EVENT_CONFIG.getViewerVoteChance()) {
            this.addRandomModifier(vault, world);
         }
      }
   }

   private void addRandomModifier(VaultRaid vault, ServerWorld world) {
      VaultModifier vModifier;
      do {
         String modifierName = ModConfigs.RAID_EVENT_CONFIG.getRandomModifier().getName();
         vModifier = ModConfigs.VAULT_MODIFIERS.getByName(modifierName);
      } while (vModifier == null);

      int minutes = ModConfigs.RAID_EVENT_CONFIG.getTemporaryModifierMinutes();
      ITextComponent ct = new StringTextComponent("Added ")
         .func_240699_a_(TextFormatting.GRAY)
         .func_230529_a_(vModifier.getNameComponent())
         .func_230529_a_(new StringTextComponent(" for ").func_240699_a_(TextFormatting.GRAY))
         .func_230529_a_(new StringTextComponent(minutes + " minutes!").func_240699_a_(TextFormatting.GOLD));
      vault.getModifiers().addTemporaryModifier(vModifier, minutes * 60 * 20);
      vault.getPlayers().forEach(vPlayer -> {
         vModifier.apply(vault, vPlayer, world, world.func_201674_k());
         vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> sPlayer.func_145747_a(ct, Util.field_240973_b_));
      });
   }

   public void onRaidFinish(VaultRaid vault, ServerWorld world, ActiveRaid raid, BlockPos controller) {
      this.completedRaids++;
      if (this.completedRaids % 10 == 0) {
         RaidModifier modifier = ModConfigs.RAID_MODIFIER_CONFIG.getByName("artifactFragment");
         if (modifier != null) {
            boolean canGetArtifact = vault.getActiveModifiersFor(PlayerFilter.any(), InventoryRestoreModifier.class)
               .stream()
               .noneMatch(InventoryRestoreModifier::preventsArtifact);
            if (canGetArtifact) {
               this.addModifier(modifier, MathUtilities.randomFloat(0.02F, 0.05F));
            }
         }
      }

      VaultRaidRoom raidRoom = vault.getGenerator().getPiecesAt(controller, VaultRaidRoom.class).stream().findFirst().orElse(null);
      if (raidRoom != null) {
         for (Direction direction : Direction.values()) {
            if (direction.func_176740_k() != Axis.Y && VaultJigsawHelper.canExpand(vault, raidRoom, direction)) {
               VaultJigsawHelper.expandVault(vault, world, raidRoom, direction, VaultJigsawHelper.getRaidChallengeRoom());
            }
         }

         raidRoom.setRaidFinished();
         this.getAllModifiers().forEach((modifier, value) -> modifier.onVaultRaidFinish(vault, world, controller, raid, value));
         AxisAlignedBB raidBoundingBox = raid.getRaidBoundingBox();
         FloatingItemModifier catalystPlacement = new FloatingItemModifier(
            "", 4, new WeightedList<SingleItemEntry>().add(new SingleItemEntry(ModItems.VAULT_CATALYST_FRAGMENT), 1), ""
         );
         vault.getActiveModifiersFor(PlayerFilter.any(), CatalystChanceModifier.class)
            .forEach(modifier -> catalystPlacement.onVaultRaidFinish(vault, world, controller, raid, 1.0F));
         BlockPlacementModifier orePlacement = new BlockPlacementModifier("", ModBlocks.UNKNOWN_ORE, 12, "");
         vault.getActiveModifiersFor(PlayerFilter.any(), LootableModifier.class)
            .forEach(modifier -> orePlacement.onVaultRaidFinish(vault, world, controller, raid, modifier.getAverageMultiplier()));
         BreadcrumbFeature.generateVaultBreadcrumb(vault, world, Lists.newArrayList(new VaultPiece[]{raidRoom}));
      }
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
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      ListNBT modifiers = new ListNBT();
      this.modifierValues.forEach((modifier, value) -> {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74778_a("name", modifier.getName());
         nbt.func_74776_a("value", value);
         modifiers.add(nbt);
      });
      tag.func_218657_a("raidModifiers", modifiers);
      tag.func_74768_a("completedRaids", this.completedRaids);
      tag.func_74757_a("started", this.started);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      ListNBT modifiers = nbt.func_150295_c("raidModifiers", 10);

      for (int i = 0; i < modifiers.size(); i++) {
         CompoundNBT modifierTag = modifiers.func_150305_b(i);
         RaidModifier modifier = ModConfigs.RAID_MODIFIER_CONFIG.getByName(modifierTag.func_74779_i("name"));
         if (modifier != null) {
            float val = modifierTag.func_74760_g("value");
            this.modifierValues.put(modifier, Float.valueOf(val));
         }
      }

      this.completedRaids = nbt.func_74762_e("completedRaids");
      this.started = nbt.func_74767_n("started");
   }
}
