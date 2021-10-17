package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.calc.ParryHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ParryTalent extends PlayerTalent {
   @Expose
   protected float additionalParryLimit;

   public ParryTalent(int cost, float additionalParryLimit) {
      super(cost);
      this.additionalParryLimit = additionalParryLimit;
   }

   public float getAdditionalParryLimit() {
      return this.additionalParryLimit;
   }

   @SubscribeEvent
   public static void onPlayerDamage(LivingAttackEvent event) {
      LivingEntity entity = event.getEntityLiving();
      World world = entity.func_130014_f_();
      if (!world.func_201670_d() && !event.getSource().func_76357_e()) {
         if (entity.field_70172_ad <= 10 || !(event.getAmount() < entity.field_110153_bc)) {
            float parryChance;
            if (entity instanceof ServerPlayerEntity) {
               parryChance = ParryHelper.getPlayerParryChance((ServerPlayerEntity)entity);
            } else {
               parryChance = ParryHelper.getParryChance(entity);
            }

            if (rand.nextFloat() <= parryChance) {
               world.func_184148_a(
                  null,
                  entity.func_226277_ct_(),
                  entity.func_226278_cu_(),
                  entity.func_226281_cx_(),
                  SoundEvents.field_187767_eL,
                  SoundCategory.MASTER,
                  0.5F,
                  1.0F
               );
               event.setCanceled(true);
               if (entity.field_70172_ad > 10) {
                  entity.field_110153_bc = event.getAmount();
               } else {
                  entity.field_70172_ad = 20;
                  entity.field_110153_bc = event.getAmount();
                  entity.field_70738_aO = 10;
                  entity.field_70737_aN = entity.field_70738_aO;
               }
            }
         }
      }
   }
}
