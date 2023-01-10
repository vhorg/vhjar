package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.HunterConfig;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class HunterTargetedConfig extends HunterConfig {
   @Expose
   private final List<String> targetedChestType;

   public HunterTargetedConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCost,
      double searchRadius,
      int color,
      int tickDuration,
      List<String> targetedChestType
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost, searchRadius, color, tickDuration, Collections.emptyList());
      this.targetedChestType = targetedChestType;
   }

   @Override
   public boolean shouldHighlightTileEntity(BlockEntity tile) {
      ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(tile.getBlockState().getBlock());
      return blockId != null && this.targetedChestType.contains(blockId.toString());
   }
}
