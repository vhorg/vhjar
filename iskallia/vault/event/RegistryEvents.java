package iskallia.vault.event;

import iskallia.vault.init.ModAbilities;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModLootModifiers;
import iskallia.vault.init.ModModels;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.init.ModSounds;
import iskallia.vault.init.ModStructures;
import iskallia.vault.util.RelicSet;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class RegistryEvents {
   @SubscribeEvent
   public static void onBlockRegister(Register<Block> event) {
      ModConfigs.registerCompressionConfigs();
      ModBlocks.registerBlocks(event);
   }

   @SubscribeEvent
   public static void onItemRegister(Register<Item> event) {
      ModConfigs.registerCompressionConfigs();
      ModItems.registerItems(event);
      ModBlocks.registerBlockItems(event);
      RelicSet.register();
      ModAbilities.init();
   }

   @SubscribeEvent
   public static void onModelRegister(ModelRegistryEvent event) {
      ModModels.setupRenderLayers();
      ModModels.ItemProperty.register();
      ModModels.GearModel.register();
      ModModels.SpecialGearModel.register();
      ModModels.SpecialSwordModel.register();
   }

   @SubscribeEvent
   public static void onSoundRegister(Register<SoundEvent> event) {
      ModSounds.registerSounds(event);
      ModSounds.registerSoundTypes();
   }

   @SubscribeEvent
   public static void onStructureRegister(Register<Structure<?>> event) {
      ModStructures.register(event);
      ModFeatures.registerStructureFeatures();
   }

   @SubscribeEvent
   public static void onFeatureRegister(Register<Feature<?>> event) {
      ModFeatures.registerFeatures(event);
   }

   @SubscribeEvent
   public static void onContainerRegister(Register<ContainerType<?>> event) {
      ModContainers.register(event);
   }

   @SubscribeEvent
   public static void onEntityRegister(Register<EntityType<?>> event) {
      ModEntities.register(event);
   }

   @SubscribeEvent
   public static void onTileEntityRegister(Register<TileEntityType<?>> event) {
      ModBlocks.registerTileEntities(event);
   }

   @SubscribeEvent
   public static void onRecipeRegister(Register<IRecipeSerializer<?>> event) {
      ModRecipes.Serializer.register(event);
   }

   @SubscribeEvent
   public static void registerGlobalLootModifierSerializers(Register<GlobalLootModifierSerializer<?>> event) {
      ModLootModifiers.registerGlobalModifiers(event);
   }

   @SubscribeEvent
   public static void onEffectRegister(Register<Effect> event) {
      ModEffects.register(event);
   }

   @SubscribeEvent
   public static void onAttributeRegister(Register<Attribute> event) {
      Attribute attr = Attributes.field_233818_a_;
      if (attr instanceof RangedAttribute) {
         ((RangedAttribute)attr).field_111118_b = Double.MAX_VALUE;
      }

      ModAttributes.register(event);
   }
}
