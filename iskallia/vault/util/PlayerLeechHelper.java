package iskallia.vault.util;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityLeechEvent;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.calc.LeechHelper;
import iskallia.vault.util.damage.AttackScaleHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class PlayerLeechHelper {
   @SubscribeEvent
   public static void onLivingHurt(LivingDamageEvent event) {
      if (!event.getEntity().level.isClientSide() && event.getSource().getEntity() instanceof LivingEntity attacker) {
         onLeech(attacker, event.getEntityLiving(), event.getAmount(), LeechHelper.getLeechPercent(attacker));
      }
   }

   public static void onLeech(LivingEntity attacker, LivingEntity attacked, float damage, float leech) {
      if (!ActiveFlags.IS_AOE_ATTACKING.isSet()
         && !ActiveFlags.IS_DOT_ATTACKING.isSet()
         && !ActiveFlags.IS_REFLECT_ATTACKING.isSet()
         && !ActiveFlags.IS_JAVELIN_ATTACKING.isSet()
         && !ActiveFlags.IS_CHARMED_ATTACKING.isSet()
         && !ActiveFlags.IS_EFFECT_ATTACKING.isSet()
         && !ActiveFlags.IS_TOTEM_ATTACKING.isSet()) {
         if (!(attacker instanceof Player player && AttackScaleHelper.getLastAttackScale(player) < 1.0F)) {
            leech = Math.min(leech, damage / attacked.getMaxHealth());
            if (!(leech <= 1.0E-4)) {
               float amountLeeched = attacker.getMaxHealth() * leech;
               ActiveFlags.IS_LEECHING.runIfNotSet(() -> attacker.heal(amountLeeched));
               if (attacker.getRandom().nextFloat() <= 0.2) {
                  float pitch = MathUtilities.randomFloat(1.0F, 1.5F);
                  attacker.getCommandSenderWorld()
                     .playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSounds.VAMPIRE_HISSING_SFX, SoundSource.MASTER, 0.020000001F, pitch);
               }

               CommonEvents.ENTITY_LEECH.invoke(new EntityLeechEvent.Data(attacker, attacked, amountLeeched));
            }
         }
      }
   }
}
