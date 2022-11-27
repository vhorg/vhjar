package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.HunterConfig;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HunterObjectiveConfig extends HunterConfig {
   @Expose
   private final List<String> objectiveRegistryKeys;

   public HunterObjectiveConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCost,
      double searchRadius,
      int color,
      int tickDuration,
      List<String> objectiveRegistryKeys
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost, searchRadius, color, tickDuration, Collections.emptyList());
      this.objectiveRegistryKeys = objectiveRegistryKeys;
   }

   @Override
   public boolean shouldHighlightTileEntity(BlockEntity tile) {
      return this.objectiveRegistryKeys.contains(tile.getType().getRegistryName().toString());
   }
}
