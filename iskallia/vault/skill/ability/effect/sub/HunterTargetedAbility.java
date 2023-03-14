package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.block.entity.base.HunterHiddenTileEntity;
import iskallia.vault.skill.ability.config.sub.HunterTargetedConfig;
import iskallia.vault.skill.ability.effect.HunterAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractHunterAbility;
import java.awt.Color;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class HunterTargetedAbility extends HunterAbility<HunterTargetedConfig> {
   protected List<AbstractHunterAbility.HighlightPosition> selectPositions(HunterTargetedConfig config, ServerLevel world, ServerPlayer player) {
      List<AbstractHunterAbility.HighlightPosition> chestPositions = super.selectPositions(config, world, player);
      Color c = new Color(config.getColor(), false);
      this.forEachTileEntity(config, world, player, (pos, tile) -> {
         if (config.shouldHighlightTileEntity(tile)) {
            if (tile instanceof HunterHiddenTileEntity hiddenTile && hiddenTile.isHidden()) {
               return;
            }

            chestPositions.add(new AbstractHunterAbility.HighlightPosition(pos, c));
         }
      });
      return chestPositions;
   }
}
