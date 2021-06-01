package iskallia.vault.research.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.research.Restrictions;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class CustomResearch extends Research {
   @Expose
   protected Map<String, Restrictions> itemRestrictions = new HashMap<>();
   @Expose
   protected Map<String, Restrictions> blockRestrictions = new HashMap<>();
   @Expose
   protected Map<String, Restrictions> entityRestrictions = new HashMap<>();

   public CustomResearch(String name, int cost) {
      super(name, cost);
   }

   public Map<String, Restrictions> getItemRestrictions() {
      return this.itemRestrictions;
   }

   public Map<String, Restrictions> getBlockRestrictions() {
      return this.blockRestrictions;
   }

   public Map<String, Restrictions> getEntityRestrictions() {
      return this.entityRestrictions;
   }

   @Override
   public boolean restricts(Item item, Restrictions.Type restrictionType) {
      ResourceLocation registryName = item.getRegistryName();
      if (registryName == null) {
         return false;
      } else {
         String sid = registryName.func_110624_b() + ":" + registryName.func_110623_a();
         Restrictions restrictions = this.itemRestrictions.get(sid);
         return restrictions == null ? false : restrictions.restricts(restrictionType);
      }
   }

   @Override
   public boolean restricts(Block block, Restrictions.Type restrictionType) {
      ResourceLocation registryName = block.getRegistryName();
      if (registryName == null) {
         return false;
      } else {
         String sid = registryName.func_110624_b() + ":" + registryName.func_110623_a();
         Restrictions restrictions = this.blockRestrictions.get(sid);
         return restrictions == null ? false : restrictions.restricts(restrictionType);
      }
   }

   @Override
   public boolean restricts(EntityType<?> entityType, Restrictions.Type restrictionType) {
      ResourceLocation registryName = entityType.getRegistryName();
      if (registryName == null) {
         return false;
      } else {
         String sid = registryName.func_110624_b() + ":" + registryName.func_110623_a();
         Restrictions restrictions = this.entityRestrictions.get(sid);
         return restrictions == null ? false : restrictions.restricts(restrictionType);
      }
   }
}
