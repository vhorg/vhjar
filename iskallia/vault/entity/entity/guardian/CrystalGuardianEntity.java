package iskallia.vault.entity.entity.guardian;

import iskallia.vault.entity.entity.guardian.helper.GuardianType;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CrystalGuardianEntity extends AbstractGuardianEntity {
   public static final EntityDataAccessor<Integer> DATA_COLOR_ID = SynchedEntityData.defineId(CrystalGuardianEntity.class, EntityDataSerializers.INT);

   public CrystalGuardianEntity(EntityType<? extends AbstractGuardianEntity> entityType, GuardianType type, Level world) {
      super(entityType, type, world);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_COLOR_ID, 0);
   }

   public CrystalGuardianEntity.Color getCrystalColor() {
      int colorIndex = (Integer)this.entityData.get(DATA_COLOR_ID);
      return CrystalGuardianEntity.Color.values()[colorIndex];
   }

   public void setCrystalColor(int colorIndex) {
      this.setCrystalColor(CrystalGuardianEntity.Color.values()[colorIndex]);
   }

   public void setCrystalColor(CrystalGuardianEntity.Color color) {
      if (color == null) {
         CrystalGuardianEntity.Color[] colors = CrystalGuardianEntity.Color.values();
         this.entityData.set(DATA_COLOR_ID, colors[this.level.getRandom().nextInt(colors.length)].ordinal());
      } else {
         this.entityData.set(DATA_COLOR_ID, color.ordinal());
      }
   }

   @Override
   public void readAdditionalSaveData(@Nonnull CompoundTag nbt) {
      super.readAdditionalSaveData(nbt);
      this.setCrystalColor(nbt.getInt("CrystalColorOrdinal"));
   }

   @Override
   public void addAdditionalSaveData(@Nonnull CompoundTag nbt) {
      super.addAdditionalSaveData(nbt);
      nbt.putInt("CrystalColorOrdinal", this.getCrystalColor().ordinal());
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.AMETHYST_BLOCK_CHIME;
   }

   @Override
   protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
      return SoundEvents.AMETHYST_BLOCK_HIT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.AMETHYST_BLOCK_BREAK;
   }

   protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockState) {
      this.playSound(SoundEvents.AMETHYST_BLOCK_STEP, 0.15F, 1.0F);
   }

   public static enum Color {
      BLUE,
      GREEN,
      ORANGE,
      VIOLET;
   }
}
