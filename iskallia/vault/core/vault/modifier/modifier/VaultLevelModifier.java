package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.core.vault.modifier.reputation.ScalarReputationProperty;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;

public class VaultLevelModifier extends VaultModifier<VaultLevelModifier.Properties> {
   public VaultLevelModifier(ResourceLocation id, VaultLevelModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted(p.levelAdded * s));
   }

   @Override
   public void onVaultAdd(VirtualWorld world, Vault vault, ModifierContext context) {
      vault.get(Vault.LEVEL).modify(VaultLevel.VALUE, level -> level + this.properties.levelAdded);
   }

   @Override
   public void onVaultRemove(VirtualWorld world, Vault vault, ModifierContext context) {
      vault.get(Vault.LEVEL).modify(VaultLevel.VALUE, level -> level - this.properties.levelAdded);
   }

   public static class Properties {
      @Expose
      private final int levelAdded;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(int levelAdded, ScalarReputationProperty reputation) {
         this.levelAdded = levelAdded;
         this.reputation = reputation;
      }

      public int getLevelAdded() {
         return this.levelAdded;
      }

      public int getLevelAdded(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.levelAdded, context) : this.levelAdded;
      }
   }
}
