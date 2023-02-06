package iskallia.vault.util;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityLeechEvent;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.calc.LeechHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerLeechHelper {
   @SubscribeEvent
   public static void onLivingHurt(LivingDamageEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (!attacker.getCommandSenderWorld().isClientSide()) {
            float leechMultiplier = 1.0F;
            if (!ActiveFlags.IS_AOE_ATTACKING.isSet() && !ActiveFlags.IS_DOT_ATTACKING.isSet() && !ActiveFlags.IS_REFLECT_ATTACKING.isSet()) {
               LivingEntity hurtEntity = event.getEntityLiving();
               float leech = LeechHelper.getLeechPercent(attacker);
               leech *= leechMultiplier;
               if (leech > 1.0E-4) {
                  float amountLeeched = event.getAmount() * leech;
                  leechHealth(attacker, amountLeeched);
                  CommonEvents.ENTITY_LEECH.invoke(new EntityLeechEvent.Data(attacker, hurtEntity, amountLeeched));
               }
            }
         }
      }
   }

   private static void leechHealth(LivingEntity attacker, float amountLeeched) {
      ActiveFlags.IS_LEECHING.runIfNotSet(() -> attacker.heal(amountLeeched));
      if (attacker.getRandom().nextFloat() <= 0.2) {
         float pitch = MathUtilities.randomFloat(1.0F, 1.5F);
         attacker.getCommandSenderWorld()
            .playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSounds.VAMPIRE_HISSING_SFX, SoundSource.MASTER, 0.020000001F, pitch);
      }
   }
}
