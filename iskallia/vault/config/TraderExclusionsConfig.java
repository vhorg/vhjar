package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public class TraderExclusionsConfig extends Config {
   @Expose
   private List<String> exclusions = new ArrayList<>();
   @Expose
   private String _comment;

   @Override
   public String getName() {
      return "trader_exclusions";
   }

   @Override
   protected void reset() {
      this.exclusions.add("minecraft:diamond");
      this.exclusions.add("darkutils:*");
      this._comment = "To exclude an item from Wandering Traders, simply add the item ID (eg. \"minecraft:diamond\") or wildcard the id to exclude the entire mod. (eg. \"darkutils:*\"). The ID must be in one of these two formats.";
   }

   public boolean shouldExclude(ResourceLocation id) {
      return this.getExcludedNamespaces().contains(id.getNamespace()) ? true : this.exclusions.contains(id.toString());
   }

   private Set<String> getExcludedNamespaces() {
      return this.exclusions.stream().filter(s -> s.contains("*")).map(s -> s.split(":")[0]).collect(Collectors.toSet());
   }
}
