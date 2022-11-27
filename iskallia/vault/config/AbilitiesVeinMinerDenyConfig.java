package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AbilitiesVeinMinerDenyConfig extends Config {
   @Expose(
      deserialize = false
   )
   private final List<String> COMMENTS = new ArrayList<String>() {
      {
         this.add("Resource locations added to DENY_ITEMS will not trigger vein miner when used with vein miner");
         this.add("Entries in DENY_ITEMS are formatted like minecraft:wooden_pickaxe");
      }
   };
   @Expose
   @SerializedName("DENY_ITEMS")
   private Set<ResourceLocation> itemDenySet;

   @Override
   public String getName() {
      return "abilities_vein_miner_deny";
   }

   @Override
   protected void reset() {
      this.itemDenySet = new HashSet<>();
   }

   public boolean isItemDenied(ItemStack itemStack) {
      Item item = itemStack.getItem();
      ResourceLocation resourceLocation = item.getRegistryName();
      return resourceLocation != null && this.itemDenySet.contains(resourceLocation);
   }
}
