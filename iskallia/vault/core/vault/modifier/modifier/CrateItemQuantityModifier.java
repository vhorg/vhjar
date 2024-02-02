package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;

public class CrateItemQuantityModifier extends VaultModifier<CrateItemQuantityModifier.Properties> {
   public CrateItemQuantityModifier(ResourceLocation id, CrateItemQuantityModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getPercentage() * s * 100.0F)));
   }

   @Override
   public void onVaultAdd(VirtualWorld world, Vault vault, ModifierContext context) {
      vault.get(Vault.OBJECTIVES)
         .forEach(
            AwardCrateObjective.class,
            objective -> {
               objective.set(
                  AwardCrateObjective.ITEM_QUANTITY,
                  Float.valueOf(objective.getOr(AwardCrateObjective.ITEM_QUANTITY, Float.valueOf(0.0F)) + this.properties.getPercentage())
               );
               return false;
            }
         );
   }

   public static class Properties {
      @Expose
      private final float percentage;

      public Properties(float percentage) {
         this.percentage = percentage;
      }

      public float getPercentage() {
         return this.percentage;
      }
   }
}
