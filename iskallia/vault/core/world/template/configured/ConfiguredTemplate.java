package iskallia.vault.core.world.template.configured;

import iskallia.vault.core.world.template.EmptyTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;

public class ConfiguredTemplate {
   public static final ConfiguredTemplate EMPTY = new ConfiguredTemplate(EmptyTemplate.INSTANCE, new PlacementSettings());
   protected Template parent;
   protected PlacementSettings settings;

   public ConfiguredTemplate(Template parent, PlacementSettings settings) {
      this.parent = parent;
      this.settings = settings;
   }

   public Template getParent() {
      return this.parent;
   }

   public PlacementSettings getSettings() {
      return this.settings;
   }

   public void place(ServerLevelAccessor world, ChunkPos pos) {
      this.parent.place(world, this.settings);
   }

   @FunctionalInterface
   public interface Factory<T extends ConfiguredTemplate> {
      T create(Template var1, PlacementSettings var2);
   }
}
