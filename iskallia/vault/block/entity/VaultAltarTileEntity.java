package iskallia.vault.block.entity;

import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.util.VectorHelper;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.PlayerVaultAltarData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class VaultAltarTileEntity extends TileEntity implements ITickableTileEntity {
   private UUID owner;
   private AltarInfusionRecipe recipe;
   private VaultAltarTileEntity.AltarState altarState;
   private int infusionTimer = -666;
   private ItemStackHandler itemHandler = this.createHandler();
   private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);

   public VaultAltarTileEntity() {
      super(ModBlocks.VAULT_ALTAR_TILE_ENTITY);
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

   public void sendUpdates() {
      if (this.field_145850_b != null) {
         this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
         this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
         this.func_70296_d();
      }
   }

   public void func_73660_a() {
      World world = this.func_145831_w();
      if (world != null && !world.field_72995_K) {
         if (this.altarState != VaultAltarTileEntity.AltarState.IDLE) {
            if (PlayerVaultAltarData.get((ServerWorld)world).getRecipe(this.owner) != null
               && PlayerVaultAltarData.get((ServerWorld)world).getRecipe(this.owner).isComplete()
               && this.altarState != VaultAltarTileEntity.AltarState.INFUSING) {
               this.altarState = VaultAltarTileEntity.AltarState.COMPLETE;
            }

            switch (this.altarState) {
               case ACCEPTING:
                  this.pullNearbyItems(
                     world,
                     PlayerVaultAltarData.get((ServerWorld)world),
                     this.func_174877_v().func_177958_n() + 0.5,
                     this.func_174877_v().func_177956_o() + 0.5,
                     this.func_174877_v().func_177952_p() + 0.5,
                     ModConfigs.VAULT_ALTAR.ITEM_RANGE_CHECK
                  );
                  break;
               case INFUSING:
                  if (this.infusionTimer-- <= 0) {
                     this.completeInfusion(world);
                     this.sendUpdates();
                  }
            }

            this.recipe = PlayerVaultAltarData.get((ServerWorld)world).getRecipe(this.owner);
            if (world.func_82737_E() % 20L == 0L) {
               this.sendUpdates();
            }
         }
      }
   }

   public void onAltarPowered() {
      if (this.field_145850_b != null && this.getAltarState() == VaultAltarTileEntity.AltarState.COMPLETE) {
         PlayerVaultAltarData.get((ServerWorld)this.field_145850_b).getAltars(this.owner).forEach(altarPos -> {
            if (!this.func_174877_v().equals(altarPos)) {
               TileEntity te = this.field_145850_b.func_175625_s(altarPos);
               if (te instanceof VaultAltarTileEntity) {
                  VaultAltarTileEntity altar = (VaultAltarTileEntity)te;
                  if (altar.getAltarState() != VaultAltarTileEntity.AltarState.IDLE) {
                     altar.onRemoveVaultRock();
                  }
               }
            }
         });
         this.infusionTimer = ModConfigs.VAULT_ALTAR.INFUSION_TIME * 20;
         this.altarState = VaultAltarTileEntity.AltarState.INFUSING;
      }
   }

   public ActionResultType onAddVaultRock(ServerPlayerEntity player, ItemStack heldItem) {
      if (this.field_145850_b == null) {
         return ActionResultType.FAIL;
      } else {
         ServerWorld world = (ServerWorld)this.field_145850_b;

         for (BlockPos altarPosition : PlayerVaultAltarData.get(world).getAltars(player.func_110124_au())) {
            TileEntity te = world.func_175625_s(altarPosition);
            if (te instanceof VaultAltarTileEntity) {
               VaultAltarTileEntity altar = (VaultAltarTileEntity)te;
               if (altar.altarState == VaultAltarTileEntity.AltarState.INFUSING) {
                  return ActionResultType.FAIL;
               }
            }
         }

         PlayerVaultAltarData altarData = PlayerVaultAltarData.get(world);
         this.recipe = altarData.getRecipe(world, this.field_174879_c, player);
         this.setAltarState(VaultAltarTileEntity.AltarState.ACCEPTING);
         if (!player.func_184812_l_()) {
            heldItem.func_190918_g(1);
         }

         this.sendUpdates();
         return ActionResultType.SUCCESS;
      }
   }

   public ActionResultType onRemoveVaultRock() {
      this.setAltarState(VaultAltarTileEntity.AltarState.IDLE);
      this.recipe = null;
      this.infusionTimer = -666;
      if (this.func_145831_w() != null) {
         this.func_145831_w()
            .func_217376_c(
               new ItemEntity(
                  this.func_145831_w(),
                  this.func_174877_v().func_177958_n() + 0.5,
                  this.func_174877_v().func_177956_o() + 1.5,
                  this.func_174877_v().func_177952_p() + 0.5,
                  new ItemStack(ModItems.VAULT_ROCK)
               )
            );
      }

      this.sendUpdates();
      return ActionResultType.SUCCESS;
   }

   private void completeInfusion(World world) {
      ItemStack crystal = new ItemStack(ModItems.VAULT_CRYSTAL);
      world.func_217376_c(
         new ItemEntity(
            world, this.func_174877_v().func_177958_n() + 0.5, this.field_174879_c.func_177956_o() + 1.5, this.field_174879_c.func_177952_p() + 0.5, crystal
         )
      );
      CrystalData data = new CrystalData(crystal);
      int level = PlayerVaultStatsData.get((ServerWorld)world).getVaultStats(this.owner).getVaultLevel();
      data.setType(ModConfigs.LOOT_TABLES.getForLevel(level).CRYSTAL_TYPE.getRandom(world.func_201674_k()));
      PlayerStatsData.get((ServerWorld)world).onCrystalCrafted(this.owner, this.recipe.getRequiredItems(), data.getType());
      this.resetAltar((ServerWorld)world);
   }

   private void resetAltar(ServerWorld world) {
      this.recipe = null;
      this.infusionTimer = -666;
      PlayerVaultAltarData.get(world).removeRecipe(this.owner);
      this.altarState = VaultAltarTileEntity.AltarState.IDLE;
   }

   private void pullNearbyItems(World world, PlayerVaultAltarData data, double x, double y, double z, double range) {
      if (data.getRecipe(this.owner) != null) {
         if (!data.getRecipe(this.owner).getRequiredItems().isEmpty()) {
            float speed = ModConfigs.VAULT_ALTAR.PULL_SPEED / 20.0F;

            for (ItemEntity itemEntity : world.func_217357_a(ItemEntity.class, this.getAABB(range, x, y, z))) {
               List<RequiredItem> itemsToPull = data.getRecipe(this.owner).getRequiredItems();
               if (itemsToPull == null) {
                  return;
               }

               for (RequiredItem required : itemsToPull) {
                  if (!required.reachedAmountRequired() && required.isItemEqual(itemEntity.func_92059_d())) {
                     int excess = required.getRemainder(itemEntity.func_92059_d().func_190916_E());
                     this.moveItemTowardPedestal(itemEntity, speed);
                     if (this.isItemInRange(itemEntity.func_233580_cy_())) {
                        if (excess > 0) {
                           required.setCurrentAmount(required.getAmountRequired());
                           itemEntity.func_92059_d().func_190920_e(excess);
                        } else {
                           required.addAmount(itemEntity.func_92059_d().func_190916_E());
                           itemEntity.func_92059_d().func_190920_e(excess);
                           itemEntity.func_70106_y();
                        }

                        data.func_76185_a();
                        this.sendUpdates();
                     }
                  }
               }
            }
         }
      }
   }

   private void moveItemTowardPedestal(ItemEntity itemEntity, float speed) {
      Vector3d target = VectorHelper.getVectorFromPos(this.func_174877_v());
      Vector3d current = VectorHelper.getVectorFromPos(itemEntity.func_233580_cy_());
      Vector3d velocity = VectorHelper.getMovementVelocity(current, target, speed);
      itemEntity.func_70024_g(velocity.field_72450_a, velocity.field_72448_b, velocity.field_72449_c);
   }

   private boolean isItemInRange(BlockPos itemPos) {
      return itemPos.func_177951_i(this.func_174877_v()) <= 4.0;
   }

   public AxisAlignedBB getAABB(double range, double x, double y, double z) {
      return new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range);
   }

   public CompoundNBT func_189515_b(CompoundNBT compound) {
      if (this.altarState != null) {
         compound.func_74768_a("AltarState", this.altarState.ordinal());
      }

      if (this.owner != null) {
         compound.func_186854_a("Owner", this.owner);
      }

      if (this.recipe != null) {
         compound.func_218657_a("Recipe", AltarInfusionRecipe.serialize(this.recipe));
      }

      compound.func_74768_a("InfusionTimer", this.infusionTimer);
      return super.func_189515_b(compound);
   }

   public void func_230337_a_(BlockState state, CompoundNBT compound) {
      if (!compound.func_74764_b("AltarState")) {
         this.migrate(compound.func_74767_n("containsVaultRock"));
      }

      if (compound.func_74764_b("AltarState")) {
         this.altarState = VaultAltarTileEntity.AltarState.values()[compound.func_74762_e("AltarState")];
      }

      if (compound.func_74764_b("Owner")) {
         this.owner = compound.func_186857_a("Owner");
      }

      if (compound.func_74764_b("Recipe")) {
         this.recipe = AltarInfusionRecipe.deserialize(compound.func_74775_l("Recipe"));
      }

      if (compound.func_74764_b("InfusionTimer")) {
         this.infusionTimer = compound.func_74762_e("InfusionTimer");
      }

      super.func_230337_a_(state, compound);
   }

   private void migrate(boolean containsVaultRock) {
      this.altarState = containsVaultRock ? VaultAltarTileEntity.AltarState.ACCEPTING : VaultAltarTileEntity.AltarState.IDLE;
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT tag = super.func_189517_E_();
      if (this.altarState != null) {
         tag.func_74768_a("AltarState", this.altarState.ordinal());
      }

      if (this.owner != null) {
         tag.func_186854_a("Owner", this.owner);
      }

      if (this.recipe != null) {
         tag.func_218657_a("Recipe", AltarInfusionRecipe.serialize(this.recipe));
      }

      tag.func_74768_a("InfusionTimer", this.infusionTimer);
      return tag;
   }

   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT tag = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), tag);
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(1) {
         protected void onContentsChanged(int slot) {
            VaultAltarTileEntity.this.sendUpdates();
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (PlayerVaultAltarData.get((ServerWorld)VaultAltarTileEntity.this.field_145850_b).getRecipe(VaultAltarTileEntity.this.owner) != null
               && !PlayerVaultAltarData.get((ServerWorld)VaultAltarTileEntity.this.field_145850_b).getRecipe(VaultAltarTileEntity.this.owner).isComplete()) {
               for (RequiredItem item : PlayerVaultAltarData.get((ServerWorld)VaultAltarTileEntity.this.field_145850_b)
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
            PlayerVaultAltarData data = PlayerVaultAltarData.get((ServerWorld)VaultAltarTileEntity.this.field_145850_b);
            if (data.getRecipe(VaultAltarTileEntity.this.owner) != null && !data.getRecipe(VaultAltarTileEntity.this.owner).isComplete()) {
               for (RequiredItem item : data.getRecipe(VaultAltarTileEntity.this.owner).getRequiredItems()) {
                  if (!item.reachedAmountRequired() && item.isItemEqual(stack)) {
                     int amount = stack.func_190916_E();
                     int excess = item.getRemainder(amount);
                     if (excess > 0) {
                        if (!simulate) {
                           item.setCurrentAmount(item.getAmountRequired());
                           data.func_76185_a();
                        }

                        return ItemHandlerHelper.copyStackWithSize(stack, excess);
                     }

                     if (!simulate) {
                        item.addAmount(amount);
                        data.func_76185_a();
                     }

                     return ItemStack.field_190927_a;
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
