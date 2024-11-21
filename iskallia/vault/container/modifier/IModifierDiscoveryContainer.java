package iskallia.vault.container.modifier;

import java.util.List;

public interface IModifierDiscoveryContainer {
   List<DiscoverableModifier> getGearModifiers();

   void tryDiscoverModifier(DiscoverableModifier var1);
}
