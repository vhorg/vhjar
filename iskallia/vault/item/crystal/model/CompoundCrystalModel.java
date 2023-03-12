package iskallia.vault.item.crystal.model;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.tool.SpecialItemRenderer;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CompoundCrystalModel extends CrystalModel {
   protected static final ArrayAdapter<CrystalModel> ARRAY = Adapters.ofArray(CrystalModel[]::new, CrystalData.MODEL);
   private CrystalModel[] models;

   public CompoundCrystalModel() {
   }

   public CompoundCrystalModel(CrystalModel... models) {
      this.models = models;
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
      for (CrystalModel model : this.models) {
         model.renderItem(renderer, crystal, stack, transformType, matrices, buffer, light, overlay);
      }
   }

   @Override
   public int getBlockColor(CrystalData crystal, float time) {
      return this.models.length == 0 ? 16777215 : this.models[0].getBlockColor(crystal, time);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      ARRAY.writeNbt(this.models).ifPresent(models -> nbt.put("models", nbt));
      return super.writeNbt();
   }

   public void readNbt(CompoundTag nbt) {
      this.models = ARRAY.readNbt(nbt.get("models")).orElse(new CrystalModel[0]);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      ARRAY.writeJson(this.models).ifPresent(models -> json.add("models", models));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.models = ARRAY.readJson(json.get("models")).orElse(new CrystalModel[0]);
   }
}
