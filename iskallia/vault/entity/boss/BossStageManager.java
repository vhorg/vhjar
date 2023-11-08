package iskallia.vault.entity.boss;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;

public class BossStageManager {
   private static Map<String, BiFunction<ArtifactBossEntity, CompoundTag, IBossStage>> STAGE_FACTORIES = new HashMap<>();
   private static Map<String, BossStageManager.StageAttributesFactory> STAGE_ATTRIBUTE_FACTORIES = new HashMap<>();

   public static IBossStage createStageFrom(ArtifactBossEntity artifactBossEntity, CompoundTag tag) {
      return STAGE_FACTORIES.get(tag.getString("StageType")).apply(artifactBossEntity, tag);
   }

   static IBossStage createStageFromAttributes(ArtifactBossEntity artifactBossEntity, String stageType, CompoundTag tag) {
      return STAGE_ATTRIBUTE_FACTORIES.get(stageType).create(artifactBossEntity, tag);
   }

   static {
      STAGE_FACTORIES.put("summoning", SummoningStage::from);
      STAGE_FACTORIES.put("spark", SparkStage::from);
      STAGE_FACTORIES.put("melee", MeleeStage::from);
      STAGE_FACTORIES.put("berserk", BerserkStage::from);
      STAGE_FACTORIES.put("catalyst", CatalystStage::from);
      STAGE_ATTRIBUTE_FACTORIES.put("summoning", SummoningStage::fromAttributes);
      STAGE_ATTRIBUTE_FACTORIES.put("spark", SparkStage::fromAttributes);
      STAGE_ATTRIBUTE_FACTORIES.put("melee", MeleeStage::fromAttributes);
      STAGE_ATTRIBUTE_FACTORIES.put("berserk", BerserkStage::fromAttributes);
      STAGE_ATTRIBUTE_FACTORIES.put("catalyst", CatalystStage::fromAttributes);
   }

   public interface StageAttributesFactory {
      IBossStage create(ArtifactBossEntity var1, CompoundTag var2);
   }
}
