package iskallia.vault.core.data.action;

import java.util.ArrayList;
import java.util.List;

public class ListTracker {
   private final List<ListAction> actions = new ArrayList<>();

   public List<ListAction> getActions() {
      return this.actions;
   }

   public void addAction(ListAction action) {
      if (action.type == ListAction.Type.CLEAR) {
         this.actions.clear();
         this.actions.add(action);
      } else if (action.type == ListAction.Type.SET) {
         ListTracker.Index tracker = new ListTracker.Index(action.index);

         for (int i = this.actions.size() - 1; i >= 0; i--) {
            ListAction target = this.actions.get(i);
            if (target.index != tracker.index) {
               tracker.previous(target);
            } else {
               if (target.type == ListAction.Type.SET || target.type == ListAction.Type.ADD || target.type == ListAction.Type.APPEND) {
                  target.value = action.value;
                  return;
               }

               tracker.previous(target);
            }
         }

         this.actions.add(action);
      } else if (action.type == ListAction.Type.REMOVE) {
         ListTracker.Index tracker = new ListTracker.Index(action.index);

         for (int ix = this.actions.size() - 1; ix >= 0; ix--) {
            ListAction target = this.actions.get(ix);
            if (target.index != tracker.index) {
               tracker.previous(target);
            } else {
               if (target.type == ListAction.Type.ADD || target.type == ListAction.Type.APPEND) {
                  ListTracker.Index anchor = new ListTracker.Index(target.index);

                  for (int j = ix + 1; j < this.actions.size(); j++) {
                     ListAction other = this.actions.get(j);
                     other.size--;
                     if (other.index > anchor.index) {
                        other.index--;
                     } else if (other.index == anchor.index && other.type == ListAction.Type.SET) {
                        this.actions.remove(j--);
                     }

                     anchor.next(other);
                  }

                  this.actions.remove(ix);
                  return;
               }

               tracker.previous(target);
            }
         }

         this.actions.add(action);
      } else {
         this.actions.add(action);
      }
   }

   private static class Index {
      private int index;

      public Index(int index) {
         this.index = index;
      }

      public void next(ListAction action) {
         if (this.index >= 0) {
            if (action.type == ListAction.Type.ADD) {
               if (this.index >= action.index) {
                  this.index++;
               }
            } else if (action.type == ListAction.Type.REMOVE && this.index == action.index) {
               this.index = -1;
            }
         }
      }

      public void previous(ListAction action) {
         if (this.index >= 0) {
            if (action.type == ListAction.Type.ADD) {
               if (this.index > action.index) {
                  this.index--;
               }
            } else if (action.type == ListAction.Type.REMOVE && this.index > action.index) {
               this.index--;
            }
         }
      }
   }
}
