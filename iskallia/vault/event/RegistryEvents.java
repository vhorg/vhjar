package iskallia.vault.event;

import iskallia.vault.antique.Antique;
import iskallia.vault.antique.AntiqueRegistry;
import iskallia.vault.antique.condition.AntiqueCondition;
import iskallia.vault.antique.condition.AntiqueConditionRegistry;
import iskallia.vault.antique.reward.AntiqueReward;
import iskallia.vault.antique.reward.AntiqueRewardTypeRegistry;
import iskallia.vault.etching.EtchingRegistry;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.charm.CharmEffect;
import iskallia.vault.gear.charm.CharmEffectRegistry;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationRegistry;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
import iskallia.vault.init.ModAbilities;
import iskallia.vault.init.ModAntiques;
import iskallia.vault.init.ModArchetypes;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModCharms;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModEtchings;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModGearModifications;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModModels;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.init.ModShaders;
import iskallia.vault.init.ModSounds;
import iskallia.vault.init.ModStructures;
import iskallia.vault.init.ModTrinkets;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.ArchetypeRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.NewRegistryEvent;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class RegistryEvents {
   @SubscribeEvent
   public static void onNewRegistryRegister(NewRegistryEvent event) {
      VaultGearAttributeRegistry.buildRegistry(event);
      EtchingRegistry.buildRegistry(event);
      TrinketEffectRegistry.buildRegistry(event);
      CharmEffectRegistry.buildRegistry(event);
      GearModificationRegistry.buildRegistry(event);
      ArchetypeRegistry.buildRegistry(event);
      AntiqueRegistry.buildRegistry(event);
      AntiqueConditionRegistry.buildRegistry(event);
      AntiqueRewardTypeRegistry.buildRegistry(event);
   }

   @SubscribeEvent
   public static void onBlockRegister(Register<Block> event) {
      ModBlocks.registerBlocks(event);
   }

   @SubscribeEvent
   public static void onItemRegister(Register<Item> event) {
      ModItems.registerItems(event);
      ModDynamicModels.initItemAssociations();
      ModDynamicModels.initCauldronWashables();
      ModBlocks.registerBlockItems(event);
      ModAbilities.init();
   }

   @SubscribeEvent
   public static void onModelRegister(ModelRegistryEvent event) {
      ModModels.setupRenderLayers();
      ModModels.ItemProperty.register();
      ModModels.ItemProperty.registerOverrides();
   }

   @SubscribeEvent
   public static void ohRegisterRenderers(RegisterRenderers event) {
      ModBlocks.registerTileEntityRenderers(event);
   }

   @SubscribeEvent
   public static void onRegisterShaders(RegisterShadersEvent event) {
      ModShaders.register(event);
   }

   @SubscribeEvent
   public static void onSoundRegister(Register<SoundEvent> event) {
      ModSounds.registerSounds(event);
   }

   @SubscribeEvent
   public static void onStructureRegister(Register<StructureFeature<?>> event) {
      ModStructures.register(event);
      ModFeatures.registerStructureFeatures();
   }

   @SubscribeEvent
   public static void onFeatureRegister(Register<Feature<?>> event) {
      ModFeatures.registerFeatures(event);
   }

   @SubscribeEvent
   public static void onContainerRegister(Register<MenuType<?>> event) {
      ModContainers.register(event);
   }

   @SubscribeEvent
   public static void onEntityRegister(Register<EntityType<?>> event) {
      ModEntities.register(event);
   }

   @SubscribeEvent
   public static void onTileEntityRegister(Register<BlockEntityType<?>> event) {
      ModBlocks.registerTileEntities(event);
   }

   @SubscribeEvent
   public static void onRecipeRegister(Register<RecipeSerializer<?>> event) {
      ModRecipes.Serializer.register(event);
   }

   @SubscribeEvent
   public static void onEffectRegister(Register<MobEffect> event) {
      ModEffects.register(event);
      MobEffects.DIG_SPEED.getAttributeModifiers().clear();
   }

   @SubscribeEvent
   public static void onAttributeRegister(Register<Attribute> event) {
      if (Attributes.MAX_HEALTH instanceof RangedAttribute attr) {
         attr.maxValue = Double.MAX_VALUE;
      }

      if (Attributes.ARMOR instanceof RangedAttribute attr) {
         attr.maxValue = Double.MAX_VALUE;
      }

      ModAttributes.register(event);
   }

   @SubscribeEvent
   public static void onGearAttributeRegistry(Register<VaultGearAttribute<?>> event) {
      ModGearAttributes.init(event);
      ModGearAttributes.registerVanillaAssociations();
   }

   @SubscribeEvent
   public static void onEtchingRegistry(Register<EtchingSet<?>> event) {
      ModEtchings.init(event);
   }

   @SubscribeEvent
   public static void onTrinketRegistry(Register<TrinketEffect<?>> event) {
      ModTrinkets.init(event);
   }

   @SubscribeEvent
   public static void onCharmRegistry(Register<CharmEffect<?>> event) {
      ModCharms.init(event);
   }

   @SubscribeEvent
   public static void onModificationRegistry(Register<GearModification> event) {
      ModGearModifications.init(event);
   }

   @SubscribeEvent
   public static void onArchetypesRegistry(Register<AbstractArchetype<?>> event) {
      ModArchetypes.init(event);
   }

   @SubscribeEvent
   public static void onAntiqueRegistry(Register<Antique> event) {
      ModAntiques.registerAntiques(event);
   }

   @SubscribeEvent
   public static void onAntiqueConditionProviderRegistry(Register<AntiqueCondition.Provider> event) {
      ModAntiques.Conditions.registerAntiqueConditions(event);
   }

   @SubscribeEvent
   public static void onAntiqueRewardProviderRegistry(Register<AntiqueReward.Provider> event) {
      ModAntiques.Rewards.registerAntiqueRewards(event);
   }
}
