package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.reputation.ScalarReputationProperty;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.time.modifier.ModifierExtension;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;

public class VaultTimeModifier extends VaultModifier<VaultTimeModifier.Properties> {
   public VaultTimeModifier(ResourceLocation id, VaultTimeModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> {
         int minutes = Math.abs(p.timeAddedTicks / 20 / 60 * s);
         return t.formatted(minutes, minutes > 1 ? "s" : "");
      });
   }

   @Override
   public void onVaultAdd(VirtualWorld world, Vault vault, ModifierContext context) {
      vault.get(Vault.CLOCK).addModifier(new ModifierExtension(this.properties.timeAddedTicks));
   }

   @Override
   public void onVaultRemove(VirtualWorld world, Vault vault, ModifierContext context) {
      vault.get(Vault.CLOCK).addModifier(new ModifierExtension(-this.properties.timeAddedTicks));
   }

   public static class Properties {
      @Expose
      private final int timeAddedTicks;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(int timeAddedTicks, ScalarReputationProperty reputation) {
         this.timeAddedTicks = timeAddedTicks;
         this.reputation = reputation;
      }

      public int getTimeAddedTicks() {
         return this.timeAddedTicks;
      }

      public int getTimeAddedTicks(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.timeAddedTicks, context) : this.timeAddedTicks;
      }
   }
}
