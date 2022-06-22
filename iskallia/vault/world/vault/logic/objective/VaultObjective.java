package iskallia.vault.world.vault.logic.objective;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import iskallia.vault.block.entity.VaultLootableTileEntity;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.config.LootTablesConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.util.StatueType;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.VaultGenerator;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import iskallia.vault.world.vault.logic.task.IVaultTask;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.ArtifactChanceModifier;
import iskallia.vault.world.vault.modifier.InventoryRestoreModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class VaultObjective implements INBTSerializable<CompoundNBT>, IVaultTask {
   public static final BiMap<ResourceLocation, Supplier<? extends VaultObjective>> REGISTRY = HashBiMap.create();
   public static final float COOP_DOUBLE_CRATE_CHANCE = 0.5F;
   protected static final Random rand = new Random();
   private ResourceLocation id;
   protected VaultTask onTick;
   private VaultTask onComplete;
   private boolean completed;
   private int completionTime = -1;
   protected VListNBT<VaultObjective.Crate, CompoundNBT> crates = VListNBT.of(() -> new VaultObjective.Crate());

   protected VaultObjective() {
   }

   public VaultObjective(ResourceLocation id, VaultTask onTick, VaultTask onComplete) {
      this.id = id;
      this.onTick = onTick;
      this.onComplete = onComplete;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void initialize(MinecraftServer srv, VaultRaid vault) {
   }

   public boolean isCompleted() {
      return this.completed;
   }

   public int getCompletionTime() {
      return this.completionTime;
   }

   public void setCompleted() {
      this.completed = true;
   }

   public void setCompletionTime(int completionTime) {
      this.completionTime = completionTime;
   }

   public void setObjectiveTargetCount(int amount) {
   }

   @Nullable
   public ITextComponent getObjectiveTargetDescription(int amount) {
      return null;
   }

   @Nonnull
   public abstract BlockState getObjectiveRelevantBlock(VaultRaid var1, ServerWorld var2, BlockPos var3);

   public void postProcessObjectiveRelevantBlock(ServerWorld world, BlockPos pos) {
   }

   @Nullable
   public abstract LootTable getRewardLootTable(VaultRaid var1, Function<ResourceLocation, LootTable> var2);

   public abstract ITextComponent getObjectiveDisplayName();

   @Nonnull
   public Supplier<? extends VaultGenerator> getVaultGenerator() {
      return VaultRaid.SINGLE_STAR;
   }

   @Nullable
   public VaultRoomLayoutGenerator getCustomLayout() {
      return null;
   }

   public ITextComponent getVaultName() {
      return new StringTextComponent("Vault");
   }

   @Deprecated
   public int getMaxObjectivePlacements() {
      return 10;
   }

   public int modifyObjectiveCount(int objectives) {
      return objectives;
   }

   public int modifyMinimumObjectiveCount(int objectives, int requiredAmount) {
      return objectives;
   }

   public Collection<VaultObjective.Crate> getCrates() {
      return this.crates;
   }

   public boolean shouldPauseTimer(MinecraftServer srv, VaultRaid vault) {
      return vault.getPlayers().stream().noneMatch(vPlayer -> vPlayer.isOnline(srv));
   }

   public int getVaultTimerStart(int vaultTime) {
      return vaultTime;
   }

   public boolean preventsEatingExtensionFruit(MinecraftServer srv, VaultRaid vault) {
      return this.isCompleted();
   }

   public boolean preventsMobSpawning() {
      return false;
   }

   public boolean preventsTrappedChests() {
      return false;
   }

   public boolean preventsInfluences() {
      return false;
   }

   public boolean preventsNormalMonsterDrops() {
      return false;
   }

   public boolean preventsCatalystFragments() {
      return false;
   }

   public void notifyBail(VaultRaid vault, VaultPlayer player, ServerWorld world) {
   }

   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      if (!this.isCompleted()) {
         vault.getPlayers().forEach(vPlayer -> {
            if (filter.test(vPlayer.getPlayerId())) {
               this.onTick.execute(vault, vPlayer, world);
            }
         });
      }
   }

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      this.onComplete.execute(vault, player, world);
   }

   public void complete(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      this.onComplete.execute(vault, player, world);
   }

   public void complete(VaultRaid vault, ServerWorld world) {
      vault.getPlayers().forEach(player -> this.onComplete.execute(vault, player, world));
   }

   public VaultObjective thenTick(VaultTask task) {
      this.onTick = this.onTick == VaultTask.EMPTY ? task : this.onTick.then(task);
      return this;
   }

   public VaultObjective thenComplete(VaultTask task) {
      this.onComplete = this.onComplete == VaultTask.EMPTY ? task : this.onComplete.then(task);
      return this;
   }

   protected NonNullList<ItemStack> createLoot(ServerWorld world, VaultRaid vault, LootContext context) {
      LootTable rewardLootTable = this.getRewardLootTable(vault, world.func_73046_m().func_200249_aQ()::func_186521_a);
      if (rewardLootTable == null) {
         return NonNullList.func_191196_a();
      } else {
         NonNullList<ItemStack> stacks = NonNullList.func_191196_a();
         NonNullList<ItemStack> specialLoot = NonNullList.func_191196_a();
         this.addSpecialLoot(world, vault, context, specialLoot);
         stacks.addAll(rewardLootTable.func_216113_a(context));
         vault.getPlayers().stream().filter(player -> player instanceof VaultRunner).findAny().ifPresent(vPlayer -> {
            VaultTimer timer = vPlayer.getTimer();
            float pTimeLeft = MathHelper.func_76131_a(1.0F - (float)timer.getRunTime() / timer.getTotalTime(), 0.0F, 1.0F);
            List<ItemStack> additionalLoot = new ArrayList<>();
            additionalLoot.addAll(rewardLootTable.func_216113_a(context));
            additionalLoot.addAll(rewardLootTable.func_216113_a(context));
            int rolls = Math.round(additionalLoot.size() * pTimeLeft);
            if (rolls > 0) {
               stacks.addAll(additionalLoot.subList(0, rolls));
            }
         });
         stacks.removeIf(ItemStack::func_190926_b);

         for (int i = 0; i < stacks.size() - 54 + specialLoot.size(); i++) {
            stacks.remove(world.field_73012_v.nextInt(stacks.size()));
         }

         stacks.addAll(specialLoot);
         Collections.shuffle(stacks);
         return stacks;
      }
   }

   protected void addSpecialLoot(ServerWorld world, VaultRaid vault, LootContext context, NonNullList<ItemStack> stacks) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      int eternals = EternalsData.get(world).getTotalEternals();
      if (eternals > 0) {
         stacks.add(new ItemStack(ModItems.ETERNAL_SOUL, Math.min(world.field_73012_v.nextInt(eternals) + 1, 64)));
      }

      if (vault.getProperties().getBase(VaultRaid.IS_RAFFLE).orElse(false)) {
         String name = vault.getProperties().getValue(VaultRaid.PLAYER_BOSS_NAME);
         stacks.add(LootStatueBlockItem.getStatueBlockItem(name, StatueType.VAULT_BOSS));
         if (world.field_73012_v.nextInt(4) != 0) {
         }
      }

      int traders = ModConfigs.SCALING_CHEST_REWARDS.traderCount(this.getId(), VaultRarity.COMMON, level);

      for (int i = 0; i < traders; i++) {
         stacks.add(new ItemStack(ModItems.TRADER_CORE));
      }

      int statues = ModConfigs.SCALING_CHEST_REWARDS.statueCount(this.getId(), VaultRarity.COMMON, level);

      for (int i = 0; i < statues; i++) {
         ItemStack statue = new ItemStack(ModBlocks.GIFT_NORMAL_STATUE);
         if (ModConfigs.SCALING_CHEST_REWARDS.isMegaStatue()) {
            statue = new ItemStack(ModBlocks.GIFT_MEGA_STATUE);
         }

         stacks.add(statue);
      }

      boolean cannotGetArtifact = vault.getActiveModifiersFor(PlayerFilter.any(), InventoryRestoreModifier.class)
         .stream()
         .anyMatch(InventoryRestoreModifier::preventsArtifact);
      if (!cannotGetArtifact && config != null) {
         float chance = config.getArtifactChance();

         for (ArtifactChanceModifier modifier : vault.getActiveModifiersFor(PlayerFilter.any(), ArtifactChanceModifier.class)) {
            chance += modifier.getArtifactChanceIncrease();
         }

         if (vault.getProperties().getBaseOrDefault(VaultRaid.COW_VAULT, false)) {
            chance *= 2.0F;
         }

         if (world.func_201674_k().nextFloat() < chance) {
            stacks.add(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
         }
      }
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Id", this.getId().toString());
      nbt.func_74757_a("Completed", this.isCompleted());
      nbt.func_218657_a("OnTick", this.onTick.serializeNBT());
      nbt.func_218657_a("OnComplete", this.onComplete.serializeNBT());
      if (this.getCompletionTime() != -1) {
         nbt.func_74768_a("CompletionTime", this.getCompletionTime());
      }

      nbt.func_218657_a("Crates", this.crates.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.id = new ResourceLocation(nbt.func_74779_i("Id"));
      this.completed = nbt.func_74767_n("Completed");
      this.onTick = VaultTask.fromNBT(nbt.func_74775_l("OnTick"));
      this.onComplete = VaultTask.fromNBT(nbt.func_74775_l("OnComplete"));
      if (nbt.func_150297_b("CompletionTime", 3)) {
         this.completionTime = nbt.func_74762_e("CompletionTime");
      }

      this.crates.deserializeNBT(nbt.func_150295_c("Crates", 10));
   }

   public static VaultObjective fromNBT(CompoundNBT nbt) {
      VaultObjective objective = (VaultObjective)((Supplier)REGISTRY.get(new ResourceLocation(nbt.func_74779_i("Id")))).get();
      objective.deserializeNBT(nbt);
      return objective;
   }

   @Nullable
   public static VaultObjective getObjective(ResourceLocation key) {
      return (VaultObjective)((Supplier)REGISTRY.getOrDefault(key, (Supplier<VaultObjective>)() -> null)).get();
   }

   public static <T extends VaultObjective> Supplier<T> register(Supplier<T> objective) {
      REGISTRY.put(objective.get().getId(), objective);
      return objective;
   }

   public static VaultLootableTileEntity.ExtendedGenerator getObjectiveBlock() {
      return new VaultLootableTileEntity.ExtendedGenerator() {
         @Nonnull
         @Override
         public BlockState generate(ServerWorld world, BlockPos pos, Random random, String poolName, UUID playerUUID) {
            VaultRaid vault = VaultRaidData.get(world).getAt(world, pos);
            VaultObjective objective = (VaultObjective)Iterables.getFirst(vault.getAllObjectives(), null);
            return objective == null ? Blocks.field_150350_a.func_176223_P() : objective.getObjectiveRelevantBlock(vault, world, pos);
         }

         @Override
         public void postProcess(ServerWorld world, BlockPos pos) {
            VaultRaid vault = VaultRaidData.get(world).getAt(world, pos);
            VaultObjective objective = (VaultObjective)Iterables.getFirst(vault.getAllObjectives(), null);
            if (objective != null) {
               objective.postProcessObjectiveRelevantBlock(world, pos);
            }
         }
      };
   }

   public static class Crate implements INBTSerializable<CompoundNBT> {
      private List<ItemStack> contents = new ArrayList<>();

      private Crate() {
      }

      public Crate(List<ItemStack> contents) {
         this.contents = contents;
      }

      public List<ItemStack> getContents() {
         return this.contents;
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         ListNBT contentsList = new ListNBT();
         this.contents.forEach(stack -> contentsList.add(stack.func_77955_b(new CompoundNBT())));
         nbt.func_218657_a("Contents", contentsList);
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.contents.clear();
         ListNBT contentsList = nbt.func_150295_c("Contents", 10);
         contentsList.stream().map(inbt -> (CompoundNBT)inbt).forEach(compoundNBT -> this.contents.add(ItemStack.func_199557_a(compoundNBT)));
      }
   }
}
