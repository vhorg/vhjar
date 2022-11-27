package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.config.LegacyScavengerHuntConfig;
import iskallia.vault.container.ScavengerChestContainer;
import iskallia.vault.entity.entity.VaultSandEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.BasicScavengerItem;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import iskallia.vault.world.vault.time.extension.SandExtension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

public class TreasureHuntObjective extends VaultObjective {
   public static final int INVENTORY_SIZE = 45;
   private final TreasureHuntObjective.ChestWatcher chestWatcher = new TreasureHuntObjective.ChestWatcher();
   private final TreasureHuntObjective.Inventory inventoryMirror = new TreasureHuntObjective.Inventory();
   private final List<TreasureHuntObjective.ItemSubmission> submissions = new ArrayList<>();
   private int requiredSubmissions;
   private NonNullList<ItemStack> chestInventory = NonNullList.withSize(45, ItemStack.EMPTY);

   public TreasureHuntObjective(ResourceLocation id) {
      this(id, ModConfigs.TREASURE_HUNT.getTotalRequiredItems());
   }

   private TreasureHuntObjective(ResourceLocation id, int requiredSubmissions) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
      this.requiredSubmissions = requiredSubmissions;
   }

   public Container getScavengerChestInventory() {
      return this.inventoryMirror;
   }

   public TreasureHuntObjective.ChestWatcher getChestWatcher() {
      return this.chestWatcher;
   }

   private Stream<TreasureHuntObjective.ItemSubmission> getActiveSubmissionsFilter() {
      return this.getAllSubmissions().stream().filter(submission -> !submission.isFinished());
   }

   public List<TreasureHuntObjective.ItemSubmission> getActiveSubmissions() {
      return this.getActiveSubmissionsFilter().collect(Collectors.toList());
   }

   public List<TreasureHuntObjective.ItemSubmission> getAllSubmissions() {
      return Collections.unmodifiableList(this.submissions);
   }

   public Predicate<LegacyScavengerHuntConfig.ItemEntry> getGenerationDropFilter() {
      List<TreasureHuntObjective.ItemSubmission> submissions = this.getActiveSubmissions();
      return entry -> {
         Item generatedItem = entry.getItem();

         for (TreasureHuntObjective.ItemSubmission submission : submissions) {
            if (generatedItem.equals(submission.getRequiredItem())) {
               return true;
            }
         }

         return false;
      };
   }

   public boolean trySubmitItem(UUID vaultIdentifier, ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else if (!vaultIdentifier.equals(BasicScavengerItem.getVaultIdentifier(stack))) {
         return false;
      } else {
         Item providedItem = stack.getItem();
         boolean addedItem = this.getActiveSubmissionsFilter()
            .filter(submission -> providedItem.equals(submission.requiredItem))
            .findFirst()
            .map(submission -> {
               int add = Math.min(stack.getCount(), submission.requiredAmount - submission.currentAmount);
               submission.currentAmount += add;
               stack.shrink(add);
               return true;
            })
            .orElse(false);
         if (this.getAllSubmissions().stream().filter(TreasureHuntObjective.ItemSubmission::isFinished).count() >= this.requiredSubmissions) {
            this.setCompleted();
         }

         return addedItem;
      }
   }

   @Override
   public void setObjectiveTargetCount(int amount) {
      this.requiredSubmissions = amount;
   }

   @Nullable
   @Override
   public Component getObjectiveTargetDescription(int amount) {
      return new TextComponent("Total required Item Types: ").append(new TextComponent(String.valueOf(amount)).withStyle(ChatFormatting.GREEN));
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerLevel world, BlockPos pos) {
      return ModBlocks.SCAVENGER_CHEST.defaultBlockState();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LegacyLootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      return config != null ? tblResolver.apply(config.getScavengerCrate()) : LootTable.EMPTY;
   }

   @Override
   public Component getObjectiveDisplayName() {
      return new TextComponent("Treasure Hunt").withStyle(ChatFormatting.GREEN);
   }

   @Override
   public Component getVaultName() {
      return new TextComponent("Treasure Vault");
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerLevel world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.getServer();
      vault.getPlayers().stream().filter(vPlayer -> filter.test(vPlayer.getPlayerId())).forEach(vPlayer -> vPlayer.runIfPresent(srv, playerEntity -> {
         VaultGoalMessage pkt = VaultGoalMessage.treasureHunt(this.getActiveSubmissions());
         ModNetwork.CHANNEL.sendTo(pkt, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      }));
      if (!this.isCompleted()) {
         long activeSubmissions = this.getActiveSubmissionsFilter().count();
         if (world.getGameTime() % 20L == 0L) {
            boolean addedAnyItem = vault.getProperties().getBase(VaultRaid.IDENTIFIER).map(identifier -> {
               boolean addedItem = false;
               NonNullList<ItemStack> inventory = this.chestInventory;

               for (int slot = 0; slot < inventory.size(); slot++) {
                  ItemStack stack = (ItemStack)inventory.get(slot);
                  if (!stack.isEmpty() && this.trySubmitItem(identifier, stack)) {
                     this.chestInventory.set(slot, stack);
                     this.updateOpenContainers(srv, vault, slot, stack);
                     addedItem = true;
                  }
               }

               return addedItem;
            }).orElse(false);
            if (activeSubmissions > this.getActiveSubmissionsFilter().count()) {
               vault.getPlayers()
                  .forEach(
                     vPlayer -> vPlayer.runIfPresent(
                        srv, sPlayer -> world.playSound(null, sPlayer.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F)
                     )
                  );
            } else if (addedAnyItem) {
               vault.getPlayers()
                  .forEach(
                     vPlayer -> vPlayer.runIfPresent(
                        srv, sPlayer -> world.playSound(null, sPlayer.blockPosition(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 1.1F, 1.4F)
                     )
                  );
            }
         }

         if (this.isCompleted()) {
            this.spawnRewards(world, vault);
         }

         if (this.getAllSubmissions().size() < this.requiredSubmissions) {
            TreasureHuntObjective.ItemSubmission newEntry = this.getNewEntry(vault);
            if (newEntry != null) {
               this.submissions.add(newEntry);
            }
         }

         vault.getPlayers()
            .stream()
            .map(vPlayer -> vPlayer.getServerPlayer(world.getServer()))
            .flatMap(Optional::stream)
            .map(player -> vault.getGenerator().getPiecesAt(player.blockPosition()))
            .flatMap(Collection::stream)
            .filter(piece -> piece instanceof VaultRoom)
            .map(piece -> (VaultRoom)piece)
            .forEach(room -> {
               if (room.getSandId().size() < ModConfigs.TREASURE_HUNT.sandPerRoom) {
                  this.spawnSand(vault, world, room);
               }
            });
      }
   }

   public void spawnSand(VaultRaid vault, ServerLevel world, VaultRoom room) {
      for (int i = 0; i < 200; i++) {
         int x = rand.nextInt(room.getBoundingBox().maxX() - room.getBoundingBox().minX() + 1) + room.getBoundingBox().minX();
         int y = rand.nextInt(room.getBoundingBox().maxY() - room.getBoundingBox().minY() + 1) + room.getBoundingBox().minY();
         int z = rand.nextInt(room.getBoundingBox().maxZ() - room.getBoundingBox().minZ() + 1) + room.getBoundingBox().minZ();
         BlockPos pos = new BlockPos(x, y, z);
         if (world.isAreaLoaded(pos, 1)) {
            BlockState top = world.getBlockState(pos.above());
            BlockState middle = world.getBlockState(pos);
            BlockState bottom = world.getBlockState(pos.below());
            if (middle.isAir() && top.isAir() && bottom.isAir()) {
               VaultSandEntity sand = VaultSandEntity.create(world, pos);
               room.addSandId(sand.getUUID());
               world.addFreshEntity(sand);
               return;
            }
         }
      }
   }

   public void depositSand(VaultRaid vault, ServerPlayer player, int amount) {
      long extraTime = (long)ModConfigs.TREASURE_HUNT.ticksPerSand * amount;
      SandExtension extension = new SandExtension(player.getUUID(), amount, extraTime);
      vault.getPlayers().forEach(vPlayer -> vPlayer.getTimer().addTime(extension, 0));
   }

   private void updateOpenContainers(MinecraftServer srv, VaultRaid vault, int slot, ItemStack stack) {
      vault.getPlayers().forEach(vPlayer -> vPlayer.runIfPresent(srv, sPlayer -> {
         if (sPlayer.containerMenu instanceof ScavengerChestContainer) {
            sPlayer.containerMenu.setItem(slot, 0, stack);
            sPlayer.connection.send(new ClientboundContainerSetSlotPacket(sPlayer.containerMenu.containerId, 0, slot, stack));
         }
      }));
   }

   @Override
   public void complete(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      super.complete(vault, player, world);
      player.sendIfPresent(world.getServer(), VaultGoalMessage.clear());
   }

   @Override
   public void complete(VaultRaid vault, ServerLevel world) {
      super.complete(vault, world);
      vault.getPlayers().forEach(player -> player.sendIfPresent(world.getServer(), VaultGoalMessage.clear()));
   }

   public void spawnRewards(ServerLevel world, VaultRaid vault) {
      VaultPlayer rewardPlayer = vault.getProperties()
         .getBase(VaultRaid.HOST)
         .flatMap(vault::getPlayer)
         .filter(vPlayer -> vPlayer instanceof VaultRunner)
         .orElseGet(() -> vault.getPlayers().stream().filter(vPlayer -> vPlayer instanceof VaultRunner).findAny().orElse(null));
      if (rewardPlayer != null) {
         rewardPlayer.runIfPresent(
            world.getServer(),
            sPlayer -> {
               BlockPos pos = sPlayer.blockPosition();
               Builder builder = new Builder(world)
                  .withRandom(world.random)
                  .withParameter(LootContextParams.THIS_ENTITY, sPlayer)
                  .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                  .withLuck(sPlayer.getLuck());
               LootContext ctx = builder.create(LootContextParamSets.CHEST);
               this.dropRewardCrate(world, vault, pos, ctx);

               for (int i = 1; i < vault.getPlayers().size(); i++) {
                  if (rand.nextFloat() < 0.5F) {
                     this.dropRewardCrate(world, vault, pos, ctx);
                  }
               }

               MutableComponent msgContainer = new TextComponent("").withStyle(ChatFormatting.WHITE);
               MutableComponent playerName = sPlayer.getDisplayName().copy();
               playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
               MiscUtils.broadcast(msgContainer.append(playerName).append(" finished a Treasure Hunt!"));
            }
         );
      }
   }

   private void dropRewardCrate(ServerLevel world, VaultRaid vault, BlockPos pos, LootContext context) {
      NonNullList<ItemStack> stacks = this.createLoot(world, vault, context);
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(VaultCrateBlock.Type.SCAVENGER, stacks);
      ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), crate);
      item.setDefaultPickUpDelay();
      world.addFreshEntity(item);
      this.crates.add(new VaultObjective.Crate(stacks));
   }

   @Nullable
   private TreasureHuntObjective.ItemSubmission getNewEntry(VaultRaid vault) {
      List<Item> currentItems = this.submissions.stream().map(submission -> submission.requiredItem).collect(Collectors.toList());
      int players = vault.getPlayers().size();
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      float multiplier = 1.0F + (players - 1) * 0.5F;
      LegacyScavengerHuntConfig.ItemEntry newEntry = ModConfigs.TREASURE_HUNT.getRandomRequiredItem(currentItems::contains);
      if (newEntry == null) {
         return null;
      } else {
         LegacyScavengerHuntConfig.SourceType sourceType = ModConfigs.TREASURE_HUNT.getRequirementSource(newEntry.createItemStack());
         switch (sourceType) {
            case MOB:
               multiplier *= 1.0F + level / 100.0F;
               break;
            case CHEST:
               multiplier *= 1.0F + level / 100.0F / 1.5F;
         }

         return TreasureHuntObjective.ItemSubmission.fromConfigEntry(newEntry, multiplier);
      }
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      ListTag list = new ListTag();

      for (TreasureHuntObjective.ItemSubmission submission : this.submissions) {
         list.add(submission.serialize());
      }

      tag.put("submissions", list);
      tag.putInt("requiredSubmissions", this.requiredSubmissions);
      ListTag inventoryList = new ListTag();

      for (int slot = 0; slot < this.chestInventory.size(); slot++) {
         ItemStack stack = (ItemStack)this.chestInventory.get(slot);
         if (!stack.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putInt("slot", slot);
            itemTag.put("item", stack.serializeNBT());
            inventoryList.add(itemTag);
         }
      }

      tag.put("inventory", inventoryList);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.submissions.clear();
      ListTag list = tag.getList("submissions", 10);

      for (int index = 0; index < list.size(); index++) {
         this.submissions.add(TreasureHuntObjective.ItemSubmission.deserialize(list.getCompound(index)));
      }

      this.requiredSubmissions = tag.getInt("requiredSubmissions");
      this.chestInventory = NonNullList.withSize(45, ItemStack.EMPTY);
      ListTag inventoryList = tag.getList("inventory", 10);

      for (int i = 0; i < inventoryList.size(); i++) {
         CompoundTag itemTag = inventoryList.getCompound(i);
         int slot = itemTag.getInt("slot");
         ItemStack stack = ItemStack.of(itemTag.getCompound("item"));
         this.chestInventory.set(slot, stack);
      }
   }

   public class ChestWatcher implements ContainerListener {
      public void slotChanged(AbstractContainerMenu container, int slotId, ItemStack stack) {
         if (slotId >= 0 && slotId < 45) {
            TreasureHuntObjective.this.chestInventory.set(slotId, stack);
         }
      }

      public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
      }
   }

   public static class Config {
      private IntSupplier requiredSubmissionsGen;
      private Function<Predicate<Item>, LegacyScavengerHuntConfig.ItemEntry> itemGen;
      private Function<ItemStack, LegacyScavengerHuntConfig.SourceType> sourceGen;

      public Config(
         IntSupplier requiredSubmissionsGen,
         Function<Predicate<Item>, LegacyScavengerHuntConfig.ItemEntry> itemGen,
         Function<ItemStack, LegacyScavengerHuntConfig.SourceType> sourceGen
      ) {
         this.requiredSubmissionsGen = requiredSubmissionsGen;
         this.itemGen = itemGen;
         this.sourceGen = sourceGen;
      }
   }

   private class Inventory implements Container {
      public int getContainerSize() {
         return TreasureHuntObjective.this.chestInventory.size();
      }

      public boolean isEmpty() {
         return TreasureHuntObjective.this.chestInventory.isEmpty();
      }

      public ItemStack getItem(int index) {
         return (ItemStack)TreasureHuntObjective.this.chestInventory.get(index);
      }

      public ItemStack removeItem(int index, int count) {
         return ContainerHelper.removeItem(TreasureHuntObjective.this.chestInventory, index, count);
      }

      public ItemStack removeItemNoUpdate(int index) {
         ItemStack existing = this.getItem(index);
         this.setItem(index, ItemStack.EMPTY);
         return existing;
      }

      public void setItem(int index, ItemStack stack) {
         TreasureHuntObjective.this.chestInventory.set(index, stack);
      }

      public void setChanged() {
      }

      public boolean stillValid(Player player) {
         return true;
      }

      public void clearContent() {
         TreasureHuntObjective.this.chestInventory.clear();
      }
   }

   public static class ItemSubmission {
      private final Item requiredItem;
      private final int requiredAmount;
      private int currentAmount = 0;

      public ItemSubmission(Item requiredItem, int requiredAmount) {
         this.requiredItem = requiredItem;
         this.requiredAmount = requiredAmount;
      }

      private static TreasureHuntObjective.ItemSubmission fromConfigEntry(LegacyScavengerHuntConfig.ItemEntry entry, float multiplyAmount) {
         return new TreasureHuntObjective.ItemSubmission(entry.getItem(), Mth.ceil(entry.getRandomAmount() * multiplyAmount));
      }

      public boolean isFinished() {
         return this.currentAmount >= this.requiredAmount;
      }

      public Item getRequiredItem() {
         return this.requiredItem;
      }

      public int getRequiredAmount() {
         return this.requiredAmount;
      }

      public int getCurrentAmount() {
         return this.currentAmount;
      }

      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.putString("item", this.requiredItem.getRegistryName().toString());
         tag.putInt("required", this.requiredAmount);
         tag.putInt("current", this.currentAmount);
         return tag;
      }

      public static TreasureHuntObjective.ItemSubmission deserialize(CompoundTag tag) {
         Item requiredItem = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.getString("item")));
         int requiredAmount = tag.getInt("required");
         int currentAmount = tag.getInt("current");
         TreasureHuntObjective.ItemSubmission submitted = new TreasureHuntObjective.ItemSubmission(requiredItem, requiredAmount);
         submitted.currentAmount = currentAmount;
         return submitted;
      }
   }
}
