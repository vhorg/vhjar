package iskallia.vault.mana;

import iskallia.vault.core.event.CommonEvents;

public interface ManaPlayer {
   float getMana();

   float setMana(float var1);

   float getManaMax();

   float getManaRegenPerSecond();

   float increaseMana(float var1);

   float decreaseMana(float var1);

   default void onModify(float oldAmount, float newAmount) {
      CommonEvents.MANA_MODIFY.invoke(this, oldAmount, newAmount);
   }
}
