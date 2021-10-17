package iskallia.vault.skill.ability.effect;

import iskallia.vault.skill.ability.config.AbilityConfig;
import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public abstract class AbilityEffect<C extends AbilityConfig> {
   protected static final Random rand = new Random();

   public abstract String getAbilityGroupName();

   public void onAdded(C config, PlayerEntity player) {
   }

   public void onRemoved(C config, PlayerEntity player) {
   }

   public void onFocus(C config, PlayerEntity player) {
   }

   public void onBlur(C config, PlayerEntity player) {
   }

   public void onTick(C config, PlayerEntity player, boolean active) {
   }

   public boolean onAction(C config, ServerPlayerEntity player, boolean active) {
      return false;
   }
}
