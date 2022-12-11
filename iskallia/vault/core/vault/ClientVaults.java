package iskallia.vault.core.vault;

import iskallia.vault.world.vault.modifier.modifier.GameControlsModifier;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ClientVaults {
   public static GameControlsModifier.Properties CONTROLS_PROPERTIES = new GameControlsModifier.Properties(true, true, true, false);
   public static Vault ACTIVE = new Vault();
}
