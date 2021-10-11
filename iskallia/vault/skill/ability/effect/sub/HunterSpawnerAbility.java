package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.HunterSpawnerConfig;
import iskallia.vault.skill.ability.effect.HunterAbility;
import java.awt.Color;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HunterSpawnerAbility extends HunterAbility<HunterSpawnerConfig> {
   protected List<Tuple<BlockPos, Color>> selectPositions(HunterSpawnerConfig config, World world, PlayerEntity player) {
      List<Tuple<BlockPos, Color>> entityPositions = super.selectPositions(config, world, player);
      Color c = new Color(config.getColor(), false);
      this.forEachTileEntity(config, world, player, (pos, tile) -> {
         if (config.shouldHighlightTileEntity(tile)) {
            entityPositions.add(new Tuple(pos, c));
         }
      });
      return entityPositions;
   }
}