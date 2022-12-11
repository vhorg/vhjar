package iskallia.vault.init;

import com.mojang.blaze3d.platform.InputConstants;
import iskallia.vault.skill.ability.group.AbilityGroup;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class ModKeybinds {
   public static KeyMapping openAbilityTree;
   public static KeyMapping abilityKey;
   public static KeyMapping abilityWheelKey;
   public static KeyMapping bountyStatusKey;
   public static Map<String, KeyMapping> abilityQuickfireKey = new HashMap<>();

   public static void register(FMLClientSetupEvent event) {
      openAbilityTree = createKeyBinding("open_ability_tree", 72);
      abilityKey = createKeyBinding("ability_key", 71);
      abilityWheelKey = createKeyBinding("ability_wheel_key", 342);
      bountyStatusKey = createKeyBinding("bounty_status_key");

      for (AbilityGroup<?, ?> group : ModConfigs.ABILITIES.getAll()) {
         String abilityDescription = group.getParentName().toLowerCase().replace(' ', '_');
         abilityQuickfireKey.put(group.getParentName(), createKeyBinding("quickselect." + abilityDescription));
      }
   }

   private static KeyMapping createKeyBinding(String name) {
      return createKeyBinding(name, InputConstants.UNKNOWN.getValue());
   }

   private static KeyMapping createKeyBinding(String name, int key) {
      KeyMapping keyBind = new KeyMapping("key.the_vault." + name, key, "key.category.the_vault");
      ClientRegistry.registerKeyBinding(keyBind);
      return keyBind;
   }
}
