package iskallia.vault.entity.entity.guardian.helper;

import com.google.gson.annotations.Expose;
import iskallia.vault.entity.entity.guardian.AbstractGuardianEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.ItemStack;

public abstract class GuardianStats {
   public void onTick(AbstractGuardianEntity guardian) {
   }

   public void onHurt(AbstractGuardianEntity guardian, DamageSource source, float amount) {
   }

   public static class Arbalist extends GuardianStats {
      @Expose
      private ItemStack meleeItem;
      @Expose
      private ItemStack rangedItem;
      @Expose
      private GuardianStats.Arbalist.MeleeActivation meleeActivation;
      @Expose
      private double meleeActivationRange;
      @Expose
      private double meleeDeactivationRange;
      @Expose
      private double meleeActivationChance;

      public Arbalist(
         ItemStack meleeItem,
         ItemStack rangedItem,
         GuardianStats.Arbalist.MeleeActivation meleeActivation,
         double meleeActivationRange,
         double meleeDeactivationRange,
         double meleeActivationChance
      ) {
         this.meleeItem = meleeItem;
         this.rangedItem = rangedItem;
         this.meleeActivation = meleeActivation;
         this.meleeActivationRange = meleeActivationRange;
         this.meleeDeactivationRange = meleeDeactivationRange;
         this.meleeActivationChance = meleeActivationChance;
      }

      @Override
      public void onTick(AbstractGuardianEntity guardian) {
         if (guardian.getMainHandItem().isEmpty()) {
            guardian.setItemSlot(EquipmentSlot.MAINHAND, this.rangedItem.copy());
         }

         if (this.meleeActivation == GuardianStats.Arbalist.MeleeActivation.ALWAYS
            && guardian.getTarget() != null
            && guardian.getTarget().distanceTo(guardian) <= this.meleeActivationChance) {
            guardian.setItemSlot(EquipmentSlot.MAINHAND, this.meleeItem.copy());
            guardian.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, guardian.getTarget());
         }

         LivingEntity target = guardian.getTarget();
         LivingEntity temp = (LivingEntity)guardian.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
         if (target == null || temp != null && target.distanceTo(guardian) > temp.distanceTo(guardian)) {
            target = temp;
         }

         if (target != null && target.distanceTo(guardian) > this.meleeDeactivationRange) {
            guardian.setItemSlot(EquipmentSlot.MAINHAND, this.rangedItem.copy());
         }
      }

      @Override
      public void onHurt(AbstractGuardianEntity guardian, DamageSource source, float amount) {
         if (this.meleeActivation == GuardianStats.Arbalist.MeleeActivation.ON_HIT) {
            if (source.getEntity() != null && source.getEntity() instanceof LivingEntity attacker && attacker.distanceTo(guardian) <= this.meleeActivationRange
               )
             {
               guardian.setItemSlot(EquipmentSlot.MAINHAND, this.meleeItem.copy());
               guardian.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, attacker);
               guardian.setTarget(attacker);
            }
         }
      }

      public static enum MeleeActivation {
         NEVER,
         ON_HIT,
         ALWAYS;
      }
   }

   public static class Bruiser extends GuardianStats {
      @Expose
      private ItemStack meleeItem;

      public Bruiser(ItemStack meleeItem) {
         this.meleeItem = meleeItem;
      }

      @Override
      public void onTick(AbstractGuardianEntity guardian) {
         if (guardian.getMainHandItem().isEmpty()) {
            guardian.setItemSlot(EquipmentSlot.MAINHAND, this.meleeItem.copy());
         }
      }
   }

   public static class Empty extends GuardianStats {
   }
}
