package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.HunterChestsConfig;
import iskallia.vault.skill.ability.effect.HunterAbility;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HunterChestAbility extends HunterAbility<HunterChestsConfig> {
   protected List<Tuple<BlockPos, Color>> selectPositions(HunterChestsConfig config, World world, PlayerEntity player) {
      List<Tuple<BlockPos, Color>> entityPositions = new ArrayList<>();
      Color c = new Color(config.getColor(), false);
      this.forEachTileEntity(config, world, player, (pos, tile) -> {
         if (config.shouldHighlightTileEntity(tile)) {
            entityPositions.add(new Tuple(pos, c));
         }
      });
      return entityPositions;
   }
}
