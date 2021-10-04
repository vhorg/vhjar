package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.research.group.ResearchGroup;
import iskallia.vault.research.type.Research;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ResearchGroupConfig extends Config {
   @Expose
   protected Map<String, ResearchGroup> groups = new HashMap<>();

   @Nonnull
   public Map<String, ResearchGroup> getGroups() {
      return this.groups;
   }

   @Nullable
   public ResearchGroup getResearchGroup(Research research) {
      return this.getResearchGroup(research.getName());
   }

   @Nullable
   public ResearchGroup getResearchGroup(String research) {
      for (ResearchGroup group : this.getGroups().values()) {
         if (group.getResearch().contains(research)) {
            return group;
         }
      }

      return null;
   }

   @Nullable
   public ResearchGroup getResearchGroupById(String groupId) {
      return this.getGroups().get(groupId);
   }

   @Nullable
   public String getResearchGroupId(ResearchGroup group) {
      return this.getGroups().entrySet().stream().filter(entry -> entry.getValue().equals(group)).map(Entry::getKey).findAny().orElse(null);
   }

   @Override
   public String getName() {
      return "researches_groups";
   }

   @Override
   protected void reset() {
      this.groups.clear();
      this.groups
         .put(
            "StorageGroup",
            ResearchGroup.builder("Storage!")
               .withResearchNodes("Storage Noob", "Storage Refined", "Storage Energistic", "Storage Enthusiast")
               .withGlobalCostIncrease(0.5F)
               .withGroupCostIncrease("MagicGroup", 2.0F)
               .build()
         );
      this.groups
         .put(
            "MagicGroup",
            ResearchGroup.builder("Magic Thing(s)!")
               .withResearchNodes("Natural Magical")
               .withGlobalCostIncrease(0.5F)
               .withGroupCostIncrease("StorageGroup", 1.0F)
               .build()
         );
      this.groups
         .put(
            "DecorationGroup",
            ResearchGroup.builder("Decoration!")
               .withResearchNodes("Decorator", "Decorator Pro")
               .withGroupCostIncrease("StorageGroup", 1.5F)
               .withGroupCostIncrease("MagicGroup", 0.5F)
               .build()
         );
   }
}
