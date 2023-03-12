package iskallia.vault.item.crystal.model;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.item.crystal.CrystalData;
import java.util.Optional;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;

public class GrayscaleCrystalModel extends CoreCrystalModel {
   private int color;

   public GrayscaleCrystalModel() {
   }

   public GrayscaleCrystalModel(int color) {
      this.color = color;
   }

   @Override
   public ModelResourceLocation getItemModel() {
      return new ModelResourceLocation("the_vault:crystal/core/grayscale#inventory");
   }

   @Override
   public int getItemColor(float time) {
      return this.color;
   }

   @Override
   public int getBlockColor(CrystalData crystal, float time) {
      return this.color;
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.INT.writeNbt(Integer.valueOf(this.color)).ifPresent(tag -> nbt.put("hue", nbt));
      return super.writeNbt();
   }

   public void readNbt(CompoundTag nbt) {
      this.color = Adapters.INT.readNbt(nbt.get("hue")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.INT.writeJson(Integer.valueOf(this.color)).ifPresent(hue -> json.add("hue", hue));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.color = Adapters.INT.readJson(json.get("hue")).orElse(0);
   }
}
