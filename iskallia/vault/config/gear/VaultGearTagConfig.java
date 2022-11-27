package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.util.data.WeightedList;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class VaultGearTagConfig extends Config {
   @Expose
   private final Map<String, VaultGearTagConfig.ModGroupTag> tags = new LinkedHashMap<>();

   @Override
   public String getName() {
      return "gear%sgear_tags".formatted(File.separator);
   }

   @Nullable
   public VaultGearTagConfig.ModGroupTag getGroupTag(String tag) {
      return this.tags.get(tag);
   }

   public Set<String> getTags() {
      return Collections.unmodifiableSet(this.tags.keySet());
   }

   @Nullable
   public String getRandomTag() {
      WeightedList<String> tags = new WeightedList<>();
      this.tags.forEach((tag, groupCfg) -> tags.add(tag, groupCfg.getWeight()));
      return tags.getRandom(rand);
   }

   @Override
   protected void reset() {
      this.tags.clear();
      this.tags.put("Armor", new VaultGearTagConfig.ModGroupTag("Armor", 4766456).addGroup("ModArmor"));
   }

   public static class ModGroupTag {
      @Expose
      private List<String> groups = new ArrayList<>();
      @Expose
      private String display;
      @Expose
      private int color;
      @Expose
      private int weight = 10;

      public ModGroupTag(String display, int color) {
         this.display = display;
         this.color = color;
      }

      public VaultGearTagConfig.ModGroupTag addGroup(String modGroup) {
         this.groups.add(modGroup);
         return this;
      }

      public List<String> getGroups() {
         return this.groups;
      }

      public String getDisplayName() {
         return this.display;
      }

      public int getColor() {
         return this.color;
      }

      public int getWeight() {
         return this.weight;
      }

      public Component getDisplayComponent() {
         return new TextComponent(this.getDisplayName()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.getColor())));
      }
   }
}
