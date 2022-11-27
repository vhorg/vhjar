package iskallia.vault.research.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.research.Restrictions;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class Research {
   @Expose
   protected String name;
   @Expose
   protected int cost;
   @Expose
   protected String gatedBy;
   @Expose
   protected List<String> discoversModels;

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

   public List<String> getDiscoversModels() {
      return this.discoversModels;
   }

   public abstract boolean restricts(Item var1, Restrictions.Type var2);

   public abstract boolean restricts(Block var1, Restrictions.Type var2);

   public abstract boolean restricts(EntityType<?> var1, Restrictions.Type var2);
}
