package iskallia.vault.block.entity;

import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItems;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.theme.PoolCrystalTheme;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.PlayerVaultAltarData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class VaultAltarTileEntity extends BlockEntity {
   private UUID owner;
   private AltarInfusionRecipe recipe;
   private VaultAltarTileEntity.AltarState altarState;
   private HashMap<String, Integer> displayedIndex = new HashMap<>();
   private int infusionTimer = -666;
   private final ItemStackHandler itemHandler = this.createHandler();
   private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);

   public VaultAltarTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_ALTAR_TILE_ENTITY, pos, state);
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
   }

   public UUID getOwner() {
      return this.owner;
   }

   public void setRecipe(AltarInfusionRecipe recipe) {
      this.recipe = recipe;
   }

   public AltarInfusionRecipe getRecipe() {
      return this.recipe;
   }

   public void setAltarState(VaultAltarTileEntity.AltarState state) {
      this.altarState = state;
   }

   public VaultAltarTileEntity.AltarState getAltarState() {
      return this.altarState;
   }

   public int getInfusionTimer() {
      return this.infusionTimer;
   }

   public HashMap<String, Integer> getDisplayedIndex() {
      return this.displayedIndex;
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public static void tick(Level world, BlockPos pos, BlockState state, VaultAltarTileEntity tile) {
      if (world != null && !world.isClientSide) {
         if (tile.altarState != VaultAltarTileEntity.AltarState.IDLE) {
            if (PlayerVaultAltarData.get((ServerLevel)world).getRecipe(tile.owner) != null
               && PlayerVaultAltarData.get((ServerLevel)world).getRecipe(tile.owner).isComplete()
               && tile.altarState != VaultAltarTileEntity.AltarState.INFUSING) {
               tile.altarState = VaultAltarTileEntity.AltarState.COMPLETE;
            }

            switch (tile.altarState) {
               case ACCEPTING:
                  tile.pullNearbyItems(
                     world,
                     PlayerVaultAltarData.get((ServerLevel)world),
                     tile.getBlockPos().getX() + 0.5,
                     tile.getBlockPos().getY() + 0.5,
                     tile.getBlockPos().getZ() + 0.5,
                     ModConfigs.VAULT_ALTAR.ITEM_RANGE_CHECK
                  );
                  break;
               case INFUSING:
                  tile.playInfusionEffects((ServerLevel)world);
                  if (tile.infusionTimer-- <= 0) {
                     tile.completeInfusion(world);
                     tile.sendUpdates();
                  }
            }

            tile.recipe = PlayerVaultAltarData.get((ServerLevel)world).getRecipe(tile.owner);
            if (world.getGameTime() % ModConfigs.VAULT_ALTAR.GROUP_DISPLAY_TICKS == 0L) {
               updateDisplayedItems(tile);
            }

            if (world.getGameTime() % 20L == 0L) {
               tile.sendUpdates();
            }
         }
      }
   }

   private static void updateDisplayedItems(VaultAltarTileEntity altar) {
      if (altar.getLevel() != null) {
         if (altar.getAltarState() == VaultAltarTileEntity.AltarState.ACCEPTING || altar.getAltarState() == VaultAltarTileEntity.AltarState.INFUSING) {
            for (RequiredItems required : altar.getRecipe().getRequiredItems()) {
               String id = required.getPoolId();
               List<ItemStack> stacks = required.getItems();
               int index = altar.displayedIndex.computeIfAbsent(id, poolId -> 0);
               altar.displayedIndex.put(id, index + 1 >= stacks.size() ? 0 : index + 1);
            }
         }
      }
   }

   public void onAltarPowered() {
      if (this.level instanceof ServerLevel serverWorld && this.getAltarState() == VaultAltarTileEntity.AltarState.COMPLETE) {
         PlayerVaultAltarData.get(serverWorld)
            .getAltars(this.owner)
            .forEach(
               altarPos -> {
                  if (!this.getBlockPos().equals(altarPos)
                     && this.level.getBlockEntity(altarPos) instanceof VaultAltarTileEntity altar
                     && altar.getAltarState() != VaultAltarTileEntity.AltarState.IDLE) {
                     altar.onRemoveVaultRock();
                  }
               }
            );
         serverWorld.playSound(
            null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 0.5F
         );
         this.infusionTimer = ModConfigs.VAULT_ALTAR.INFUSION_TIME * 20;
         this.altarState = VaultAltarTileEntity.AltarState.INFUSING;
      }
   }

   public InteractionResult onAddVaultRock(ServerPlayer player, ItemStack heldItem) {
      if (this.level == null) {
         return InteractionResult.FAIL;
      } else {
         ServerLevel world = (ServerLevel)this.level;

         for (BlockPos altarPosition : PlayerVaultAltarData.get(world).getAltars(player.getUUID())) {
            if (world.getBlockEntity(altarPosition) instanceof VaultAltarTileEntity altar && altar.altarState == VaultAltarTileEntity.AltarState.INFUSING) {
               return InteractionResult.FAIL;
            }
         }

         PlayerVaultAltarData altarData = PlayerVaultAltarData.get(world);
         this.recipe = altarData.getRecipe(player, this.worldPosition);
         this.setAltarState(VaultAltarTileEntity.AltarState.ACCEPTING);
         if (!player.isCreative()) {
            heldItem.shrink(1);
         }

         this.sendUpdates();
         player.connection.send(this.getUpdatePacket());
         return InteractionResult.SUCCESS;
      }
   }

   public InteractionResult onPogRightClick(ServerPlayer player, ItemStack heldItem) {
      if (this.level == null) {
         return InteractionResult.FAIL;
      } else {
         ServerLevel world = (ServerLevel)this.level;
         if (this.recipe == null) {
            return InteractionResult.SUCCESS;
         } else {
            for (BlockPos altarPosition : PlayerVaultAltarData.get(world).getAltars(player.getUUID())) {
               if (world.getBlockEntity(altarPosition) instanceof VaultAltarTileEntity altar && altar.altarState == VaultAltarTileEntity.AltarState.INFUSING) {
                  return InteractionResult.FAIL;
               }
            }

            List<RequiredItems> idolRequirements = new ArrayList<>();
            this.recipe.cacheRequiredItems(idolRequirements);
            this.recipe.setPogInfused(true);
            PlayerVaultAltarData.get(world).setDirty();
            if (!player.isCreative()) {
               heldItem.shrink(1);
            }

            this.sendUpdates();
            world.playSound(
               null,
               this.getBlockPos().getX(),
               this.getBlockPos().getY(),
               this.getBlockPos().getZ(),
               SoundEvents.END_PORTAL_SPAWN,
               SoundSource.BLOCKS,
               1.0F,
               2.0F
            );
            return InteractionResult.SUCCESS;
         }
      }
   }

   public InteractionResult onRemoveVaultRock() {
      this.setAltarState(VaultAltarTileEntity.AltarState.IDLE);
      this.recipe = null;
      this.infusionTimer = -666;
      if (this.getLevel() != null) {
         this.getLevel()
            .addFreshEntity(
               new ItemEntity(
                  this.getLevel(),
                  this.getBlockPos().getX() + 0.5,
                  this.getBlockPos().getY() + 1.5,
                  this.getBlockPos().getZ() + 0.5,
                  new ItemStack(ModItems.VAULT_ROCK)
               )
            );
      }

      this.sendUpdates();
      return InteractionResult.SUCCESS;
   }

   public InteractionResult onRemovePogInfusion() {
      this.setAltarState(VaultAltarTileEntity.AltarState.ACCEPTING);
      this.recipe.revertCache();
      this.recipe.setPogInfused(false);
      if (this.level != null) {
         this.level
            .playSound(
               null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), SoundEvents.WITHER_DEATH, SoundSource.BLOCKS, 0.7F, 1.5F
            );
      }

      this.sendUpdates();
      return InteractionResult.SUCCESS;
   }

   private void completeInfusion(Level world) {
      ServerLevel serverWorld = (ServerLevel)world;
      ItemStack stack = new ItemStack(ModItems.VAULT_CRYSTAL);
      CrystalData crystal = new CrystalData(stack);
      crystal.setTheme(new PoolCrystalTheme(VaultMod.id("default")));
      int level = PlayerVaultStatsData.get((ServerLevel)world).getVaultStats(this.owner).getVaultLevel();
      crystal.setLevel(level);
      world.addFreshEntity(new ItemEntity(world, this.getBlockPos().getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 0.5, stack));
      PlayerStatsData.get(serverWorld.getServer()).onCrystalCrafted(this.owner, this.recipe.getRequiredItems());
      this.resetAltar((ServerLevel)world);
      this.playCompletionEffects(serverWorld);
   }

   private void playInfusionEffects(ServerLevel world) {
      float speed = this.infusionTimer * 0.01F - 0.5F;
      if (speed > 0.0F) {
         world.sendParticles(
            ParticleTypes.PORTAL, this.worldPosition.getX() + 0.5, this.getBlockPos().getY() + 1.6, this.getBlockPos().getZ() + 0.5, 3, 0.0, 0.0, 0.0, speed
         );
      }
   }

   private void playCompletionEffects(ServerLevel serverWorld) {
      DustParticleOptions particleData = new DustParticleOptions(new Vector3f(0.0F, 1.0F, 0.0F), 1.0F);

      for (int i = 0; i < 10; i++) {
         float offset = 0.1F * i;
         if (serverWorld.random.nextFloat() < 0.5F) {
            offset *= -1.0F;
         }

         serverWorld.sendParticles(
            particleData, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.6, this.worldPosition.getZ() + 0.5, 10, offset, offset, offset, 1.0
         );
      }

      serverWorld.playSound(
         null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.7F, 1.5F
      );
   }

   private void resetAltar(ServerLevel world) {
      this.infusionTimer = -666;
      if (this.recipe.isPogInfused()) {
         this.recipe.revertCache();
         this.recipe.setPogInfused(false);
         PlayerVaultAltarData.get(world).setDirty();
         this.altarState = VaultAltarTileEntity.AltarState.ACCEPTING;
         this.sendUpdates();
      } else {
         this.recipe = null;
         PlayerVaultAltarData.get(world).removeRecipe(this.owner);
         this.altarState = VaultAltarTileEntity.AltarState.IDLE;
      }
   }

   private void pullNearbyItems(Level world, PlayerVaultAltarData data, double x, double y, double z, double range) {
      if (data.getRecipe(this.owner) != null) {
         if (!data.getRecipe(this.owner).getRequiredItems().isEmpty()) {
            float speed = ModConfigs.VAULT_ALTAR.PULL_SPEED / 20.0F;

            for (ItemEntity itemEntity : world.getEntitiesOfClass(ItemEntity.class, this.getAABB(range, x, y, z))) {
               if (this.itemHandler.isItemValid(0, itemEntity.getItem())) {
                  List<RequiredItems> itemsToPull = data.getRecipe(this.owner).getRequiredItems();
                  itemsToPull.stream().filter(requiredItem -> !requiredItem.reachedAmountRequired()).forEach(requiredItem -> {
                     if (!requiredItem.getItems().stream().noneMatch(itemStack -> ItemStack.isSameIgnoreDurability(itemStack, itemEntity.getItem()))) {
                        this.moveStacksAndUpdate(data, speed, itemEntity, requiredItem);
                     }
                  });
               }
            }
         }
      }
   }

   private void moveStacksAndUpdate(PlayerVaultAltarData data, float speed, ItemEntity itemEntity, RequiredItems requiredItems) {
      int excess = requiredItems.getRemainder(itemEntity.getItem().getCount());
      this.moveItemTowardPedestal(itemEntity, speed);
      if (this.isItemInRange(itemEntity.blockPosition())) {
         if (excess > 0) {
            requiredItems.setCurrentAmount(requiredItems.getAmountRequired());
            itemEntity.getItem().setCount(excess);
         } else {
            requiredItems.addAmount(itemEntity.getItem().getCount());
            itemEntity.getItem().setCount(excess);
            itemEntity.discard();
         }

         data.setDirty();
         this.sendUpdates();
      }
   }

   private void moveItemTowardPedestal(ItemEntity itemEntity, float speed) {
      Vec3 target = Vec3.atCenterOf(this.getBlockPos());
      Vec3 current = itemEntity.position();
      Vec3 velocity = target.subtract(current).normalize().scale(speed);
      itemEntity.push(velocity.x, velocity.y, velocity.z);
   }

   private boolean isItemInRange(BlockPos itemPos) {
      return itemPos.distSqr(this.getBlockPos()) <= 4.0;
   }

   public AABB getAABB(double range, double x, double y, double z) {
      return new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
   }

   protected void saveAdditional(CompoundTag compound) {
      super.saveAdditional(compound);
      if (this.altarState != null) {
         compound.putInt("AltarState", this.altarState.ordinal());
      }

      if (this.owner != null) {
         compound.putUUID("Owner", this.owner);
      }

      if (this.recipe != null) {
         compound.put("Recipe", this.recipe.serializeNBT());
      }

      compound.putInt("InfusionTimer", this.infusionTimer);
   }

   public void load(CompoundTag compound) {
      super.load(compound);
      if (!compound.contains("AltarState")) {
         this.migrate(compound.getBoolean("containsVaultRock"));
      }

      if (compound.contains("AltarState")) {
         this.altarState = VaultAltarTileEntity.AltarState.values()[compound.getInt("AltarState")];
      }

      if (compound.contains("Owner")) {
         this.owner = compound.getUUID("Owner");
      }

      if (compound.contains("Recipe")) {
         this.recipe = new AltarInfusionRecipe(compound.getCompound("Recipe"));
      }

      if (compound.contains("InfusionTimer")) {
         this.infusionTimer = compound.getInt("InfusionTimer");
      }
   }

   private void migrate(boolean containsVaultRock) {
      this.altarState = containsVaultRock ? VaultAltarTileEntity.AltarState.ACCEPTING : VaultAltarTileEntity.AltarState.IDLE;
   }

   @NotNull
   public CompoundTag getUpdateTag() {
      CompoundTag tag = this.saveWithoutMetadata();
      CompoundTag displayed = new CompoundTag();
      this.displayedIndex.forEach(displayed::putInt);
      tag.put("displayed", displayed);
      return tag;
   }

   public void handleUpdateTag(CompoundTag tag) {
      this.updateDisplayed(tag);
      super.handleUpdateTag(tag);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
      CompoundTag tag = pkt.getTag();
      if (tag != null) {
         this.updateDisplayed(tag);
      }

      super.onDataPacket(net, pkt);
   }

   private void updateDisplayed(CompoundTag tag) {
      CompoundTag displayed = tag.getCompound("displayed");

      for (String poolId : displayed.getAllKeys()) {
         this.displayedIndex.put(poolId, displayed.getInt(poolId));
      }
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(1) {
         protected void onContentsChanged(int slot) {
            VaultAltarTileEntity.this.sendUpdates();
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (VaultAltarTileEntity.this.level instanceof ServerLevel serverLevel) {
               AltarInfusionRecipe altarInfusionRecipe = PlayerVaultAltarData.get(serverLevel).getRecipe(VaultAltarTileEntity.this.owner);
               if (altarInfusionRecipe == null) {
                  return false;
               } else {
                  return altarInfusionRecipe.isComplete()
                     ? false
                     : altarInfusionRecipe.getRequiredItems()
                        .stream()
                        .filter(requiredItem -> !requiredItem.reachedAmountRequired())
                        .map(RequiredItems::getItems)
                        .flatMap(Collection::stream)
                        .anyMatch(requiredStack -> ItemStack.isSameIgnoreDurability(stack, requiredStack));
               }
            } else {
               return false;
            }
         }

         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!this.isItemValid(slot, stack)) {
               return stack;
            } else if (VaultAltarTileEntity.this.level instanceof ServerLevel serverLevel) {
               PlayerVaultAltarData data = PlayerVaultAltarData.get(serverLevel);
               AltarInfusionRecipe altarInfusionRecipe = data.getRecipe(VaultAltarTileEntity.this.owner);
               if (altarInfusionRecipe == null) {
                  return stack;
               } else if (altarInfusionRecipe.isComplete()) {
                  return stack;
               } else {
                  for (RequiredItems item : altarInfusionRecipe.getRequiredItems()) {
                     if (!item.reachedAmountRequired()) {
                        int amount = stack.getCount();
                        int excess = item.getRemainder(amount);
                        if (excess > 0) {
                           if (!simulate) {
                              item.setCurrentAmount(item.getAmountRequired());
                              data.setDirty();
                           }

                           return ItemHandlerHelper.copyStackWithSize(stack, excess);
                        }

                        if (!simulate) {
                           item.addAmount(amount);
                           data.setDirty();
                        }

                        return ItemStack.EMPTY;
                     }
                  }

                  return stack;
               }
            } else {
               return stack;
            }
         }
      };
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.handler.cast() : super.getCapability(cap, side);
   }

   public static enum AltarState {
      IDLE,
      ACCEPTING,
      COMPLETE,
      INFUSING;
   }
}
