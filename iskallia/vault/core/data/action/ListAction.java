package iskallia.vault.core.data.action;

import java.util.List;

public class ListAction {
   public int index;
   public Object value;
   public ListAction.Type type;
   public int size;

   public ListAction(int index, Object value, ListAction.Type type, int size) {
      this.index = index;
      this.value = value;
      this.type = type;
      this.size = size;
   }

   public static ListAction ofAppend(int size, Object value) {
      return new ListAction(size, value, ListAction.Type.APPEND, -1);
   }

   public static ListAction ofAdd(int index, Object value, int size) {
      return new ListAction(index, value, ListAction.Type.ADD, size);
   }

   public static ListAction ofSet(int index, Object value, int size) {
      return new ListAction(index, value, ListAction.Type.SET, size);
   }

   public static ListAction ofRemove(int index, int size) {
      return new ListAction(index, null, ListAction.Type.REMOVE, size);
   }

   public static ListAction ofClear() {
      return new ListAction(-1, null, ListAction.Type.CLEAR, -1);
   }

   public void apply(List list) {
      switch (this.type) {
         case APPEND:
            list.add(this.value);
            break;
         case ADD:
            list.add(this.index, this.value);
            break;
         case SET:
            list.set(this.index, this.value);
            break;
         case REMOVE:
            list.remove(this.index);
            break;
         case CLEAR:
            list.clear();
      }
   }

   public static enum Type {
      APPEND,
      ADD,
      SET,
      REMOVE,
      CLEAR;
   }
}
