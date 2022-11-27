package iskallia.vault.mana;

public interface ManaPlayer {
   float getMana();

   float setMana(float var1);

   float getManaMax();

   float getManaRegenPerSecond();

   float increaseMana(float var1);

   float decreaseMana(float var1);
}
