package iskallia.vault.util;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.calc.LeechHelper;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerLeechHelper {
   @SubscribeEvent
   public static void onLivingHurt(LivingDamageEvent event) {
      if (event.getSource().func_76346_g() instanceof LivingEntity) {
         LivingEntity attacker = (LivingEntity)event.getSource().func_76346_g();
         if (!attacker.func_130014_f_().func_201670_d()) {
            float leechMultiplier = 1.0F;
            if (attacker instanceof ServerPlayerEntity) {
               ServerPlayerEntity sPlayer = (ServerPlayerEntity)attacker;
               TalentTree talents = PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer);
               if (talents.hasLearnedNode(ModConfigs.TALENTS.WARD)) {
                  return;
               }
            }

            float leech;
            if (attacker instanceof ServerPlayerEntity) {
               leech = LeechHelper.getPlayerLeechPercent((ServerPlayerEntity)attacker);
            } else {
               leech = LeechHelper.getLeechPercent(attacker);
            }

            leech *= leechMultiplier;
            if (leech > 1.0E-4) {
               leechHealth(attacker, event.getAmount() * leech);
            }
         }
      }
   }

   private static void leechHealth(LivingEntity attacker, float amountLeeched) {
      ActiveFlags.IS_LEECHING.runIfNotSet(() -> attacker.func_70691_i(amountLeeched));
      if (attacker.func_70681_au().nextFloat() <= 0.2) {
         float pitch = MathUtilities.randomFloat(1.0F, 1.5F);
         attacker.func_130014_f_()
            .func_184148_a(
               null,
               attacker.func_226277_ct_(),
               attacker.func_226278_cu_(),
               attacker.func_226281_cx_(),
               ModSounds.VAMPIRE_HISSING_SFX,
               SoundCategory.MASTER,
               0.020000001F,
               pitch
            );
      }
   }
}
