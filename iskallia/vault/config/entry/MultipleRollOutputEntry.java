package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MultipleRollOutputEntry {
   @Expose
   private MultipleRollOutputEntry.OutcomeBias outcomeBias;
   @Expose
   private int rollAttempts;

   public MultipleRollOutputEntry(MultipleRollOutputEntry.OutcomeBias outcomeBias, int rollAttempts) {
      this.outcomeBias = outcomeBias;
      this.rollAttempts = rollAttempts;
   }

   protected int getRollAttempts() {
      return this.rollAttempts;
   }

   protected MultipleRollOutputEntry.OutcomeBias getOutcomeBias() {
      return this.outcomeBias;
   }

   public Optional<Float> getOutcomeFloat(Supplier<Float> randomGen) {
      List<Float> rolls = new ArrayList<>(this.rollAttempts);

      for (int i = 0; i < this.rollAttempts; i++) {
         rolls.add(randomGen.get());
      }

      return this.outcomeBias.select(rolls);
   }

   public Optional<Integer> getOutcomeInt(Supplier<Integer> randomGen) {
      List<Integer> rolls = new ArrayList<>(this.rollAttempts);

      for (int i = 0; i < this.rollAttempts; i++) {
         rolls.add(randomGen.get());
      }

      return this.outcomeBias.select(rolls);
   }

   public static enum OutcomeBias {
      WORST((cmp, values) -> values.stream().sorted(cmp).findFirst().orElse(null)),
      BEST((cmp, values) -> values.stream().sorted(cmp.reversed()).findFirst().orElse(null));

      private final BiFunction<Comparator, List, Object> collapseFn;

      private OutcomeBias(BiFunction<Comparator, List, Object> collapseFn) {
         this.collapseFn = collapseFn;
      }

      public <T extends Comparable<T>> Optional<T> select(List<T> list) {
         return this.select(Comparator.naturalOrder(), list);
      }

      public <T> Optional<T> select(Comparator<T> cmp, List<T> list) {
         return list.isEmpty() ? Optional.empty() : Optional.ofNullable((T)this.collapseFn.apply(cmp, list));
      }
   }
}
