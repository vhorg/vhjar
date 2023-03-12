package iskallia.vault.util.calc;

import iskallia.vault.aura.ActiveAura;
import iskallia.vault.aura.AuraManager;
import iskallia.vault.aura.type.ResistanceAuraConfig;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.LowHealthResistanceTalent;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ResistanceHelper {
   public static float getResistance(LivingEntity entity) {
      return Mth.clamp(getResistanceUnlimited(entity), 0.0F, AttributeLimitHelper.getResistanceLimit(entity));
   }

   public static float getResistanceUnlimited(LivingEntity entity) {
      float resistancePercent = 0.0F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      resistancePercent += snapshot.getAttributeValue(ModGearAttributes.RESISTANCE, VaultGearAttributeTypeMerger.floatSum());
      if (entity.hasEffect(ModEffects.RESISTANCE)) {
         resistancePercent += (entity.getEffect(ModEffects.RESISTANCE).getAmplifier() + 1) / 100.0F;
      }

      for (ActiveAura aura : AuraManager.getInstance().getAurasAffecting(entity)) {
         if (aura.getAura() instanceof ResistanceAuraConfig) {
            resistancePercent += ((ResistanceAuraConfig)aura.getAura()).getAdditionalResistance();
         }
      }

      if (entity instanceof Player player) {
         boolean var10 = false;
      }

      if (entity instanceof ServerPlayer sPlayer) {
         TalentTree tree = PlayerTalentsData.get(sPlayer.getLevel()).getTalents(sPlayer);

         for (LowHealthResistanceTalent talent : tree.getTalents(LowHealthResistanceTalent.class)) {
            if (talent.shouldGetBenefits(sPlayer)) {
               resistancePercent += talent.getAdditionalResistance();
            }
         }
      }

      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.RESISTANCE, entity, resistancePercent).getValue();
   }
}
