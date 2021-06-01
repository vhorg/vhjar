package iskallia.vault.init;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class ModKeybinds {
   public static KeyBinding openAbilityTree;
   public static KeyBinding abilityKey;
   public static KeyBinding abilityWheelKey;

   public static void register(FMLClientSetupEvent event) {
      openAbilityTree = createKeyBinding("open_ability_tree", 72);
      abilityKey = createKeyBinding("ability_key", 71);
      abilityWheelKey = createKeyBinding("ability_wheel_key", 342);
      ClientRegistry.registerKeyBinding(openAbilityTree);
      ClientRegistry.registerKeyBinding(abilityKey);
      ClientRegistry.registerKeyBinding(abilityWheelKey);
   }

   private static KeyBinding createKeyBinding(String name, int key) {
      return new KeyBinding("key.the_vault." + name, key, "key.category.the_vault");
   }
}
