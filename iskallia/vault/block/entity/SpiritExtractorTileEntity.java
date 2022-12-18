package iskallia.vault.block.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.config.SpiritConfig;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.entity.IPlayerSkinHolder;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.PlayerSpiritRecoveryData;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SpiritExtractorTileEntity extends BlockEntity implements IPlayerSkinHolder {
   private static final String OWNER_PROFILE_TAG = "ownerProfile";
   private static final String ITEMS_TAG = "items";
   private static final String PAYMENT_STACK_TAG = "paymentStack";
   private static final String TOTAL_COST_TAG = "totalCost";
   private static final String SPIRIT_RECOVERY_COUNT_TAG = "spiritRecoveryCount";
   @Nullable
   private GameProfile gameProfile;
   private final NonNullList<ItemStack> items = NonNullList.create();
   private int vaultLevel;
   private ItemStack totalCost = ItemStack.EMPTY;
   private int spiritRecoveryCount = 0;
   private final OverSizedInventory paymentInventory = new OverSizedInventory(1, this::setChanged, player -> true) {
      public boolean canPlaceItem(int pIndex, ItemStack pStack) {
         return SpiritExtractorTileEntity.this.getTotalCost().getItem() == pStack.getItem();
      }

      @Override
      public int getMaxStackSize() {
         return SpiritExtractorTileEntity.this.getTotalCost().getCount();
      }
   };
   private boolean spewingItems;
   private int itemsPerDrop;
   private long spewingCooldownTime;
   private ResourceLocation skinLocation = null;
   private boolean updatingSkin = false;
   private boolean slimSkin = false;

   public SpiritExtractorTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.SPIRIT_EXTRACTOR_TILE_ENTITY, pos, state);
   }

   public ItemStack getTotalCost() {
      if (!this.spewingItems && this.totalCost.isEmpty()) {
         this.recalculateCost();
      }

      return this.totalCost;
   }

   public int getSpiritRecoveryCount() {
      return this.spiritRecoveryCount;
   }

   public void recalculateCost() {
      if (this.level instanceof ServerLevel serverLevel && this.gameProfile != null) {
         this.spiritRecoveryCount = this.getSpiritRecoveryCountFromData(serverLevel);
         this.totalCost = this.spewingItems
            ? ItemStack.EMPTY
            : this.getLevelCost(this.vaultLevel).map(levelCost -> new ItemStack(levelCost.item, this.calculateCount(levelCost.count))).orElse(ItemStack.EMPTY);
      }
   }

   public int getSpiritRecoveryCountFromData(ServerLevel serverLevel) {
      return this.getGameProfile().map(gp -> PlayerSpiritRecoveryData.get(serverLevel).getSpiritRecoveryCount(gp.getId())).orElse(0);
   }

   private int calculateCount(int levelCostCount) {
      return (int)Math.round(levelCostCount * Math.pow(ModConfigs.SPIRIT.recoveryCostMultiplier, this.spiritRecoveryCount));
   }

   private Optional<SpiritConfig.LevelCost> getLevelCost(int vaultLevel) {
      SpiritConfig.LevelCost ret = null;

      for (SpiritConfig.LevelCost levelCost : ModConfigs.SPIRIT.levelCosts) {
         if (levelCost.minLevel <= vaultLevel && (ret == null || ret.minLevel < levelCost.minLevel)) {
            ret = levelCost;
         }
      }

      return Optional.ofNullable(ret);
   }

   public OverSizedInventory getPaymentInventory() {
      return this.paymentInventory;
   }

   public void setVaultLevel(int vaultLevel) {
      this.vaultLevel = vaultLevel;
      this.setChanged();
   }

   @Override
   public void setGameProfile(GameProfile gameProfile) {
      this.gameProfile = gameProfile;
      this.setChanged();
   }

   @Override
   public Optional<ResourceLocation> getSkinLocation() {
      return Optional.ofNullable(this.skinLocation);
   }

   @Override
   public boolean isUpdatingSkin() {
      return this.updatingSkin;
   }

   @Override
   public void setSkinLocation(ResourceLocation skinLocation) {
      this.skinLocation = skinLocation;
   }

   @Override
   public void startUpdatingSkin() {
      this.updatingSkin = true;
   }

   @Override
   public void stopUpdatingSkin() {
      this.updatingSkin = false;
   }

   @Override
   public boolean hasSlimSkin() {
      return this.slimSkin;
   }

   @Override
   public void setSlimSkin(boolean slimSkin) {
      this.slimSkin = slimSkin;
   }

   @Override
   public Optional<GameProfile> getGameProfile() {
      return Optional.ofNullable(this.gameProfile);
   }

   public void setItems(List<ItemStack> items) {
      this.items.clear();
      this.items.addAll(items);
      this.itemsPerDrop = this.calculateItemsPerDrop();
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, blockEntity -> this.addSpiritRecoveryData(blockEntity.getUpdateTag()));
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   private CompoundTag addSpiritRecoveryData(CompoundTag tag) {
      tag.put("totalCost", this.totalCost.save(new CompoundTag()));
      tag.putInt("spiritRecoveryCount", this.spiritRecoveryCount);
      return tag;
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      if (this.gameProfile != null) {
         tag.put("ownerProfile", NbtUtils.writeGameProfile(new CompoundTag(), this.gameProfile));
      }

      tag.putInt("vaultLevel", this.vaultLevel);
      ListTag itemList = new ListTag();

      for (ItemStack item : this.items) {
         itemList.add(item.save(new CompoundTag()));
      }

      tag.put("items", itemList);
      ItemStack goldStack = this.paymentInventory.getItem(0);
      if (!goldStack.isEmpty()) {
         tag.put("paymentStack", OverSizedItemStack.of(goldStack).serialize());
      }

      tag.putBoolean("spewingItems", this.spewingItems);
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.setGameProfile(tag.contains("ownerProfile") ? NbtUtils.readGameProfile(tag.getCompound("ownerProfile")) : null);
      if (tag.contains("items")) {
         this.items.clear();

         for (Tag itemTag : tag.getList("items", 10)) {
            this.items.add(ItemStack.of((CompoundTag)itemTag));
         }

         this.itemsPerDrop = this.calculateItemsPerDrop();
      }

      this.vaultLevel = tag.getInt("vaultLevel");
      if (tag.contains("paymentStack")) {
         this.paymentInventory.setOverSizedStack(0, OverSizedItemStack.deserialize(tag.getCompound("paymentStack")));
      }

      this.spewingItems = tag.getBoolean("spewingItems");
      if (tag.contains("totalCost", 10)) {
         this.totalCost = ItemStack.of(tag.getCompound("totalCost"));
      }

      if (tag.contains("spiritRecoveryCount")) {
         this.spiritRecoveryCount = tag.getInt("spiritRecoveryCount");
      }
   }

   private int calculateItemsPerDrop() {
      return this.items.size() <= 20 ? 1 : this.items.size() / 20;
   }

   public AABB getRenderBoundingBox() {
      return super.getRenderBoundingBox().expandTowards(0.0, 2.0, 0.0);
   }

   public List<ItemStack> getItems() {
      return this.items;
   }

   public void spewItems() {
      if (!this.spewingItems && this.coinsCoverTotalCost()) {
         if (this.level.isClientSide()) {
            this.spawnParticles();
         } else {
            if (this.level instanceof ServerLevel serverLevel && this.gameProfile != null) {
               PlayerSpiritRecoveryData.get(serverLevel).incrementSpiritRecovery(this.gameProfile.getId());
            }

            this.paymentInventory.setItem(0, ItemStack.EMPTY);
            this.spewingItems = true;
            this.spewingCooldownTime = this.level.getGameTime() + 20L;
         }
      }
   }

   public boolean coinsCoverTotalCost() {
      int totalCost = this.getTotalCost().getCount();
      return totalCost >= 0 && this.paymentInventory.getItem(0).getCount() >= totalCost;
   }

   private void spawnParticles() {
      int numberOfParticles = 15;

      for (int i = 0; i < numberOfParticles; i++) {
         double x = this.getBlockPos().getX() + this.level.random.nextDouble();
         double y = this.getBlockPos().getY() + 0.5 + this.level.random.nextDouble() * 0.5;
         double z = this.getBlockPos().getZ() + this.level.random.nextDouble();
         this.level
            .addParticle(
               ParticleTypes.HAPPY_VILLAGER,
               x,
               y,
               z,
               this.level.random.nextGaussian() * 0.02,
               this.level.random.nextGaussian() * 0.02,
               this.level.random.nextGaussian() * 0.02
            );
      }
   }

   public boolean isSpewingItems() {
      return this.spewingItems;
   }

   public static void tick(Level level, BlockPos pos, BlockState state, SpiritExtractorTileEntity e) {
      if (!level.isClientSide() && e.spewingItems && e.spewingCooldownTime < level.getGameTime()) {
         if (e.items.isEmpty()) {
            e.spewingItems = false;
            e.spewingCooldownTime = 0L;
            e.removeSpirit();
            return;
         }

         for (int i = 0; i < e.itemsPerDrop && !e.items.isEmpty(); i++) {
            int itemIndex = level.random.nextInt(e.items.size());
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, (ItemStack)e.items.remove(itemIndex));
         }

         e.spewingCooldownTime = level.getGameTime() + 2L;
         e.setChanged();
      }
   }

   public int getVaultLevel() {
      return this.vaultLevel;
   }

   public void removeSpirit() {
      this.gameProfile = null;
      this.itemsPerDrop = 0;
      this.vaultLevel = 0;
      this.paymentInventory.setItem(0, ItemStack.EMPTY);
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }
}
