package iskallia.vault.effect;

import iskallia.vault.entity.entity.VaultGuardianEntity;
import iskallia.vault.init.ModEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ImmortalityEffect extends MobEffect {
   public ImmortalityEffect(ResourceLocation key, MobEffectCategory category, int color) {
      super(category, color);
      this.setRegistryName(key);
   }

   public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onHurt(LivingHurtEvent event) {
      if (event.getEntityLiving().hasEffect(ModEffects.IMMORTALITY)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onAttack(LivingAttackEvent event) {
      if (event.getEntityLiving().hasEffect(ModEffects.IMMORTALITY)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onAdded(PotionApplicableEvent event) {
      LivingEntity entity = event.getEntityLiving();
      if (event.getPotionEffect().getEffect() == ModEffects.IMMORTALITY && (entity instanceof Creeper || entity instanceof VaultGuardianEntity)) {
         event.setResult(Result.DENY);
      }
   }
}
