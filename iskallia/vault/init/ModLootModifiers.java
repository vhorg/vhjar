package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.loot.LootModifierAutoSmelt;
import iskallia.vault.loot.LootModifierDestructive;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModLootModifiers {
   public static void registerGlobalModifiers(Register<GlobalLootModifierSerializer<?>> event) {
      IForgeRegistry<GlobalLootModifierSerializer<?>> registry = event.getRegistry();
      registry.register(new LootModifierAutoSmelt.Serializer().setRegistryName(Vault.id("paxel_auto_smelt")));
      registry.register(new LootModifierDestructive.Serializer().setRegistryName(Vault.id("paxel_destructive")));
   }
}
