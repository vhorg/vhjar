package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.Vault;
import iskallia.vault.config.ScavengerHuntConfig;
import iskallia.vault.config.VaultModifiersConfig;
import iskallia.vault.container.ScavengerChestContainer;
import iskallia.vault.entity.VaultSandEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.BasicScavengerItem;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.layout.SquareRoomLayout;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.ArtifactChanceModifier;
import iskallia.vault.world.vault.modifier.InventoryRestoreModifier;
import iskallia.vault.world.vault.modifier.NoExitModifier;
import iskallia.vault.world.vault.modifier.TimerModifier;
import iskallia.vault.world.vault.modifier.VaultModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.time.extension.SandExtension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

public class TreasureHuntObjective extends VaultObjective {
   public static final int INVENTORY_SIZE = 45;
   private final TreasureHuntObjective.ChestWatcher chestWatcher = new TreasureHuntObjective.ChestWatcher();
   private final TreasureHuntObjective.Inventory inventoryMirror = new TreasureHuntObjective.Inventory();
   private final List<TreasureHuntObjective.ItemSubmission> submissions = new ArrayList<>();
   private int requiredSubmissions;
   private int addedSand;
   private int sandPerModifier = -1;
   private NonNullList<ItemStack> chestInventory = NonNullList.func_191197_a(45, ItemStack.field_190927_a);
   private ResourceLocation roomPool = Vault.id("raid/rooms");
   private ResourceLocation tunnelPool = Vault.id("vault/tunnels");

   public TreasureHuntObjective(ResourceLocation id) {
      this(id, ModConfigs.TREASURE_HUNT.getTotalRequiredItems());
   }

