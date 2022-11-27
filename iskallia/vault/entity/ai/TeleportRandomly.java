package iskallia.vault.entity.ai;

import iskallia.vault.init.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

public class TeleportRandomly<T extends LivingEntity> implements INBTSerializable<CompoundTag> {
   protected T entity;
   private final TeleportRandomly.Condition<T>[] conditions;

   public TeleportRandomly(T entity) {
      this(entity);
   }

   public TeleportRandomly(T entity, TeleportRandomly.Condition<T>... conditions) {
      this.entity = entity;
      this.conditions = conditions;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      for (TeleportRandomly.Condition<T> condition : this.conditions) {
         double chance = condition.getChance(this.entity, source, amount);
         if (this.entity.level.random.nextDouble() < chance) {
            for (int i = 0; i < 64; i++) {
               if (this.teleportRandomly()) {
                  this.entity
                     .level
                     .playSound(null, this.entity.xo, this.entity.yo, this.entity.zo, ModSounds.BOSS_TP_SFX, this.entity.getSoundSource(), 1.0F, 1.0F);
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean teleportRandomly() {
      if (!this.entity.level.isClientSide() && this.entity.isAlive()) {
         double d0 = this.entity.getX() + (this.entity.level.random.nextDouble() - 0.5) * 64.0;
         double d1 = this.entity.getY() + (this.entity.level.random.nextInt(64) - 32);
         double d2 = this.entity.getZ() + (this.entity.level.random.nextDouble() - 0.5) * 64.0;
         return this.entity.randomTeleport(d0, d1, d2, true);
      } else {
         return false;
      }
   }

   public CompoundTag serializeNBT() {
      return new CompoundTag();
   }

   public void deserializeNBT(CompoundTag nbt) {
   }

   public static <T extends LivingEntity> TeleportRandomly<T> fromNBT(T entity, CompoundTag nbt) {
      TeleportRandomly<T> tp = new TeleportRandomly<>(entity);
      tp.deserializeNBT(nbt);
      return tp;
   }

   @FunctionalInterface
   public interface Condition<T extends LivingEntity> {
      double getChance(T var1, DamageSource var2, double var3);
   }
}
