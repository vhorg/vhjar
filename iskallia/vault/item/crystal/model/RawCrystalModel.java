package iskallia.vault.item.crystal.model;

import com.google.gson.JsonObject;
import iskallia.vault.item.crystal.CrystalData;
import java.awt.Color;
import java.util.Optional;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;

public class RawCrystalModel extends CoreCrystalModel {
   @Override
   public ModelResourceLocation getItemModel() {
      return new ModelResourceLocation("the_vault:crystal/core/raw#inventory");
   }

   @Override
   public int getItemColor(float time) {
      return 16777215;
   }

   @Override
   public int getBlockColor(CrystalData crystal, float time) {
      return Color.getHSBColor(time / 200.0F, 1.0F, 0.7F).getRGB();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag());
   }

   public void readNbt(CompoundTag nbt) {
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.of(new JsonObject());
   }

   public void readJson(JsonObject json) {
   }
}
