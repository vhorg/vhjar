package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

public class VaultLootableWeightModifier extends VaultModifier<VaultLootableWeightModifier.Properties> {
   public VaultLootableWeightModifier(ResourceLocation id, VaultLootableWeightModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.PLACEHOLDER_GENERATION.register(context.getUUID(), data -> {
         if (data.getParent().target == data.getTile().getState().get(PlaceholderBlock.TYPE)) {
            data.setProbability(data.getProbability() * this.properties.multiplier);
         }
      });
   }

   public static class Properties {
      @Expose
      private final PlaceholderBlock.Type type;
      @Expose
      private final double multiplier;

      public Properties(PlaceholderBlock.Type type, double multiplier) {
         this.type = type;
         this.multiplier = multiplier;
      }

      public PlaceholderBlock.Type getType() {
         return this.type;
      }

      public double getMultiplier() {
         return this.multiplier;
      }
   }
}
