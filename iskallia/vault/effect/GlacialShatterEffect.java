package iskallia.vault.effect;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEffects;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.util.damage.AttackScaleHelper;
import iskallia.vault.util.damage.CritHelper;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class GlacialShatterEffect extends MobEffect {
   public GlacialShatterEffect(MobEffectCategory typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public boolean isInstantenous() {
      return false;
   }

   public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
      return pDuration % 3 == 0;
   }

   public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
      super.applyEffectTick(pLivingEntity, pAmplifier);
      MobEffectInstance effectInstance = pLivingEntity.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
      if (effectInstance == null || effectInstance.getAmplifier() < pAmplifier || effectInstance.getDuration() <= 3) {
         pLivingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, pAmplifier, true, true));
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void on(LivingHurtEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (!ActiveFlags.IS_AOE_ATTACKING.isSet()) {
            if (!ActiveFlags.IS_TOTEM_ATTACKING.isSet()) {
               if (!ActiveFlags.IS_CHARMED_ATTACKING.isSet()) {
                  if (!ActiveFlags.IS_DOT_ATTACKING.isSet()) {
                     if (!ActiveFlags.IS_REFLECT_ATTACKING.isSet()) {
                        if (!ActiveFlags.IS_EFFECT_ATTACKING.isSet()) {
                           if (event.getSource().getEntity() instanceof ServerPlayer player) {
                              if (!CritHelper.getCrit(player)) {
                                 if (!(AttackScaleHelper.getLastAttackScale(player) < 1.0F)) {
                                    MobEffectInstance effectInstance = mob.getEffect(ModEffects.GLACIAL_SHATTER);
                                    if (effectInstance != null) {
                                       ServerScheduler.INSTANCE
                                          .schedule(
                                             1,
                                             () -> {
                                                Entity entity = event.getEntity();
                                                if (event.getEntity() != null) {
                                                   BlockParticleOption particle = new BlockParticleOption(
                                                      ParticleTypes.BLOCK, Blocks.PACKED_ICE.defaultBlockState()
                                                   );
                                                   ((ServerLevel)event.getEntity().getLevel())
                                                      .sendParticles(
                                                         particle,
                                                         entity.position().x,
                                                         entity.position().y + mob.getBbHeight() / 2.0F,
                                                         entity.position().z,
                                                         200,
                                                         mob.getBbWidth() / 2.0F,
                                                         mob.getBbHeight() / 2.0F,
                                                         mob.getBbWidth() / 2.0F,
                                                         1.5
                                                      );
                                                   event.getEntity()
                                                      .getLevel()
                                                      .playSound(null, event.getEntity(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 0.75F);
                                                   event.getEntity().hurt(DamageSource.playerAttack(player), ((Mob)event.getEntity()).getMaxHealth() * 1.5F);
                                                }
                                             }
                                          );
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
