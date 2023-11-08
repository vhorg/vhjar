package iskallia.vault.item.crystal.model;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.client.util.color.ColorUtil;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.tool.SpecialItemRenderer;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class NullCrystalModel extends CrystalModel {
   public static NullCrystalModel INSTANCE = new NullCrystalModel();

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
      this.resolve(crystal, (float)ClientScheduler.INSTANCE.getTickCount())
         .renderItem(renderer, crystal, stack, transformType, matrices, buffer, light, overlay);
   }

   @Override
   public int getBlockColor(CrystalData crystal, float time) {
      return this.resolve(crystal, time).getBlockColor(crystal, time);
   }

   public CrystalModel resolve(CrystalData crystal, float time) {
      CrystalModel core = crystal.getObjective()
         .getColor(time)
         .map(color -> new GrayscaleCrystalModel(ColorUtil.blendColors(color, 16777215, 0.85F)))
         .orElseGet(RainbowCrystalModel::new);
      CrystalModel augment = crystal.getTheme().getColor().map(AugmentCrystalModel::new).orElse(null);
      return (CrystalModel)(augment == null ? core : new CompoundCrystalModel(core, augment));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.empty();
   }

   public void readNbt(CompoundTag nbt) {
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.empty();
   }

   public void readJson(JsonObject json) {
   }
}
