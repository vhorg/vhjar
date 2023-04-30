package iskallia.vault.core.data.compound;

import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.number.LegacySegmentedIntAdapter;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import java.util.ArrayList;
import java.util.List;

public class IntList extends DataList<IntList, Integer> {
   protected IntList(List<Integer> delegate, IBitAdapter<Integer, ?> adapter) {
      super(delegate, adapter);
   }

   public static IntList create() {
      return new IntList(new ArrayList<>(), Adapters.INT);
   }

   public static IntList create(int... ints) {
      List<Integer> delegate = new ArrayList<>();
      return new IntList(delegate, Adapters.INT);
   }

   public static IntList create(List<Integer> delegate) {
      return new IntList(delegate, Adapters.INT);
   }

   public static IntList createBounded(int bound) {
      return new IntList(new ArrayList<>(), Adapters.ofBoundedInt(bound));
   }

   public static IntList createBounded(List<Integer> delegate, int bound) {
      return new IntList(delegate, Adapters.ofBoundedInt(bound));
   }

   public static IntList createBounded(int min, int max) {
      return new IntList(new ArrayList<>(), Adapters.ofBoundedInt(min, max));
   }

   public static IntList createBounded(List<Integer> delegate, int min, int max) {
      return new IntList(delegate, Adapters.ofBoundedInt(min, max));
   }

   public static IntList createSegmented(int segment) {
      return new IntList(new ArrayList<>(), new LegacySegmentedIntAdapter(segment, false));
   }

   public static IntList createSegmented(List<Integer> delegate, int segment) {
      return new IntList(delegate, new LegacySegmentedIntAdapter(segment, false));
   }
}
