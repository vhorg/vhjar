package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.calc.FatalStrikeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class FatalStrikeTalent extends PlayerTalent {
   @Expose
   private final float fatalStrikeChance;
   @Expose
   private final float fatalStrikeDamage;

   public FatalStrikeTalent(int cost, float fatalStrikeChance, float fatalStrikeDamage) {
      super(cost);
      this.fatalStrikeChance = fatalStrikeChance;
      this.fatalStrikeDamage = fatalStrikeDamage;
   }

   public float getFatalStrikeChance() {
      return this.fatalStrikeChance;
   }

   public float getFatalStrikeDamage() {
      return this.fatalStrikeDamage;
   }

   @SubscribeEvent
   public static void onPlayerAttack(LivingHurtEvent event) {
      LivingEntity attacked = event.getEntityLiving();
      if (!attacked.func_130014_f_().func_201670_d()) {
         Entity source = event.getSource().func_76346_g();
         float fatalChance;
         if (source instanceof ServerPlayerEntity) {
            fatalChance = FatalStrikeHelper.getPlayerFatalStrikeChance((ServerPlayerEntity)source);
         } else {
            if (!(source instanceof LivingEntity)) {
               return;
            }

            fatalChance = FatalStrikeHelper.getFatalStrikeChance((LivingEntity)source);
         }

         if (!(rand.nextFloat() >= fatalChance)) {
            float fatalPercentDamage;
            if (source instanceof ServerPlayerEntity) {
               fatalPercentDamage = FatalStrikeHelper.getPlayerFatalStrikeDamage((ServerPlayerEntity)source);
            } else {
               fatalPercentDamage = FatalStrikeHelper.getFatalStrikeDamage((LivingEntity)source);
            }

            float damage = event.getAmount() * (1.0F + fatalPercentDamage);
            event.setAmount(damage);
         }
      }
   }
}
