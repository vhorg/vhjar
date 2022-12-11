package iskallia.vault;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {
   public void connect() {
      Mixins.addConfigurations(new String[]{"assets/the_vault/the_vault.mixins.json", "assets/the_vault/the_vault.integration.mixins.json"});
   }
}
