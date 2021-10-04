package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.HunterConfig;
import java.util.List;
import net.minecraft.tileentity.TileEntity;

public class HunterSpawnerConfig extends HunterConfig {
   @Expose
   private final List<String> spawnerRegistryKeys;

   public HunterSpawnerConfig(int learningCost, double searchRadius, int color, int tickDuration, List<String> spawnerRegistryKeys) {
      super(learningCost, searchRadius, color, tickDuration);
      this.spawnerRegistryKeys = spawnerRegistryKeys;
   }

   public boolean shouldHighlightTileEntity(TileEntity tile) {
      return this.spawnerRegistryKeys.contains(tile.func_200662_C().getRegistryName().toString());
   }
}
