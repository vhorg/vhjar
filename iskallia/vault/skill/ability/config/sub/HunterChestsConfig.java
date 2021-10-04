package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.HunterConfig;
import java.util.List;
import net.minecraft.tileentity.TileEntity;

public class HunterChestsConfig extends HunterConfig {
   @Expose
   private final List<String> chestRegistryKeys;

   public HunterChestsConfig(int learningCost, double searchRadius, int color, int tickDuration, List<String> chestRegistryKeys) {
      super(learningCost, searchRadius, color, tickDuration);
      this.chestRegistryKeys = chestRegistryKeys;
   }

   public boolean shouldHighlightTileEntity(TileEntity tile) {
      return this.chestRegistryKeys.contains(tile.func_200662_C().getRegistryName().toString());
   }
}
