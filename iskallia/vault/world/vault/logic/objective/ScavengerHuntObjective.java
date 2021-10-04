package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.LootTablesConfig;
import iskallia.vault.config.ScavengerHuntConfig;
import iskallia.vault.container.ScavengerChestContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.BasicScavengerItem;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

public class ScavengerHuntObjective extends VaultObjective {
   public static final int INVENTORY_SIZE = 45;
   private static final int MAX_ACTIVE_SUBMISSIONS = 5;
   private final ScavengerHuntObjective.ChestWatcher chestWatcher = new ScavengerHuntObjective.ChestWatcher();
   private final ScavengerHuntObjective.Inventory inventoryMirror = new ScavengerHuntObjective.Inventory();
   private final List<ScavengerHuntObjective.ItemSubmission> submissions = new ArrayList<>();
   private int requiredSubmissions;
   private NonNullList<ItemStack> chestInventory = NonNullList.func_191197_a(45, ItemStack.field_190927_a);

   public ScavengerHuntObjective(ResourceLocation id) {
      this(id, ModConfigs.SCAVENGER_HUNT.getTotalRequiredItems());
   }

   private ScavengerHuntObjective(ResourceLocation id, int requiredSubmissions) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
      this.requiredSubmissions = requiredSubmissions;
   }

   public IInventory getScavengerChestInventory() {
      return this.inventoryMirror;
   }

   public ScavengerHuntObjective.ChestWatcher getChestWatcher() {
      return this.chestWatcher;
   }

   private Stream<ScavengerHuntObjective.ItemSubmission> getActiveSubmissionsFilter() {
      return this.getAllSubmissions().stream().filter(submission -> !submission.isFinished()).limit(5L);
   }

   public List<ScavengerHuntObjective.ItemSubmission> getActiveSubmissions() {
      return this.getActiveSubmissionsFilter().collect(Collectors.toList());
   }

   public List<ScavengerHuntObjective.ItemSubmission> getAllSubmissions() {
      return Collections.unmodifiableList(this.submissions);
   }

   public boolean trySubmitItem(UUID vaultIdentifier, ItemStack stack) {
      if (stack.func_190926_b()) {
         return false;
      } else if (!vaultIdentifier.equals(BasicScavengerItem.getVaultIdentifier(stack))) {
         return false;
      } else {
         Item providedItem = stack.func_77973_b();
         boolean addedItem = this.getActiveSubmissionsFilter()
            .filter(submission -> providedItem.equals(submission.requiredItem))
            .findFirst()
            .map(submission -> {
               int add = Math.min(stack.func_190916_E(), submission.requiredAmount - submission.currentAmount);
               submission.currentAmount = submission.currentAmount + add;
               stack.func_190918_g(add);
               return true;
            })
            .orElse(false);
         if (this.getAllSubmissions().stream().filter(ScavengerHuntObjective.ItemSubmission::isFinished).count() >= this.requiredSubmissions) {
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
   public ITextComponent getObjectiveTargetDescription(int amount) {
      return new StringTextComponent("Total required Item Types: ")
         .func_230529_a_(new StringTextComponent(String.valueOf(amount)).func_240699_a_(TextFormatting.GREEN));
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock() {
      return ModBlocks.SCAVENGER_CHEST.func_176223_P();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      return config != null ? tblResolver.apply(config.getScavengerCrate()) : LootTable.field_186464_a;
   }

   @Override
   public ITextComponent getObjectiveDisplayName() {
      return new StringTextComponent("Scavenger Hunt").func_240699_a_(TextFormatting.GREEN);
   }

   @Override
   public ITextComponent getVaultName() {
      return new StringTextComponent("Scavenger Vault");
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.func_73046_m();
      vault.getPlayers().stream().filter(vPlayer -> filter.test(vPlayer.getPlayerId())).forEach(vPlayer -> vPlayer.runIfPresent(srv, playerEntity -> {
         VaultGoalMessage pkt = VaultGoalMessage.scavengerHunt(this.getActiveSubmissions());
         ModNetwork.CHANNEL.sendTo(pkt, playerEntity.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
      }));
      if (!this.isCompleted()) {
         long activeSubmissions = this.getActiveSubmissionsFilter().count();
         if (world.func_82737_E() % 20L == 0L) {
            boolean addedAnyItem = vault.getProperties().getBase(VaultRaid.IDENTIFIER).map(identifier -> {
               boolean addedItem = false;
               NonNullList<ItemStack> inventory = this.chestInventory;

               for (int slot = 0; slot < inventory.size(); slot++) {
                  ItemStack stack = (ItemStack)inventory.get(slot);
                  if (!stack.func_190926_b() && this.trySubmitItem(identifier, stack)) {
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
                        srv, sPlayer -> world.func_184133_a(null, sPlayer.func_233580_cy_(), SoundEvents.field_187802_ec, SoundCategory.BLOCKS, 1.0F, 1.0F)
                     )
                  );
            } else if (addedAnyItem) {
               vault.getPlayers()
                  .forEach(
                     vPlayer -> vPlayer.runIfPresent(
                        srv, sPlayer -> world.func_184133_a(null, sPlayer.func_233580_cy_(), SoundEvents.field_193807_ew, SoundCategory.PLAYERS, 1.1F, 1.4F)
                     )
                  );
            }
         }

         if (this.isCompleted()) {
            this.spawnRewards(world, vault);
         }

         if (activeSubmissions < 5L && this.getAllSubmissions().size() < this.requiredSubmissions) {
            ScavengerHuntObjective.ItemSubmission newEntry = this.getNewEntry(vault);
            if (newEntry != null) {
               this.submissions.add(newEntry);
            }
         }
      }
   }

   private void updateOpenContainers(MinecraftServer srv, VaultRaid vault, int slot, ItemStack stack) {
      vault.getPlayers().forEach(vPlayer -> vPlayer.runIfPresent(srv, sPlayer -> {
         if (sPlayer.field_71070_bA instanceof ScavengerChestContainer) {
            sPlayer.field_71070_bA.func_75141_a(slot, stack);
            sPlayer.field_71135_a.func_147359_a(new SSetSlotPacket(sPlayer.field_71070_bA.field_75152_c, slot, stack));
         }
      }));
   }

   @Override
   public void complete(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      super.complete(vault, player, world);
      player.sendIfPresent(world.func_73046_m(), VaultGoalMessage.clear());
   }

   @Override
   public void complete(VaultRaid vault, ServerWorld world) {
      super.complete(vault, world);
      vault.getPlayers().forEach(player -> player.sendIfPresent(world.func_73046_m(), VaultGoalMessage.clear()));
   }

   public void spawnRewards(ServerWorld world, VaultRaid vault) {
      VaultPlayer rewardPlayer = vault.getProperties()
         .getBase(VaultRaid.HOST)
         .flatMap(vault::getPlayer)
         .filter(vPlayer -> vPlayer instanceof VaultRunner)
         .orElseGet(() -> vault.getPlayers().stream().filter(vPlayer -> vPlayer instanceof VaultRunner).findAny().orElse(null));
      if (rewardPlayer != null) {
         rewardPlayer.runIfPresent(
            world.func_73046_m(),
            sPlayer -> {
               BlockPos pos = sPlayer.func_233580_cy_();
               Builder builder = new Builder(world)
                  .func_216023_a(world.field_73012_v)
                  .func_216015_a(LootParameters.field_216281_a, sPlayer)
                  .func_216015_a(LootParameters.field_237457_g_, Vector3d.func_237489_a_(pos))
                  .func_186469_a(sPlayer.func_184817_da());
               LootContext ctx = builder.func_216022_a(LootParameterSets.field_216261_b);
               this.dropRewardCrate(world, vault, pos, ctx);
               float additionalChance = vault.getPlayers().size() <= 1 ? 0.0F : Math.min(vault.getPlayers().size() * 0.5F, 1.0F);
               if (world.field_73012_v.nextFloat() < additionalChance) {
                  this.dropRewardCrate(world, vault, pos, ctx);
               }

               IFormattableTextComponent msgContainer = new StringTextComponent("").func_240699_a_(TextFormatting.WHITE);
               IFormattableTextComponent playerName = sPlayer.func_145748_c_().func_230532_e_();
               playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
               ITextComponent msg = msgContainer.func_230529_a_(playerName).func_240702_b_(" finished a Scavenger Hunt!");
               world.func_73046_m().func_184103_al().func_232641_a_(msg, ChatType.CHAT, Util.field_240973_b_);
            }
         );
      }
   }

   private void dropRewardCrate(ServerWorld world, VaultRaid vault, BlockPos pos, LootContext context) {
      NonNullList<ItemStack> stacks = this.createLoot(world, vault, context);
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(ModBlocks.VAULT_CRATE_SCAVENGER, stacks);
      ItemEntity item = new ItemEntity(world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), crate);
      item.func_174869_p();
      world.func_217376_c(item);
      this.crates.add(new VaultObjective.Crate(stacks));
   }

   @Nullable
   private ScavengerHuntObjective.ItemSubmission getNewEntry(VaultRaid vault) {
      List<Item> currentItems = this.submissions.stream().map(submission -> submission.requiredItem).collect(Collectors.toList());
      int players = vault.getPlayers().size();
      float multiplier = 1.0F + (players - 1) * 0.5F;
      ScavengerHuntConfig.ItemEntry newEntry = ModConfigs.SCAVENGER_HUNT.getRandomRequiredItem(currentItems::contains);
      return newEntry == null ? null : ScavengerHuntObjective.ItemSubmission.fromConfigEntry(newEntry, multiplier);
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      ListNBT list = new ListNBT();

      for (ScavengerHuntObjective.ItemSubmission submission : this.submissions) {
         list.add(submission.serialize());
      }

      tag.func_218657_a("submissions", list);
      tag.func_74768_a("requiredSubmissions", this.requiredSubmissions);
      ListNBT inventoryList = new ListNBT();

      for (int slot = 0; slot < this.chestInventory.size(); slot++) {
         ItemStack stack = (ItemStack)this.chestInventory.get(slot);
         if (!stack.func_190926_b()) {
            CompoundNBT itemTag = new CompoundNBT();
            itemTag.func_74768_a("slot", slot);
            itemTag.func_218657_a("item", stack.serializeNBT());
            inventoryList.add(itemTag);
         }
      }

      tag.func_218657_a("inventory", inventoryList);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.submissions.clear();
      ListNBT list = tag.func_150295_c("submissions", 10);

      for (int index = 0; index < list.size(); index++) {
         this.submissions.add(ScavengerHuntObjective.ItemSubmission.deserialize(list.func_150305_b(index)));
      }

      this.requiredSubmissions = tag.func_74762_e("requiredSubmissions");
      this.chestInventory = NonNullList.func_191197_a(45, ItemStack.field_190927_a);
      ListNBT inventoryList = tag.func_150295_c("inventory", 10);

      for (int i = 0; i < inventoryList.size(); i++) {
         CompoundNBT itemTag = inventoryList.func_150305_b(i);
         int slot = itemTag.func_74762_e("slot");
         ItemStack stack = ItemStack.func_199557_a(itemTag.func_74775_l("item"));
         this.chestInventory.set(slot, stack);
      }
   }

   public class ChestWatcher implements IContainerListener {
      public void func_71110_a(Container container, NonNullList<ItemStack> items) {
      }

      public void func_71111_a(Container container, int slotId, ItemStack stack) {
         if (slotId >= 0 && slotId < 45) {
            ScavengerHuntObjective.this.chestInventory.set(slotId, stack);
         }
      }

      public void func_71112_a(Container containerIn, int varToUpdate, int newValue) {
      }
   }

   private class Inventory implements IInventory {
      private Inventory() {
      }

      public int func_70302_i_() {
         return ScavengerHuntObjective.this.chestInventory.size();
      }

      public boolean func_191420_l() {
         return ScavengerHuntObjective.this.chestInventory.isEmpty();
      }

      public ItemStack func_70301_a(int index) {
         return (ItemStack)ScavengerHuntObjective.this.chestInventory.get(index);
      }

      public ItemStack func_70298_a(int index, int count) {
         return ItemStackHelper.func_188382_a(ScavengerHuntObjective.this.chestInventory, index, count);
      }

      public ItemStack func_70304_b(int index) {
         ItemStack existing = this.func_70301_a(index);
         this.func_70299_a(index, ItemStack.field_190927_a);
         return existing;
      }

      public void func_70299_a(int index, ItemStack stack) {
         ScavengerHuntObjective.this.chestInventory.set(index, stack);
      }

      public void func_70296_d() {
      }

      public boolean func_70300_a(PlayerEntity player) {
         return true;
      }

      public void func_174888_l() {
         ScavengerHuntObjective.this.chestInventory.clear();
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

      private static ScavengerHuntObjective.ItemSubmission fromConfigEntry(ScavengerHuntConfig.ItemEntry entry, float multiplyAmount) {
         return new ScavengerHuntObjective.ItemSubmission(entry.getItem(), MathHelper.func_76123_f(entry.getRandomAmount() * multiplyAmount));
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

      public CompoundNBT serialize() {
         CompoundNBT tag = new CompoundNBT();
         tag.func_74778_a("item", this.requiredItem.getRegistryName().toString());
         tag.func_74768_a("required", this.requiredAmount);
         tag.func_74768_a("current", this.currentAmount);
         return tag;
      }

      public static ScavengerHuntObjective.ItemSubmission deserialize(CompoundNBT tag) {
         Item requiredItem = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.func_74779_i("item")));
         int requiredAmount = tag.func_74762_e("required");
         int currentAmount = tag.func_74762_e("current");
         ScavengerHuntObjective.ItemSubmission submitted = new ScavengerHuntObjective.ItemSubmission(requiredItem, requiredAmount);
         submitted.currentAmount = currentAmount;
         return submitted;
      }
   }
}
