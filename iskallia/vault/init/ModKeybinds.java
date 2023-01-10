package iskallia.vault.init;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import iskallia.vault.skill.ability.group.AbilityGroup;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ModKeybinds {
   public static final String KEY_CATEGORY = "key.category.the_vault";
   public static KeyMapping openAbilityTree;
   public static KeyMapping abilityKey;
   public static KeyMapping abilityWheelKey;
   public static KeyMapping bountyStatusKey;
   public static Map<String, KeyMapping> abilityQuickfireKey = new HashMap<>();

   public static void register(FMLClientSetupEvent event) {
      openAbilityTree = mapping(name("open_ability_tree"), KeyConflictContext.IN_GAME, key(Type.KEYSYM, 72));
      abilityKey = mapping(name("ability_key"), KeyConflictContext.IN_GAME, key(Type.KEYSYM, 71));
      abilityWheelKey = mapping(name("ability_wheel_key"), KeyConflictContext.IN_GAME, key(Type.KEYSYM, 342));
      bountyStatusKey = mapping(name("bounty_status_key"), KeyConflictContext.UNIVERSAL);

      for (AbilityGroup<?, ?> group : ModConfigs.ABILITIES.getAll()) {
         String name = "quickselect." + group.getParentName().toLowerCase().replace(' ', '_');
         abilityQuickfireKey.put(group.getParentName(), mapping(name(name), KeyConflictContext.IN_GAME));
      }
   }

   @NotNull
   private static String name(String name) {
      return "key.the_vault." + name;
   }

   private static Key key(Type inputType, int keyCode) {
      return inputType.getOrCreate(keyCode);
   }

   private static KeyMapping mapping(String description) {
      return mapping(description, KeyConflictContext.UNIVERSAL);
   }

   private static KeyMapping mapping(String description, IKeyConflictContext keyConflictContext) {
      return mapping(description, keyConflictContext, KeyModifier.NONE);
   }

   private static KeyMapping mapping(String description, IKeyConflictContext keyConflictContext, Key keyCode) {
      return mapping(description, keyConflictContext, KeyModifier.NONE, keyCode, "key.category.the_vault");
   }

   private static KeyMapping mapping(String description, IKeyConflictContext keyConflictContext, Key keyCode, String category) {
      return mapping(description, keyConflictContext, KeyModifier.NONE, keyCode, category);
   }

   private static KeyMapping mapping(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier) {
      return mapping(description, keyConflictContext, keyModifier, InputConstants.UNKNOWN);
   }

   private static KeyMapping mapping(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, Key keyCode) {
      return mapping(description, keyConflictContext, keyModifier, keyCode, "key.category.the_vault");
   }

   private static KeyMapping mapping(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, Key keyCode, String category) {
      KeyMapping keyMapping = new KeyMapping(description, keyConflictContext, keyCode, category);
      ClientRegistry.registerKeyBinding(keyMapping);
      return keyMapping;
   }
}
