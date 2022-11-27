package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModelProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ModWeaponModels {
   private static final Map<ResourceLocation, ModWeaponModels.ModelDefinition> SWORDS = new HashMap<>();
   private static final Map<ResourceLocation, ModWeaponModels.ModelDefinition> AXES = new HashMap<>();
   public static final ModWeaponModels.ModelDefinition RED_SWORD = registerSword(
      new ModWeaponModels.ModelDefinition(VaultMod.id("gear/sword/red_sword"), "Red Sword")
         .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
   );
   public static final ModWeaponModels.ModelDefinition RED_AXE = registerAxe(
      new ModWeaponModels.ModelDefinition(VaultMod.id("gear/axe/red_axe"), "Red Axe")
         .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
   );

   public static Map<ResourceLocation, ModWeaponModels.ModelDefinition> getSwords() {
      return Collections.unmodifiableMap(SWORDS);
   }

   public static Map<ResourceLocation, ModWeaponModels.ModelDefinition> getAxes() {
      return Collections.unmodifiableMap(AXES);
   }

   @SubscribeEvent
   public static void stitchVaultArmorTextures(Pre event) {
   }

   @SubscribeEvent
   public static void bakeModels(ModelBakeEvent event) {
   }

   private static ModWeaponModels.ModelDefinition registerSword(ModWeaponModels.ModelDefinition definition) {
      SWORDS.put(definition.getId(), definition);
      return definition;
   }

   private static ModWeaponModels.ModelDefinition registerAxe(ModWeaponModels.ModelDefinition definition) {
      AXES.put(definition.getId(), definition);
      return definition;
   }

   public static class ModelDefinition {
      ResourceLocation id;
      String displayName;
      DynamicModelProperties modelProperties;

      public ModelDefinition(ResourceLocation id, String displayName) {
         this.id = id;
         this.displayName = displayName;
      }

      public ModWeaponModels.ModelDefinition properties(DynamicModelProperties modelProperties) {
         this.modelProperties = modelProperties;
         return this;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public DynamicModelProperties getModelProperties() {
         return this.modelProperties;
      }
   }
}
