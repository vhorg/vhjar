package iskallia.vault.util;

public class Counter {
   private int value;

   public Counter() {
      this(0);
   }

   public Counter(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public void decrement() {
      this.value--;
   }

   public void increment() {
      this.value++;
   }
}
