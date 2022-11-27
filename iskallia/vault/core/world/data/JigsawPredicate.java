package iskallia.vault.core.world.data;

import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.world.template.JigsawTemplate;
import java.util.function.Predicate;

@FunctionalInterface
public interface JigsawPredicate extends Predicate<JigsawTemplate> {
   static JigsawPredicate of(JigsawTemplate template) {
      return _template -> _template == template;
   }

   static JigsawPredicate of(TemplateKey templateKey) {
      return _template -> _template.getKey() == templateKey;
   }
}
