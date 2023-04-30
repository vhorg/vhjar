package iskallia.vault.core.vault.modifier.spi;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.modifier.reputation.ScalarReputationProperty;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractChanceModifier<P extends AbstractChanceModifier.Properties> extends VaultModifier<P> {
   public AbstractChanceModifier(ResourceLocation id, P properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getChance() * s * 100.0F)));
   }

   public static class Properties {
      @Expose
      private final float chance;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(float chance, ScalarReputationProperty reputation) {
         this.chance = chance;
         this.reputation = reputation;
      }

      public float getChance() {
         return this.chance;
      }

      public float getChance(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.chance, context) : this.chance;
      }
   }
}
