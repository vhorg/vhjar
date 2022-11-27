package iskallia.vault.world.vault.logic.objective;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.VaultGenerator;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import iskallia.vault.world.vault.logic.task.IVaultTask;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.VaultModifiers;
import iskallia.vault.world.vault.modifier.modifier.ChanceArtifactModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class VaultObjective implements INBTSerializable<CompoundTag>, IVaultTask {
   public static final BiMap<ResourceLocation, Supplier<? extends VaultObjective>> REGISTRY = HashBiMap.create();
   public static final float COOP_DOUBLE_CRATE_CHANCE = 0.5F;
   protected static final Random rand = new Random();
   private ResourceLocation id;
   private VaultTask onTick;
   private VaultTask onComplete;
   private boolean completed;
   private int completionTime = -1;
   protected VListNBT<VaultObjective.Crate, CompoundTag> crates = VListNBT.of(VaultObjective.Crate::new);

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
   public Component getObjectiveTargetDescription(int amount) {
      return null;
   }

   @Nonnull
   public abstract BlockState getObjectiveRelevantBlock(VaultRaid var1, ServerLevel var2, BlockPos var3);

   public void postProcessObjectiveRelevantBlock(ServerLevel world, BlockPos pos) {
   }

   @Nullable
   public abstract LootTable getRewardLootTable(VaultRaid var1, Function<ResourceLocation, LootTable> var2);

   public abstract Component getObjectiveDisplayName();

   @Nonnull
   public Supplier<? extends VaultGenerator> getVaultGenerator() {
      return VaultRaid.SINGLE_STAR;
   }

   @Nullable
   public VaultRoomLayoutGenerator getCustomLayout() {
      return null;
   }

   public Component getVaultName() {
      return new TextComponent("Vault");
   }

   @Deprecated
   public int getMaxObjectivePlacements() {
      return 10;
   }

   public int modifyObjectiveCount(int objectives) {
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

   public void notifyBail(VaultRaid vault, VaultPlayer player, ServerLevel world) {
   }

   public void tick(VaultRaid vault, PlayerFilter filter, ServerLevel world) {
      if (!this.isCompleted()) {
         vault.getPlayers().forEach(vPlayer -> {
            if (filter.test(vPlayer.getPlayerId())) {
               this.onTick.execute(vault, vPlayer, world);
            }
         });
      }
   }

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      this.onComplete.execute(vault, player, world);
   }

   public void complete(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      this.onComplete.execute(vault, player, world);
   }

   public void complete(VaultRaid vault, ServerLevel world) {
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

   protected NonNullList<ItemStack> createLoot(ServerLevel world, VaultRaid vault, LootContext context) {
      LootTable rewardLootTable = this.getRewardLootTable(vault, world.getServer().getLootTables()::get);
      if (rewardLootTable == null) {
         return NonNullList.create();
      } else {
         NonNullList<ItemStack> stacks = NonNullList.create();
         NonNullList<ItemStack> specialLoot = NonNullList.create();
         this.addSpecialLoot(world, vault, context, specialLoot);
         stacks.addAll(rewardLootTable.getRandomItems(context));
         vault.getPlayers().stream().filter(player -> player instanceof VaultRunner).findAny().ifPresent(vPlayer -> {
            VaultTimer timer = vPlayer.getTimer();
            float pTimeLeft = Mth.clamp(1.0F - (float)timer.getRunTime() / timer.getTotalTime(), 0.0F, 1.0F);
            List<ItemStack> additionalLoot = new ArrayList<>();
            additionalLoot.addAll(rewardLootTable.getRandomItems(context));
            additionalLoot.addAll(rewardLootTable.getRandomItems(context));
            int rolls = Math.round(additionalLoot.size() * pTimeLeft);
            if (rolls > 0) {
               stacks.addAll(additionalLoot.subList(0, rolls));
            }
         });
         stacks.removeIf(ItemStack::isEmpty);

         for (int i = 0; i < stacks.size() - 54 + specialLoot.size(); i++) {
            stacks.remove(world.random.nextInt(stacks.size()));
         }

         stacks.addAll(specialLoot);
         Collections.shuffle(stacks);
         return stacks;
      }
   }

   protected void addSpecialLoot(ServerLevel world, VaultRaid vault, LootContext context, NonNullList<ItemStack> stacks) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LegacyLootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      int eternals = EternalsData.get(world).getTotalEternals();
      if (eternals > 0) {
         stacks.add(new ItemStack(ModItems.ETERNAL_SOUL, Math.min(world.random.nextInt(eternals) + 1, 64)));
      }

      if (vault.getProperties().getBase(VaultRaid.IS_RAFFLE).orElse(false)) {
         String name = vault.getProperties().getValue(VaultRaid.PLAYER_BOSS_NAME);
         stacks.add(LootStatueBlockItem.getStatueBlockItem(name));
         if (world.random.nextInt(4) != 0) {
         }
      }

      boolean cannotGetArtifact = vault.hasActiveModifierFor(PlayerFilter.any(), PlayerInventoryRestoreModifier.class, m -> m.properties().preventsArtifact());
      if (!cannotGetArtifact && config != null) {
         float chance = config.getArtifactChance();

         for (VaultModifiers.ActiveModifierStack<ChanceArtifactModifier> activeModifier : vault.getActiveModifiersFor(
            PlayerFilter.any(), ChanceArtifactModifier.class
         )) {
            chance += activeModifier.getModifier().properties().getChance() * activeModifier.getSize();
         }

         if (vault.getProperties().getBaseOrDefault(VaultRaid.COW_VAULT, false)) {
            chance *= 2.0F;
         }

         if (world.getRandom().nextFloat() < chance) {
            stacks.add(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
         }
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Id", this.getId().toString());
      nbt.putBoolean("Completed", this.isCompleted());
      nbt.put("OnTick", this.onTick.serializeNBT());
      nbt.put("OnComplete", this.onComplete.serializeNBT());
      if (this.getCompletionTime() != -1) {
         nbt.putInt("CompletionTime", this.getCompletionTime());
      }

      nbt.put("Crates", this.crates.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.id = new ResourceLocation(nbt.getString("Id"));
      this.completed = nbt.getBoolean("Completed");
      this.onTick = VaultTask.fromNBT(nbt.getCompound("OnTick"));
      this.onComplete = VaultTask.fromNBT(nbt.getCompound("OnComplete"));
      if (nbt.contains("CompletionTime", 3)) {
         this.completionTime = nbt.getInt("CompletionTime");
      }

      this.crates.deserializeNBT(nbt.getList("Crates", 10));
   }

   public static VaultObjective fromNBT(CompoundTag nbt) {
      VaultObjective objective = (VaultObjective)((Supplier)REGISTRY.get(new ResourceLocation(nbt.getString("Id")))).get();
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

   public static class Crate implements INBTSerializable<CompoundTag> {
      private List<ItemStack> contents = new ArrayList<>();

      private Crate() {
      }

      public Crate(List<ItemStack> contents) {
         this.contents = contents;
      }

      public List<ItemStack> getContents() {
         return this.contents;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         ListTag contentsList = new ListTag();
         this.contents.forEach(stack -> contentsList.add(stack.save(new CompoundTag())));
         nbt.put("Contents", contentsList);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.contents.clear();
         ListTag contentsList = nbt.getList("Contents", 10);
         contentsList.stream().map(inbt -> (CompoundTag)inbt).forEach(compoundNBT -> this.contents.add(ItemStack.of(compoundNBT)));
      }
   }
}
