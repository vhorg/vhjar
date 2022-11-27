package iskallia.vault.core.data.compound;

import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.adapter.Adapter;
import java.util.ArrayList;
import java.util.List;

public class IntList extends DataList<IntList, Integer> {
   protected IntList(List<Integer> delegate, Adapter<Integer> adapter) {
      super(delegate, adapter);
   }

   public static IntList create() {
      return new IntList(new ArrayList<>(), Adapter.ofInt());
   }

   public static IntList create(int... ints) {
      List<Integer> delegate = new ArrayList<>();
      return new IntList(delegate, Adapter.ofInt());
   }

   public static IntList create(List<Integer> delegate) {
      return new IntList(delegate, Adapter.ofInt());
   }

   public static IntList createBounded(int bound) {
      return new IntList(new ArrayList<>(), Adapter.ofBoundedInt(bound));
   }

   public static IntList createBounded(List<Integer> delegate, int bound) {
      return new IntList(delegate, Adapter.ofBoundedInt(bound));
   }

   public static IntList createBounded(int min, int max) {
      return new IntList(new ArrayList<>(), Adapter.ofBoundedInt(min, max));
   }

   public static IntList createBounded(List<Integer> delegate, int min, int max) {
      return new IntList(delegate, Adapter.ofBoundedInt(min, max));
   }

   public static IntList createSegmented(int segment) {
      return new IntList(new ArrayList<>(), Adapter.ofSegmentedInt(segment));
   }

   public static IntList createSegmented(List<Integer> delegate, int segment) {
      return new IntList(delegate, Adapter.ofSegmentedInt(segment));
   }
}
