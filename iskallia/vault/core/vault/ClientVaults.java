package iskallia.vault.core.vault;

import iskallia.vault.core.vault.modifier.modifier.GameControlsModifier;
import java.util.Optional;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ClientVaults {
   public static GameControlsModifier.Properties CONTROLS_PROPERTIES = new GameControlsModifier.Properties(true, true, true, false);
   public static Vault ACTIVE = new Vault();

   public static Optional<Vault> getActive() {
      return ACTIVE.has(Vault.ID) ? Optional.of(ACTIVE) : Optional.empty();
   }
}
