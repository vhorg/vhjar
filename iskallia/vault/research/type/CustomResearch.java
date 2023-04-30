package iskallia.vault.research.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.research.Restrictions;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class CustomResearch extends Research {
   @Expose
   protected Map<ItemPredicate, Restrictions> itemRestrictions = new HashMap<>();
   @Expose
   protected Map<String, Restrictions> blockRestrictions = new HashMap<>();
   @Expose
   protected Map<String, Restrictions> entityRestrictions = new HashMap<>();

   public CustomResearch(String name, int cost) {
      super(name, cost);
   }

   public Map<ItemPredicate, Restrictions> getItemRestrictions() {
      return this.itemRestrictions;
   }

   public Map<String, Restrictions> getBlockRestrictions() {
      return this.blockRestrictions;
   }

   public Map<String, Restrictions> getEntityRestrictions() {
      return this.entityRestrictions;
   }

   @Override
   public boolean restricts(ItemStack stack, Restrictions.Type restrictionType) {
      for (Entry<ItemPredicate, Restrictions> entry : this.itemRestrictions.entrySet()) {
         if (entry.getKey().test(stack)) {
            return entry.getValue().restricts(restrictionType);
         }
      }

      return false;
   }

   @Override
   public boolean restricts(Block block, Restrictions.Type restrictionType) {
      ResourceLocation registryName = block.getRegistryName();
      if (registryName == null) {
         return false;
      } else {
         String sid = registryName.getNamespace() + ":" + registryName.getPath();
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
         String sid = registryName.getNamespace() + ":" + registryName.getPath();
         Restrictions restrictions = this.entityRestrictions.get(sid);
         return restrictions == null ? false : restrictions.restricts(restrictionType);
      }
   }
}
