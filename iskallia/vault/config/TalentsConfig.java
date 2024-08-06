package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.TalentTree;
import java.util.Optional;

public class TalentsConfig extends Config {
   @Expose
   public TalentTree tree;

   @Override
   public String getName() {
      return "talents";
   }

   public Optional<TalentTree> get() {
      return Optional.of(this.tree);
   }

   public Optional<Skill> getTalentById(String name) {
      return this.tree.getForId(name);
   }

   @Override
   protected boolean isValid() {
      return this.tree != null;
   }

   @Override
   protected void reset() {
   }
}
