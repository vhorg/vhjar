package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Modifiers;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public class GroupedModifier extends VaultModifier<GroupedModifier.Properties> {
   public GroupedModifier(ResourceLocation id, GroupedModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @Override
   public Stream<Modifiers.Entry> flatten(boolean display, RandomSource random) {
      return Stream.concat(
         this.properties.getChildren().stream().map(modifier -> new Modifiers.Entry((VaultModifier<?>)modifier, false)),
         Stream.of(new Modifiers.Entry(this, true))
      );
   }

   public static class Properties {
      @Expose
      private Map<String, Integer> children;

      public Properties(Map<String, Integer> children) {
         this.children = children;
      }

      public List<VaultModifier<?>> getChildren() {
         List<VaultModifier<?>> result = new ArrayList<>();

         for (Entry<String, Integer> entry : this.children.entrySet()) {
            VaultModifierRegistry.getOpt(new ResourceLocation(entry.getKey())).ifPresent(modifier -> {
               for (int i = 0; i < entry.getValue(); i++) {
                  result.add((VaultModifier<?>)modifier);
               }
            });
         }

         return result;
      }
   }
}
