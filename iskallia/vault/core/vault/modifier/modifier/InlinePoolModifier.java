package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModConfigs;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public class InlinePoolModifier extends VaultModifier<InlinePoolModifier.Properties> {
   public InlinePoolModifier(ResourceLocation id, InlinePoolModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @Override
   public Stream<VaultModifier<?>> flatten(RandomSource random) {
      return ModConfigs.VAULT_MODIFIER_POOLS.getRandom(this.properties.pool, this.properties.level, random).stream();
   }

   public static class Properties {
      @Expose
      private final ResourceLocation pool;
      @Expose
      private final int level;

      public Properties(ResourceLocation pool, int level) {
         this.pool = pool;
         this.level = level;
      }

      public ResourceLocation getPool() {
         return this.pool;
      }

      public int getLevel() {
         return this.level;
      }
   }
}
