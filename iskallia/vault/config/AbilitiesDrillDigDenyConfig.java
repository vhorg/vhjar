package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class AbilitiesDrillDigDenyConfig extends Config {
   @Expose(
      deserialize = false
   )
   private final List<String> COMMENTS = new ArrayList<String>() {
      {
         this.add("Blocks added to DENY will not break with the Drill or Dig ability");
         this.add("Entries in DENY are formatted like minecraft:dirt");
      }
   };
   @Expose
   @SerializedName("DENY")
   private Set<ResourceLocation> blockDenySet;

   @Override
   public String getName() {
      return "abilities_drill_dig_deny";
   }

   @Override
   protected void reset() {
      this.blockDenySet = new HashSet<ResourceLocation>() {
         {
            this.add(ModBlocks.BLUE_PUZZLE_CONCRETE.getRegistryName());
            this.add(ModBlocks.GREEN_PUZZLE_CONCRETE.getRegistryName());
            this.add(ModBlocks.PINK_PUZZLE_CONCRETE.getRegistryName());
            this.add(ModBlocks.YELLOW_PUZZLE_CONCRETE.getRegistryName());
         }
      };
   }

   public boolean isBlockAllowed(Block block) {
      ResourceLocation resourceLocation = block.getRegistryName();
      return resourceLocation != null && !this.blockDenySet.contains(resourceLocation);
   }
}
