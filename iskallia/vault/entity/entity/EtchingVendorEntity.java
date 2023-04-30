package iskallia.vault.entity.entity;

import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.container.inventory.EtchingTradeContainer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class EtchingVendorEntity extends Mob {
   private static final EntityDataAccessor<BlockPos> VENDOR_POS = SynchedEntityData.defineId(EtchingVendorEntity.class, EntityDataSerializers.BLOCK_POS);

   public EtchingVendorEntity(EntityType<? extends Mob> type, Level world) {
      super(type, world);
      this.setInvulnerable(true);
      this.setNoGravity(true);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(VENDOR_POS, BlockPos.ZERO);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector = new GoalSelector(this.level.getProfilerSupplier());
      this.targetSelector = new GoalSelector(this.level.getProfilerSupplier());
      this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
   }

   public void setVendorPos(BlockPos pos) {
      this.entityData.set(VENDOR_POS, pos);
   }

   public BlockPos getVendorPos() {
      return (BlockPos)this.entityData.get(VENDOR_POS);
   }

   public void tick() {
      super.tick();
      this.dropLeash(true, false);
      if (!this.level.isClientSide()) {
         if (!this.isValid()) {
            this.remove(RemovalReason.DISCARDED);
         }
      }
   }

   public boolean isValid() {
      if (!this.level.isAreaLoaded(this.getVendorPos(), 1)) {
         return false;
      } else if (this.distanceToSqr(Vec3.atCenterOf(this.getVendorPos())) > 4.0) {
         return false;
      } else {
         BlockEntity te = this.level.getBlockEntity(this.getVendorPos());
         return !(te instanceof EtchingVendorControllerTileEntity) ? false : ((EtchingVendorControllerTileEntity)te).getMonitoredEntityId() == this.getId();
      }
   }

   @Nullable
   public EtchingVendorControllerTileEntity getControllerTile() {
      return (EtchingVendorControllerTileEntity)this.level.getBlockEntity(this.getVendorPos());
   }

   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      if (player instanceof ServerPlayer) {
         NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
            public Component getDisplayName() {
               return new TextComponent("Etching Trader");
            }

            @Nullable
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerx) {
               return new EtchingTradeContainer(windowId, playerInventory, EtchingVendorEntity.this.getId());
            }
         }, buf -> buf.writeInt(this.getId()));
      }

      return InteractionResult.sidedSuccess(this.level.isClientSide);
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.VILLAGER_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.VILLAGER_HURT;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.VILLAGER_DEATH;
   }
}
