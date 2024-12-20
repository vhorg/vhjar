package iskallia.vault.init;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import iskallia.vault.VaultMod;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.DungeonDoorBlock;
import iskallia.vault.block.EasterEggBlock;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.TreasureDoorBlock;
import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.block.model.BloodOrbModel;
import iskallia.vault.block.model.BountyBlockExclamationModel;
import iskallia.vault.block.model.BountyBlockQuestionModel;
import iskallia.vault.block.model.HeraldControllerModel;
import iskallia.vault.block.model.PylonCrystalModel;
import iskallia.vault.block.model.WendarrChainModel;
import iskallia.vault.block.model.WendarrSparkSourceModel;
import iskallia.vault.block.render.AncientCopperConduitRenderer;
import iskallia.vault.block.render.AngelBlockRenderer;
import iskallia.vault.block.render.ArtifactProjectorRenderer;
import iskallia.vault.block.render.IdentificationStandRenderer;
import iskallia.vault.block.render.PotionModifierDiscoveryRenderer;
import iskallia.vault.block.render.SparkRenderer;
import iskallia.vault.block.render.VaultEnchanterRenderer;
import iskallia.vault.block.render.VelvetBedRenderer;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.client.util.color.ColorUtil;
import iskallia.vault.config.CharmConfig;
import iskallia.vault.config.gear.VaultGearTypeConfig;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.entity.model.BossProtectionCatalystModel;
import iskallia.vault.entity.model.FireballModel;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.PiercingJavelinModel;
import iskallia.vault.entity.model.ScatterJavelinModel;
import iskallia.vault.entity.model.ScrappyJavelinModel;
import iskallia.vault.entity.model.SightJavelinModel;
import iskallia.vault.etching.EtchingRegistry;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.charm.AttributeCharm;
import iskallia.vault.gear.charm.CharmEffect;
import iskallia.vault.gear.charm.CharmEffectRegistry;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
import iskallia.vault.item.AugmentItem;
import iskallia.vault.item.ItemDrillArrow;
import iskallia.vault.item.JewelPouchItem;
import iskallia.vault.item.LegacyMagnetItem;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.research.StageManager;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.calc.BlockChanceHelper;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties.CompassWobble;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ModModels {
   public static void setupRenderLayers() {
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.VAULT_PORTAL, RenderType.translucent());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.TREASURE_DOOR, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.DUNGEON_DOOR, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.VAULT_ALTAR, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.VAULT_ARTIFACT, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.MVP_CROWN, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.GOD_ALTAR, RenderType.translucent());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.VAULT_GLASS, RenderType.translucent());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.SKILL_ALTAR, RenderType.translucent());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.MOB_BARRIER, RenderType.translucent());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.FINAL_VAULT_FRAME, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.ELITE_SPAWNER, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.SUGAR_PLUM_FAIRY_FLOWER, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_CLUSTER, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.LARGE_CRYSTAL_BUD, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.MEDIUM_CRYSTAL_BUD, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.SMALL_CRYSTAL_BUD, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.MONOLITH, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLACK_MARKET, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANIMAL_JAR, RenderType.translucent());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.ETERNAL_PEDESTAL, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.MODIFIER_DISCOVERY, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.TOTEM_PLAYER_HEALTH, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.TOTEM_MOB_DAMAGE, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.TOTEM_PLAYER_DAMAGE, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.VAULT_JEWEL_APPLICATION_STATION, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.GILDED_SCONCE, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.GILDED_SCONCE_WALL, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.SPARK, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.CONVERTED_SPARK, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.GILDED_CANDELABRA, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.ARTIFACT_PROJECTOR_BLOCK, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANCIENT_COPPER_TRAPDOOR, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANCIENT_COPPER_TRAPDOOR_EXPOSED, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANCIENT_COPPER_TRAPDOOR_WEATHERED, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANCIENT_COPPER_TRAPDOOR_OXIDIZED, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.GRID_GATEWAY, RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.TASK_PILLAR, RenderType.cutout());
      setRenderLayers(ModBlocks.VAULT_SWEETS, RenderType.solid(), RenderType.translucent());
      setRenderLayers(ModBlocks.DIVINE_ALTAR, RenderType.cutout(), RenderType.translucent());
      setRenderLayers(ModBlocks.VAULT_JEWEL_CUTTING_STATION, RenderType.cutout(), RenderType.translucent());
      setRenderLayers(ModBlocks.JEWEL_CRAFTING_TABLE, RenderType.cutout(), RenderType.translucent());
      setRenderLayers(ModBlocks.ALCHEMY_ARCHIVE, RenderType.solid(), RenderType.translucent());
      setRenderLayers(ModBlocks.ALCHEMY_TABLE, RenderType.cutout(), RenderType.translucent());
      setRenderLayers(ModBlocks.CRYO_CHAMBER, RenderType.solid(), RenderType.translucent());
      setRenderLayers(ModBlocks.HOURGLASS, RenderType.solid(), RenderType.translucent());
      setRenderLayers(ModBlocks.STABILIZER, RenderType.solid(), RenderType.translucent());
      setRenderLayers(ModBlocks.RAID_CONTROLLER, RenderType.translucent());
      setRenderLayers(ModBlocks.X_MARK_CONTROLLER, RenderType.translucent());
      setRenderLayers(ModBlocks.ELITE_CONTROLLER, RenderType.translucent());
      setRenderLayers(ModBlocks.ELITE_CONTROLLER_PROXY, RenderType.translucent());
      setRenderLayers(ModBlocks.VAULT_CHARM_CONTROLLER_BLOCK, RenderType.solid(), RenderType.translucent());
      setRenderLayers(ModBlocks.CRAKE_PEDESTAL, RenderType.translucent());
      setRenderLayers(ModBlocks.WENDARR_JEWEL_GLASS, RenderType.translucent());
      setRenderLayers(ModBlocks.WENDARR_JEWEL_GLASS_PANE, RenderType.translucent());
      setRenderLayers(ModBlocks.VELARA_VINE, RenderType.cutout());
      setRenderLayers(ModBlocks.VAULT_BARS, RenderType.cutout());
      setRenderLayers(ModBlocks.CARD_ESSENCE_EXTRACTOR, RenderType.solid(), RenderType.translucent());
   }

   private static void setRenderLayers(Block block, RenderType... renderTypes) {
      ItemBlockRenderTypes.setRenderLayer(block, Predicates.in(Arrays.asList(renderTypes)));
   }

   public static void registerItemColors(ItemColors colors) {
      colors.register(
         (stack, tintLayer) -> {
            if (tintLayer > 0) {
               GearDataCache clientCache = GearDataCache.of(stack);
               if (clientCache.getState() == VaultGearState.UNIDENTIFIED) {
                  VaultGearTypeConfig.RollType type = Optional.ofNullable(clientCache.getGearRollType())
                     .flatMap(ModConfigs.VAULT_GEAR_TYPE_CONFIG::getRollPool)
                     .orElse(null);
                  if (type != null) {
                     return ColorUtil.blendColors(type.getColor(), -1, 0.8F);
                  }
               }

               return -1;
            } else {
               return VaultGearHelper.getGearColor(stack);
            }
         },
         new ItemLike[]{
            ModItems.HELMET,
            ModItems.CHESTPLATE,
            ModItems.LEGGINGS,
            ModItems.BOOTS,
            ModItems.AXE,
            ModItems.SWORD,
            ModItems.SHIELD,
            ModItems.IDOL_BENEVOLENT,
            ModItems.IDOL_OMNISCIENT,
            ModItems.IDOL_TIMEKEEPER,
            ModItems.IDOL_MALEVOLENCE,
            ModItems.MAGNET,
            ModItems.WAND,
            ModItems.FOCUS
         }
      );
      colors.register((stack, tintIndex) -> tintIndex == 0 ? JewelItem.getColor(stack) : -1, new ItemLike[]{ModItems.JEWEL});
      colors.register((stack, tintIndex) -> tintIndex == 1 ? AugmentItem.getColor(stack) : -1, new ItemLike[]{ModItems.AUGMENT});
      colors.register((stack, tintIndex) -> {
         if (tintIndex == 1) {
            return BottleItem.getColor(stack);
         } else {
            return tintIndex == 2 ? BottleItem.getEffectColor(stack) : -1;
         }
      }, new ItemLike[]{ModItems.BOTTLE});
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void registerLayerDefinitions(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(PylonCrystalModel.MODEL_LOCATION, PylonCrystalModel::createBodyLayer);
      event.registerLayerDefinition(ScrappyJavelinModel.MODEL_LOCATION, ScrappyJavelinModel::createBodyLayer);
      event.registerLayerDefinition(SightJavelinModel.MODEL_LOCATION, SightJavelinModel::createBodyLayer);
      event.registerLayerDefinition(ScatterJavelinModel.MODEL_LOCATION, ScatterJavelinModel::createBodyLayer);
      event.registerLayerDefinition(PiercingJavelinModel.MODEL_LOCATION, PiercingJavelinModel::createBodyLayer);
      event.registerLayerDefinition(FireballModel.MODEL_LOCATION, FireballModel::createBodyLayer);
      event.registerLayerDefinition(BossProtectionCatalystModel.MODEL_LOCATION, BossProtectionCatalystModel::createBodyLayer);
      event.registerLayerDefinition(HeraldControllerModel.MODEL_LOCATION, HeraldControllerModel::createBodyLayer);
      event.registerLayerDefinition(BloodOrbModel.LAYER_LOCATION, BloodOrbModel::createBodyLayer);
      event.registerLayerDefinition(ModModelLayers.ANGEL_BLOCK_EYE, AngelBlockRenderer::createEyeLayer);
      event.registerLayerDefinition(ModModelLayers.ANGEL_BLOCK_WIND, AngelBlockRenderer::createWindLayer);
      event.registerLayerDefinition(ModModelLayers.ANGEL_BLOCK_CAGE, AngelBlockRenderer::createCageLayer);
      event.registerLayerDefinition(ModModelLayers.ANCIENT_COPPER_CONDUIT_BLOCK_EYE, AncientCopperConduitRenderer::createEyeLayer);
      event.registerLayerDefinition(ModModelLayers.ANCIENT_COPPER_CONDUIT_BLOCK_WIND, AncientCopperConduitRenderer::createWindLayer);
      event.registerLayerDefinition(ModModelLayers.ANCIENT_COPPER_CONDUIT_BLOCK_CAGE, AncientCopperConduitRenderer::createCageLayer);
      event.registerLayerDefinition(BountyBlockExclamationModel.LAYER_LOCATION, BountyBlockExclamationModel::createBodyLayer);
      event.registerLayerDefinition(BountyBlockQuestionModel.LAYER_LOCATION, BountyBlockQuestionModel::createBodyLayer);
      event.registerLayerDefinition(VelvetBedRenderer.HEAD_LAYER_LOCATION, VelvetBedRenderer::createHeadLayer);
      event.registerLayerDefinition(VelvetBedRenderer.FOOT_LAYER_LOCATION, VelvetBedRenderer::createFootLayer);
      event.registerLayerDefinition(WendarrSparkSourceModel.LAYER_LOCATION, WendarrSparkSourceModel::createBodyLayer);
      event.registerLayerDefinition(WendarrChainModel.LAYER_LOCATION, WendarrChainModel::createBodyLayer);
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void stitchTextures(Pre event) {
      if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
         event.addSprite(PylonCrystalModel.TEXTURE_LOCATION);
         event.addSprite(HeraldControllerModel.TEXTURE_LOCATION);
         event.addSprite(HeraldControllerModel.FILLED_TEXTURE_LOCATION);
         event.addSprite(VaultEnchanterRenderer.BOOK_TEXTURE);
         event.addSprite(ArtifactProjectorRenderer.BOOK_TEXTURE);
         event.addSprite(IdentificationStandRenderer.BOOK_TEXTURE);
         event.addSprite(PotionModifierDiscoveryRenderer.BOOK_TEXTURE.texture());
         event.addSprite(AngelBlockRenderer.ANGEL_OUTTER.texture());
         event.addSprite(AngelBlockRenderer.ANGEL_OUTTER2.texture());
         event.addSprite(AngelBlockRenderer.ANGEL_CENTER.texture());
         event.addSprite(AngelBlockRenderer.ANGEL_WIND.texture());
         event.addSprite(AngelBlockRenderer.ANGEL_WIND_VERTICAL.texture());
         event.addSprite(AncientCopperConduitRenderer.CONDUIT_OUTTER.texture());
         event.addSprite(AncientCopperConduitRenderer.CONDUIT_OUTTER2.texture());
         event.addSprite(AncientCopperConduitRenderer.CONDUIT_CENTER.texture());
         event.addSprite(AncientCopperConduitRenderer.CONDUIT_WIND.texture());
         event.addSprite(AncientCopperConduitRenderer.CONDUIT_WIND_VERTICAL.texture());
         event.addSprite(BountyBlockExclamationModel.TEXTURE_LOCATION);
         event.addSprite(BountyBlockQuestionModel.TEXTURE_LOCATION);
         event.addSprite(VelvetBedRenderer.TEXTURE_LOCATION);
         event.addSprite(SparkRenderer.TEXTURE_LOCATION);
         event.addSprite(WendarrSparkSourceModel.TEXTURE_LOCATION);
         event.addSprite(WendarrChainModel.TEXTURE_LOCATION);
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void stitchParticleTextures(Pre event) {
      if (event.getAtlas().location().equals(TextureAtlas.LOCATION_PARTICLES)) {
         CharmConfig.LOC.forEach(event::addSprite);
      }
   }

   public static class ItemProperty {
      public static ItemPropertyFunction ETCHING = (stack, world, entity, seed) -> {
         AttributeGearData data = AttributeGearData.read(stack);
         EtchingSet<?> etching = data.getFirstValue(ModGearAttributes.ETCHING).orElse(null);
         return etching == null ? -1.0F : EtchingRegistry.getOrderedEntries().indexOf(etching);
      };
      public static ItemPropertyFunction TRINKET = (stack, world, entity, seed) -> {
         AttributeGearData data = AttributeGearData.read(stack);
         TrinketEffect<?> trinket = data.getFirstValue(ModGearAttributes.TRINKET_EFFECT).orElse(null);
         return trinket == null ? -1.0F : TrinketEffectRegistry.getOrderedEntries().indexOf(trinket);
      };
      public static ItemPropertyFunction CHARM = (stack, world, entity, seed) -> {
         AttributeGearData data = AttributeGearData.read(stack);
         CharmEffect<?> charm = data.getFirstValue(ModGearAttributes.CHARM_EFFECT).orElse(null);
         return charm instanceof AttributeCharm<?> attributeCharm ? CharmEffectRegistry.getOrderedEntries().indexOf(charm) : -1.0F;
      };
      public static ItemPropertyFunction PLACEHOLDER_TYPE = (stack, world, entity, seed) -> {
         CompoundTag nbt = stack.getTag();
         if (nbt == null) {
            return -1.0F;
         } else {
            PlaceholderBlock.Type type = PlaceholderBlock.Type.fromString(nbt.getString("type"));
            return type == null ? -1.0F : type.ordinal();
         }
      };
      public static ItemPropertyFunction VAULT_ORE_TYPE = (stack, world, entity, seed) -> {
         CompoundTag nbt = stack.getTag();
         if (nbt == null) {
            return -1.0F;
         } else {
            VaultOreBlock.Type type = VaultOreBlock.Type.fromString(nbt.getString("type"));
            return type == null ? -1.0F : type.ordinal();
         }
      };
      public static ItemPropertyFunction EASTER_EGG_TYPE = (stack, world, entity, seed) -> {
         CompoundTag nbt = stack.getTag();
         if (nbt == null) {
            return -1.0F;
         } else {
            EasterEggBlock.Color color = EasterEggBlock.Color.fromString(nbt.getString("color"));
            return color == null ? -1.0F : color.ordinal();
         }
      };
      public static ItemPropertyFunction TREASURE_DOOR_TYPE = (stack, world, entity, seed) -> {
         CompoundTag nbt = stack.getTag();
         if (nbt == null) {
            return -1.0F;
         } else {
            TreasureDoorBlock.Type type = TreasureDoorBlock.Type.fromString(nbt.getString("type"));
            return type == null ? -1.0F : type.ordinal();
         }
      };
      public static ItemPropertyFunction DUNGEON_DOOR_TYPE = (stack, world, entity, seed) -> {
         CompoundTag nbt = stack.getTag();
         if (nbt == null) {
            return -1.0F;
         } else {
            DungeonDoorBlock.Type type = DungeonDoorBlock.Type.fromString(nbt.getString("type"));
            return type == null ? -1.0F : type.ordinal();
         }
      };
      public static ItemPropertyFunction GOD_ITEM_TYPE = (stack, world, entity, seed) -> {
         CompoundTag nbt = stack.getTag();
         VaultGod type;
         return nbt != null && (type = VaultGod.fromName(nbt.getString("type"))) != null
            ? type.ordinal()
            : (float)((ClientScheduler.INSTANCE.getTick() >> 4) % VaultGod.values().length);
      };
      public static ItemPropertyFunction GOD_ALTAR_TYPE = (stack, world, entity, seed) -> {
         CompoundTag nbt = stack.getTag();
         if (nbt == null) {
            return -1.0F;
         } else {
            VaultGod god = VaultGod.fromName(nbt.getString("god"));
            return god == null ? -1.0F : god.ordinal();
         }
      };

      public static void register() {
         registerItemProperty(ModItems.ETCHING, "etching", ETCHING);
         registerItemProperty(ModItems.TRINKET, "trinket", TRINKET);
         registerItemProperty(ModItems.SMALL_CHARM, "charm", CHARM);
         registerItemProperty(ModItems.LARGE_CHARM, "charm", CHARM);
         registerItemProperty(ModItems.GRAND_CHARM, "charm", CHARM);
         registerItemProperty(ModItems.MAJESTIC_CHARM, "charm", CHARM);
         registerItemProperty(ModItems.SHIELD, "blocking", (stack, world, entity, seed) -> {
            if (entity instanceof Player player && BlockChanceHelper.isPlayerBlocking(player)) {
               return 1.0F;
            } else {
               return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
            }
         });
         ItemProperties.register(
            ModItems.DRILL_ARROW,
            new ResourceLocation("tier"),
            (stack, world, entity, seed) -> (float)ItemDrillArrow.getArrowTier(stack).ordinal() / ItemDrillArrow.ArrowTier.values().length
         );
         ItemProperties.register(
            ModItems.JEWEL_POUCH, new ResourceLocation("open"), (stack, world, entity, seed) -> JewelPouchItem.getJewels(stack).isEmpty() ? 0.0F : 1.0F
         );
         ItemProperties.register(
            Item.byBlock(ModBlocks.CRYO_CHAMBER),
            new ResourceLocation("type"),
            (stack, world, entity, seed) -> (float)stack.getDamageValue() / CryoChamberBlock.ChamberState.values().length
         );
         ItemProperties.register(ModItems.VAULT_COMPASS, new ResourceLocation("angle"), new ModModels.ItemProperty.CompassPropertyFunction());
         ItemProperties.register(
            ModItems.BOTTLE, new ResourceLocation("type"), (stack, level, entity, seed) -> BottleItem.getType(stack).orElse(BottleItem.Type.VIAL).ordinal()
         );
         ItemProperties.register(
            ModItems.BOTTLE, new ResourceLocation("fill"), (stack, level, entity, seed) -> 1.0F - (float)stack.getDamageValue() / stack.getMaxDamage()
         );
         registerItemProperty(ModBlocks.PLACEHOLDER.asItem(), "placeholder_type", PLACEHOLDER_TYPE);
         registerItemProperty(ModBlocks.TREASURE_DOOR.asItem(), "treasure_door_type", TREASURE_DOOR_TYPE);
         registerItemProperty(ModBlocks.DUNGEON_DOOR.asItem(), "dungeon_door_type", DUNGEON_DOOR_TYPE);
         registerItemProperty(ModItems.MAGNET, "magnet_perk", (stack, world, entity, seed) -> LegacyMagnetItem.getPerk(stack).ordinal());
         ItemProperties.registerGeneric(VaultMod.id("count"), (s, w, e, l) -> s.getCount());

         for (VaultOreBlock block : VaultOreBlock.ALL) {
            registerItemProperty(block.asItem(), "vault_ore_type", VAULT_ORE_TYPE);
         }

         registerItemProperty(ModBlocks.EASTER_EGG.asItem(), "easter_egg_type", EASTER_EGG_TYPE);
         registerItemProperty(ModItems.GOD_BLESSING, "god_item_type", GOD_ITEM_TYPE);
         registerItemProperty(ModItems.GOD_TOKEN, "god_item_type", GOD_ITEM_TYPE);
         registerItemProperty(ModBlocks.GOD_ALTAR.asItem(), "god_altar_type", GOD_ALTAR_TYPE);
      }

      public static void registerOverrides() {
         override(
            Items.SHIELD,
            new ResourceLocation("blocking"),
            existing -> (stack, world, entity, seed) -> entity instanceof Player player && BlockChanceHelper.isPlayerBlocking(player)
               ? 1.0F
               : existing.call(stack, world, entity, seed)
         );
      }

      public static void registerItemProperty(Item item, String name, ItemPropertyFunction property) {
         ItemProperties.register(item, VaultMod.id(name), property);
      }

      public static void override(Item item, ResourceLocation key, Function<ItemPropertyFunction, ItemPropertyFunction> decorateFn) {
         ItemPropertyFunction existing = ItemProperties.getProperty(item, key);
         if (existing != null) {
            ItemProperties.register(item, key, (ItemPropertyFunction)decorateFn.apply(existing));
         }
      }

      private static class CompassPropertyFunction implements ClampedItemPropertyFunction {
         private final CompassWobble wobble = new CompassWobble();
         private final CompassWobble wobbleRandom = new CompassWobble();

         public float unclampedCall(ItemStack compass, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, int seed) {
            if (livingEntity instanceof Player player) {
               if (level == null && livingEntity.level instanceof ClientLevel clientLevel) {
                  level = clientLevel;
               }

               BlockPos target = this.getCompassTarget(player, level, compass, seed);
               long gameTime = level.getGameTime();
               Research vaultCompass = ModConfigs.RESEARCHES.getByName("Vault Compass");
               boolean researched = vaultCompass != null && StageManager.getResearchTree(player).isResearched(vaultCompass);
               if (target != null && player.position().distanceToSqr(target.getX() + 0.5, player.position().y(), target.getZ() + 0.5) > 1.0E-5 && researched) {
                  double yRotation = player.getYRot();
                  yRotation = Mth.positiveModulo(yRotation / 360.0, 1.0);
                  double index = this.getAngleTo(Vec3.atCenterOf(target), player) / (float) (Math.PI * 2);
                  if (this.wobble.shouldUpdate(gameTime)) {
                     this.wobble.update(gameTime, 0.5 - (yRotation - 0.25));
                  }

                  index += this.wobble.rotation;
                  return Mth.positiveModulo((float)index, 1.0F);
               } else {
                  if (this.wobbleRandom.shouldUpdate(gameTime)) {
                     this.wobbleRandom.update(gameTime, Math.random());
                  }

                  float randomRotation = (float)(this.wobbleRandom.rotation + this.hash(seed) / 2.1474836E9F);
                  return Mth.positiveModulo(randomRotation, 1.0F);
               }
            } else {
               return 0.0F;
            }
         }

         @Nullable
         private BlockPos getCompassTarget(Player player, ClientLevel level, ItemStack compass, int seed) {
            return ClientVaults.getActive().flatMap(vault -> {
               Listeners listeners = vault.get(Vault.LISTENERS);
               return listeners.contains(player.getUUID()) ? listeners.get(player.getUUID()).getOptional(Listener.COMPASS_TARGET) : Optional.empty();
            }).orElse(ClientEvents.COMPASS_PROPERTY.invoke(level, player, compass, seed, null).getTarget());
         }

         private int hash(int seed) {
            return seed * 1327217883;
         }

         private double getAngleTo(Vec3 portalPos, Entity player) {
            return Math.atan2(portalPos.z() - player.getZ(), portalPos.x() - player.getX());
         }
      }
   }
}
