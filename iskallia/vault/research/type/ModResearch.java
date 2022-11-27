package iskallia.vault.research.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.research.Restrictions;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModResearch extends Research {
   @Expose
   protected Set<String> modIds = new HashSet<>();
   @Expose
   protected Restrictions restrictions = Restrictions.forMods();

   public ModResearch(String name, int cost, String... modIds) {
      super(name, cost);
      Collections.addAll(this.modIds, modIds);
   }

   public Set<String> getModIds() {
      return this.modIds;
   }

   public Restrictions getRestrictions() {
      return this.restrictions;
   }

   public ModResearch withRestrictions(boolean hittability, boolean entityIntr, boolean blockIntr, boolean usability, boolean craftability) {
      this.restrictions.set(Restrictions.Type.HITTABILITY, hittability);
      this.restrictions.set(Restrictions.Type.ENTITY_INTERACTABILITY, entityIntr);
      this.restrictions.set(Restrictions.Type.BLOCK_INTERACTABILITY, blockIntr);
      this.restrictions.set(Restrictions.Type.USABILITY, usability);
      this.restrictions.set(Restrictions.Type.CRAFTABILITY, craftability);
      return this;
   }

   @Override
   public boolean restricts(Item item, Restrictions.Type restrictionType) {
      if (!this.restrictions.restricts(restrictionType)) {
         return false;
      } else {
         ResourceLocation registryName = item.getRegistryName();
         return registryName == null ? false : this.modIds.contains(registryName.getNamespace());
      }
   }

   @Override
   public boolean restricts(Block block, Restrictions.Type restrictionType) {
      if (!this.restrictions.restricts(restrictionType)) {
         return false;
      } else {
         ResourceLocation registryName = block.getRegistryName();
         return registryName == null ? false : this.modIds.contains(registryName.getNamespace());
      }
   }

   @Override
   public boolean restricts(EntityType<?> entityType, Restrictions.Type restrictionType) {
      if (!this.restrictions.restricts(restrictionType)) {
         return false;
      } else {
         ResourceLocation registryName = entityType.getRegistryName();
         return registryName == null ? false : this.modIds.contains(registryName.getNamespace());
      }
   }
}
