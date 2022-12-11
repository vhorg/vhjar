package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.HunterConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractHunterAbility;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

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

   @Override
   protected void doSound(C config, ServerPlayer player) {
      player.level.playSound(null, player.position().x, player.position().y, player.position().z, ModSounds.HUNTER_SFX, SoundSource.PLAYERS, 1.0F, 1.0F);
   }
}
