package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModArchetypes;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class ArchetypeGUIConfig extends Config {
   @Expose
   private Map<ResourceLocation, ArchetypeGUIConfig.ArchetypeStyle> styles;

   @Override
   public String getName() {
      return "archetype_gui_styles";
   }

   @Nullable
   public ArchetypeGUIConfig.ArchetypeStyle getStyle(ResourceLocation resourceLocation) {
      return this.styles.get(resourceLocation);
   }

   public Map<ResourceLocation, ArchetypeGUIConfig.ArchetypeStyle> getStyles() {
      return this.styles;
   }

   @Override
   protected void reset() {
      this.styles = new HashMap<>();
      int x = 0;
      int y = 0;
      int spacing = 166;
      this.styles.put(ModArchetypes.DEFAULT.getRegistryName(), new ArchetypeGUIConfig.ArchetypeStyle(x, y, VaultMod.id("builtin/player_face")));
      x += spacing;
      this.styles.put(ModArchetypes.BERSERKER.getRegistryName(), new ArchetypeGUIConfig.ArchetypeStyle(x, y, VaultMod.id("gui/archetypes/berserker")));
      x += spacing;
      this.styles.put(ModArchetypes.COMMANDER.getRegistryName(), new ArchetypeGUIConfig.ArchetypeStyle(x, y, VaultMod.id("gui/archetypes/commander")));
      x += spacing;
      this.styles
         .put(ModArchetypes.TREASURE_HUNTER.getRegistryName(), new ArchetypeGUIConfig.ArchetypeStyle(x, y, VaultMod.id("gui/archetypes/treasure_hunter")));
      x += spacing;
      this.styles.put(ModArchetypes.WARD.getRegistryName(), new ArchetypeGUIConfig.ArchetypeStyle(x, y, VaultMod.id("gui/archetypes/ward")));
      x += spacing;
      this.styles.put(ModArchetypes.BARBARIAN.getRegistryName(), new ArchetypeGUIConfig.ArchetypeStyle(x, y, VaultMod.id("gui/archetypes/barbarian")));
      x += spacing;
      this.styles.put(ModArchetypes.VAMPIRE.getRegistryName(), new ArchetypeGUIConfig.ArchetypeStyle(x, y, VaultMod.id("gui/archetypes/vampire")));
   }

   public static class ArchetypeStyle extends ArchetypeGUIConfig.IconStyle {
      @Expose
      private final int x;
      @Expose
      private final int y;

      public ArchetypeStyle(int x, int y, ResourceLocation icon) {
         super(icon);
         this.x = x;
         this.y = y;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }
   }

   public static class IconStyle {
      @Expose
      private final ResourceLocation icon;

      public IconStyle(ResourceLocation icon) {
         this.icon = icon;
      }

      public ResourceLocation getIcon() {
         return this.icon;
      }
   }
}
