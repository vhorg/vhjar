package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;

public class ObjectiveTargetModifier extends VaultModifier<ObjectiveTargetModifier.Properties> {
   public ObjectiveTargetModifier(ResourceLocation id, ObjectiveTargetModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getIncrease() * s * 100.0)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.OBJECTIVE_TARGET.register(context.getUUID(), data -> {
         if (vault == data.getVault()) {
            data.setIncrease(data.getIncrease() + this.properties.getIncrease());
         }
      });
   }

   public static class Properties {
      @Expose
      private final double increase;

      public Properties(double increase) {
         this.increase = increase;
      }

      public double getIncrease() {
         return this.increase;
      }
   }
}
