package iskallia.vault.research.group;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResearchGroup {
   @Expose
   protected String title = "";
   @Expose
   protected List<String> research = new ArrayList<>();
   @Expose
   protected float globalCostIncrease = 0.0F;
   @Expose
   protected Map<String, Float> groupCostIncrease = new HashMap<>();

   public static ResearchGroup.Builder builder(String title) {
      return new ResearchGroup.Builder(title);
   }

   public String getTitle() {
      return this.title;
   }

   public List<String> getResearch() {
      return this.research;
   }

   public float getGlobalCostIncrease() {
      return this.globalCostIncrease;
   }

   public float getGroupIncreasedResearchCost(String researchGroup) {
      return this.groupCostIncrease.getOrDefault(researchGroup, this.getGlobalCostIncrease());
   }

   public Map<String, Float> getGroupCostIncrease() {
      return this.groupCostIncrease;
   }

   public static class Builder {
      private final ResearchGroup group = new ResearchGroup();

      private Builder(String title) {
         this.group.title = title;
      }

      public ResearchGroup.Builder withResearchNodes(String... nodes) {
         this.group.research.addAll(Arrays.asList(nodes));
         return this;
      }

      public ResearchGroup.Builder withGlobalCostIncrease(float increase) {
         this.group.globalCostIncrease = increase;
         return this;
      }

      public ResearchGroup.Builder withGroupCostIncrease(String researchGroup, float increase) {
         this.group.groupCostIncrease.put(researchGroup, increase);
         return this;
      }

      public ResearchGroup build() {
         return this.group;
      }
   }
}
