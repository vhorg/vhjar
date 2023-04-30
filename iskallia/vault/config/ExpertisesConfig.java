package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.tree.ExpertiseTree;

public class ExpertisesConfig extends Config {
   @Expose
   public ExpertiseTree tree;

   @Override
   public String getName() {
      return "expertises";
   }

   public ExpertiseTree getAll() {
      return this.tree == null ? new ExpertiseTree() : this.tree;
   }

   @Override
   protected boolean isValid() {
      return this.tree != null;
   }

   @Override
   protected void reset() {
   }
}
