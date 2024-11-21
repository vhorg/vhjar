package iskallia.vault.entity.entity.cave;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CaveSkeletonEntity extends Skeleton {
   public static final EntityDataAccessor<Integer> TIER_ID = SynchedEntityData.defineId(CaveSkeletonEntity.class, EntityDataSerializers.INT);
   private int tier;

   public CaveSkeletonEntity(EntityType<? extends Skeleton> entityType, Level level, int tier) {
      super(entityType, level);
      this.tier = tier;
      this.entityData.set(TIER_ID, this.tier);
   }

   public int getTier() {
      return this.tier;
   }

   public void setTier(int tier) {
      this.tier = tier;
      this.entityData.set(TIER_ID, this.tier);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(TIER_ID, this.tier);
   }

   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.setTier(tag.getInt("Tier"));
   }

   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("Tier", this.tier);
   }
}
