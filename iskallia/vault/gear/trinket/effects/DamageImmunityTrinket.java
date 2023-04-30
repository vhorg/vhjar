package iskallia.vault.gear.trinket.effects;

import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.init.ModEffects;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class DamageImmunityTrinket extends TrinketEffect.Simple {
   private final Predicate<DamageSource> dmgImmunityFilter;
   private final boolean isFireDamage;

   public DamageImmunityTrinket(ResourceLocation name, Predicate<DamageSource> dmgImmunityFilter, boolean isFireDamage) {
      super(name);
      this.dmgImmunityFilter = dmgImmunityFilter;
      this.isFireDamage = isFireDamage;
   }

   public boolean isFireDamage() {
      return this.isFireDamage;
   }

   @SubscribeEvent
   public static void onPotionEffect(PotionApplicableEvent event) {
      if (event.getEntityLiving() instanceof Player player) {
         TrinketHelper.getTrinkets(player, DamageImmunityTrinket.class).forEach(immunityTrinket -> {
            if (!event.getPotionEffect().getEffect().isBeneficial() && event.getPotionEffect().getEffect() != ModEffects.TIMER_ACCELERATION) {
               event.setResult(Result.DENY);
            }
         });
      }
   }

   @SubscribeEvent
   public static void onAttack(LivingAttackEvent event) {
      if (event.getEntityLiving() instanceof Player player) {
         TrinketHelper.getTrinkets(player, DamageImmunityTrinket.class).forEach(immunityTrinket -> {
            if (immunityTrinket.isUsable(player) && immunityTrinket.trinket().dmgImmunityFilter.test(event.getSource())) {
               event.setCanceled(true);
            }
         });
      }
   }
}
