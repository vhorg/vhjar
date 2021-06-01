package iskallia.vault.entity.ai;

import iskallia.vault.init.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.INBTSerializable;

public class TeleportRandomly<T extends LivingEntity> implements INBTSerializable<CompoundNBT> {
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
         if (this.entity.field_70170_p.field_73012_v.nextDouble() < chance) {
            for (int i = 0; i < 64; i++) {
               if (this.teleportRandomly()) {
                  System.out.println("TP!");
                  this.entity
                     .field_70170_p
                     .func_184148_a(
                        null,
                        this.entity.field_70169_q,
                        this.entity.field_70167_r,
                        this.entity.field_70166_s,
                        ModSounds.BOSS_TP_SFX,
                        this.entity.func_184176_by(),
                        1.0F,
                        1.0F
                     );
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean teleportRandomly() {
      if (!this.entity.field_70170_p.func_201670_d() && this.entity.func_70089_S()) {
         double d0 = this.entity.func_226277_ct_() + (this.entity.field_70170_p.field_73012_v.nextDouble() - 0.5) * 64.0;
         double d1 = this.entity.func_226278_cu_() + (this.entity.field_70170_p.field_73012_v.nextInt(64) - 32);
         double d2 = this.entity.func_226281_cx_() + (this.entity.field_70170_p.field_73012_v.nextDouble() - 0.5) * 64.0;
         return this.entity.func_213373_a(d0, d1, d2, true);
      } else {
         return false;
      }
   }

   public CompoundNBT serializeNBT() {
      return new CompoundNBT();
   }

   public void deserializeNBT(CompoundNBT nbt) {
   }

   public static <T extends LivingEntity> TeleportRandomly<T> fromNBT(T entity, CompoundNBT nbt) {
      TeleportRandomly<T> tp = new TeleportRandomly<>(entity);
      tp.deserializeNBT(nbt);
      return tp;
   }

   @FunctionalInterface
   public interface Condition<T extends LivingEntity> {
      double getChance(T var1, DamageSource var2, double var3);
   }
}
