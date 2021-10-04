package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.HunterConfig;
import java.util.List;
import net.minecraft.tileentity.TileEntity;

public class HunterObjectiveConfig extends HunterConfig {
   @Expose
   private final List<String> objectiveRegistryKeys;

   public HunterObjectiveConfig(int learningCost, double searchRadius, int color, int tickDuration, List<String> objectiveRegistryKeys) {
      super(learningCost, searchRadius, color, tickDuration);
      this.objectiveRegistryKeys = objectiveRegistryKeys;
   }

   public boolean shouldHighlightTileEntity(TileEntity tile) {
      return this.objectiveRegistryKeys.contains(tile.func_200662_C().getRegistryName().toString());
   }
}
