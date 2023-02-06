package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.vault.modifier.reputation.ScalarReputationProperty;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

public class VaultLootableWeightModifier extends VaultModifier<VaultLootableWeightModifier.Properties> {
   public VaultLootableWeightModifier(ResourceLocation id, VaultLootableWeightModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getChance() * s * 100.0)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.PLACEHOLDER_GENERATION.register(context.getUUID(), data -> {
         if (data.getVault() == vault) {
            if (data.getParent().target == data.getTile().getState().get(PlaceholderBlock.TYPE)) {
               data.setProbability(data.getProbability() + this.properties.getChance(context) * data.getBaseProbability());
            }
         }
      });
   }

   public static class Properties {
      @Expose
      private final PlaceholderBlock.Type type;
      @Expose
      private final double chance;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(PlaceholderBlock.Type type, double chance, ScalarReputationProperty reputation) {
         this.type = type;
         this.chance = chance;
         this.reputation = reputation;
      }

      public PlaceholderBlock.Type getType() {
         return this.type;
      }

      public double getChance() {
         return this.chance;
      }

      public double getChance(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.chance, context) : this.chance;
      }
   }
}
