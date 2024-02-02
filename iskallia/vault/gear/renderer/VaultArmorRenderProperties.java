package iskallia.vault.gear.renderer;

import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.Nullable;

public class VaultArmorRenderProperties implements IItemRenderProperties {
   public static final VaultArmorRenderProperties INSTANCE = new VaultArmorRenderProperties();
   public static final Map<ResourceLocation, ArmorLayers.BaseLayer> BAKED_LAYERS = new HashMap<>();

   private VaultArmorRenderProperties() {
   }

   @Nullable
   public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> _default) {
      VaultGearData gearData = VaultGearData.read(itemStack);
      ArmorLayers.BaseLayer model = gearData.getFirstValue(ModGearAttributes.GEAR_MODEL)
         .flatMap(ModDynamicModels.Armor.PIECE_REGISTRY::get)
         .map(DynamicModel::getId)
         .map(BAKED_LAYERS::get)
         .orElse(null);
      if (model != null) {
         model.setScaleModel(_default);
      }

      return model;
   }
}
