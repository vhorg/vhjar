package iskallia.vault.block.entity;

import com.google.common.collect.Lists;
import com.mojang.math.Vector3f;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.entity.base.HunterHiddenTileEntity;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.ChestGenerationEvent;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.core.world.loot.generator.TieredLootTableGenerator;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.util.calc.ItemQuantityHelper;
import iskallia.vault.util.calc.ItemRarityHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class VaultChestTileEntity extends ChestBlockEntity implements HunterHiddenTileEntity {
   private VaultRarity rarity;
   private boolean generated;
   private int generatedStacksCount;
   private int size;
   private boolean hidden;
   private BlockState renderState;
   private int ticksSinceSync;

   public VaultChestTileEntity(BlockPos pos, BlockState state) {
      this(ModBlocks.VAULT_CHEST_TILE_ENTITY, pos, state);
   }

   protected VaultChestTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
      super(typeIn, pos, state);
      this.size = this.getSize(state);
      this.setItems(NonNullList.withSize(this.size, ItemStack.EMPTY));
   }

   private int getSize(BlockState state) {
      if (state.getBlock() == ModBlocks.TREASURE_CHEST || state.getBlock() == ModBlocks.TREASURE_CHEST_PLACEABLE) {
         return 54;
      } else if (state.getBlock() == ModBlocks.ORNATE_CHEST_PLACEABLE
         || state.getBlock() == ModBlocks.GILDED_CHEST_PLACEABLE
         || state.getBlock() == ModBlocks.ORNATE_STRONGBOX
         || state.getBlock() == ModBlocks.GILDED_STRONGBOX
         || state.getBlock() == ModBlocks.LIVING_STRONGBOX) {
         return 36;
      } else {
         return state.getBlock() != ModBlocks.LIVING_CHEST_PLACEABLE && state.getBlock() != ModBlocks.ALTAR_CHEST_PLACEABLE ? 27 : 45;
      }
   }

   public int getContainerSize() {
      return this.size;
   }

   public int getGeneratedStacksCount() {
      return this.generatedStacksCount;
   }

   @Nullable
   public VaultRarity getRarity() {
      return this.rarity;
   }

   @Override
   public boolean isHidden() {
      return this.hidden;
   }

   @Override
   public void setHidden(boolean hidden) {
      if (this.hidden != (this.hidden = hidden)) {
         this.setChanged();
      }
   }

   public void startOpen(Player player) {
      super.startOpen(player);
      this.playVaultChestSound();
   }

   public static <E extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, VaultChestTileEntity tile) {
      ChestBlockEntity.lidAnimateTick(level, pos, state, tile);
      tile.addParticles();
   }

   public boolean canOpen(Player pPlayer) {
      return this.getBlockState().getBlock() instanceof VaultChestBlock chestBlock && chestBlock.isStrongbox() && !pPlayer.isCreative()
         ? false
         : super.canOpen(pPlayer);
   }

   private void playVaultChestSound() {
      if (this.level != null && this.rarity != null) {
         double x = this.worldPosition.getX() + 0.5;
         double y = this.worldPosition.getY() + 0.5;
         double z = this.worldPosition.getZ() + 0.5;
         switch (this.rarity) {
            case RARE:
               this.level.playSound(null, x, y, z, ModSounds.VAULT_CHEST_RARE_OPEN, SoundSource.BLOCKS, 0.2F, this.level.random.nextFloat() * 0.1F + 0.9F);
               break;
            case EPIC:
               this.level.playSound(null, x, y, z, ModSounds.VAULT_CHEST_EPIC_OPEN, SoundSource.BLOCKS, 0.2F, this.level.random.nextFloat() * 0.1F + 0.9F);
               break;
            case OMEGA:
               this.level.playSound(null, x, y, z, ModSounds.VAULT_CHEST_OMEGA_OPEN, SoundSource.BLOCKS, 0.2F, this.level.random.nextFloat() * 0.1F + 0.9F);
         }
      }
   }

   private void addParticles() {
      if (this.level != null) {
         if (this.rarity != null && this.rarity != VaultRarity.COMMON && this.rarity != VaultRarity.RARE) {
            float xx = this.level.random.nextFloat() * 2.0F - 1.0F;
            float zz = this.level.random.nextFloat() * 2.0F - 1.0F;
            double x = this.worldPosition.getX() + 0.5 + 0.7 * xx;
            double y = this.worldPosition.getY() + this.level.random.nextFloat();
            double z = this.worldPosition.getZ() + 0.5 + 0.7 * zz;
            double xSpeed = this.level.random.nextFloat() * xx;
            double ySpeed = (this.level.random.nextFloat() - 0.5) * 0.25;
            double zSpeed = this.level.random.nextFloat() * zz;
            float red = this.rarity == VaultRarity.EPIC ? 1.0F : 0.0F;
            float green = this.rarity == VaultRarity.OMEGA ? 1.0F : 0.0F;
            float blue = this.rarity == VaultRarity.EPIC ? 1.0F : 0.0F;
            this.level.addParticle(new DustParticleOptions(new Vector3f(red, green, blue), 1.0F), x, y, z, xSpeed, ySpeed, zSpeed);
         }
      }
   }

   public void unpackLootTable(Player player) {
      this.generateChestLoot(player, false);
   }

   public void generateChestLoot(Player source, boolean compress) {
      if (this.getLevel() != null && !this.getLevel().isClientSide() && source instanceof ServerPlayer player && !this.generated) {
         if (!MiscUtils.isPlayerFakeMP(player) && !source.isSpectator()) {
            List<ItemStack> loot = new ArrayList<>();
            ChestGenerationEvent.Data data = CommonEvents.CHEST_LOOT_GENERATION
               .invoke(
                  player,
                  this.getBlockState(),
                  this.getBlockPos(),
                  this.lootTable,
                  this,
                  loot,
                  this.rarity,
                  Version.latest(),
                  JavaRandom.ofNanoTime(),
                  ChestGenerationEvent.Phase.PRE
               );
            if ((this.lootTable = data.getLootTable()) == null) {
               this.generated = true;
               this.generatedStacksCount = 0;
               this.setChanged();
            } else {
               this.generateLootTable(data.getVersion(), source, loot, data.getRandom());
               CommonEvents.CHEST_LOOT_GENERATION
                  .invoke(
                     player,
                     this.getBlockState(),
                     this.getBlockPos(),
                     this.lootTable,
                     this,
                     loot,
                     this.rarity,
                     data.getVersion(),
                     data.getRandom(),
                     ChestGenerationEvent.Phase.POST
                  );
               this.fillLoot(loot, compress, data.getRandom());
               this.generated = true;
               this.generatedStacksCount = 0;

               for (int i = 0; i < this.getContainerSize(); i++) {
                  this.generatedStacksCount = this.generatedStacksCount + (this.getItem(i).isEmpty() ? 1 : 0);
               }

               this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
            }
         } else {
            this.generated = true;
            this.generatedStacksCount = 0;
            this.setChanged();
         }
      }
   }

   private void generateLootTable(Version version, @Nullable Player player, List<ItemStack> loot, RandomSource random) {
      float quantity = ItemQuantityHelper.getItemQuantity(player);
      float rarity = ItemRarityHelper.getItemRarity(player);
      LootTableKey key = VaultRegistry.LOOT_TABLE.getKey(this.lootTable);
      if (key != null) {
         if (this.getBlockState().is(ModBlocks.TREASURE_CHEST)) {
            LootTableGenerator generator = new LootTableGenerator(version, key, 0.0F);
            generator.source = player;
            generator.generate(random);
            this.rarity = VaultRarity.COMMON;
            generator.getItems().forEachRemaining(loot::add);
         } else {
            TieredLootTableGenerator generator = new TieredLootTableGenerator(version, key, rarity, quantity, 54);
            generator.source = player;
            generator.generate(random);
            this.rarity = ModConfigs.VAULT_CHEST.getRarity(generator.getCDF());
            generator.getItems().forEachRemaining(loot::add);
         }
      } else {
         this.rarity = VaultRarity.COMMON;
      }

      this.setChanged();
   }

   public void fillLoot(List<ItemStack> loot, boolean compress, RandomSource random) {
      if (compress) {
         List<ItemStack> mergedLoot = MiscUtils.splitAndLimitStackSize(MiscUtils.mergeItemStacks(loot));
         mergedLoot.forEach(stack -> MiscUtils.addItemStack(this, stack));
         this.setChanged();
      } else {
         if (loot.size() > this.size) {
            loot = MiscUtils.splitAndLimitStackSize(MiscUtils.mergeItemStacks(loot));
         }

         List<Integer> slots = this.getAvailableSlots(this, random);
         this.shuffleAndSplitItems(loot, slots.size(), random);

         for (ItemStack itemstack : loot) {
            if (slots.isEmpty()) {
               break;
            }

            this.setItem(slots.remove(slots.size() - 1), itemstack.isEmpty() ? ItemStack.EMPTY : itemstack);
         }

         this.setChanged();
      }
   }

   private List<Integer> getAvailableSlots(Container pInventory, RandomSource pRand) {
      List<Integer> list = Lists.newArrayList();

      for (int i = 0; i < pInventory.getContainerSize(); i++) {
         if (pInventory.getItem(i).isEmpty()) {
            list.add(i);
         }
      }

      for (int ix = list.size(); ix > 1; ix--) {
         int index = pRand.nextInt(ix);
         int temp = list.get(ix - 1);
         list.set(ix - 1, list.get(index));
         list.set(index, temp);
      }

      return list;
   }

   private void shuffleAndSplitItems(List<ItemStack> pStacks, int pEmptySlotsCount, RandomSource random) {
      List<ItemStack> list = Lists.newArrayList();
      Iterator<ItemStack> iterator = pStacks.iterator();

      while (iterator.hasNext()) {
         ItemStack itemstack = iterator.next();
         if (itemstack.isEmpty()) {
            iterator.remove();
         } else if (itemstack.getCount() > 1) {
            list.add(itemstack);
            iterator.remove();
         }
      }

      while (pEmptySlotsCount - pStacks.size() - list.size() > 0 && !list.isEmpty()) {
         ItemStack itemstack2 = list.remove(random.nextInt(list.size()));
         int i = random.nextInt(1, itemstack2.getCount() / 2 + 1);
         ItemStack itemstack1 = itemstack2.split(i);
         if (itemstack2.getCount() > 1 && random.nextBoolean()) {
            list.add(itemstack2);
         } else {
            pStacks.add(itemstack2);
         }

         if (itemstack1.getCount() > 1 && random.nextBoolean()) {
            list.add(itemstack1);
         } else {
            pStacks.add(itemstack1);
         }
      }

      pStacks.addAll(list);

      for (int ix = list.size(); ix > 1; ix--) {
         int index = random.nextInt(ix);
         ItemStack temp = pStacks.get(ix - 1);
         pStacks.set(ix - 1, pStacks.get(index));
         pStacks.set(index, temp);
      }
   }

   public void setItem(int index, ItemStack stack) {
      super.setItem(index, stack);
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public ItemStack removeItem(int index, int count) {
      ItemStack stack = super.removeItem(index, count);
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      return stack;
   }

   public ItemStack removeItemNoUpdate(int index) {
      ItemStack stack = super.removeItemNoUpdate(index);
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      return stack;
   }

   public BlockState getBlockState() {
      return this.renderState != null ? this.renderState : super.getBlockState();
   }

   protected boolean tryLoadLootTable(CompoundTag nbt) {
      super.tryLoadLootTable(nbt);
      return false;
   }

   protected boolean trySaveLootTable(CompoundTag nbt) {
      super.trySaveLootTable(nbt);
      return false;
   }

   public void load(@NotNull CompoundTag nbt) {
      super.load(nbt);
      if (nbt.contains("Rarity", 3)) {
         this.rarity = VaultRarity.values()[nbt.getInt("Rarity")];
      }

      this.generated = nbt.getBoolean("Generated");
      this.generatedStacksCount = nbt.getInt("GeneratedStacksCount");
      this.hidden = nbt.getBoolean("Hidden");
   }

   protected void saveAdditional(@NotNull CompoundTag nbt) {
      super.saveAdditional(nbt);
      if (this.rarity != null) {
         nbt.putInt("Rarity", this.rarity.ordinal());
      }

      nbt.putBoolean("Generated", this.generated);
      nbt.putInt("GeneratedStacksCount", this.generatedStacksCount);
      nbt.putBoolean("Hidden", this.hidden);
   }

   public Component getDisplayName() {
      if (this.rarity != null) {
         String rarity = StringUtils.capitalize(this.rarity.name().toLowerCase());
         BlockState state = this.getBlockState();
         if (state.getBlock() == ModBlocks.WOODEN_CHEST || state.getBlock() == ModBlocks.WOODEN_CHEST_PLACEABLE) {
            return new TextComponent(rarity + " Wooden Chest");
         }

         if (state.getBlock() == ModBlocks.GILDED_CHEST || state.getBlock() == ModBlocks.GILDED_CHEST_PLACEABLE) {
            return new TextComponent(rarity + " Gilded Chest");
         }

         if (state.getBlock() == ModBlocks.LIVING_CHEST || state.getBlock() == ModBlocks.LIVING_CHEST_PLACEABLE) {
            return new TextComponent(rarity + " Living Chest");
         }

         if (state.getBlock() == ModBlocks.ORNATE_CHEST || state.getBlock() == ModBlocks.ORNATE_CHEST_PLACEABLE) {
            return new TextComponent(rarity + " Ornate Chest");
         }

         if (state.getBlock() == ModBlocks.TREASURE_CHEST || state.getBlock() == ModBlocks.TREASURE_CHEST_PLACEABLE) {
            return new TextComponent("Treasure Chest");
         }

         if (state.getBlock() == ModBlocks.ALTAR_CHEST || state.getBlock() == ModBlocks.ALTAR_CHEST_PLACEABLE) {
            return new TextComponent(rarity + " Altar Chest");
         }

         if (state.getBlock() == ModBlocks.ORNATE_STRONGBOX) {
            return new TextComponent(rarity + " Ornate Strongbox");
         }

         if (state.getBlock() == ModBlocks.GILDED_STRONGBOX) {
            return new TextComponent(rarity + " Gilded Strongbox");
         }

         if (state.getBlock() == ModBlocks.LIVING_STRONGBOX) {
            return new TextComponent(rarity + " Living Strongbox");
         }
      }

      return super.getDisplayName();
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
