package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.util.calc.ThornsHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ThornsTalent extends PlayerTalent {
   @Expose
   private final float thornsChance;
   @Expose
   private final float thornsDamage;

   public ThornsTalent(int cost, float thornsChance, float thornsDamage) {
      super(cost);
      this.thornsChance = thornsChance;
      this.thornsDamage = thornsDamage;
   }

   public float getThornsChance() {
      return this.thornsChance;
   }

   public float getThornsDamage() {
      return this.thornsDamage;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onLivingAttack(LivingAttackEvent event) {
      LivingEntity hurt = event.getEntityLiving();
      if (!hurt.func_130014_f_().func_201670_d()) {
         Entity source = event.getSource().func_76346_g();
         if (source instanceof LivingEntity) {
            float thornsChance;
            if (hurt instanceof ServerPlayerEntity) {
               thornsChance = ThornsHelper.getPlayerThornsChance((ServerPlayerEntity)hurt);
            } else {
               thornsChance = ThornsHelper.getThornsChance(hurt);
            }

            if (!(rand.nextFloat() >= thornsChance)) {
               float thornsDamage;
               if (hurt instanceof ServerPlayerEntity) {
                  thornsDamage = ThornsHelper.getPlayerThornsDamage((ServerPlayerEntity)hurt);
               } else {
                  thornsDamage = ThornsHelper.getThornsDamage(hurt);
               }

               float dmg = (float)hurt.func_233637_b_(Attributes.field_233823_f_);
               ServerScheduler.INSTANCE.schedule(0, () -> source.func_70097_a(DamageSource.func_92087_a(hurt), dmg * (1.0F + thornsDamage)));
            }
         }
      }
   }
}
