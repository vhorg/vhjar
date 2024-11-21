package iskallia.vault.entity.boss;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlackWidowBossEntity extends VaultBossEntity {
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(BlackWidowBossEntity.class, EntityDataSerializers.BYTE);

   public BlackWidowBossEntity(EntityType<? extends Monster> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   protected PathNavigation createNavigation(Level pLevel) {
      return new WallClimberNavigation(this, pLevel);
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
      return size.height * 0.4F;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level.isClientSide) {
         this.setClimbing(this.horizontalCollision);
      }
   }

   private void setClimbing(boolean pClimbing) {
      byte flags = (Byte)this.entityData.get(DATA_FLAGS_ID);
      if (pClimbing) {
         flags = (byte)(flags | 1);
      } else {
         flags = (byte)(flags & -2);
      }

      this.entityData.set(DATA_FLAGS_ID, flags);
   }

   public boolean onClimbable() {
      return this.isClimbing();
   }

   private boolean isClimbing() {
      return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void makeStuckInBlock(BlockState state, Vec3 motionMultiplier) {
      if (!state.is(Blocks.COBWEB)) {
         super.makeStuckInBlock(state, motionMultiplier);
      }
   }
}
