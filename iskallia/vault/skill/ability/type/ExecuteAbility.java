package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class ExecuteAbility extends EffectAbility {
   @Expose
   private float damageMultiplier;

   public ExecuteAbility(int cost, Effect effect, int level, EffectAbility.Type type, PlayerAbility.Behavior behavior, float damageMultiplier) {
      super(cost, effect, level, type, behavior);
      this.damageMultiplier = damageMultiplier;
   }

   public float getDamageMultiplier() {
      return this.damageMultiplier;
   }

   @Override
   public void onAction(PlayerEntity player, boolean active) {
      EffectInstance activeEffect = player.func_70660_b(this.getEffect());
      EffectInstance newEffect = new EffectInstance(
         this.getEffect(), Integer.MAX_VALUE, this.getAmplifier(), false, this.getType().showParticles, this.getType().showIcon
      );
      if (activeEffect == null) {
         player.func_195064_c(newEffect);
      }
   }

   @SubscribeEvent
   public static void onDamage(LivingDamageEvent event) {
      if (!event.getEntity().field_70170_p.field_72995_K) {
         if (event.getSource().func_76346_g() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)event.getSource().func_76346_g();
            EffectInstance execute = player.func_70660_b(ModEffects.EXECUTE);
            if (execute != null) {
               float damageMultiplier = ModConfigs.ABILITIES.EXECUTE.getAbility(execute.func_76458_c() + 1).getDamageMultiplier();
               LivingEntity entity = event.getEntityLiving();
               float currentHealth = entity.func_110143_aJ();
               float health = entity.func_110138_aP();
               float damage = (health - currentHealth) * damageMultiplier;
               event.setAmount(damage);
               player.func_184614_ca().func_222118_a((int)damage, player, playerEntity -> {});
               player.field_70170_p
                  .func_184148_a(
                     null,
                     player.func_226277_ct_(),
                     player.func_226278_cu_(),
                     player.func_226281_cx_(),
                     SoundEvents.field_191244_bn,
                     SoundCategory.MASTER,
                     1.0F,
                     1.0F
                  );
               player.func_195063_d(ModEffects.EXECUTE);
            }
         }
      }
   }

   private static Vector3d getOffset(Direction direction) {
      switch (direction) {
         case SOUTH:
            return new Vector3d(0.0, 0.0, -1.0);
         case WEST:
            return new Vector3d(-1.0, 0.0, 0.0);
         case EAST:
            return new Vector3d(0.0, 0.0, 1.0);
         default:
            return new Vector3d(1.0, 0.0, 0.0);
      }
   }

   @Override
   public void onTick(PlayerEntity player, boolean active) {
   }

   @Override
   public void onBlur(PlayerEntity player) {
   }
}
