package iskallia.vault.core.world.template.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.IKeyed;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedTree;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;

public class TemplatePool extends WeightedTree<TemplateEntry> implements IKeyed<TemplatePool> {
   private static final Gson GSON = new GsonBuilder().registerTypeAdapter(TemplatePool.class, Adapters.TEMPLATE_POOL).setPrettyPrinting().create();
   protected VersionedKey<?, TemplatePool> key;
   protected String path;

   public static TemplatePool fromPath(String path) {
      TemplatePool templatePool;
      try {
         templatePool = (TemplatePool)GSON.fromJson(new FileReader(path), TemplatePool.class);
      } catch (FileNotFoundException var3) {
         return null;
      }

      templatePool.path = path;
      return templatePool;
   }

   public String getPath() {
      return this.path;
   }

   public Optional<TemplateEntry> getRandomFlat(Version version, RandomSource random) {
      return super.getRandom(random).map(entry -> entry.flatten(version, random));
   }

   @Override
   public VersionedKey<?, TemplatePool> getKey() {
      return this.key;
   }

   @Override
   public void setKey(VersionedKey<?, TemplatePool> key) {
      this.key = key;
   }
}
