package iskallia.vault.mana;

public class FullManaPlayer implements ManaPlayer {
   public static ManaPlayer INSTANCE = new FullManaPlayer();

   @Override
   public float getMana() {
      return Float.MAX_VALUE;
   }

   @Override
   public float setMana(ManaAction action, float amount) {
      return Float.MAX_VALUE;
   }

   @Override
   public float getManaMax() {
      return Float.MAX_VALUE;
   }

   @Override
   public float getManaRegenPerSecond() {
      return Float.MAX_VALUE;
   }

   @Override
   public float increaseMana(ManaAction action, float amount) {
      return Float.MAX_VALUE;
   }

   @Override
   public float decreaseMana(ManaAction action, float amount) {
      return Float.MAX_VALUE;
   }
}
