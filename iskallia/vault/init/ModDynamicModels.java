package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicBakedModel;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.DynamicModelProperties;
import iskallia.vault.dynamodel.baked.JsonFileBakedModel;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.dynamodel.model.item.HandHeldModel;
import iskallia.vault.dynamodel.model.item.PlainItemModel;
import iskallia.vault.dynamodel.model.item.shield.AbsoluteBinnerShieldModel;
import iskallia.vault.dynamodel.model.item.shield.BellShieldModel;
import iskallia.vault.dynamodel.model.item.shield.CoconutShieldModel;
import iskallia.vault.dynamodel.model.item.shield.ShieldModel;
import iskallia.vault.dynamodel.registry.ArmorPieceModelRegistry;
import iskallia.vault.dynamodel.registry.DynamicModelRegistries;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.gear.model.armor.layers.AngelArmorLayers;
import iskallia.vault.gear.model.armor.layers.ArcadeArmorLayers;
import iskallia.vault.gear.model.armor.layers.ArmourIdonaArmorLayers;
import iskallia.vault.gear.model.armor.layers.ArmourTenosArmorLayers;
import iskallia.vault.gear.model.armor.layers.ArmourVelaraArmorLayers;
import iskallia.vault.gear.model.armor.layers.ArmourWendarrArmorLayers;
import iskallia.vault.gear.model.armor.layers.AtomaticArmorLayers;
import iskallia.vault.gear.model.armor.layers.BardArmorLayers;
import iskallia.vault.gear.model.armor.layers.BoneDragonArmorLayers;
import iskallia.vault.gear.model.armor.layers.BotaniaArmorLayers;
import iskallia.vault.gear.model.armor.layers.BuilderArmorLayers;
import iskallia.vault.gear.model.armor.layers.BumboCactoniArmorLayers;
import iskallia.vault.gear.model.armor.layers.CakeArmorLayers;
import iskallia.vault.gear.model.armor.layers.ClericArmorLayers;
import iskallia.vault.gear.model.armor.layers.CrayonArmorLayers;
import iskallia.vault.gear.model.armor.layers.CreateArmorLayers;
import iskallia.vault.gear.model.armor.layers.CrusaderArmorLayers;
import iskallia.vault.gear.model.armor.layers.DankArmorLayers;
import iskallia.vault.gear.model.armor.layers.DeerArmorLayers;
import iskallia.vault.gear.model.armor.layers.DevilArmorLayers;
import iskallia.vault.gear.model.armor.layers.DonkeyArmorLayers;
import iskallia.vault.gear.model.armor.layers.DruidArmorLayers;
import iskallia.vault.gear.model.armor.layers.FairyArmorLayers;
import iskallia.vault.gear.model.armor.layers.FlamingoArmorLayers;
import iskallia.vault.gear.model.armor.layers.FluxArmorLayers;
import iskallia.vault.gear.model.armor.layers.FurnaceArmorLayers;
import iskallia.vault.gear.model.armor.layers.GladiatorArmorLayers;
import iskallia.vault.gear.model.armor.layers.GrizzlyArmorLayers;
import iskallia.vault.gear.model.armor.layers.HellCowArmorLayers;
import iskallia.vault.gear.model.armor.layers.HellDuckArmorLayers;
import iskallia.vault.gear.model.armor.layers.HippopotamusArmorLayers;
import iskallia.vault.gear.model.armor.layers.IskallIbeArmorLayers;
import iskallia.vault.gear.model.armor.layers.JardoonCheeseArmorLayers;
import iskallia.vault.gear.model.armor.layers.JawboneArmorLayers;
import iskallia.vault.gear.model.armor.layers.KitsuneArmorLayers;
import iskallia.vault.gear.model.armor.layers.KnightArmorLayers;
import iskallia.vault.gear.model.armor.layers.LeprechaunArmorLayers;
import iskallia.vault.gear.model.armor.layers.LionguardArmorLayers;
import iskallia.vault.gear.model.armor.layers.MagmaticArmorLayers;
import iskallia.vault.gear.model.armor.layers.MailboxArmorLayers;
import iskallia.vault.gear.model.armor.layers.MekaArmorLayers;
import iskallia.vault.gear.model.armor.layers.MinotaurArmorLayers;
import iskallia.vault.gear.model.armor.layers.MonkArmorLayers;
import iskallia.vault.gear.model.armor.layers.OrcArmorLayers;
import iskallia.vault.gear.model.armor.layers.PaladinArmorLayers;
import iskallia.vault.gear.model.armor.layers.PirateArmorLayers;
import iskallia.vault.gear.model.armor.layers.PlatemailArmorLayers;
import iskallia.vault.gear.model.armor.layers.PowahArmorLayers;
import iskallia.vault.gear.model.armor.layers.ReinforcedPlatemailArmorLayers;
import iskallia.vault.gear.model.armor.layers.RhinoArmorLayers;
import iskallia.vault.gear.model.armor.layers.RoboticArmorLayers;
import iskallia.vault.gear.model.armor.layers.RogueArmorLayers;
import iskallia.vault.gear.model.armor.layers.RoyalArmorLayers;
import iskallia.vault.gear.model.armor.layers.RustyKnightArmorLayers;
import iskallia.vault.gear.model.armor.layers.RustyRaiderArmorLayers;
import iskallia.vault.gear.model.armor.layers.RustyScavengerArmorLayers;
import iskallia.vault.gear.model.armor.layers.SamuraiArmorLayers;
import iskallia.vault.gear.model.armor.layers.ScarecrowArmorLayers;
import iskallia.vault.gear.model.armor.layers.ShadowKingArmorLayers;
import iskallia.vault.gear.model.armor.layers.SkallibombaArmorLayers;
import iskallia.vault.gear.model.armor.layers.SoulEaterArmorLayers;
import iskallia.vault.gear.model.armor.layers.SpikyPlatemailArmorLayers;
import iskallia.vault.gear.model.armor.layers.SquireArmorLayers;
import iskallia.vault.gear.model.armor.layers.StressFlowerArmorLayers;
import iskallia.vault.gear.model.armor.layers.ThermalArmorLayers;
import iskallia.vault.gear.model.armor.layers.TorchArmorLayers;
import iskallia.vault.gear.model.armor.layers.TrashArmorLayers;
import iskallia.vault.gear.model.armor.layers.VillagerArmorLayers;
import iskallia.vault.gear.model.armor.layers.WarriorArmorLayers;
import iskallia.vault.gear.model.armor.layers.WitchArmorLayers;
import iskallia.vault.gear.model.armor.layers.WitherArmorLayers;
import iskallia.vault.gear.model.armor.layers.WizardArmorLayers;
import iskallia.vault.gear.model.armor.layers.XnetArmorLayers;
import iskallia.vault.gear.model.armor.layers.ZombieArmorLayers;
import iskallia.vault.gear.renderer.VaultArmorRenderProperties;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.AddLayers;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ModDynamicModels {
   public static final DynamicModelRegistries REGISTRIES = new DynamicModelRegistries();
   public static final ResourceLocation EMPTY_TEXTURE = VaultMod.id("item/empty");

   public static void initItemAssociations() {
      REGISTRIES.associate(ModItems.HELMET, ModDynamicModels.Armor.PIECE_REGISTRY.getPiecesOf(EquipmentSlot.HEAD))
         .associate(ModItems.CHESTPLATE, ModDynamicModels.Armor.PIECE_REGISTRY.getPiecesOf(EquipmentSlot.CHEST))
         .associate(ModItems.LEGGINGS, ModDynamicModels.Armor.PIECE_REGISTRY.getPiecesOf(EquipmentSlot.LEGS))
         .associate(ModItems.BOOTS, ModDynamicModels.Armor.PIECE_REGISTRY.getPiecesOf(EquipmentSlot.FEET))
         .associate(ModItems.SWORD, ModDynamicModels.Swords.REGISTRY)
         .associate(ModItems.AXE, ModDynamicModels.Axes.REGISTRY)
         .associate(ModItems.IDOL_TIMEKEEPER, ModDynamicModels.Idols.REGISTRY_WENDARR)
         .associate(ModItems.IDOL_MALEVOLENCE, ModDynamicModels.Idols.REGISTRY_IDONA)
         .associate(ModItems.IDOL_BENEVOLENT, ModDynamicModels.Idols.REGISTRY_VELARA)
         .associate(ModItems.IDOL_OMNISCIENT, ModDynamicModels.Idols.REGISTRY_TENOS)
         .associate(ModItems.SHIELD, ModDynamicModels.Shields.REGISTRY)
         .associate(ModItems.RELIC, ModDynamicModels.Relics.RELIC_REGISTRY)
         .associate(ModItems.RELIC_FRAGMENT, ModDynamicModels.Relics.FRAGMENT_REGISTRY)
         .associate(ModItems.MAGNET, ModDynamicModels.Magnets.REGISTRY_MAGNETS);
   }

   public static void initCauldronWashables() {
      CauldronInteraction.WATER.put(ModItems.MAGNET, CauldronInteraction.DYED_ITEM);
      CauldronInteraction.WATER.put(ModItems.HELMET, CauldronInteraction.DYED_ITEM);
      CauldronInteraction.WATER.put(ModItems.CHESTPLATE, CauldronInteraction.DYED_ITEM);
      CauldronInteraction.WATER.put(ModItems.LEGGINGS, CauldronInteraction.DYED_ITEM);
      CauldronInteraction.WATER.put(ModItems.BOOTS, CauldronInteraction.DYED_ITEM);
      CauldronInteraction.WATER.put(ModItems.SWORD, CauldronInteraction.DYED_ITEM);
      CauldronInteraction.WATER.put(ModItems.AXE, CauldronInteraction.DYED_ITEM);
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void registerLayerGeometries(RegisterLayerDefinitions event) {
      ModDynamicModels.Armor.PIECE_REGISTRY.forEach((modelName, piece) -> {
         EquipmentSlot equipmentSlot = piece.getEquipmentSlot();
         ModelLayerLocation layerLocation = piece.getLayerLocation();
         ArmorLayers modelLayers = piece.getLayers();
         event.registerLayerDefinition(layerLocation, modelLayers.getGeometrySupplier(equipmentSlot));
      });
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void bakeModelLayers(AddLayers event) {
      EntityModelSet entityModelSet = event.getEntityModels();
      ModDynamicModels.Armor.PIECE_REGISTRY.forEach((pieceId, piece) -> {
         ModelPart root = entityModelSet.bakeLayer(piece.getLayerLocation());
         EquipmentSlot equipmentSlot = piece.getEquipmentSlot();
         ArmorLayers model = piece.getLayers();
         ArmorLayers.BaseLayer layer = model.getLayerSupplier(equipmentSlot).supply(piece, root);
         VaultArmorRenderProperties.BAKED_LAYERS.put(pieceId, layer);
      });
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void stitchTextures(Pre event) {
      if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
         event.addSprite(EMPTY_TEXTURE);
         ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
         REGISTRIES.getUniqueRegistries().forEach(registry -> registry.forEach((modelId, dynamicModel) -> {
            ResourceLocation resourceLocation = new ResourceLocation(modelId.getNamespace(), "item/" + modelId.getPath());
            dynamicModel.resolveTextures(resourceManager, resourceLocation).values().forEach(event::addSprite);
         }));
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void bakeModels(ModelBakeEvent event) {
      Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
      ForgeModelBakery modelLoader = event.getModelLoader();
      ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
      REGISTRIES.getUniqueRegistries()
         .forEach(registry -> registry.forEach((modelId, dynamicModel) -> dynamicModel.getAssociatedModelLocations().forEach(modelLocation -> {
            BlockModel unbakedModel = (BlockModel)modelLoader.getModel(modelLocation);
            BakedModel bakedIcon;
            if (jsonModelExists(resourceManager, DynamicModel.prependToId("item/", modelId))) {
               bakedIcon = unbakedModel.bake(
                  modelLoader, unbakedModel, ForgeModelBakery.defaultTextureGetter(), SimpleModelState.IDENTITY, modelLocation, false
               );
               bakedIcon = new JsonFileBakedModel(bakedIcon);
            } else {
               bakedIcon = dynamicModel.bakeModel(modelLocation, modelLoader, unbakedModel);
            }

            ResourceLocation bakedId = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath());
            registry.bakeIcon(bakedId, bakedIcon);
         })));
      REGISTRIES.getUniqueItems().forEach(item -> {
         ResourceLocation itemId = item.getRegistryName();
         if (itemId == null) {
            throw new InternalError("Registry name does not exist for item -> " + item);
         } else {
            ModelResourceLocation key = new ModelResourceLocation(itemId, "inventory");
            BakedModel oldModel = modelRegistry.get(key);
            if (oldModel != null) {
               modelRegistry.put(key, new DynamicBakedModel(oldModel, modelLoader));
            }
         }
      });
   }

   public static boolean jsonModelExists(ResourceManager manager, ResourceLocation id) {
      ResourceLocation location = new ResourceLocation(id.getNamespace(), "models/" + id.getPath() + ".json");
      return manager.hasResource(location);
   }

   public static boolean textureExists(ResourceManager manager, ResourceLocation id) {
      ResourceLocation location = new ResourceLocation(id.getNamespace(), "textures/" + id.getPath() + ".png");
      return manager.hasResource(location);
   }

   public static boolean hasOverlayTexture(ResourceManager manager, ResourceLocation id) {
      return textureExists(manager, new ResourceLocation(id.getNamespace(), id.getPath() + "_overlay"));
   }

   public static class Armor {
      public static final ArmorPieceModelRegistry PIECE_REGISTRY = new ArmorPieceModelRegistry();
      public static final DynamicModelRegistry<ArmorModel> MODEL_REGISTRY = PIECE_REGISTRY.getArmorModels();
      public static final ArmorModel ANGEL = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/angel"), "Angel")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new AngelArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel BARD = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/bard"), "Bard")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new BardArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel BONE_DRAGON = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/bone_dragon"), "Bone Dragon")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new BoneDragonArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel CLERIC = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/cleric"), "Cleric")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ClericArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel CRUSADER = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/crusader"), "Crusader")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new CrusaderArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel DEER = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/deer"), "Deer")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new DeerArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel DEVIL = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/devil"), "Devil")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new DevilArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel DONKEY = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/donkey"), "Donkey")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new DonkeyArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel FURNACE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/furnace"), "Furnace")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new FurnaceArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel GLADIATOR = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/gladiator"), "Gladiator")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new GladiatorArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel GLADIATOR_DARK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/gladiator_dark"), "Dark Gladiator")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new GladiatorArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel GRIZZLY = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/grizzly"), "Grizzly")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new GrizzlyArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel HELL_DUCK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/hell_duck"), "Hell Duck")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new HellDuckArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel HIPPOPOTAMUS = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/hippopotamus"), "Hippopotamus")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new HippopotamusArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel JAWBONE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/jawbone"), "Jawbone")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new JawboneArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel KITSUNE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/kitsune"), "Kitsune")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new KitsuneArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel KNIGHT = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/knight"), "Knight")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new KnightArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel LEPRECHAUN = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/leprechaun"), "Leprechaun")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new LeprechaunArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel MAILBOX = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/mailbox"), "Mailbox")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new MailboxArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel MAILBOX_DARK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/mailbox_dark"), "Dark Mailbox")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new MailboxArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel MINOTAUR = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/minotaur"), "Minotaur")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new MinotaurArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel MINOTAUR_DARK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/minotaur_dark"), "Dark Minotaur")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new MinotaurArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel MONK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/monk"), "Monk")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new MonkArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel PALADIN = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/paladin"), "Paladin")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new PaladinArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel PLATEMAIL = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/platemail"), "Platemail")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new PlatemailArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel PLATEMAIL_DARK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/platemail_dark"), "Dark Platemail")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new PlatemailArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel REINFORCED_PLATEMAIL = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/reinforced_platemail"), "Reinforced Platemail")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ReinforcedPlatemailArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel REINFORCED_PLATEMAIL_DARK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/reinforced_platemail_dark"), "Dark Reinforced Platemail")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ReinforcedPlatemailArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel RHINO = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/rhino"), "Rhino")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new RhinoArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel RHINO_DARK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/rhino_dark"), "Dark Rhino")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new RhinoArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel ROGUE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/rogue"), "Rogue")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new RogueArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel ROYAL_GOLDEN = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/royal_golden"), "Golden Royal")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new RoyalArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel ROYAL_SILVER = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/royal_silver"), "Silver Royal")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new RoyalArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel LIONGUARD = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/lionguard"), "Lionguard")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new LionguardArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel RUSTY_KNIGHT = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/rusty_knight"), "Rusty Knight")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new RustyKnightArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel RUSTY_RAIDER = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/rusty_raider"), "Rusty Raider")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new RustyRaiderArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel RUSTY_SCAVENGER = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/rusty_scavenger"), "Rusty Scavenger")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new RustyScavengerArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel SAMURAI = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/samurai"), "Samurai")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new SamuraiArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel FLAMINGO = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/flamingo"), "Flamingo")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new FlamingoArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel SHADOW_KING = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/shadow_king"), "Shadow King")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ShadowKingArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel SPIKY_PLATEMAIL = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/spiky_platemail"), "Spiky Platemail")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new SpikyPlatemailArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel SPIKY_PLATEMAIL_DARK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/spiky_platemail_dark"), "Dark Spiky Platemail")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new SpikyPlatemailArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel SQUIRE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/squire"), "Squire")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new SquireArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel WARRIOR = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/warrior"), "Warrior")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new WarriorArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel WIZARD = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/wizard"), "Wizard")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new WizardArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel ATOMATIC = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/atomatic"), "Atomatic")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new AtomaticArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel BOTANIA = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/botania"), "Botania")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new BotaniaArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel BUILDER = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/builder"), "Builder")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new BuilderArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel CAKE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/cake"), "Cake")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new CakeArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel CREATE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/create"), "Create")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new CreateArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel DANK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/dank"), "Dank")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new DankArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel FAIRY = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/fairy"), "Fairy")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new FairyArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel FLUX = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/flux"), "Flux")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new FluxArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel HELL_COW = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/hell_cow"), "Hell Cow")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new HellCowArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel ROBOTIC = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/robotic"), "Robotic")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new RoboticArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel ARCADE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/arcade"), "Arcade")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ArcadeArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel JARDOON_CHEESE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/jardoon_cheese"), "Jardoon Cheeserson")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new JardoonCheeseArmorLayers())
            .addSlot(EquipmentSlot.HEAD, "Jardoon Cheeserson")
      );
      public static final ArmorModel MEKA_LIGHT = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/meka_light"), "Light Meka")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new MekaArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel MEKA_DARK = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/meka_dark"), "Dark Meka")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new MekaArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel POWAH = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/powah"), "Powah")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new PowahArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel SKALLIBOMBA = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/skallibomba"), "Skallibomba")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new SkallibombaArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel STRESS_FLOWER = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/stress_flower"), "Stress Flower")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new StressFlowerArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
      );
      public static final ArmorModel SCARECROW = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/scarecrow"), "Scarecrow")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ScarecrowArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel THERMAL = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/thermal"), "Thermal")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ThermalArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel TRASH = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/trash"), "Trash")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new TrashArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel VILLAGER = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/villager"), "Villager")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new VillagerArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel XNET = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/xnet"), "Xnet")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new XnetArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel ZOMBIE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/zombie"), "Zombie")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ZombieArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel ISKALL_IBE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/iskall_ibe"), "IBE")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new IskallIbeArmorLayers())
            .addSlot(EquipmentSlot.HEAD, "Iskall Bionic Eye")
      );
      public static final ArmorModel DRUID = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/druid"), "Druid")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new DruidArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel ORC = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/orc"), "Orc")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new OrcArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel WITCH = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/witch"), "Witch")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new WitchArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel MAGMATIC = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/magmatic"), "Magmatic")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new MagmaticArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel BUMBO = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/bumbo"), "Bumbo Cactoni")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new BumboCactoniArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel WITHER = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/wither"), "Wither")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new WitherArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel SOUL_EATER = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/soul_eater"), "Soul Eater")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new SoulEaterArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel PIRATE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/pirate"), "Pirate")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new PirateArmorLayers())
            .addSlot(EquipmentSlot.HEAD, "Pirate Tricorne")
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel CRAYON_BLUE = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/crayon_blue"), "Blue Crayon")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new CrayonArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
      );
      public static final ArmorModel CRAYON_GREEN = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/crayon_green"), "Green Crayon")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new CrayonArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
      );
      public static final ArmorModel CRAYON_RED = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/crayon_red"), "Red Crayon")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new CrayonArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
      );
      public static final ArmorModel CRAYON_YELLOW = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/crayon_yellow"), "Yellow Crayon")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new CrayonArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
      );
      public static final ArmorModel TORCH = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/torch"), "Torch")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new TorchArmorLayers())
            .addSlot(EquipmentSlot.HEAD)
            .addSlot(EquipmentSlot.CHEST)
            .addSlot(EquipmentSlot.LEGS)
            .addSlot(EquipmentSlot.FEET)
      );
      public static final ArmorModel IDONAS_ARMOUR = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/armour_idona"), "Idona's Armour")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ArmourIdonaArmorLayers())
            .addSlot(EquipmentSlot.HEAD, "Idona's Helmet")
            .addSlot(EquipmentSlot.CHEST, "Idona's Chestplate")
            .addSlot(EquipmentSlot.LEGS, "Idona's Leggings")
            .addSlot(EquipmentSlot.FEET, "Idona's Boots")
      );
      public static final ArmorModel TENOS_ARMOUR = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/armour_tenos"), "Tenos' Armour")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ArmourTenosArmorLayers())
            .addSlot(EquipmentSlot.HEAD, "Tenos' Helmet")
            .addSlot(EquipmentSlot.CHEST, "Tenos' Chestplate")
            .addSlot(EquipmentSlot.LEGS, "Tenos' Leggings")
            .addSlot(EquipmentSlot.FEET, "Tenos' Boots")
      );
      public static final ArmorModel VELARAS_ARMOUR = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/armour_velara"), "Velara's Armour")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ArmourVelaraArmorLayers())
            .addSlot(EquipmentSlot.HEAD, "Velara's Helmet")
            .addSlot(EquipmentSlot.CHEST, "Velara's Chestplate")
            .addSlot(EquipmentSlot.LEGS, "Velara's Leggings")
            .addSlot(EquipmentSlot.FEET, "Velara's Boots")
      );
      public static final ArmorModel WENDARRS_ARMOUR = PIECE_REGISTRY.registerAll(
         new ArmorModel(VaultMod.id("gear/armor/armour_wendarr"), "Wendarr's Armour")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
            .usingLayers(new ArmourWendarrArmorLayers())
            .addSlot(EquipmentSlot.HEAD, "Wendarr's Helmet")
            .addSlot(EquipmentSlot.CHEST, "Wendarr's Chestplate")
            .addSlot(EquipmentSlot.LEGS, "Wendarr's Leggings")
            .addSlot(EquipmentSlot.FEET, "Wendarr's Boots")
      );
   }

   public static class Axes {
      public static final DynamicModelRegistry<HandHeldModel> REGISTRY = new DynamicModelRegistry<>();
      public static final HandHeldModel AXE_0 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_0"), "Axe_0").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_1 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_1"), "Axe_1").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_2 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_2"), "Axe_2").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_3 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_3"), "Axe_3").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_4 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_4"), "Axe_4").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_5 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_5"), "Axe_5").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_6 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_6"), "Axe_6").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_7 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_7"), "Axe_7").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_8 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_8"), "Axe_8").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_9 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_9"), "Axe_9").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_10 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_10"), "Axe_10").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_11 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_11"), "Axe_11").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_12 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_12"), "Axe_12").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel AXE_13 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/axe_13"), "Axe_13").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel JANITORS_BROOM = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/janitors_broom"), "Janitor's Broomstick")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel BIG_CHOPPA = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/big_choppa"), "Big Choppa")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel EVIL_MACE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/evil_mace"), "Evil Mace").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel GREATHAMMER = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/greathammer"), "Greathammer")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel TINY_HAMMER = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/tiny_hammer"), "Tiny Hammer")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel BLOOD_CHOPPER = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/blood_chopper"), "Blood Chopper")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel BLOOD_CLEAVER = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/blood_cleaver"), "Blood Cleaver")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel LAST_SIGHT = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/last_sight"), "Last Sight")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel IDONAS_SCYTHE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/idonas_scythe"), "Idona's Scythe")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel TENOS_STAFF = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/tenos_staff"), "Tenos' Staff")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel VELARAS_HAMMER = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/velaras_hammer"), "Velara's Hammer")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel WENDARRS_CLOCKAXE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/axe/wendarrs_clockaxe"), "Wendarr's Clockaxe")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
   }

   public static class Idols {
      public static final DynamicModelRegistry<PlainItemModel> REGISTRY_WENDARR = new DynamicModelRegistry<>();
      public static final DynamicModelRegistry<PlainItemModel> REGISTRY_IDONA = new DynamicModelRegistry<>();
      public static final DynamicModelRegistry<PlainItemModel> REGISTRY_VELARA = new DynamicModelRegistry<>();
      public static final DynamicModelRegistry<PlainItemModel> REGISTRY_TENOS = new DynamicModelRegistry<>();
      public static final PlainItemModel WENDARR = REGISTRY_WENDARR.register(
         new PlainItemModel(VaultMod.id("gear/idol/wendarr"), "Wendarr's Idol")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final PlainItemModel IDONA = REGISTRY_IDONA.register(
         new PlainItemModel(VaultMod.id("gear/idol/idona"), "Idona's Idol").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final PlainItemModel VELARA = REGISTRY_VELARA.register(
         new PlainItemModel(VaultMod.id("gear/idol/velara"), "Velara's Idol")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final PlainItemModel TENOS = REGISTRY_TENOS.register(
         new PlainItemModel(VaultMod.id("gear/idol/tenos"), "Tenos' Idol").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
   }

   public static class Magnets {
      public static final DynamicModelRegistry<PlainItemModel> REGISTRY_MAGNETS = new DynamicModelRegistry<>();
      public static final PlainItemModel DEFAULT = REGISTRY_MAGNETS.register(new PlainItemModel(VaultMod.id("magnets/magnet_1"), "Magnet"));
   }

   public static class Relics {
      public static final DynamicModelRegistry<PlainItemModel> RELIC_REGISTRY = new DynamicModelRegistry<>();
      public static final DynamicModelRegistry<PlainItemModel> FRAGMENT_REGISTRY = new DynamicModelRegistry<>();
      public static final PlainItemModel DRAGON_BREATH = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/dragon/frag1"), "Dragon Breath"));
      public static final PlainItemModel DRAGON_CHEST = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/dragon/frag2"), "Dragon Chest"));
      public static final PlainItemModel DRAGON_FOOT = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/dragon/frag3"), "Dragon Foot"));
      public static final PlainItemModel DRAGON_HEAD = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/dragon/frag4"), "Dragon Head"));
      public static final PlainItemModel DRAGON_TAIL = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/dragon/frag5"), "Dragon Tail"));
      public static final PlainItemModel DRAGON_RELIC = RELIC_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/dragon/statue"), "Dragon Relic"));
      public static final PlainItemModel MINERS_LIGHT = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/miner/frag1"), "Miner's Light"));
      public static final PlainItemModel MINERS_DELIGHT = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/miner/frag2"), "Miner's Delight"));
      public static final PlainItemModel PICKAXE_HANDLE = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/miner/frag3"), "Pickaxe Handle"));
      public static final PlainItemModel PICKAXE_HEAD = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/miner/frag4"), "Pickaxe Head"));
      public static final PlainItemModel PICKAXE_TOOL = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/miner/frag5"), "Pickaxe Tool"));
      public static final PlainItemModel MINER_RELIC = RELIC_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/miner/statue"), "Miner Relic"));
      public static final PlainItemModel WARRIORS_ARMOUR = FRAGMENT_REGISTRY.register(
         new PlainItemModel(VaultMod.id("relic/warrior/frag1"), "Warrior's Armour")
      );
      public static final PlainItemModel WARRIORS_CHARM = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/warrior/frag2"), "Warrior's Charm"));
      public static final PlainItemModel SWORD_BLADE = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/warrior/frag3"), "Sword Blade"));
      public static final PlainItemModel SWORD_HANDLE = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/warrior/frag4"), "Sword Handle"));
      public static final PlainItemModel SWORD_STICK = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/warrior/frag5"), "Sword Stick"));
      public static final PlainItemModel WARRIOR_RELIC = RELIC_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/warrior/statue"), "Warrior Relic"));
      public static final PlainItemModel DIAMOND_ESSENCE = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/richity/frag1"), "Diamond Essence"));
      public static final PlainItemModel GOLD_ESSENCE = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/richity/frag2"), "Gold Essence"));
      public static final PlainItemModel MYSTIC_GEM_ESSENCE = FRAGMENT_REGISTRY.register(
         new PlainItemModel(VaultMod.id("relic/richity/frag3"), "Mystic Gem Essence")
      );
      public static final PlainItemModel NETHERITE_ESSENCE = FRAGMENT_REGISTRY.register(
         new PlainItemModel(VaultMod.id("relic/richity/frag4"), "Netherite Essence")
      );
      public static final PlainItemModel PLATINUM_ESSENCE = FRAGMENT_REGISTRY.register(
         new PlainItemModel(VaultMod.id("relic/richity/frag5"), "Platinum Essence")
      );
      public static final PlainItemModel RICHITY_RELIC = RELIC_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/richity/statue"), "Richity Relic"));
      public static final PlainItemModel TWITCH_EMOTE_1 = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/twitch/frag1"), "Twitch Emote #1"));
      public static final PlainItemModel TWITCH_EMOTE_2 = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/twitch/frag2"), "Twitch Emote #2"));
      public static final PlainItemModel TWITCH_EMOTE_3 = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/twitch/frag3"), "Twitch Emote #3"));
      public static final PlainItemModel TWITCH_EMOTE_4 = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/twitch/frag4"), "Twitch Emote #4"));
      public static final PlainItemModel TWITCH_EMOTE_5 = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/twitch/frag5"), "Twitch Emote #5"));
      public static final PlainItemModel TWITCH_RELIC = RELIC_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/twitch/statue"), "Twitch Relic"));
      public static final PlainItemModel CUPCAKE_BLUE = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/cupcake/frag1"), "Blue Cupcake"));
      public static final PlainItemModel CUPCAKE_LIME = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/cupcake/frag2"), "Lime Cupcake"));
      public static final PlainItemModel CUPCAKE_PINK = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/cupcake/frag3"), "Pink Cupcake"));
      public static final PlainItemModel CUPCAKE_PURPLE = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/cupcake/frag4"), "Purple Cupcake"));
      public static final PlainItemModel CUPCAKE_RED = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/cupcake/frag5"), "Red Cupcake"));
      public static final PlainItemModel CUPCAKE_RELIC = RELIC_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/cupcake/statue"), "Cupcake Relic"));
      public static final PlainItemModel ELEMENT_AIR = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/elemental/frag1"), "Air Element"));
      public static final PlainItemModel ELEMENT_EARTH = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/elemental/frag2"), "Earth Element"));
      public static final PlainItemModel ELEMENT_FIRE = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/elemental/frag3"), "Fire Element"));
      public static final PlainItemModel ELEMENT_WATER = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/elemental/frag4"), "Water Element"));
      public static final PlainItemModel ELEMENT_SPIRIT = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/elemental/frag5"), "Spirit Element"));
      public static final PlainItemModel ELEMENTAL_RELIC = RELIC_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/elemental/statue"), "Elemental Relic"));
      public static final PlainItemModel SER = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/nazar/frag1"), "Şer"));
      public static final PlainItemModel KEM_GOZ = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/nazar/frag2"), "Kem Göz"));
      public static final PlainItemModel KADER = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/nazar/frag3"), "Kader"));
      public static final PlainItemModel KISMET = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/nazar/frag4"), "Kısmet"));
      public static final PlainItemModel NAZARLIK = FRAGMENT_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/nazar/frag5"), "Nazarlık"));
      public static final PlainItemModel NAZAR_BONCUGU = RELIC_REGISTRY.register(new PlainItemModel(VaultMod.id("relic/nazar/statue"), "Nazar Boncuğu"));
   }

   public static class Shields {
      public static final DynamicModelRegistry<ShieldModel> REGISTRY = new DynamicModelRegistry<>();
      public static final ShieldModel VANILLA = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/vanilla"), "Good ol' Vanilla Shield")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel WOODEN = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/wooden"), "Wooden Shield")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel ENDER = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/ender"), "Ender Shield").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel INFERNO = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/inferno"), "Inferno Shield")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel COCONUT = REGISTRY.register(
         new CoconutShieldModel(VaultMod.id("gear/shield/coconut"), VaultMod.id("gear/shield/coconut_orange"), "Coconut Shield")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel BELL = REGISTRY.register(
         new BellShieldModel(VaultMod.id("gear/shield/bell"), "Bell Shield")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel TURTLE = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/turtle"), "Turtle Shell").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel PRESENT = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/present"), "Present Box Lid")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel SCULK = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/sculk"), "Sculk Shield").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel FLOWAH = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/flowah"), "Chamomile").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel EMBERWING = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/emberwing"), "Emberwing Dragonshield")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel SCRAP = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/scrap"), "Scrap Shield").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel DRUID = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/druid"), "Druid Shield").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel GOLD_PLATED = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/gold_plated"), "Gold Plated Shield")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel PEPPERMINT = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/peppermint"), "Peppermint Shield")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel NOU = REGISTRY.register(
         new ShieldModel(VaultMod.id("gear/shield/nou"), "NOU Shield").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final ShieldModel ABSOLUTE_BINNER = REGISTRY.register(
         new AbsoluteBinnerShieldModel(VaultMod.id("gear/shield/absolutebinner"), "Absolute Binner")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
   }

   public static class Swords {
      public static final DynamicModelRegistry<HandHeldModel> REGISTRY = new DynamicModelRegistry<>();
      public static final HandHeldModel SWORD_0 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/sword_0"), "Sword_0").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SWORD_1 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/sword_1"), "Sword_1").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SWORD_2 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/sword_2"), "Sword_2").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SWORD_3 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/sword_3"), "Sword_3").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SWORD_4 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/sword_4"), "Sword_4").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SWORD_5 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/sword_5"), "Sword_5").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SWORD_6 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/sword_6"), "Sword_6").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SWORD_7 = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/sword_7"), "Sword_7").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel ALLIUMBLADE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/alliumblade"), "Alliumblade")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel BASEBALL_BAT = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/baseball_bat"), "Baseball Bat")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel CHAINSWORD = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/chainsword"), "Chainsword")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel DARK_BLADE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/dark_blade"), "Dark Blade")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SOUL_SWORD = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/soul_sword"), "Soul Sword")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SOUL_SWORD_GREEN = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/soul_sword_green"), "Soul Sword (Green)")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SOUL_SWORD_BLUE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/soul_sword_blue"), "Soul Sword (Blue)")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel RED_KATANA = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/red_katana"), "Red Katana")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel NIGHTFALL = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/nightfall"), "Nightfall")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel DEATHS_DOOR = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/deaths_door"), "Death's Door")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel GLADIUS = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/gladius"), "Gladius Sword")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel VELARAS_GREATSWORD = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/velaras_greatsword"), "Velara's Greatsword")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel WENDARRS_GREATSWORD = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/wendarrs_greatsword"), "Wendarr's Greatsword")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel IDONAS_SWORD = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/idonas_sword"), "Idona's Sword")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel CRYSTALLISED_BLADE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/crystallised_blade"), "Crystallised Blade")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel DOUBLE_BLADE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/double_blade"), "Double Blade")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel DOUWSWORDS_SWOUSKY = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/douwswords_swousky"), "Douwsword's Swousky")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel KINDLED_BLADE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/kindled_blade"), "Kindled Blade")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel MOONSHINE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/moonshine"), "Moonshine")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel PLATE_PIERCER = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/plate_piercer"), "Plate Piercer")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel REFRACTED_BLADE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/refracted_blade"), "Refracted Blade")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel RING_BLADE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/ring_blade"), "Ring Blade")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SKALLIFIED_SWORD = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/skallified_sword"), "Skallified Sword")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel SWAXE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/swaxe"), "Swaxe").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel TARNISHED_BLADE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/tarnished_blade"), "Tarnished Blade")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel TRIBAL_BLADE = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/tribal_blade"), "Tribal Blade")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel GLOREM_GLIPSUM = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/glorem_glipsum"), "Glorem Glipsum")
            .properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
      public static final HandHeldModel BAMBOO = REGISTRY.register(
         new HandHeldModel(VaultMod.id("gear/sword/bamboo"), "Bamboo").properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll())
      );
   }
}
