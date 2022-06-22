package iskallia.vault.init;

import com.google.common.base.Predicates;
import iskallia.vault.Vault;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.config.VaultGearConfig;
import iskallia.vault.item.ItemDrillArrow;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.gear.GearModelProperties;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.model.AngelArmorModel;
import iskallia.vault.item.gear.model.BarbarianArmorModel;
import iskallia.vault.item.gear.model.BoneArmorModel;
import iskallia.vault.item.gear.model.CloakArmorModel;
import iskallia.vault.item.gear.model.DevilArmorModel;
import iskallia.vault.item.gear.model.DevilDuckArmorModel;
import iskallia.vault.item.gear.model.FurArmorModel;
import iskallia.vault.item.gear.model.JawboneArmorModel;
import iskallia.vault.item.gear.model.KnightArmorModel;
import iskallia.vault.item.gear.model.LeprechaunArmorModel;
import iskallia.vault.item.gear.model.OmarlatifArmorModel;
import iskallia.vault.item.gear.model.PlatedArmorModel;
import iskallia.vault.item.gear.model.RevenantArmorModel;
import iskallia.vault.item.gear.model.RoyalArmorModel;
import iskallia.vault.item.gear.model.ScaleArmorModel;
import iskallia.vault.item.gear.model.ScrappyArmorModel;
import iskallia.vault.item.gear.model.ScubaArmorModel;
import iskallia.vault.item.gear.model.VaultGearModel;
import iskallia.vault.item.gear.specials.AutomaticArmorModel;
import iskallia.vault.item.gear.specials.BotaniaArmorModel;
import iskallia.vault.item.gear.specials.BuildingArmorModel;
import iskallia.vault.item.gear.specials.CakeArmorModel;
import iskallia.vault.item.gear.specials.CheeseHatModel;
import iskallia.vault.item.gear.specials.CreateArmorModel;
import iskallia.vault.item.gear.specials.DankArmorModel;
import iskallia.vault.item.gear.specials.FairyArmorModel;
import iskallia.vault.item.gear.specials.FluxArmorModel;
import iskallia.vault.item.gear.specials.HellcowArmorModel;
import iskallia.vault.item.gear.specials.ImmersiveEngineeringArmorModel;
import iskallia.vault.item.gear.specials.IndustrialForegoingArmorModel;
import iskallia.vault.item.gear.specials.IskallHololensModel;
import iskallia.vault.item.gear.specials.MekaArmorModel;
import iskallia.vault.item.gear.specials.PowahArmorModel;
import iskallia.vault.item.gear.specials.SkallibombaArmorModel;
import iskallia.vault.item.gear.specials.TestDummyArmorModel;
import iskallia.vault.item.gear.specials.ThermalArmorModel;
import iskallia.vault.item.gear.specials.TrashArmorModel;
import iskallia.vault.item.gear.specials.VillagerArmorModel;
import iskallia.vault.item.gear.specials.XnetArmorModel;
import iskallia.vault.item.gear.specials.ZombieArmorModel;
import iskallia.vault.util.MiscUtils;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ModModels {
   public static void setupRenderLayers() {
      RenderTypeLookup.setRenderLayer(ModBlocks.VAULT_PORTAL, RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ISKALLIUM_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.GORGINITE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.SPARKLETINE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ASHIUM_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.BOMIGNITE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.FUNSOIDE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.TUBIUM_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.UPALINE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.PUFFIUM_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.VAULT_ALTAR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.VAULT_ARTIFACT, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.KEY_PRESS, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.OMEGA_STATUE, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.XP_ALTAR, RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer(ModBlocks.BLOOD_ALTAR, RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer(ModBlocks.TIME_ALTAR, RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer(ModBlocks.SOUL_ALTAR, RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer(ModBlocks.VAULT_GLASS, RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer(ModBlocks.FINAL_VAULT_FRAME, RenderType.func_228643_e_());
      setRenderLayers(ModBlocks.VENDING_MACHINE, RenderType.func_228643_e_(), RenderType.func_228645_f_());
      setRenderLayers(ModBlocks.ADVANCED_VENDING_MACHINE, RenderType.func_228643_e_(), RenderType.func_228645_f_());
      setRenderLayers(ModBlocks.CRYO_CHAMBER, RenderType.func_228639_c_(), RenderType.func_228645_f_());
      setRenderLayers(ModBlocks.HOURGLASS, RenderType.func_228639_c_(), RenderType.func_228645_f_());
      setRenderLayers(ModBlocks.VAULT_CRATE_SCAVENGER, RenderType.func_228639_c_(), RenderType.func_228645_f_());
      setRenderLayers(ModBlocks.VAULT_CRATE_CAKE, RenderType.func_228643_e_());
      setRenderLayers(ModBlocks.STABILIZER, RenderType.func_228639_c_(), RenderType.func_228645_f_());
      setRenderLayers(ModBlocks.RAID_CONTROLLER_BLOCK, RenderType.func_228639_c_(), RenderType.func_228645_f_());
      setRenderLayers(ModBlocks.VAULT_CHARM_CONTROLLER_BLOCK, RenderType.func_228639_c_(), RenderType.func_228645_f_());
   }

   private static void setRenderLayers(Block block, RenderType... renderTypes) {
      RenderTypeLookup.setRenderLayer(block, Predicates.in(Arrays.asList(renderTypes)));
   }

   public static void registerItemColors(ItemColors colors) {
      colors.func_199877_a((stack, color) -> {
         if (color > 0) {
            if (ModAttributes.GEAR_STATE.getBase(stack).orElse(VaultGear.State.UNIDENTIFIED) == VaultGear.State.UNIDENTIFIED) {
               String gearType = ModAttributes.GEAR_ROLL_TYPE.getBase(stack).orElse(null);
               VaultGearConfig.General.Roll gearRoll = ModConfigs.VAULT_GEAR.getRoll(gearType).orElse(null);
               if (gearRoll != null) {
                  return MiscUtils.blendColors(gearRoll.getColor(), 16777215, 0.8F);
               }
            }

            return -1;
         } else {
            return ((IDyeableArmorItem)stack.func_77973_b()).func_200886_f(stack);
         }
      }, new IItemProvider[]{ModItems.HELMET, ModItems.CHESTPLATE, ModItems.LEGGINGS, ModItems.BOOTS});
      colors.func_199877_a((stack, color) -> {
         if (color > 0) {
            if (ModAttributes.GEAR_STATE.getBase(stack).orElse(VaultGear.State.UNIDENTIFIED) == VaultGear.State.UNIDENTIFIED) {
               String gearType = ModAttributes.GEAR_ROLL_TYPE.getBase(stack).orElse(null);
               VaultGearConfig.General.Roll gearRoll = ModConfigs.VAULT_GEAR.getRoll(gearType).orElse(null);
               if (gearRoll != null) {
                  return MiscUtils.blendColors(gearRoll.getColor(), 16777215, 0.8F);
               }
            }

            return -1;
         } else {
            return ((VaultGear)stack.func_77973_b()).getColor(stack.func_77973_b(), stack);
         }
      }, new IItemProvider[]{ModItems.AXE, ModItems.SWORD});
      colors.func_199877_a((stack, color) -> {
         if (color > 0) {
            if (ModAttributes.GEAR_STATE.getBase(stack).orElse(VaultGear.State.UNIDENTIFIED) == VaultGear.State.UNIDENTIFIED) {
               String gearType = ModAttributes.GEAR_ROLL_TYPE.getBase(stack).orElse(null);
               VaultGearConfig.General.Roll gearRoll = ModConfigs.VAULT_GEAR.getRoll(gearType).orElse(null);
               if (gearRoll != null) {
                  return MiscUtils.blendColors(gearRoll.getColor(), 16777215, 0.8F);
               }
            }

            return -1;
         } else {
            return -1;
         }
      }, new IItemProvider[]{ModItems.IDOL_BENEVOLENT, ModItems.IDOL_OMNISCIENT, ModItems.IDOL_TIMEKEEPER, ModItems.IDOL_MALEVOLENCE});
   }

   public static class GearModel {
      public static Map<Integer, ModModels.GearModel> SCRAPPY_REGISTRY;
      public static Map<Integer, ModModels.GearModel> REGISTRY;
      public static ModModels.GearModel SCRAPPY_1;
      public static ModModels.GearModel SCRAPPY_2;
      public static ModModels.GearModel SCRAPPY_3;
      public static ModModels.GearModel SCALE_1;
      public static ModModels.GearModel SCALE_2;
      public static ModModels.GearModel SCALE_3;
      public static ModModels.GearModel SCALE_4;
      public static ModModels.GearModel PLATED_1;
      public static ModModels.GearModel PLATED_1_DARK;
      public static ModModels.GearModel PLATED_2;
      public static ModModels.GearModel PLATED_2_DARK;
      public static ModModels.GearModel PLATED_3;
      public static ModModels.GearModel PLATED_3_DARK;
      public static ModModels.GearModel PLATED_4;
      public static ModModels.GearModel PLATED_4_DARK;
      public static ModModels.GearModel FUR_1;
      public static ModModels.GearModel FUR_2;
      public static ModModels.GearModel FUR_3;
      public static ModModels.GearModel FUR_4;
      public static ModModels.GearModel CLOAK_1;
      public static ModModels.GearModel CLOAK_2;
      public static ModModels.GearModel CLOAK_3;
      public static ModModels.GearModel CLOAK_4;
      public static ModModels.GearModel ROYAL_1;
      public static ModModels.GearModel ROYAL_2;
      public static ModModels.GearModel SCRAPPY_1_NORMAL;
      public static ModModels.GearModel SCRAPPY_2_NORMAL;
      public static ModModels.GearModel SCRAPPY_3_NORMAL;
      public static ModModels.GearModel BARBARIAN_1;
      public static ModModels.GearModel BARBARIAN_2;
      public static ModModels.GearModel BARBARIAN_3;
      public static ModModels.GearModel ROYAL_1_DARK;
      public static ModModels.GearModel BARBARIAN_1_DARK;
      public static ModModels.GearModel BARBARIAN_2_DARK;
      public static ModModels.GearModel BARBARIAN_3_DARK;
      public static ModModels.GearModel OMARLATIF;
      public static ModModels.GearModel SCUBA_1;
      public static ModModels.GearModel LEPRECHAUN_1;
      public static ModModels.GearModel BONE_1;
      public static ModModels.GearModel JAWBONE_1;
      public static ModModels.GearModel REVENANT_1;
      public static ModModels.GearModel REVENANT_2;
      public static ModModels.GearModel KNIGHT_1;
      public static ModModels.GearModel KNIGHT_2;
      public static ModModels.GearModel KNIGHT_3;
      public static ModModels.GearModel DEVIL_DUCK_1;
      public static ModModels.GearModel ANGEL_1;
      public static ModModels.GearModel DEVIL_1;
      int id;
      String displayName;
      VaultGearModel<? extends LivingEntity> helmetModel;
      VaultGearModel<? extends LivingEntity> chestplateModel;
      VaultGearModel<? extends LivingEntity> leggingsModel;
      VaultGearModel<? extends LivingEntity> bootsModel;

      public static void register() {
         SCRAPPY_REGISTRY = new HashMap<>();
         REGISTRY = new HashMap<>();
         SCRAPPY_1 = registerScrappy("Scrappy 1", () -> ScrappyArmorModel.Variant1.class);
         SCRAPPY_2 = registerScrappy("Scrappy 2", () -> ScrappyArmorModel.Variant2.class);
         SCRAPPY_3 = registerScrappy("Scrappy 3", () -> ScrappyArmorModel.Variant3.class);
         SCALE_1 = register("Scale 1", () -> ScaleArmorModel.Variant1.class);
         SCALE_2 = register("Scale 2", () -> ScaleArmorModel.Variant2.class);
         SCALE_3 = register("Scale 3", () -> ScaleArmorModel.Variant3.class);
         SCALE_4 = register("Scale 4", () -> ScaleArmorModel.Variant4.class);
         PLATED_1 = register("Plated 1", () -> PlatedArmorModel.Variant1.class);
         PLATED_1_DARK = register("Plated 1 Dark", () -> PlatedArmorModel.Variant1.class);
         PLATED_2 = register("Plated 2", () -> PlatedArmorModel.Variant2.class);
         PLATED_2_DARK = register("Plated 2 Dark", () -> PlatedArmorModel.Variant2.class);
         PLATED_3 = register("Plated 3", () -> PlatedArmorModel.Variant3.class);
         PLATED_3_DARK = register("Plated 3 Dark", () -> PlatedArmorModel.Variant3.class);
         PLATED_4 = register("Plated 4", () -> PlatedArmorModel.Variant4.class);
         PLATED_4_DARK = register("Plated 4 Dark", () -> PlatedArmorModel.Variant4.class);
         FUR_1 = register("Fur 1", () -> FurArmorModel.Variant1.class);
         FUR_2 = register("Fur 2", () -> FurArmorModel.Variant2.class);
         FUR_3 = register("Fur 3", () -> FurArmorModel.Variant3.class);
         FUR_4 = register("Fur 4", () -> FurArmorModel.Variant4.class);
         CLOAK_1 = register("Cloak 1", () -> CloakArmorModel.Variant1.class);
         CLOAK_2 = register("Cloak 2", () -> CloakArmorModel.Variant2.class);
         CLOAK_3 = register("Cloak 3", () -> CloakArmorModel.Variant3.class);
         CLOAK_4 = register("Cloak 4", () -> CloakArmorModel.Variant4.class);
         ROYAL_1 = register("Royal 1", () -> RoyalArmorModel.Variant1.class);
         ROYAL_2 = register("Royal 2", () -> RoyalArmorModel.Variant2.class);
         SCRAPPY_1_NORMAL = register("Scrappy 1", () -> ScrappyArmorModel.Variant1.class);
         SCRAPPY_2_NORMAL = register("Scrappy 2", () -> ScrappyArmorModel.Variant2.class);
         SCRAPPY_3_NORMAL = register("Scrappy 3", () -> ScrappyArmorModel.Variant3.class);
         BARBARIAN_1 = register("Barbarian 1", () -> BarbarianArmorModel.Variant1.class);
         BARBARIAN_2 = register("Barbarian 2", () -> BarbarianArmorModel.Variant2.class);
         BARBARIAN_3 = register("Barbarian 3", () -> BarbarianArmorModel.Variant3.class);
         ROYAL_1_DARK = register("Royal 1 Dark", () -> RoyalArmorModel.Variant1.class);
         BARBARIAN_1_DARK = register("Barbarian 1 Dark", () -> BarbarianArmorModel.Variant1.class);
         BARBARIAN_2_DARK = register("Barbarian 2 Dark", () -> BarbarianArmorModel.Variant2.class);
         BARBARIAN_3_DARK = register("Barbarian 3 Dark", () -> BarbarianArmorModel.Variant3.class);
         OMARLATIF = register("Omarlatif", () -> OmarlatifArmorModel.class);
         SCUBA_1 = register("Scuba 1", () -> ScubaArmorModel.Variant1.class);
         LEPRECHAUN_1 = register("Leprechaun 1", () -> LeprechaunArmorModel.Variant1.class);
         BONE_1 = register("Bone 1", () -> BoneArmorModel.Variant1.class);
         JAWBONE_1 = register("Jawbone 1", () -> JawboneArmorModel.Variant1.class);
         REVENANT_1 = register("Revenant 1", () -> RevenantArmorModel.Variant1.class);
         REVENANT_2 = register("Revenant 2", () -> RevenantArmorModel.Variant2.class);
         KNIGHT_1 = register("Knight 1", () -> KnightArmorModel.Variant1.class);
         KNIGHT_2 = register("Knight 2", () -> KnightArmorModel.Variant2.class);
         KNIGHT_3 = register("Knight 3", () -> KnightArmorModel.Variant3.class);
         DEVIL_DUCK_1 = register("DevilDuck 1", () -> DevilDuckArmorModel.Variant1.class);
         ANGEL_1 = register("Angel 1", () -> AngelArmorModel.Variant1.class);
         DEVIL_1 = register("Devil 1", () -> DevilArmorModel.Variant1.class);
      }

      public VaultGearModel<? extends LivingEntity> forSlotType(EquipmentSlotType slotType) {
         switch (slotType) {
            case HEAD:
               return this.helmetModel;
            case CHEST:
               return this.chestplateModel;
            case LEGS:
               return this.leggingsModel;
            case FEET:
            default:
               return this.bootsModel;
         }
      }

      public int getId() {
         return this.id;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public String getTextureName(EquipmentSlotType slotType, String type) {
         String base = Vault.sId("textures/models/armor/" + this.displayName.toLowerCase().replace(" ", "_") + "_armor")
            + (slotType == EquipmentSlotType.LEGS ? "_layer2" : "_layer1");
         return (type == null ? base : base + "_" + type) + ".png";
      }

      private static <T extends VaultGearModel<?>> ModModels.GearModel registerScrappy(String textureName, Supplier<Class<T>> modelClassSupplier) {
         return register(textureName, modelClassSupplier, SCRAPPY_REGISTRY);
      }

      private static <T extends VaultGearModel<?>> ModModels.GearModel register(String textureName, Supplier<Class<T>> modelClassSupplier) {
         return register(textureName, modelClassSupplier, REGISTRY);
      }

      private static <T extends VaultGearModel<?>> ModModels.GearModel register(
         String textureName, Supplier<Class<T>> modelClassSupplier, Map<Integer, ModModels.GearModel> registry
      ) {
         try {
            ModModels.GearModel gearModel = new ModModels.GearModel();
            gearModel.displayName = textureName;
            gearModel.id = registry.size();
            if (FMLEnvironment.dist.isClient()) {
               Class<T> modelClass = modelClassSupplier.get();

               assert modelClass != null;

               Constructor<T> constructor = modelClass.getConstructor(float.class, EquipmentSlotType.class);
               T helmetModel = (T)constructor.newInstance(1.0F, EquipmentSlotType.HEAD);
               T chestplateModel = (T)constructor.newInstance(1.0F, EquipmentSlotType.CHEST);
               T leggingsModel = (T)constructor.newInstance(1.0F, EquipmentSlotType.LEGS);
               T bootsModel = (T)constructor.newInstance(1.0F, EquipmentSlotType.FEET);
               gearModel.helmetModel = helmetModel;
               gearModel.chestplateModel = chestplateModel;
               gearModel.leggingsModel = leggingsModel;
               gearModel.bootsModel = bootsModel;
            }

            registry.put(gearModel.id, gearModel);
            return gearModel;
         } catch (Exception var10) {
            throw new InternalError("Error while registering Gear Model: " + textureName);
         }
      }
   }

   public static class ItemProperty {
      public static IItemPropertyGetter SPECIAL_GEAR_TEXTURE = (stack, world, entity) -> ModAttributes.GEAR_SPECIAL_MODEL
         .getOrDefault(stack, -1)
         .getValue(stack)
         .intValue();
      public static IItemPropertyGetter GEAR_TEXTURE = (stack, world, entity) -> ModAttributes.GEAR_MODEL.getOrDefault(stack, -1).getValue(stack).intValue();
      public static IItemPropertyGetter GEAR_RARITY = (stack, world, entity) -> ModAttributes.GEAR_RARITY
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .map(Enum::ordinal)
         .orElse(-1)
         .intValue();
      public static IItemPropertyGetter ETCHING = (stack, world, entity) -> ModAttributes.GEAR_SET
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .map(Enum::ordinal)
         .orElse(-1)
         .intValue();
      public static IItemPropertyGetter PUZZLE_COLOR = (stack, world, entity) -> ModAttributes.PUZZLE_COLOR
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .map(Enum::ordinal)
         .orElse(-1)
         .intValue();

      public static void register() {
         registerItemProperty(ModItems.SWORD, "special_texture", SPECIAL_GEAR_TEXTURE);
         registerItemProperty(ModItems.HELMET, "special_texture", SPECIAL_GEAR_TEXTURE);
         registerItemProperty(ModItems.CHESTPLATE, "special_texture", SPECIAL_GEAR_TEXTURE);
         registerItemProperty(ModItems.LEGGINGS, "special_texture", SPECIAL_GEAR_TEXTURE);
         registerItemProperty(ModItems.BOOTS, "special_texture", SPECIAL_GEAR_TEXTURE);
         registerItemProperty(ModItems.SWORD, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.AXE, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.HELMET, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.CHESTPLATE, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.LEGGINGS, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.BOOTS, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.ETCHING, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.IDOL_BENEVOLENT, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.IDOL_OMNISCIENT, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.IDOL_TIMEKEEPER, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.IDOL_MALEVOLENCE, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.SWORD, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.AXE, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.HELMET, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.CHESTPLATE, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.LEGGINGS, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.BOOTS, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.ETCHING, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.IDOL_BENEVOLENT, "vault_rarity", GEAR_TEXTURE);
         registerItemProperty(ModItems.IDOL_OMNISCIENT, "vault_rarity", GEAR_TEXTURE);
         registerItemProperty(ModItems.IDOL_TIMEKEEPER, "vault_rarity", GEAR_TEXTURE);
         registerItemProperty(ModItems.IDOL_MALEVOLENCE, "vault_rarity", GEAR_TEXTURE);
         registerItemProperty(ModItems.ETCHING, "vault_set", ETCHING);
         registerItemProperty(ModItems.PUZZLE_RUNE, "puzzle_color", PUZZLE_COLOR);
         registerItemProperty(ModBlocks.PUZZLE_RUNE_BLOCK_ITEM, "puzzle_color", PUZZLE_COLOR);
         ItemModelsProperties.func_239418_a_(
            ModItems.DRILL_ARROW,
            new ResourceLocation("tier"),
            (stack, world, entity) -> (float)ItemDrillArrow.getArrowTier(stack).ordinal() / ItemDrillArrow.ArrowTier.values().length
         );
         ItemModelsProperties.func_239418_a_(
            Item.func_150898_a(ModBlocks.CRYO_CHAMBER),
            new ResourceLocation("type"),
            (stack, world, entity) -> (float)stack.func_77952_i() / CryoChamberBlock.ChamberState.values().length
         );
         ItemModelsProperties.func_239418_a_(
            ModItems.VAULT_CRYSTAL, new ResourceLocation("type"), (stack, world, entity) -> VaultCrystalItem.getData(stack).getType().ordinal()
         );
      }

      public static void registerItemProperty(Item item, String name, IItemPropertyGetter property) {
         ItemModelsProperties.func_239418_a_(item, Vault.id(name), property);
      }
   }

   public static class SpecialGearModel {
      public static Map<Integer, ModModels.SpecialGearModel> HEAD_REGISTRY;
      public static Map<Integer, ModModels.SpecialGearModel> CHESTPLATE_REGISTRY;
      public static Map<Integer, ModModels.SpecialGearModel> LEGGINGS_REGISTRY;
      public static Map<Integer, ModModels.SpecialGearModel> BOOTS_REGISTRY;
      public static ModModels.SpecialGearModel CHEESE_HAT;
      public static ModModels.SpecialGearModel ISKALL_HOLOLENS;
      public static ModModels.SpecialGearModel.SpecialGearModelSet HELLCOW_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet BOTANIA_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet CREATE_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet DANK_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet FLUX_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet IMMERSIVE_ENGINEERING_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet MEKA_SET_LIGHT;
      public static ModModels.SpecialGearModel.SpecialGearModelSet MEKA_SET_DARK;
      public static ModModels.SpecialGearModel.SpecialGearModelSet POWAH_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet THERMAL_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet TRASH_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet SKALLIBOMBA_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet VILLAGER_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet AUTOMATIC_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet FAIRY_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet BUILDING_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet ZOMBIE_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet XNET_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet TEST_DUMMY_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet INDUSTRIAL_FOREGOING_SET;
      public static ModModels.SpecialGearModel.SpecialGearModelSet CAKE_SET;
      int id;
      String displayName;
      VaultGearModel<? extends LivingEntity> model;
      GearModelProperties modelProperties = new GearModelProperties();

      public static Map<Integer, ModModels.SpecialGearModel> getRegistryForSlot(EquipmentSlotType slotType) {
         switch (slotType) {
            case HEAD:
               return HEAD_REGISTRY;
            case CHEST:
               return CHESTPLATE_REGISTRY;
            case LEGS:
               return LEGGINGS_REGISTRY;
            case FEET:
            default:
               return BOOTS_REGISTRY;
         }
      }

      public static ModModels.SpecialGearModel getModel(EquipmentSlotType slotType, int id) {
         Map<Integer, ModModels.SpecialGearModel> registry = getRegistryForSlot(slotType);
         return registry == null ? null : registry.get(id);
      }

      public static void register() {
         HEAD_REGISTRY = new HashMap<>();
         CHESTPLATE_REGISTRY = new HashMap<>();
         LEGGINGS_REGISTRY = new HashMap<>();
         BOOTS_REGISTRY = new HashMap<>();
         CHEESE_HAT = registerHead("Cheese Hat", () -> CheeseHatModel.class);
         ISKALL_HOLOLENS = registerHead("Iskall Hololens", () -> IskallHololensModel.class);
         HELLCOW_SET = registerSet("Hellcow", () -> HellcowArmorModel.class, new GearModelProperties().allowTransmogrification());
         BOTANIA_SET = registerSet("Botania", () -> BotaniaArmorModel.class, new GearModelProperties().allowTransmogrification());
         CREATE_SET = registerSet("Create", () -> CreateArmorModel.class, new GearModelProperties().allowTransmogrification());
         DANK_SET = registerSet("Dank", () -> DankArmorModel.class, new GearModelProperties().allowTransmogrification());
         FLUX_SET = registerSet("Flux", () -> FluxArmorModel.class, new GearModelProperties().allowTransmogrification());
         IMMERSIVE_ENGINEERING_SET = registerSet(
            "Immersive Engineering", () -> ImmersiveEngineeringArmorModel.class, new GearModelProperties().allowTransmogrification()
         );
         MEKA_SET_LIGHT = registerSet("Meka Light", () -> MekaArmorModel.class, new GearModelProperties().allowTransmogrification());
         MEKA_SET_DARK = registerSet("Meka Dark", () -> MekaArmorModel.class, new GearModelProperties().allowTransmogrification());
         POWAH_SET = registerSet("Powah", () -> PowahArmorModel.class, new GearModelProperties().allowTransmogrification());
         THERMAL_SET = registerSet("Thermal", () -> ThermalArmorModel.class, new GearModelProperties().allowTransmogrification());
         TRASH_SET = registerSet("Trash", () -> TrashArmorModel.class, new GearModelProperties().allowTransmogrification());
         SKALLIBOMBA_SET = registerSet("Skallibomba", () -> SkallibombaArmorModel.class, new GearModelProperties().allowTransmogrification());
         VILLAGER_SET = registerSet("Villager", () -> VillagerArmorModel.class, new GearModelProperties().allowTransmogrification());
         AUTOMATIC_SET = registerSet("Automatic", () -> AutomaticArmorModel.class, new GearModelProperties().allowTransmogrification());
         FAIRY_SET = registerSet("Fairy", () -> FairyArmorModel.class, new GearModelProperties().allowTransmogrification());
         BUILDING_SET = registerSet("Building", () -> BuildingArmorModel.class, new GearModelProperties().allowTransmogrification());
         ZOMBIE_SET = registerSet("Zombie", () -> ZombieArmorModel.class, new GearModelProperties().allowTransmogrification());
         XNET_SET = registerSet("Xnet", () -> XnetArmorModel.class, new GearModelProperties().allowTransmogrification());
         TEST_DUMMY_SET = registerSet("Test Dummy", () -> TestDummyArmorModel.class, new GearModelProperties().allowTransmogrification());
         INDUSTRIAL_FOREGOING_SET = registerSet(
            "Industrial Foregoing", () -> IndustrialForegoingArmorModel.class, new GearModelProperties().allowTransmogrification()
         );
         CAKE_SET = registerSet("Cake", () -> CakeArmorModel.class, new GearModelProperties().allowTransmogrification());
      }

      public int getId() {
         return this.id;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public VaultGearModel<? extends LivingEntity> getModel() {
         return this.model;
      }

      public String getTextureName(EquipmentSlotType slotType, String type) {
         if (this.modelProperties.isPieceOfSet()) {
            String base = Vault.sId("textures/models/armor/special/" + this.displayName.toLowerCase().replace(" ", "_") + "_armor")
               + (slotType == EquipmentSlotType.LEGS ? "_layer2" : "_layer1");
            return (type == null ? base : base + "_" + type) + ".png";
         } else {
            String base = Vault.sId("textures/models/armor/special/" + this.displayName.toLowerCase().replace(" ", "_"));
            return (type == null ? base : base + "_" + type) + ".png";
         }
      }

      public GearModelProperties getModelProperties() {
         return this.modelProperties;
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel.SpecialGearModelSet registerSet(
         String textureName, Supplier<Class<T>> modelClassSupplier
      ) {
         return registerSet(textureName, modelClassSupplier, new GearModelProperties());
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel.SpecialGearModelSet registerSet(
         String textureName, Supplier<Class<T>> modelClassSupplier, GearModelProperties modelProperties
      ) {
         ModModels.SpecialGearModel.SpecialGearModelSet set = new ModModels.SpecialGearModel.SpecialGearModelSet();
         modelProperties.makePieceOfSet();
         set.head = registerHead(textureName, modelClassSupplier, modelProperties);
         set.chestplate = registerChestplate(textureName, modelClassSupplier, modelProperties);
         set.leggings = registerLeggings(textureName, modelClassSupplier, modelProperties);
         set.boots = registerBoots(textureName, modelClassSupplier, modelProperties);
         return set;
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel registerHead(String textureName, Supplier<Class<T>> modelClassSupplier) {
         return registerHead(textureName, modelClassSupplier, new GearModelProperties());
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel registerHead(
         String textureName, Supplier<Class<T>> modelClassSupplier, GearModelProperties modelProperties
      ) {
         return register(textureName, modelClassSupplier, modelProperties, EquipmentSlotType.HEAD, HEAD_REGISTRY);
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel registerChestplate(String textureName, Supplier<Class<T>> modelClassSupplier) {
         return registerChestplate(textureName, modelClassSupplier, new GearModelProperties());
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel registerChestplate(
         String textureName, Supplier<Class<T>> modelClassSupplier, GearModelProperties modelProperties
      ) {
         return register(textureName, modelClassSupplier, modelProperties, EquipmentSlotType.CHEST, CHESTPLATE_REGISTRY);
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel registerLeggings(String textureName, Supplier<Class<T>> modelClassSupplier) {
         return registerLeggings(textureName, modelClassSupplier, new GearModelProperties());
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel registerLeggings(
         String textureName, Supplier<Class<T>> modelClassSupplier, GearModelProperties modelProperties
      ) {
         return register(textureName, modelClassSupplier, modelProperties, EquipmentSlotType.LEGS, LEGGINGS_REGISTRY);
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel registerBoots(String textureName, Supplier<Class<T>> modelClassSupplier) {
         return registerBoots(textureName, modelClassSupplier, new GearModelProperties());
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel registerBoots(
         String textureName, Supplier<Class<T>> modelClassSupplier, GearModelProperties modelProperties
      ) {
         return register(textureName, modelClassSupplier, modelProperties, EquipmentSlotType.FEET, BOOTS_REGISTRY);
      }

      private static <T extends VaultGearModel<?>> ModModels.SpecialGearModel register(
         String textureName,
         Supplier<Class<T>> modelClassSupplier,
         GearModelProperties modelProperties,
         EquipmentSlotType slotType,
         Map<Integer, ModModels.SpecialGearModel> registry
      ) {
         try {
            ModModels.SpecialGearModel specialGearModel = new ModModels.SpecialGearModel();
            specialGearModel.displayName = textureName;
            specialGearModel.id = registry.size();
            specialGearModel.modelProperties = modelProperties;
            if (FMLEnvironment.dist.isClient()) {
               Class<T> modelClass = modelClassSupplier.get();
               Constructor<T> constructor = modelClass.getConstructor(float.class, EquipmentSlotType.class);
               specialGearModel.model = constructor.newInstance(1.0F, slotType);
            }

            registry.put(specialGearModel.id, specialGearModel);
            return specialGearModel;
         } catch (Exception var8) {
            throw new InternalError("Error while registering Special Gear Model: " + textureName);
         }
      }

      public static class SpecialGearModelSet {
         public ModModels.SpecialGearModel head;
         public ModModels.SpecialGearModel chestplate;
         public ModModels.SpecialGearModel leggings;
         public ModModels.SpecialGearModel boots;

         public ModModels.SpecialGearModel modelForSlot(EquipmentSlotType slot) {
            if (slot == EquipmentSlotType.HEAD) {
               return this.head;
            } else if (slot == EquipmentSlotType.CHEST) {
               return this.chestplate;
            } else {
               return slot == EquipmentSlotType.LEGS ? this.leggings : this.boots;
            }
         }
      }
   }

   public static class SpecialSwordModel {
      public static Map<Integer, ModModels.SpecialSwordModel> REGISTRY;
      public static ModModels.SpecialSwordModel JANITORS_BROOM;
      int id;
      String displayName;
      GearModelProperties modelProperties = new GearModelProperties();

      public static ModModels.SpecialSwordModel getModel(int id) {
         return REGISTRY.get(id);
      }

      public static void register() {
         REGISTRY = new HashMap<>();
         JANITORS_BROOM = register("Janitor's Broom", new GearModelProperties().allowTransmogrification());
      }

      public int getId() {
         return this.id;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public GearModelProperties getModelProperties() {
         return this.modelProperties;
      }

      private static ModModels.SpecialSwordModel register(String displayName) {
         ModModels.SpecialSwordModel swordModel = new ModModels.SpecialSwordModel();
         swordModel.displayName = displayName;
         swordModel.id = REGISTRY.size();
         REGISTRY.put(swordModel.id, swordModel);
         return swordModel;
      }

      private static ModModels.SpecialSwordModel register(String displayName, GearModelProperties modelProperties) {
         ModModels.SpecialSwordModel swordModel = register(displayName);
         swordModel.modelProperties = modelProperties;
         return swordModel;
      }
   }
}
