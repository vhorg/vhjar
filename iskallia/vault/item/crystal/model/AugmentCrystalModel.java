package iskallia.vault.item.crystal.model;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.tool.SpecialItemRenderer;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class AugmentCrystalModel extends CrystalModel {
   private int color;

   public AugmentCrystalModel() {
   }

   public AugmentCrystalModel(int color) {
      this.color = color;
   }

   @Override
   public void renderItem(
      SpecialItemRenderer renderer,
      CrystalData crystal,
      ItemStack stack,
      TransformType transformType,
      PoseStack matrices,
      MultiBufferSource buffer,
      int light,
      int overlay
   ) {
      ModelResourceLocation core = new ModelResourceLocation("the_vault:crystal/augment/core#inventory");
      renderer.renderModel(core, this.color, stack, transformType, matrices, buffer, light, overlay, null);
   }

   @Override
   public int getBlockColor(CrystalData crystal, float time) {
      return this.color;
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.INT.writeNbt(Integer.valueOf(this.color)).ifPresent(color -> nbt.put("color", nbt));
      return super.writeNbt();
   }

   public void readNbt(CompoundTag nbt) {
      this.color = Adapters.INT.readNbt(nbt.get("color")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.INT.writeJson(Integer.valueOf(this.color)).ifPresent(color -> json.add("color", color));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.color = Adapters.INT.readJson(json.get("color")).orElse(0);
   }
}
