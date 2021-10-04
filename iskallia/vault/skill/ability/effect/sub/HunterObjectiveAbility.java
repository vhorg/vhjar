package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModEntities;
import iskallia.vault.skill.ability.config.sub.HunterObjectiveConfig;
import iskallia.vault.skill.ability.effect.HunterAbility;
import java.awt.Color;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HunterObjectiveAbility extends HunterAbility<HunterObjectiveConfig> {
   @Override
   protected Predicate<LivingEntity> getEntityFilter() {
      return e -> e.func_70089_S() && !e.func_175149_v() && e.func_200600_R().equals(ModEntities.TREASURE_GOBLIN);
   }

   protected List<Tuple<BlockPos, Color>> selectPositions(HunterObjectiveConfig config, World world, PlayerEntity player) {
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
