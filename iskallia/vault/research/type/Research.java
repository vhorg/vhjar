package iskallia.vault.research.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.research.Restrictions;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public abstract class Research {
   @Expose
   protected String name;
   @Expose
   protected int cost;
   @Expose
   protected boolean usesKnowledge;
   @Expose
   protected String gatedBy;

   public Research(String name, int cost) {
      this.name = name;
      this.cost = cost;
   }

   public String getName() {
      return this.name;
   }

   public int getCost() {
      return this.cost;
   }

   public boolean isGated() {
      return this.gatedBy != null;
   }

   public String gatedBy() {
      return this.gatedBy;
   }

   public boolean usesKnowledge() {
      return this.usesKnowledge;
   }

   public abstract boolean restricts(Item var1, Restrictions.Type var2);

   public abstract boolean restricts(Block var1, Restrictions.Type var2);

   public abstract boolean restricts(EntityType<?> var1, Restrictions.Type var2);
}