   private TreasureHuntObjective(ResourceLocation id, int requiredSubmissions) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
      this.requiredSubmissions = requiredSubmissions;
   }

   public IInventory getScavengerChestInventory() {
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

   public Predicate<ScavengerHuntConfig.ItemEntry> getGenerationDropFilter() {
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

   public void setSandPerModifier(int sandPerModifier) {
      this.sandPerModifier = sandPerModifier;
   }

   public void setRoomPool(ResourceLocation roomPool) {
      this.roomPool = roomPool;
   }

   public void setTunnelPool(ResourceLocation tunnelPool) {
      this.tunnelPool = tunnelPool;
   }

   @Nullable
   @Override
   public VaultRoomLayoutGenerator getCustomLayout() {
      SquareRoomLayout layout = new SquareRoomLayout(19);
      layout.setRoomId(this.roomPool);
      layout.setTunnelId(this.tunnelPool);
      return layout;
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
   public ITextComponent getObjectiveTargetDescription(int amount) {
      return new StringTextComponent("Total required Item Types: ")
         .func_230529_a_(new StringTextComponent(String.valueOf(amount)).func_240699_a_(TextFormatting.GREEN));
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerWorld world, BlockPos pos) {
      return ModBlocks.SCAVENGER_CHEST.func_176223_P();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      return null;
   }

   @Override
   public ITextComponent getObjectiveDisplayName() {
      return new StringTextComponent("Treasure Hunt").func_240699_a_(TextFormatting.GREEN);
   }

   @Override
   public ITextComponent getVaultName() {
      return new StringTextComponent("Treasure Vault");
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.func_73046_m();
      vault.getPlayers().stream().filter(vPlayer -> filter.test(vPlayer.getPlayerId())).forEach(vPlayer -> vPlayer.runIfPresent(srv, playerEntity -> {
         VaultGoalMessage pkt = VaultGoalMessage.treasureHunt(this.getActiveSubmissions());
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

         if (this.getAllSubmissions().size() < this.requiredSubmissions) {
            TreasureHuntObjective.ItemSubmission newEntry = this.getNewEntry(vault);
            if (newEntry != null) {
               this.submissions.add(newEntry);
            }
         }

         vault.getPlayers()
            .stream()
            .map(vPlayer -> vPlayer.getServerPlayer(world.func_73046_m()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(player -> vault.getGenerator().getPiecesAt(player.func_233580_cy_()))
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

   public void spawnSand(VaultRaid vault, ServerWorld world, VaultRoom room) {
      for (int i = 0; i < 200; i++) {
         int x = rand.nextInt(room.getBoundingBox().field_78893_d - room.getBoundingBox().field_78897_a + 1) + room.getBoundingBox().field_78897_a;
         int y = rand.nextInt(room.getBoundingBox().field_78894_e - room.getBoundingBox().field_78895_b + 1) + room.getBoundingBox().field_78895_b;
         int z = rand.nextInt(room.getBoundingBox().field_78892_f - room.getBoundingBox().field_78896_c + 1) + room.getBoundingBox().field_78896_c;
         BlockPos pos = new BlockPos(x, y, z);
         if (world.isAreaLoaded(pos, 1)) {
            BlockState state = world.func_180495_p(pos);
            if (state.isAir(world, pos) && state.isAir(world, pos.func_177977_b()) && state.isAir(world, pos.func_177984_a())) {
               VaultSandEntity sand = VaultSandEntity.create(world, pos);
               ItemStack stack = sand.func_92059_d();
               stack.func_196082_o().func_186854_a("vault_id", vault.getProperties().getValue(VaultRaid.IDENTIFIER));
               sand.func_92058_a(stack);
               room.addSandId(sand.func_110124_au());
               world.func_217376_c(sand);
               return;
            }
         }
      }
   }

   public void depositSand(VaultRaid vault, ServerPlayerEntity player, int amount) {
      long extraTime = (long)ModConfigs.TREASURE_HUNT.ticksPerSand * amount;
      SandExtension extension = new SandExtension(player.func_110124_au(), amount, extraTime);
      vault.getPlayers().forEach(vPlayer -> vPlayer.getTimer().addTime(extension, 0));
      if (this.sandPerModifier > 0) {
         for (int i = 1; i <= amount; i++) {
            int count = this.addedSand + i;
            if (count % this.sandPerModifier == 0) {
               this.addRandomModifier(vault, player.func_71121_q(), player);
            }
         }
      }

      this.addedSand += amount;
   }

   private void addRandomModifier(VaultRaid vault, ServerWorld sWorld, ServerPlayerEntity player) {
      int level = vault.getProperties().getValue(VaultRaid.LEVEL);
      Set<VaultModifier> modifiers = ModConfigs.VAULT_MODIFIERS.getRandom(rand, level, VaultModifiersConfig.ModifierPoolType.FINAL_WENDARR_ADDS, null);
      modifiers.removeIf(mod -> mod instanceof NoExitModifier);
      modifiers.removeIf(mod -> mod instanceof TimerModifier);
      modifiers.removeIf(mod -> mod instanceof InventoryRestoreModifier);
      if (sWorld.func_201674_k().nextFloat() < 0.65F) {
         modifiers.removeIf(mod -> mod instanceof ArtifactChanceModifier);
      }

      List<VaultModifier> modifierList = new ArrayList<>(modifiers);
      Collections.shuffle(modifierList);
      VaultModifier modifier = MiscUtils.getRandomEntry(modifierList, rand);
      if (modifier != null) {
         ITextComponent c0 = player.func_145748_c_().func_230532_e_().func_240699_a_(TextFormatting.LIGHT_PURPLE);
         ITextComponent c1 = new StringTextComponent(" deposited ").func_240699_a_(TextFormatting.GRAY);
         ITextComponent c2 = new StringTextComponent("sand").func_240699_a_(TextFormatting.YELLOW);
         ITextComponent c3 = new StringTextComponent(" and added ").func_240699_a_(TextFormatting.GRAY);
         ITextComponent c4 = modifier.getNameComponent();
         ITextComponent c5 = new StringTextComponent(".").func_240699_a_(TextFormatting.GRAY);
         ITextComponent ct = new StringTextComponent("")
            .func_230529_a_(c0)
            .func_230529_a_(c1)
            .func_230529_a_(c2)
            .func_230529_a_(c3)
            .func_230529_a_(c4)
            .func_230529_a_(c5);
         vault.getModifiers().addPermanentModifier(modifier);
         vault.getPlayers().forEach(vPlayer -> {
            modifier.apply(vault, vPlayer, sWorld, sWorld.func_201674_k());
            vPlayer.runIfPresent(sWorld.func_73046_m(), sPlayer -> sPlayer.func_145747_a(ct, Util.field_240973_b_));
         });
      }
   }

   public int getAddedSand() {
      return this.addedSand;
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

   @Nullable
   private TreasureHuntObjective.ItemSubmission getNewEntry(VaultRaid vault) {
      List<Item> currentItems = this.submissions.stream().map(submission -> submission.requiredItem).collect(Collectors.toList());
      int players = vault.getPlayers().size();
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      float multiplier = 1.0F + (players - 1) * 0.5F;
      ScavengerHuntConfig.ItemEntry newEntry = ModConfigs.TREASURE_HUNT.getRandomRequiredItem(currentItems::contains);
      if (newEntry == null) {
         return null;
      } else {
         ScavengerHuntConfig.SourceType sourceType = ModConfigs.TREASURE_HUNT.getRequirementSource(newEntry.createItemStack());
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
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      ListNBT list = new ListNBT();

      for (TreasureHuntObjective.ItemSubmission submission : this.submissions) {
         list.add(submission.serialize());
      }

      tag.func_218657_a("submissions", list);
      tag.func_74768_a("requiredSubmissions", this.requiredSubmissions);
      tag.func_74768_a("sandPerModifier", this.sandPerModifier);
      tag.func_74768_a("addedSand", this.addedSand);
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
      tag.func_74778_a("roomPool", this.roomPool.toString());
      tag.func_74778_a("tunnelPool", this.tunnelPool.toString());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.submissions.clear();
      ListNBT list = tag.func_150295_c("submissions", 10);

      for (int index = 0; index < list.size(); index++) {
         this.submissions.add(TreasureHuntObjective.ItemSubmission.deserialize(list.func_150305_b(index)));
      }

      this.requiredSubmissions = tag.func_74762_e("requiredSubmissions");
      this.sandPerModifier = tag.func_74762_e("sandPerModifier");
      this.addedSand = tag.func_74762_e("addedSand");
      this.chestInventory = NonNullList.func_191197_a(45, ItemStack.field_190927_a);
      ListNBT inventoryList = tag.func_150295_c("inventory", 10);

      for (int i = 0; i < inventoryList.size(); i++) {
         CompoundNBT itemTag = inventoryList.func_150305_b(i);
         int slot = itemTag.func_74762_e("slot");
         ItemStack stack = ItemStack.func_199557_a(itemTag.func_74775_l("item"));
         this.chestInventory.set(slot, stack);
      }

      if (tag.func_150297_b("roomPool", 8)) {
         this.roomPool = new ResourceLocation(tag.func_74779_i("roomPool"));
      }

      if (tag.func_150297_b("tunnelPool", 8)) {
         this.tunnelPool = new ResourceLocation(tag.func_74779_i("tunnelPool"));
      }
   }

   public class ChestWatcher implements IContainerListener {
      public void func_71110_a(Container container, NonNullList<ItemStack> items) {
      }

      public void func_71111_a(Container container, int slotId, ItemStack stack) {
         if (slotId >= 0 && slotId < 45) {
            TreasureHuntObjective.this.chestInventory.set(slotId, stack);
         }
      }

      public void func_71112_a(Container containerIn, int varToUpdate, int newValue) {
      }
   }

   public static class Config {
      private IntSupplier requiredSubmissionsGen;
      private Function<Predicate<Item>, ScavengerHuntConfig.ItemEntry> itemGen;
      private Function<ItemStack, ScavengerHuntConfig.SourceType> sourceGen;

      public Config(
         IntSupplier requiredSubmissionsGen,
         Function<Predicate<Item>, ScavengerHuntConfig.ItemEntry> itemGen,
         Function<ItemStack, ScavengerHuntConfig.SourceType> sourceGen
      ) {
         this.requiredSubmissionsGen = requiredSubmissionsGen;
         this.itemGen = itemGen;
         this.sourceGen = sourceGen;
      }
   }

   private class Inventory implements IInventory {
      private Inventory() {
      }

      public int func_70302_i_() {
         return TreasureHuntObjective.this.chestInventory.size();
      }

      public boolean func_191420_l() {
         return TreasureHuntObjective.this.chestInventory.isEmpty();
      }

      public ItemStack func_70301_a(int index) {
         return (ItemStack)TreasureHuntObjective.this.chestInventory.get(index);
      }

      public ItemStack func_70298_a(int index, int count) {
         return ItemStackHelper.func_188382_a(TreasureHuntObjective.this.chestInventory, index, count);
      }

      public ItemStack func_70304_b(int index) {
         ItemStack existing = this.func_70301_a(index);
         this.func_70299_a(index, ItemStack.field_190927_a);
         return existing;
      }

      public void func_70299_a(int index, ItemStack stack) {
         TreasureHuntObjective.this.chestInventory.set(index, stack);
      }

      public void func_70296_d() {
      }

      public boolean func_70300_a(PlayerEntity player) {
         return true;
      }

      public void func_174888_l() {
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

      private static TreasureHuntObjective.ItemSubmission fromConfigEntry(ScavengerHuntConfig.ItemEntry entry, float multiplyAmount) {
         return new TreasureHuntObjective.ItemSubmission(entry.getItem(), MathHelper.func_76123_f(entry.getRandomAmount() * multiplyAmount));
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

      public static TreasureHuntObjective.ItemSubmission deserialize(CompoundNBT tag) {
         Item requiredItem = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.func_74779_i("item")));
         int requiredAmount = tag.func_74762_e("required");
         int currentAmount = tag.func_74762_e("current");
         TreasureHuntObjective.ItemSubmission submitted = new TreasureHuntObjective.ItemSubmission(requiredItem, requiredAmount);
         submitted.currentAmount = currentAmount;
         return submitted;
      }
   }
}
