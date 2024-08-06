package iskallia.vault.antique.condition;

import com.google.gson.annotations.Expose;

public class DropSourceCondition {
   @Expose
   private DropConditionType type;
   @Expose
   private AntiqueCondition condition;

   public boolean isType(DropConditionContext context) {
      return context.getType() == this.type;
   }

   public boolean checkCondition(DropConditionContext context) {
      return this.condition.test(context);
   }
}
