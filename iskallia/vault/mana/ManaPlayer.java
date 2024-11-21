package iskallia.vault.mana;

import iskallia.vault.core.event.CommonEvents;

public interface ManaPlayer {
   float getMana();

   float setMana(ManaAction var1, float var2);

   float getManaMax();

   float getManaRegenPerSecond();

   float increaseMana(ManaAction var1, float var2);

   float decreaseMana(ManaAction var1, float var2);

   default void onModify(ManaAction action, float oldAmount, float newAmount) {
      CommonEvents.MANA_MODIFY.invoke(this, action, oldAmount, newAmount);
   }
}
