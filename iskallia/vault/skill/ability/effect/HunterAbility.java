package iskallia.vault.skill.ability.effect;

import iskallia.vault.skill.ability.config.HunterConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractHunterAbility;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class HunterAbility<C extends HunterConfig> extends AbstractHunterAbility<C> {
   @Override
   protected List<AbstractHunterAbility.HighlightPosition> selectPositions(C config, ServerLevel world, ServerPlayer player) {
      List<AbstractHunterAbility.HighlightPosition> result = new ArrayList<>();
      Color c = new Color(config.getColor(), false);
      this.forEachTileEntity(config, world, player, (pos, tile) -> {
         if (config.shouldHighlightTileEntity(tile)) {
            result.add(new AbstractHunterAbility.HighlightPosition(pos, c));
         }
      });
      return result;
   }
}
