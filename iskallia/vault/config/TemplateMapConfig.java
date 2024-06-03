package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TemplateMapConfig extends Config {
   @Expose
   private List<TemplateMapConfig.Entry> map;

   @Override
   public String getName() {
      return "template_map";
   }

   @Override
   protected void reset() {
      this.map = new ArrayList<>();
   }

   public static class Entry {
      @Expose
      private Set<ResourceLocation> templates = new LinkedHashSet<>();
      @Expose
      private ItemStack display = new ItemStack(ModItems.ERROR_ITEM);
   }
}
