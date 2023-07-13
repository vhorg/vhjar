package iskallia.vault.effect;

import iskallia.vault.init.ModEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class VulnerableEffect extends MobEffect {
   public VulnerableEffect(MobEffectCategory typeIn, int liquidColorIn, ResourceLocation id) {
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
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void on(LivingHurtEvent event) {
      if (event.getEntity() instanceof LivingEntity living) {
         MobEffectInstance effectInstance = living.getEffect(ModEffects.VULNERABLE);
         if (effectInstance != null) {
            event.setAmount(event.getAmount() + event.getAmount() * effectInstance.getAmplifier() * 0.1F);
         }
      }
   }
}
