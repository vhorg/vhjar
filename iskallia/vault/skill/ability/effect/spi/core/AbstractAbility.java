package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.skill.ability.KeyBehavior;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import java.util.Random;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractAbility<C extends AbstractAbilityConfig> {
   protected static final Random RANDOM = new Random();
   protected final KeyBehavior keyBehavior;

   protected AbstractAbility(KeyBehavior keyBehavior) {
      this.keyBehavior = keyBehavior;
   }

   public KeyBehavior getKeyBehavior() {
      return this.keyBehavior;
   }

   public abstract String getAbilityGroupName();

   public void onAdded(C config, Player player) {
   }

   public void onRemoved(C config, Player player) {
   }

   public void onFocus(C config, Player player) {
   }

   public void onBlur(C config, Player player) {
   }

   public AbilityTickResult onTick(C config, ServerPlayer player, boolean active) {
      return AbilityTickResult.PASS;
   }

   public AbilityActionResult onAction(C config, ServerPlayer player, boolean active) {
      return AbilityActionResult.FAIL;
   }
}
