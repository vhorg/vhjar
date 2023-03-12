package iskallia.vault.config.bounty;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class BountyOresConfig extends Config {
   @Expose
   private HashMap<ResourceLocation, List<ResourceLocation>> ores = new HashMap<>();

   @Override
   public String getName() {
      return "bounty/ores";
   }

   @Override
   protected void reset() {
   }

   public List<ResourceLocation> getValidOres(ResourceLocation id) {
      return this.ores.computeIfAbsent(id, location -> new ArrayList<>());
   }
}
