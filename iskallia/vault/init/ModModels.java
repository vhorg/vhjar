package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.model.DragonArmorModel;
import iskallia.vault.item.gear.model.GuardArmorModel;
import iskallia.vault.item.gear.model.KnightArmorModel;
import iskallia.vault.item.gear.model.Plated1ArmorModel;
import iskallia.vault.item.gear.model.Plated2ArmorModel;
import iskallia.vault.item.gear.model.Plated3ArmorModel;
import iskallia.vault.item.gear.model.Plated4ArmorModel;
import iskallia.vault.item.gear.model.SamuraiArmorModel;
import iskallia.vault.item.gear.model.VaultGearModel;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.RenderType.State;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.IItemProvider;

public class ModModels {
   public static void setupRenderLayers() {
      RenderTypeLookup.setRenderLayer(ModBlocks.VAULT_PORTAL, RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ALEXANDRITE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.BENITOITE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.LARIMAR_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.BLACK_OPAL_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.PAINITE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ISKALLIUM_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.RENIUM_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.GORGINITE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.SPARKLETINE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.WUTODIE_DOOR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.VAULT_ALTAR, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_1, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_2, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_3, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_4, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_5, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_6, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_7, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_8, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_9, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_10, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_11, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_12, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_13, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_14, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_15, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_16, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.MVP_CROWN, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.VENDING_MACHINE, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.ADVANCED_VENDING_MACHINE, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.GLOBAL_TRADER, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.CRYO_CHAMBER, RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer(ModBlocks.KEY_PRESS, RenderType.func_228643_e_());
   }

   public static void registerItemColors(ItemColors colors) {
      colors.func_199877_a(
         (stack, color) -> color > 0 ? -1 : ((IDyeableArmorItem)stack.func_77973_b()).func_200886_f(stack),
         new IItemProvider[]{ModItems.HELMET, ModItems.CHESTPLATE, ModItems.LEGGINGS, ModItems.BOOTS}
      );
      colors.func_199877_a(
         (stack, color) -> color > 0 ? -1 : ((VaultGear)stack.func_77973_b()).getColor(stack.func_77973_b(), stack),
         new IItemProvider[]{ModItems.AXE, ModItems.SWORD, ModItems.DAGGER}
      );
   }

   private static class CustomRenderType extends RenderType {
      private static final RenderType INSTANCE = func_228633_a_(
         "cutout_ignoring_normals",
         DefaultVertexFormats.field_176600_a,
         7,
         131072,
         true,
         false,
         State.func_228694_a_()
            .func_228723_a_(field_228520_l_)
            .func_228719_a_(field_228528_t_)
            .func_228724_a_(field_228522_n_)
            .func_228713_a_(field_228518_j_)
            .func_228728_a_(true)
      );

      public CustomRenderType(
         String nameIn,
         VertexFormat formatIn,
         int drawModeIn,
         int bufferSizeIn,
         boolean useDelegateIn,
         boolean needsSortingIn,
         Runnable setupTaskIn,
         Runnable clearTaskIn
      ) {
         super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
      }
   }

   public static class GearModel {
      public static Map<Integer, ModModels.GearModel> REGISTRY;
      public static ModModels.GearModel SCRAPPY;
      public static ModModels.GearModel SAMURAI;
      public static ModModels.GearModel KNIGHT;
      public static ModModels.GearModel GUARD;
      public static ModModels.GearModel DRAGON;
      public static ModModels.GearModel PLATED_1;
      public static ModModels.GearModel PLATED_1_DARK;
      public static ModModels.GearModel PLATED_2;
      public static ModModels.GearModel PLATED_2_DARK;
      public static ModModels.GearModel PLATED_3;
      public static ModModels.GearModel PLATED_3_DARK;
      public static ModModels.GearModel PLATED_4;
      public static ModModels.GearModel PLATED_4_DARK;
      String displayName;
      VaultGearModel<? extends LivingEntity> helmetModel;
      VaultGearModel<? extends LivingEntity> chestplateModel;
      VaultGearModel<? extends LivingEntity> leggingsModel;
      VaultGearModel<? extends LivingEntity> bootsModel;

      public static void register() {
         REGISTRY = new HashMap<>();
         SCRAPPY = register("Scrappy", null);
         SAMURAI = register("Samurai", SamuraiArmorModel.class);
         KNIGHT = register("Knight", KnightArmorModel.class);
         GUARD = register("Guard", GuardArmorModel.class);
         DRAGON = register("Dragon", DragonArmorModel.class);
         PLATED_1 = register("Plated 1", Plated1ArmorModel.class);
         PLATED_1_DARK = register("Plated 1 Dark", Plated1ArmorModel.class);
         PLATED_2 = register("Plated 2", Plated2ArmorModel.class);
         PLATED_2_DARK = register("Plated 2 Dark", Plated2ArmorModel.class);
         PLATED_3 = register("Plated 3", Plated3ArmorModel.class);
         PLATED_3_DARK = register("Plated 3 Dark", Plated3ArmorModel.class);
         PLATED_4 = register("Plated 4", Plated4ArmorModel.class);
         PLATED_4_DARK = register("Plated 4 Dark", Plated4ArmorModel.class);
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

      public String getDisplayName() {
         return this.displayName;
      }

      public String getTextureName(EquipmentSlotType slotType, String type) {
         String base = Vault.sId("textures/models/armor/" + this.displayName.toLowerCase().replace(" ", "_") + "_armor")
            + (slotType == EquipmentSlotType.LEGS ? "_layer2" : "_layer1");
         return (type == null ? base : base + "_" + type) + ".png";
      }

      private static <T extends VaultGearModel<?>> ModModels.GearModel register(String textureName, Class<T> modelClass) {
         try {
            ModModels.GearModel gearModel = new ModModels.GearModel();
            gearModel.displayName = textureName;
            if (modelClass != null) {
               Constructor<T> constructor = modelClass.getConstructor(float.class, EquipmentSlotType.class);
               T helmetModel = (T)constructor.newInstance(1.0F, EquipmentSlotType.HEAD);
               T chestplateModel = (T)constructor.newInstance(1.0F, EquipmentSlotType.CHEST);
               T leggingsModel = (T)constructor.newInstance(1.0F, EquipmentSlotType.LEGS);
               T bootsModel = (T)constructor.newInstance(1.0F, EquipmentSlotType.FEET);
               gearModel.helmetModel = helmetModel;
               gearModel.chestplateModel = chestplateModel;
               gearModel.leggingsModel = leggingsModel;
               gearModel.bootsModel = bootsModel;
               REGISTRY.put(REGISTRY.size(), gearModel);
            }

            return gearModel;
         } catch (Exception var8) {
            throw new InternalError("Error while registering Gear Model: " + modelClass.getSimpleName());
         }
      }
   }

   public static class ItemProperty {
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
         registerItemProperty(ModItems.SWORD, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.AXE, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.DAGGER, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.HELMET, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.CHESTPLATE, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.LEGGINGS, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.BOOTS, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.ETCHING, "texture", GEAR_TEXTURE);
         registerItemProperty(ModItems.SWORD, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.AXE, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.DAGGER, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.HELMET, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.CHESTPLATE, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.LEGGINGS, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.BOOTS, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.ETCHING, "vault_rarity", GEAR_RARITY);
         registerItemProperty(ModItems.ETCHING, "vault_set", ETCHING);
         registerItemProperty(ModItems.PUZZLE_RUNE, "puzzle_color", PUZZLE_COLOR);
         registerItemProperty(ModBlocks.PUZZLE_RUNE_BLOCK_ITEM, "puzzle_color", PUZZLE_COLOR);
      }

      public static void registerItemProperty(Item item, String name, IItemPropertyGetter property) {
         ItemModelsProperties.func_239418_a_(item, Vault.id(name), property);
      }
   }
}
