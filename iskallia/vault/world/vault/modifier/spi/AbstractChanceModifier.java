package iskallia.vault.world.vault.modifier.spi;

import com.google.gson.annotations.Expose;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractChanceModifier<P extends AbstractChanceModifier.Properties> extends VaultModifier<P> {
   public AbstractChanceModifier(ResourceLocation id, P properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getChance() * s * 100.0F)));
   }

   public static class Properties {
      @Expose
      private final float chance;

      public Properties(float chance) {
         this.chance = chance;
      }

      public float getChance() {
         return this.chance;
      }
   }
}
