package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.set.PlayerSet;
import net.minecraft.entity.player.PlayerEntity;

public abstract class PlayerAbility {
   @Expose
   private int cost;
   @Expose
   protected int cooldown;
   @Expose
   protected PlayerAbility.Behavior behavior;

   public PlayerAbility(int cost, PlayerAbility.Behavior behavior) {
      this.cost = cost;
      this.behavior = behavior;
      this.cooldown = 200;
   }

   public int getCost() {
      return this.cost;
   }

   public PlayerAbility.Behavior getBehavior() {
      return this.behavior;
   }

   public int getCooldown(PlayerEntity player) {
      return PlayerSet.isActive(VaultGear.Set.RIFT, player) ? this.cooldown / 2 : this.cooldown;
   }

   public void onAdded(PlayerEntity player) {
   }

   public void onFocus(PlayerEntity player) {
   }

   public void onBlur(PlayerEntity player) {
   }

   public void onTick(PlayerEntity player, boolean active) {
   }

   public void onAction(PlayerEntity player, boolean active) {
   }

   public void onRemoved(PlayerEntity player) {
   }

   public static enum Behavior {
      HOLD_TO_ACTIVATE,
      PRESS_TO_TOGGLE,
      RELEASE_TO_PERFORM;
   }
}
