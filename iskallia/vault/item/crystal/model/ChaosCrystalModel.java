package iskallia.vault.item.crystal.model;

import com.google.gson.JsonObject;
import iskallia.vault.client.util.color.ColorUtil;
import iskallia.vault.item.crystal.CrystalData;
import java.awt.Color;
import java.util.Optional;
import java.util.Random;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;

public class ChaosCrystalModel extends CoreCrystalModel {
   @Override
   public ModelResourceLocation getItemModel() {
      return new ModelResourceLocation("the_vault:crystal/core/chaos#inventory");
   }

   @Override
   public int getItemColor(float time) {
      return 16777215;
   }

   @Override
   public int getBlockColor(CrystalData crystal, float time) {
      int second = (int)(time / 20.0F);
      float progress = time % 20.0F / 20.0F;
      return this.getColor(second, progress);
   }

   public int getColor(int second, float progress) {
      Random random = new Random(second - 1);
      random.nextLong();
      int previous = Color.getHSBColor(random.nextFloat(), random.nextFloat(), random.nextFloat()).getRGB();
      random = new Random(second);
      random.nextLong();
      int current = Color.getHSBColor(random.nextFloat(), random.nextFloat(), random.nextFloat()).getRGB();
      return ColorUtil.blendColors(previous, current, 1.0F - progress);
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
