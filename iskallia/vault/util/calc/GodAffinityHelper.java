package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.DivineExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.world.data.PlayerExpertisesData;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class GodAffinityHelper {
   private static final Map<VaultGod, VaultGearAttribute<Float>> GOD_TO_ATTRIBUTE = new HashMap<>();
   private static final Map<VaultGod, PlayerStat> GOD_TO_STAT = new HashMap<>();

   public static float getAffinityPercent(LivingEntity entity, VaultGod god) {
      float affinity = 0.05F;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      affinity += snapshot.getAttributeValue(GOD_TO_ATTRIBUTE.get(god), VaultGearAttributeTypeMerger.floatSum());
      affinity = CommonEvents.PLAYER_STAT.invoke(GOD_TO_STAT.get(god), entity, affinity).getValue();
      if (entity instanceof ServerPlayer serverPlayer) {
         ExpertiseTree expertises = PlayerExpertisesData.get(serverPlayer.getLevel()).getExpertises(serverPlayer);
         float affinityIncrease = 0.0F;

         for (DivineExpertise expertise : expertises.getAll(DivineExpertise.class, Skill::isUnlocked)) {
            affinityIncrease += expertise.getAffinityIncrease();
         }

         affinity += affinityIncrease;
      }

      return affinity;
   }

   static {
      GOD_TO_ATTRIBUTE.put(VaultGod.VELARA, ModGearAttributes.VELARA_AFFINITY);
      GOD_TO_ATTRIBUTE.put(VaultGod.TENOS, ModGearAttributes.TENOS_AFFINITY);
      GOD_TO_ATTRIBUTE.put(VaultGod.WENDARR, ModGearAttributes.WENDARR_AFFINITY);
      GOD_TO_ATTRIBUTE.put(VaultGod.IDONA, ModGearAttributes.IDONA_AFFINITY);
      GOD_TO_STAT.put(VaultGod.VELARA, PlayerStat.VELARA_AFFINITY);
      GOD_TO_STAT.put(VaultGod.TENOS, PlayerStat.TENOS_AFFINITY);
      GOD_TO_STAT.put(VaultGod.WENDARR, PlayerStat.WENDARR_AFFINITY);
      GOD_TO_STAT.put(VaultGod.IDONA, PlayerStat.IDONA_AFFINITY);
   }
}
