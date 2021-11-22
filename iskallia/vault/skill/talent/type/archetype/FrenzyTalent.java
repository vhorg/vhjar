package iskallia.vault.skill.talent.type.archetype;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.PlayerDamageHelper;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class FrenzyTalent extends ArchetypeTalent {
   private static final Map<UUID, PlayerDamageHelper.DamageMultiplier> multiplierMap = new HashMap<>();
   @Expose
   protected float threshold;
   @Expose
   protected float damageMultiplier;

   public FrenzyTalent(int cost, float threshold, float damageMultiplier) {
      super(cost);
      this.threshold = threshold;
      this.damageMultiplier = damageMultiplier;
   }

   public float getThreshold() {
      return this.threshold;
   }

   public float getDamageMultiplier() {
      return this.damageMultiplier;
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase != Phase.END && event.player instanceof ServerPlayerEntity) {
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)event.player;
         TalentTree talents = PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer);
         float healthPart = sPlayer.func_110143_aJ() / sPlayer.func_110138_aP();
         boolean fulfillsFrenzyConditions = false;
         float damageMultiplier = 1.0F;

         for (TalentNode<FrenzyTalent> talentNode : talents.getLearnedNodes(FrenzyTalent.class)) {
            if (healthPart <= talentNode.getTalent().getThreshold()) {
               fulfillsFrenzyConditions = true;
               damageMultiplier = talentNode.getTalent().getDamageMultiplier();
               break;
            }
         }

         if (fulfillsFrenzyConditions && isEnabled(sPlayer.func_71121_q())) {
            PlayerDamageHelper.DamageMultiplier existing = multiplierMap.get(sPlayer.func_110124_au());
            if (existing != null) {
               if (existing.getMultiplier() == damageMultiplier) {
                  existing.refreshDuration(sPlayer.func_184102_h());
               } else {
                  PlayerDamageHelper.removeMultiplier(sPlayer, existing);
                  existing = null;
               }
            }

            if (existing == null) {
               existing = PlayerDamageHelper.applyMultiplier(sPlayer, damageMultiplier, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY);
               multiplierMap.put(sPlayer.func_110124_au(), existing);
            }
         } else {
            removeExistingDamageBuff(sPlayer);
         }
      }
   }

   private static void removeExistingDamageBuff(ServerPlayerEntity player) {
      PlayerDamageHelper.DamageMultiplier existing = multiplierMap.get(player.func_110124_au());
      if (existing != null) {
         PlayerDamageHelper.removeMultiplier(player, existing);
      }
   }
}
