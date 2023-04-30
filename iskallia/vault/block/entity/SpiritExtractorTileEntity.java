package iskallia.vault.block.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.config.SpiritConfig;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.entity.IPlayerSkinHolder;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.item.tool.ToolMaterial;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.world.data.PlayerSpiritRecoveryData;
import java.util.ArrayList;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
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
   private static final String RECOVERY_COST_TAG = "recoveryCost";
   private static final String RECYCLABLE_TAG = "recyclable";
   @Nullable
   private GameProfile gameProfile;
   private final NonNullList<ItemStack> items = NonNullList.create();
   private int vaultLevel;
   private int playerLevel;
   private SpiritExtractorTileEntity.RecoveryCost recoveryCost = new SpiritExtractorTileEntity.RecoveryCost();
   private final OverSizedInventory paymentInventory = new OverSizedInventory(1, this::setChanged, player -> true) {
      public boolean canPlaceItem(int pIndex, ItemStack pStack) {
         return SpiritExtractorTileEntity.this.getRecoveryCost().getTotalCost().getItem() == pStack.getItem();
      }

      @Override
      public int getMaxStackSize() {
         return SpiritExtractorTileEntity.this.getRecoveryCost().getTotalCost().getCount();
      }
   };
   private boolean spewingItems;
   private int itemsPerDrop;
   private long spewingCooldownTime;
   private ResourceLocation skinLocation = null;
   private boolean updatingSkin = false;
   private boolean slimSkin = false;
   private boolean recyclable = false;
   private float rescuedBonus = 0.0F;

   public SpiritExtractorTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.SPIRIT_EXTRACTOR_TILE_ENTITY, pos, state);
   }

   public SpiritExtractorTileEntity.RecoveryCost getRecoveryCost() {
      if (!this.spewingItems && this.recoveryCost.isEmpty()) {
         this.recalculateCost();
      }

      return this.recoveryCost;
   }

   public void recalculateCost() {
      if (this.level instanceof ServerLevel serverLevel) {
         if (this.gameProfile != null) {
            PlayerSpiritRecoveryData data = PlayerSpiritRecoveryData.get(serverLevel);
            this.recoveryCost
               .calculate(
                  data.getSpiritRecoveryMultiplier(this.gameProfile.getId()),
                  this.playerLevel,
                  this.items,
                  data.getHeroDiscount(this.gameProfile.getId()),
                  this.rescuedBonus
               );
         }
      }
   }

   public OverSizedInventory getPaymentInventory() {
      return this.paymentInventory;
   }

   public void setVaultLevel(int vaultLevel) {
      this.vaultLevel = vaultLevel;
      this.setChanged();
   }

   public void setPlayerLevel(int playerLevel) {
      this.playerLevel = playerLevel;
      this.setChanged();
   }

   public void setRescuedBonus(float rescuedBonus) {
      this.rescuedBonus = rescuedBonus;
      this.setChanged();
   }

   public float getRescuedBonus() {
      return this.rescuedBonus;
   }

   @Override
   public void setGameProfile(GameProfile gameProfile) {
      if (gameProfile == null || !gameProfile.equals(this.gameProfile)) {
         this.gameProfile = gameProfile;
         this.skinLocation = null;
      }

      this.setChanged();
   }

   public void setRecyclable(boolean recyclable) {
      this.recyclable = recyclable;
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

   private CompoundTag addSpiritRecoveryData(CompoundTag tag) {
      tag.put("recoveryCost", this.recoveryCost.serialize());
      return tag;
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      if (this.gameProfile != null) {
         tag.put("ownerProfile", NbtUtils.writeGameProfile(new CompoundTag(), this.gameProfile));
      }

      tag.putInt("vaultLevel", this.vaultLevel);
      tag.putInt("playerLevel", this.playerLevel);
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
      tag.putBoolean("recyclable", this.recyclable);
      tag.putFloat("rescuedBonus", this.rescuedBonus);
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
      this.playerLevel = tag.contains("playerLevel") ? tag.getInt("playerLevel") : this.vaultLevel;
      if (tag.contains("paymentStack")) {
         this.paymentInventory.setOverSizedStack(0, OverSizedItemStack.deserialize(tag.getCompound("paymentStack")));
      }

      this.spewingItems = tag.getBoolean("spewingItems");
      if (tag.contains("recoveryCost", 10)) {
         this.recoveryCost.deserialize(tag.getCompound("recoveryCost"));
      }

      this.recyclable = !tag.contains("recyclable") || tag.getBoolean("recyclable");
      this.rescuedBonus = tag.getFloat("rescuedBonus");
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
               PlayerSpiritRecoveryData data = PlayerSpiritRecoveryData.get(serverLevel);
               data.increaseMultiplierOnRecovery(this.gameProfile.getId());
               data.removeHeroDiscount(this.gameProfile.getId());
            }

            this.paymentInventory.setItem(0, ItemStack.EMPTY);
            this.rescuedBonus = 0.0F;
            this.recoveryCost = new SpiritExtractorTileEntity.RecoveryCost();
            this.spewingItems = true;
            this.spewingCooldownTime = this.level.getGameTime() + 20L;
            this.level.playSound(null, this.getBlockPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 0.5F);
         }
      }
   }

   public boolean coinsCoverTotalCost() {
      int totalCost = this.getRecoveryCost().getTotalCost().getCount();
      return totalCost > 0 && this.paymentInventory.getItem(0).getCount() >= totalCost;
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

   public int getPlayerLevel() {
      return this.playerLevel;
   }

   public void removeSpirit() {
      this.items.clear();
      this.gameProfile = null;
      this.itemsPerDrop = 0;
      this.vaultLevel = 0;
      this.playerLevel = 0;
      this.rescuedBonus = 0.0F;
      this.dropPaymentInventory();
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   private void dropPaymentInventory() {
      BlockPos pos = this.getBlockPos();
      Containers.dropItemStack(this.level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, this.paymentInventory.getItem(0));
      this.paymentInventory.setItem(0, ItemStack.EMPTY);
   }

   public void recycle() {
      if (this.gameProfile != null && !this.isSpewingItems()) {
         if (this.recyclable) {
            ItemStack stack = new ItemStack(ModItems.VAULT_CRYSTAL);
            CrystalData crystal = CrystalData.read(stack);
            crystal.setLevel(this.vaultLevel);
            crystal.write(stack);
            BlockPos pos = this.getBlockPos();
            Containers.dropItemStack(this.level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack);
         }

         this.items.clear();
         this.removeSpirit();
         this.setChanged();
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
         this.level.playSound(null, this.getBlockPos(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   public boolean isRecyclable() {
      return this.recyclable;
   }

   public static class RecoveryCost {
      private OverSizedItemStack totalCost = OverSizedItemStack.EMPTY;
      private float baseCount = 0.0F;
      private List<Tuple<ItemStack, Integer>> stackCost = new ArrayList<>();

      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.put("totalCost", this.totalCost.serialize());
         tag.putFloat("baseCount", this.baseCount);
         if (!this.stackCost.isEmpty()) {
            ListTag stackCostTag = new ListTag();
            this.stackCost.forEach(t -> {
               CompoundTag singleStackTag = new CompoundTag();
               singleStackTag.put("stack", ((ItemStack)t.getA()).save(new CompoundTag()));
               singleStackTag.putInt("cost", (Integer)t.getB());
               stackCostTag.add(singleStackTag);
            });
            tag.put("stackCost", stackCostTag);
         }

         return tag;
      }

      public void deserialize(CompoundTag tag) {
         this.totalCost = tag.contains("totalCost", 10) ? OverSizedItemStack.deserialize(tag.getCompound("totalCost")) : OverSizedItemStack.EMPTY;
         this.baseCount = tag.contains("baseCost", 10) ? OverSizedItemStack.deserialize(tag.getCompound("baseCost")).amount() : tag.getFloat("baseCount");
         this.stackCost.clear();
         if (tag.contains("stackCost", 9)) {
            tag.getList("stackCost", 10).forEach(t -> {
               CompoundTag singleStackCostTag = (CompoundTag)t;
               this.stackCost.add(new Tuple(ItemStack.of(singleStackCostTag.getCompound("stack")), singleStackCostTag.getInt("cost")));
            });
         }
      }

      public boolean isEmpty() {
         return this.totalCost.overSizedStack().isEmpty();
      }

      public void calculate(float multiplier, int vaultLevel, List<ItemStack> items, float heroDiscount, float rescuedBonus) {
         this.getLevelCost(vaultLevel).ifPresent(cost -> {
            this.baseCount = cost.count;
            int totalCost = (int)Math.ceil(cost.count * Math.max(1, vaultLevel) + 1.0F);
            this.stackCost.clear();
            totalCost += this.getItemsCost(cost, items);
            totalCost = (int)(totalCost * multiplier * (1.0F - heroDiscount) * (1.0F - rescuedBonus));
            totalCost = Math.max(1, totalCost);
            this.totalCost = new OverSizedItemStack(new ItemStack(cost.item, totalCost), totalCost);
         });
      }

      private int getItemsCost(SpiritConfig.LevelCost cost, List<ItemStack> items) {
         int totalItemCost = 0;

         for (ItemStack foundItem : InventoryUtil.findAllItems(items)) {
            int itemCost = this.getItemCost(cost, foundItem);
            if (itemCost > 0) {
               totalItemCost += itemCost;
            }
         }

         return totalItemCost;
      }

      private int getItemCost(SpiritConfig.LevelCost cost, ItemStack item) {
         if (this.isSoulbound(item)) {
            return 0;
         } else {
            int itemCost = 0;
            if (item.getItem() instanceof TrinketItem && TrinketItem.hasUsesLeft(item)) {
               this.addItemCost(item.copy(), cost.trinketCost);
               itemCost = cost.trinketCost;
            } else if (item.getItem() instanceof VaultGearItem && !(item.getItem() instanceof JewelItem) && !(item.getItem() instanceof ToolItem)) {
               VaultGearData data = VaultGearData.read(item);
               if (cost.gearRarityCost.containsKey(data.getRarity())) {
                  int gearCost = cost.gearRarityCost.get(data.getRarity());
                  this.addItemCost(item.copy(), gearCost);
                  itemCost = gearCost;
               }
            } else if (item.getItem() instanceof ToolItem) {
               ToolMaterial material = ToolItem.getMaterial(item);
               if (cost.toolMaterialCost.containsKey(material)) {
                  int toolCost = cost.toolMaterialCost.get(material);
                  this.addItemCost(item.copy(), toolCost);
                  itemCost = toolCost;
               }
            } else {
               itemCost = cost.getStackCost(item);
               this.addItemCost(item, itemCost);
            }

            return itemCost;
         }
      }

      private void addItemCost(ItemStack item, int itemCost) {
         if (itemCost != 0) {
            this.stackCost.add(new Tuple(item.copy(), itemCost));
         }
      }

      private boolean isSoulbound(ItemStack stack) {
         return AttributeGearData.<AttributeGearData>read(stack).get(ModGearAttributes.SOULBOUND, VaultGearAttributeTypeMerger.anyTrue());
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

      public ItemStack getTotalCost() {
         return this.totalCost.overSizedStack();
      }

      public float getBaseCount() {
         return this.baseCount;
      }

      public List<Tuple<ItemStack, Integer>> getStackCost() {
         return this.stackCost;
      }
   }
}
