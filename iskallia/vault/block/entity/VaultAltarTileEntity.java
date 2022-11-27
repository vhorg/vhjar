package iskallia.vault.block.entity;

import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.theme.PoolCrystalTheme;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.PlayerVaultAltarData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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

public class VaultAltarTileEntity extends BlockEntity {
   private UUID owner;
   private AltarInfusionRecipe recipe;
   private VaultAltarTileEntity.AltarState altarState;
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
            if (world.getGameTime() % 20L == 0L) {
               tile.sendUpdates();
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
         this.recipe = altarData.getRecipe(world, this.worldPosition, player);
         this.setAltarState(VaultAltarTileEntity.AltarState.ACCEPTING);
         if (!player.isCreative()) {
            heldItem.shrink(1);
         }

         this.sendUpdates();
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

            List<RequiredItem> idolRequirements = new ArrayList<>();
            ItemStack benevolent = new ItemStack(ModItems.IDOL_BENEVOLENT);
            ItemStack malevolence = new ItemStack(ModItems.IDOL_MALEVOLENCE);
            ItemStack omniscient = new ItemStack(ModItems.IDOL_OMNISCIENT);
            ItemStack timekeeper = new ItemStack(ModItems.IDOL_TIMEKEEPER);
            idolRequirements.add(new RequiredItem(benevolent, 0, 1));
            idolRequirements.add(new RequiredItem(malevolence, 0, 1));
            idolRequirements.add(new RequiredItem(omniscient, 0, 1));
            idolRequirements.add(new RequiredItem(timekeeper, 0, 1));
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
      if (this.recipe.isPogInfused()) {
      }

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
               for (RequiredItem required : data.getRecipe(this.owner).getRequiredItems()) {
                  if (!required.reachedAmountRequired() && required.isItemEqual(itemEntity.getItem())) {
                     int excess = required.getRemainder(itemEntity.getItem().getCount());
                     this.moveItemTowardPedestal(itemEntity, speed);
                     if (this.isItemInRange(itemEntity.blockPosition())) {
                        if (excess > 0) {
                           required.setCurrentAmount(required.getAmountRequired());
                           itemEntity.getItem().setCount(excess);
                        } else {
                           required.addAmount(itemEntity.getItem().getCount());
                           itemEntity.getItem().setCount(excess);
                           itemEntity.discard();
                        }

                        data.setDirty();
                        this.sendUpdates();
                     }
                  }
               }
            }
         }
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
         compound.put("Recipe", this.recipe.serialize());
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
         this.recipe = AltarInfusionRecipe.deserialize(compound.getCompound("Recipe"));
      }

      if (compound.contains("InfusionTimer")) {
         this.infusionTimer = compound.getInt("InfusionTimer");
      }
   }

   private void migrate(boolean containsVaultRock) {
      this.altarState = containsVaultRock ? VaultAltarTileEntity.AltarState.ACCEPTING : VaultAltarTileEntity.AltarState.IDLE;
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(1) {
         protected void onContentsChanged(int slot) {
            VaultAltarTileEntity.this.sendUpdates();
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (PlayerVaultAltarData.get((ServerLevel)VaultAltarTileEntity.this.level).getRecipe(VaultAltarTileEntity.this.owner) != null
               && !PlayerVaultAltarData.get((ServerLevel)VaultAltarTileEntity.this.level).getRecipe(VaultAltarTileEntity.this.owner).isComplete()) {
               for (RequiredItem item : PlayerVaultAltarData.get((ServerLevel)VaultAltarTileEntity.this.level)
                  .getRecipe(VaultAltarTileEntity.this.owner)
                  .getRequiredItems()) {
                  if (item.isItemEqual(stack)) {
                     return true;
                  }
               }
            }

            return false;
         }

         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            PlayerVaultAltarData data = PlayerVaultAltarData.get((ServerLevel)VaultAltarTileEntity.this.level);
            AltarInfusionRecipe recipe = data.getRecipe(VaultAltarTileEntity.this.owner);
            if (recipe != null && !recipe.isComplete()) {
               for (RequiredItem item : recipe.getRequiredItems()) {
                  if (!item.reachedAmountRequired() && item.isItemEqual(stack)) {
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
            }

            return stack;
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
