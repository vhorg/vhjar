package iskallia.vault.entity.entity.plastic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PlasticZombieEntity extends Zombie {
   public static final EntityDataAccessor<Integer> TIER_ID = SynchedEntityData.defineId(PlasticZombieEntity.class, EntityDataSerializers.INT);
   private int tier;

   public PlasticZombieEntity(EntityType<? extends Zombie> entityType, Level level, int tier) {
      super(entityType, level);
      this.tier = tier;
      this.entityData.set(TIER_ID, this.tier);
   }

   public int getTier() {
      int tier = (Integer)this.entityData.get(TIER_ID);
      if (tier == 0) {
         this.setTier(this.tier);
         return this.tier;
      } else {
         return (Integer)this.entityData.get(TIER_ID);
      }
   }

   public void setTier(int tier) {
      this.tier = tier;
      this.entityData.set(TIER_ID, this.tier);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(TIER_ID, 1);
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
