package iskallia.vault.integration;

import iskallia.vault.Vault;
import iskallia.vault.init.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class IntegrationJEI implements IModPlugin {
   public void registerItemSubtypes(ISubtypeRegistration registration) {
      registration.useNbtForSubtypes(new Item[]{ModItems.RESPEC_FLASK, ModItems.RESET_FLASK});
   }

   public ResourceLocation getPluginUid() {
      return Vault.id("jei_integration");
   }
}
