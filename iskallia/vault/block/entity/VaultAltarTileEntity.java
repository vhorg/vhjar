package iskallia.vault.block.entity;

import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemVaultCrystal;
import iskallia.vault.util.VectorHelper;
import iskallia.vault.world.data.PlayerVaultAltarData;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
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
   private boolean containsVaultRock = false;
   private int infusionTimer = -1;
   private boolean infusing;
   private ItemStackHandler itemHandler = this.createHandler();
   private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);

   public VaultAltarTileEntity() {
      super(ModBlocks.VAULT_ALTAR_TILE_ENTITY);
   }

   public void setContainsVaultRock(boolean containsVaultRock) {
      this.containsVaultRock = containsVaultRock;
   }

   public boolean containsVaultRock() {
      return this.containsVaultRock;
   }

   public int getInfusionTimer() {
      return this.infusionTimer;
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

   public boolean isInfusing() {
      return this.infusing;
   }

   public void setInfusing(boolean infusing) {
      this.infusing = infusing;
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
      if (world != null && !world.field_72995_K && this.containsVaultRock) {
         double x = this.func_174877_v().func_177958_n() + 0.5;
         double y = this.func_174877_v().func_177956_o() + 0.5;
         double z = this.func_174877_v().func_177952_p() + 0.5;
         PlayerVaultAltarData data = PlayerVaultAltarData.get((ServerWorld)world);
         this.pullNearbyItems(world, data, x, y, z, ModConfigs.VAULT_ALTAR.ITEM_RANGE_CHECK);
         if (this.infusing) {
            this.infusionTimer--;
         }

         if (this.infusionTimer == 0) {
            this.completeInfusion(world);
            this.sendUpdates();
         }

         this.recipe = data.getRecipe(this.owner);
         if (this.containsVaultRock && this.recipe == null && !this.infusing) {
            this.containsVaultRock = false;
            world.func_217376_c(
               new ItemEntity(
                  world,
                  this.func_174877_v().func_177958_n() + 0.5,
                  this.field_174879_c.func_177956_o() + 1.5,
                  this.field_174879_c.func_177952_p() + 0.5,
                  new ItemStack(ModItems.VAULT_ROCK)
               )
            );
         }

         if (world.func_82737_E() % 20L == 0L) {
            this.sendUpdates();
         }
      }
   }

   private void completeInfusion(World world) {
      this.containsVaultRock = false;
      this.recipe = null;
      this.infusionTimer = -1;
      this.infusing = false;
      ItemStack crystal = ItemVaultCrystal.getRandomCrystal();
      world.func_217376_c(
         new ItemEntity(
            world, this.func_174877_v().func_177958_n() + 0.5, this.field_174879_c.func_177956_o() + 1.5, this.field_174879_c.func_177952_p() + 0.5, crystal
         )
      );
   }

   public void startInfusionTimer(int seconds) {
      this.infusionTimer = seconds * 20;
   }

   private void pullNearbyItems(World world, PlayerVaultAltarData data, double x, double y, double z, double range) {
      if (data.getRecipe(this.owner) != null && !data.getRecipe(this.owner).getRequiredItems().isEmpty()) {
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
      compound.func_74757_a("containsVaultRock", this.containsVaultRock);
      if (this.owner != null) {
         compound.func_186854_a("Owner", this.owner);
      }

      if (this.recipe != null) {
         compound.func_218657_a("Recipe", AltarInfusionRecipe.serialize(this.recipe));
      }

      return super.func_189515_b(compound);
   }

   public void func_230337_a_(BlockState state, CompoundNBT compound) {
      this.containsVaultRock = compound.func_74767_n("containsVaultRock");
      if (compound.func_74764_b("Owner")) {
         this.owner = compound.func_186857_a("Owner");
      }

      if (compound.func_74764_b("Recipe")) {
         this.recipe = AltarInfusionRecipe.deserialize(compound.func_74775_l("Recipe"));
      }

      super.func_230337_a_(state, compound);
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT tag = super.func_189517_E_();
      tag.func_74757_a("containsVaultRock", this.containsVaultRock);
      if (this.owner != null) {
         tag.func_186854_a("Owner", this.owner);
      }

      if (this.recipe != null) {
         tag.func_218657_a("Recipe", AltarInfusionRecipe.serialize(this.recipe));
      }

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
            if (VaultAltarTileEntity.this.recipe != null && !VaultAltarTileEntity.this.recipe.getRequiredItems().isEmpty()) {
               for (RequiredItem item : VaultAltarTileEntity.this.recipe.getRequiredItems()) {
                  if (item.isItemEqual(stack)) {
                     return true;
                  }
               }
            }

            return false;
         }

         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (VaultAltarTileEntity.this.recipe != null && !VaultAltarTileEntity.this.recipe.getRequiredItems().isEmpty()) {
               for (RequiredItem item : VaultAltarTileEntity.this.recipe.getRequiredItems()) {
                  if (!item.reachedAmountRequired() && item.isItemEqual(stack)) {
                     int amount = stack.func_190916_E();
                     int excess = item.getRemainder(amount);
                     if (excess > 0) {
                        if (!simulate) {
                           item.setCurrentAmount(item.getAmountRequired());
                           PlayerVaultAltarData.get((ServerWorld)VaultAltarTileEntity.this.field_145850_b).func_76185_a();
                        }

                        return ItemHandlerHelper.copyStackWithSize(stack, excess);
                     }

                     if (!simulate) {
                        item.addAmount(stack.func_190916_E());
                        PlayerVaultAltarData.get((ServerWorld)VaultAltarTileEntity.this.field_145850_b).func_76185_a();
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
}
