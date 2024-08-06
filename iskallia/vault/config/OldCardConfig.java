package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.card.Card;
import iskallia.vault.core.random.RandomSource;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class OldCardConfig extends Config {
   @Expose
   private Map<ResourceLocation, Card.Config> pools;

   @Override
   public String getName() {
      return "card";
   }

   public Card getRandom(ResourceLocation pool, int tier, RandomSource random) {
      return this.pools.get(pool).generate(tier, random);
   }

   @Override
   protected void reset() {
      this.pools = new LinkedHashMap<>();
   }
}
