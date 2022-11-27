package iskallia.vault.dynamodel.model.armor;

import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.baked.PlainBakedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;

public class ArmorPieceModel extends DynamicModel<ArmorPieceModel> {
   ArmorModel armorModel;
   EquipmentSlot equipmentSlot;
   ArmorLayers layers;

   public ArmorPieceModel(ResourceLocation id, String displayName) {
      super(id, displayName);
   }

   @OnlyIn(Dist.CLIENT)
   public ModelLayerLocation getLayerLocation() {
      return ArmorLayers.createLayerLocation(this.equipmentSlot, this.id);
   }

   public ArmorModel getArmorModel() {
      return this.armorModel;
   }

   public EquipmentSlot getEquipmentSlot() {
      return this.equipmentSlot;
   }

   public ArmorLayers getLayers() {
      return this.layers;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public BakedModel bakeModel(ModelResourceLocation modelLocation, ForgeModelBakery modelLoader, BlockModel unbakedModel) {
      return new PlainBakedModel(super.bakeModel(modelLocation, modelLoader, unbakedModel));
   }
}
