package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractInstantManaConfig;
import java.util.List;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HunterConfig extends AbstractInstantManaConfig {
   @Expose
   private final double searchRadius;
   @Expose
   private final int color;
   @Expose
   private final int durationTicks;
   @Expose
   private final List<String> chestRegistryKeys;

   public HunterConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCost,
      double searchRadius,
      int color,
      int durationTicks,
      List<String> chestRegistryKeys
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost);
      this.searchRadius = searchRadius;
      this.color = color;
      this.durationTicks = durationTicks;
      this.chestRegistryKeys = chestRegistryKeys;
   }

   public double getSearchRadius() {
      return this.searchRadius;
   }

   public int getColor() {
      return this.color;
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }

   public boolean shouldHighlightTileEntity(BlockEntity tile) {
      return this.chestRegistryKeys.contains(tile.getType().getRegistryName().toString());
   }
}
