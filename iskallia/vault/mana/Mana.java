package iskallia.vault.mana;

import iskallia.vault.init.ModAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public final class Mana {
   public static float get(Player player) {
      return ((ManaPlayer)player).getMana();
   }

   public static float set(Player player, ManaAction action, float amount) {
      return ((ManaPlayer)player).setMana(action, amount);
   }

   public static float increase(Player player, ManaAction action, float amount) {
      return ((ManaPlayer)player).increaseMana(action, amount);
   }

   public static float decrease(Player player, ManaAction action, float amount) {
      return ((ManaPlayer)player).decreaseMana(action, amount);
   }

   public static float getMax(Player player) {
      return ((ManaPlayer)player).getManaMax();
   }

   public static float getRegenPerSecond(Player player) {
      return ((ManaPlayer)player).getManaRegenPerSecond();
   }

   public static AttributeInstance getManaMaxAttribute(Player player) {
      return player.getAttribute(ModAttributes.MANA_MAX);
   }

   public static AttributeInstance getManaRegenAttribute(Player player) {
      return player.getAttribute(ModAttributes.MANA_REGEN);
   }

   private Mana() {
   }
}
